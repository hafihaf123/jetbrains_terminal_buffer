package org.example

class TerminalBuffer(val width: Int, val height: Int, val maxScrollback: Int) {
    private var cells = ArrayDeque<Row>(height + maxScrollback)
    var cursorPosition = Pair(0, height - 1)
        set(value) {
            val (x, y) = value
            field = Pair(x.coerceIn(0, width - 1), y.coerceIn(currentScrollback, currentScrollback + height - 1))
        }
    private var currentScrollback = 0

    var foregroundColor = Color.DEFAULT
    var backgroundColor = Color.DEFAULT
    var style = Style.NORMAL

    enum class Direction { UP, DOWN, LEFT, RIGHT }

    /**
     * Moves the cursor in the specified direction by the given number of steps.
     *
     * @param direction The direction in which to move the cursor. Can be one of the values: UP, DOWN, LEFT, or RIGHT.
     * @param n The number of steps to move the cursor in the specified direction. Defaults to 1 if not provided.
     */
    fun moveCursor(direction: Direction, n: Int = 1) {
        cursorPosition = when (direction) {
            Direction.UP -> cursorPosition.first to cursorPosition.second + n
            Direction.DOWN -> cursorPosition.first to cursorPosition.second - n
            Direction.LEFT -> {
                val adjustedX = cursorPosition.first - n
                val x = adjustedX.mod(width)
                val y = cursorPosition.second - adjustedX.floorDiv(width)

                x to y
            }

            Direction.RIGHT -> {
                val adjustedX = cursorPosition.first + n
                val x = adjustedX.mod(width)
                val y = cursorPosition.second + adjustedX.floorDiv(width)

                x to y
            }
        }
    }

    /**
     * Moves the cursor up by the specified number of steps within the terminal buffer.
     *
     * @param n The number of steps to move the cursor up. Defaults to 1 if not provided.
     */
    fun moveCursorUp(n: Int = 1) = moveCursor(Direction.UP, n)

    /**
     * Moves the cursor down by the specified number of steps within the terminal buffer.
     *
     * @param n The number of steps to move the cursor down. Defaults to 1 if not provided.
     */
    fun moveCursorDown(n: Int = 1) = moveCursor(Direction.DOWN, n)

    /**
     * Moves the cursor left by the specified number of steps within the terminal buffer.
     *
     * @param n The number of steps to move the cursor left. Defaults to 1 if not provided.
     */
    fun moveCursorLeft(n: Int = 1) = moveCursor(Direction.LEFT, n)

    /**
     * Moves the cursor right by the specified number of steps within the terminal buffer.
     *
     * @param n The number of steps to move the cursor right. Defaults to 1 if not provided.
     */
    fun moveCursorRight(n: Int = 1) = moveCursor(Direction.RIGHT, n)

    /**
     * Scrolls the terminal buffer content upward by the specified number of lines.
     *
     * @param n The number of lines to scroll up. Defaults to 1 if not provided.
     */
    fun scrollUp(n: Int = 1) {
        currentScrollback = (currentScrollback + n).coerceAtMost(maxScrollback)
    }

    /**
     * Scrolls the terminal buffer content downward by the specified number of lines.
     *
     * @param n The number of lines to scroll down. Defaults to 1 if not provided.
     */
    fun scrollDown(n: Int = 1) {
        currentScrollback = (currentScrollback - n).coerceAtLeast(0)
    }
}