package com.sleeperbaby.app.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val Glow = Color(0xFFFFF1C9)
private val Figure = Color(0xFFFFE7A8)
private val SoftWhite = Color(0xFFFFF8E8)

fun DrawScope.drawBear(x: Float, y: Float, size: Float, rotation: Float, alpha: Float) {
    silhouette(x, y, size, rotation, alpha) {
        addOval(Rect(16f, 58f, 84f, 100f))
        addOval(Rect(22f, 20f, 78f, 74f))
        addOval(Rect(16f, 10f, 38f, 34f))
        addOval(Rect(62f, 10f, 84f, 34f))
        addOval(Rect(38f, 46f, 62f, 66f))
    }
}

fun DrawScope.drawBunny(x: Float, y: Float, size: Float, rotation: Float, alpha: Float) {
    silhouette(x, y, size, rotation, alpha) {
        addOval(Rect(30f, 0f, 44f, 50f))
        addOval(Rect(52f, 2f, 66f, 52f))
        addOval(Rect(26f, 36f, 74f, 78f))
        addOval(Rect(22f, 66f, 78f, 102f))
        addOval(Rect(72f, 78f, 90f, 96f))
    }
}

fun DrawScope.drawOwl(x: Float, y: Float, size: Float, rotation: Float, alpha: Float) {
    silhouette(x, y, size, rotation, alpha) {
        addOval(Rect(18f, 28f, 82f, 98f))
        moveTo(28f, 34f)
        lineTo(18f, 6f)
        lineTo(44f, 30f)
        close()
        moveTo(72f, 34f)
        lineTo(82f, 6f)
        lineTo(56f, 30f)
        close()
        addOval(Rect(30f, 38f, 50f, 58f))
        addOval(Rect(50f, 38f, 70f, 58f))
    }
    translate(x, y) {
        rotate(rotation, pivot = Offset(size / 2f, size / 2f)) {
            scale(size / 100f, Offset.Zero) {
                drawCircle(Color.Black.copy(alpha = 0.45f * alpha), 6f, Offset(40f, 48f))
                drawCircle(Color.Black.copy(alpha = 0.45f * alpha), 6f, Offset(60f, 48f))
            }
        }
    }
}

fun DrawScope.drawBird(x: Float, y: Float, size: Float, rotation: Float, alpha: Float) {
    silhouette(x, y, size, rotation, alpha) {
        addOval(Rect(18f, 38f, 78f, 78f))
        addOval(Rect(62f, 28f, 90f, 56f))
        moveTo(18f, 52f)
        lineTo(0f, 40f)
        lineTo(18f, 62f)
        close()
        moveTo(40f, 58f)
        lineTo(18f, 86f)
        lineTo(52f, 70f)
        close()
        addOval(Rect(8f, 70f, 28f, 88f))
    }
}

fun DrawScope.drawMoon(x: Float, y: Float, size: Float, rotation: Float, alpha: Float) {
    val full = Path().apply { addOval(Rect(8f, 8f, 92f, 92f)) }
    val hole = Path().apply { addOval(Rect(32f, 0f, 108f, 80f)) }
    val moon = Path().apply { op(full, hole, PathOperation.Difference) }
    translate(x, y) {
        rotate(rotation, pivot = Offset(size / 2f, size / 2f)) {
            scale(size / 100f, Offset.Zero) {
                drawPath(moon, Glow.copy(alpha = 0.18f * alpha), style = Stroke(width = 16f))
                drawPath(moon, Figure.copy(alpha = alpha))
            }
        }
    }
}

fun DrawScope.drawCloud(x: Float, y: Float, size: Float, alpha: Float) {
    silhouette(x, y, size, 0f, alpha * 0.85f) {
        addOval(Rect(8f, 38f, 52f, 78f))
        addOval(Rect(32f, 22f, 78f, 70f))
        addOval(Rect(58f, 40f, 96f, 80f))
        addOval(Rect(18f, 52f, 88f, 88f))
    }
}

fun DrawScope.drawStar(center: Offset, radius: Float, alpha: Float, points: Int = 5) {
    val path = Path()
    val inner = radius * 0.4f
    for (i in 0 until points * 2) {
        val angle = (i * PI / points) - PI / 2
        val r = if (i % 2 == 0) radius else inner
        val px = center.x + (cos(angle) * r).toFloat()
        val py = center.y + (sin(angle) * r).toFloat()
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(path, SoftWhite.copy(alpha = 0.22f * alpha), style = Stroke(width = radius * 0.35f))
    drawPath(path, SoftWhite.copy(alpha = alpha), style = Fill)
}

private fun DrawScope.silhouette(
    x: Float,
    y: Float,
    size: Float,
    rotation: Float,
    alpha: Float,
    builder: Path.() -> Unit,
) {
    val path = Path().apply(builder)
    translate(x, y) {
        rotate(rotation, pivot = Offset(size / 2f, size / 2f)) {
            scale(size / 100f, Offset.Zero) {
                drawPath(path, Glow.copy(alpha = 0.2f * alpha), style = Stroke(width = 14f))
                drawPath(path, Figure.copy(alpha = alpha), style = Fill)
            }
        }
    }
}
