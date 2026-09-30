package com.slydroidstudio.familevelup.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.slydroidstudio.familevelup.data.model.ActivityLogItem
import com.slydroidstudio.familevelup.data.model.CouponStatus
import com.slydroidstudio.familevelup.data.model.Quest
import com.slydroidstudio.familevelup.data.model.QuestFrequency
import com.slydroidstudio.familevelup.data.model.QuestStatus
import com.slydroidstudio.familevelup.data.model.RedeemedReward
import com.slydroidstudio.familevelup.data.model.Reward
import com.slydroidstudio.familevelup.data.model.User
import com.slydroidstudio.familevelup.data.model.UserCategory
import com.slydroidstudio.familevelup.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.ceil

class FamilyViewModel : ViewModel() {

    private val _users = MutableStateFlow(
        listOf(
            User(id = "p1", name = "Papa / Maman", role = UserRole.PARENT, category = UserCategory.ADULT),
            User(id = "c1", name = "Caleb (6 ans)", role = UserRole.CHILD, xp = 20, level = 1, coins = 10, avatarSkin = "🛡️", category = UserCategory.CHILD_6_10, rewardMultiplier = 1.0f),
            User(id = "c2", name = "Luna (6 ans)", role = UserRole.CHILD, xp = 20, level = 1, coins = 10, avatarSkin = "🦄", category = UserCategory.CHILD_6_10, rewardMultiplier = 1.0f)
        )
    )
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _quests = MutableStateFlow(
        listOf(
            Quest(id = "q1", title = "Ranger ses jouets", xpReward = 15, coinReward = 15, assigneeId = "c1", status = QuestStatus.PENDING, frequency = QuestFrequency.DAILY),
            Quest(id = "q2", title = "Mettre la table", xpReward = 20, coinReward = 20, assigneeId = "c2", status = QuestStatus.PENDING, frequency = QuestFrequency.DAILY),
            Quest(id = "raid_1", title = "🏰 Raid : Nettoyage du salon", xpReward = 50, coinReward = 50, assigneeId = "FAMILY", status = QuestStatus.PENDING, isRaid = true)
        )
    )
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    private val _rewards = MutableStateFlow(
        listOf(
            Reward(id = "r1", title = "30 min de dessin animé", cost = 40, description = "Regarde ton dessin animé préféré."),
            Reward(id = "r2", title = "Choisir le dessert", cost = 60, description = "Choisis le dessert du soir !"),
            Reward(id = "r3", title = "Argent de poche (2€)", cost = 100, description = "Convertis tes pièces en vrai argent de poche.")
        )
    )
    val rewards: StateFlow<List<Reward>> = _rewards.asStateFlow()

    private val _redeemedRewards = MutableStateFlow<List<RedeemedReward>>(emptyList())
    val redeemedRewards: StateFlow<List<RedeemedReward>> = _redeemedRewards.asStateFlow()

    private val _activityLog = MutableStateFlow<List<ActivityLogItem>>(
        listOf(
            ActivityLogItem(id = "log_1", title = "Famille Dumalin 🏰", description = "Bienvenue dans FamiLevelUp !")
        )
    )
    val activityLog: StateFlow<List<ActivityLogItem>> = _activityLog.asStateFlow()

    private val _householdName = MutableStateFlow("Famille Dumalin")
    val householdName: StateFlow<String> = _householdName.asStateFlow()

    fun updateHouseholdName(name: String) {
        _householdName.value = name
        logActivity("Paramètres mis à jour ⚙️", "Nom du foyer modifié : $name")
    }

    fun selectUser(user: User?) {
        _currentUser.value = user
    }

    fun verifyParentPin(pin: String): Boolean {
        return pin == "1234"
    }

    fun addQuest(title: String, xp: Int, coins: Int, assigneeId: String, frequency: QuestFrequency, isOpenBounty: Boolean, validityDays: Int = 1) {
        val calculatedValidity = when (frequency) {
            QuestFrequency.DAILY -> 1
            QuestFrequency.WEEKLY -> 7
            QuestFrequency.ONE_TIME -> validityDays
        }
        val newQuest = Quest(
            id = "q_${System.currentTimeMillis()}",
            title = title,
            xpReward = xp,
            coinReward = coins,
            assigneeId = if (isOpenBounty) "OPEN" else assigneeId,
            status = QuestStatus.PENDING,
            isRaid = false,
            frequency = frequency,
            isOpenBounty = isOpenBounty,
            validityDays = calculatedValidity,
            createdAt = System.currentTimeMillis()
        )
        _quests.update { it + newQuest }
        logActivity("Nouvelle quête 📜", "Tâche créée : $title (Validité : ${if (calculatedValidity == -1) "Illimitée" else "$calculatedValidity j"})")
    }

