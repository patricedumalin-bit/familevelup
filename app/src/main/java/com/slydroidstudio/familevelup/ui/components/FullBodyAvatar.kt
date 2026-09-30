package com.slydroidstudio.familevelup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class FullBodyConfig(
    val outfit: String = "Hero",         // "Hero", "Knight", "Wizard", "Ninja", "Explorer"
    val headgear: String = "Crown",      // "None", "Crown", "Wizard Hat", "Helmet", "Ninja Mask"
    val handItem: String = "Sword",      // "None", "Sword", "Wand", "Shield"
    val outfitColor: Color = Color(0xFF3F51B5)
)

@Composable
fun FullBodyAvatar(
    config: FullBodyConfig,
    size: Dp = 140.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val width = size.toPx()
        val height = size.toPx()
        val centerX = width / 2f

        val skinColor = Color(0xFFFFCC80)
        val pantsColor = Color(0xFF37474F)

        // 1. Legs
        drawRect(color = pantsColor, topLeft = Offset(centerX - width * 0.18f, height * 0.65f), size = Size(width * 0.12f, height * 0.3f))
        drawRect(color = pantsColor, topLeft = Offset(centerX + width * 0.06f, height * 0.65f), size = Size(width * 0.12f, height * 0.3f))

        // 2. Torso / Outfit
        drawRect(color = config.outfitColor, topLeft = Offset(centerX - width * 0.25f, height * 0.35f), size = Size(width * 0.5f, height * 0.32f))

        // Outfit specific emblem / detail
        when (config.outfit) {
            "Knight" -> {
                drawRect(color = Color(0xFFB0BEC5), topLeft = Offset(centerX - width * 0.15f, height * 0.4f), size = Size(width * 0.3f, height * 0.15f))
            }
            "Wizard" -> {
                drawCircle(color = Color(0xFFFFD700), radius = width * 0.06f, center = Offset(centerX, height * 0.5f))
            }
            "Ninja" -> {
                drawRect(color = Color.Black, topLeft = Offset(centerX - width * 0.25f, height * 0.45f), size = Size(width * 0.5f, height * 0.08f))
            }
        }

        // 3. Arms / Hands
        drawRect(color = skinColor, topLeft = Offset(centerX - width * 0.38f, height * 0.38f), size = Size(width * 0.11f, height * 0.25f))
        drawRect(color = skinColor, topLeft = Offset(centerX + width * 0.27f, height * 0.38f), size = Size(width * 0.11f, height * 0.25f))

        // 4. Head / Face
        drawCircle(color = skinColor, radius = width * 0.22f, center = Offset(centerX, height * 0.22f))

        // Eyes
        drawCircle(color = Color.DarkGray, radius = width * 0.035f, center = Offset(centerX - width * 0.07f, height * 0.21f))
        drawCircle(color = Color.DarkGray, radius = width * 0.035f, center = Offset(centerX + width * 0.07f, height * 0.21f))

        // 5. Headgear
        when (config.headgear) {
            "Crown" -> {
                drawRect(color = Color(0xFFFFD700), topLeft = Offset(centerX - width * 0.18f, height * 0.03f), size = Size(width * 0.36f, height * 0.1f))
            }
            "Wizard Hat" -> {
                drawRect(color = Color(0xFF673AB7), topLeft = Offset(centerX - width * 0.22f, height * 0.05f), size = Size(width * 0.44f, height * 0.08f))
            }
            "Helmet" -> {
                drawRect(color = Color(0xFF78909C), topLeft = Offset(centerX - width * 0.23f, height * 0.04f), size = Size(width * 0.46f, height * 0.18f))
            }
            "Ninja Mask" -> {
                drawRect(color = Color(0xFF212121), topLeft = Offset(centerX - width * 0.23f, height * 0.15f), size = Size(width * 0.46f, height * 0.12f))
            }
        }

        // 6. Hand Item
        when (config.handItem) {
            "Sword" -> {
                drawRect(color = Color(0xFFB0BEC5), topLeft = Offset(centerX + width * 0.32f, height * 0.25f), size = Size(width * 0.05f, height * 0.35f))
            }
            "Wand" -> {
                drawRect(color = Color(0xFF8D6E63), topLeft = Offset(centerX + width * 0.35f, height * 0.3f), size = Size(width * 0.03f, height * 0.25f))
                drawCircle(color = Color(0xFFFFEB3B), radius = width * 0.04f, center = Offset(centerX + width * 0.365f, height * 0.3f))
            }
            "Shield" -> {
                drawRect(color = Color(0xFFD32F2F), topLeft = Offset(centerX - width * 0.48f, height * 0.38f), size = Size(width * 0.14f, height * 0.22f))
            }
        }
    }
}
