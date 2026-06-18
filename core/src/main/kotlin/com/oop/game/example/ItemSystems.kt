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
    COMMON("일반", 35),
    RARE("희귀", 60),
    EPIC("에픽", 95)
}

enum class ItemKind(val label: String) {
    PASSIVE("패시브"),
    EQUIPMENT("장비"),
    SYSTEM("패시브"),
    CURSE_CONTRACT("저주"),
    EVOLUTION("진화")
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
    ACTIVE_BOOST,
    TIME_SLOW,
    PHASE_SHIFT,
    ACTIVE_SHIELD,
    OBSTACLE_BREAK,
    HOOK_STAR,
    REWIND_CLOCK,
    AIR_DASH,
    NEAR_MISS_OVERDRIVE,
    COIN_PICKUP_VALUE,
    COIN_MAGNET,
    ACTIVE_COOLDOWN_REFUND,
    COMBO_COIN,
    LOW_HP_REWARD,
    WIND_RIDER,
    FREE_REROLL,
    COIN_STREAK_SYSTEM,
    COMBO_DRAIN_GUARD,
    BOUNTY_SYSTEM,
    FEVER_SYSTEM,
    EMERGENCY_POTION_SYSTEM,
    LUCKY_SLOT_SYSTEM,
    SHOP_LOCK_SYSTEM,
    COIN_SHIELD_SYSTEM,
    COIN_INTEREST_SYSTEM,
    NEAR_MISS_ROULETTE_SYSTEM,
    GLASS_HEART_CONTRACT,
    GREED_CONTRACT,
    SILENCE_CONTRACT,
    BLOOD_CONTRACT,
    GOLDEN_ORBIT_EVOLUTION,
    STORM_LANDING_EVOLUTION,
    BOUNTY_HUNTER_EVOLUTION,
    TIME_THIEF_EVOLUTION,
    IMMORTAL_SHIELD_EVOLUTION
}

data class ItemDefinition(
    val id: String,
    val name: String,
    val rarity: ItemRarity,
    val tags: Set<ItemTag>,
    val effect: ItemEffect,
    val power: Float,
    val defaultUnlocked: Boolean,
    val description: String,
    val kind: ItemKind = ItemKind.PASSIVE
)

data class EquipmentState(
    val definition: ItemDefinition,
    val level: Int
)

data class CurseState(
    val definition: ItemDefinition,
    var timeLeft: Float,
    val expiresAtShop: Boolean
)

data class CurseResolution(
    val message: String,
    val coinGain: Int = 0,
    val coinLossRate: Float = 0f,
    val damage: Float = 0f,
    val resetCombo: Boolean = false,
    val rareShopBoost: Int = 0
)

