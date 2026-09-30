package com.slydroidstudio.familevelup.ui.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slydroidstudio.familevelup.data.model.Quest
import com.slydroidstudio.familevelup.data.model.QuestStatus
import com.slydroidstudio.familevelup.data.model.User
import com.slydroidstudio.familevelup.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    users: List<User>,
    quests: List<Quest>,
    onAddQuest: (String, Int, Int, String) -> Unit,
    onValidateQuest: (String) -> Unit,
    onRequestRevision: (String) -> Unit,
    onLogout: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newXp by remember { mutableStateOf("15") }
    var newCoins by remember { mutableStateOf("15") }
    val children = users.filter { it.role == UserRole.CHILD }
    var selectedChildId by remember { mutableStateOf(children.firstOrNull()?.id ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tableau de Bord Parent 🛡️") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Changer de profil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Text("+", fontSize = 24.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Progression des Enfants",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            children.forEach { child ->
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
                        Column {
                            Text(child.name, fontWeight = FontWeight.Bold)
                            Text("Niveau ${child.level} • ${child.xp} XP", fontSize = 14.sp)
                        }
                        Badge {
                            Text("🪙 ${child.coins}")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "File de validation des Quêtes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(quests) { quest ->
                    val assignee = users.find { it.id == quest.assigneeId }?.name ?: "Enfant"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
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
                            Text("Attribué à : $assignee • 🎁 ${quest.xpReward} XP / ${quest.coinReward} pièces", fontSize = 13.sp)

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

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Nouvelle Quête") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Intitulé de la tâche") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newXp,
                            onValueChange = { newXp = it },
                            label = { Text("Récompense XP") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newCoins,
                            onValueChange = { newCoins = it },
                            label = { Text("Récompense Pièces") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Attribuer à :", fontWeight = FontWeight.Medium)
                        children.forEach { child ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = selectedChildId == child.id,
                                    onClick = { selectedChildId = child.id }
                                )
                                Text(child.name)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (newTitle.isNotBlank()) {
                            onAddQuest(
                                newTitle,
                                newXp.toIntOrNull() ?: 15,
                                newCoins.toIntOrNull() ?: 15,
                                selectedChildId
                            )
                            newTitle = ""
                            showAddDialog = false
                        }
                    }) {
                        Text("Créer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
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
