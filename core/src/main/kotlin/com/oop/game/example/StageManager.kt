package com.oop.game.example

data class StageConfig(
    val index: Int,
    val name: String,
    val startDistance: Float,
    val scrollSpeed: Float,
    val obstacleSpeed: Float,
    val spawnDelay: Float
)

class StageManager {
    private val stages = listOf(
        StageConfig(
            index = 1,
            name = "산들 들판",
            startDistance = 0f,
            scrollSpeed = 200f,
            obstacleSpeed = 360f,
            spawnDelay = 2.45f
        ),
        StageConfig(
            index = 2,
            name = "상자 길",
            startDistance = 7200f,
            scrollSpeed = 200f,
            obstacleSpeed = 430f,
            spawnDelay = 2.25f
        ),
        StageConfig(
            index = 3,
            name = "바람 협곡",
            startDistance = 14400f,
            scrollSpeed = 200f,
            obstacleSpeed = 490f,
            spawnDelay = 2.05f
        ),
        StageConfig(
            index = 4,
            name = "위험 공장",
            startDistance = 21600f,
            scrollSpeed = 200f,
            obstacleSpeed = 550f,
            spawnDelay = 1.90f
        ),
        StageConfig(
            index = 5,
            name = "상자 골렘 보스전",
            startDistance = 28800f,
            scrollSpeed = 200f,
            obstacleSpeed = 620f,
            spawnDelay = 1.75f
        )
    )

    var currentStage: StageConfig = stages.first()
        private set
    var message: String = currentStage.name
        private set
    var messageTimer: Float = 0f
        private set
    var highestStageReached: Int = currentStage.index
        private set

    private val shopDistances = listOf(7200f, 14400f, 21600f, 28800f)
    private var nextShopIndex = 0

    fun reset() {
        currentStage = stages.first()
        message = currentStage.name
        messageTimer = 2.2f
        highestStageReached = currentStage.index
        nextShopIndex = 0
    }

    fun update(distance: Float, delta: Float) {
        if (messageTimer > 0f) {
            messageTimer = (messageTimer - delta).coerceAtLeast(0f)
        }

        val nextStage = stages.last { distance >= it.startDistance }
        if (nextStage.index != currentStage.index) {
            currentStage = nextStage
            highestStageReached = maxOf(highestStageReached, currentStage.index)
            message = currentStage.name
            messageTimer = 2.4f
        }
    }

    fun shouldOpenShop(distance: Float): Boolean {
        val nextShopDistance = shopDistances.getOrNull(nextShopIndex) ?: return false
        return distance >= nextShopDistance
    }

    fun currentShopDepth(): Int {
        return nextShopIndex + 1
    }

    fun consumeShopGate() {
        nextShopIndex++
    }
}
