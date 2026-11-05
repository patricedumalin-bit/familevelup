package com.slydroidstudio.familevelup.ui.screens.child

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slydroidstudio.familevelup.data.model.Achievement
import com.slydroidstudio.familevelup.data.model.User

fun getHonoraryTitle(level: Int): String {
    return when {
        level >= 5 -> "Légende Vivante 🌟"
        level == 4 -> "Seigneur des Tâches 👑"
        level == 3 -> "Mage de l'Ordre 🧙‍♂️"
        level == 2 -> "Chevalier du Foyer ⚔️"
        else -> "Apprenti Héros 🛡️"
    }
}

@Composable
fun ChildProfileTab(
    child: User,
    onUpdateAvatarSkin: (String, String) -> Unit
) {
    var showEmojiDialog by remember { mutableStateOf(false) }
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }

    val emojis = listOf(
        "🦸", "🧙", "⚔️", "🛡️", "🦁", "🐯",
        "🚀", "🤖", "🦄", "👑", "🦊", "🐼",
        "🐲", "👽", "🌟", "🔥", "⚡", "💎"
    )

    val achievements = listOf(
        Achievement("a1", "Premier pas", "Gagne tes premiers XP en accomplissant une quête.", "🌟", child.xp > 0),
        Achievement("a2", "Série de feu", "Atteins 3 jours de streak consécutifs.", "🔥", child.streakCount >= 3),
        Achievement("a3", "Économe", "Amasse 50 pièces d'or ou plus dans ta bourse.", "🪙", child.coins >= 50),
        Achievement("a4", "Héros du foyer", "Atteins le niveau 2 ou plus en cumulant de l'XP.", "🧙‍♂️", child.level >= 2),
        Achievement("a5", "Maître du rangement", "Valide 5 quêtes de maison.", "🧹", child.xp >= 100)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Compact Character Sheet Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { showEmojiDialog = true },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text("⚙️", fontSize = 18.sp)
                    }

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clickable { showEmojiDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(child.avatarSkin.ifBlank { "🦸" }, fontSize = 42.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(child.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(getHonoraryTitle(child.level), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(child.category.displayName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("🔥 ${child.streakCount} j.", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("🪙 ${child.coins} p.", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    if (child.rewardChoice == "CANDY") {
                                        Text("🍬 Bonbons", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    } else {
                                        Text("🏦 %.2f €".format(child.pocketMoneyEuros), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { (child.xp % 100) / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Niveau ${child.level}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${child.xp} XP total (${100 - (child.xp % 100)} XP avant niv. ${child.level + 1})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Mes Trophées & Badges 🏆", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(achievements) { ach ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedAchievement = ach },
                colors = if (ach.isUnlocked) {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                } else {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Grayed out if locked, bright if unlocked
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .alpha(if (ach.isUnlocked) 1.0f else 0.4f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(ach.icon, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            ach.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (ach.isUnlocked) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            if (ach.isUnlocked) "Débloqué ✅" else "Verrouillé 🔒 (Clique pour voir)",
                            fontSize = 11.sp,
                            color = if (ach.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }

    // Popup Detail Dialog for clicked Achievement / Trophy
    if (selectedAchievement != null) {
        val ach = selectedAchievement!!
        AlertDialog(
            onDismissRequest = { selectedAchievement = null },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(ach.icon, fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(ach.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center)
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (ach.isUnlocked) "Statut : Débloqué avec succès ! 🎉" else "Statut : Verrouillé 🔒",
                        fontWeight = FontWeight.Bold,
                        color = if (ach.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Condition d'obtention :\n${ach.description}",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedAchievement = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fermer")
                }
            }
        )
    }

    if (showEmojiDialog) {
        AlertDialog(
            onDismissRequest = { showEmojiDialog = false },
            title = { Text("Choisir mon Avatar Emojis ⭐") },
            text = {
                Box(modifier = Modifier.height(200.dp)) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(emojis) { emoji ->
                            Button(
                                onClick = {
                                    onUpdateAvatarSkin(child.id, emoji)
                                    showEmojiDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (child.avatarSkin == emoji) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.size(52.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(emoji, fontSize = 24.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showEmojiDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }
}
