package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameWorld
import com.oop.game.InputHandler
import kotlin.math.floor
import kotlin.random.Random


class ExampleWorld(
    screenWidth: Float,
    screenHeight: Float,
    worldWidth: Float,
    worldHeight: Float
) : GameWorld(screenWidth, screenHeight, worldWidth, worldHeight) {

    private enum class GameState {
        MENU,
        IN_PLAY,
        GAME_OVER
    }

    // 장애물 종류 — 작은 박스, 큰 박스, 공중 장애물.
    private enum class ObstacleType {
        SMALL,
        BIG,
        FLYING
    }

    // 장애물 정보 — 별도 파일을 만들지 않고 ExampleWorld 내부에서 관리한다.
    private data class Obstacle(
        var x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val type: ObstacleType,
        var isDead: Boolean = false
    )

    private val groundY = 200f // 땅 위치는 높이 200f부터.

    // 플레이어 — 월드 중앙 하단에서 시작.
    //   월드 크기를 함께 넘겨서, 경계 밖으로 못 나가게 한다.
    private var player = ExamplePlayer(
        x = 100f,   //초기 위치 100f로 고정
        y = groundY,
        worldWidth = worldWidth,
        worldHeight = worldHeight,
        groundY = groundY
    )

    // 적 — 월드 상단에서 좌우 왕복.
    private var enemy = ExampleEnemy(
        x = 100f,
        y = worldHeight - 100f,
        minX = 0f,
        maxX = worldWidth
    )

    // 현재 게임 상태 — 메뉴에서 시작해 플레이/게임오버로 전환된다.
    private var state = GameState.MENU

    private var score = 0 //현재 점수
    private var scoreTimer = 0f // 점수 증가용 타이머
    private var blinkTimer = 0f

    // 쿠키런 방식 HP 시스템 — 시간이 지날수록 체력이 줄어든다.
    private val maxHp = 500f
    private var hp = maxHp

    // 랜덤 장애물 생성용 — 작은 박스, 큰 박스, 공중 장애물을 일정 시간마다 생성한다.
    private val obstacles = mutableListOf<Obstacle>()
    private var obstacleSpawnTimer = 0f
    private var nextSpawnTime = 1.5f

    // 점수 300점마다 게임 속도를 조금씩 올리기 위한 기준값.
    private val speedUpScoreUnit = 300

    // 카메라 전진 속도는 고정한다.
    // 장애물 속도만 빨라지게 해야 캐릭터가 뒤로 밀려 보이지 않는다.
    private val baseScrollSpeed = 200f

    private val baseObstacleSpeed = 400f
    private val obstacleSpeedIncrease = 40f

    // ── 배경/지형 그리기용 기본 텍스처 ──
    // tile.png 는 흰색 64x64 정사각형 한 장.
    // 같은 텍스처에 batch.color 를 바꿔가며 하늘, 구름, 지형을 색으로 그린다.
    private val tileTexture = Texture(Gdx.files.internal("tile.png"))
    private val obstacleSmallTexture = Texture(Gdx.files.internal("obstacle_small.png"))
    private val obstacleBigTexture = Texture(Gdx.files.internal("obstacle_big.png"))

    private val tileSize = 64f

    /**
     * 생성자 본문 — 월드에 플레이어와 적을 등록한다.
     * 이렇게 등록해야 update / draw 루프에 포함된다.
     */
    init {
        add(player)
        add(enemy)
    }

    /**
     * 매 프레임 게임 로직 — 모든 '입력 처리·상태 변경' 은 이 안에서.
     *
     * 상태별로 해야 할 일이 완전히 다르므로 when 으로 분기한다.
     * (입력 처리가 render() 가 아닌 update() 에 있는 이유:
     * '로직과 그리기의 분리' — render 는 매 프레임 그리는 일에만 집중하고,
     * 상태 변화·입력은 update 가 책임진다.)
     */
    override fun update(delta: Float) {
        blinkTimer += delta

        when (state) {
            GameState.MENU -> updateMenu()
            GameState.IN_PLAY -> updateInPlay(delta)
            GameState.GAME_OVER -> updateGameOver()
        }
    }

    /** MENU 상태에서 Enter를 누르면 게임 시작. */
    private fun updateMenu() {
        if (InputHandler.isKeyJustPressed(InputHandler.ENTER)) {
            startGame()
        }
    }

    private fun startGame() {
        state = GameState.IN_PLAY
        score = 0
        scoreTimer = 0f
        hp = maxHp
        obstacles.clear()
        obstacleSpawnTimer = 0f
        nextSpawnTime = 1.5f
        offsetX = 0f
        offsetY = 0f
    }

    // 현재 점수를 기준으로 속도 단계 계산.
    // 예: 0~299점 = 0단계, 300~599점 = 1단계, 600~899점 = 2단계.
    private fun getSpeedLevel(): Int {
        return score / speedUpScoreUnit
    }

    // 점수 300점마다 장애물 이동 속도를 조금씩 증가시킨다.
    private fun getObstacleSpeed(): Float {
        return baseObstacleSpeed + getSpeedLevel() * obstacleSpeedIncrease
    }

    // 카메라 전진 속도는 점수와 상관없이 고정한다.
    // 기존처럼 점수 300점마다 카메라가 빨라지면 플레이어가 뒤로 밀려 보인다.
    private fun getScrollSpeed(): Float {
        return baseScrollSpeed
    }

    // 점수 300점마다 장애물 생성 간격을 조금씩 줄인다.
    // 단, 너무 빠르게 생성되지 않도록 최소 0.6초는 유지한다.
    private fun getRandomSpawnTime(): Float {
        val randomBaseTime = Random.nextFloat() * 1.5f + 1f
        val speedBonus = getSpeedLevel() * 0.1f

        return (randomBaseTime - speedBonus).coerceAtLeast(0.6f)
    }

    /** IN_PLAY 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크. */
    private fun updateInPlay(delta: Float) {
        // ── 카메라 이동 (WASD) ──
        //   offsetX/Y 를 바꾸면 카메라가 월드 안에서 움직인다.

        val scrollSpeed = getScrollSpeed() // 전진 속도
        offsetX += scrollSpeed * delta // 매 프레임 자동으로 오른쪽 전진

        // 카메라가 월드 경계 밖을 보여주지 않도록 clamp.
        //   보여주는 영역이 [offset, offset+screen] 이어야 하므로
        //   offset 은 0 ~ (world - screen) 범위여야 한다.
        offsetX = offsetX.coerceAtLeast(0f) //gpt가 카메라 제한 풀려면 이렇게 하라고....
        offsetY = offsetY.coerceIn(0f, worldHeight - screenHeight)

        // ── 게임 객체 갱신 — 각자 한 프레임씩 진행 ──
        updateAllObjects(delta)
        updateObstacles(delta)
        spawnRandomObstacle(delta)

        scoreTimer += delta // 점수 증가용 시간 누적

        if (scoreTimer >= 1f) {
            score += 5
            scoreTimer = 0f
        }

        // 쿠키런처럼 시간이 지날수록 HP가 초당 1씩 감소한다.
        hp -= 1f * delta

        if (hp <= 0f) {
            hp = 0f
            state = GameState.GAME_OVER
        }

        // ── 상호작용 결정 — 누가 누구와 부딪혀 어떻게 되는지 ──
        //   collidesWith 는 GameObject 의 메서드 → 모든 게임 객체가 자동으로 가짐.
        //   기존 바로 게임오버 로직에서 HP 차감 및 사망 조건부 게임오버로 수정 결합
        if (player.collidesWith(enemy)) {
            if (hp <= 0f) {
                hp = 0f
                state = GameState.GAME_OVER // HP가 0이 되면 비로소 최종 게임오버
            }
        }

        checkObstacleCollision()

        // ── 죽은 객체 정리 ──
        //   현재 예제에선 아무 것도 안 죽으므로 영향 없지만,
        //   bullet/enemy 가 추가될 때를 대비한 표준 흐름이다.
        removeDead()
    }

    // 장애물 이동 및 화면 밖으로 나간 장애물 제거 처리.
    private fun updateObstacles(delta: Float) {
        val obstacleSpeed = getObstacleSpeed()

        for (obstacle in obstacles) {
            obstacle.x -= obstacleSpeed * delta

            if (obstacle.x + obstacle.width < offsetX - 100f) {
                obstacle.isDead = true
            }
        }

        obstacles.removeIf {
            it.isDead
        }
    }

    // 일정 시간마다 작은 박스, 큰 박스, 공중 장애물 중 하나를 랜덤으로 생성한다.
    private fun spawnRandomObstacle(delta: Float) {
        obstacleSpawnTimer += delta

        if (obstacleSpawnTimer >= nextSpawnTime) {
            val randomType = ObstacleType.values().random()

            val obstacleWidth = when (randomType) {
                ObstacleType.SMALL -> 80f
                ObstacleType.BIG -> 115f
                ObstacleType.FLYING -> 70f
            }

            val obstacleHeight = when (randomType) {
                ObstacleType.SMALL -> 80f
                ObstacleType.BIG -> 130f
                ObstacleType.FLYING -> 70f
            }

            val obstacleY = when (randomType) {
                ObstacleType.SMALL -> groundY
                ObstacleType.BIG -> groundY
                ObstacleType.FLYING -> groundY + 105f
            }

            obstacles.add(
                Obstacle(
                    x = offsetX + screenWidth + 100f,
                    y = obstacleY,
                    width = obstacleWidth,
                    height = obstacleHeight,
                    type = randomType
                )
            )

            obstacleSpawnTimer = 0f
            nextSpawnTime = getRandomSpawnTime()
        }
    }

    // 플레이어와 장애물이 부딪혔는지 확인하고, 부딪히면 HP를 차감한다.
    private fun checkObstacleCollision() {
        for (obstacle in obstacles) {
            if (!obstacle.isDead && isPlayerCollidingWithObstacle(obstacle)) {
                hp -= 30f
                obstacle.isDead = true

                if (hp <= 0f) {
                    hp = 0f
                    state = GameState.GAME_OVER
                }
            }
        }

        obstacles.removeIf {
            it.isDead
        }
    }

    // 플레이어와 장애물의 사각형 충돌 판정.
    private fun isPlayerCollidingWithObstacle(obstacle: Obstacle): Boolean {
        val playerPaddingX = 12f
        val playerPaddingY = 10f

        val obstaclePaddingX = when (obstacle.type) {
            ObstacleType.SMALL -> 10f
            ObstacleType.BIG -> 14f
            ObstacleType.FLYING -> 12f
        }

        val obstaclePaddingY = when (obstacle.type) {
            ObstacleType.SMALL -> 8f
            ObstacleType.BIG -> 12f
            ObstacleType.FLYING -> 10f
        }

        val playerLeft = player.x + playerPaddingX
        val playerRight = player.x + player.width - playerPaddingX
        val playerBottom = player.y + playerPaddingY
        val playerTop = player.y + player.height - playerPaddingY

        val obstacleLeft = obstacle.x + obstaclePaddingX
        val obstacleRight = obstacle.x + obstacle.width - obstaclePaddingX
        val obstacleBottom = obstacle.y + obstaclePaddingY
        val obstacleTop = obstacle.y + obstacle.height - obstaclePaddingY

        return playerRight > obstacleLeft &&
                playerLeft < obstacleRight &&
                playerTop > obstacleBottom &&
                playerBottom < obstacleTop
    }

    /** GAME_OVER 상태에서 매 프레임 처리 — ESC 입력만 감시한다. */
    private fun updateGameOver() {
        if (InputHandler.isKeyJustPressed(InputHandler.R)) {
            restartGame()
        }

        // ESC 키가 '막 눌린 순간' 앱 종료.
        //   isKeyJustPressed 로 한 이유: 누르고 있는 동안 매 프레임 exit 호출되지 않게.
        if (InputHandler.isKeyJustPressed(InputHandler.ESCAPE)) {
            Gdx.app.exit()
        }
    }

    private fun restartGame() {
        remove(player)
        remove(enemy)
        player.dispose()
        enemy.dispose()

        player = ExamplePlayer(
            x = 100f,
            y = groundY,
            worldWidth = worldWidth,
            worldHeight = worldHeight,
            groundY = groundY
        )
        enemy = ExampleEnemy(
            x = 100f,
            y = worldHeight - 100f,
            minX = 0f,
            maxX = worldWidth
        )

        add(player)
        add(enemy)
        startGame()
    }

    /**
     * 배경 그리기 — GameWorld.drawBackground(batch) 를 override.
     *
     * 부모가 이미 batch.begin() 을 호출한 상태에서 이 함수를 부르므로,
     * 여기선 batch.draw() 호출만 하면 된다. (begin/end 를 또 부르면 안 된다)
     *
     * 카메라(offset) 에 따라 구름 위치가 조금씩 바뀌어 이동감을 준다.
     *
     * 색을 입히는 방법:
     * batch.color 를 바꾼 뒤 batch.draw 하면 텍스처가 그 색으로 곱해져 그려진다.
     * tile.png 가 흰색이라 어떤 색이든 그대로 적용된다.
     * 끝에 다시 흰색으로 되돌려두지 않으면 그 다음 그리는 것까지 영향을 받으니 주의.
     */
    override fun drawBackground(batch: SpriteBatch) {
        drawSky(batch)
    }

    // 이미지 없이 색으로 하늘을 그린다.
    // 파란색 줄을 여러 개 쌓아서 그라데이션처럼 보이게 만들고,
    // 사각형을 여러 개 겹쳐서 픽셀 구름 느낌을 낸다.
    private fun drawSky(batch: SpriteBatch) {
        // ── 1) 하늘 그라데이션 ──
        // 화면을 여러 개의 가로 줄로 나눈 뒤, 아래쪽은 밝게 위쪽은 진하게 칠한다.
        val stripeCount = 28
        val stripeHeight = screenHeight / stripeCount

        for (i in 0 until stripeCount) {
            val t = i.toFloat() / stripeCount

            batch.color = Color(
                0.03f + t * 0.16f,
                0.42f + t * 0.34f,
                0.88f + t * 0.10f,
                1f
            )

            batch.draw(
                tileTexture,
                0f,
                i * stripeHeight,
                screenWidth,
                stripeHeight + 1f
            )
        }

        // ── 2) 먼 구름 ──
        // alpha 값을 낮게 해서 멀리 있는 구름처럼 연하게 보이게 한다.
        drawCloud(batch, 120f - offsetX * 0.08f, screenHeight - 260f, 0.75f, 0.45f)
        drawCloud(batch, 670f - offsetX * 0.06f, screenHeight - 170f, 0.70f, 0.38f)
        drawCloud(batch, 1180f - offsetX * 0.07f, screenHeight - 330f, 0.80f, 0.42f)

        // ── 3) 가까운 구름 ──
        // alpha 값을 높게 해서 이미지처럼 뚜렷한 하얀 구름 느낌을 준다.
        drawCloud(batch, 220f - offsetX * 0.15f, screenHeight - 160f, 1.25f, 0.95f)
        drawCloud(batch, 560f - offsetX * 0.12f, screenHeight - 300f, 1.05f, 0.90f)
        drawCloud(batch, 1030f - offsetX * 0.14f, screenHeight - 210f, 1.15f, 0.95f)
        drawCloud(batch, 1320f - offsetX * 0.13f, screenHeight - 390f, 1.00f, 0.90f)

        batch.color = Color.WHITE
    }

    // 사각형 여러 개를 겹쳐서 구름을 그린다.
    // 원형 이미지를 쓰지 않아도, 작은 사각형을 덩어리로 배치하면 픽셀아트 구름 느낌이 난다.
    private fun drawCloud(batch: SpriteBatch, x: Float, y: Float, scale: Float, alpha: Float) {
        val wrapWidth = screenWidth + 420f
        val cloudX = ((x % wrapWidth) + wrapWidth) % wrapWidth - 210f

        // ── 1) 구름 아래쪽의 연한 하늘색 그림자 ──
        batch.color = Color(0.62f, 0.86f, 1.00f, alpha * 0.55f)
        batch.draw(tileTexture, cloudX - 65f * scale, y - 12f * scale, 260f * scale, 13f * scale)
        batch.draw(tileTexture, cloudX - 10f * scale, y + 2f * scale, 130f * scale, 20f * scale)
        batch.draw(tileTexture, cloudX + 95f * scale, y - 2f * scale, 80f * scale, 16f * scale)

        // ── 2) 구름 흰색 본체 ──
        batch.color = Color(1f, 1f, 1f, alpha)
        batch.draw(tileTexture, cloudX - 30f * scale, y, 58f * scale, 28f * scale)
        batch.draw(tileTexture, cloudX + 10f * scale, y + 16f * scale, 75f * scale, 42f * scale)
        batch.draw(tileTexture, cloudX + 70f * scale, y + 6f * scale, 62f * scale, 30f * scale)
        batch.draw(tileTexture, cloudX + 120f * scale, y, 52f * scale, 22f * scale)

        // ── 3) 구름 위쪽 하이라이트 ──
        batch.color = Color(1f, 1f, 1f, alpha * 0.85f)
        batch.draw(tileTexture, cloudX + 22f * scale, y + 42f * scale, 45f * scale, 12f * scale)
        batch.draw(tileTexture, cloudX + 78f * scale, y + 28f * scale, 35f * scale, 10f * scale)

        // ── 4) 아래쪽 긴 꼬리 구름 ──
        batch.color = Color(0.86f, 0.96f, 1f, alpha * 0.75f)
        batch.draw(tileTexture, cloudX - 90f * scale, y - 18f * scale, 310f * scale, 8f * scale)
        batch.draw(tileTexture, cloudX - 120f * scale, y - 15f * scale, 70f * scale, 5f * scale)

        batch.color = Color.WHITE
    }

    override fun render(delta: Float) {
        super.render(delta)

        // 이미지 없이 색으로 지형을 먼저 그린다.
        // 그 다음 장애물을 그리면 장애물이 지형 위에 서 있는 것처럼 보인다.
        drawGroundByColor()
        drawObstacles()

        // ── 상태별로 그리는 것이 다름 ──
        when (state) {
            GameState.MENU -> drawMenuScreen()
            GameState.IN_PLAY -> {
                drawHud()
                drawHealthBar()
            }
            GameState.GAME_OVER -> {
                drawHud()
                drawHealthBar()
                drawGameOverOverlay()
            }
        }
    }

    // 이미지 없이 색으로 지형을 그린다.
    // 위에는 초록색 잔디, 아래는 갈색 흙으로 화면 아래를 꽉 채운다.
    private fun drawGroundByColor() {
        batch.begin()

        // 플레이어와 장애물이 서 있는 기준선.
        // groundY를 기준으로 화면 좌표로 바꿔준다.
        val groundTopY = groundY - offsetY - 8f

        // 흙이 화면 아래를 꽉 채우도록 높이를 계산한다.
        val dirtHeight = groundTopY.coerceAtLeast(0f)

        // ── 1) 흙 전체 ──
        batch.color = Color(0.45f, 0.28f, 0.08f, 1f)
        batch.draw(
            tileTexture,
            0f,
            0f,
            screenWidth,
            dirtHeight
        )

        // ── 2) 흙 윗부분의 어두운 그림자 ──
        batch.color = Color(0.25f, 0.16f, 0.06f, 1f)
        batch.draw(
            tileTexture,
            0f,
            groundTopY - 18f,
            screenWidth,
            18f
        )

        // ── 3) 잔디 밑 어두운 부분 ──
        batch.color = Color(0.04f, 0.28f, 0.10f, 1f)
        batch.draw(
            tileTexture,
            0f,
            groundTopY - 8f,
            screenWidth,
            8f
        )

        // ── 4) 잔디 윗면 ──
        batch.color = Color(0.10f, 0.65f, 0.18f, 1f)
        batch.draw(
            tileTexture,
            0f,
            groundTopY,
            screenWidth,
            10f
        )

        // ── 5) 잔디 하이라이트 ──
        batch.color = Color(0.25f, 0.85f, 0.25f, 1f)
        batch.draw(
            tileTexture,
            0f,
            groundTopY + 7f,
            screenWidth,
            3f
        )

        // ── 6) 흙 안의 작은 돌들 ──
        // 이미지 없이 작은 회색 네모를 여러 개 찍어서 흙 텍스처 느낌을 낸다.
        for (i in 0 until 55) {
            val stoneX = ((i * 137 - offsetX.toInt()) % screenWidth.toInt()).toFloat()
            val fixedStoneX = if (stoneX < 0f) stoneX + screenWidth else stoneX

            val stoneY = ((i * 53) % dirtHeight.toInt().coerceAtLeast(1)).toFloat()

            batch.color = Color(0.55f, 0.52f, 0.45f, 1f)
            batch.draw(
                tileTexture,
                fixedStoneX,
                stoneY,
                5f,
                5f
            )

            batch.color = Color(0.30f, 0.28f, 0.23f, 1f)
            batch.draw(
                tileTexture,
                fixedStoneX + 1f,
                stoneY - 1f,
                4f,
                2f
            )
        }

        batch.color = Color.WHITE
        batch.end()
    }

    // 장애물을 화면에 그린다.
    // 작은 박스, 큰 박스, 공중 장애물을 색과 크기로 구분해서 표시한다.
    private fun drawObstacles() {
        batch.begin()

        for (obstacle in obstacles) {
            val texture = when (obstacle.type) {
                ObstacleType.SMALL -> obstacleSmallTexture
                ObstacleType.BIG -> obstacleBigTexture
                ObstacleType.FLYING -> obstacleSmallTexture
            }

            batch.color = Color.WHITE

            batch.draw(
                texture,
                obstacle.x - offsetX,
                obstacle.y - offsetY,
                obstacle.width,
                obstacle.height
            )
        }

        batch.color = Color.WHITE
        batch.end()
    }

    private fun drawMenuScreen() {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f

        drawTextOnScreen(
            text = "RUNNING GAME",
            x = centerX - 110f,
            y = centerY + 60f,
            color = Color.CYAN,
            scale = 2.5f
        )
        drawTextOnScreen(
            text = "SPACE: Jump   Z: Slide",
            x = centerX - 120f,
            y = centerY,
            color = Color.LIGHT_GRAY,
            scale = 1f
        )
        if ((blinkTimer % 1.2f) < 0.6f) {
            drawTextOnScreen(
                text = "PRESS ENTER TO START",
                x = centerX - 125f,
                y = centerY - 50f,
                color = Color.YELLOW,
                scale = 1.3f
            )
        }
    }

    /** 항상 화면에 표시되는 정보 — HP 표시와 월드 중앙 표지. */
    private fun drawHud() {
        val hudY = screenHeight - 16f

        drawTextOnScreen(
            text = "SCORE: $score",
            x = screenWidth - 160f, //오른쪽 위치
            y = hudY, // 위쪽 위치
            color = Color.WHITE,
            scale = 1.2f // 글씨 확대
        )
    }


    private fun drawHealthBar() {
        val hpRatio = hp / maxHp //현재 체력이 최대 체력의 몇 %인지 계산

        //체력바 위치
        val barX = 10f // 왼쪽에서 얼마나 떨어질지
        val barY = screenHeight - 40f // 위에서 얼마나 떨어질지

        //체력바 크기
        val barWidth = 300f
        val barHeight = 24f

        //체력바 두께
        val borderSize = 3f

        batch.begin() // 렌더링 시작

        // ── 1) 체력바 테두리 ──
        batch.color = Color.WHITE
        batch.draw(
            tileTexture,
            barX - borderSize,
            barY - borderSize,
            barWidth + borderSize * 2,
            barHeight + borderSize * 2
        )

        // ── 2) 빈 체력바 배경 ──
        batch.color = Color.DARK_GRAY
        batch.draw(
            tileTexture,
            barX,
            barY,
            barWidth,
            barHeight
        )

        // ── 3) 현재 체력 ──
        batch.color = Color.RED
        batch.draw(
            tileTexture,
            barX,
            barY,
            barWidth * hpRatio,
            barHeight
        )

        batch.color = Color.WHITE
        batch.end()

        drawTextOnScreen(
            text = "${hp.toInt()} / ${maxHp.toInt()}",
            x = barX + barWidth + 10f,
            y = barY + barHeight,
            color = Color.WHITE,
            scale = 1f
        )
    }

    /** 게임 오버 시 화면 중앙에 띄우는 안내 메시지. */
    private fun drawGameOverOverlay() {
        drawTextOnScreen(
            text = "Game Over!",
            x = screenWidth / 2 - 80f,
            y = screenHeight / 2,
            color = Color.WHITE,
            scale = 2f
        )
        drawTextOnScreen(
            text = "Press ESC to exit",
            x = screenWidth / 2 - 70f,
            y = screenHeight / 2 - 40f,
            color = Color.WHITE,
            scale = 1f
        )
        if ((blinkTimer % 1.2f) < 0.6f) {
            drawTextOnScreen(
                text = "Press R to restart",
                x = screenWidth / 2 - 70f,
                y = screenHeight / 2 - 70f,
                color = Color.YELLOW,
                scale = 1f
            )
        }
    }

    /** 화면이 닫힐 때 — 부모도 dispose 한 뒤 우리만의 자원도 해제. */
    override fun dispose() {
        super.dispose()
        tileTexture.dispose()
        obstacleSmallTexture.dispose()
        obstacleBigTexture.dispose()
    }
}
