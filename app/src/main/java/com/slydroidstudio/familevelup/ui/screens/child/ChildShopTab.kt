package com.slydroidstudio.familevelup.ui.screens.child

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
import com.slydroidstudio.familevelup.data.model.CouponStatus
import com.slydroidstudio.familevelup.data.model.RedeemedReward
import com.slydroidstudio.familevelup.data.model.Reward
import com.slydroidstudio.familevelup.data.model.User

@Composable
fun ChildShopTab(
    child: User,
    rewards: List<Reward>,
    redeemedRewards: List<RedeemedReward>,
    onBuyReward: (String, Reward) -> Unit
) {
    val myCoupons = redeemedRewards.filter { it.childId == child.id }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (myCoupons.isNotEmpty()) {
            item {
                Text("Mes Bons de Récompenses 🎟️", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(myCoupons) { coupon ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("🎟️ ${coupon.rewardTitle}", fontWeight = FontWeight.Bold)
                            Text("Coût : 🪙 ${coupon.cost} pièces", fontSize = 12.sp)
                        }
                        if (coupon.status == CouponStatus.READY_TO_USE) {
                            Text("À utiliser 🎟️", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        } else {
                            Text("En attente de délivrance ⏳", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            Text("Boutique de Récompenses 🎁", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Mes pièces disponibles : 🪙 ${child.coins}", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(rewards) { reward ->
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
                        Text(reward.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(reward.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { onBuyReward(child.id, reward) },
                        enabled = child.coins >= reward.cost
                    ) {
                        Text("🪙 ${reward.cost}")
                    }
                }
            }
        }
    }
}
