package com.slydroidstudio.familevelup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AvatarConfig(
    val hairStyle: String = "Classic",
    val eyewear: String = "None",
    val shirtColor: Color = Color(0xFF3F51B5),
    val skinColor: Color = Color(0xFFFFCC80)
)

@Composable
fun ModularAvatar(
    config: AvatarConfig,
    size: Dp = 96.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
        val radius = size.toPx() / 2.5f

        // 1. Body / T-shirt (Flat design rectangle / shoulders)
        drawRect(
            color = config.shirtColor,
            topLeft = Offset(center.x - radius * 0.9f, center.y + radius * 0.4f),
            size = Size(radius * 1.8f, radius * 1.2f)
        )

        // 2. Head / Face
        drawCircle(
            color = config.skinColor,
            radius = radius * 0.8f,
            center = center
        )

        // 3. Eyes
        drawCircle(
            color = Color.DarkGray,
            radius = radius * 0.12f,
            center = Offset(center.x - radius * 0.3f, center.y - radius * 0.1f)
        )
        drawCircle(
            color = Color.DarkGray,
            radius = radius * 0.12f,
            center = Offset(center.x + radius * 0.3f, center.y - radius * 0.1f)
        )

        // 4. Hair style
        when (config.hairStyle) {
            "Spiky" -> {
                drawRect(
                    color = Color(0xFF5D4037),
                    topLeft = Offset(center.x - radius * 0.85f, center.y - radius * 1.1f),
                    size = Size(radius * 1.7f, radius * 0.5f)
                )
            }
            "Crown" -> {
                drawCircle(
                    color = Color(0xFFFFD700),
                    radius = radius * 0.4f,
                    center = Offset(center.x, center.y - radius * 0.7f)
                )
            }
            "Cap" -> {
                drawRect(
                    color = Color(0xFFE91E63),
                    topLeft = Offset(center.x - radius * 0.9f, center.y - radius * 0.9f),
                    size = Size(radius * 1.8f, radius * 0.4f)
                )
            }
            else -> { // Classic
                drawCircle(
                    color = Color(0xFF3E2723),
                    radius = radius * 0.75f,
                    center = Offset(center.x, center.y - radius * 0.3f)
                )
            }
        }

        // 5. Eyewear
        if (config.eyewear == "Cool Glasses") {
            drawRect(
                color = Color.Black,
                topLeft = Offset(center.x - radius * 0.7f, center.y - radius * 0.25f),
                size = Size(radius * 0.6f, radius * 0.3f)
            )
            drawRect(
                color = Color.Black,
                topLeft = Offset(center.x + radius * 0.1f, center.y - radius * 0.25f),
                size = Size(radius * 0.6f, radius * 0.3f)
            )
        } else if (config.eyewear == "Round Glasses") {
            drawCircle(
                color = Color.Black,
                radius = radius * 0.25f,
                center = Offset(center.x - radius * 0.3f, center.y - radius * 0.1f)
            )
            drawCircle(
                color = Color.Black,
                radius = radius * 0.25f,
                center = Offset(center.x + radius * 0.3f, center.y - radius * 0.1f)
            )
        }
    }
}
