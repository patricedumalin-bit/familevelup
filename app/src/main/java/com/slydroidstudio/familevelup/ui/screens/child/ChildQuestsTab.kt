package com.slydroidstudio.familevelup.ui.screens.child

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slydroidstudio.familevelup.R
import com.slydroidstudio.familevelup.data.model.Quest
import com.slydroidstudio.familevelup.data.model.QuestStatus
import com.slydroidstudio.familevelup.data.model.User

fun getQuestIllustrationRes(title: String): Int? {
    val t = title.lowercase()
    return when {
        t.contains("ranger") || t.contains("jouet") -> R.drawable.q_ranger_jouets
        t.contains("lit") -> R.drawable.q_faire_lit
        t.contains("dent") || t.contains("brosser") -> R.drawable.q_brosser_dents
        t.contains("habiller") || t.contains("vetement") -> R.drawable.q_habiller
        t.contains("table") -> R.drawable.q_mettre_table
        t.contains("main") -> R.drawable.q_laver_mains
        t.contains("livre") || t.contains("lire") -> R.drawable.q_lire_livre
        t.contains("cuisiner") || t.contains("cuisine") || t.contains("repas") || t.contains("dîner") -> R.drawable.q_cuisiner
        t.contains("plante") || t.contains("arroser") -> R.drawable.q_arroser_plantes
        t.contains("cartable") || t.contains("ecole") -> R.drawable.q_cartable
        else -> null
    }
}

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
            val displayXp = (quest.xpReward * child.rewardMultiplier).toInt()
            val displayCoins = (quest.coinReward * child.rewardMultiplier).toInt()

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
                                .alpha(0.85f)
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.horizontalGradient(
                                        0.0f to MaterialTheme.colorScheme.surface.copy(alpha = 0.0f),
                                        0.75f to MaterialTheme.colorScheme.surface.copy(alpha = 0.0f),
                                        0.88f to MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                        1.0f to MaterialTheme.colorScheme.surface
                                    )
                                )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(0.65f)) {
                            Text(
                                text = quest.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🎁 +$displayXp XP • +$displayCoins pièces", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            if (isOpen) {
                                Text("🎯 Premier arrivé (Open Bounty)", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            if (quest.isRaid) {
                                Text("🏰 Raid Familial", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                            if (quest.proofImageUrl != null) {
                                Text("📸 Preuve photo jointe", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, maxLines = 1)
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier.weight(0.35f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            if (isOpen) {
                                FilledTonalButton(
                                    onClick = { onClaimOpenBounty(quest.id, child.id) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Prendre", fontSize = 12.sp, maxLines = 1)
                                }
                            } else {
                                when (quest.status) {
                                    QuestStatus.PENDING -> {
                                        FilledTonalButton(
                                            onClick = {
                                                onCompleteQuest(quest.id, "photo_preuve_${System.currentTimeMillis()}.jpg")
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                        ) {
                                            Text("Terminer", fontSize = 12.sp, maxLines = 1)
                                        }
                                    }
                                    QuestStatus.COMPLETED -> {
                                        Text("En attente", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                                    }
                                    QuestStatus.VALIDATED -> {
                                        Text("Validé ✅", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                                    }
                                    QuestStatus.NEEDS_REVISION -> {
                                        FilledTonalButton(
                                            onClick = { onCompleteQuest(quest.id, "photo_revision_${System.currentTimeMillis()}.jpg") },
                                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("Refaire", fontSize = 12.sp, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
