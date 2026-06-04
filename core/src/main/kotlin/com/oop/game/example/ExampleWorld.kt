package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameWorld
import com.oop.game.InputHandler
import kotlin.math.floor

/**
 * ════════════════════════════════════════════════════════════
 * 게임 월드 예제 — Player vs Enemy 회피 게임 (이미지 사용).
 * ════════════════════════════════════════════════════════════
 *
 * GameWorld 를 상속해 만든 가장 작은 플레이 가능한 예제.
 * 학생은 이 파일을 참고해서 자기만의 월드를 만들면 된다.
 *
 * ── 조작법 ──
 * ▸ 화살표 키  : 플레이어 이동
 * ▸ WASD      : 카메라 이동 (월드가 화면보다 커서 탐험 가능)
 * ▸ ESC       : 게임 오버 후 종료
 *
 * ── 사용 이미지 (core/src/main/resources/) ──
 * ▸ player.png  — 30x30 플레이어 스프라이트
 * ▸ enemy.png   — 40x40 적 스프라이트
 * ▸ tile.png    — 64x64 흰색 정사각형 (체스판 배경에 색만 입혀 사용)
 *
 * ── 게임 상태 ──
 * IN_PLAY   : 일반 진행 (이동·충돌 체크)
 * GAME_OVER : 충돌 후 정지, ESC 입력 대기
 *
 * ── 텍스트 데모 ──
 * ▸ 좌측 상단 "HP: 3"       — 화면 좌표 (카메라 움직여도 고정)
 * ▸ 월드 중앙 "WORLD CENTER" — 월드 좌표 (카메라와 함께 이동)
 * 두 개를 같이 두어, 두 좌표계의 차이를 눈으로 확인할 수 있게 했다.
 *
 * ── 배경 ──
 * tile.png(흰 사각형)를 두 가지 색으로 틴트해 체스판처럼 깐다.
 * 카메라 이동을 눈으로 보여주기 위함이다.
 * GameWorld.drawBackground(batch) 를 override 해서 그린다.
 *
 * @param screenWidth  화면에 보이는 영역 너비
 * @param screenHeight 화면에 보이는 영역 높이
 * @param worldWidth   월드 전체 너비 (화면보다 크면 WASD 로 탐험 가능)
 * @param worldHeight  월드 전체 높이
 */
