package com.slydroidstudio.familevelup.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun AvatarView(
    seed: String,
    style: String = "bottts",
    size: Dp = 96.dp
) {
    val context = LocalContext.current
    val url = "https://api.dicebear.com/7.x/$style/svg?seed=$seed"
    var isError by remember { mutableStateOf(false) }

    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        if (isError) {
            Text("🤖", fontSize = (size.value * 0.5f).sp)
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(url)
                    .decoderFactory(SvgDecoder.Factory())
                    .crossfade(true)
                    .build(),
                contentDescription = "Avatar vectoriel",
                onError = { isError = true },
                modifier = Modifier.size(size)
            )
        }
    }
}
