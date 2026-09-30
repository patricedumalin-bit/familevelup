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
import com.slydroidstudio.familevelup.data.model.User

@Composable
fun ChildQuestsTab(
    child: User,
    quests: List<Quest>,
    onCompleteQuest: (String, String) -> Unit,
    onClaimOpenBounty: (String, String) -> Unit
) {
    val visibleQuests = quests.filter { it.assigneeId == child.id || it.isRaid || it.isOpenBounty || it.assigneeId == "OPEN" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Mes Missions & Quêtes ⚔️", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (visibleQuests.isEmpty()) {
            item {
                Text("Aucune quête en cours pour le moment ! Profite de ton repos 🛡️", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        items(visibleQuests) { quest ->
            val isOpen = quest.isOpenBounty || quest.assigneeId == "OPEN"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = when {
                    quest.isRaid -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    isOpen -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    else -> CardDefaults.cardColors()
                }
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
                        if (isOpen) {
                            Text("🎯 Premier arrivé (Open Bounty)", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        }
                        if (quest.isRaid) {
                            Text("🏰 Raid Familial", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                        }
                        if (quest.proofImageUrl != null) {
                            Text("📸 Preuve photo jointe", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                    }

                    if (isOpen) {
                        Button(onClick = { onClaimOpenBounty(quest.id, child.id) }) {
                            Text("Prendre 🎯")
                        }
                    } else {
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
        }
    }
}
