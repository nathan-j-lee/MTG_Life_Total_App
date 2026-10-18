package ui.lifecounter.playerbutton

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.game.timer.TurnOrder
import theme.scaledSp
import theme.textShadowStyle
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TurnOrderSelector(
    modifier: Modifier = Modifier,
    textColor: Color,
    onSelect: (TurnOrder) -> Unit,
) {
    BoxWithConstraints(modifier) {
        val iconSize = minOf(maxWidth / 4, maxHeight / 2.5f, 96.dp)
        val labelSize = (minOf(maxWidth.value / 16, maxHeight.value / 10) + 8).scaledSp
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Choose turn order",
                color = textColor,
                fontSize = labelSize * 0.75f,
                textAlign = TextAlign.Center,
                style = textShadowStyle(),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            Row(Modifier.fillMaxWidth().weight(1f)) {
                listOf(TurnOrder.COUNTERCLOCKWISE, TurnOrder.CLOCKWISE).forEach { order ->
                    val clockwise = order == TurnOrder.CLOCKWISE
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                            .clickable { onSelect(order) }
                            .semantics {
                                contentDescription = if (clockwise) "Clockwise" else "Counterclockwise"
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                    ) {
                        Canvas(Modifier.size(iconSize).graphicsLayer {
                            scaleX = if (clockwise) 1f else -1f
                        }) {
                            val radius = size.minDimension * 0.36f
                            val strokeWidth = size.minDimension * 0.08f
                            drawArc(
                                color = textColor,
                                startAngle = -60f,
                                sweepAngle = 300f,
                                useCenter = false,
                                topLeft = center - Offset(radius, radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            val angle = 240f * kotlin.math.PI.toFloat() / 180f
                            val radial = Offset(cos(angle), sin(angle))
                            val tangent = Offset(-sin(angle), cos(angle))
                            val tip = center + radial * radius + tangent * strokeWidth
                            val base = tip - tangent * (strokeWidth * 3)
                            drawPath(Path().apply {
                                moveTo(tip.x, tip.y)
                                val first = base + radial * (strokeWidth * 1.5f)
                                val second = base - radial * (strokeWidth * 1.5f)
                                lineTo(first.x, first.y)
                                lineTo(second.x, second.y)
                                close()
                            }, color = textColor)
                        }
                        Text(
                            text = if (clockwise) "CW" else "CCW",
                            color = textColor,
                            fontSize = labelSize,
                            textAlign = TextAlign.Center,
                            style = textShadowStyle()
                        )
                    }
                }
            }
        }
    }
}
