package com.oop.game.example

import java.util.Locale

class ScoreSystem {
    var score: Int = 0
        private set
    var combo: Int = 0
        private set
    var bestCombo: Int = 0
        private set
    var multiplier: Float = 1f
        private set

    private var scoreCarry = 0f

    fun reset() {
        score = 0
        combo = 0
        bestCombo = 0
        multiplier = 1f
        scoreCarry = 0f
    }

    fun update(delta: Float, baseScoreMultiplier: Float) {
        addScore(10f * baseScoreMultiplier * multiplier * delta)
    }

    fun rewardNearMiss(
        scoreBonusMultiplier: Float,
        multiplierGrowthBonus: Float,
        maxMultiplier: Float
    ): Int {
        combo++
        bestCombo = maxOf(bestCombo, combo)
        multiplier = (1f + combo * (0.1f + multiplierGrowthBonus)).coerceAtMost(maxMultiplier)

        val before = score
        addScore(20f * scoreBonusMultiplier * multiplier)
        return score - before
    }

    fun resetCombo(
        preservedCombo: Int = 0,
        multiplierGrowthBonus: Float = 0f,
        maxMultiplier: Float = 5f
    ) {
        combo = preservedCombo.coerceAtLeast(0)
        multiplier = (1f + combo * (0.1f + multiplierGrowthBonus)).coerceAtMost(maxMultiplier)
        scoreCarry = 0f
    }

    fun addBonusScore(value: Int) {
        if (value > 0) {
            score += value
        }
    }

    fun multiplierText(): String {
        return String.format(Locale.US, "x%.1f", multiplier)
    }

    private fun addScore(value: Float) {
        scoreCarry += value

        val gainedScore = scoreCarry.toInt()
        if (gainedScore > 0) {
            score += gainedScore
            scoreCarry -= gainedScore
        }
    }
}
