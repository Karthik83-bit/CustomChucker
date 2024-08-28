package com.isu.apitracker.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TextUnit.toEm(): TextUnit {
    return (this.value/ LocalDensity.current.fontScale).sp
}