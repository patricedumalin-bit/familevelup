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
import com.slydroidstudio.familevelup.data.model.ActivityLogItem
import com.slydroidstudio.familevelup.data.model.CouponStatus
import com.slydroidstudio.familevelup.data.model.RedeemedReward

@Composable
fun ParentActivityTab(
    redeemedRewards: List<RedeemedReward>,
    activityLog: List<ActivityLogItem>,
    onFulfillReward: (String) -> Unit,
    onConsumeReward: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (redeemedRewards.isNotEmpty()) {
            item {
                Text("🎁 Bons de récompenses achetés :", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(redeemedRewards) { coupon ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("🎁 ${coupon.rewardTitle}", fontWeight = FontWeight.Bold)
                            Text("Demandé par : ${coupon.childName} (🪙 ${coupon.cost} pièces)", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (coupon.status == CouponStatus.PENDING_PARENT_APPROVAL) "Statut : En attente de délivrance ⏳" else "Statut : À utiliser par l'enfant 🎟️",
                                fontSize = 11.sp,
                                color = if (coupon.status == CouponStatus.PENDING_PARENT_APPROVAL) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (coupon.status == CouponStatus.PENDING_PARENT_APPROVAL) {
                            Button(onClick = { onFulfillReward(coupon.id) }) {
                                Text("Délivrer ✔️")
                            }
                        } else {
                            Button(
                                onClick = { onConsumeReward(coupon.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Consommer 🗑️")
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            Text("📜 Journal d'activité récent :", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (activityLog.isEmpty()) {
            item {
                Text("Aucune activité récente.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        items(activityLog) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(log.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(log.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
