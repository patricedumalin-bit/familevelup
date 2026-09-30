package com.slydroidstudio.familevelup.ui.screens.child

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slydroidstudio.familevelup.data.model.Quest
import com.slydroidstudio.familevelup.data.model.QuestStatus
import com.slydroidstudio.familevelup.data.model.Reward
import com.slydroidstudio.familevelup.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDashboardScreen(
    child: User,
    quests: List<Quest>,
    rewards: List<Reward>,
    onCompleteQuest: (String, String) -> Unit,
    onBuyReward: (String, Reward) -> Unit,
    onLogout: () -> Unit
) {
    val myQuests = quests.filter { it.assigneeId == child.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quêtes de ${child.name} ⚔️") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Changer")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header Card - Level & Coins & Streak
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Niveau ${child.level}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Badge {
                                    Text("🔥 ${child.streakCount} j", fontSize = 14.sp)
                                }
                                Badge {
                                    Text("🪙 ${child.coins}", fontSize = 14.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (child.xp % 100) / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${child.xp} XP total (${100 - (child.xp % 100)} XP avant niveau ${child.level + 1})", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Mes Missions du Jour 📜", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(myQuests) { quest ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(quest.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🎁 +${quest.xpReward} XP • +${quest.coinReward} pièces", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            if (quest.proofImageUrl != null) {
                                Text("📸 Preuve photo jointe", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                        }

                        when (quest.status) {
                            QuestStatus.PENDING -> {
                                Button(onClick = {
                                    onCompleteQuest(quest.id, "photo_preuve_${System.currentTimeMillis()}.jpg")
                                }) {
                                    Text("Terminer 📷")
                                }
                            }
                            QuestStatus.COMPLETED -> {
                                Text("En attente ⏳", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                            }
                            QuestStatus.VALIDATED -> {
                                Text("Validé ✅", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            QuestStatus.NEEDS_REVISION -> {
                                Button(
                                    onClick = { onCompleteQuest(quest.id, "photo_revision_${System.currentTimeMillis()}.jpg") },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Refaire ⚠️")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Boutique de Récompenses 🎁", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(rewards) { reward ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(reward.title, fontWeight = FontWeight.Bold)
                            Text(reward.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { onBuyReward(child.id, reward) },
                            enabled = child.coins >= reward.cost
                        ) {
                            Text("🪙 ${reward.cost}")
                        }
                    }
                }
            }
        }
    }
}
