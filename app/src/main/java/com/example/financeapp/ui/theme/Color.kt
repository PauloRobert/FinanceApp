package com.example.financeapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================
// IF Bank — Design System Colors
// ============================================================

// ── Brand Colors ───────────────────────────────────────────
val IFBlue = Color(0xFF2563EB)
val IFBlueDark = Color(0xFF1E3A8A)
val IFPurple = Color(0xFF7C3AED)
val IFLilac = Color(0xFFA855F7)
val IFLilacLight = Color(0xFFC084FC)

// ── Light Theme ────────────────────────────────────────────
val LightPrimary = IFBlue
val LightPrimaryDark = IFBlueDark
val LightSecondary = IFPurple
val LightTertiary = IFLilac
val LightTertiaryLight = IFLilacLight

val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF0F172A)
val LightOnSurface = Color(0xFF1E293B)
val LightOnSurfaceVariant = Color(0xFF64748B)
val LightOutline = Color(0xFFCBD5E1)
val LightPrimaryContainer = Color(0xFFDBEAFE)
val LightOnPrimaryContainer = Color(0xFF1E3A8A)
val LightSecondaryContainer = Color(0xFFEDE9FE)
val LightOnSecondaryContainer = Color(0xFF5B21B6)

// ── Dark Theme ─────────────────────────────────────────────
val DarkPrimary = Color(0xFF60A5FA)
val DarkPrimaryDark = IFBlue
val DarkSecondary = Color(0xFFA78BFA)
val DarkTertiary = IFLilacLight

val DarkBackground = Color(0xFF0F172A)
val DarkSurface = Color(0xFF1E293B)
val DarkSurfaceVariant = Color(0xFF334155)
val DarkOnPrimary = Color(0xFF1E3A8A)
val DarkOnBackground = Color(0xFFF1F5F9)
val DarkOnSurface = Color(0xFFE2E8F0)
val DarkOnSurfaceVariant = Color(0xFF94A3B8)
val DarkOutline = Color(0xFF475569)
val DarkPrimaryContainer = Color(0xFF1E3A8A)
val DarkOnPrimaryContainer = Color(0xFFDBEAFE)
val DarkSecondaryContainer = Color(0xFF4C1D95)
val DarkOnSecondaryContainer = Color(0xFFEDE9FE)

// ── Semantic Colors ────────────────────────────────────────
val IFIncome = Color(0xFF10B981)
val IFExpense = Color(0xFFEF4444)
val IFWarning = Color(0xFFF59E0B)
val IFSuccess = Color(0xFF10B981)
val IFError = Color(0xFFEF4444)

val IFIncomeDark = Color(0xFF34D399)
val IFExpenseDark = Color(0xFFFCA5A5)

// ── Gradients ──────────────────────────────────────────────
val IFGradientPrimary = Brush.linearGradient(listOf(IFBlue, IFPurple))
val IFGradientCard = Brush.linearGradient(listOf(IFBlueDark, IFPurple))
val IFGradientAccent = Brush.linearGradient(listOf(IFPurple, IFLilac))
val IFGradientSubtle = Brush.linearGradient(listOf(IFLilac, IFLilacLight))
val IFGradientDarkCard = Brush.linearGradient(listOf(IFBlueDark, Color(0xFF581C87)))
val IFGradientDarkSurface = Brush.linearGradient(listOf(DarkSurface, Color(0xFF312E81)))