package com.slydroidstudio.familevelup

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.slydroidstudio.familevelup.data.model.UserRole
import com.slydroidstudio.familevelup.ui.screens.OnboardingScreen
import com.slydroidstudio.familevelup.ui.screens.RoleSelectionScreen
import com.slydroidstudio.familevelup.ui.screens.child.ChildDashboardScreen
import com.slydroidstudio.familevelup.ui.screens.parent.ParentDashboardScreen
import com.slydroidstudio.familevelup.ui.screens.parent.ParentSettingsScreen
import com.slydroidstudio.familevelup.ui.screens.parent.ProfileManagementScreen
import com.slydroidstudio.familevelup.ui.theme.FamiLevelUpTheme
import com.slydroidstudio.familevelup.ui.viewmodel.FamilyViewModel

class MainActivity : FragmentActivity() {
    private val viewModel: FamilyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamiLevelUpTheme {
                val users by viewModel.users.collectAsState()
                val currentUser by viewModel.currentUser.collectAsState()
                val quests by viewModel.quests.collectAsState()
                val rewards by viewModel.rewards.collectAsState()
                val redeemedRewards by viewModel.redeemedRewards.collectAsState()
                val activityLog by viewModel.activityLog.collectAsState()
                val householdName by viewModel.householdName.collectAsState()

                var showOnboarding by remember { mutableStateOf(false) }
                var parentScreen by remember(currentUser) { mutableStateOf("dashboard") }

                when {
                    showOnboarding -> {
                        OnboardingScreen(onFinished = { showOnboarding = false })
                    }
                    currentUser == null -> {
                        RoleSelectionScreen(
                            users = users,
                            onSelectUser = { user -> viewModel.selectUser(user) },
                            onSelectParent = { authenticateParent() }
                        )
                    }
                    currentUser?.role == UserRole.PARENT -> {
                        when (parentScreen) {
                            "settings" -> {
                                ParentSettingsScreen(
                                    householdName = householdName,
                                    onUpdateHouseholdName = { name -> viewModel.updateHouseholdName(name) },
                                    onNavigateToProfiles = { parentScreen = "profiles" },
                                    onBack = { parentScreen = "dashboard" }
                                )
                            }
                            "profiles" -> {
                                ProfileManagementScreen(
                                    users = users,
                                    onAddUser = { name, role, category, mult -> viewModel.addUser(name, role, category, mult) },
                                    onDeleteUser = { id -> viewModel.deleteUser(id) },
                                    onUpdateProfile = { id, name, cat, mult -> viewModel.updateUserProfile(id, name, cat, mult) },
                                    onResetProgress = { id -> viewModel.resetChildProgress(id) },
                                    onBack = { parentScreen = "settings" }
                                )
                            }
                            else -> {
                                ParentDashboardScreen(
                                    users = users,
                                    quests = quests,
                                    redeemedRewards = redeemedRewards,
                                    activityLog = activityLog,
                                    householdName = householdName,
                                    onAddQuest = { title, xp, coins, assigneeId, frequency, isOpenBounty, validityDays ->
                                        viewModel.addQuest(title, xp, coins, assigneeId, frequency, isOpenBounty, validityDays)
                                    },
                                    onAddRaid = { title, xp, coins ->
                                        viewModel.addRaid(title, xp, coins)
                                    },
                                    onValidateQuest = { questId -> viewModel.validateQuest(questId) },
                                    onRequestRevision = { questId -> viewModel.requestRevision(questId) },
                                    onFulfillReward = { couponId -> viewModel.fulfillReward(couponId) },
                                    onConsumeReward = { couponId -> viewModel.consumeReward(couponId) },
                                    onPayoutPocketMoney = { childId, amount -> viewModel.payoutPocketMoney(childId, amount) },
                                    onNavigateToSettings = { parentScreen = "settings" },
                                    onNavigateToProfiles = { parentScreen = "profiles" },
                                    onLogout = { viewModel.selectUser(null) }
                                )
                            }
                        }
                    }
                    currentUser?.role == UserRole.CHILD -> {
                        val activeChild = currentUser!!
                        ChildDashboardScreen(
                            child = activeChild,
                            quests = quests,
                            rewards = rewards,
                            redeemedRewards = redeemedRewards,
                            onCompleteQuest = { questId, proofUrl -> viewModel.completeQuest(questId, proofUrl) },
                            onClaimOpenBounty = { questId, childId -> viewModel.claimOpenBounty(questId, childId) },
                            onBuyReward = { userId, reward -> viewModel.buyReward(userId, reward) },
                            onUpdateAvatarSkin = { userId, skin -> viewModel.updateAvatarSkin(userId, skin) },
                            onLogout = { viewModel.selectUser(null) }
                        )
                    }
                }
            }
        }
    }

    private fun authenticateParent() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val parentUser = viewModel.users.value.find { it.role == UserRole.PARENT }
                    if (parentUser != null) {
                        viewModel.selectUser(parentUser)
                    }
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Sécurité FamiLevelUp")
            .setSubtitle("Authentification biométrique requise pour accéder au Mode Parent 🛡️")
            .setNegativeButtonText("Annuler")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
