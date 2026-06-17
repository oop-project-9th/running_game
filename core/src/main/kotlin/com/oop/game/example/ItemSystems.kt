package com.oop.game.example

import com.badlogic.gdx.Gdx
import kotlin.math.roundToInt

enum class ItemTag(val label: String) {
    RISK("위험"),
    GOLD("골드"),
    SHIELD("방어"),
    COMBO("콤보"),
    HEAL("회복"),
    AGILITY("민첩")
}

enum class ItemRarity(val label: String, val basePrice: Int) {
    COMMON("일반", 20),
    RARE("희귀", 35),
    EPIC("에픽", 55)
}

enum class ItemEffect {
    NEAR_MISS_COIN,
    NEAR_MISS_SCORE,
    DAMAGE_REDUCTION,
    STAGE_HEAL,
    COMBO_GROWTH,
    NEAR_MISS_RANGE,
    SHOP_DISCOUNT,
    MAX_MULTIPLIER,
    COMBO_KEEP,
    BASE_SCORE,
    FIVE_COMBO_HEAL,
    EXTRA_JUMP,
    QUICK_DROP,
    TIME_SLOW,
    ACTIVE_SHIELD,
    OBSTACLE_BREAK,
    AIR_DASH,
    NEAR_MISS_OVERDRIVE,
    COIN_PICKUP_VALUE,
    COIN_MAGNET,
    ACTIVE_COOLDOWN_REFUND,
    COMBO_COIN,
    LOW_HP_REWARD,
    AIR_COIN_BONUS,
    FREE_REROLL,
    COIN_STREAK_SYSTEM,
    SLIDE_TRICK_SYSTEM,
    BOUNTY_SYSTEM,
    FEVER_SYSTEM,
    EMERGENCY_POTION_SYSTEM,
    LUCKY_SLOT_SYSTEM,
    SHOP_LOCK_SYSTEM,
    COIN_SHIELD_SYSTEM,
    COIN_INTEREST_SYSTEM,
    NEAR_MISS_ROULETTE_SYSTEM
}

data class ItemDefinition(
    val id: String,
    val name: String,
    val rarity: ItemRarity,
    val tags: Set<ItemTag>,
    val effect: ItemEffect,
    val power: Float,
    val defaultUnlocked: Boolean,
    val description: String
)

