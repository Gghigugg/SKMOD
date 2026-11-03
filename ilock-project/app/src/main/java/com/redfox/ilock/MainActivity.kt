package com.redfox.ilock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
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

private val White = Color.White
private val GlassEdge = White.copy(alpha = .52f)

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
    var drag by remember { mutableFloatStateOf(0f) }
    var unlocked by remember { mutableStateOf(false) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) { now = System.currentTimeMillis(); delay(1000) }
    }
    val reveal by animateFloatAsState(
        if (unlocked) 1f else (drag / 220f).coerceIn(0f, 1f),
        spring(dampingRatio = .84f, stiffness = 300f), label = "unlock"
    )
    val clock = remember(now) { SimpleDateFormat("h:mm", Locale.getDefault()).format(Date(now)) }
    val date = remember(now) {
        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date(now))
            .replaceFirstChar { it.uppercase() }
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(Color(0xFF858C87))) {
        val screenHeight = maxHeight
        // Layered, softly blurred wallpaper light; neutral, warm and photographic in feel.
        Box(Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF687773), Color(0xFFC8B9A6), Color(0xFFE6D8C7), Color(0xFF737D77)))
        ))
        LightBlob(Modifier.align(Alignment.TopStart).offset((-110).dp, 90.dp).size(330.dp), Color(0xFFFFE5C8), 85.dp)
        LightBlob(Modifier.align(Alignment.TopEnd).offset(110.dp, 240.dp).size(290.dp), Color(0xFFDCE6DC), 90.dp)
        LightBlob(Modifier.align(Alignment.BottomStart).offset((-100).dp, (-20).dp).size(360.dp), Color(0xFFE9C9B2), 95.dp)
        LightBlob(Modifier.align(Alignment.BottomEnd).offset(100.dp, 30.dp).size(300.dp), Color(0xFFC9D3CB), 90.dp)
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(White.copy(.16f), Color.Transparent, Color.Black.copy(.13f)))
        ))

        Column(
            Modifier.fillMaxSize()
                .graphicsLayer {
                    translationY = -reveal * screenHeight.toPx() * .78f
                    scaleX = 1f - reveal * .035f
                    scaleY = 1f - reveal * .035f
                }
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            IOSStatusBar()
            Spacer(Modifier.height(29.dp))
            Text(date, color = White.copy(.98f), fontSize = 17.sp, fontWeight = FontWeight.Medium)
            Text(clock, color = White, fontSize = 94.sp, lineHeight = 98.sp,
                fontWeight = FontWeight.ExtraLight, letterSpacing = (-5.5).sp)
            Spacer(Modifier.height(13.dp))
            WeatherWidget()
            Spacer(Modifier.weight(1f))
            NotificationStack()
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                LockShortcut("ϟ")
                LockShortcut("◉")
            }
            Spacer(Modifier.height(15.dp))
            Text(if (unlocked) "Preview unlocked" else "Swipe up to open",
                color = White.copy(.96f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier.fillMaxWidth().height(42.dp).pointerInput(unlocked) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, amount -> drag = (drag - amount).coerceIn(0f, 300f) },
                        onDragEnd = { if (drag > 90f) unlocked = true; drag = 0f }
                    )
                }, contentAlignment = Alignment.Center
            ) {
                Box(Modifier.width(136.dp).height(5.dp).clip(CircleShape).background(White))
            }
            Spacer(Modifier.height(4.dp))
        }

        if (unlocked) {
            Surface(
                Modifier.align(Alignment.Center).padding(28.dp),
                shape = RoundedCornerShape(30.dp), color = White.copy(.22f),
                border = BorderStroke(1.dp, GlassEdge), shadowElevation = 24.dp
            ) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Welcome", color = White, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
                    Text("Lock-screen interaction preview", color = White.copy(.9f), fontSize = 13.sp)
                    Spacer(Modifier.height(14.dp))
                    Text("Swipe down to return", color = White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun IOSStatusBar() {
    Row(Modifier.fillMaxWidth().height(22.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()),
            color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.weight(1f))
        IOSSignalIcons()
    }
}

@Composable
private fun IOSSignalIcons() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Canvas(Modifier.size(width = 18.dp, height = 14.dp)) {
            val widths = size.width / 5f
            for (i in 0..3) {
                val barHeight = size.height * (0.30f + i * 0.19f)
                drawRoundRect(White, Offset(i * (widths + 1.1.dp.toPx()), size.height - barHeight),
                    androidx.compose.ui.geometry.Size(widths, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()))
            }
        }
        Canvas(Modifier.size(17.dp, 14.dp)) {
            val p = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * .08f, size.height * .38f)
                quadraticTo(size.width * .5f, -size.height * .12f, size.width * .92f, size.height * .38f)
                moveTo(size.width * .25f, size.height * .57f)
                quadraticTo(size.width * .5f, size.height * .20f, size.width * .75f, size.height * .57f)
                moveTo(size.width * .44f, size.height * .78f)
                quadraticTo(size.width * .5f, size.height * .68f, size.width * .56f, size.height * .78f)
            }
            drawPath(p, White, style = androidx.compose.ui.graphics.drawscope.Stroke(1.7.dp.toPx()))
        }
        Canvas(Modifier.size(width = 25.dp, height = 14.dp)) {
            drawRoundRect(White.copy(.95f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(1.2.dp.toPx()))
            drawRoundRect(White, topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(size.width * .68f, size.height - 4.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
            drawRect(White, topLeft = Offset(size.width, size.height * .34f),
                size = androidx.compose.ui.geometry.Size(2.dp.toPx(), size.height * .32f))
        }
    }
}

@Composable
private fun WeatherWidget() {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = White.copy(.16f),
        border = BorderStroke(1.dp, White.copy(.36f)),
        shadowElevation = 10.dp
    ) {
        Row(Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("☀", color = Color(0xFFFFE5A5), fontSize = 28.sp)
            Column {
                Text("WEATHER", color = White.copy(.78f), fontSize = 9.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Text("Add your city", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Text("—°", color = White, fontSize = 23.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
private fun LightBlob(modifier: Modifier, color: Color, blurRadius: androidx.compose.ui.unit.Dp) {
    Box(modifier.blur(blurRadius).clip(CircleShape).background(color.copy(.76f)))
}

@Composable
private fun NotificationStack() {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        GlassNotification("MESSAGES", "Your day, your moment", "Everything is looking beautiful today.", "now", "✦")
        GlassNotification("NOTIFICATIONS", "A softer lock screen", "Your liquid glass display is ready.", "1m", "◌")
    }
}

@Composable
private fun GlassNotification(app: String, title: String, body: String, time: String, icon: String) {
    Surface(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp),
        color = White.copy(.20f), border = BorderStroke(1.dp, White.copy(.55f)),
        shadowElevation = 17.dp
    ) {
        Box {
            Box(Modifier.fillMaxWidth().height(30.dp).background(
                Brush.verticalGradient(listOf(White.copy(.18f), Color.Transparent))))
            Row(Modifier.padding(horizontal = 15.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(10.dp), color = White.copy(.25f),
                    border = BorderStroke(1.dp, White.copy(.34f))) {
                    Box(Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                        Text(icon, color = White, fontSize = 18.sp)
                    }
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text(app, color = White.copy(.78f), fontSize = 9.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    Text(title, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(body, color = White.copy(.88f), fontSize = 11.sp)
                }
                Text(time, color = White.copy(.76f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun LockShortcut(symbol: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(Modifier.size(54.dp), shape = CircleShape, color = White.copy(.22f),
            border = BorderStroke(1.dp, White.copy(.58f)), shadowElevation = 13.dp) {
            Box(contentAlignment = Alignment.Center) {
                Text(symbol, color = White, fontSize = 24.sp, fontWeight = FontWeight.Light)
            }
        }
    }
}
