package com.redfox.ilock

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor

private val LockWhite = Color(0xFFF8FBFF)

class MainActivity : FragmentActivity() {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        val executor: Executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    finish()
                }
            })
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock device")
            .setSubtitle("Confirm with your phone's screen lock")
            .setAllowedAuthenticators(
                androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        setContent {
            MaterialTheme {
                IOSLockExperience(
                    onRequestSystemUnlock = { biometricPrompt.authenticate(promptInfo) }
                )
            }
        }
    }
}

@Composable
private fun IOSLockExperience(onRequestSystemUnlock: () -> Unit) {
    var showPasscode by remember { mutableStateOf(false) }
    var enteredCount by remember { mutableIntStateOf(0) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) { now = System.currentTimeMillis(); delay(1000) }
    }
    val time = remember(now) { SimpleDateFormat("h:mm", Locale.getDefault()).format(Date(now)) }
    val date = remember(now) { SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date(now)) }

    if (showPasscode) {
        IOSPasscodeScreen(
            onDigit = {
                if (enteredCount < 4) {
                    enteredCount += 1
                    if (enteredCount == 4) {
                        enteredCount = 0
                        onRequestSystemUnlock()
                    }
                }
            },
            onClear = { enteredCount = 0 },
            enteredCount = enteredCount,
            onBack = { showPasscode = false; enteredCount = 0 }
        )
    } else {
        IOSHomeLockScreen(time, date, onSwipeUp = { showPasscode = true })
    }
}

@Composable
private fun IOSHomeLockScreen(time: String, date: String, onSwipeUp: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxHeight < 760.dp
        Box(Modifier.fillMaxSize().background(
            Brush.linearGradient(
                colorStops = arrayOf(
                    0f to Color(0xFFB7C9D6), .28f to Color(0xFF477D68),
                    .58f to Color(0xFF08733F), .82f to Color(0xFF145D43),
                    1f to Color(0xFF8AAFA4)
                )
            )
        ))
        Box(Modifier.align(Alignment.TopStart).offset((-70).dp, 120.dp).size(260.dp)
            .blur(75.dp).background(Color(0xFFE7F1F7).copy(.65f), CircleShape))
        Box(Modifier.align(Alignment.CenterEnd).offset(90.dp, 40.dp).size(240.dp)
            .blur(85.dp).background(Color(0xFF00A64F).copy(.55f), CircleShape))
        Box(Modifier.align(Alignment.BottomStart).offset((-40).dp, 50.dp).size(270.dp)
            .blur(90.dp).background(Color(0xFFB9E4E8).copy(.55f), CircleShape))
        WallpaperRibbons(Modifier.fillMaxSize())
        FlowerArtwork(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(maxHeight * .46f))

        Column(Modifier.fillMaxSize().padding(horizontal = 27.dp)) {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Jio", color = LockWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                StatusGlyphs()
            }
            Spacer(Modifier.height(9.dp))
            DynamicIsland()
            Spacer(Modifier.height(if (compact) 34.dp else 43.dp))
            Text(date, Modifier.fillMaxWidth(), color = LockWhite.copy(.96f),
                fontSize = 17.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
            Text(time, Modifier.fillMaxWidth(), color = LockWhite,
                fontSize = if (compact) 91.sp else 101.sp,
                lineHeight = if (compact) 94.sp else 104.sp,
                fontWeight = FontWeight.ExtraLight, letterSpacing = (-5.5).sp,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(if (compact) 13.dp else 18.dp))
            LockWidgets()
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth().padding(horizontal = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                RoundLockButton("flash")
                RoundLockButton("camera")
            }
            Spacer(Modifier.height(7.dp))
            Box(
                Modifier.fillMaxWidth().height(34.dp)
                    .pointerInput(Unit) {
                        var totalDrag = 0f
                        detectVerticalDragGestures(
                            onVerticalDrag = { _, amount -> totalDrag += amount },
                            onDragEnd = {
                                if (totalDrag < -45f) onSwipeUp()
                                totalDrag = 0f
                            },
                            onDragCancel = { totalDrag = 0f }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.width(132.dp).height(5.dp).clip(CircleShape).background(LockWhite))
            }
            Spacer(Modifier.height(3.dp))
        }
    }
}