object ItemCatalog {
    val all = listOf(
        ItemDefinition(
            id = "lucky_pouch",
            name = "행운 주머니",
            rarity = ItemRarity.COMMON,
            tags = setOf(ItemTag.GOLD),
            effect = ItemEffect.NEAR_MISS_COIN,
            power = 0.25f,
            defaultUnlocked = true,
            description = "니어미스 코인 획득량 +25%."
        ),
        ItemDefinition(
            id = "soft_boots",
            name = "부드러운 장화",
            rarity = ItemRarity.COMMON,
            tags = setOf(ItemTag.AGILITY),
            effect = ItemEffect.NEAR_MISS_RANGE,
            power = 8f,
            defaultUnlocked = true,
            description = "니어미스 판정 범위 +8."
        ),
        ItemDefinition(
            id = "heart_seed",
            name = "심장 씨앗",
            rarity = ItemRarity.COMMON,
            tags = setOf(ItemTag.HEAL),
            effect = ItemEffect.STAGE_HEAL,
            power = 18f,
            defaultUnlocked = true,
            description = "스테이지 클리어 시 체력 18 회복."
        ),
        ItemDefinition(
            id = "sharp_feather",
            name = "날카로운 깃털",
            rarity = ItemRarity.COMMON,
            tags = setOf(ItemTag.RISK),
            effect = ItemEffect.NEAR_MISS_SCORE,
            power = 0.20f,
            defaultUnlocked = true,
            description = "니어미스 점수 +20%."
        ),
        ItemDefinition(
            id = "combo_thread",
            name = "콤보 실타래",
            rarity = ItemRarity.COMMON,
            tags = setOf(ItemTag.COMBO),
            effect = ItemEffect.COMBO_GROWTH,
            power = 0.02f,
            defaultUnlocked = true,
            description = "콤보당 배율 증가량 +0.02."
        ),
        ItemDefinition(
            id = "wind_socks",
            name = "바람 양말",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.AGILITY, ItemTag.COMBO),
            effect = ItemEffect.NEAR_MISS_RANGE,
            power = 6f,
            defaultUnlocked = true,
            description = "니어미스 판정 범위 +6."
        ),
        ItemDefinition(
            id = "golden_coupon",
            name = "황금 쿠폰",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.GOLD),
            effect = ItemEffect.SHOP_DISCOUNT,
            power = 0.10f,
            defaultUnlocked = true,
            description = "상점 가격 10% 할인."
        ),
        ItemDefinition(
            id = "brave_badge",
            name = "용기 배지",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.RISK, ItemTag.SHIELD),
            effect = ItemEffect.MAX_MULTIPLIER,
            power = 0.4f,
            defaultUnlocked = true,
            description = "최대 배율 +0.4."
        ),
        ItemDefinition(
            id = "recovery_jam",
            name = "회복 잼",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.HEAL),
            effect = ItemEffect.FIVE_COMBO_HEAL,
            power = 8f,
            defaultUnlocked = true,
            description = "콤보가 5의 배수일 때 체력 8 회복."
        ),
        ItemDefinition(
            id = "guard_jelly",
            name = "방어 젤리",
            rarity = ItemRarity.COMMON,
            tags = setOf(ItemTag.SHIELD),
            effect = ItemEffect.DAMAGE_REDUCTION,
            power = 0.10f,
            defaultUnlocked = true,
            description = "피격 피해량 10% 감소."
        ),
        ItemDefinition(
            id = "magnet_bracelet",
            name = "자석 팔찌",
            rarity = ItemRarity.COMMON,
            tags = setOf(ItemTag.GOLD, ItemTag.AGILITY),
            effect = ItemEffect.COIN_MAGNET,
            power = 95f,
            defaultUnlocked = true,
            description = "맵 코인을 더 먼 거리에서 끌어당김."
        ),
        ItemDefinition(
            id = "gilded_lens",
            name = "도금 렌즈",
            rarity = ItemRarity.COMMON,
            tags = setOf(ItemTag.GOLD),
            effect = ItemEffect.COIN_PICKUP_VALUE,
            power = 0.30f,
            defaultUnlocked = true,
            description = "맵 코인 획득량 +30%."
        ),
        ItemDefinition(
            id = "air_wallet",
            name = "공중 지갑",
            rarity = ItemRarity.COMMON,
            tags = setOf(ItemTag.AGILITY, ItemTag.GOLD),
            effect = ItemEffect.AIR_COIN_BONUS,
            power = 1f,
            defaultUnlocked = true,
            description = "공중에서 먹은 맵 코인마다 코인 +1."
        ),
        ItemDefinition(
            id = "combo_safe",
            name = "콤보 금고",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.COMBO, ItemTag.GOLD),
            effect = ItemEffect.COMBO_COIN,
            power = 5f,
            defaultUnlocked = true,
            description = "10콤보마다 추가 코인 +5."
        ),
        ItemDefinition(
            id = "charge_battery",
            name = "축전 배터리",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.COMBO, ItemTag.AGILITY),
            effect = ItemEffect.ACTIVE_COOLDOWN_REFUND,
            power = 0.75f,
            defaultUnlocked = true,
            description = "니어미스마다 액티브 대기시간 0.75초 감소."
        ),
        ItemDefinition(
            id = "shop_dice",
            name = "상점 주사위",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.GOLD, ItemTag.COMBO),
            effect = ItemEffect.FREE_REROLL,
            power = 1f,
            defaultUnlocked = true,
            description = "상점마다 무료 새로고침 +1."
        ),
        ItemDefinition(
            id = "coin_streak_engine",
            name = "코인 연쇄 엔진",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.GOLD, ItemTag.COMBO),
            effect = ItemEffect.COIN_STREAK_SYSTEM,
            power = 1f,
            defaultUnlocked = true,
            description = "맵 코인을 빠르게 이어 먹으면 코인 콤보가 생김."
        ),
        ItemDefinition(
            id = "slide_scanner",
            name = "슬라이드 스캐너",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.AGILITY, ItemTag.RISK),
            effect = ItemEffect.SLIDE_TRICK_SYSTEM,
            power = 1f,
            defaultUnlocked = true,
            description = "슬라이드로 공중 장애물 밑을 통과하면 보상."
        ),
        ItemDefinition(
            id = "bounty_stamp",
            name = "현상금 도장",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.RISK, ItemTag.GOLD),
            effect = ItemEffect.BOUNTY_SYSTEM,
            power = 1f,
            defaultUnlocked = true,
            description = "일부 장애물에 현상금 표식이 생김."
        ),
        ItemDefinition(
            id = "fever_clock",
            name = "피버 시계",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.COMBO, ItemTag.AGILITY),
            effect = ItemEffect.FEVER_SYSTEM,
            power = 1f,
            defaultUnlocked = true,
            description = "니어미스와 코인으로 피버 게이지를 충전."
        ),
        ItemDefinition(
            id = "potion_belt",
            name = "긴급 물약 벨트",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.HEAL, ItemTag.SHIELD),
            effect = ItemEffect.EMERGENCY_POTION_SYSTEM,
            power = 1f,
            defaultUnlocked = true,
            description = "체력이 낮아지면 한 번 자동 회복."
        ),
        ItemDefinition(
            id = "glass_crown",
            name = "유리 왕관",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.RISK, ItemTag.COMBO),
            effect = ItemEffect.MAX_MULTIPLIER,
            power = 1.0f,
            defaultUnlocked = false,
            description = "최대 배율 +1.0."
        ),
        ItemDefinition(
            id = "royal_contract",
            name = "왕실 계약서",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.GOLD, ItemTag.RISK),
            effect = ItemEffect.NEAR_MISS_COIN,
            power = 0.50f,
            defaultUnlocked = false,
            description = "니어미스 코인 획득량 +50%."
        ),
        ItemDefinition(
            id = "mirror_charm",
            name = "거울 부적",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.COMBO, ItemTag.AGILITY),
            effect = ItemEffect.COMBO_KEEP,
            power = 1f,
            defaultUnlocked = false,
            description = "스테이지마다 1회 피격 시 콤보 절반 보존."
        ),
        ItemDefinition(
            id = "phoenix_crumb",
            name = "불사조 부스러기",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.HEAL, ItemTag.RISK),
            effect = ItemEffect.STAGE_HEAL,
            power = 45f,
            defaultUnlocked = false,
            description = "스테이지 클리어 시 체력 45 회복."
        ),
        ItemDefinition(
            id = "turbo_laces",
            name = "터보 끈",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.AGILITY),
            effect = ItemEffect.BASE_SCORE,
            power = 0.25f,
            defaultUnlocked = false,
            description = "기본 생존 점수 +25%."
        ),
        ItemDefinition(
            id = "crowned_shield",
            name = "왕관 방패",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.SHIELD, ItemTag.GOLD),
            effect = ItemEffect.DAMAGE_REDUCTION,
            power = 0.20f,
            defaultUnlocked = false,
            description = "피격 피해량 20% 감소."
        ),
        ItemDefinition(
            id = "risk_contract",
            name = "위험 계약서",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.RISK, ItemTag.GOLD),
            effect = ItemEffect.LOW_HP_REWARD,
            power = 0.45f,
            defaultUnlocked = false,
            description = "체력 40% 이하일 때 점수와 코인 보상 +45%."
        ),
        ItemDefinition(
            id = "jackpot_core",
            name = "잭팟 코어",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.GOLD, ItemTag.RISK),
            effect = ItemEffect.COIN_PICKUP_VALUE,
            power = 0.85f,
            defaultUnlocked = false,
            description = "맵 코인 획득량 +85%."
        ),
        ItemDefinition(
            id = "lucky_slot_machine",
            name = "행운 슬롯머신",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.GOLD, ItemTag.RISK),
            effect = ItemEffect.LUCKY_SLOT_SYSTEM,
            power = 1f,
            defaultUnlocked = false,
            description = "일정 횟수 코인을 먹을 때마다 무작위 정산."
        ),
        ItemDefinition(
            id = "shop_bookmark",
            name = "상점 예약권",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.GOLD, ItemTag.COMBO),
            effect = ItemEffect.SHOP_LOCK_SYSTEM,
            power = 1f,
            defaultUnlocked = false,
            description = "상점에서 L로 아이템 하나를 리롤 보호."
        ),
        ItemDefinition(
            id = "coin_shield",
            name = "동전 방패",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.SHIELD, ItemTag.GOLD),
            effect = ItemEffect.COIN_SHIELD_SYSTEM,
            power = 1f,
            defaultUnlocked = false,
            description = "피격 시 코인을 지불하고 피해를 무효화."
        ),
        ItemDefinition(
            id = "interest_piggy_bank",
            name = "이자 저금통",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.GOLD),
            effect = ItemEffect.COIN_INTEREST_SYSTEM,
            power = 1f,
            defaultUnlocked = false,
            description = "상점 도착 시 보유 코인에 이자가 붙음."
        ),
        ItemDefinition(
            id = "near_miss_roulette",
            name = "니어미스 룰렛",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.RISK, ItemTag.COMBO),
            effect = ItemEffect.NEAR_MISS_ROULETTE_SYSTEM,
            power = 1f,
            defaultUnlocked = false,
            description = "니어미스 4회마다 무작위 보상 발동."
        ),
        ItemDefinition(
            id = "third_jump_core",
            name = "삼단 점프 코어",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.AGILITY, ItemTag.COMBO),
            effect = ItemEffect.EXTRA_JUMP,
            power = 1f,
            defaultUnlocked = true,
            description = "점프 가능 횟수 +1. SPACE로 3단 점프 가능."
        ),
        ItemDefinition(
            id = "gravity_anchor",
            name = "중력 앵커",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.AGILITY, ItemTag.RISK),
            effect = ItemEffect.QUICK_DROP,
            power = 1f,
            defaultUnlocked = true,
            description = "공중에서 X를 누르면 즉시 착지."
        ),
        ItemDefinition(
            id = "time_jelly",
            name = "시간 젤리",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.COMBO, ItemTag.SHIELD),
            effect = ItemEffect.TIME_SLOW,
            power = 1f,
            defaultUnlocked = true,
            description = "X로 잠시 장애물 속도 45% 감소."
        ),
        ItemDefinition(
            id = "shield_capsule",
            name = "보호막 캡슐",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.SHIELD),
            effect = ItemEffect.ACTIVE_SHIELD,
            power = 1f,
            defaultUnlocked = true,
            description = "X로 3초 동안 피격 1회 무시."
        ),
        ItemDefinition(
            id = "crate_firework",
            name = "상자 폭죽",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.RISK, ItemTag.GOLD),
            effect = ItemEffect.OBSTACLE_BREAK,
            power = 1f,
            defaultUnlocked = false,
            description = "X로 가장 가까운 장애물 제거."
        ),
        ItemDefinition(
            id = "dash_feather",
            name = "질주 깃털",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.AGILITY, ItemTag.RISK),
            effect = ItemEffect.AIR_DASH,
            power = 150f,
            defaultUnlocked = false,
            description = "X로 짧게 앞으로 대시."
        ),
        ItemDefinition(
            id = "overdrive_badge",
            name = "오버드라이브 배지",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.COMBO, ItemTag.GOLD),
            effect = ItemEffect.NEAR_MISS_OVERDRIVE,
            power = 1f,
            defaultUnlocked = false,
            description = "X로 5초간 니어미스 보상이 2배."
        )
    )

    private val byId = all.associateBy { it.id }

    fun find(id: String): ItemDefinition? {
        return byId[id]
    }

    fun defaultUnlockedIds(): Set<String> {
        return all.filter { it.defaultUnlocked }.map { it.id }.toSet()
    }

    fun lockedDefinitions(): List<ItemDefinition> {
        return all.filter { !it.defaultUnlocked }
    }

    fun unlockedDefinitions(unlockedIds: Set<String>): List<ItemDefinition> {
        return all.filter { it.defaultUnlocked || it.id in unlockedIds }
    }
}

