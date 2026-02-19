package org.example

class TerminalBuffer(val width: Int, val height: Int, val maxScrollback: Int) {
    private var rows = ArrayDeque<Row>(height + maxScrollback)
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

    /**
     * Writes the specified text to the terminal buffer starting at the current cursor position, overriding the content.
     *
     * Each character in the text will overwrite the corresponding position in the buffer,
     * and the cursor will move right after writing each character.
     *
     * @param text The text to be written to the terminal buffer. Each character will be written sequentially.
     */
    fun write(text: String) {
        for (c in text) {
            rows[cursorPosition.second].overwrite(
                cursorPosition.first,
                CharacterCell(c, foregroundColor, backgroundColor, style)
            )
            moveCursorRight()
        }
    }

    /**
     * Inserts the specified text into the terminal buffer starting at the current cursor position.
     * Each character is inserted sequentially, and the cursor is moved right after each insertion.
     * Handles overflowed cells by shifting them to later rows if necessary.
     *
     * @param text The text to insert. Each character in the provided string will be inserted
     *             one by one into the buffer at the current cursor position.
     */
    fun insert(text: String) {
        for (c in text) {
            var overflowed = rows[cursorPosition.second].insert(
                cursorPosition.first,
                CharacterCell(c, foregroundColor, backgroundColor, style)
            )
            var overflowRowIndex = cursorPosition.second + 1
            while (overflowed.character != null) {
                if (overflowRowIndex >= rows.size) {
                    // TODO: add a new row
                    break
                }
                overflowed = rows[overflowRowIndex].insert(0, overflowed)
                overflowRowIndex++
            }
            moveCursorRight()
        }
    }

    /**
     * Fills the current row at the cursor position with the specified character, applying the current
     * foreground color, background color, and style settings to all cells in the row.
     *
     * @param char The character to fill the row with. If null, the row will be filled with empty cells.
     */
    fun fillRow(char: Char?) {
        rows[cursorPosition.second].fill(CharacterCell(char, foregroundColor, backgroundColor, style))
    }
}