data class EvolutionRule(
    val resultId: String,
    val requiredIds: Set<String>,
    val requiredTag: ItemTag? = null,
    val requiredTagCount: Int = 0
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
            name = "바람 돛",
            rarity = ItemRarity.COMMON,
            tags = setOf(ItemTag.AGILITY),
            effect = ItemEffect.WIND_RIDER,
            power = 0.28f,
            defaultUnlocked = true,
            description = "바람기둥을 타면 더 높게 상승."
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
            description = "맵 코인을 빠르게 이어 먹으면 코인 콤보가 생김.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "slide_scanner",
            name = "슬라이드 스캐너",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.COMBO, ItemTag.SHIELD),
            effect = ItemEffect.COMBO_DRAIN_GUARD,
            power = 1f,
            defaultUnlocked = true,
            description = "콤보가 높을수록 체력 감소 속도 완화.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "bounty_stamp",
            name = "현상금 도장",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.RISK, ItemTag.GOLD),
            effect = ItemEffect.BOUNTY_SYSTEM,
            power = 1f,
            defaultUnlocked = true,
            description = "일부 장애물에 현상금 표식이 생김.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "fever_clock",
            name = "피버 시계",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.COMBO, ItemTag.AGILITY),
            effect = ItemEffect.FEVER_SYSTEM,
            power = 1f,
            defaultUnlocked = true,
            description = "니어미스와 코인으로 피버 게이지를 충전.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "potion_belt",
            name = "긴급 물약 벨트",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.HEAL, ItemTag.SHIELD),
            effect = ItemEffect.EMERGENCY_POTION_SYSTEM,
            power = 1f,
            defaultUnlocked = true,
            description = "체력이 낮아지면 한 번 자동 회복.",
            kind = ItemKind.SYSTEM
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
            description = "일정 횟수 코인을 먹을 때마다 무작위 정산.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "shop_bookmark",
            name = "상점 예약권",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.GOLD, ItemTag.COMBO),
            effect = ItemEffect.SHOP_LOCK_SYSTEM,
            power = 1f,
            defaultUnlocked = false,
            description = "상점에서 L로 아이템 하나를 리롤 보호.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "coin_shield",
            name = "동전 방패",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.SHIELD, ItemTag.GOLD),
            effect = ItemEffect.COIN_SHIELD_SYSTEM,
            power = 1f,
            defaultUnlocked = false,
            description = "피격 시 코인을 지불하고 피해를 무효화.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "interest_piggy_bank",
            name = "이자 저금통",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.GOLD),
            effect = ItemEffect.COIN_INTEREST_SYSTEM,
            power = 1f,
            defaultUnlocked = false,
            description = "상점 도착 시 보유 코인에 이자가 붙음.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "near_miss_roulette",
            name = "니어미스 룰렛",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.RISK, ItemTag.COMBO),
            effect = ItemEffect.NEAR_MISS_ROULETTE_SYSTEM,
            power = 1f,
            defaultUnlocked = false,
            description = "니어미스 4회마다 무작위 보상 발동.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "third_jump_core",
            name = "삼단 점프 코어",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.AGILITY, ItemTag.COMBO),
            effect = ItemEffect.EXTRA_JUMP,
            power = 1f,
            defaultUnlocked = true,
            description = "점프 가능 횟수 +1. SPACE로 3단 점프 가능.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "gravity_anchor",
            name = "중력 앵커",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.AGILITY, ItemTag.RISK),
            effect = ItemEffect.QUICK_DROP,
            power = 1f,
            defaultUnlocked = true,
            description = "공중에서 X를 누르면 즉시 착지.",
            kind = ItemKind.EQUIPMENT
        ),
        ItemDefinition(
            id = "turbo_booster",
            name = "터보 부스터",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.AGILITY, ItemTag.RISK),
            effect = ItemEffect.ACTIVE_BOOST,
            power = 1f,
            defaultUnlocked = true,
            description = "X로 순간 질주. 앞의 장애물을 밀고 지나가며 거리와 점수 획득.",
            kind = ItemKind.EQUIPMENT
        ),
        ItemDefinition(
            id = "time_jelly",
            name = "시간 정지 시계",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.COMBO, ItemTag.SHIELD),
            effect = ItemEffect.TIME_SLOW,
            power = 1f,
            defaultUnlocked = true,
            description = "X로 5초 동안 장애물과 기믹 속도 45% 감소.",
            kind = ItemKind.EQUIPMENT
        ),
        ItemDefinition(
            id = "phase_cloak",
            name = "위상 망토",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.SHIELD, ItemTag.RISK),
            effect = ItemEffect.PHASE_SHIFT,
            power = 1f,
            defaultUnlocked = true,
            description = "X로 짧게 몸을 흐리게 만들어 충돌과 함정을 통과.",
            kind = ItemKind.EQUIPMENT
        ),
        ItemDefinition(
            id = "shield_capsule",
            name = "순간 보호막",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.SHIELD),
            effect = ItemEffect.ACTIVE_SHIELD,
            power = 1f,
            defaultUnlocked = true,
            description = "X로 3초 동안 피격 1회 무시.",
            kind = ItemKind.EQUIPMENT
        ),
        ItemDefinition(
            id = "crate_firework",
            name = "상자 폭죽",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.RISK, ItemTag.GOLD),
            effect = ItemEffect.OBSTACLE_BREAK,
            power = 1f,
            defaultUnlocked = false,
            description = "X로 가장 가까운 장애물 제거. 보스전에서는 보스 피해.",
            kind = ItemKind.EQUIPMENT
        ),
        ItemDefinition(
            id = "hook_star",
            name = "갈고리 별",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.AGILITY, ItemTag.GOLD),
            effect = ItemEffect.HOOK_STAR,
            power = 1f,
            defaultUnlocked = true,
            description = "X로 앞쪽 코인 루트를 향해 끌려 올라감.",
            kind = ItemKind.EQUIPMENT
        ),
        ItemDefinition(
            id = "rewind_clock",
            name = "되감기 시계",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.COMBO, ItemTag.SHIELD),
            effect = ItemEffect.REWIND_CLOCK,
            power = 1f,
            defaultUnlocked = false,
            description = "X로 약 1초 전 위치와 체력을 되돌림.",
            kind = ItemKind.EQUIPMENT
        ),
        ItemDefinition(
            id = "dash_feather",
            name = "질주 깃털",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.AGILITY, ItemTag.RISK),
            effect = ItemEffect.NEAR_MISS_RANGE,
            power = 10f,
            defaultUnlocked = false,
            description = "니어미스 판정 범위 +10. 민첩 빌드용 패시브."
        ),
        ItemDefinition(
            id = "overdrive_badge",
            name = "오버드라이브 배지",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.COMBO, ItemTag.GOLD),
            effect = ItemEffect.NEAR_MISS_OVERDRIVE,
            power = 1f,
            defaultUnlocked = false,
            description = "8콤보마다 5초간 니어미스 보상이 2배.",
            kind = ItemKind.SYSTEM
        ),
        ItemDefinition(
            id = "glass_heart_contract",
            name = "유리 심장 계약",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.RISK),
            effect = ItemEffect.GLASS_HEART_CONTRACT,
            power = 1f,
            defaultUnlocked = true,
            description = "18초 동안 피격 피해 2배. 버티면 코인 +35.",
            kind = ItemKind.CURSE_CONTRACT
        ),
        ItemDefinition(
            id = "greed_contract",
            name = "탐욕 계약",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.GOLD, ItemTag.RISK),
            effect = ItemEffect.GREED_CONTRACT,
            power = 1f,
            defaultUnlocked = true,
            description = "다음 상점까지 코인 획득 2배. 피격 시 코인 35% 손실.",
            kind = ItemKind.CURSE_CONTRACT
        ),
        ItemDefinition(
            id = "silence_contract",
            name = "침묵 계약",
            rarity = ItemRarity.RARE,
            tags = setOf(ItemTag.COMBO, ItemTag.RISK),
            effect = ItemEffect.SILENCE_CONTRACT,
            power = 1f,
            defaultUnlocked = true,
            description = "20초 동안 장비 사용 불가. 버티면 희귀 상점 확률 증가.",
            kind = ItemKind.CURSE_CONTRACT
        ),
        ItemDefinition(
            id = "blood_contract",
            name = "피의 계약",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.RISK, ItemTag.HEAL),
            effect = ItemEffect.BLOOD_CONTRACT,
            power = 1f,
            defaultUnlocked = true,
            description = "체력 80 소모. 다음 상점까지 니어미스 점수와 보스 피해 증가.",
            kind = ItemKind.CURSE_CONTRACT
        ),
        ItemDefinition(
            id = "golden_orbit",
            name = "황금 궤도",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.GOLD, ItemTag.AGILITY),
            effect = ItemEffect.GOLDEN_ORBIT_EVOLUTION,
            power = 1f,
            defaultUnlocked = true,
            description = "코인을 모으면 궤도 충전. 가득 차면 앞 장애물을 제거.",
            kind = ItemKind.EVOLUTION
        ),
        ItemDefinition(
            id = "storm_landing",
            name = "폭풍 착지",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.AGILITY, ItemTag.RISK),
            effect = ItemEffect.STORM_LANDING_EVOLUTION,
            power = 1f,
            defaultUnlocked = true,
            description = "중력 앵커 착지 충격파가 낮은 장애물을 제거.",
            kind = ItemKind.EVOLUTION
        ),
        ItemDefinition(
            id = "bounty_hunter",
            name = "현상금 사냥꾼",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.RISK, ItemTag.GOLD),
            effect = ItemEffect.BOUNTY_HUNTER_EVOLUTION,
            power = 1f,
            defaultUnlocked = true,
            description = "현상금 보상과 보스 피해가 증가.",
            kind = ItemKind.EVOLUTION
        ),
        ItemDefinition(
            id = "time_thief",
            name = "시간 도둑",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.COMBO, ItemTag.AGILITY),
            effect = ItemEffect.TIME_THIEF_EVOLUTION,
            power = 1f,
            defaultUnlocked = true,
            description = "니어미스가 시간 정지 시계 쿨타임을 크게 줄임.",
            kind = ItemKind.EVOLUTION
        ),
        ItemDefinition(
            id = "immortal_shield",
            name = "불사 보호막",
            rarity = ItemRarity.EPIC,
            tags = setOf(ItemTag.SHIELD, ItemTag.HEAL),
            effect = ItemEffect.IMMORTAL_SHIELD_EVOLUTION,
            power = 1f,
            defaultUnlocked = true,
            description = "죽을 피해를 한 번 막고 짧은 보호막을 부여.",
            kind = ItemKind.EVOLUTION
        )
    )

    private val byId = all.associateBy { it.id }
    private val shopItemIds = setOf(
        "third_jump_core",
        "slide_scanner",
        "air_wallet",
        "magnet_bracelet",
        "coin_streak_engine",
        "bounty_stamp",
        "fever_clock",
        "potion_belt",
        "shop_bookmark",
        "near_miss_roulette",
        "gravity_anchor",
        "turbo_booster",
        "time_jelly",
        "phase_cloak",
        "shield_capsule",
        "crate_firework",
        "hook_star",
        "rewind_clock",
        "glass_heart_contract",
        "greed_contract",
        "silence_contract"
    )

    fun find(id: String): ItemDefinition? {
        return byId[id]
    }

    fun defaultUnlockedIds(): Set<String> {
        return all.filter { it.defaultUnlocked && it.id in shopItemIds }.map { it.id }.toSet()
    }

    fun lockedDefinitions(): List<ItemDefinition> {
        return all.filter { !it.defaultUnlocked && it.id in shopItemIds }
    }

    fun unlockedDefinitions(unlockedIds: Set<String>): List<ItemDefinition> {
        return all.filter { it.id in shopItemIds && (it.defaultUnlocked || it.id in unlockedIds) }
    }
}

