package org.example

class TerminalBuffer(val width: Int, val height: Int, val maxScrollback: Int) {
    private var cells = ArrayDeque<Row>(height + maxScrollback)
    var cursorPosition = Pair(0, height - 1)
    private var currentScrollback = 0

    var foregroundColor = Color.DEFAULT
    var backgroundColor = Color.DEFAULT
    var style = Style.NORMAL
}