    fun addRaid(title: String, xp: Int, coins: Int) {
        val newRaid = Quest(
            id = "raid_${System.currentTimeMillis()}",
            title = title,
            xpReward = xp,
            coinReward = coins,
            assigneeId = "FAMILY",
            status = QuestStatus.PENDING,
            isRaid = true
        )
        _quests.update { it + newRaid }
        logActivity("Nouveau Raid 🏰", "Raid familial créé : $title")
    }

    fun claimOpenBounty(questId: String, childId: String) {
        val quest = _quests.value.find { it.id == questId }
        val child = _users.value.find { it.id == childId }
        _quests.update { list ->
            list.map { q ->
                if (q.id == questId && q.isOpenBounty) {
                    q.copy(assigneeId = childId, isOpenBounty = false)
                } else q
            }
        }
        if (quest != null && child != null) {
            logActivity("Quête saisie 🎯", "${child.name} a pris la quête : ${quest.title}")
        }
    }

    fun completeQuest(questId: String, proofUrl: String? = null) {
        val quest = _quests.value.find { it.id == questId }
        _quests.update { list ->
            list.map { q ->
                if (q.id == questId) q.copy(status = QuestStatus.COMPLETED, proofImageUrl = proofUrl) else q
            }
        }
        if (quest != null) {
            logActivity("Mission terminée 📷", "Quête soumise pour validation : ${quest.title}")
        }
    }