class InventorySystem {
    private val stacks = mutableMapOf<String, Int>()
    private var stageShieldUsed = false
    private var stageComboSaveUsed = false

    fun resetRun() {
        stacks.clear()
        resetStage()
    }

    fun resetStage() {
        stageShieldUsed = false
        stageComboSaveUsed = false
    }

    fun canAdd(definition: ItemDefinition): Boolean {
        return stackOf(definition) < maxStackOf(definition)
    }

    fun maxStackOf(definition: ItemDefinition): Int {
        return when (definition.effect) {
            ItemEffect.EXTRA_JUMP,
            ItemEffect.QUICK_DROP,
            ItemEffect.TIME_SLOW,
            ItemEffect.ACTIVE_SHIELD,
            ItemEffect.OBSTACLE_BREAK,
            ItemEffect.AIR_DASH,
            ItemEffect.NEAR_MISS_OVERDRIVE,
            ItemEffect.COIN_STREAK_SYSTEM,
            ItemEffect.SLIDE_TRICK_SYSTEM,
            ItemEffect.BOUNTY_SYSTEM,
            ItemEffect.FEVER_SYSTEM,
            ItemEffect.EMERGENCY_POTION_SYSTEM,
            ItemEffect.LUCKY_SLOT_SYSTEM,
            ItemEffect.SHOP_LOCK_SYSTEM,
            ItemEffect.COIN_SHIELD_SYSTEM,
            ItemEffect.COIN_INTEREST_SYSTEM,
            ItemEffect.NEAR_MISS_ROULETTE_SYSTEM -> 1
            else -> 3
        }
    }

