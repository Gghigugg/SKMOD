package com.redfox.ilock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            (android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR).let {
                it and android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        setContent { MaterialTheme { LockScreenPreview() } }
    }
}

@Composable
private fun LockScreenPreview() {
    var drag by remember { mutableFloatStateOf(0f) }
    var unlocked by remember { mutableStateOf(false) }
    val reveal by animateFloatAsState(
        targetValue = if (unlocked) 1f else (drag / 260f).coerceIn(0f, 1f),
        animationSpec = tween(260), label = "unlockReveal"
    )
    val time = remember { SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()) }
    val date = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()) }

    Box(
        Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF263D51), Color(0xFF748B9B), Color(0xFFE2C9B3))))
    ) {
        // Soft ambient wallpaper glow behind the glass layers.
        Box(Modifier.align(Alignment.TopEnd).size(270.dp).offset(x = 80.dp, y = 120.dp)
            .blur(55.dp).background(Color(0xFFFFE7CE), CircleShape))
        Box(Modifier.align(Alignment.BottomStart).size(300.dp).offset(x = (-90).dp, y = 60.dp)
            .blur(65.dp).background(Color(0xFF9FC6D8), CircleShape))

        Column(
            Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp)
                .graphicsLayer { translationY = -reveal * size.height * 0.72f; alpha = 1f - reveal * 0.25f },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(58.dp))
            GlassPill("◉   9:41   ◉", Modifier)
            Spacer(Modifier.height(38.dp))
            Text(date, color = Color.White.copy(alpha = .94f), fontSize = 17.sp, fontWeight = FontWeight.Medium)
            Text(time, color = Color.White, fontSize = 88.sp, lineHeight = 94.sp,
                fontWeight = FontWeight.Light, letterSpacing = (-4).sp)
            Spacer(Modifier.height(18.dp))
            Text("A beautiful day begins with you", color = Color.White.copy(alpha = .82f),
                fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            GlassCard()
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                GlassCircle("⌁")
                GlassCircle("◉")
            }
            Spacer(Modifier.height(28.dp))
            Text(if (unlocked) "Preview unlocked" else "Swipe up to unlock",
                color = Color.White.copy(alpha = .96f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(24.dp))
                .pointerInput(unlocked) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, amount -> drag = (drag - amount).coerceIn(0f, 300f) },
                        onDragEnd = { if (drag > 95f) unlocked = true; drag = 0f }
                    )
                }, contentAlignment = Alignment.Center) {
                Box(Modifier.width(112.dp).height(5.dp).clip(CircleShape).background(Color.White.copy(alpha = .9f)))
            }
        }
        if (unlocked) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp),
                color = Color.White.copy(alpha = .18f), shape = RoundedCornerShape(30.dp)
            ) {
                Text("This is a UI preview — system unlock integration comes next",
                    Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    color = Color.White, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun GlassPill(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = .13f),
        shape = RoundedCornerShape(30.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .28f))
    ) {
        Text(label, Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
            color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun GlassCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = .16f),
        shape = RoundedCornerShape(30.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .34f)),
        shadowElevation = 18.dp
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("●", color = Color.White, fontSize = 10.sp)
                Spacer(Modifier.width(8.dp))
                Text("NOTIFICATIONS", color = Color.White.copy(alpha = .75f), fontSize = 10.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text("Your lock screen, reimagined", color = Color.White, fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text("Liquid Glass surfaces with soft reflections and depth.",
                color = Color.White.copy(alpha = .82f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun GlassCircle(symbol: String) {
    Surface(
        modifier = Modifier.size(54.dp),
        color = Color.White.copy(alpha = .17f),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .32f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(symbol, color = Color.White, fontSize = 22.sp)
        }
    }
}
