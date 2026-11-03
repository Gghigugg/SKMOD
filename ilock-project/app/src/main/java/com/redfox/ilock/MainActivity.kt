package com.redfox.ilock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PasscodeWhite = Color(0xFFF7F7F7)

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
        setContent { MaterialTheme { IOSPasscodeScreen() } }
    }
}

@Composable
private fun IOSPasscodeScreen() {
    BoxWithConstraints(
        Modifier.fillMaxSize().background(Color(0xFF080808))
    ) {
        val h = maxHeight
        // Dark blurred wallpaper glow, matching the supplied passcode reference.
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                0f to Color(0xFF101010),
                .46f to Color(0xFF090909),
                .72f to Color(0xFF24201F),
                1f to Color(0xFF666466)
            )
        ))
        Box(
            Modifier.align(Alignment.Center).offset(y = 125.dp)
                .size(width = 250.dp, height = 330.dp)
                .blur(100.dp).background(Color(0xFF8C5B50).copy(alpha = .42f), CircleShape)
        )
        Box(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(h * .34f)
                .background(Brush.verticalGradient(
                    listOf(Color.Transparent, Color(0xFF777579).copy(alpha = .72f))
                ))
        )
        Column(Modifier.fillMaxSize().padding(horizontal = 28.dp)) {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Airtel", color = PasscodeWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text("▮▮▮", color = PasscodeWhite.copy(.55f), fontSize = 15.sp, letterSpacing = (-2).sp)
                Spacer(Modifier.width(8.dp))
                BatteryIcon()
            }

            Spacer(Modifier.height(9.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LockGlyph()
            }

            Spacer(Modifier.height(105.dp))
            Text(
                "Enter Passcode",
                modifier = Modifier.fillMaxWidth(),
                color = PasscodeWhite,
                fontSize = 23.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = (-.35).sp
            )
            Spacer(Modifier.height(24.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    Box(
                        Modifier.padding(horizontal = 13.dp).size(22.dp)
                            .then(if (index < 3) Modifier.background(PasscodeWhite, CircleShape)
                            else Modifier.background(Color.Transparent, CircleShape)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index == 3) {
                            Box(Modifier.fillMaxSize().background(
                                Brush.radialGradient(listOf(Color.Transparent, Color.Transparent)
                                ), CircleShape
                            ))
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, PasscodeWhite)
                            ) {}
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val rows = listOf(
                    listOf("1" to "", "2" to "ABC", "3" to "DEF"),
                    listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
                    listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ")
                )
                rows.forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        row.forEach { (number, letters) -> NumberKey(number, letters) }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    NumberKey("0", "")
                }
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.fillMaxWidth().height(38.dp), contentAlignment = Alignment.CenterEnd) {
                Text("Clear", color = PasscodeWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(18.dp))
            Box(Modifier.fillMaxWidth().height(22.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.width(132.dp).height(5.dp).clip(CircleShape).background(PasscodeWhite))
            }
            Spacer(Modifier.height(3.dp))
        }
    }
}

@Composable
private fun NumberKey(number: String, letters: String) {
    Surface(
        modifier = Modifier.size(91.dp),
        shape = CircleShape,
        color = Color(0xFF858487).copy(alpha = .43f)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(number, color = PasscodeWhite, fontSize = 39.sp, fontWeight = FontWeight.Light, lineHeight = 42.sp)
            if (letters.isNotEmpty()) {
                Text(letters, color = PasscodeWhite.copy(.92f), fontSize = 9.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
            }
        }
    }
}

@Composable
private fun LockGlyph() {
    Box(Modifier.size(width = 38.dp, height = 49.dp), contentAlignment = Alignment.BottomCenter) {
        Box(
            Modifier.align(Alignment.TopCenter).width(24.dp).height(29.dp)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(Color.Transparent)
        ) {
            Box(Modifier.fillMaxSize().padding(3.dp).clip(
                RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ).background(Color.Transparent))
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                drawArc(
                    color = PasscodeWhite,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * .12f, size.height * .05f),
                    size = androidx.compose.ui.geometry.Size(size.width * .76f, size.height * .9f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                )
            }
        }
        Box(
            Modifier.width(36.dp).height(29.dp).clip(RoundedCornerShape(5.dp))
                .background(PasscodeWhite)
        )
    }
}

@Composable
private fun BatteryIcon() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.width(30.dp).height(15.dp).clip(RoundedCornerShape(4.dp))
                .background(Color.Transparent)
        ) {
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = PasscodeWhite.copy(.85f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.4.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFF75D36C),
                    topLeft = androidx.compose.ui.geometry.Offset(2.dp.toPx(), 2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width * .70f, size.height - 4.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                )
            }
        }
        Box(Modifier.padding(start = 1.dp).width(2.dp).height(6.dp)
            .clip(RoundedCornerShape(1.dp)).background(PasscodeWhite.copy(.8f)))
    }
}
