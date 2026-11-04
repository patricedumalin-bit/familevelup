package com.slydroidstudio.familevelup

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.slydroidstudio.familevelup.data.model.UserRole
import com.slydroidstudio.familevelup.security.BiometricAuthManager
import com.slydroidstudio.familevelup.security.BiometricState
import com.slydroidstudio.familevelup.security.PreferencesManager
import com.slydroidstudio.familevelup.security.QuestSyncWorker
import com.slydroidstudio.familevelup.ui.screens.OnboardingScreen
import com.slydroidstudio.familevelup.ui.screens.RoleSelectionScreen
import com.slydroidstudio.familevelup.ui.screens.child.ChildDashboardScreen
import com.slydroidstudio.familevelup.ui.screens.parent.ParentDashboardScreen
import com.slydroidstudio.familevelup.ui.screens.parent.ParentSettingsScreen
import com.slydroidstudio.familevelup.ui.screens.parent.ProfileManagementScreen
import com.slydroidstudio.familevelup.ui.theme.FamiLevelUpTheme
import com.slydroidstudio.familevelup.ui.theme.ThemeManager
import com.slydroidstudio.familevelup.ui.viewmodel.FamilyViewModel
import java.util.concurrent.TimeUnit

class MainActivity : FragmentActivity() {
    private val viewModel: FamilyViewModel by viewModels()
    private var isUnlocked = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enable FLAG_SECURE for security
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        ThemeManager.init(this)
        scheduleQuestWorker()
        val prefs = PreferencesManager(this)

        if (prefs.biometricEnabled && BiometricAuthManager.isBiometricAvailable(this)) {
            isUnlocked.value = false
            Handler(Looper.getMainLooper()).postDelayed({
                promptBiometric()
            }, 300)
        } else {
            isUnlocked.value = true
        }

        setContent {
            val currentGalaxyTheme by ThemeManager.currentTheme.collectAsState()

            FamiLevelUpTheme(galaxyTheme = currentGalaxyTheme) {
                val unlocked by remember { isUnlocked }
                if (!unlocked) {
                    LockScreen(onUnlockClick = { promptBiometric() })
                } else {
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
    }

    override fun onResume() {
        super.onResume()
        if (BiometricState.isActionActive()) return
        val prefs = PreferencesManager(this)
        if (prefs.biometricEnabled && !isUnlocked.value && BiometricAuthManager.isBiometricAvailable(this)) {
            Handler(Looper.getMainLooper()).postDelayed({
                promptBiometric()
            }, 200)
        }
    }

    override fun onStop() {
        super.onStop()
        val prefs = PreferencesManager(this)
        if (prefs.biometricEnabled && !BiometricState.isActionActive()) {
            isUnlocked.value = false
        }
    }

    private fun promptBiometric() {
        BiometricAuthManager.promptBiometricAuth(
            activity = this,
            title = "FamiLevelUp Verrouillé",
            subtitle = "Authentifiez-vous pour accéder à l'application.",
            onSuccess = {
                isUnlocked.value = true
            },
            onError = { _ -> }
        )
    }

    private fun authenticateParent() {
        BiometricAuthManager.promptBiometricAuth(
            activity = this,
            title = "Mode Parent FamiLevelUp",
            subtitle = "Authentification biométrique requise pour accéder au Mode Parent 🛡️",
            onSuccess = {
                isUnlocked.value = true
                val parentUser = viewModel.users.value.find { it.role == UserRole.PARENT }
                if (parentUser != null) {
                    viewModel.selectUser(parentUser)
                }
            },
            onError = { _ -> }
        )
    }

    private fun scheduleQuestWorker() {
        val workRequest = PeriodicWorkRequestBuilder<QuestSyncWorker>(12, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "QuestSyncWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@Composable
fun LockScreen(onUnlockClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Verrouillé",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "FamiLevelUp Verrouillé",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Veuillez vous authentifier pour accéder à vos quêtes et récompenses familiales.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onUnlockClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Déverrouiller")
            }
        }
    }
}