    fun addItem(definition: ItemDefinition): Boolean {
        if (!canAdd(definition)) {
            return false
        }

        stacks[definition.id] = stackOf(definition) + 1
        return true
    }

    fun stackOf(definition: ItemDefinition): Int {
        return stacks[definition.id] ?: 0
    }

    fun ownedItems(): List<Pair<ItemDefinition, Int>> {
        return stacks.entries.mapNotNull { (id, stack) ->
            ItemCatalog.find(id)?.let { it to stack }
        }
    }

    fun itemCount(): Int {
        return stacks.values.sum()
    }

    fun tagSummary(): String {
        val summary = ItemTag.values()
            .mapNotNull { tag ->
                val count = tagCount(tag)
                if (count > 0) "${tag.label}$count" else null
            }

        return if (summary.isEmpty()) "없음" else summary.joinToString(" ")
    }

    fun synergySummary(): String {
        val active = ItemTag.values()
            .mapNotNull { tag ->
                val count = tagCount(tag)
                when {
                    count >= 4 -> "${tag.label}4"
                    count >= 2 -> "${tag.label}2"
                    else -> null
                }
            }

        return if (active.isEmpty()) "활성 시너지 없음" else active.joinToString(" ")
    }

    fun nearMissScoreMultiplier(): Float {
        var value = 1f + effectValue(ItemEffect.NEAR_MISS_SCORE)
        if (tagCount(ItemTag.RISK) >= 2) value += 0.25f
        if (tagCount(ItemTag.RISK) >= 4) value += 0.25f
        return value
    }

