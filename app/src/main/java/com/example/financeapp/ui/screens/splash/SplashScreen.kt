package com.example.financeapp.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.ui.theme.IFBlue
import com.example.financeapp.ui.theme.IFBlueDark
import com.example.financeapp.ui.theme.IFLilac
import com.example.financeapp.ui.theme.IFLilacLight
import com.example.financeapp.ui.theme.IFPurple
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    aoNavegar: () -> Unit
) {
    // Animações de entrada
    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo aparece com scale + fade
        logoScale.animateTo(1.1f, tween(600, easing = FastOutSlowInEasing))
        logoScale.animateTo(1f, tween(200))
        logoAlpha.animateTo(1f, tween(400))
        delay(200)
        textAlpha.animateTo(1f, tween(500))
        delay(150)
        subtitleAlpha.animateTo(1f, tween(400))
        delay(1200)
        aoNavegar()
    }

    // Animações contínuas de fundo
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val wave1 by infiniteTransition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Restart),
        label = "w2"
    )
    val pulse by infiniteTransition.animateFloat(
        0.85f, 1.15f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val orbit by infiniteTransition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "orbit"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Canvas de fundo com ondas financeiras e partículas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Gradiente de fundo profundo
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF0A0F1E), IFBlueDark, Color(0xFF1E1B4B))
                )
            )

            // Grade sutil (linhas de gráfico financeiro)
            val gridColor = IFBlue.copy(alpha = 0.04f)
            for (i in 0..20) {
                val y = h * i / 20f
                drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 0.5f)
            }
            for (i in 0..10) {
                val x = w * i / 10f
                drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 0.5f)
            }

            // Onda 1 — gráfico de ações subindo (azul)
            val chartPath = Path()
            chartPath.moveTo(0f, h * 0.7f)
            for (x in 0..w.toInt() step 3) {
                val xf = x.toFloat()
                val progress = xf / w
                val y = h * 0.7f -
                        sin((progress * 3 + wave1 * Math.PI / 180).toFloat()) * 35f -
                        progress * h * 0.15f // Tendência de alta
                chartPath.lineTo(xf, y)
            }
            drawPath(
                chartPath,
                brush = Brush.horizontalGradient(listOf(IFBlue.copy(alpha = 0.4f), IFPurple.copy(alpha = 0.3f))),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )

            // Onda 2 — segunda linha de gráfico (roxo)
            val chartPath2 = Path()
            chartPath2.moveTo(0f, h * 0.75f)
            for (x in 0..w.toInt() step 3) {
                val xf = x.toFloat()
                val progress = xf / w
                val y = h * 0.75f -
                        sin((progress * 4 + wave2 * Math.PI / 180).toFloat()) * 25f -
                        progress * h * 0.08f
                chartPath2.lineTo(xf, y)
            }
            drawPath(
                chartPath2,
                brush = Brush.horizontalGradient(listOf(IFPurple.copy(alpha = 0.25f), IFLilac.copy(alpha = 0.2f))),
                style = Stroke(width = 1.8f, cap = StrokeCap.Round)
            )

            // Área preenchida sob a primeira onda
            val fillPath = Path()
            fillPath.addPath(chartPath)
            fillPath.lineTo(w, h)
            fillPath.lineTo(0f, h)
            fillPath.close()
            drawPath(
                fillPath,
                brush = Brush.verticalGradient(
                    listOf(IFBlue.copy(alpha = 0.08f), Color.Transparent),
                    startY = h * 0.5f,
                    endY = h
                )
            )

            // Partículas orbitantes (dados financeiros em movimento)
            val cx = w / 2
            val cy = h * 0.38f
            for (i in 0..7) {
                val angle = (orbit + i * 45) * Math.PI / 180
                val radius = 80f + i * 18f
                val px = cx + cos(angle).toFloat() * radius
                val py = cy + sin(angle).toFloat() * radius * 0.5f
                val dotSize = 3f + (i % 3) * 2f
                val alpha = 0.15f + (i % 4) * 0.08f
                drawCircle(
                    color = when (i % 3) {
                        0 -> IFBlue.copy(alpha = alpha)
                        1 -> IFPurple.copy(alpha = alpha)
                        else -> IFLilacLight.copy(alpha = alpha)
                    },
                    radius = dotSize,
                    center = Offset(px, py)
                )
            }

            // Círculos concêntricos pulsantes (efeito tecnológico)
            val ringAlpha = 0.06f
            drawCircle(IFBlue.copy(alpha = ringAlpha), radius = 100f * pulse, center = Offset(cx, cy), style = Stroke(1.2f))
            drawCircle(IFPurple.copy(alpha = ringAlpha * 0.7f), radius = 150f * pulse, center = Offset(cx, cy), style = Stroke(0.8f))
            drawCircle(IFLilac.copy(alpha = ringAlpha * 0.5f), radius = 200f * pulse, center = Offset(cx, cy), style = Stroke(0.6f))

            // Pontos de dados flutuantes (simulando ticker de mercado)
            for (i in 0..20) {
                val px = (w * ((i * 0.047f + wave1 / 360f * 0.3f) % 1f))
                val py = h * 0.1f + (i * 37f % (h * 0.25f))
                drawCircle(
                    color = if (i % 2 == 0) IFBlue.copy(alpha = 0.15f) else IFLilac.copy(alpha = 0.1f),
                    radius = 1.5f + (i % 3),
                    center = Offset(px, py)
                )
            }
        }

        // Logo e texto central
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.35f))

            // Logo circular com gradiente e pulse
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(logoScale.value * pulse)
                    .alpha(logoAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Anel externo com gradiente
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(IFBlue, IFPurple, IFLilac, IFBlue)
                        ),
                        style = Stroke(width = 3f)
                    )
                    // Preenchimento interno
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(IFBlueDark.copy(alpha = 0.8f), IFPurple.copy(alpha = 0.4f))
                        )
                    )
                }
                Text(
                    "IF",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-2).sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "IF Bank",
                modifier = Modifier.alpha(textAlpha.value),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "Seu banco digital inteligente",
                modifier = Modifier.alpha(subtitleAlpha.value),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.weight(0.55f))

            Text(
                "Tecnologia • Segurança • Inovação",
                modifier = Modifier.alpha(subtitleAlpha.value),
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.3f),
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
