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

class FamilyViewModel : ViewModel() {

    private val _users = MutableStateFlow(
        listOf(
            User(id = "p1", name = "Patrice", role = UserRole.PARENT, category = UserCategory.ADULT, avatarSkin = "🛡️"),
            User(id = "p2", name = "Diane", role = UserRole.PARENT, category = UserCategory.ADULT, avatarSkin = "👑"),
            User(id = "c1", name = "Caleb (6 ans)", role = UserRole.CHILD, xp = 30, level = 1, coins = 25, avatarSkin = "🦸", category = UserCategory.CHILD_6_10, rewardMultiplier = 1.0f, rewardChoice = "CANDY"),
            User(id = "c2", name = "Luna (6 ans)", role = UserRole.CHILD, xp = 30, level = 1, coins = 25, avatarSkin = "🦄", category = UserCategory.CHILD_6_10, rewardMultiplier = 1.0f, rewardChoice = "MONEY")
        )
    )
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _quests = MutableStateFlow(
        listOf(
            // Parents' Quests
            Quest(id = "q_p1_1", title = "Préparer le dîner de la semaine", xpReward = 50, coinReward = 50, assigneeId = "p1", status = QuestStatus.PENDING, frequency = QuestFrequency.WEEKLY, validityDays = 7),
            Quest(id = "q_p2_1", title = "Organiser la sortie du week-end", xpReward = 50, coinReward = 50, assigneeId = "p2", status = QuestStatus.PENDING, frequency = QuestFrequency.WEEKLY, validityDays = 7),

            // Caleb's Quests
            Quest(id = "q_c1_1", title = "Ranger ses jouets", xpReward = 15, coinReward = 15, assigneeId = "c1", status = QuestStatus.PENDING, frequency = QuestFrequency.DAILY, validityDays = 1),
            Quest(id = "q_c1_2", title = "Faire son lit", xpReward = 20, coinReward = 20, assigneeId = "c1", status = QuestStatus.PENDING, frequency = QuestFrequency.DAILY, validityDays = 1),
            Quest(id = "q_c1_3", title = "Se brosser les dents", xpReward = 10, coinReward = 10, assigneeId = "c1", status = QuestStatus.PENDING, frequency = QuestFrequency.DAILY, validityDays = 1),
            Quest(id = "q_c1_4", title = "S'habiller tout seul", xpReward = 15, coinReward = 15, assigneeId = "c1", status = QuestStatus.PENDING, frequency = QuestFrequency.DAILY, validityDays = 1),
            Quest(id = "q_c1_5", title = "Préparer son cartable", xpReward = 25, coinReward = 25, assigneeId = "c1", status = QuestStatus.PENDING, frequency = QuestFrequency.WEEKLY, validityDays = 7),

            // Luna's Quests
            Quest(id = "q_c2_1", title = "Ranger ses jouets", xpReward = 15, coinReward = 15, assigneeId = "c2", status = QuestStatus.PENDING, frequency = QuestFrequency.DAILY, validityDays = 1),
            Quest(id = "q_c2_2", title = "Mettre la table", xpReward = 20, coinReward = 20, assigneeId = "c2", status = QuestStatus.PENDING, frequency = QuestFrequency.DAILY, validityDays = 1),
            Quest(id = "q_c2_3", title = "Se brosser les dents", xpReward = 10, coinReward = 10, assigneeId = "c2", status = QuestStatus.PENDING, frequency = QuestFrequency.DAILY, validityDays = 1),
            Quest(id = "q_c2_4", title = "Arroser les plantes", xpReward = 20, coinReward = 20, assigneeId = "c2", status = QuestStatus.PENDING, frequency = QuestFrequency.WEEKLY, validityDays = 7),
            Quest(id = "q_c2_5", title = "Lire un livre", xpReward = 15, coinReward = 15, assigneeId = "c2", status = QuestStatus.PENDING, frequency = QuestFrequency.DAILY, validityDays = 1),

            // Shared / Family Quests & Raids
            Quest(id = "q_shared_1", title = "Cuisiner en famille", xpReward = 40, coinReward = 40, assigneeId = "OPEN", status = QuestStatus.PENDING, isOpenBounty = true, validityDays = 3),
            Quest(id = "raid_weekend", title = "🏰 Raid : Nettoyage de la chambre & du salon", xpReward = 80, coinReward = 80, assigneeId = "FAMILY", status = QuestStatus.PENDING, isRaid = true, validityDays = 2)
        )
    )
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    private val _rewards = MutableStateFlow(
        listOf(
            Reward(id = "r1", title = "30 min de dessin animé", cost = 40, description = "Regarde ton dessin animé préféré."),
            Reward(id = "r2", title = "Choisir le dessert", cost = 60, description = "Choisis le dessert du soir !"),
            Reward(id = "r3", title = "Argent de poche (2€)", cost = 100, description = "Convertis tes pièces en vrai argent de poche."),
            Reward(id = "r_candy", title = "Bonbon magique 🍬 (2€)", cost = 100, description = "Convertis tes pièces en bonbon (1 bonbon = 2€).")
        )
    )
    val rewards: StateFlow<List<Reward>> = _rewards.asStateFlow()