    fun nearMissCoinGain(baseCoins: Int): Int {
        var multiplier = 1f + effectValue(ItemEffect.NEAR_MISS_COIN)
        if (tagCount(ItemTag.GOLD) >= 2) multiplier += 0.25f
        if (tagCount(ItemTag.GOLD) >= 4) multiplier += 0.50f
        return (baseCoins * multiplier).roundToInt().coerceAtLeast(1)
    }

    fun maxMultiplier(): Float {
        var value = 5f + effectValue(ItemEffect.MAX_MULTIPLIER)
        if (tagCount(ItemTag.RISK) >= 4) value += 1f
        return value
    }

    fun multiplierGrowthBonus(): Float {
        var value = effectValue(ItemEffect.COMBO_GROWTH)
        if (tagCount(ItemTag.COMBO) >= 2) value += 0.03f
        if (tagCount(ItemTag.COMBO) >= 4) value += 0.04f
        return value
    }

    fun baseScoreMultiplier(): Float {
        var value = 1f + effectValue(ItemEffect.BASE_SCORE)
        if (tagCount(ItemTag.AGILITY) >= 4) value += 0.15f
        return value
    }

    fun coinPickupGain(baseCoins: Int, hpRatio: Float, airborne: Boolean): Int {
        var multiplier = 1f + effectValue(ItemEffect.COIN_PICKUP_VALUE)
        if (tagCount(ItemTag.GOLD) >= 2) multiplier += 0.15f
        if (tagCount(ItemTag.GOLD) >= 4) multiplier += 0.20f
        multiplier *= lowHpRewardMultiplier(hpRatio)

        var value = (baseCoins * multiplier).roundToInt()
        if (airborne) {
            value += effectValue(ItemEffect.AIR_COIN_BONUS).roundToInt()
        }

        return value.coerceAtLeast(1)
    }

    fun coinMagnetRadius(): Float {
        var radius = 46f + effectValue(ItemEffect.COIN_MAGNET)
        if (tagCount(ItemTag.AGILITY) >= 2) radius += 30f
        if (tagCount(ItemTag.AGILITY) >= 4) radius += 35f
        return radius
    }

    fun nearMissCooldownRefund(): Float {
        return effectValue(ItemEffect.ACTIVE_COOLDOWN_REFUND)
    }

    fun comboCoinReward(combo: Int): Int {
        if (combo <= 0 || combo % 10 != 0) {
            return 0
        }

        var value = effectValue(ItemEffect.COMBO_COIN)
        if (tagCount(ItemTag.GOLD) >= 4) value += 3f
        return value.roundToInt()
    }

    fun lowHpRewardMultiplier(hpRatio: Float): Float {
        if (hpRatio > 0.40f) {
            return 1f
        }

        return 1f + effectValue(ItemEffect.LOW_HP_REWARD)
    }

