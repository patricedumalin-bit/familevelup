package com.slydroidstudio.familevelup.ui.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slydroidstudio.familevelup.data.model.Quest
import com.slydroidstudio.familevelup.data.model.QuestStatus
import com.slydroidstudio.familevelup.data.model.User
import com.slydroidstudio.familevelup.data.model.UserRole

@Composable
fun ParentQuestsTab(
    users: List<User>,
    quests: List<Quest>,
    onValidateQuest: (String) -> Unit,
    onRequestRevision: (String) -> Unit
) {
    val totalValidatedQuests = quests.count { it.status == QuestStatus.VALIDATED }
    val totalXp = users.filter { it.role == UserRole.CHILD }.sumOf { it.xp }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🛡️ Hub de Validation", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Statistiques : $totalValidatedQuests quêtes validées • $totalXp XP cumulé", fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Quêtes & Raids en cours 📋", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (quests.isEmpty()) {
            item {
                Text("Aucune quête créée. Appuie sur le bouton + pour en ajouter !", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        items(quests) { quest ->
            val assignee = when {
                quest.isRaid -> "🏰 Toute la famille (Raid)"
                quest.isOpenBounty -> "🎯 Premier arrivé (Open Bounty)"
                else -> users.find { it.id == quest.assigneeId }?.name ?: "Enfant"
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = when {
                    quest.isRaid -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    quest.isOpenBounty -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    else -> CardDefaults.cardColors()
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(quest.title, fontWeight = FontWeight.Bold)
                        StatusBadge(quest.status)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Cible : $assignee • Fréquence : ${quest.frequency.name} • 🎁 ${quest.xpReward} XP / ${quest.coinReward} pièces", fontSize = 13.sp)
                    if (quest.validityDays != -1) {
                        Text("Validité : ${quest.validityDays} jour(s)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Text("Validité : Illimitée", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    if (quest.status == QuestStatus.COMPLETED) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { onRequestRevision(quest.id) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("À refaire")
                            }
                            Button(
                                onClick = { onValidateQuest(quest.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Valider ✔️")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: QuestStatus) {
    val (text, color) = when (status) {
        QuestStatus.PENDING -> "En cours" to MaterialTheme.colorScheme.outline
        QuestStatus.COMPLETED -> "À valider 📷" to MaterialTheme.colorScheme.tertiary
        QuestStatus.VALIDATED -> "Validé ✅" to MaterialTheme.colorScheme.primary
        QuestStatus.NEEDS_REVISION -> "À rectifier ⚠️" to MaterialTheme.colorScheme.error
    }
    Text(text, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
}
