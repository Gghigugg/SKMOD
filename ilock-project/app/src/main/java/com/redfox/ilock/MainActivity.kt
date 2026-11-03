package com.redfox.ilock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val White = Color(0xFFFFFFFF)
private val GlassLine = White.copy(alpha = .48f)

class MainActivity : ComponentActivity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        setContent { MaterialTheme { IOSLockScreen() } }
    }
}

@Composable
private fun IOSLockScreen() {
    var dragAmount by remember { mutableFloatStateOf(0f) }
    var unlocked by remember { mutableStateOf(false) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }
    val reveal by animateFloatAsState(
        targetValue = if (unlocked) 1f else (dragAmount / 230f).coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = .82f, stiffness = 320f),
        label = "lockReveal"
    )
    val time = remember(now) { SimpleDateFormat("h:mm", Locale.getDefault()).format(Date(now)) }
    val date = remember(now) {
        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date(now))
            .replaceFirstChar { it.uppercase() }
    }

    BoxWithConstraints(
        Modifier.fillMaxSize().background(Color(0xFF8F969A))
    ) {
        val h = maxHeight
        // Soft-focus wallpaper, deliberately neutral rather than the usual blue UI.
        Box(Modifier.fillMaxSize().background(
            Brush.linearGradient(
                listOf(Color(0xFF6D7775), Color(0xFFB6B0A5), Color(0xFFE4D8C8), Color(0xFF858D8A))
            )
        ))
        Glow(Modifier.align(Alignment.TopStart).offset((-90).dp, 100.dp).size(310.dp),
            Color(0xFFFFE8CE), 90.dp)
        Glow(Modifier.align(Alignment.TopEnd).offset(100.dp, 280.dp).size(260.dp),
            Color(0xFFD8E1D9), 85.dp)
        Glow(Modifier.align(Alignment.BottomStart).offset((-100).dp, (-35).dp).size(350.dp),
            Color(0xFFE7CBB9), 95.dp)
        Glow(Modifier.align(Alignment.BottomEnd).offset(90.dp, 10.dp).size(280.dp),
            Color(0xFFC7D0C8), 90.dp)
        // Fine translucent wash over the wallpaper gives the glass a brighter iOS-like edge.
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(White.copy(.13f), Color.Transparent, Color.Black.copy(.10f)))
        ))

        Column(
            Modifier.fillMaxSize()
                .graphicsLayer {
                    translationY = -reveal * h.toPx() * .78f
                    alpha = 1f - reveal * .18f
                    scaleX = 1f - reveal * .035f
                    scaleY = 1f - reveal * .035f
                }
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(13.dp))
            TopStatus()
            Spacer(Modifier.height(34.dp))
            Text(date, color = White.copy(.96f), fontSize = 17.sp,
                fontWeight = FontWeight.Medium, letterSpacing = .15.sp)
            Spacer(Modifier.height(1.dp))
            Text(time, color = White, fontSize = 91.sp, lineHeight = 96.sp,
                fontWeight = FontWeight.Light, letterSpacing = (-5).sp)
            Spacer(Modifier.height(9.dp))
            Text("A beautiful day begins with you",
                color = White.copy(.92f), fontSize = 13.sp, letterSpacing = .2.sp)
            Spacer(Modifier.weight(1f))
            NotificationStack()
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                QuickAction("ϟ", "Flashlight")
                QuickAction("◉", "Camera")
            }
            Spacer(Modifier.height(19.dp))
            Text(if (unlocked) "Preview unlocked" else "Swipe up to open",
                color = White.copy(.96f), fontSize = 13.sp, fontWeight = FontWeight.Medium,
                letterSpacing = .25.sp)
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier.fillMaxWidth().height(42.dp)
                    .pointerInput(unlocked) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { _, amount ->
                                dragAmount = (dragAmount - amount).coerceIn(0f, 300f)
                            },
                            onDragEnd = {
                                if (dragAmount > 95f) unlocked = true
                                dragAmount = 0f
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.width(135.dp).height(5.dp).clip(CircleShape)
                    .background(White.copy(.96f)))
            }
            Spacer(Modifier.height(4.dp))
        }

        if (unlocked) {
            Surface(
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 30.dp),
                shape = RoundedCornerShape(28.dp),
                color = White.copy(.24f),
                border = BorderStroke(1.dp, GlassLine),
                shadowElevation = 22.dp
            ) {
                Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Welcome", color = White, fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("This is the lock-screen interaction preview.",
                        color = White.copy(.9f), fontSize = 13.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(15.dp))
                    Text("Tap to lock again", color = White, fontSize = 12.sp,
                        modifier = Modifier.pointerInput(Unit) {
                            detectVerticalDragGestures(onDragEnd = { unlocked = false })
                        })
                }
            }
        }
    }
}

@Composable
private fun TopStatus() {
    Row(Modifier.fillMaxWidth().padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("9:41", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.weight(1f))
        Text("▮▮▮   Wi-Fi   ▰", color = White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun Glow(modifier: Modifier, color: Color, blur: androidx.compose.ui.unit.Dp) {
    Box(modifier.blur(blur).clip(CircleShape).background(color.copy(alpha = .72f)))
}

@Composable
private fun NotificationStack() {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        GlassPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(9.dp), color = White.copy(.25f),
                    border = BorderStroke(1.dp, White.copy(.3f))) {
                    Box(Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                        Text("✦", color = White, fontSize = 18.sp)
                    }
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text("iLOCK", color = White.copy(.82f), fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    Text("Your lock screen, your style", color = White, fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold)
                    Text("Liquid Glass is ready to shine.", color = White.copy(.84f), fontSize = 12.sp)
                }
                Text("now", color = White.copy(.78f), fontSize = 10.sp)
            }
        }
        GlassPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("◌", color = White, fontSize = 23.sp)
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text("DISPLAY", color = White.copy(.76f), fontSize = 9.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
                    Text("A softer, more personal lock screen", color = White,
                        fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Text("1m", color = White.copy(.75f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun GlassPanel(content: @Composable RowScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(27.dp),
        color = White.copy(.19f),
        border = BorderStroke(1.dp, GlassLine),
        shadowElevation = 16.dp
    ) {
        Box {
            Box(Modifier.fillMaxWidth().height(34.dp).background(
                Brush.verticalGradient(listOf(White.copy(.17f), Color.Transparent))
            ))
            Row(Modifier.padding(horizontal = 15.dp, vertical = 15.dp), content = content)
        }
    }
}

@Composable
private fun QuickAction(symbol: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(54.dp),
            shape = CircleShape,
            color = White.copy(.22f),
            border = BorderStroke(1.dp, White.copy(.48f)),
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(symbol, color = White, fontSize = 23.sp, fontWeight = FontWeight.Light)
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(label, color = White.copy(.8f), fontSize = 9.sp)
    }
}