@Composable
private fun IOSPasscodeScreen(
    onDigit: () -> Unit,
    onClear: () -> Unit,
    enteredCount: Int,
    onBack: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Color(0xFF090909))) {
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF111111), Color(0xFF090909), Color(0xFF282120), Color(0xFF68666A)))
        ))
        Box(Modifier.align(Alignment.Center).offset(y = 130.dp).size(280.dp)
            .blur(95.dp).background(Color(0xFF96645B).copy(.34f), CircleShape))
        Column(Modifier.fillMaxSize().padding(horizontal = 28.dp)) {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Jio", color = LockWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                StatusGlyphs()
            }
            Box(Modifier.fillMaxWidth().padding(top = 14.dp), contentAlignment = Alignment.Center) {
                Text("▣", color = LockWhite, fontSize = 43.sp)
            }
            Spacer(Modifier.height(75.dp))
            Text("Enter Passcode", Modifier.fillMaxWidth(), color = LockWhite,
                fontSize = 23.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(22.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                repeat(4) { index ->
                    Box(Modifier.padding(horizontal = 12.dp).size(22.dp)
                        .then(if (index < enteredCount) Modifier.background(LockWhite, CircleShape)
                        else Modifier.background(Color.Transparent, CircleShape)),
                        contentAlignment = Alignment.Center) {
                        if (index >= enteredCount) {
                            Surface(Modifier.fillMaxSize(), shape = CircleShape, color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, LockWhite)) {}
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            val rows = listOf(
                listOf("1" to "", "2" to "ABC", "3" to "DEF"),
                listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
                listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ")
            )
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                rows.forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        row.forEach { (digit, letters) -> PasscodeKey(digit, letters, onDigit) }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    PasscodeKey("0", "", onDigit)
                }
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Back", color = LockWhite.copy(.8f), fontSize = 15.sp,
                    modifier = Modifier.clickable { onBack() })
                Text("Clear", color = LockWhite, fontSize = 16.sp,
                    modifier = Modifier.clickable { onClear() })
            }
            Spacer(Modifier.height(20.dp))
            Box(Modifier.fillMaxWidth().height(22.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.width(132.dp).height(5.dp).clip(CircleShape).background(LockWhite))
            }
            Spacer(Modifier.height(3.dp))
        }
    }
}

@Composable
private fun PasscodeKey(number: String, letters: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(88.dp).clickable { onClick() },
        shape = CircleShape, color = Color(0xFF858487).copy(.43f)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(number, color = LockWhite, fontSize = 39.sp, fontWeight = FontWeight.Light, lineHeight = 42.sp)
            if (letters.isNotEmpty()) Text(letters, color = LockWhite.copy(.92f),
                fontSize = 9.sp, fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
        }
    }
}

@Composable
private fun DynamicIsland() {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.width(164.dp).height(35.dp), shape = CircleShape,
            color = Color(0xFF001D0C).copy(.96f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF32FF70).copy(.85f)),
            shadowElevation = 12.dp
        ) {
            Box(Modifier.fillMaxSize().padding(start = 15.dp), contentAlignment = Alignment.CenterStart) {
                Text("▣", color = LockWhite, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun StatusGlyphs() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Canvas(Modifier.size(18.dp, 14.dp)) {
            for (i in 0..3) {
                val bh = size.height * (.34f + i * .19f)
                drawRoundRect(LockWhite, Offset(i * 4.5.dp.toPx(), size.height - bh),
                    Size(3.1.dp.toPx(), bh), androidx.compose.ui.geometry.CornerRadius(1.2.dp.toPx()))
            }
        }
        Canvas(Modifier.size(18.dp, 14.dp)) {
            val p = Path().apply {
                moveTo(size.width * .08f, size.height * .38f)
                quadraticTo(size.width * .5f, -size.height * .08f, size.width * .92f, size.height * .38f)
                moveTo(size.width * .26f, size.height * .59f)
                quadraticTo(size.width * .5f, size.height * .22f, size.width * .74f, size.height * .59f)
                moveTo(size.width * .46f, size.height * .80f)
                quadraticTo(size.width * .5f, size.height * .72f, size.width * .54f, size.height * .80f)
            }
            drawPath(p, LockWhite, style = Stroke(1.8.dp.toPx()))
        }
        Surface(shape = RoundedCornerShape(5.dp), color = LockWhite, modifier = Modifier.height(15.dp)) {
            Text("100", color = Color(0xFF355344), fontSize = 9.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
        }
    }
}

@Composable
private fun LockWidgets() {
    Surface(
        modifier = Modifier.fillMaxWidth().height(108.dp), shape = RoundedCornerShape(30.dp),
        color = Color.White.copy(.19f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(.57f)),
        shadowElevation = 20.dp
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("☁", color = LockWhite, fontSize = 28.sp)
                    Spacer(Modifier.width(9.dp))
                    Text("24°", color = LockWhite, fontSize = 29.sp, fontWeight = FontWeight.Light)
                }
                Text("Mostly Cloudy", color = LockWhite.copy(.95f), fontSize = 13.sp)
                Text("H:28°  L:18°", color = LockWhite.copy(.85f), fontSize = 11.sp)
            }
            Box(Modifier.width(1.dp).fillMaxHeight(.85f).background(LockWhite.copy(.62f)))
            Spacer(Modifier.width(15.dp))
            Column(Modifier.weight(1.12f), verticalArrangement = Arrangement.Center) {
                Text("▣  Next Event", color = LockWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(5.dp))
                Text("Meeting at 11:00 AM", color = LockWhite.copy(.95f), fontSize = 12.sp)
            }
            Text("›", color = LockWhite, fontSize = 27.sp)
        }
    }
}

