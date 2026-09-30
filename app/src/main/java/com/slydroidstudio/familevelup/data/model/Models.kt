package com.slydroidstudio.familevelup.data.model

enum class UserRole {
    PARENT,
    CHILD
}

enum class QuestStatus {
    PENDING,
    COMPLETED,
    VALIDATED,
    NEEDS_REVISION
}

enum class UserCategory(val displayName: String) {
    CHILD_6_10("Enfant (6-10 ans)"),
    PRETEEN_11_13("Préado (11-13 ans)"),
    TEEN_14_18("Ado (14-18 ans)"),
    ADULT("Adulte")
}

data class User(
    val id: String,
    val name: String,
    val role: UserRole,
    val xp: Int = 0,
    val level: Int = 1,
    val coins: Int = 0,
    val avatarSkin: String = "default_avatar",
    val streakCount: Int = 0,
    val pocketMoneyEuros: Double = 0.0,
    val category: UserCategory = UserCategory.CHILD_6_10,
    val rewardMultiplier: Float = 1.0f
)

enum class QuestFrequency {
    DAILY,
    WEEKLY,
    ONE_TIME
}

data class Quest(
    val id: String,
    val title: String,
    val xpReward: Int,
    val coinReward: Int,
    val assigneeId: String,
    val status: QuestStatus = QuestStatus.PENDING,
    val proofImageUrl: String? = null,
    val isRaid: Boolean = false,
    val frequency: QuestFrequency = QuestFrequency.DAILY,
    val isOpenBounty: Boolean = false,
    val validityDays: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

data class Reward(
    val id: String,
    val title: String,
    val cost: Int,
    val description: String
)

enum class CouponStatus {
    PENDING_PARENT_APPROVAL,
    READY_TO_USE
}

data class RedeemedReward(
    val id: String,
    val rewardTitle: String,
    val childId: String,
    val childName: String,
    val cost: Int,
    val status: CouponStatus = CouponStatus.PENDING_PARENT_APPROVAL
)

data class ActivityLogItem(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false
)