    fun nearMissPaddingBonus(): Float {
        var value = effectValue(ItemEffect.NEAR_MISS_RANGE)
        if (tagCount(ItemTag.AGILITY) >= 2) value += 12f
        if (tagCount(ItemTag.AGILITY) >= 4) value += 18f
        return value
    }

    fun bonusJumpCount(): Int {
        return effectValue(ItemEffect.EXTRA_JUMP).roundToInt().coerceIn(0, 3)
    }

    fun hasQuickDrop(): Boolean {
        return effectValue(ItemEffect.QUICK_DROP) > 0f
    }

    fun hasTimeSlow(): Boolean {
        return effectValue(ItemEffect.TIME_SLOW) > 0f
    }

    fun hasActiveShield(): Boolean {
        return effectValue(ItemEffect.ACTIVE_SHIELD) > 0f
    }

    fun hasObstacleBreak(): Boolean {
        return effectValue(ItemEffect.OBSTACLE_BREAK) > 0f
    }

    fun dashDistance(): Float {
        return effectValue(ItemEffect.AIR_DASH)
    }

    fun hasNearMissOverdrive(): Boolean {
        return effectValue(ItemEffect.NEAR_MISS_OVERDRIVE) > 0f
    }

    fun activeAbilitySummary(): String {
        val abilities = mutableListOf<String>()
        if (bonusJumpCount() > 0) abilities.add("3단점프")
        if (hasQuickDrop()) abilities.add("급강하")
        if (hasTimeSlow()) abilities.add("시간감속")
        if (hasActiveShield()) abilities.add("보호막")
        if (hasObstacleBreak()) abilities.add("장애물제거")
        if (dashDistance() > 0f) abilities.add("대시")
        if (hasNearMissOverdrive()) abilities.add("오버드라이브")
        return if (abilities.isEmpty()) "없음" else abilities.joinToString("/")
    }

    fun freeRerollsPerShop(): Int {
        return effectValue(ItemEffect.FREE_REROLL).roundToInt().coerceAtLeast(0)
    }

    fun hasCoinStreakSystem(): Boolean {
        return effectValue(ItemEffect.COIN_STREAK_SYSTEM) > 0f
    }

    fun hasSlideTrickSystem(): Boolean {
        return effectValue(ItemEffect.SLIDE_TRICK_SYSTEM) > 0f
    }

    fun hasBountySystem(): Boolean {
        return effectValue(ItemEffect.BOUNTY_SYSTEM) > 0f
    }

    fun hasFeverSystem(): Boolean {
        return effectValue(ItemEffect.FEVER_SYSTEM) > 0f
    }

    fun hasEmergencyPotionSystem(): Boolean {
        return effectValue(ItemEffect.EMERGENCY_POTION_SYSTEM) > 0f
    }

    fun hasLuckySlotSystem(): Boolean {
        return effectValue(ItemEffect.LUCKY_SLOT_SYSTEM) > 0f
    }

    fun hasShopLockSystem(): Boolean {
        return effectValue(ItemEffect.SHOP_LOCK_SYSTEM) > 0f
    }

    fun hasCoinShieldSystem(): Boolean {
        return effectValue(ItemEffect.COIN_SHIELD_SYSTEM) > 0f
    }

    fun hasCoinInterestSystem(): Boolean {
        return effectValue(ItemEffect.COIN_INTEREST_SYSTEM) > 0f
    }

    fun hasNearMissRouletteSystem(): Boolean {
        return effectValue(ItemEffect.NEAR_MISS_ROULETTE_SYSTEM) > 0f
    }

    fun systemSummary(): String {
        val systems = mutableListOf<String>()
        if (hasCoinStreakSystem()) systems.add("코인연쇄")
        if (hasSlideTrickSystem()) systems.add("슬라이드보상")
        if (hasBountySystem()) systems.add("현상금")
        if (hasFeverSystem()) systems.add("피버")
        if (hasEmergencyPotionSystem()) systems.add("긴급회복")
        if (hasLuckySlotSystem()) systems.add("슬롯")
        if (hasShopLockSystem()) systems.add("상점예약")
        if (hasCoinShieldSystem()) systems.add("동전방패")
        if (hasCoinInterestSystem()) systems.add("이자")
        if (hasNearMissRouletteSystem()) systems.add("룰렛")
        return if (systems.isEmpty()) "없음" else systems.joinToString("/")
    }

    fun shopTagBias(tags: Set<ItemTag>): Int {
        var bias = 0
        for (tag in tags) {
            bias += tagCount(tag).coerceAtMost(3)
        }
        return bias
    }

