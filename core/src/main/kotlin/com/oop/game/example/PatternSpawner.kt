package com.oop.game.example

enum class ObstacleType {
    SMALL,
    BIG,
    FLYING,
    BARREL,
    SAW,
    BOMB,
    FALLING_ROCK
}

enum class GimmickType {
    PIT,
    SPRING,
    BOOST,
    MUD,
    SPIKES,
    WIND
}

data class ObstacleSpawn(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val type: ObstacleType
)

data class CoinSpawn(
    val x: Float,
    val y: Float,
    val value: Int = 1
)

data class GimmickSpawn(
    val x: Float,
    val width: Float,
    val type: GimmickType
)

class PatternSpawner(
    private val groundY: Float
) {
    private data class PatternItem(
        val type: ObstacleType,
        val offsetX: Float
    )

    private data class GimmickItem(
        val type: GimmickType,
        val offsetX: Float,
        val width: Float
    )

    private val patternIndexes = mutableMapOf<Int, Int>()
    private val coinTrailIndexes = mutableMapOf<Int, Int>()
    private val gimmickIndexes = mutableMapOf<Int, Int>()

    fun reset() {
        patternIndexes.clear()
        coinTrailIndexes.clear()
        gimmickIndexes.clear()
    }

    fun nextPattern(stage: StageConfig, startX: Float): List<ObstacleSpawn> {
        val patterns = patternsFor(stage.index)
        val currentIndex = patternIndexes.getOrDefault(stage.index, 0)
        patternIndexes[stage.index] = (currentIndex + 1) % patterns.size

        return patterns[currentIndex].map { item ->
            createSpawn(item.type, startX + item.offsetX)
        }
    }

    fun nextCoinTrail(stage: StageConfig, startX: Float): List<CoinSpawn> {
        val trails = coinTrailsFor(stage.index)
        val currentIndex = coinTrailIndexes.getOrDefault(stage.index, 0)
        coinTrailIndexes[stage.index] = (currentIndex + 1) % trails.size

        return trails[currentIndex](startX)
    }

    fun nextGimmicks(stage: StageConfig, startX: Float): List<GimmickSpawn> {
        val patterns = gimmicksFor(stage.index)
        val currentIndex = gimmickIndexes.getOrDefault(stage.index, 0)
        gimmickIndexes[stage.index] = (currentIndex + 1) % patterns.size

        return patterns[currentIndex].map { item ->
            GimmickSpawn(
                x = startX + item.offsetX,
                width = item.width,
                type = item.type
            )
        }
    }

    private fun patternsFor(stageIndex: Int): List<List<PatternItem>> {
        return when (stageIndex) {
            1 -> listOf(
                listOf(PatternItem(ObstacleType.SMALL, 0f)),
                listOf(PatternItem(ObstacleType.BARREL, 0f)),
                listOf(PatternItem(ObstacleType.FLYING, 0f)),
                listOf(
                    PatternItem(ObstacleType.SMALL, 0f),
                    PatternItem(ObstacleType.BARREL, 640f)
                ),
                listOf(PatternItem(ObstacleType.BIG, 0f))
            )

            2 -> listOf(
                listOf(
                    PatternItem(ObstacleType.SMALL, 0f),
                    PatternItem(ObstacleType.BARREL, 560f)
                ),
                listOf(
                    PatternItem(ObstacleType.FLYING, 0f),
                    PatternItem(ObstacleType.SMALL, 560f)
                ),
                listOf(
                    PatternItem(ObstacleType.BIG, 0f),
                    PatternItem(ObstacleType.SAW, 700f)
                ),
                listOf(
                    PatternItem(ObstacleType.BARREL, 0f),
                    PatternItem(ObstacleType.BOMB, 700f)
                )
            )

            3 -> listOf(
                listOf(
                    PatternItem(ObstacleType.SMALL, 0f),
                    PatternItem(ObstacleType.BOMB, 650f),
                    PatternItem(ObstacleType.SAW, 1280f)
                ),
                listOf(
                    PatternItem(ObstacleType.FALLING_ROCK, 0f),
                    PatternItem(ObstacleType.BARREL, 640f),
                    PatternItem(ObstacleType.FLYING, 1260f)
                ),
                listOf(
                    PatternItem(ObstacleType.BIG, 0f),
                    PatternItem(ObstacleType.SAW, 720f),
                    PatternItem(ObstacleType.BOMB, 1360f)
                ),
                listOf(
                    PatternItem(ObstacleType.BARREL, 0f),
                    PatternItem(ObstacleType.FLYING, 620f),
                    PatternItem(ObstacleType.FALLING_ROCK, 1320f)
                )
            )

            4 -> listOf(
                listOf(
                    PatternItem(ObstacleType.SAW, 0f),
                    PatternItem(ObstacleType.BARREL, 620f),
                    PatternItem(ObstacleType.FALLING_ROCK, 1260f)
                ),
                listOf(
                    PatternItem(ObstacleType.BIG, 0f),
                    PatternItem(ObstacleType.BOMB, 740f),
                    PatternItem(ObstacleType.SAW, 1400f)
                ),
                listOf(
                    PatternItem(ObstacleType.FALLING_ROCK, 0f),
                    PatternItem(ObstacleType.SMALL, 590f),
                    PatternItem(ObstacleType.BIG, 1260f)
                ),
                listOf(
                    PatternItem(ObstacleType.BARREL, 0f),
                    PatternItem(ObstacleType.SAW, 620f),
                    PatternItem(ObstacleType.BOMB, 1260f)
                )
            )

            else -> listOf(
                listOf(
                    PatternItem(ObstacleType.FALLING_ROCK, 0f),
                    PatternItem(ObstacleType.BARREL, 600f),
                    PatternItem(ObstacleType.SAW, 1220f),
                    PatternItem(ObstacleType.BOMB, 1880f)
                ),
                listOf(
                    PatternItem(ObstacleType.BIG, 0f),
                    PatternItem(ObstacleType.FALLING_ROCK, 680f),
                    PatternItem(ObstacleType.BARREL, 1340f),
                    PatternItem(ObstacleType.SAW, 1980f)
                ),
                listOf(
                    PatternItem(ObstacleType.SAW, 0f),
                    PatternItem(ObstacleType.BOMB, 620f),
                    PatternItem(ObstacleType.FALLING_ROCK, 1240f),
                    PatternItem(ObstacleType.BIG, 1900f)
                ),
                listOf(
                    PatternItem(ObstacleType.BARREL, 0f),
                    PatternItem(ObstacleType.BOMB, 650f),
                    PatternItem(ObstacleType.SAW, 1320f)
                )
            )
        }
    }

    fun createSpawn(type: ObstacleType, x: Float): ObstacleSpawn {
        val width = when (type) {
            ObstacleType.SMALL -> 82f
            ObstacleType.BIG -> 96f
            ObstacleType.FLYING -> 76f
            ObstacleType.BARREL -> 82f
            ObstacleType.SAW -> 76f
            ObstacleType.BOMB -> 76f
            ObstacleType.FALLING_ROCK -> 92f
        }

        val height = when (type) {
            ObstacleType.SMALL -> 86f
            ObstacleType.BIG -> 146f
            ObstacleType.FLYING -> 76f
            ObstacleType.BARREL -> 82f
            ObstacleType.SAW -> 76f
            ObstacleType.BOMB -> 86f
            ObstacleType.FALLING_ROCK -> 92f
        }

        val y = when (type) {
            ObstacleType.SMALL -> groundY
            ObstacleType.BIG -> groundY
            ObstacleType.FLYING -> groundY + 118f
            ObstacleType.BARREL -> groundY
            ObstacleType.SAW -> groundY + 132f
            ObstacleType.BOMB -> groundY + 22f
            ObstacleType.FALLING_ROCK -> groundY + 330f
        }

        return ObstacleSpawn(
            x = x,
            y = y,
            width = width,
            height = height,
            type = type
        )
    }

    private fun coinTrailsFor(stageIndex: Int): List<(Float) -> List<CoinSpawn>> {
        return when (stageIndex) {
            1 -> listOf(
                { startX -> coinLine(startX, 170f, groundY + 132f, 6, 58f) },
                { startX -> coinArc(startX, 140f, groundY + 154f, 7, 54f, 72f) },
                { startX -> coinLine(startX, 260f, groundY + 230f, 5, 62f, value = 2) }
            )

            2 -> listOf(
                { startX -> coinLine(startX, 180f, groundY + 132f, 7, 55f) },
                { startX -> coinArc(startX, 170f, groundY + 160f, 8, 52f, 96f) },
                { startX -> coinZigzag(startX, 200f, groundY + 145f, groundY + 265f, 8, 56f) }
            )

            3 -> listOf(
                { startX -> coinArc(startX, 160f, groundY + 170f, 8, 56f, 130f, value = 2) },
                { startX -> coinZigzag(startX, 180f, groundY + 130f, groundY + 300f, 9, 52f) },
                { startX -> coinLine(startX, 320f, groundY + 270f, 6, 62f, value = 2) }
            )

            4 -> listOf(
                { startX -> coinZigzag(startX, 150f, groundY + 125f, groundY + 315f, 10, 50f) },
                { startX -> coinArc(startX, 180f, groundY + 150f, 9, 52f, 155f, value = 2) },
                { startX -> coinLine(startX, 260f, groundY + 335f, 5, 64f, value = 3) }
            )

            else -> listOf(
                { startX -> coinZigzag(startX, 140f, groundY + 120f, groundY + 335f, 11, 48f, value = 2) },
                { startX -> coinArc(startX, 160f, groundY + 150f, 10, 50f, 180f, value = 2) },
                { startX -> coinLine(startX, 340f, groundY + 360f, 5, 70f, value = 3) }
            )
        }
    }

    private fun coinLine(
        startX: Float,
        offsetX: Float,
        y: Float,
        count: Int,
        spacing: Float,
        value: Int = 1
    ): List<CoinSpawn> {
        return List(count) { index ->
            CoinSpawn(
                x = startX + offsetX + spacing * index,
                y = y,
                value = value
            )
        }
    }

    private fun coinArc(
        startX: Float,
        offsetX: Float,
        baseY: Float,
        count: Int,
        spacing: Float,
        height: Float,
        value: Int = 1
    ): List<CoinSpawn> {
        val middle = (count - 1) / 2f
        return List(count) { index ->
            val distanceFromMiddle = kotlin.math.abs(index - middle) / middle.coerceAtLeast(1f)
            val arcY = baseY + height * (1f - distanceFromMiddle * distanceFromMiddle)
            CoinSpawn(
                x = startX + offsetX + spacing * index,
                y = arcY,
                value = value
            )
        }
    }

    private fun coinZigzag(
        startX: Float,
        offsetX: Float,
        lowY: Float,
        highY: Float,
        count: Int,
        spacing: Float,
        value: Int = 1
    ): List<CoinSpawn> {
        return List(count) { index ->
            CoinSpawn(
                x = startX + offsetX + spacing * index,
                y = if (index % 2 == 0) lowY else highY,
                value = value
            )
        }
    }

    private fun gimmicksFor(stageIndex: Int): List<List<GimmickItem>> {
        return when (stageIndex) {
            1 -> listOf(
                emptyList(),
                listOf(GimmickItem(GimmickType.SPRING, 360f, 82f)),
                listOf(GimmickItem(GimmickType.PIT, 520f, 138f)),
                listOf(GimmickItem(GimmickType.BOOST, 420f, 100f))
            )

            2 -> listOf(
                listOf(GimmickItem(GimmickType.PIT, 430f, 160f)),
                listOf(GimmickItem(GimmickType.MUD, 300f, 210f)),
                listOf(
                    GimmickItem(GimmickType.SPRING, 340f, 82f),
                    GimmickItem(GimmickType.PIT, 780f, 170f)
                ),
                listOf(GimmickItem(GimmickType.SPIKES, 520f, 135f))
            )

            3 -> listOf(
                listOf(
                    GimmickItem(GimmickType.PIT, 360f, 180f),
                    GimmickItem(GimmickType.SPRING, 840f, 82f)
                ),
                listOf(GimmickItem(GimmickType.WIND, 520f, 110f)),
                listOf(
                    GimmickItem(GimmickType.MUD, 260f, 220f),
                    GimmickItem(GimmickType.SPIKES, 760f, 150f)
                ),
                listOf(GimmickItem(GimmickType.BOOST, 460f, 110f))
            )

            4 -> listOf(
                listOf(
                    GimmickItem(GimmickType.PIT, 320f, 190f),
                    GimmickItem(GimmickType.PIT, 880f, 175f)
                ),
                listOf(
                    GimmickItem(GimmickType.WIND, 420f, 120f),
                    GimmickItem(GimmickType.SPIKES, 900f, 160f)
                ),
                listOf(GimmickItem(GimmickType.MUD, 260f, 300f)),
                listOf(
                    GimmickItem(GimmickType.SPRING, 300f, 82f),
                    GimmickItem(GimmickType.BOOST, 760f, 120f)
                )
            )

            else -> listOf(
                listOf(
                    GimmickItem(GimmickType.PIT, 300f, 210f),
                    GimmickItem(GimmickType.WIND, 860f, 130f)
                ),
                listOf(
                    GimmickItem(GimmickType.SPIKES, 360f, 170f),
                    GimmickItem(GimmickType.PIT, 980f, 200f)
                ),
                listOf(
                    GimmickItem(GimmickType.MUD, 260f, 250f),
                    GimmickItem(GimmickType.SPRING, 780f, 82f)
                ),
                listOf(
                    GimmickItem(GimmickType.BOOST, 340f, 130f),
                    GimmickItem(GimmickType.PIT, 980f, 220f)
                )
            )
        }
    }
}