    private val _redeemedRewards = MutableStateFlow<List<RedeemedReward>>(emptyList())
    val redeemedRewards: StateFlow<List<RedeemedReward>> = _redeemedRewards.asStateFlow()

    private val _activityLog = MutableStateFlow<List<ActivityLogItem>>(
        listOf(
            ActivityLogItem(id = "log_1", title = "Famille Dumalin 🏰", description = "Planning de la semaine initialisé pour Patrice, Diane, Caleb & Luna !")
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
        logActivity("Nouvelle quête 📜", "Tâche créée : $title")
    }

    fun deleteQuest(questId: String) {
        val quest = _quests.value.find { it.id == questId }
        _quests.update { list -> list.filter { it.id != questId } }
        if (quest != null) {
            logActivity("Quête supprimée 🗑️", "Quête supprimée : ${quest.title}")
        }
    }

    fun updateQuest(questId: String, title: String, xp: Int, coins: Int) {
        _quests.update { list ->
            list.map { q ->
                if (q.id == questId) q.copy(title = title, xpReward = xp, coinReward = coins) else q
            }
        }
        logActivity("Quête modifiée ✏️", "Quête mise à jour : $title")
    }

    fun addRaid(title: String, xp: Int, coins: Int) {
        val newRaid = Quest(
            id = "raid_${System.currentTimeMillis()}",
            title = "🏰 Raid : $title",
            xpReward = xp,
            coinReward = coins,
            assigneeId = "FAMILY",
            status = QuestStatus.PENDING,
            isRaid = true,
            frequency = QuestFrequency.WEEKLY,
            validityDays = 2,
            createdAt = System.currentTimeMillis()
        )
        _quests.update { it + newRaid }
        logActivity("Nouveau Raid Familial 🏰", "Raid créé : $title")
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
        if (quest.status == QuestStatus.COMPLETED || quest.status == QuestStatus.PENDING) {
            if (quest.isRaid) {
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
                val assignee = _users.value.find { it.id == quest.assigneeId }
                _users.update { usersList ->
                    usersList.map { u ->
                        if (u.id == quest.assigneeId) {
                            if (u.category == UserCategory.ADULT) {
                                u.copy(xp = u.xp + quest.xpReward)
                            } else {
                                val rewardedXp = (quest.xpReward * u.rewardMultiplier).toInt()
                                val rewardedCoins = (quest.coinReward * u.rewardMultiplier).toInt()
                                val newStreak = u.streakCount + 1
                                val streakBonusCoins = if (newStreak % 3 == 0) 15 else 0
                                val newXp = u.xp + rewardedXp
                                val newCoins = (u.coins + rewardedCoins + streakBonusCoins).coerceAtLeast(0)
                                val newLevel = 1 + (newXp / 100)
                                val newPocketMoney = if (u.rewardChoice == "MONEY" && newCoins >= 100) {
                                    u.pocketMoneyEuros + 2.0
                                } else {
                                    u.pocketMoneyEuros
                                }
                                val finalCoins = if (u.rewardChoice == "MONEY" && newCoins >= 100) newCoins - 100 else newCoins
                                u.copy(
                                    xp = newXp,
                                    level = newLevel,
                                    coins = finalCoins,
                                    streakCount = newStreak,
                                    pocketMoneyEuros = newPocketMoney
                                )
                            }
                        } else u
                    }
                }
                logActivity("Quête validée ✅", "Quête '${quest.title}' validée pour ${assignee?.name ?: "le membre"}")
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
        logActivity("À rectifier ⚠️", "Le parent a demandé une correction sur une quête.")
    }

    fun buyReward(userId: String, reward: Reward) {
        val user = _users.value.find { it.id == userId } ?: return
        if (user.coins >= reward.cost) {
            _users.update { usersList ->
                usersList.map { u ->
                    if (u.id == userId) u.copy(coins = u.coins - reward.cost) else u
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
            logActivity("Récompense délivrée 🎟️", "Bon validé pour ${coupon.childName} : ${coupon.rewardTitle}")
        }
    }

    fun consumeReward(couponId: String) {
        _redeemedRewards.update { list ->
            list.filter { it.id != couponId }
        }
        logActivity("Récompense consommée ✅", "Bon utilisé.")
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

    fun addUser(name: String, role: UserRole, category: UserCategory, multiplier: Float, rewardChoice: String = "MONEY") {
        val newUser = User(
            id = "u_${System.currentTimeMillis()}",
            name = name,
            role = role,
            category = category,
            rewardMultiplier = multiplier,
            rewardChoice = rewardChoice,
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

    fun updateUserProfile(userId: String, name: String, category: UserCategory, multiplier: Float, rewardChoice: String = "MONEY") {
        _users.update { usersList ->
            usersList.map { u ->
                if (u.id == userId) u.copy(name = name, category = category, rewardMultiplier = multiplier, rewardChoice = rewardChoice) else u
            }
        }
        logActivity("Profil mis à jour ✏️", "Profil modifié : $name")
    }

    fun addReward(title: String, cost: Int, description: String) {
        val newReward = Reward(
            id = "r_${System.currentTimeMillis()}",
            title = title,
            cost = cost,
            description = description
        )
        _rewards.update { it + newReward }
        logActivity("Nouvelle Récompense 🎁", "Récompense ajoutée : $title ($cost pièces)")
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