    fun shopDiscountRate(): Float {
        var value = effectValue(ItemEffect.SHOP_DISCOUNT)
        if (tagCount(ItemTag.GOLD) >= 4) value += 0.15f
        return value.coerceAtMost(0.60f)
    }

    fun stageClearHeal(): Int {
        var value = effectValue(ItemEffect.STAGE_HEAL)
        if (tagCount(ItemTag.HEAL) >= 2) value += 25f
        if (tagCount(ItemTag.HEAL) >= 4) value += 35f
        return value.roundToInt()
    }

    fun comboHeal(combo: Int): Int {
        if (combo <= 0 || combo % 5 != 0) {
            return 0
        }

        var value = effectValue(ItemEffect.FIVE_COMBO_HEAL)
        if (tagCount(ItemTag.HEAL) >= 4) value += 12f
        return value.roundToInt()
    }

    fun adjustedDamage(baseDamage: Float): Float {
        var reduction = effectValue(ItemEffect.DAMAGE_REDUCTION)
        if (tagCount(ItemTag.SHIELD) >= 2) reduction += 0.15f

        var damage = baseDamage * (1f - reduction.coerceAtMost(0.55f))

        if (tagCount(ItemTag.SHIELD) >= 4 && !stageShieldUsed) {
            stageShieldUsed = true
            damage *= 0.5f
        }

        return damage.coerceAtLeast(5f)
    }

    fun comboToPreserve(currentCombo: Int): Int {
        if (currentCombo <= 1) {
            return 0
        }

        val canSaveCombo = tagCount(ItemTag.COMBO) >= 4 || effectValue(ItemEffect.COMBO_KEEP) > 0f
        if (!canSaveCombo || stageComboSaveUsed) {
            return 0
        }

        stageComboSaveUsed = true
        return currentCombo / 2
    }

    private fun tagCount(tag: ItemTag): Int {
        var count = 0
        for ((definition, stack) in ownedItems()) {
            if (tag in definition.tags) {
                count += stack
            }
        }
        return count
    }

    private fun effectValue(effect: ItemEffect): Float {
        var value = 0f
        for ((definition, stack) in ownedItems()) {
            if (definition.effect == effect) {
                value += definition.power * stack
            }
        }
        return value
    }
}

data class ItemOffer(
    val definition: ItemDefinition,
    val price: Int
)