private val evolutionRules = listOf(
    EvolutionRule(
        resultId = "golden_orbit",
        requiredIds = setOf("magnet_bracelet", "coin_streak_engine", "air_wallet")
    ),
    EvolutionRule(
        resultId = "storm_landing",
        requiredIds = setOf("gravity_anchor", "third_jump_core", "slide_scanner")
    ),
    EvolutionRule(
        resultId = "bounty_hunter",
        requiredIds = setOf("bounty_stamp", "crate_firework"),
        requiredTag = ItemTag.RISK,
        requiredTagCount = 3
    ),
    EvolutionRule(
        resultId = "time_thief",
        requiredIds = setOf("time_jelly", "charge_battery", "near_miss_roulette")
    ),
    EvolutionRule(
        resultId = "immortal_shield",
        requiredIds = setOf("shield_capsule", "potion_belt", "guard_jelly")
    )
)

class InventorySystem {
    private val stacks = mutableMapOf<String, Int>()
    private var stageShieldUsed = false
    private var stageComboSaveUsed = false
    private var currentEquipmentId: String? = null
    private var activeCurse: CurseState? = null
    private var rareShopBoost = 0
    private val triggeredEvolutions = mutableSetOf<String>()

    fun resetRun() {
        stacks.clear()
        currentEquipmentId = null
        activeCurse = null
        rareShopBoost = 0
        triggeredEvolutions.clear()
        resetStage()
    }

