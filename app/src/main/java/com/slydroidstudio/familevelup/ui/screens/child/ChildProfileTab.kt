package com.slydroidstudio.familevelup.ui.screens.child

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slydroidstudio.familevelup.data.model.Achievement
import com.slydroidstudio.familevelup.data.model.User
import com.slydroidstudio.familevelup.ui.components.FullBodyAvatar
import com.slydroidstudio.familevelup.ui.components.FullBodyConfig

@Composable
fun ChildProfileTab(
    child: User,
    onUpdateAvatarSkin: (String, String) -> Unit
) {
    var showAvatarDialog by remember { mutableStateOf(false) }

    // Modular avatar configuration state
    var selectedOutfit by remember { mutableStateOf("Hero") }
    var selectedHeadgear by remember { mutableStateOf("Crown") }
    var selectedHandItem by remember { mutableStateOf("Sword") }
    var selectedColorIndex by remember { mutableStateOf(0) }

    val outfits = listOf("Hero", "Knight", "Wizard", "Ninja", "Explorer")
    val headgears = listOf("None", "Crown", "Wizard Hat", "Helmet", "Ninja Mask")
    val handItems = listOf("None", "Sword", "Wand", "Shield")
    val colors = listOf(Color(0xFF3F51B5), Color(0xFFE91E63), Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF00BCD4))

    val achievements = listOf(
        Achievement("a1", "Premier pas", "Gagne tes premiers XP", "🌟", child.xp > 0),
        Achievement("a2", "Série de feu", "Atteins 3 jours de streak", "🔥", child.streakCount >= 3),
        Achievement("a3", "Économe", "Possède 50 pièces ou plus", "🪙", child.coins >= 50),
        Achievement("a4", "Héros du foyer", "Atteins le niveau 2 ou plus", "🧙‍♂️", child.level >= 2)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Character Sheet Card with Full-Body Modular Vector Avatar & Gear Icon
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Gear Icon Button to open Avatar Customization Modal
                    IconButton(
                        onClick = { showAvatarDialog = true },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text("⚙️", fontSize = 20.sp)
                    }

                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FullBodyAvatar(
                            config = FullBodyConfig(
                                outfit = selectedOutfit,
                                headgear = selectedHeadgear,
                                handItem = selectedHandItem,
                                outfitColor = colors[selectedColorIndex]
                            ),
                            size = 130.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(child.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Niveau ${child.level} • ${child.category.displayName}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔥 Série", fontSize = 12.sp)
                                Text("${child.streakCount} jours", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🪙 Pièces", fontSize = 12.sp)
                                Text("${child.coins}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🏦 Cagnotte", fontSize = 12.sp)
                                Text("%.2f €".format(child.pocketMoneyEuros), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { (child.xp % 100) / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${child.xp} XP total (${100 - (child.xp % 100)} XP avant niveau ${child.level + 1})", fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Mes Trophées & Badges 🏆", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(achievements) { ach ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = if (ach.isUnlocked) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer) else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(ach.icon, fontSize = 28.sp, modifier = Modifier.padding(end = 16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ach.title, fontWeight = FontWeight.Bold)
                        Text(ach.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (ach.isUnlocked) {
                        Badge {
                            Text("Débloqué ✅")
                        }
                    } else {
                        Text("Verrouillé 🔒", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }

    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = { Text("Personnaliser mon Héros ⚙️") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Tenue :", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        outfits.forEach { o ->
                            Button(
                                onClick = { selectedOutfit = o },
                                colors = ButtonDefaults.buttonColors(containerColor = if (selectedOutfit == o) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text(o, fontSize = 11.sp)
                            }
                        }
                    }

                    Text("Couvre-chef :", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        headgears.forEach { h ->
                            Button(
                                onClick = { selectedHeadgear = h },
                                colors = ButtonDefaults.buttonColors(containerColor = if (selectedHeadgear == h) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Text(h, fontSize = 10.sp)
                            }
                        }
                    }

                    Text("Accessoire en main :", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        handItems.forEach { i ->
                            Button(
                                onClick = { selectedHandItem = i },
                                colors = ButtonDefaults.buttonColors(containerColor = if (selectedHandItem == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text(i, fontSize = 11.sp)
                            }
                        }
                    }

                    Text("Couleur principale :", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        colors.forEachIndexed { idx, col ->
                            Button(
                                onClick = { selectedColorIndex = idx },
                                colors = ButtonDefaults.buttonColors(containerColor = col),
                                modifier = Modifier.size(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                if (selectedColorIndex == idx) Text("✓", color = Color.White)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAvatarDialog = false }) {
                    Text("Valider")
                }
            }
        )
    }
}
