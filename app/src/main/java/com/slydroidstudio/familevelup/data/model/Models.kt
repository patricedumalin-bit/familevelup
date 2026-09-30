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

data class User(
    val id: String,
    val name: String,
    val role: UserRole,
    val xp: Int = 0,
    val level: Int = 1,
    val coins: Int = 0,
    val avatarSkin: String = "default_avatar",
    val streakCount: Int = 0
)

data class Quest(
    val id: String,
    val title: String,
    val xpReward: Int,
    val coinReward: Int,
    val assigneeId: String,
    val status: QuestStatus = QuestStatus.PENDING,
    val proofImageUrl: String? = null
)

data class Reward(
    val id: String,
    val title: String,
    val cost: Int,
    val description: String
)
