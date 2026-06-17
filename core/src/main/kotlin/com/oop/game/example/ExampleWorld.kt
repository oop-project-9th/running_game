package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.oop.game.GameWorld
import com.oop.game.InputHandler
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class ExampleWorld(
    screenWidth: Float,
    screenHeight: Float,
    worldWidth: Float,
    worldHeight: Float
) : GameWorld(screenWidth, screenHeight, worldWidth, worldHeight) {

    private enum class GameState {
        MENU,
        IN_PLAY,
        SHOP,
        GAME_OVER
    }

    private data class Obstacle(
        var x: Float,
        var y: Float,
        val baseY: Float,
        val width: Float,
        val height: Float,
        val type: ObstacleType,
        val phase: Float = 0f,
        var motionTimer: Float = 0f,
        var nearMissChecked: Boolean = false,
        var slideTrickChecked: Boolean = false,
        var passed: Boolean = false,
        var bounty: Boolean = false,
        var isDead: Boolean = false
    )

    private data class CoinPickup(
        var x: Float,
        var y: Float,
        val value: Int,
        var isDead: Boolean = false
    )

    private data class StageGimmick(
        var x: Float,
        val width: Float,
        val type: GimmickType,
        var triggered: Boolean = false,
        var passed: Boolean = false,
        var isDead: Boolean = false
    )

    private val groundY = 200f
    private val coinSize = 24f

    private var player = ExamplePlayer(
        x = 100f,
        y = groundY,
        worldWidth = worldWidth,
        worldHeight = worldHeight,
        groundY = groundY
    )

    private var enemy = ExampleEnemy(
        x = 100f,
        y = worldHeight - 100f,
        minX = 0f,
        maxX = worldWidth
    )

    private var state = GameState.MENU
    private var blinkTimer = 0f

    private val maxHp = 500f
    private var hp = maxHp

    private val scoreSystem = ScoreSystem()
    private val stageManager = StageManager()
    private val patternSpawner = PatternSpawner(groundY)
    private val inventory = InventorySystem()
    private val metaProgression = MetaProgression()
    private val shopSystem = ShopSystem(metaProgression)

    private val obstacles = mutableListOf<Obstacle>()
    private val coinPickups = mutableListOf<CoinPickup>()
    private val stageGimmicks = mutableListOf<StageGimmick>()
    private var patternTimer = 0f
    private var coins = 0
    private var lastGemReward = 0
    private var runTimer = 0f
    private var coinStreak = 0
    private var coinStreakTimer = 0f
    private var luckySlotCounter = 0
    private var obstacleSpawnCounter = 0
    private var feverGauge = 0f
    private var feverTimer = 0f
    private var emergencyPotionUsed = false
    private var nearMissRouletteCount = 0
    private var activeCooldownTimer = 0f
    private var shieldTimer = 0f
    private var slowTimer = 0f
    private var overdriveTimer = 0f
    private val bossMaxHp = 560f
    private var bossHp = bossMaxHp
    private var bossAttackTimer = 0f
    private var bossAttackIndex = 0
    private var bossDefeated = false

    private var feedbackMessage = ""
    private var feedbackTimer = 0f

    private val tileTexture = Texture(Gdx.files.internal("tile.png"))
    private val backgroundTexture = Texture(Gdx.files.internal("Gemini_Generated_Image_rr8i8lrr8i8lrr8i.png"))
    private val groundTexture = Texture(Gdx.files.internal("Gemini_Generated_Image_z8alehz8alehz8al.png"))
    private val groundRegion = TextureRegion(
        groundTexture,
        0,
        groundTexture.height / 2,
        groundTexture.width,
        groundTexture.height / 2
    )
    private val obstacleCrateSmallTexture = Texture(Gdx.files.internal("obstacle_crate_small.png"))
    private val obstacleCrateTallTexture = Texture(Gdx.files.internal("obstacle_crate_tall.png"))
    private val obstacleBarrelTexture = Texture(Gdx.files.internal("obstacle_barrel.png"))
    private val obstacleSawTexture = Texture(Gdx.files.internal("obstacle_saw.png"))
    private val obstacleBombTexture = Texture(Gdx.files.internal("obstacle_bomb.png"))
    private val obstacleRockTexture = Texture(Gdx.files.internal("obstacle_falling_rock.png"))
    private val coinTexture = Texture(Gdx.files.internal("coin_gold.png"))
    private val warningTexture = Texture(Gdx.files.internal("warning_marker.png"))
    private val bossTexture = Texture(Gdx.files.internal("boss_crate_golem.png"))
    private val pitTexture = Texture(Gdx.files.internal("gimmick_pit.png"))
    private val springTexture = Texture(Gdx.files.internal("gimmick_spring.png"))
    private val boostTexture = Texture(Gdx.files.internal("gimmick_boost.png"))
    private val mudTexture = Texture(Gdx.files.internal("gimmick_mud.png"))
    private val spikesTexture = Texture(Gdx.files.internal("gimmick_spikes.png"))
    private val windTexture = Texture(Gdx.files.internal("gimmick_wind.png"))

    init {
        add(player)
        add(enemy)
    }

    override fun update(delta: Float) {
        blinkTimer += delta
        updateFeedbackTimer(delta)
        if (state == GameState.IN_PLAY) {
            updateActiveTimers(delta)
        }

        when (state) {
            GameState.MENU -> updateMenu()
            GameState.IN_PLAY -> updateInPlay(delta)
            GameState.SHOP -> updateShop()
            GameState.GAME_OVER -> updateGameOver()
        }
    }

    private fun updateMenu() {
        if (InputHandler.isKeyJustPressed(InputHandler.ENTER)) {
            startGame()
        }

        if (InputHandler.isKeyJustPressed(InputHandler.U)) {
            val result = metaProgression.unlockNext()
            showFeedback(result.message)
        }
    }

    private fun startGame() {
        state = GameState.IN_PLAY
        hp = maxHp
        coins = 0
        lastGemReward = 0
        activeCooldownTimer = 0f
        shieldTimer = 0f
        slowTimer = 0f
        overdriveTimer = 0f
        bossHp = bossMaxHp
        bossAttackTimer = 0f
        bossAttackIndex = 0
        bossDefeated = false
        obstacles.clear()
        coinPickups.clear()
        stageGimmicks.clear()
        patternTimer = 0f
        runTimer = 0f
        coinStreak = 0
        coinStreakTimer = 0f
        luckySlotCounter = 0
        obstacleSpawnCounter = 0
        feverGauge = 0f
        feverTimer = 0f
        emergencyPotionUsed = false
        nearMissRouletteCount = 0
        feedbackMessage = ""
        feedbackTimer = 0f
        offsetX = 0f
        offsetY = 0f
        scoreSystem.reset()
        stageManager.reset()
        patternSpawner.reset()
        inventory.resetRun()
        syncPlayerItemEffects()
    }

    private fun updateInPlay(delta: Float) {
        val stageBeforeMove = stageManager.currentStage

        runTimer += delta
        offsetX += stageBeforeMove.scrollSpeed * delta
        offsetX = offsetX.coerceAtLeast(0f)
        offsetY = offsetY.coerceIn(0f, worldHeight - screenHeight)

        stageManager.update(offsetX, delta)

        if (stageManager.shouldOpenShop(offsetX)) {
            openShop()
            return
        }

        syncPlayerItemEffects()
        handleActiveInput()

        val activeStage = stageManager.currentStage
        updateAllObjects(delta)
        updateObstacles(delta, activeStage)
        updateCoinPickups(delta, activeStage)
        updateStageGimmicks(delta, activeStage)
        spawnStagePattern(delta, activeStage)
        updateBoss(delta, activeStage)
        scoreSystem.update(
            delta = delta,
            baseScoreMultiplier = inventory.baseScoreMultiplier() *
                    inventory.lowHpRewardMultiplier(hp / maxHp) *
                    feverScoreMultiplier()
        )
        drainHp(delta)

        if (state != GameState.IN_PLAY) {
            return
        }

        checkGimmickInteractions(delta)
        if (state != GameState.IN_PLAY) {
            return
        }

        checkObstacleInteractions()
        removeDead()
    }

    private fun openShop() {
        val shopDepth = stageManager.currentShopDepth()
        stageManager.consumeShopGate()
        inventory.resetStage()
        obstacles.clear()
        coinPickups.clear()
        stageGimmicks.clear()
        patternTimer = 0f

        val heal = inventory.stageClearHeal()
        if (heal > 0) {
            healPlayer(heal)
        }

        val interest = if (inventory.hasCoinInterestSystem()) {
            (coins * 0.12f).roundToInt().coerceIn(0, 24)
        } else {
            0
        }
        if (interest > 0) {
            coins += interest
        }

        shopSystem.open(
            depth = shopDepth,
            inventory = inventory,
            discountRate = inventory.shopDiscountRate(),
            freeRerolls = inventory.freeRerollsPerShop()
        )
        state = GameState.SHOP

        val rewards = mutableListOf("스테이지 클리어")
        if (heal > 0) rewards.add("체력 +$heal")
        if (interest > 0) rewards.add("이자 +$interest 코인")
        if (heal > 0) {
            showFeedback(rewards.joinToString(" / "))
        } else {
            showFeedback("${rewards.joinToString(" / ")}! 아이템을 고르세요")
        }
    }

    private fun updateShop() {
        if (InputHandler.isKeyJustPressed(InputHandler.LEFT)) {
            shopSystem.moveLeft()
        }

        if (InputHandler.isKeyJustPressed(InputHandler.RIGHT)) {
            shopSystem.moveRight()
        }

        if (InputHandler.isKeyJustPressed(InputHandler.R)) {
            rerollShop()
        }

        if (InputHandler.isKeyJustPressed(InputHandler.L)) {
            if (inventory.hasShopLockSystem()) {
                showFeedback(shopSystem.toggleLockSelected())
            } else {
                showFeedback("상점 예약권이 필요해")
            }
        }

        if (InputHandler.isKeyJustPressed(InputHandler.ENTER)) {
            buySelectedItem()
        }

        if (InputHandler.isKeyJustPressed(InputHandler.SPACE)) {
            closeShop("상점 스킵")
        }
    }

    private fun buySelectedItem() {
        val offer = shopSystem.selectedOffer()
        if (offer == null) {
            closeShop("구매할 수 있는 아이템이 없습니다")
            return
        }

        if (coins < offer.price) {
            showFeedback("코인이 ${offer.price - coins}개 더 필요해")
            return
        }

        if (!inventory.addItem(offer.definition)) {
            showFeedback("${offer.definition.name}은 이미 최대 중첩")
            shopSystem.reroll(inventory, inventory.shopDiscountRate(), consumeReroll = false)
            return
        }

        coins -= offer.price
        shopSystem.consumeLockIf(offer.definition)
        syncPlayerItemEffects()
        closeShop("구매: ${offer.definition.name} - ${offer.definition.description}")
    }

    private fun rerollShop() {
        val cost = shopSystem.rerollCost(inventory.shopDiscountRate())
        if (coins < cost) {
            showFeedback("코인이 ${cost - coins}개 더 필요해")
            return
        }

        coins -= cost
        shopSystem.reroll(inventory, inventory.shopDiscountRate())
        showFeedback("상점 새로고침")
    }

    private fun closeShop(message: String) {
        state = GameState.IN_PLAY
        inventory.resetStage()
        stageManager.update(offsetX, 0f)
        showFeedback(message)
    }

    private fun updateObstacles(delta: Float, stage: StageConfig) {
        val obstacleSpeed = if (slowTimer > 0f) {
            stage.obstacleSpeed * 0.55f
        } else {
            stage.obstacleSpeed
        }

        for (obstacle in obstacles) {
            obstacle.motionTimer += delta
            obstacle.x -= obstacleSpeed * obstacleSpeedMultiplier(obstacle.type) * delta
            updateObstacleMotion(obstacle, delta, stage)

            if (!obstacle.passed && obstacle.x + obstacle.width < player.x) {
                obstacle.passed = true
            }

            if (obstacle.x + obstacle.width < offsetX - 120f) {
                obstacle.isDead = true
            }
        }

        obstacles.removeIf { it.isDead }
    }

    private fun obstacleSpeedMultiplier(type: ObstacleType): Float {
        return when (type) {
            ObstacleType.BARREL -> 1.24f
            ObstacleType.FALLING_ROCK -> 0.58f
            else -> 1f
        }
    }

    private fun updateObstacleMotion(obstacle: Obstacle, delta: Float, stage: StageConfig) {
        when (obstacle.type) {
            ObstacleType.FLYING,
            ObstacleType.SAW -> {
                obstacle.y = obstacle.baseY + MathUtils.sin(obstacle.motionTimer * 3.6f + obstacle.phase) * 28f
            }
            ObstacleType.BOMB -> {
                val jump = kotlin.math.abs(MathUtils.sin(obstacle.motionTimer * 4.6f + obstacle.phase)) * 72f
                obstacle.y = obstacle.baseY + jump
            }
            ObstacleType.FALLING_ROCK -> {
                obstacle.y -= (300f + stage.index * 28f) * delta
                if (obstacle.y < groundY + 4f) {
                    obstacle.y = groundY + 4f
                }
            }
            ObstacleType.SMALL,
            ObstacleType.BIG,
            ObstacleType.BARREL -> {
                obstacle.y = obstacle.baseY
            }
        }
    }

    private fun updateCoinPickups(delta: Float, stage: StageConfig) {
        val coinSpeed = if (slowTimer > 0f) {
            stage.obstacleSpeed * 0.55f
        } else {
            stage.obstacleSpeed
        }
        val magnetRadius = inventory.coinMagnetRadius()

        for (coin in coinPickups) {
            coin.x -= coinSpeed * delta

            if (isCoinInsideMagnetRange(coin, magnetRadius)) {
                moveCoinTowardPlayer(coin, delta)
            }

            if (isPlayerCollectingCoin(coin)) {
                collectCoin(coin)
                continue
            }

            if (coin.x + coinSize < offsetX - 120f) {
                coin.isDead = true
            }
        }

        coinPickups.removeIf { it.isDead }
    }

    private fun updateStageGimmicks(delta: Float, stage: StageConfig) {
        val gimmickSpeed = if (slowTimer > 0f) {
            stage.obstacleSpeed * 0.55f
        } else {
            stage.obstacleSpeed
        }

        for (gimmick in stageGimmicks) {
            gimmick.x -= gimmickSpeed * delta

            if (!gimmick.passed && gimmick.x + gimmick.width < player.x) {
                rewardGimmickPass(gimmick)
            }

            if (gimmick.x + gimmick.width < offsetX - 140f) {
                gimmick.isDead = true
            }
        }

        stageGimmicks.removeIf { it.isDead }
    }

    private fun spawnStagePattern(delta: Float, stage: StageConfig) {
        patternTimer += delta

        if (patternTimer < stage.spawnDelay) {
            return
        }

        val startX = offsetX + screenWidth + 130f
        val spawns = patternSpawner.nextPattern(stage, startX)

        for (spawn in spawns) {
            addObstacleFromSpawn(spawn)
        }

        for (gimmickSpawn in patternSpawner.nextGimmicks(stage, startX)) {
            stageGimmicks.add(
                StageGimmick(
                    x = gimmickSpawn.x,
                    width = gimmickSpawn.width,
                    type = gimmickSpawn.type
                )
            )
        }

        for (coinSpawn in patternSpawner.nextCoinTrail(stage, startX)) {
            coinPickups.add(
                CoinPickup(
                    x = coinSpawn.x,
                    y = coinSpawn.y,
                    value = coinSpawn.value
                )
            )
        }

        patternTimer = 0f
    }

    private fun addObstacle(type: ObstacleType, x: Float, bounty: Boolean = false) {
        addObstacleFromSpawn(patternSpawner.createSpawn(type, x), bounty)
    }

    private fun addObstacleFromSpawn(spawn: ObstacleSpawn, forceBounty: Boolean = false) {
        obstacleSpawnCounter++
        val hasBounty = forceBounty || (inventory.hasBountySystem() && obstacleSpawnCounter % 5 == 0)
        val phase = obstacleSpawnCounter * 0.73f
        obstacles.add(
            Obstacle(
                x = spawn.x,
                y = spawn.y,
                baseY = spawn.y,
                width = spawn.width,
                height = spawn.height,
                type = spawn.type,
                phase = phase,
                bounty = hasBounty
            )
        )
    }

    private fun updateBoss(delta: Float, stage: StageConfig) {
        if (stage.index < 5 || bossDefeated || state != GameState.IN_PLAY) {
            return
        }

        bossAttackTimer += delta
        if (bossAttackTimer < 3.8f) {
            return
        }

        spawnBossAttack()
        bossAttackTimer = 0f
    }

    private fun spawnBossAttack() {
        val startX = offsetX + screenWidth + 230f
        when (bossAttackIndex % 4) {
            0 -> {
                addObstacle(ObstacleType.BARREL, startX + 80f, bounty = true)
                addObstacle(ObstacleType.FALLING_ROCK, startX + 720f)
            }
            1 -> {
                addObstacle(ObstacleType.SAW, startX + 160f)
                addObstacle(ObstacleType.BOMB, startX + 820f, bounty = true)
            }
            2 -> {
                addObstacle(ObstacleType.FALLING_ROCK, startX + 120f)
                addObstacle(ObstacleType.FALLING_ROCK, startX + 700f)
                addObstacle(ObstacleType.SMALL, startX + 1260f)
            }
            else -> {
                addObstacle(ObstacleType.BIG, startX + 120f)
                addObstacle(ObstacleType.SAW, startX + 780f)
                addObstacle(ObstacleType.BARREL, startX + 1360f, bounty = true)
            }
        }
        bossAttackIndex++
    }

    private fun damageBoss(amount: Float): String {
        if (stageManager.currentStage.index < 5 || bossDefeated) {
            return ""
        }

        bossHp = (bossHp - amount).coerceAtLeast(0f)
        if (bossHp > 0f) {
            return "보스 피해 ${amount.roundToInt()}"
        }

        bossDefeated = true
        coins += 120
        scoreSystem.addBonusScore(1500)
        addFeverGauge(35f)
        return "보스 격파! +120코인 / 점수 +1500"
    }

    private fun isCoinInsideMagnetRange(coin: CoinPickup, magnetRadius: Float): Boolean {
        val coinCenterX = coin.x + coinSize / 2f
        val coinCenterY = coin.y + coinSize / 2f
        val playerCenterX = player.x + player.width / 2f
        val playerCenterY = player.y + player.height / 2f
        val dx = playerCenterX - coinCenterX
        val dy = playerCenterY - coinCenterY
        return sqrt(dx * dx + dy * dy) <= magnetRadius
    }

    private fun moveCoinTowardPlayer(coin: CoinPickup, delta: Float) {
        val coinCenterX = coin.x + coinSize / 2f
        val coinCenterY = coin.y + coinSize / 2f
        val playerCenterX = player.x + player.width / 2f
        val playerCenterY = player.y + player.height / 2f
        val dx = playerCenterX - coinCenterX
        val dy = playerCenterY - coinCenterY
        val distance = sqrt(dx * dx + dy * dy)

        if (distance <= 0.01f) {
            return
        }

        val moveDistance = min(distance, 720f * delta)
        coin.x += dx / distance * moveDistance
        coin.y += dy / distance * moveDistance
    }

    private fun isPlayerCollectingCoin(coin: CoinPickup): Boolean {
        return overlaps(
            leftA = player.x + 10f,
            rightA = player.x + player.width - 10f,
            bottomA = player.y + 10f,
            topA = player.y + player.height - 10f,
            leftB = coin.x,
            rightB = coin.x + coinSize,
            bottomB = coin.y,
            topB = coin.y + coinSize
        )
    }

    private fun collectCoin(coin: CoinPickup) {
        val baseGain = inventory.coinPickupGain(
            baseCoins = coin.value,
            hpRatio = hp / maxHp,
            airborne = player.isAirborne()
        )
        val gained = (baseGain * feverRewardMultiplier()).roundToInt().coerceAtLeast(1)
        val streakMessage = updateCoinStreak()
        val slotMessage = triggerLuckySlot()
        coins += gained
        coin.isDead = true
        addFeverGauge(2f)

        val parts = mutableListOf<String>()
        if (gained >= 3) parts.add("맵 코인 +$gained")
        if (streakMessage.isNotBlank()) parts.add(streakMessage)
        if (slotMessage.isNotBlank()) parts.add(slotMessage)
        if (parts.isNotEmpty()) {
            showFeedback(parts.joinToString(" / "))
        }
    }

    private fun updateCoinStreak(): String {
        if (!inventory.hasCoinStreakSystem()) {
            return ""
        }

        coinStreak = if (coinStreakTimer > 0f) coinStreak + 1 else 1
        coinStreakTimer = 1.35f

        if (coinStreak % 8 != 0) {
            return "코인연쇄 ${coinStreak}"
        }

        val bonus = 8 + stageManager.currentStage.index * 2
        coins += bonus
        scoreSystem.addBonusScore(bonus * 8)
        addFeverGauge(10f)
        return "코인연쇄 ${coinStreak}! +$bonus"
    }

    private fun triggerLuckySlot(): String {
        if (!inventory.hasLuckySlotSystem()) {
            return ""
        }

        luckySlotCounter++
        if (luckySlotCounter % 7 != 0) {
            return ""
        }

        return when (MathUtils.random(0, 99)) {
            in 0..54 -> {
                coins += 16
                "슬롯 잭팟 +16"
            }
            in 55..84 -> {
                coins += 6
                "슬롯 당첨 +6"
            }
            else -> {
                val loss = min(5, coins)
                coins -= loss
                "슬롯 꽝 -$loss"
            }
        }
    }

    private fun checkGimmickInteractions(delta: Float) {
        for (gimmick in stageGimmicks) {
            if (gimmick.isDead || !isPlayerTouchingGimmick(gimmick)) {
                continue
            }

            when (gimmick.type) {
                GimmickType.PIT -> handlePit(gimmick)
                GimmickType.SPRING -> handleSpring(gimmick)
                GimmickType.BOOST -> handleBoostPad(gimmick)
                GimmickType.MUD -> handleMud(gimmick, delta)
                GimmickType.SPIKES -> handleSpikes(gimmick)
                GimmickType.WIND -> handleWindColumn(gimmick)
            }

            if (state != GameState.IN_PLAY) {
                break
            }
        }

        stageGimmicks.removeIf { it.isDead }
    }

    private fun handlePit(gimmick: StageGimmick) {
        if (gimmick.triggered || !player.isGrounded()) {
            return
        }

        gimmick.triggered = true
        coinStreak = 0
        coinStreakTimer = 0f
        damagePlayer(42f)
        if (state != GameState.IN_PLAY) {
            return
        }
        player.launchUp(620f)
        showFeedback("구멍에 빠짐! 점프로 넘어야 해")
    }

    private fun handleSpring(gimmick: StageGimmick) {
        if (gimmick.triggered || !player.isGrounded()) {
            return
        }

        gimmick.triggered = true
        player.launchUp(1480f)
        coins += 3
        scoreSystem.addBonusScore(35)
        addFeverGauge(8f)
        showFeedback("스프링 점프! 코인 +3")
    }

    private fun handleBoostPad(gimmick: StageGimmick) {
        if (gimmick.triggered || !player.isGrounded()) {
            return
        }

        gimmick.triggered = true
        activeCooldownTimer = (activeCooldownTimer - 1.2f).coerceAtLeast(0f)
        scoreSystem.addBonusScore(70)
        addFeverGauge(10f)
        showFeedback("부스트 발판! 점수 +70 / 대기시간 감소")
    }

    private fun handleMud(gimmick: StageGimmick, delta: Float) {
        if (!player.isGrounded()) {
            return
        }

        hp -= 12f * delta
        activeCooldownTimer += 0.25f * delta
        coinStreak = 0
        coinStreakTimer = 0f

        if (!gimmick.triggered) {
            gimmick.triggered = true
            showFeedback("진흙 구간! 체력과 연쇄가 깎여")
        }

        if (hp <= 0f) {
            hp = 0f
            finishGame()
        }
    }

    private fun handleSpikes(gimmick: StageGimmick) {
        if (gimmick.triggered || !player.isGrounded()) {
            return
        }

        gimmick.triggered = true
        damagePlayer(34f)
        if (state != GameState.IN_PLAY) {
            return
        }
        showFeedback("가시 밟음! 점프로 피해야 해")
    }

    private fun handleWindColumn(gimmick: StageGimmick) {
        if (gimmick.triggered) {
            return
        }

        gimmick.triggered = true
        player.launchUp(1180f)
        addFeverGauge(7f)
        showFeedback("바람기둥 상승!")
    }

    private fun rewardGimmickPass(gimmick: StageGimmick) {
        if (gimmick.passed) {
            return
        }

        gimmick.passed = true

        when (gimmick.type) {
            GimmickType.PIT -> {
                if (!gimmick.triggered) {
                    val reward = 5 + stageManager.currentStage.index
                    coins += reward
                    scoreSystem.addBonusScore(reward * 10)
                    addFeverGauge(9f)
                    showFeedback("구멍 점프 통과! 코인 +$reward")
                }
            }
            GimmickType.SPIKES -> {
                if (!gimmick.triggered) {
                    scoreSystem.addBonusScore(25)
                    addFeverGauge(5f)
                }
            }
            else -> Unit
        }
    }

    private fun isPlayerTouchingGimmick(gimmick: StageGimmick): Boolean {
        val playerCenterX = player.x + player.width / 2f

        return when (gimmick.type) {
            GimmickType.PIT -> playerCenterX >= gimmick.x && playerCenterX <= gimmick.x + gimmick.width
            GimmickType.MUD,
            GimmickType.SPIKES -> overlaps(
                leftA = player.x + 16f,
                rightA = player.x + player.width - 16f,
                bottomA = player.y,
                topA = player.y + 20f,
                leftB = gimmick.x,
                rightB = gimmick.x + gimmick.width,
                bottomB = groundY,
                topB = groundY + 34f
            )
            GimmickType.SPRING,
            GimmickType.BOOST -> overlaps(
                leftA = player.x + 16f,
                rightA = player.x + player.width - 16f,
                bottomA = player.y,
                topA = player.y + 18f,
                leftB = gimmick.x,
                rightB = gimmick.x + gimmick.width,
                bottomB = groundY,
                topB = groundY + 26f
            )
            GimmickType.WIND -> overlaps(
                leftA = player.x + 12f,
                rightA = player.x + player.width - 12f,
                bottomA = player.y,
                topA = player.y + player.height,
                leftB = gimmick.x,
                rightB = gimmick.x + gimmick.width,
                bottomB = groundY,
                topB = screenHeight
            )
        }
    }

    private fun drainHp(delta: Float) {
        hp -= 1f * delta
        triggerEmergencyPotionIfNeeded()

        if (hp <= 0f) {
            hp = 0f
            finishGame()
        }
    }

    private fun triggerEmergencyPotionIfNeeded() {
        if (!inventory.hasEmergencyPotionSystem() || emergencyPotionUsed || hp > maxHp * 0.22f) {
            return
        }

        emergencyPotionUsed = true
        healPlayer(135)
        shieldTimer = maxOf(shieldTimer, 2.5f)
        showFeedback("긴급 물약 발동: 체력 +135 / 보호막")
    }

    private fun checkObstacleInteractions() {
        for (obstacle in obstacles) {
            if (obstacle.isDead) {
                continue
            }

            val isColliding = isPlayerCollidingWithObstacle(obstacle)
            if (isColliding) {
                damagePlayer(30f)
                obstacle.isDead = true
                if (state == GameState.GAME_OVER) {
                    break
                }
                continue
            }

            if (!obstacle.slideTrickChecked &&
                inventory.hasSlideTrickSystem() &&
                isPlayerSlidingUnderObstacle(obstacle)
            ) {
                rewardSlideTrick(obstacle)
            }

            if (!obstacle.nearMissChecked && isPlayerNearObstacle(obstacle)) {
                rewardNearMiss(obstacle)
            }
        }

        obstacles.removeIf { it.isDead }
    }

    private fun rewardNearMiss(obstacle: Obstacle) {
        val overdriveMultiplier = if (overdriveTimer > 0f) 2f else 1f
        val rewardMultiplier = overdriveMultiplier * feverRewardMultiplier()
        val lowHpMultiplier = inventory.lowHpRewardMultiplier(hp / maxHp)
        val bonus = scoreSystem.rewardNearMiss(
            scoreBonusMultiplier = inventory.nearMissScoreMultiplier() * rewardMultiplier * lowHpMultiplier,
            multiplierGrowthBonus = inventory.multiplierGrowthBonus(),
            maxMultiplier = inventory.maxMultiplier()
        )
        val coinGain = (inventory.nearMissCoinGain(4 + stageManager.currentStage.index) * rewardMultiplier * lowHpMultiplier)
            .roundToInt()
            .coerceAtLeast(1)
        val comboCoinGain = inventory.comboCoinReward(scoreSystem.combo)
        val cooldownRefund = inventory.nearMissCooldownRefund()
        val heal = inventory.comboHeal(scoreSystem.combo)
        val bountyMessage = rewardBountyIfAvailable(obstacle, "현상금")
        val rouletteMessage = triggerNearMissRoulette()
        val bossMessage = damageBoss(18f + scoreSystem.combo.coerceAtMost(40) * 0.45f)

        coins += coinGain + comboCoinGain
        if (cooldownRefund > 0f && activeCooldownTimer > 0f) {
            activeCooldownTimer = (activeCooldownTimer - cooldownRefund).coerceAtLeast(0f)
        }
        if (heal > 0) {
            healPlayer(heal)
        }

        obstacle.nearMissChecked = true
        addFeverGauge(12f)

        val parts = mutableListOf("니어미스 점수 +$bonus", "코인 +${coinGain + comboCoinGain}")
        if (comboCoinGain > 0) parts.add("콤보금고 +$comboCoinGain")
        if (cooldownRefund > 0f) parts.add("대기-${cooldownRefund}초")
        if (heal > 0) parts.add("체력 +$heal")
        if (bountyMessage.isNotBlank()) parts.add(bountyMessage)
        if (rouletteMessage.isNotBlank()) parts.add(rouletteMessage)
        if (bossMessage.isNotBlank()) parts.add(bossMessage)
        showFeedback(parts.joinToString(" / "))
    }

    private fun isPlayerSlidingUnderObstacle(obstacle: Obstacle): Boolean {
        if (!isAirObstacle(obstacle.type) || !player.isSliding()) {
            return false
        }

        val horizontalOverlap = player.x + player.width > obstacle.x &&
                player.x < obstacle.x + obstacle.width
        val playerTop = player.y + player.height
        return horizontalOverlap && playerTop <= obstacle.y + 18f
    }

    private fun rewardSlideTrick(obstacle: Obstacle) {
        val coinGain = ((5 + stageManager.currentStage.index) * feverRewardMultiplier()).roundToInt()
        val scoreGain = (18 * stageManager.currentStage.index * feverScoreMultiplier()).roundToInt()
        coins += coinGain
        scoreSystem.addBonusScore(scoreGain)
        obstacle.slideTrickChecked = true
        addFeverGauge(10f)

        val bountyMessage = rewardBountyIfAvailable(obstacle, "슬라이드 현상금")
        val bossMessage = damageBoss(24f)
        val parts = mutableListOf("슬라이드 통과 +$coinGain 코인", "점수 +$scoreGain")
        if (bountyMessage.isNotBlank()) parts.add(bountyMessage)
        if (bossMessage.isNotBlank()) parts.add(bossMessage)
        showFeedback(parts.joinToString(" / "))
    }

    private fun rewardBountyIfAvailable(obstacle: Obstacle, label: String): String {
        if (!obstacle.bounty) {
            return ""
        }

        val coinGain = 10 + stageManager.currentStage.index * 3
        val scoreGain = 45 + stageManager.currentStage.index * 15
        coins += coinGain
        scoreSystem.addBonusScore(scoreGain)
        obstacle.bounty = false
        addFeverGauge(14f)
        return "$label +$coinGain 코인"
    }

    private fun triggerNearMissRoulette(): String {
        if (!inventory.hasNearMissRouletteSystem()) {
            return ""
        }

        nearMissRouletteCount++
        if (nearMissRouletteCount % 4 != 0) {
            return ""
        }

        return when (MathUtils.random(0, 3)) {
            0 -> {
                coins += 18
                "룰렛 +18 코인"
            }
            1 -> {
                healPlayer(28)
                "룰렛 체력 +28"
            }
            2 -> {
                activeCooldownTimer = (activeCooldownTimer - 3f).coerceAtLeast(0f)
                "룰렛 대기-3초"
            }
            else -> {
                addFeverGauge(24f)
                "룰렛 피버 +24"
            }
        }
    }

    private fun damagePlayer(damage: Float) {
        if (shieldTimer > 0f) {
            shieldTimer = 0f
            showFeedback("보호막으로 피격 무시")
            return
        }

        val coinShieldCost = 14 + stageManager.currentStage.index * 4
        if (inventory.hasCoinShieldSystem() && coins >= coinShieldCost) {
            coins -= coinShieldCost
            showFeedback("동전 방패 발동: 코인 -$coinShieldCost")
            return
        }

        val adjustedDamage = inventory.adjustedDamage(damage)
        hp -= adjustedDamage
        triggerEmergencyPotionIfNeeded()

        val preservedCombo = inventory.comboToPreserve(scoreSystem.combo)
        scoreSystem.resetCombo(
            preservedCombo = preservedCombo,
            multiplierGrowthBonus = inventory.multiplierGrowthBonus(),
            maxMultiplier = inventory.maxMultiplier()
        )

        if (preservedCombo > 0) {
            showFeedback("피격! 콤보 $preservedCombo 보존")
        } else {
            showFeedback("피격! 배율 초기화")
        }

        if (hp <= 0f) {
            hp = 0f
            finishGame()
        }
    }

    private fun syncPlayerItemEffects() {
        player.setBonusJumpCount(inventory.bonusJumpCount())
    }

    private fun updateActiveTimers(delta: Float) {
        activeCooldownTimer = (activeCooldownTimer - delta).coerceAtLeast(0f)
        shieldTimer = (shieldTimer - delta).coerceAtLeast(0f)
        slowTimer = (slowTimer - delta).coerceAtLeast(0f)
        overdriveTimer = (overdriveTimer - delta).coerceAtLeast(0f)
        feverTimer = (feverTimer - delta).coerceAtLeast(0f)
        coinStreakTimer = (coinStreakTimer - delta).coerceAtLeast(0f)
        if (coinStreakTimer <= 0f) {
            coinStreak = 0
        }
    }

    private fun handleActiveInput() {
        if (!InputHandler.isKeyJustPressed(InputHandler.X)) {
            return
        }

        if (activeCooldownTimer > 0f) {
            showFeedback("액티브 대기 ${activeCooldownTimer.toInt() + 1}초")
            return
        }

        if (inventory.hasQuickDrop() && player.isAirborne()) {
            player.forceLanding()
            activeCooldownTimer = 2.2f
            showFeedback("중력 앵커: 즉시 착지")
            return
        }

        val usedMessages = mutableListOf<String>()

        if (inventory.hasActiveShield()) {
            shieldTimer = 3f
            usedMessages.add("보호막")
        }

        if (inventory.hasTimeSlow()) {
            slowTimer = 3.2f
            usedMessages.add("시간감속")
        }

        if (inventory.hasObstacleBreak() && breakNearestObstacle()) {
            usedMessages.add("장애물제거")
        }

        val dashDistance = inventory.dashDistance()
        if (dashDistance > 0f) {
            player.dashForward(dashDistance)
            usedMessages.add("대시")
        }

        if (inventory.hasNearMissOverdrive()) {
            overdriveTimer = 5f
            usedMessages.add("오버드라이브")
        }

        if (usedMessages.isEmpty()) {
            if (inventory.hasQuickDrop()) {
                showFeedback("중력 앵커는 공중에서 X")
            } else {
                showFeedback("사용 가능한 액티브 아이템 없음")
            }
            return
        }

        activeCooldownTimer = 6f
        showFeedback("액티브 발동: ${usedMessages.joinToString("/")}")
    }

    private fun breakNearestObstacle(): Boolean {
        val target = obstacles
            .filter { !it.isDead && it.x + it.width > player.x }
            .minByOrNull { it.x }

        if (target != null) {
            rewardBountyIfAvailable(target, "현상금 제거")
            damageBoss(60f)
            target.isDead = true
        }
        return target != null
    }

    private fun addFeverGauge(amount: Float) {
        if (!inventory.hasFeverSystem() || feverTimer > 0f) {
            return
        }

        feverGauge += amount
        if (feverGauge >= 100f) {
            feverGauge = 0f
            feverTimer = 6f
            showFeedback("피버 발동! 점수와 코인 보상 증가")
        }
    }

    private fun feverRewardMultiplier(): Float {
        return if (feverTimer > 0f) 2f else 1f
    }

    private fun feverScoreMultiplier(): Float {
        return if (feverTimer > 0f) 1.7f else 1f
    }

    private fun healPlayer(amount: Int) {
        hp = (hp + amount).coerceAtMost(maxHp)
    }

    private fun finishGame() {
        if (state == GameState.GAME_OVER) {
            return
        }

        obstacles.clear()
        coinPickups.clear()
        stageGimmicks.clear()
        lastGemReward = metaProgression.grantGameOverGems(
            score = scoreSystem.score,
            bestCombo = scoreSystem.bestCombo,
            highestStage = stageManager.highestStageReached
        )
        state = GameState.GAME_OVER
    }

    private fun isPlayerCollidingWithObstacle(obstacle: Obstacle): Boolean {
        val playerPaddingX = 12f
        val playerPaddingY = 10f
        val obstaclePaddingX = when (obstacle.type) {
            ObstacleType.SMALL -> 10f
            ObstacleType.BIG -> 14f
            ObstacleType.FLYING -> 12f
            ObstacleType.BARREL -> 10f
            ObstacleType.SAW -> 14f
            ObstacleType.BOMB -> 12f
            ObstacleType.FALLING_ROCK -> 14f
        }
        val obstaclePaddingY = when (obstacle.type) {
            ObstacleType.SMALL -> 8f
            ObstacleType.BIG -> 12f
            ObstacleType.FLYING -> 10f
            ObstacleType.BARREL -> 10f
            ObstacleType.SAW -> 14f
            ObstacleType.BOMB -> 12f
            ObstacleType.FALLING_ROCK -> 14f
        }

        return overlaps(
            leftA = player.x + playerPaddingX,
            rightA = player.x + player.width - playerPaddingX,
            bottomA = player.y + playerPaddingY,
            topA = player.y + player.height - playerPaddingY,
            leftB = obstacle.x + obstaclePaddingX,
            rightB = obstacle.x + obstacle.width - obstaclePaddingX,
            bottomB = obstacle.y + obstaclePaddingY,
            topB = obstacle.y + obstacle.height - obstaclePaddingY
        )
    }

    private fun isAirObstacle(type: ObstacleType): Boolean {
        return type == ObstacleType.FLYING || type == ObstacleType.SAW
    }

    private fun isPlayerNearObstacle(obstacle: Obstacle): Boolean {
        if (isPlayerCollidingWithObstacle(obstacle)) {
            return false
        }

        val nearPaddingX = 34f + inventory.nearMissPaddingBonus()
        val nearPaddingY = 24f + inventory.nearMissPaddingBonus() * 0.5f

        return overlaps(
            leftA = player.x + 8f,
            rightA = player.x + player.width - 8f,
            bottomA = player.y + 8f,
            topA = player.y + player.height - 8f,
            leftB = obstacle.x - nearPaddingX,
            rightB = obstacle.x + obstacle.width + nearPaddingX,
            bottomB = obstacle.y - nearPaddingY,
            topB = obstacle.y + obstacle.height + nearPaddingY
        )
    }

    private fun overlaps(
        leftA: Float,
        rightA: Float,
        bottomA: Float,
        topA: Float,
        leftB: Float,
        rightB: Float,
        bottomB: Float,
        topB: Float
    ): Boolean {
        return rightA > leftB &&
                leftA < rightB &&
                topA > bottomB &&
                bottomA < topB
    }

    private fun updateFeedbackTimer(delta: Float) {
        if (feedbackTimer > 0f) {
            feedbackTimer = (feedbackTimer - delta).coerceAtLeast(0f)
        }
    }

    private fun showFeedback(message: String) {
        feedbackMessage = message
        feedbackTimer = 1.4f
    }

    private fun updateGameOver() {
        if (InputHandler.isKeyJustPressed(InputHandler.R)) {
            restartGame()
        }

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

    override fun drawBackground(batch: SpriteBatch) {
        drawSkyFallback(batch)
        drawParallaxBackground(batch)
        drawStageTint(batch)
        drawGroundTexture(batch)
        drawStageGimmicks(batch)
        batch.color = Color.WHITE
    }

    private fun drawSkyFallback(batch: SpriteBatch) {
        val stripeCount = 24
        val stripeHeight = screenHeight / stripeCount

        for (i in 0 until stripeCount) {
            val t = i.toFloat() / stripeCount
            batch.color = Color(
                0.04f + t * 0.14f,
                0.42f + t * 0.30f,
                0.82f + t * 0.12f,
                1f
            )
            batch.draw(tileTexture, 0f, i * stripeHeight, screenWidth, stripeHeight + 1f)
        }
    }

    private fun drawParallaxBackground(batch: SpriteBatch) {
        val height = screenHeight
        val width = backgroundTexture.width * (height / backgroundTexture.height)
        val scrollX = offsetX * 0.18f

        batch.color = when (stageManager.currentStage.index) {
            1 -> Color.WHITE
            2 -> Color(1f, 0.95f, 0.84f, 1f)
            3 -> Color(0.88f, 0.96f, 1f, 1f)
            4 -> Color(1f, 0.86f, 0.86f, 1f)
            else -> Color(0.92f, 0.86f, 1f, 1f)
        }

        drawRepeatingTexture(batch, backgroundTexture, scrollX, 0f, width, height)
    }

    private fun drawGroundTexture(batch: SpriteBatch) {
        val groundHeight = groundY + 55f
        val groundWidth = groundRegion.regionWidth * (groundHeight / groundRegion.regionHeight)
        val scrollX = offsetX * 0.85f

        batch.color = Color.WHITE
        drawRepeatingRegion(batch, groundRegion, scrollX, 0f, groundWidth, groundHeight)
    }

    private fun drawStageTint(batch: SpriteBatch) {
        val tintColor = when (stageManager.currentStage.index) {
            1 -> null
            2 -> Color(0.55f, 0.32f, 0.08f, 0.10f)
            3 -> Color(0.05f, 0.28f, 0.55f, 0.12f)
            4 -> Color(0.55f, 0.05f, 0.08f, 0.16f)
            else -> Color(0.20f, 0.05f, 0.42f, 0.18f)
        } ?: return

        batch.color = tintColor
        batch.draw(tileTexture, 0f, 0f, screenWidth, screenHeight)
    }

    private fun drawStageGimmicks(batch: SpriteBatch) {
        for (gimmick in stageGimmicks) {
            val screenX = gimmick.x - offsetX
            if (screenX > screenWidth + 80f || screenX + gimmick.width < -80f) {
                continue
            }

            when (gimmick.type) {
                GimmickType.PIT -> drawPit(batch, screenX, gimmick.width)
                GimmickType.SPRING -> drawSpring(batch, screenX, gimmick.width)
                GimmickType.BOOST -> drawBoostPad(batch, screenX, gimmick.width)
                GimmickType.MUD -> drawMud(batch, screenX, gimmick.width)
                GimmickType.SPIKES -> drawSpikes(batch, screenX, gimmick.width)
                GimmickType.WIND -> drawWindColumn(batch, screenX, gimmick.width)
            }
        }
    }

    private fun drawPit(batch: SpriteBatch, x: Float, width: Float) {
        batch.color = Color.WHITE
        batch.draw(pitTexture, x - 22f, groundY - 82f, width + 44f, 145f)
    }

    private fun drawSpring(batch: SpriteBatch, x: Float, width: Float) {
        batch.color = Color.WHITE
        batch.draw(springTexture, x - 12f, groundY - 16f, width + 24f, 82f)
    }

    private fun drawBoostPad(batch: SpriteBatch, x: Float, width: Float) {
        batch.color = Color.WHITE
        batch.draw(boostTexture, x - 16f, groundY - 16f, width + 32f, 76f)
    }

    private fun drawMud(batch: SpriteBatch, x: Float, width: Float) {
        batch.color = Color.WHITE
        batch.draw(mudTexture, x - 14f, groundY - 12f, width + 28f, 58f)
    }

    private fun drawSpikes(batch: SpriteBatch, x: Float, width: Float) {
        batch.color = Color.WHITE
        batch.draw(spikesTexture, x - 12f, groundY - 10f, width + 24f, 68f)
    }

    private fun drawWindColumn(batch: SpriteBatch, x: Float, width: Float) {
        batch.color = Color(0.45f, 0.90f, 1f, 0.16f)
        batch.draw(tileTexture, x + width * 0.12f, groundY, width * 0.76f, screenHeight - groundY)

        batch.color = Color.WHITE
        batch.draw(windTexture, x - 18f, groundY - 8f, width + 36f, 238f)
    }

    private fun drawRepeatingTexture(
        batch: SpriteBatch,
        texture: Texture,
        scrollX: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        var drawX = -positiveModulo(scrollX, width)

        while (drawX < screenWidth) {
            batch.draw(texture, drawX, y, width, height)
            drawX += width
        }
    }

    private fun drawRepeatingRegion(
        batch: SpriteBatch,
        region: TextureRegion,
        scrollX: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        var drawX = -positiveModulo(scrollX, width)

        while (drawX < screenWidth) {
            batch.draw(region, drawX, y, width, height)
            drawX += width
        }
    }

    private fun positiveModulo(value: Float, modulo: Float): Float {
        return ((value % modulo) + modulo) % modulo
    }

    override fun render(delta: Float) {
        super.render(delta)
        drawCoinPickups()
        drawBoss()
        drawObstacles()

        when (state) {
            GameState.MENU -> {
                drawMenuScreen()
                drawFeedback()
            }
            GameState.IN_PLAY -> {
                drawHud()
                drawHealthBar()
                drawBossHealthBar()
                drawInventorySummary()
                drawStageMessage()
                drawFeedback()
            }
            GameState.SHOP -> {
                drawHud()
                drawHealthBar()
                drawShopScreen()
                drawFeedback()
            }
            GameState.GAME_OVER -> {
                drawHud()
                drawHealthBar()
                drawBossHealthBar()
                drawGameOverOverlay()
                drawFeedback()
            }
        }
    }

    private fun drawCoinPickups() {
        batch.begin()

        for (coin in coinPickups) {
            val screenX = coin.x - offsetX
            val screenY = coin.y - offsetY
            val size = if (coin.value >= 3) coinSize + 8f else if (coin.value >= 2) coinSize + 4f else coinSize
            val drawWidth = size + 6f
            val drawHeight = drawWidth * 1.2f

            batch.color = if (coin.value >= 2) Color(1f, 0.93f, 0.60f, 1f) else Color.WHITE
            batch.draw(
                coinTexture,
                screenX - 3f,
                screenY - 5f,
                drawWidth,
                drawHeight
            )
        }

        batch.color = Color.WHITE
        batch.end()
    }

    private fun drawObstacles() {
        batch.begin()

        for (obstacle in obstacles) {
            val texture = when (obstacle.type) {
                ObstacleType.SMALL -> obstacleCrateSmallTexture
                ObstacleType.BIG -> obstacleCrateTallTexture
                ObstacleType.FLYING -> obstacleSawTexture
                ObstacleType.BARREL -> obstacleBarrelTexture
                ObstacleType.SAW -> obstacleSawTexture
                ObstacleType.BOMB -> obstacleBombTexture
                ObstacleType.FALLING_ROCK -> obstacleRockTexture
            }

            if (obstacle.type == ObstacleType.FALLING_ROCK && obstacle.y > groundY + 90f) {
                batch.color = Color.WHITE
                batch.draw(
                    warningTexture,
                    obstacle.x - offsetX + obstacle.width / 2f - 20f,
                    groundY + 10f - offsetY,
                    40f,
                    40f
                )
            }

            batch.color = when {
                obstacle.bounty -> Color(1f, 0.78f, 0.18f, 1f)
                obstacle.nearMissChecked -> Color(1f, 1f, 0.72f, 1f)
                else -> Color.WHITE
            }

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

    private fun drawBoss() {
        if (stageManager.currentStage.index < 5 || bossDefeated || state != GameState.IN_PLAY) {
            return
        }

        val bossWidth = 330f
        val bossHeight = 236f
        val bossX = screenWidth - bossWidth - 24f + MathUtils.sin(runTimer * 1.7f) * 8f
        val bossY = groundY + 38f + MathUtils.sin(runTimer * 2.2f) * 5f

        batch.begin()
        batch.color = Color.WHITE
        batch.draw(bossTexture, bossX, bossY, bossWidth, bossHeight)
        batch.end()
    }

    private fun drawMenuScreen() {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        val nextUnlock = metaProgression.nextLockedItem()?.name ?: "전부 해금됨"

        drawTextOnScreen(
            text = "스테이지 콤보 러너",
            x = centerX - 155f,
            y = centerY + 82f,
            color = Color.CYAN,
            scale = 2.2f
        )
        drawTextOnScreen(
            text = "SPACE: 점프 / 2단 점프   Z: 슬라이드   X: 액티브",
            x = centerX - 235f,
            y = centerY + 25f,
            color = Color.LIGHT_GRAY,
            scale = 1f
        )
        drawTextOnScreen(
            text = "아슬아슬하게 피하기 -> 코인 -> 상점 아이템 -> 태그 시너지",
            x = centerX - 260f,
            y = centerY - 5f,
            color = Color.YELLOW,
            scale = 1f
        )
        drawTextOnScreen(
            text = "보석: ${metaProgression.gems()}   U: 다음 아이템 해금(보석 5개): $nextUnlock",
            x = centerX - 285f,
            y = centerY - 38f,
            color = Color.WHITE,
            scale = 0.95f
        )

        if ((blinkTimer % 1.2f) < 0.6f) {
            drawTextOnScreen(
                text = "ENTER로 시작",
                x = centerX - 80f,
                y = centerY - 86f,
                color = Color.WHITE,
                scale = 1.3f
            )
        }
    }

    private fun drawHud() {
        val rightX = screenWidth - 205f
        val hudY = screenHeight - 16f

        drawTextOnScreen(
            text = "점수: ${scoreSystem.score}",
            x = rightX,
            y = hudY,
            color = Color.WHITE,
            scale = 1.10f
        )
        drawTextOnScreen(
            text = "코인: $coins",
            x = rightX,
            y = hudY - 24f,
            color = Color.GOLD,
            scale = 1f
        )
        drawTextOnScreen(
            text = "콤보: ${scoreSystem.combo}",
            x = rightX,
            y = hudY - 48f,
            color = Color.ORANGE,
            scale = 1f
        )
        drawTextOnScreen(
            text = "배율: ${scoreSystem.multiplierText()}",
            x = rightX,
            y = hudY - 72f,
            color = Color.YELLOW,
            scale = 1f
        )
        drawTextOnScreen(
            text = "시간: ${runTimeText()}",
            x = rightX,
            y = hudY - 96f,
            color = Color.LIGHT_GRAY,
            scale = 0.95f
        )
        drawTextOnScreen(
            text = "스테이지 ${stageManager.currentStage.index}: ${stageManager.currentStage.name}",
            x = screenWidth / 2f - 155f,
            y = hudY,
            color = Color.WHITE,
            scale = 1f
        )
    }

    private fun runTimeText(): String {
        val totalSeconds = runTimer.toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "$minutes:${seconds.toString().padStart(2, '0')}"
    }

    private fun drawInventorySummary() {
        drawTextOnScreen(
            text = "아이템 ${inventory.itemCount()}개   태그 ${inventory.tagSummary()}",
            x = 10f,
            y = screenHeight - 72f,
            color = Color.WHITE,
            scale = 0.9f
        )
        drawTextOnScreen(
            text = "시너지 ${inventory.synergySummary()}",
            x = 10f,
            y = screenHeight - 94f,
            color = Color.YELLOW,
            scale = 0.9f
        )
        drawTextOnScreen(
            text = "시스템 ${inventory.systemSummary()}",
            x = 10f,
            y = screenHeight - 116f,
            color = Color.CYAN,
            scale = 0.86f
        )
        drawTextOnScreen(
            text = "액티브 ${inventory.activeAbilitySummary()}   X 대기 ${activeCooldownText()}   ${feverText()}",
            x = 10f,
            y = screenHeight - 138f,
            color = if (activeCooldownTimer <= 0f) Color.CYAN else Color.LIGHT_GRAY,
            scale = 0.86f
        )
    }

    private fun feverText(): String {
        if (!inventory.hasFeverSystem()) {
            return ""
        }

        return if (feverTimer > 0f) {
            "피버 ${feverTimer.toInt() + 1}초"
        } else {
            "피버 ${feverGauge.toInt()}%"
        }
    }

    private fun activeCooldownText(): String {
        return if (activeCooldownTimer <= 0f) {
            "준비됨"
        } else {
            "${activeCooldownTimer.toInt() + 1}초"
        }
    }

    private fun drawHealthBar() {
        val hpRatio = (hp / maxHp).coerceIn(0f, 1f)
        val barX = 10f
        val barY = screenHeight - 40f
        val barWidth = 300f
        val barHeight = 24f
        val borderSize = 3f

        batch.begin()

        batch.color = Color.WHITE
        batch.draw(tileTexture, barX - borderSize, barY - borderSize, barWidth + borderSize * 2, barHeight + borderSize * 2)

        batch.color = Color.DARK_GRAY
        batch.draw(tileTexture, barX, barY, barWidth, barHeight)

        batch.color = when {
            hpRatio > 0.55f -> Color.RED
            hpRatio > 0.25f -> Color.ORANGE
            else -> Color.MAGENTA
        }
        batch.draw(tileTexture, barX, barY, barWidth * hpRatio, barHeight)

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

    private fun drawBossHealthBar() {
        if (stageManager.currentStage.index < 5 || bossDefeated) {
            return
        }

        val hpRatio = (bossHp / bossMaxHp).coerceIn(0f, 1f)
        val barWidth = 360f
        val barHeight = 14f
        val barX = screenWidth / 2f - barWidth / 2f
        val barY = screenHeight - 84f

        batch.begin()
        batch.color = Color(0.08f, 0.04f, 0.04f, 0.88f)
        batch.draw(tileTexture, barX - 4f, barY - 4f, barWidth + 8f, barHeight + 8f)
        batch.color = Color.DARK_GRAY
        batch.draw(tileTexture, barX, barY, barWidth, barHeight)
        batch.color = Color(0.88f, 0.10f, 0.08f, 1f)
        batch.draw(tileTexture, barX, barY, barWidth * hpRatio, barHeight)
        batch.color = Color.WHITE
        batch.end()

        drawTextOnScreen(
            text = "보스 HP ${bossHp.toInt()} / ${bossMaxHp.toInt()}",
            x = barX + 104f,
            y = barY + 30f,
            color = Color.RED,
            scale = 0.92f
        )
    }

    private fun drawStageMessage() {
        if (stageManager.messageTimer <= 0f) {
            return
        }

        drawTextOnScreen(
            text = "스테이지 ${stageManager.currentStage.index}",
            x = screenWidth / 2f - 80f,
            y = screenHeight / 2f + 95f,
            color = Color.YELLOW,
            scale = 1.8f
        )
        drawTextOnScreen(
            text = stageManager.message,
            x = screenWidth / 2f - 85f,
            y = screenHeight / 2f + 60f,
            color = Color.WHITE,
            scale = 1.25f
        )
    }

    private fun drawShopScreen() {
        val cardWidth = 310f
        val cardHeight = 250f
        val gap = 28f
        val totalWidth = cardWidth * 3f + gap * 2f
        val startX = (screenWidth - totalWidth) / 2f
        val cardY = 172f

        batch.begin()
        batch.color = Color(0f, 0f, 0f, 0.68f)
        batch.draw(tileTexture, 0f, 0f, screenWidth, screenHeight)

        for (i in 0 until 3) {
            val x = startX + i * (cardWidth + gap)
            val selected = i == shopSystem.selectedIndex
            batch.color = if (selected) {
                Color(1f, 0.82f, 0.18f, 0.95f)
            } else {
                Color(0.12f, 0.14f, 0.20f, 0.92f)
            }
            batch.draw(tileTexture, x, cardY, cardWidth, cardHeight)

            if (selected) {
                batch.color = Color(0.08f, 0.09f, 0.13f, 0.96f)
                batch.draw(tileTexture, x + 5f, cardY + 5f, cardWidth - 10f, cardHeight - 10f)
            }
        }
        batch.color = Color.WHITE
        batch.end()

        drawTextOnScreen(
            text = "스테이지 상점",
            x = screenWidth / 2f - 95f,
            y = screenHeight - 112f,
            color = Color.YELLOW,
            scale = 1.8f
        )
        drawTextOnScreen(
            text = "←/→ 선택   ENTER 구매   SPACE 스킵   R 새로고침(${shopSystem.rerollCost(inventory.shopDiscountRate())} 코인, ${shopSystem.rerollInfo()})   L 예약",
            x = screenWidth / 2f - 380f,
            y = screenHeight - 145f,
            color = Color.WHITE,
            scale = 0.9f
        )

        if (shopSystem.offers.isEmpty()) {
            drawTextOnScreen(
                text = "구매 가능한 아이템이 없습니다. SPACE로 계속 진행.",
                x = screenWidth / 2f - 215f,
                y = screenHeight / 2f,
                color = Color.WHITE,
                scale = 1.1f
            )
        }

        for ((index, offer) in shopSystem.offers.withIndex()) {
            drawShopOffer(offer, index, startX + index * (cardWidth + gap), cardY, cardWidth)
        }

        val owned = inventory.ownedItems()
            .joinToString("  ") { (definition, stack) -> "${definition.name}x$stack" }
        val ownedText = if (owned.isBlank()) "아직 아이템 없음" else owned.take(108)
        drawTextOnScreen(
            text = "보유: $ownedText",
            x = 55f,
            y = 105f,
            color = Color.LIGHT_GRAY,
            scale = 0.86f
        )
        drawTextOnScreen(
            text = "시너지: ${inventory.synergySummary()}",
            x = 55f,
            y = 78f,
            color = Color.YELLOW,
            scale = 0.9f
        )
        drawTextOnScreen(
            text = "액티브: ${inventory.activeAbilitySummary()}",
            x = 55f,
            y = 52f,
            color = Color.CYAN,
            scale = 0.9f
        )
        drawTextOnScreen(
            text = "시스템: ${inventory.systemSummary()}",
            x = 55f,
            y = 28f,
            color = Color.WHITE,
            scale = 0.82f
        )
    }

    private fun drawShopOffer(offer: ItemOffer, index: Int, x: Float, y: Float, cardWidth: Float) {
        val definition = offer.definition
        val lineX = x + 22f
        var lineY = y + 218f

        drawTextOnScreen(
            text = "${index + 1}. ${definition.name}${if (shopSystem.isLocked(definition)) " [예약]" else ""}",
            x = lineX,
            y = lineY,
            color = rarityColor(definition.rarity),
            scale = 1.08f
        )

        lineY -= 30f
        drawTextOnScreen(
            text = "${definition.rarity.label}   ${offer.price} 코인   중첩 ${inventory.stackOf(definition)}/${inventory.maxStackOf(definition)}",
            x = lineX,
            y = lineY,
            color = Color.WHITE,
            scale = 0.84f
        )

        lineY -= 28f
        drawTextOnScreen(
            text = "태그: ${definition.tags.joinToString("/") { it.label }}",
            x = lineX,
            y = lineY,
            color = Color.YELLOW,
            scale = 0.82f
        )

        lineY -= 34f
        for (line in wrapText(definition.description, 30)) {
            drawTextOnScreen(
                text = line,
                x = lineX,
                y = lineY,
                color = Color.LIGHT_GRAY,
                scale = 0.82f
            )
            lineY -= 22f
        }

        val selectedText = if (shopSystem.selectedIndex == index) "< 선택됨 >" else ""
        drawTextOnScreen(
            text = selectedText,
            x = x + cardWidth / 2f - 65f,
            y = y + 28f,
            color = Color.YELLOW,
            scale = 0.85f
        )
    }

    private fun rarityColor(rarity: ItemRarity): Color {
        return when (rarity) {
            ItemRarity.COMMON -> Color.WHITE
            ItemRarity.RARE -> Color.CYAN
            ItemRarity.EPIC -> Color.MAGENTA
        }
    }

    private fun wrapText(text: String, maxChars: Int): List<String> {
        val lines = mutableListOf<String>()
        var current = ""

        for (word in text.split(" ")) {
            val next = if (current.isEmpty()) word else "$current $word"
            if (next.length > maxChars && current.isNotEmpty()) {
                lines.add(current)
                current = word
            } else {
                current = next
            }
        }

        if (current.isNotEmpty()) {
            lines.add(current)
        }

        return lines
    }

    private fun drawFeedback() {
        if (feedbackTimer <= 0f) {
            return
        }

        val color = when {
            feedbackMessage.startsWith("피격") -> Color.RED
            feedbackMessage.startsWith("코인") -> Color.ORANGE
            feedbackMessage.startsWith("구매") || feedbackMessage.contains("해금 완료") -> Color.GREEN
            else -> Color.YELLOW
        }

        drawTextOnScreen(
            text = feedbackMessage,
            x = screenWidth / 2f - 170f,
            y = screenHeight / 2f + 20f,
            color = color,
            scale = 1.2f
        )
    }

    private fun drawGameOverOverlay() {
        drawTextOnScreen(
            text = "게임 오버!",
            x = screenWidth / 2 - 85f,
            y = screenHeight / 2 + 25f,
            color = Color.WHITE,
            scale = 2f
        )
        drawTextOnScreen(
            text = "점수 ${scoreSystem.score} / 최고 콤보 ${scoreSystem.bestCombo}",
            x = screenWidth / 2 - 135f,
            y = screenHeight / 2 - 14f,
            color = Color.YELLOW,
            scale = 1f
        )
        drawTextOnScreen(
            text = "보석 +$lastGemReward / 총 ${metaProgression.gems()}",
            x = screenWidth / 2 - 95f,
            y = screenHeight / 2 - 42f,
            color = Color.CYAN,
            scale = 1f
        )
        drawTextOnScreen(
            text = "ESC로 종료",
            x = screenWidth / 2 - 55f,
            y = screenHeight / 2 - 74f,
            color = Color.WHITE,
            scale = 1f
        )
        if ((blinkTimer % 1.2f) < 0.6f) {
            drawTextOnScreen(
                text = "R로 다시 시작",
                x = screenWidth / 2 - 65f,
                y = screenHeight / 2 - 102f,
                color = Color.YELLOW,
                scale = 1f
            )
        }
    }

    override fun dispose() {
        super.dispose()
        tileTexture.dispose()
        backgroundTexture.dispose()
        groundTexture.dispose()
        obstacleCrateSmallTexture.dispose()
        obstacleCrateTallTexture.dispose()
        obstacleBarrelTexture.dispose()
        obstacleSawTexture.dispose()
        obstacleBombTexture.dispose()
        obstacleRockTexture.dispose()
        coinTexture.dispose()
        warningTexture.dispose()
        bossTexture.dispose()
        pitTexture.dispose()
        springTexture.dispose()
        boostTexture.dispose()
        mudTexture.dispose()
        spikesTexture.dispose()
        windTexture.dispose()
    }
}
