package com.isu.apitracker

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em

fun TextUnit.toEm(): TextUnit {
    return this.value.times(6.25 / 35).em
}