@Composable
private fun RoundLockButton(type: String) {
    Surface(
        modifier = Modifier.size(58.dp), shape = CircleShape,
        color = Color(0xFF34453F).copy(.48f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(.52f)),
        shadowElevation = 12.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(if (type == "flash") "ϟ" else "◎", color = LockWhite, fontSize = 28.sp)
        }
    }
}

@Composable
private fun WallpaperRibbons(modifier: Modifier) {
    Canvas(modifier) {
        val path1 = Path().apply {
            moveTo(size.width * 1.05f, size.height * .08f)
            cubicTo(size.width * .82f, size.height * .34f, size.width * .80f, size.height * .42f, size.width * .38f, size.height * .57f)
            cubicTo(size.width * .02f, size.height * .72f, size.width * .08f, size.height * .81f, size.width * .48f, size.height * .89f)
        }
        drawPath(path1, Color.White.copy(.45f), style = Stroke(width = 3.dp.toPx()))
        drawPath(path1, Brush.horizontalGradient(listOf(Color.White.copy(.04f), Color(0xFFB8F9E0).copy(.38f), Color.White.copy(.08f))),
            style = Stroke(width = 10.dp.toPx()))
        val path2 = Path().apply {
            moveTo(size.width * -.10f, size.height * .61f)
            cubicTo(size.width * .20f, size.height * .48f, size.width * .45f, size.height * .67f, size.width * 1.05f, size.height * .42f)
        }
        drawPath(path2, Color.White.copy(.32f), style = Stroke(width = 2.dp.toPx()))
    }
}

@Composable
private fun FlowerArtwork(modifier: Modifier) {
    Canvas(modifier) {
        val cx = size.width * .66f
        val cy = size.height * .60f
        val petalW = size.width * .16f
        val petalH = size.height * .31f
        for (i in 0 until 8) {
            rotate(i * 45f, pivot = Offset(cx, cy)) {
                drawOval(
                    brush = Brush.linearGradient(listOf(Color.White.copy(.98f), Color(0xFFDCECF0).copy(.84f), Color.White.copy(.58f))),
                    topLeft = Offset(cx - petalW / 2, cy - petalH * .93f), size = Size(petalW, petalH)
                )
                drawOval(color = Color.White.copy(.45f),
                    topLeft = Offset(cx - petalW * .34f, cy - petalH * .89f),
                    size = Size(petalW * .68f, petalH * .78f), style = Stroke(width = 1.2.dp.toPx()))
            }
        }
        drawCircle(Color(0xFFFFD45B), radius = size.width * .027f, center = Offset(cx, cy))
        for (i in 0 until 12) {
            val angle = i * (Math.PI * 2 / 12)
            val sx = cx + kotlin.math.cos(angle).toFloat() * size.width * .045f
            val sy = cy + kotlin.math.sin(angle).toFloat() * size.width * .045f
            drawLine(Color(0xFFFFD45B), Offset(cx, cy), Offset(sx, sy), strokeWidth = 1.5.dp.toPx())
            drawCircle(Color(0xFFFFC34C), radius = 2.dp.toPx(), center = Offset(sx, sy))
        }
        for (i in 0..7) {
            val y = size.height * (.82f + i * .022f)
            drawLine(Color.White.copy(.18f - i * .012f), Offset(size.width * .35f, y),
                Offset(size.width * .94f, y), strokeWidth = (1.5f + i * .25f).dp.toPx())
        }
    }
}