    fun resetStage() {
        stageShieldUsed = false
        stageComboSaveUsed = false
    }

    fun canAdd(definition: ItemDefinition): Boolean {
        if (definition.kind == ItemKind.EVOLUTION) {
            return false
        }
        if (definition.kind == ItemKind.CURSE_CONTRACT && activeCurse != null) {
            return false
        }
        return stackOf(definition) < maxStackOf(definition)
    }

    fun maxStackOf(definition: ItemDefinition): Int {
        return when (definition.kind) {
            ItemKind.EQUIPMENT -> 3
            ItemKind.SYSTEM,
            ItemKind.CURSE_CONTRACT,
            ItemKind.EVOLUTION -> 1
            ItemKind.PASSIVE -> 3
        }
    }

    fun addItem(definition: ItemDefinition): Boolean {
        if (!canAdd(definition)) {
            return false
        }

        stacks[definition.id] = stackOf(definition) + 1
        when (definition.kind) {
            ItemKind.EQUIPMENT -> currentEquipmentId = definition.id
            ItemKind.CURSE_CONTRACT -> activeCurse = createCurseState(definition)
            ItemKind.EVOLUTION -> triggeredEvolutions.add(definition.id)
            ItemKind.PASSIVE,
            ItemKind.SYSTEM -> Unit
        }
        return true
    }

