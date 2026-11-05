package com.slydroidstudio.familevelup.ui.screens.parent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slydroidstudio.familevelup.data.model.Quest
import com.slydroidstudio.familevelup.data.model.QuestStatus
import com.slydroidstudio.familevelup.data.model.User
import com.slydroidstudio.familevelup.data.model.UserRole
import com.slydroidstudio.familevelup.ui.screens.child.getQuestIllustrationRes

@Composable
fun ParentQuestsTab(
    users: List<User>,
    quests: List<Quest>,
    onValidateQuest: (String) -> Unit,
    onRequestRevision: (String) -> Unit,
    onDeleteQuest: (String) -> Unit,
    onUpdateQuest: (String, String, Int, Int) -> Unit
) {
    var editingQuest by remember { mutableStateOf<Quest?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editXp by remember { mutableStateOf("") }
    var editCoins by remember { mutableStateOf("") }

    var confirmDeleteQuest by remember { mutableStateOf<Quest?>(null) }

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
                else -> users.find { it.id == quest.assigneeId }?.name ?: "Membre"
            }
            val assignedUser = users.find { it.id == quest.assigneeId }
            val isParentQuest = assignedUser?.role == UserRole.PARENT

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    val bgRes = getQuestIllustrationRes(quest.title)
                    if (bgRes != null) {
                        Image(
                            painter = painterResource(id = bgRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .alpha(0.75f)
                        )
                    }

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

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row {
                                TextButton(onClick = {
                                    editingQuest = quest
                                    editTitle = quest.title
                                    editXp = quest.xpReward.toString()
                                    editCoins = quest.coinReward.toString()
                                }) {
                                    Text("Modifier ✏️", fontSize = 12.sp)
                                }
                                TextButton(
                                    onClick = { confirmDeleteQuest = quest },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Supprimer 🗑️", fontSize = 12.sp)
                                }
                            }

                            if (quest.status == QuestStatus.COMPLETED || (isParentQuest && quest.status == QuestStatus.PENDING)) {
                                Row {
                                    if (!isParentQuest && quest.status == QuestStatus.COMPLETED) {
                                        OutlinedButton(
                                            onClick = { onRequestRevision(quest.id) },
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text("À refaire", fontSize = 12.sp)
                                        }
                                    }
                                    Button(
                                        onClick = { onValidateQuest(quest.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text(if (isParentQuest) "Terminer & Valider ✔️" else "Valider ✔️", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (editingQuest != null) {
        AlertDialog(
            onDismissRequest = { editingQuest = null },
            title = { Text("Modifier la quête") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Intitulé") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editXp,
                        onValueChange = { editXp = it },
                        label = { Text("XP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCoins,
                        onValueChange = { editCoins = it },
                        label = { Text("Pièces") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (editTitle.isNotBlank()) {
                        val xp = editXp.toIntOrNull() ?: 25
                        val coins = editCoins.toIntOrNull() ?: 25
                        onUpdateQuest(editingQuest!!.id, editTitle, xp, coins)
                        editingQuest = null
                    }
                }) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingQuest = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    if (confirmDeleteQuest != null) {
        AlertDialog(
            onDismissRequest = { confirmDeleteQuest = null },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer la quête '${confirmDeleteQuest!!.title}' ?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteQuest(confirmDeleteQuest!!.id)
                        confirmDeleteQuest = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteQuest = null }) {
                    Text("Annuler")
                }
            }
        )
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