class ExampleWorld(
    screenWidth: Float,
    screenHeight: Float,
    worldWidth: Float,
    worldHeight: Float
) : GameWorld(screenWidth, screenHeight, worldWidth, worldHeight) {

    /**
     * 게임의 현재 상태를 나타내는 열거형.
     *
     * Boolean 깃발(isGameOver) 대신 enum 을 쓰는 이유:
     * ▸ 상태 가짓수가 늘어날 때 깔끔히 확장 가능 (예: PAUSED, MENU, VICTORY)
     * ▸ when 으로 분기하면 'else' 없이 모든 상태를 다뤘는지 컴파일러가 체크해줌
     * ▸ 코드를 읽을 때 "이 게임에 어떤 상태들이 있는가" 가 한눈에 보임
     * (7주차에서 배우는 enum class 의 전형적 활용)
     */
    private enum class GameState {
        MENU,
        IN_PLAY,
        GAME_OVER
    }

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

    // ── 체스판 배경 설정 (drawBackground() 에서 사용) ──
    //   이게 없으면 검은 배경뿐이라 카메라(WASD) 이동이 눈에 안 보인다.
    //   학생은 자기 게임에선 다른 배경을 그리거나, 그냥 두면 검은 배경이다.
    //
    //   tile.png 는 흰색 64x64 정사각형 한 장. 같은 텍스처에 batch.color 를
    //   바꿔가며 두 가지 색으로 그리는 트릭(틴트) 으로 체스판을 만든다.
    private val tileTexture = Texture(Gdx.files.internal("tile.png"))
    private val bgColorDark = Color(0.08f, 0.08f, 0.08f, 1f)
    private val bgColorLight = Color(0.15f, 0.15f, 0.15f, 1f)
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
        offsetX = 0f
        offsetY = 0f
    }

    /** IN_PLAY 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크. */
    private fun updateInPlay(delta: Float) {
        // ── 카메라 이동 (WASD) ──
        //   offsetX/Y 를 바꾸면 카메라가 월드 안에서 움직인다.

        val scrollSpeed = 200f // 전진 속도
        offsetX += scrollSpeed * delta // 매 프레임 자동으로 오른쪽 전진

        // 카메라가 월드 경계 밖을 보여주지 않도록 clamp.
        //   보여주는 영역이 [offset, offset+screen] 이어야 하므로
        //   offset 은 0 ~ (world - screen) 범위여야 한다.
        offsetX = offsetX.coerceAtLeast(0f) //gpt가 카메라 제한 풀려면 이렇게 하라고....
        offsetY = offsetY.coerceIn(0f, worldHeight - screenHeight)

        // ── 1) 게임 객체 갱신 — 각자 한 프레임씩 진행 ──
        updateAllObjects(delta)
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

        // ── 2) 상호작용 결정 — 누가 누구와 부딪혀 어떻게 되는지 ──
        //   collidesWith 는 GameObject 의 메서드 → 모든 게임 객체가 자동으로 가짐.
        //   이 예제에선 충돌 시 객체를 죽이지 않고 게임 상태만 바꾼다.
        //   (총알 게임이라면 여기서 bullet.kill(), enemy.kill() 같은 처리)
        //   ── [작성자: 본인 이름] 기존 바로 게임오버 로직에서 HP 차감 및 사망 조건부 게임오버로 수정 결합 ──
        if (player.collidesWith(enemy)) {
            hp -= 20f // 부딪히면 HP 추가 차감

            if (hp <= 0f) {
                hp = 0f
                state = GameState.GAME_OVER // HP가 0이 되면 비로소 최종 게임오버
            }
        }

        // ── 3) 죽은 객체 정리 ──
        //   현재 예제에선 아무 것도 안 죽으므로 영향 없지만,
        //   bullet/enemy 가 추가될 때를 대비한 표준 흐름이다.
        removeDead()
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
     * 카메라(offset) 에 따라 타일 위치가 바뀌어 이동감을 준다.
     * 타일 인덱스 자체는 월드 좌표 격자에서 변하지 않지만,
     * 각 타일을 그릴 때 offset 만큼 빼서 화면 좌표로 변환한다.
     *
     * 색을 입히는 방법:
     * batch.color 를 바꾼 뒤 batch.draw 하면 텍스처가 그 색으로 곱해져 그려진다.
     * tile.png 가 흰색이라 어떤 색이든 그대로 적용된다.
     * 끝에 다시 흰색으로 되돌려두지 않으면 그 다음 그리는 것까지 영향을 받으니 주의.
     */
    override fun drawBackground(batch: SpriteBatch) {
        // 현재 카메라 시작점이 속한 타일 인덱스 (여유분으로 -1)
        val startCol = floor(offsetX / tileSize).toInt() - 1
        val startRow = floor(offsetY / tileSize).toInt() - 1
        // 화면을 채우는 데 필요한 타일 개수 (여유분 +3)
        val cols = (screenWidth / tileSize).toInt() + 3
        val rows = (screenHeight / tileSize).toInt() + 3

        for (row in startRow until startRow + rows) {
            for (col in startCol until startCol + cols) {
                // 행+열이 짝수면 어둡게, 홀수면 밝게 → 체스판 패턴
                batch.color = if ((row + col) % 2 == 0) bgColorDark else bgColorLight

                // 월드 좌표의 타일 위치에서 offset 만큼 빼면 화면 좌표
                val drawX = col * tileSize - offsetX
                val drawY = row * tileSize - offsetY
                batch.draw(tileTexture, drawX, drawY, tileSize, tileSize)
            }
        }

        // 배경에 입힌 색이 다음 그리기(게임 객체)에 영향을 주지 않도록 흰색으로 복원.
        batch.color = Color.WHITE
    }

    /**
     * 매 프레임 그리기 — 부모가 배경·객체까지 그려준 뒤, 텍스트 UI 를 얹는다.
     *
     * 이 함수에서는 '그리기' 만 한다. 입력 처리·상태 변경은 update() 의 책임.
     *
     * 주의: super.render(delta) 가 화면 clear + 배경 + 객체까지 그리므로,
     * 텍스트는 반드시 super 호출 **이후** 그려야 가려지지 않는다.
     */
    override fun render(delta: Float) {
        super.render(delta)

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

    /**
     * 플레이어 체력바 UI.
     *
     * 기존에는 "HP: 3"처럼 텍스트로만 체력을 표시했다.
     * 하지만 텍스트만 있으면 현재 체력이 얼마나 남았는지 직관적으로 보기 어렵기 때문에
     * 막대(bar) 형태의 체력바를 추가했다.
     *
     * 체력이 줄어들면 빨간 부분의 너비도 함께 줄어든다.
     *
     * 작은 흰 네모 이미지(tileTexture)를
     * 크기와 색만 바꿔서
     * 체력바처럼 보이게 만든 것
     *
     *
     * 1. 먼저 체력바보다 조금 더 큰 흰색 직사각형을 그림
     * → 이게 테두리 역할
     *
     * 2. 그 위에 원래 크기의 회색 직사각형을 그림
     * → 빈 체력바(최대 체력 영역)
     *
     * 3. 마지막으로 현재 체력 비율만큼의 빨간 직사각형을 그림
     * → 실제 남은 체력 표시
     *
     */

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
    }
}