    fun validateQuest(questId: String) {
        val quest = _quests.value.find { it.id == questId } ?: return
        if (quest.status == QuestStatus.COMPLETED) {
            if (quest.isRaid) {
                // Reward ALL children/members except adults for family raid
                _users.update { usersList ->
                    usersList.map { u ->
                        if (u.role == UserRole.CHILD && u.category != UserCategory.ADULT) {
                            val rewardedXp = (quest.xpReward * u.rewardMultiplier).toInt()
                            val rewardedCoins = (quest.coinReward * u.rewardMultiplier).toInt()
                            val newXp = u.xp + rewardedXp
                            val newCoins = u.coins + rewardedCoins
                            val newLevel = 1 + (newXp / 100)
                            u.copy(xp = newXp, level = newLevel, coins = newCoins)
                        } else u
                    }
                }
                logActivity("Raid validé ✅", "Le raid '${quest.title}' a été validé pour la famille !")
            } else {
                // Reward specific assignee & increment streak (if not adult)
                val isAssignedPersonal = !quest.isRaid && !quest.isOpenBounty && quest.assigneeId != "OPEN" && quest.assigneeId != "FAMILY"
                val isExpired = isAssignedPersonal && quest.validityDays != -1 && (System.currentTimeMillis() - quest.createdAt > quest.validityDays * 24 * 60 * 60 * 1000L)
                val penalty = if (isExpired) ceil(quest.coinReward * 0.10).toInt() else 0

                val assignee = _users.value.find { it.id == quest.assigneeId }
                _users.update { usersList ->
                    usersList.map { u ->
                        if (u.id == quest.assigneeId) {
                            if (u.category == UserCategory.ADULT) {
                                u
                            } else {
                                val rewardedXp = (quest.xpReward * u.rewardMultiplier).toInt()
                                val rewardedCoins = (quest.coinReward * u.rewardMultiplier).toInt()
                                val newStreak = u.streakCount + 1
                                val streakBonusCoins = if (newStreak % 3 == 0) 15 else 0
                                val newXp = u.xp + rewardedXp
                                val newCoins = (u.coins + rewardedCoins + streakBonusCoins - penalty).coerceAtLeast(0)
                                val newLevel = 1 + (newXp / 100)
                                u.copy(xp = newXp, level = newLevel, coins = newCoins, streakCount = newStreak)
                            }
                        } else u
                    }
                }
                if (assignee != null) {
                    if (isExpired && penalty > 0) {
                        logActivity("Malus appliqué ⚠️", "'${quest.title}' validée en retard pour ${assignee.name} (-$penalty pièces de malus)")
                    } else {
                        logActivity("Quête validée ✅", "'${quest.title}' validée pour ${assignee.name}")
                    }
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
        val quest = _quests.value.find { it.id == questId }
        _quests.update { list ->
            list.map { q ->
                if (q.id == questId) q.copy(status = QuestStatus.NEEDS_REVISION) else q
            }
        }
        if (quest != null) {
            logActivity("Retouche demandée ⚠️", "Quête à refaire : ${quest.title}")
        }
    }

    fun buyReward(userId: String, reward: Reward) {
        val user = _users.value.find { it.id == userId } ?: return
        if (user.coins >= reward.cost) {
            val isPocketMoney = reward.id == "r3" || reward.title.contains("Argent de poche", ignoreCase = true)
            val euroAdd = if (isPocketMoney) 2.0 else 0.0

            _users.update { usersList ->
                usersList.map { u ->
                    if (u.id == userId) {
                        u.copy(
                            coins = u.coins - reward.cost,
                            pocketMoneyEuros = u.pocketMoneyEuros + euroAdd
                        )
                    } else u
                }
            }
            if (_currentUser.value?.id == userId) {
                _currentUser.update { 
                    it?.copy(
                        coins = it.coins - reward.cost,
                        pocketMoneyEuros = it.pocketMoneyEuros + euroAdd
                    ) 
                }
            }
            val coupon = RedeemedReward(
                id = "coupon_${System.currentTimeMillis()}",
                rewardTitle = reward.title,
                childId = user.id,
                childName = user.name,
                cost = reward.cost,
                status = CouponStatus.PENDING_PARENT_APPROVAL
            )
            _redeemedRewards.update { it + coupon }
            logActivity("Achat boutique 🎁", "${user.name} a acheté : ${reward.title}")
        }
    }

    fun fulfillReward(couponId: String) {
        val coupon = _redeemedRewards.value.find { it.id == couponId }
        _redeemedRewards.update { list ->
            list.map { c ->
                if (c.id == couponId) c.copy(status = CouponStatus.READY_TO_USE) else c
            }
        }
        if (coupon != null) {
            logActivity("Bon délivré 🎟️", "Le bon '${coupon.rewardTitle}' a été délivré à ${coupon.childName}")
        }
    }

    fun consumeReward(couponId: String) {
        val coupon = _redeemedRewards.value.find { it.id == couponId }
        _redeemedRewards.update { list ->
            list.filter { it.id != couponId }
        }
        if (coupon != null) {
            logActivity("Bon consommé 🗑️", "Le bon '${coupon.rewardTitle}' a été utilisé par ${coupon.childName}")
        }
    }

    fun updateAvatarSkin(userId: String, skin: String) {
        val user = _users.value.find { it.id == userId }
        _users.update { usersList ->
            usersList.map { u ->
                if (u.id == userId) u.copy(avatarSkin = skin) else u
            }
        }
        if (_currentUser.value?.id == userId) {
            _currentUser.update { it?.copy(avatarSkin = skin) }
        }
        if (user != null) {
            logActivity("Avatar changé 👤", "${user.name} a choisi l'avatar $skin")
        }
    }

    fun payoutPocketMoney(childId: String, amountEuros: Double) {
        val child = _users.value.find { it.id == childId }
        _users.update { usersList ->
            usersList.map { u ->
                if (u.id == childId) {
                    val newBalance = (u.pocketMoneyEuros - amountEuros).coerceAtLeast(0.0)
                    u.copy(pocketMoneyEuros = newBalance)
                } else u
            }
        }
        if (child != null) {
            logActivity("Versement d'argent 💶", "Versement de %.2f € à ${child.name}".format(amountEuros))
        }
    }

    fun addUser(name: String, role: UserRole, category: UserCategory, multiplier: Float) {
        val newUser = User(
            id = "u_${System.currentTimeMillis()}",
            name = name,
            role = role,
            category = category,
            rewardMultiplier = multiplier,
            avatarSkin = if (role == UserRole.PARENT || category == UserCategory.ADULT) "🛡️" else "⚔️"
        )
        _users.update { it + newUser }
        logActivity("Membre ajouté ➕", "Nouveau profil : $name (${category.displayName})")
    }

    fun deleteUser(userId: String) {
        val user = _users.value.find { it.id == userId }
        _users.update { list -> list.filter { it.id != userId } }
        if (user != null) {
            logActivity("Membre supprimé 🗑️", "Profil supprimé : ${user.name}")
        }
    }

    fun updateUserProfile(userId: String, name: String, category: UserCategory, multiplier: Float) {
        _users.update { usersList ->
            usersList.map { u ->
                if (u.id == userId) u.copy(name = name, category = category, rewardMultiplier = multiplier) else u
            }
        }
        logActivity("Profil mis à jour ✏️", "Profil modifié : $name")
    }

    fun resetChildProgress(childId: String) {
        _users.update { usersList ->
            usersList.map { u ->
                if (u.id == childId) {
                    u.copy(xp = 0, level = 1, coins = 0, streakCount = 0, pocketMoneyEuros = 0.0)
                } else u
            }
        }
        val child = _users.value.find { it.id == childId }
        if (child != null) {
            logActivity("Réinitialisation 🔄", "Progression réinitialisée pour ${child.name}")
        }
    }

    private fun logActivity(title: String, description: String) {
        val item = ActivityLogItem(
            id = "log_${System.currentTimeMillis()}",
            title = title,
            description = description
        )
        _activityLog.update { listOf(item) + it }
    }
}
