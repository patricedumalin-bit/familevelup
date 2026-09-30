package com.slydroidstudio.familevelup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slydroidstudio.familevelup.data.model.UserRole
import com.slydroidstudio.familevelup.ui.screens.RoleSelectionScreen
import com.slydroidstudio.familevelup.ui.screens.child.ChildDashboardScreen
import com.slydroidstudio.familevelup.ui.screens.parent.ParentDashboardScreen
import com.slydroidstudio.familevelup.ui.theme.FamiLevelUpTheme
import com.slydroidstudio.familevelup.ui.viewmodel.FamilyViewModel

class MainActivity : ComponentActivity() {
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

                when {
                    currentUser == null -> {
                        RoleSelectionScreen(
                            users = users,
                            onSelectUser = { user -> viewModel.selectUser(user) }
                        )
                    }
                    currentUser?.role == UserRole.PARENT -> {
                        ParentDashboardScreen(
                            users = users,
                            quests = quests,
                            onAddQuest = { title, xp, coins, assigneeId ->
                                viewModel.addQuest(title, xp, coins, assigneeId)
                            },
                            onValidateQuest = { questId -> viewModel.validateQuest(questId) },
                            onRequestRevision = { questId -> viewModel.requestRevision(questId) },
                            onLogout = { viewModel.selectUser(null) }
                        )
                    }
                    currentUser?.role == UserRole.CHILD -> {
                        val activeChild = currentUser!!
                        ChildDashboardScreen(
                            child = activeChild,
                            quests = quests,
                            rewards = rewards,
                            onCompleteQuest = { questId, proofUrl -> viewModel.completeQuest(questId, proofUrl) },
                            onBuyReward = { userId, reward -> viewModel.buyReward(userId, reward) },
                            onLogout = { viewModel.selectUser(null) }
                        )
                    }
                }
            }
        }
    }
}
