package com.wllpwr.gameolife

data class Cell constructor(
    var row: Int = 0,
    var column: Int = 0,
    var living: Boolean = false,
)