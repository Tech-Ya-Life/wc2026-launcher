package com.wc2026.launcher.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Draws subtle football pitch markings over the gradient background.
 * All lines are white at low alpha so they don't overpower the theme colours.
 */
@Composable
fun FootballPitchOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val lineColor = Color.White.copy(alpha = 0.10f)
        val strokePx = 2.dp.toPx()
        val stroke = Stroke(width = strokePx)

        // ── Halfway line ──────────────────────────────────
        drawLine(
            color = lineColor,
            start = Offset(0f, h / 2f),
            end = Offset(w, h / 2f),
            strokeWidth = strokePx
        )

        // ── Centre circle ────────────────────────────────
        val circleRadius = w * 0.22f
        drawCircle(
            color = lineColor,
            radius = circleRadius,
            center = Offset(w / 2f, h / 2f),
            style = stroke
        )

        // Centre spot
        drawCircle(
            color = lineColor,
            radius = 4.dp.toPx(),
            center = Offset(w / 2f, h / 2f)
        )

        // ── Penalty boxes (top & bottom) ─────────────────
        val penBoxW = w * 0.62f
        val penBoxH = h * 0.16f
        val penBoxLeft = (w - penBoxW) / 2f

        // Top penalty box
        drawRect(
            color = lineColor,
            topLeft = Offset(penBoxLeft, 0f),
            size = Size(penBoxW, penBoxH),
            style = stroke
        )
        // Bottom penalty box
        drawRect(
            color = lineColor,
            topLeft = Offset(penBoxLeft, h - penBoxH),
            size = Size(penBoxW, penBoxH),
            style = stroke
        )

        // ── Goal areas (6-yard boxes) ─────────────────────
        val goalBoxW = w * 0.32f
        val goalBoxH = h * 0.065f
        val goalBoxLeft = (w - goalBoxW) / 2f

        drawRect(
            color = lineColor,
            topLeft = Offset(goalBoxLeft, 0f),
            size = Size(goalBoxW, goalBoxH),
            style = stroke
        )
        drawRect(
            color = lineColor,
            topLeft = Offset(goalBoxLeft, h - goalBoxH),
            size = Size(goalBoxW, goalBoxH),
            style = stroke
        )

        // ── Penalty spots ─────────────────────────────────
        val penSpotOffset = penBoxH * 0.68f
        drawCircle(
            color = lineColor,
            radius = 4.dp.toPx(),
            center = Offset(w / 2f, penSpotOffset)
        )
        drawCircle(
            color = lineColor,
            radius = 4.dp.toPx(),
            center = Offset(w / 2f, h - penSpotOffset)
        )

        // ── Penalty arcs (the "D") ────────────────────────
        val arcRadius = w * 0.19f
        // Top D — arc that bulges downward (away from goal)
        drawArc(
            color = lineColor,
            startAngle = 25f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(w / 2f - arcRadius, penSpotOffset - arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = stroke
        )
        // Bottom D — arc that bulges upward (away from goal)
        drawArc(
            color = lineColor,
            startAngle = 205f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(w / 2f - arcRadius, h - penSpotOffset - arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = stroke
        )

        // ── Corner arcs ───────────────────────────────────
        val cr = 18.dp.toPx()
        // Top-left
        drawArc(
            color = lineColor,
            startAngle = 0f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(-cr, -cr), size = Size(cr * 2, cr * 2), style = stroke
        )
        // Top-right
        drawArc(
            color = lineColor,
            startAngle = 90f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(w - cr, -cr), size = Size(cr * 2, cr * 2), style = stroke
        )
        // Bottom-left
        drawArc(
            color = lineColor,
            startAngle = -90f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(-cr, h - cr), size = Size(cr * 2, cr * 2), style = stroke
        )
        // Bottom-right
        drawArc(
            color = lineColor,
            startAngle = 180f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(w - cr, h - cr), size = Size(cr * 2, cr * 2), style = stroke
        )
    }
}
