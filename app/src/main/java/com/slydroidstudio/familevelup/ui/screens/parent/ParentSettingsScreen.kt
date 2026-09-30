package com.slydroidstudio.familevelup.ui.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentSettingsScreen(
    householdName: String,
    onUpdateHouseholdName: (String) -> Unit,
    onNavigateToProfiles: () -> Unit,
    onBack: () -> Unit
) {
    var nameInput by remember { mutableStateOf(householdName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres du Foyer ⚙️") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Nom de la famille / Foyer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Nom du foyer") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Taux de conversion des pièces", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("1 € = 20 pièces d'or (Cagnotte d'argent de poche)", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onNavigateToProfiles,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gérer les profils des enfants 👥")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    onUpdateHouseholdName(nameInput)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer les modifications")
            }
        }
    }
}
