package com.slydroidstudio.familevelup.ui.screens.child

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.slydroidstudio.familevelup.data.model.Quest
import com.slydroidstudio.familevelup.data.model.RedeemedReward
import com.slydroidstudio.familevelup.data.model.Reward
import com.slydroidstudio.familevelup.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDashboardScreen(
    child: User,
    quests: List<Quest>,
    rewards: List<Reward>,
    redeemedRewards: List<RedeemedReward>,
    onCompleteQuest: (String, String) -> Unit,
    onClaimOpenBounty: (String, String) -> Unit,
    onBuyReward: (String, Reward) -> Unit,
    onUpdateAvatarSkin: (String, String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${child.avatarSkin} FamiLevelUp - ${child.name}") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Changer")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("🛡️") },
                    label = { Text("Profil") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("⚔️") },
                    label = { Text("Quêtes") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text("🎁") },
                    label = { Text("Boutique") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> ChildProfileTab(child = child, onUpdateAvatarSkin = onUpdateAvatarSkin)
                1 -> ChildQuestsTab(child = child, quests = quests, onCompleteQuest = onCompleteQuest, onClaimOpenBounty = onClaimOpenBounty)
                2 -> ChildShopTab(child = child, rewards = rewards, redeemedRewards = redeemedRewards, onBuyReward = onBuyReward)
            }
        }
    }
}
