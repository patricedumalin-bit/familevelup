package com.slydroidstudio.familevelup.ui.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slydroidstudio.familevelup.data.model.ActivityLogItem
import com.slydroidstudio.familevelup.data.model.Quest
import com.slydroidstudio.familevelup.data.model.QuestFrequency
import com.slydroidstudio.familevelup.data.model.QuestStatus
import com.slydroidstudio.familevelup.data.model.RedeemedReward
import com.slydroidstudio.familevelup.data.model.User
import com.slydroidstudio.familevelup.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    users: List<User>,
    quests: List<Quest>,
    redeemedRewards: List<RedeemedReward>,
    activityLog: List<ActivityLogItem>,
    householdName: String,
    onAddQuest: (String, Int, Int, String, QuestFrequency, Boolean, Int) -> Unit,
    onAddRaid: (String, Int, Int) -> Unit,
    onValidateQuest: (String) -> Unit,
    onRequestRevision: (String) -> Unit,
    onFulfillReward: (String) -> Unit,
    onConsumeReward: (String) -> Unit,
    onPayoutPocketMoney: (String, Double) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfiles: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newXp by remember { mutableStateOf("25") }
    var newCoins by remember { mutableStateOf("25") }

    val children = users.filter { it.role == UserRole.CHILD }

    // Dropdown states
    val targetOptions = children.map { it.name } + listOf("🏰 Raid Familial", "🎯 Premier arrivé (Open Bounty)")
    var selectedTargetText by remember { mutableStateOf(children.firstOrNull()?.name ?: "") }

    val frequencyOptions = listOf("Quotidienne", "Hebdomadaire", "Ponctuelle")
    var selectedFrequencyText by remember { mutableStateOf("Quotidienne") }

    val validityOptions = listOf("1 jour", "7 jours", "15 jours", "30 jours", "Illimitée")
    var selectedValidityText by remember { mutableStateOf("7 jours") }

    var childForPayout by remember { mutableStateOf<User?>(null) }
    var payoutAmountInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛡️ $householdName (Mode Parent)") },
                actions = {
                    TextButton(onClick = onNavigateToSettings) {
                        Text("Paramètres ⚙️")
                    }
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
                    icon = { Text("📋") },
                    label = { Text("Quêtes") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("👥") },
                    label = { Text("Enfants") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text("🎁") },
                    label = { Text("Bons & Journal") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Text("+", fontSize = 24.sp)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> ParentQuestsTab(
                    users = users,
                    quests = quests,
                    onValidateQuest = onValidateQuest,
                    onRequestRevision = onRequestRevision
                )
                1 -> ParentChildrenTab(
                    users = users,
                    onPayoutClick = { child ->
                        childForPayout = child
                        payoutAmountInput = "%.2f".format(child.pocketMoneyEuros).replace(",", ".")
                    },
                    onNavigateToProfiles = onNavigateToProfiles
                )
                2 -> ParentActivityTab(
                    redeemedRewards = redeemedRewards,
                    activityLog = activityLog,
                    onFulfillReward = onFulfillReward,
                    onConsumeReward = onConsumeReward
                )
            }
        }

        if (childForPayout != null) {
            AlertDialog(
                onDismissRequest = { childForPayout = null },
                title = { Text("Verser de l'argent de poche 💶") },
                text = {
                    Column {
                        Text("Enfant : ${childForPayout!!.name}")
                        Text("Solde actuel de la cagnotte : %.2f €".format(childForPayout!!.pocketMoneyEuros))
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = payoutAmountInput,
                            onValueChange = { payoutAmountInput = it },
                            label = { Text("Montant à verser (€)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val amount = payoutAmountInput.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (amount > 0.0) {
                            onPayoutPocketMoney(childForPayout!!.id, amount)
                            childForPayout = null
                        }
                    }) {
                        Text("Confirmer le versement")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { childForPayout = null }) {
                        Text("Annuler")
                    }
                }
            )
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Nouvelle Quête / Raid") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Intitulé de la quête") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newXp,
                            onValueChange = { newXp = it },
                            label = { Text("Récompense XP") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newCoins,
                            onValueChange = { newCoins = it },
                            label = { Text("Récompense Pièces") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Target Dropdown
                        ExposedDropdownMenuSelector(
                            label = "Cible / Attribution",
                            options = targetOptions,
                            selectedOption = selectedTargetText,
                            onOptionSelected = { selectedTargetText = it }
                        )

                        // Frequency Dropdown
                        ExposedDropdownMenuSelector(
                            label = "Fréquence",
                            options = frequencyOptions,
                            selectedOption = selectedFrequencyText,
                            onOptionSelected = { selectedFrequencyText = it }
                        )

                        // Validity Dropdown (only if Ponctuelle)
                        if (selectedFrequencyText == "Ponctuelle") {
                            ExposedDropdownMenuSelector(
                                label = "Durée de validité",
                                options = validityOptions,
                                selectedOption = selectedValidityText,
                                onOptionSelected = { selectedValidityText = it }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (newTitle.isNotBlank()) {
                            val xp = newXp.toIntOrNull() ?: 25
                            val coins = newCoins.toIntOrNull() ?: 25

                            when {
                                selectedTargetText.contains("Raid Familial") -> {
                                    onAddRaid(newTitle, xp, coins)
                                }
                                selectedTargetText.contains("Premier arrivé") -> {
                                    val freq = when (selectedFrequencyText) {
                                        "Hebdomadaire" -> QuestFrequency.WEEKLY
                                        "Ponctuelle" -> QuestFrequency.ONE_TIME
                                        else -> QuestFrequency.DAILY
                                    }
                                    val days = when (selectedValidityText) {
                                        "1 jour" -> 1
                                        "7 jours" -> 7
                                        "15 jours" -> 15
                                        "30 jours" -> 30
                                        else -> -1
                                    }
                                    onAddQuest(newTitle, xp, coins, "OPEN", freq, true, days)
                                }
                                else -> {
                                    val matchedChild = children.find { it.name == selectedTargetText }
                                    val childId = matchedChild?.id ?: (children.firstOrNull()?.id ?: "c1")
                                    val freq = when (selectedFrequencyText) {
                                        "Hebdomadaire" -> QuestFrequency.WEEKLY
                                        "Ponctuelle" -> QuestFrequency.ONE_TIME
                                        else -> QuestFrequency.DAILY
                                    }
                                    val days = when (selectedValidityText) {
                                        "1 jour" -> 1
                                        "7 jours" -> 7
                                        "15 jours" -> 15
                                        "30 jours" -> 30
                                        else -> -1
                                    }
                                    onAddQuest(newTitle, xp, coins, childId, freq, false, days)
                                }
                            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
