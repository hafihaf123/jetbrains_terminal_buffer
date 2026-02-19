package org.example

class TerminalBuffer(val width: Int, val height: Int, val maxScrollback: Int) {
    private var cells = ArrayDeque<Row>(height + maxScrollback)
    var cursorPosition = Pair(0, height - 1)
        set(value) {
            val (x, y) = value
            field = Pair(x.coerceIn(0, width - 1), y.coerceIn(0, currentScrollback + height - 1))
        }
    private var currentScrollback = 0

    var foregroundColor = Color.DEFAULT
    var backgroundColor = Color.DEFAULT
    var style = Style.NORMAL

    enum class Direction { UP, DOWN, LEFT, RIGHT }

    fun moveCursor(direction: Direction, n: Int = 1) {
        cursorPosition = when (direction) {
            Direction.UP -> cursorPosition.first to cursorPosition.second + n
            Direction.DOWN -> cursorPosition.first to cursorPosition.second - n
            Direction.LEFT -> cursorPosition.first - n to cursorPosition.second
            Direction.RIGHT -> cursorPosition.first + n to cursorPosition.second
        }
    }

    fun moveCursorUp(n: Int = 1) = moveCursor(Direction.UP, n)
    fun moveCursorDown(n: Int = 1) = moveCursor(Direction.DOWN, n)
    fun moveCursorLeft(n: Int = 1) = moveCursor(Direction.LEFT, n)
    fun moveCursorRight(n: Int = 1) = moveCursor(Direction.RIGHT, n)
}