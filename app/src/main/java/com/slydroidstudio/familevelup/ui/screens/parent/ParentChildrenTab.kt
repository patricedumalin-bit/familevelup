package com.slydroidstudio.familevelup.ui.screens.parent

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
import com.slydroidstudio.familevelup.data.model.User
import com.slydroidstudio.familevelup.data.model.UserRole

@Composable
fun ParentChildrenTab(
    users: List<User>,
    onPayoutClick: (User) -> Unit,
    onNavigateToProfiles: () -> Unit
) {
    val children = users.filter { it.role == UserRole.CHILD }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToProfiles,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Gestion des Membres & Foyers 👥", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Ajouter, modifier ou supprimer des profils", fontSize = 12.sp)
                    }
                    Text("→", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Progression des Enfants 🎮", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(children) { child ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${child.avatarSkin} ${child.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Niveau ${child.level} • ${child.xp} XP • 🔥 ${child.streakCount} j", fontSize = 13.sp)
                        Text("🏦 Cagnotte : %.2f €".format(child.pocketMoneyEuros), fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Badge {
                            Text("🪙 ${child.coins}")
                        }
                        if (child.pocketMoneyEuros > 0.0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { onPayoutClick(child) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Verser 💶", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
