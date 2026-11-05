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
import com.slydroidstudio.familevelup.data.model.User
import com.slydroidstudio.familevelup.data.model.UserCategory
import com.slydroidstudio.familevelup.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileManagementScreen(
    users: List<User>,
    onAddUser: (String, UserRole, UserCategory, Float, String) -> Unit,
    onDeleteUser: (String) -> Unit,
    onUpdateProfile: (String, String, UserCategory, Float, String) -> Unit,
    onResetProgress: (String) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }
    var confirmDeleteUser by remember { mutableStateOf<User?>(null) }
    var confirmResetUser by remember { mutableStateOf<User?>(null) }

    var nameInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(UserCategory.CHILD_6_10) }
    var multiplierInput by remember { mutableStateOf("1.0") }
    var rewardChoiceInput by remember { mutableStateOf("MONEY") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion des Profils & Foyer 👥") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Retour")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                nameInput = ""
                selectedCategory = UserCategory.CHILD_6_10
                multiplierInput = "1.0"
                rewardChoiceInput = "MONEY"
                showAddDialog = true
            }) {
                Text("+", fontSize = 24.sp)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text("Membres du Foyer :", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(users) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = if (user.role == UserRole.PARENT) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer) else CardDefaults.cardColors()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${user.avatarSkin} ${user.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Rôle : ${user.role.name} • Catégorie : ${user.category.displayName}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (user.category != UserCategory.ADULT) {
                                val rewardTypeDesc = if (user.rewardChoice == "CANDY") "🍬 Bonbons" else "💶 Argent de poche"
                                Text("Récompense : $rewardTypeDesc • ${user.rewardMultiplier}x • Niv. ${user.level} • 🪙 ${user.coins} p.", fontSize = 12.sp)
                            } else {
                                Text("Participant (Sans gain d'XP/pièces)", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Row {
                                TextButton(onClick = {
                                    editingUser = user
                                    nameInput = user.name
                                    selectedCategory = user.category
                                    multiplierInput = user.rewardMultiplier.toString()
                                    rewardChoiceInput = user.rewardChoice
                                }) {
                                    Text("Modifier ✏️")
                                }
                                if (user.role != UserRole.PARENT) {
                                    TextButton(
                                        onClick = { confirmResetUser = user },
                                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
                                    ) {
                                        Text("Reset 🔄")
                                    }
                                }
                            }
                            if (user.role != UserRole.PARENT && users.size > 2) {
                                TextButton(
                                    onClick = { confirmDeleteUser = user },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Supprimer 🗑️")
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
                title = { Text("Ajouter un membre") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Nom du membre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Catégorie de profil :", fontWeight = FontWeight.Medium)
                        UserCategory.entries.forEach { cat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat }
                                )
                                Text(cat.displayName)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (selectedCategory != UserCategory.ADULT) {
                            OutlinedTextField(
                                value = multiplierInput,
                                onValueChange = { multiplierInput = it },
                                label = { Text("Multiplicateur de récompense (ex: 1.0, 1.5)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Type de récompense (pour les plus jeunes) :", fontWeight = FontWeight.Medium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { rewardChoiceInput = "MONEY" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (rewardChoiceInput != "CANDY") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Text("💶 Argent")
                                }
                                Button(
                                    onClick = { rewardChoiceInput = "CANDY" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (rewardChoiceInput == "CANDY") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Text("🍬 Bonbon (2€=1)")
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (nameInput.isNotBlank()) {
                            val mult = multiplierInput.replace(",", ".").toFloatOrNull() ?: 1.0f
                            val role = if (selectedCategory == UserCategory.ADULT) UserRole.PARENT else UserRole.CHILD
                            onAddUser(nameInput, role, selectedCategory, mult, rewardChoiceInput)
                            showAddDialog = false
                        }
                    }) {
                        Text("Ajouter")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }

        if (editingUser != null) {
            AlertDialog(
                onDismissRequest = { editingUser = null },
                title = { Text("Modifier le profil") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Nom") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Catégorie :", fontWeight = FontWeight.Medium)
                        UserCategory.entries.forEach { cat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat }
                                )
                                Text(cat.displayName)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (selectedCategory != UserCategory.ADULT) {
                            OutlinedTextField(
                                value = multiplierInput,
                                onValueChange = { multiplierInput = it },
                                label = { Text("Multiplicateur (ex: 1.5)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Type de récompense :", fontWeight = FontWeight.Medium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { rewardChoiceInput = "MONEY" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (rewardChoiceInput != "CANDY") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Text("💶 Argent")
                                }
                                Button(
                                    onClick = { rewardChoiceInput = "CANDY" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (rewardChoiceInput == "CANDY") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Text("🍬 Bonbon (2€=1)")
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (nameInput.isNotBlank()) {
                            val mult = multiplierInput.replace(",", ".").toFloatOrNull() ?: 1.0f
                            onUpdateProfile(editingUser!!.id, nameInput, selectedCategory, mult, rewardChoiceInput)
                            editingUser = null
                        }
                    }) {
                        Text("Enregistrer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingUser = null }) {
                        Text("Annuler")
                    }
                }
            )
        }

        if (confirmDeleteUser != null) {
            AlertDialog(
                onDismissRequest = { confirmDeleteUser = null },
                title = { Text("Confirmer la suppression") },
                text = { Text("Voulez-vous vraiment supprimer ${confirmDeleteUser!!.name} du foyer ?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteUser(confirmDeleteUser!!.id)
                            confirmDeleteUser = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Supprimer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmDeleteUser = null }) {
                        Text("Annuler")
                    }
                }
            )
        }

        if (confirmResetUser != null) {
            AlertDialog(
                onDismissRequest = { confirmResetUser = null },
                title = { Text("Confirmer la réinitialisation") },
                text = { Text("Réinitialiser la progression de ${confirmResetUser!!.name} ?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onResetProgress(confirmResetUser!!.id)
                            confirmResetUser = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Réinitialiser")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmResetUser = null }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}
