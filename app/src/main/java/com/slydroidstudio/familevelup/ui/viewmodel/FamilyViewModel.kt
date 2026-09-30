package com.slydroidstudio.familevelup.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.slydroidstudio.familevelup.data.model.Quest
import com.slydroidstudio.familevelup.data.model.QuestStatus
import com.slydroidstudio.familevelup.data.model.Reward
import com.slydroidstudio.familevelup.data.model.User
import com.slydroidstudio.familevelup.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FamilyViewModel : ViewModel() {

    private val _users = MutableStateFlow(
        listOf(
            User(id = "p1", name = "Papa / Maman", role = UserRole.PARENT),
            User(id = "c1", name = "Lucas (10 ans)", role = UserRole.CHILD, xp = 120, level = 2, coins = 50),
            User(id = "c2", name = "Emma (7 ans)", role = UserRole.CHILD, xp = 80, level = 1, coins = 30)
        )
    )
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _quests = MutableStateFlow(
        listOf(
            Quest(id = "q1", title = "Vider le lave-vaisselle", xpReward = 15, coinReward = 15, assigneeId = "c1", status = QuestStatus.PENDING),
            Quest(id = "q2", title = "Ranger sa chambre", xpReward = 30, coinReward = 30, assigneeId = "c2", status = QuestStatus.PENDING),
            Quest(id = "q3", title = "Nourrir le chat", xpReward = 10, coinReward = 10, assigneeId = "c1", status = QuestStatus.COMPLETED)
        )
    )
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    private val _rewards = MutableStateFlow(
        listOf(
            Reward(id = "r1", title = "30 min de jeux vidéo", cost = 50, description = "Du temps d'écran supplémentaire en plus."),
            Reward(id = "r2", title = "Choisir le film du vendredi", cost = 80, description = "Choisis le film familial de la semaine !"),
            Reward(id = "r3", title = "Argent de poche (2€)", cost = 100, description = "Convertis tes pièces en vrai argent de poche.")
        )
    )
    val rewards: StateFlow<List<Reward>> = _rewards.asStateFlow()

    fun selectUser(user: User?) {
        _currentUser.value = user
    }

    fun addQuest(title: String, xp: Int, coins: Int, assigneeId: String) {
        val newQuest = Quest(
            id = "q_${System.currentTimeMillis()}",
            title = title,
            xpReward = xp,
            coinReward = coins,
            assigneeId = assigneeId,
            status = QuestStatus.PENDING
        )
        _quests.update { it + newQuest }
    }

    fun completeQuest(questId: String, proofUrl: String? = null) {
        _quests.update { list ->
            list.map { q ->
                if (q.id == questId) q.copy(status = QuestStatus.COMPLETED, proofImageUrl = proofUrl) else q
            }
        }
    }

    fun validateQuest(questId: String) {
        val quest = _quests.value.find { it.id == questId } ?: return
        if (quest.status == QuestStatus.COMPLETED) {
            // Reward user & increment streak
            _users.update { usersList ->
                usersList.map { u ->
                    if (u.id == quest.assigneeId) {
                        val newStreak = u.streakCount + 1
                        val streakBonusCoins = if (newStreak % 3 == 0) 15 else 0 // Bonus every 3 streaks!
                        val newXp = u.xp + quest.xpReward
                        val newCoins = u.coins + quest.coinReward + streakBonusCoins
                        val newLevel = 1 + (newXp / 100)
                        u.copy(xp = newXp, level = newLevel, coins = newCoins, streakCount = newStreak)
                    } else u
                }
            }
            _quests.update { list ->
                list.map { q ->
                    if (q.id == questId) q.copy(status = QuestStatus.VALIDATED) else q
                }
            }
        }
    }

    fun requestRevision(questId: String) {
        _quests.update { list ->
            list.map { q ->
                if (q.id == questId) q.copy(status = QuestStatus.NEEDS_REVISION) else q
            }
        }
    }

    fun buyReward(userId: String, reward: Reward) {
        val user = _users.value.find { it.id == userId } ?: return
        if (user.coins >= reward.cost) {
            _users.update { usersList ->
                usersList.map { u ->
                    if (u.id == userId) u.copy(coins = u.coins - reward.cost) else u
                }
            }
        }
    }
}