    private fun createCurseState(definition: ItemDefinition): CurseState {
        return when (definition.effect) {
            ItemEffect.GLASS_HEART_CONTRACT -> CurseState(definition, timeLeft = 18f, expiresAtShop = false)
            ItemEffect.GREED_CONTRACT -> CurseState(definition, timeLeft = -1f, expiresAtShop = true)
            ItemEffect.SILENCE_CONTRACT -> CurseState(definition, timeLeft = 20f, expiresAtShop = false)
            ItemEffect.BLOOD_CONTRACT -> CurseState(definition, timeLeft = -1f, expiresAtShop = true)
            else -> CurseState(definition, timeLeft = 0f, expiresAtShop = false)
        }
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

    fun coinPickupGain(baseCoins: Int, hpRatio: Float): Int {
        var multiplier = 1f + effectValue(ItemEffect.COIN_PICKUP_VALUE)
        if (tagCount(ItemTag.GOLD) >= 2) multiplier += 0.15f
        if (tagCount(ItemTag.GOLD) >= 4) multiplier += 0.20f
        multiplier *= lowHpRewardMultiplier(hpRatio)

        return (baseCoins * multiplier).roundToInt().coerceAtLeast(1)
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

    fun hasActiveBoost(): Boolean {
        return effectValue(ItemEffect.ACTIVE_BOOST) > 0f
    }

    fun hasTimeSlow(): Boolean {
        return effectValue(ItemEffect.TIME_SLOW) > 0f
    }

    fun hasPhaseShift(): Boolean {
        return effectValue(ItemEffect.PHASE_SHIFT) > 0f
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

    fun currentEquipment(): EquipmentState? {
        val id = currentEquipmentId ?: return null
        val definition = ItemCatalog.find(id) ?: return null
        return EquipmentState(definition, stackOf(definition).coerceAtLeast(1))
    }

    fun equipmentCooldown(): Float {
        val level = currentEquipment()?.level ?: return 0f
        return (7f - (level - 1) * 1f).coerceAtLeast(3f)
    }

    fun equipmentDuration(baseDuration: Float): Float {
        val level = currentEquipment()?.level ?: return baseDuration
        return baseDuration + (level - 1) * 0.45f
    }

    fun isEquipmentSilenced(): Boolean {
        return activeCurse?.definition?.effect == ItemEffect.SILENCE_CONTRACT
    }

    fun equipmentSummary(): String {
        val equipment = currentEquipment() ?: return "없음"
        return "${equipment.definition.name} Lv${equipment.level}"
    }

    fun activeAbilitySummary(): String {
        return equipmentSummary()
    }

    fun freeRerollsPerShop(): Int {
        return effectValue(ItemEffect.FREE_REROLL).roundToInt().coerceAtLeast(0)
    }

    fun hasCoinStreakSystem(): Boolean {
        return effectValue(ItemEffect.COIN_STREAK_SYSTEM) > 0f
    }

    fun hpDrainMultiplier(combo: Int): Float {
        val guardPower = effectValue(ItemEffect.COMBO_DRAIN_GUARD)
        if (guardPower <= 0f || combo <= 0) {
            return 1f
        }

        val reduction = ((combo / 10f) * 0.08f * guardPower).coerceAtMost(0.65f)
        return 1f - reduction
    }

    fun windColumnMultiplier(): Float {
        return (1f + effectValue(ItemEffect.WIND_RIDER)).coerceAtMost(1.65f)
    }

    fun hasComboDrainGuard(): Boolean {
        return effectValue(ItemEffect.COMBO_DRAIN_GUARD) > 0f
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
        if (hasComboDrainGuard()) systems.add("콤보생존")
        if (hasBountySystem()) systems.add("현상금")
        if (hasFeverSystem()) systems.add("피버")
        if (hasEmergencyPotionSystem()) systems.add("긴급회복")
        if (hasLuckySlotSystem()) systems.add("슬롯")
        if (hasShopLockSystem()) systems.add("상점예약")
        if (hasCoinShieldSystem()) systems.add("동전방패")
        if (hasCoinInterestSystem()) systems.add("이자")
        if (hasNearMissRouletteSystem()) systems.add("룰렛")
        if (hasNearMissOverdrive()) systems.add("오버드라이브")
        return if (systems.isEmpty()) "없음" else systems.joinToString("/")
    }

    fun activeCurse(): CurseState? {
        return activeCurse
    }

    fun curseSummary(): String {
        val curse = activeCurse ?: return "없음"
        val timeText = if (curse.timeLeft > 0f) " ${curse.timeLeft.toInt() + 1}초" else " 다음 상점까지"
        return "${curse.definition.name}$timeText"
    }

    fun updateCurse(delta: Float): CurseResolution? {
        val curse = activeCurse ?: return null
        if (curse.timeLeft <= 0f) {
            return null
        }

        curse.timeLeft = (curse.timeLeft - delta).coerceAtLeast(0f)
        if (curse.timeLeft > 0f) {
            return null
        }

        activeCurse = null
        return when (curse.definition.effect) {
            ItemEffect.GLASS_HEART_CONTRACT -> CurseResolution(
                message = "유리 심장 계약 성공! 코인 +35",
                coinGain = 35
            )
            ItemEffect.SILENCE_CONTRACT -> {
                rareShopBoost += 2
                CurseResolution(
                    message = "침묵 계약 성공! 다음 상점 희귀 확률 증가",
                    rareShopBoost = 2
                )
            }
            else -> CurseResolution(message = "${curse.definition.name} 종료")
        }
    }

    fun completeShopCurse(): CurseResolution? {
        val curse = activeCurse ?: return null
        if (!curse.expiresAtShop) {
            return null
        }

        activeCurse = null
        return when (curse.definition.effect) {
            ItemEffect.GREED_CONTRACT -> CurseResolution(message = "탐욕 계약 완료")
            ItemEffect.BLOOD_CONTRACT -> CurseResolution(message = "피의 계약 완료")
            else -> CurseResolution(message = "${curse.definition.name} 완료")
        }
    }

    fun curseDamageMultiplier(): Float {
        return if (activeCurse?.definition?.effect == ItemEffect.GLASS_HEART_CONTRACT) 2f else 1f
    }

    fun curseCoinMultiplier(): Float {
        return if (activeCurse?.definition?.effect == ItemEffect.GREED_CONTRACT) 2f else 1f
    }

    fun curseNearMissScoreMultiplier(): Float {
        return if (activeCurse?.definition?.effect == ItemEffect.BLOOD_CONTRACT) 1.6f else 1f
    }

    fun curseBossDamageMultiplier(): Float {
        return if (activeCurse?.definition?.effect == ItemEffect.BLOOD_CONTRACT) 1.7f else 1f
    }

    fun curseHitPenalty(): CurseResolution? {
        val curse = activeCurse ?: return null
        return when (curse.definition.effect) {
            ItemEffect.GREED_CONTRACT -> CurseResolution(
                message = "탐욕 계약 피격! 코인 35% 손실",
                coinLossRate = 0.35f
            )
            else -> null
        }
    }

    fun shopRarityBias(): Int {
        return rareShopBoost
    }

    fun checkEvolutions(): List<ItemDefinition> {
        val evolved = mutableListOf<ItemDefinition>()

        for (rule in evolutionRules) {
            if (rule.resultId in triggeredEvolutions) {
                continue
            }
            if (!rule.requiredIds.all { id -> (stacks[id] ?: 0) > 0 }) {
                continue
            }
            if (rule.requiredTag != null && tagCount(rule.requiredTag) < rule.requiredTagCount) {
                continue
            }

            val definition = ItemCatalog.find(rule.resultId) ?: continue
            stacks[definition.id] = 1
            triggeredEvolutions.add(definition.id)
            evolved.add(definition)
        }

        return evolved
    }

    fun hasEvolution(id: String): Boolean {
        return id in triggeredEvolutions || ((stacks[id] ?: 0) > 0 && ItemCatalog.find(id)?.kind == ItemKind.EVOLUTION)
    }

    fun evolutionSummary(): String {
        val names = triggeredEvolutions
            .mapNotNull { id -> ItemCatalog.find(id)?.name }

        return if (names.isEmpty()) "없음" else names.joinToString("/")
    }

    fun purchaseSummary(definition: ItemDefinition): String {
        return when (definition.kind) {
            ItemKind.EQUIPMENT -> "장비 장착: ${definition.name} Lv${stackOf(definition)}"
            ItemKind.CURSE_CONTRACT -> "저주 계약 시작: ${definition.name}"
            ItemKind.SYSTEM -> "패시브 해금: ${definition.name}"
            ItemKind.EVOLUTION -> "진화 발동: ${definition.name}"
            ItemKind.PASSIVE -> "구매: ${definition.name}"
        }
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
            .filter { it.kind != ItemKind.EVOLUTION }
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
        val rawCost = 14f * depthMultiplier() * rerollMultiplier * (1f - discountRate)
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
        val rarityBoost = when (definition.rarity) {
            ItemRarity.COMMON -> 0
            ItemRarity.RARE -> inventory.shopRarityBias()
            ItemRarity.EPIC -> inventory.shopRarityBias() * 2
        }
        val kindBoost = when (definition.kind) {
            ItemKind.EQUIPMENT -> 5
            ItemKind.SYSTEM -> 3
            ItemKind.CURSE_CONTRACT -> 1
            ItemKind.PASSIVE -> -2
            ItemKind.EVOLUTION -> 0
        }
        return (rarityWeight + rarityBoost + kindBoost + inventory.shopTagBias(definition.tags)).coerceAtLeast(1)
    }

    private fun depthMultiplier(): Float {
        return 1f + (shopDepth - 1).coerceAtLeast(0) * 0.20f
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