class ShopSystem(
    private val metaProgression: MetaProgression
) {
    var offers: List<ItemOffer> = emptyList()
        private set
    var selectedIndex: Int = 0
        private set
    var shopDepth: Int = 1
        private set
    private var freeRerollsLeft = 0
    private var paidRerollsThisShop = 0
    private var lockedDefinition: ItemDefinition? = null

    fun open(depth: Int, inventory: InventorySystem, discountRate: Float, freeRerolls: Int) {
        shopDepth = depth
        freeRerollsLeft = freeRerolls.coerceAtLeast(0)
        paidRerollsThisShop = 0
        selectedIndex = 0
        rollOffers(inventory, discountRate)
    }

    fun reroll(inventory: InventorySystem, discountRate: Float, consumeReroll: Boolean = true) {
        if (consumeReroll) {
            if (freeRerollsLeft > 0) {
                freeRerollsLeft--
            } else {
                paidRerollsThisShop++
            }
        }

        rollOffers(inventory, discountRate)
    }

    private fun rollOffers(inventory: InventorySystem, discountRate: Float) {
        val availableDefinitions = ItemCatalog.unlockedDefinitions(metaProgression.unlockedItemIds())
            .filter { inventory.canAdd(it) }

        val locked = lockedDefinition?.takeIf { lockedItem ->
            availableDefinitions.any { it.id == lockedItem.id }
        }
        if (lockedDefinition != null && locked == null) {
            lockedDefinition = null
        }

        val pool = availableDefinitions
            .filter { definition -> locked?.id != definition.id }
            .flatMap { definition ->
                List(weightFor(definition, inventory)) { definition }
            }
            .shuffled()

        val selectedDefinitions = mutableListOf<ItemDefinition>()
        if (locked != null) {
            selectedDefinitions.add(locked)
        }
        selectedDefinitions.addAll(
            pool.distinctBy { it.id }.take(3 - selectedDefinitions.size)
        )

        offers = selectedDefinitions.map { definition ->
            ItemOffer(definition, priceFor(definition, discountRate))
        }
        selectedIndex = selectedIndex.coerceIn(0, (offers.size - 1).coerceAtLeast(0))
    }

    fun moveLeft() {
        if (offers.isEmpty()) return
        selectedIndex = if (selectedIndex == 0) offers.lastIndex else selectedIndex - 1
    }

    fun moveRight() {
        if (offers.isEmpty()) return
        selectedIndex = if (selectedIndex == offers.lastIndex) 0 else selectedIndex + 1
    }

    fun selectedOffer(): ItemOffer? {
        return offers.getOrNull(selectedIndex)
    }

    fun toggleLockSelected(): String {
        val selected = selectedOffer()
            ?: return "예약할 아이템이 없습니다"

        return if (lockedDefinition?.id == selected.definition.id) {
            lockedDefinition = null
            "${selected.definition.name} 예약 해제"
        } else {
            lockedDefinition = selected.definition
            "${selected.definition.name} 예약됨"
        }
    }

    fun consumeLockIf(definition: ItemDefinition) {
        if (lockedDefinition?.id == definition.id) {
            lockedDefinition = null
        }
    }

    fun isLocked(definition: ItemDefinition): Boolean {
        return lockedDefinition?.id == definition.id
    }

    fun rerollCost(discountRate: Float): Int {
        if (freeRerollsLeft > 0) {
            return 0
        }

        val rerollMultiplier = 1f + paidRerollsThisShop * 0.45f
        val rawCost = 10f * depthMultiplier() * rerollMultiplier * (1f - discountRate)
        return rawCost.roundToInt().coerceAtLeast(1)
    }

    fun rerollInfo(): String {
        val rerollText = if (freeRerollsLeft > 0) {
            "무료 $freeRerollsLeft"
        } else {
            "유료 ${paidRerollsThisShop}회"
        }
        val lockedText = lockedDefinition?.let { " / 예약: ${it.name}" } ?: ""
        return rerollText + lockedText
    }

    private fun priceFor(definition: ItemDefinition, discountRate: Float): Int {
        val rawPrice = definition.rarity.basePrice * depthMultiplier() * (1f - discountRate)
        return rawPrice.roundToInt().coerceAtLeast(5)
    }

    private fun weightFor(definition: ItemDefinition, inventory: InventorySystem): Int {
        val rarityWeight = when (definition.rarity) {
            ItemRarity.COMMON -> 7
            ItemRarity.RARE -> 4 + shopDepth / 2
            ItemRarity.EPIC -> 1 + shopDepth / 2
        }
        return (rarityWeight + inventory.shopTagBias(definition.tags)).coerceAtLeast(1)
    }

    private fun depthMultiplier(): Float {
        return 1f + (shopDepth - 1).coerceAtLeast(0) * 0.15f
    }
}

data class UnlockResult(
    val success: Boolean,
    val message: String
)

class MetaProgression {
    private val preferences = Gdx.app.getPreferences("stage_combo_runner_meta")
    private val gemsKey = "gems"
    private val unlockedKey = "unlocked_items"

    fun gems(): Int {
        return preferences.getInteger(gemsKey, 0)
    }

    fun unlockedItemIds(): Set<String> {
        return ItemCatalog.defaultUnlockedIds() + savedUnlockedIds()
    }

    fun nextLockedItem(): ItemDefinition? {
        val unlockedIds = unlockedItemIds()
        return ItemCatalog.lockedDefinitions().firstOrNull { it.id !in unlockedIds }
    }

    fun unlockNext(cost: Int = 5): UnlockResult {
        val target = nextLockedItem()
            ?: return UnlockResult(false, "모든 아이템을 해금했어.")

        if (gems() < cost) {
            return UnlockResult(false, "${target.name} 해금에는 보석 $cost 개가 필요해.")
        }

        val unlockedIds = savedUnlockedIds() + target.id
        preferences.putInteger(gemsKey, gems() - cost)
        preferences.putString(unlockedKey, unlockedIds.joinToString(","))
        preferences.flush()

        return UnlockResult(true, "${target.name} 해금 완료!")
    }

    fun grantGameOverGems(score: Int, bestCombo: Int, highestStage: Int): Int {
        val gained = (score / 500 + bestCombo / 10 + (highestStage - 1).coerceAtLeast(0))
            .coerceAtLeast(1)
        preferences.putInteger(gemsKey, gems() + gained)
        preferences.flush()
        return gained
    }

    private fun savedUnlockedIds(): Set<String> {
        val raw = preferences.getString(unlockedKey, "")
        if (raw.isBlank()) {
            return emptySet()
        }

        return raw.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()
    }
}
