package org.example

class TerminalBuffer(val width: Int, val height: Int, val maxScrollback: Int) {
    private var rows = ArrayDeque<Row>(height + maxScrollback)

    init {
        repeat(height + maxScrollback) {
            rows.addLast(Row(width))
        }
    }

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

                if (y >= currentScrollback + height) {
                    0 to y
                } else {
                    x to y
                }
            }

            Direction.RIGHT -> {
                val adjustedX = cursorPosition.first + n
                val x = adjustedX.mod(width)
                val y = cursorPosition.second - adjustedX.floorDiv(width)

                if (y < currentScrollback) {
                    width - 1 to y
                } else {
                    x to y
                }
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
                cursorPosition.first, CharacterCell(c, foregroundColor, backgroundColor, style)
            )

            if (cursorPosition.first == width - 1 && cursorPosition.second == 0) addRow()
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
                cursorPosition.first, CharacterCell(c, foregroundColor, backgroundColor, style)
            )
            var overflowRowIndex = cursorPosition.second + 1
            while (overflowed.character != null) {
                if (overflowRowIndex >= rows.size) addRow()

                overflowed = rows[overflowRowIndex].insert(0, overflowed)
                overflowRowIndex++
            }

            if (cursorPosition.first == width - 1 && cursorPosition.second == 0) addRow()
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

    /**
     * Adds a new row to the terminal buffer while reusing an existing, recycled row to maintain
     * a consistent buffer size and improve performance.
     *
     * The first row from the buffer is removed, cleared, and repopulated with default character cells,
     * then added back to the end of the buffer. This ensures the buffer retains a fixed size by
     * recycling rows instead of creating new ones.
     *
     * The cleared row is populated using the default `CharacterCell` configuration, which includes:
     * - `character = null`
     * - `foregroundColor = Color.DEFAULT`
     * - `backgroundColor = Color.DEFAULT`
     * - `style = Style.NORMAL`
     */
    fun addRow() {
        val recycledRow = rows.removeFirst()
        recycledRow.clear()
        rows.addLast(recycledRow)
    }

    /**
     * Clears the visible portion of the terminal screen starting from the current scrollback position.
     *
     * Iterates over all rows in the buffer from the current scrollback index to the total number of rows
     * and clears their content. Clearing a row resets its cells to their default state, including
     * character, foreground color, background color, and style.
     *
     * This method does not modify rows outside the visible portion of the terminal.
     */
    fun clearScreen() {
        for (i in currentScrollback until rows.size) {
            rows[i].clear()
        }
    }

    /**
     * Clears the entire terminal buffer by resetting the content of all rows to their default state.
     *
     * Each row in the buffer is iterated over and cleared. Clearing a row involves resetting
     * all its cells to their default configuration, which typically includes:
     * - `character = null`
     * - `foregroundColor = Color.DEFAULT`
     * - `backgroundColor = Color.DEFAULT`
     * - `style = Style.NORMAL`
     *
     * This method operates on all rows in the buffer, including those in the scrollback area,
     * effectively ensuring a completely clean state for the terminal buffer.
     */
    fun clearBuffer() {
        rows.forEach { it.clear() }
    }

    /**
     * Retrieves the character cell at the specified coordinates in the terminal buffer.
     *
     * @param x The horizontal position of the cell, where 0 is the leftmost column.
     * @param y The vertical position of the cell, where 0 is the bottom-most row.
     * @return The `CharacterCell` object of the specified cell.
     *         This includes the character, foreground color, background color, and style information.
     */
    fun getCellAt(x: Int, y: Int): CharacterCell = rows[y].cells[x]

    /**
     * Retrieves the character at the specified coordinates in the terminal buffer.
     *
     * @param x The horizontal position of the cell, where 0 is the leftmost column.
     * @param y The vertical position of the cell, where 0 is the bottom-most row.
     * @return The character of the specified cell, or null if the cell is empty.
     */
    fun getCharacterAt(x: Int, y: Int): Char? = getCellAt(x, y).character

    /**
     * Retrieves the foreground color at the specified coordinates in the terminal buffer.
     *
     * @param x The horizontal position of the cell, where 0 is the leftmost column.
     * @param y The vertical position of the cell, where 0 is the bottom-most row.
     * @return The foreground color of the specified cell.
     */
    fun getFgColorAt(x: Int, y: Int): Color = getCellAt(x, y).foregroundColor

    /**
     * Retrieves the background color at the specified coordinates in the terminal buffer.
     *
     * @param x The horizontal position of the cell, where 0 is the leftmost column.
     * @param y The vertical position of the cell, where 0 is the bottom-most row.
     * @return The background color of the specified cell.
     */
    fun getBgColorAt(x: Int, y: Int): Color = getCellAt(x, y).backgroundColor

    /**
     * Retrieves the style of the character cell at the specified coordinates in the terminal buffer.
     *
     * @param x The horizontal position of the cell, where 0 is the leftmost column.
     * @param y The vertical position of the cell, where 0 is the bottom-most row.
     * @return The style applied to the specified cell.
     */
    fun getStyleAt(x: Int, y: Int): Style = getCellAt(x, y).style

    /**
     * Retrieves the content of a specific row in the terminal buffer as a string.
     *
     * @param y The index of the row to retrieve. The index is zero-based,
     * where 0 corresponds to the bottom-most row in the buffer.
     * @return The content of the specified row as a string.
     */
    fun getLine(y: Int): String = rows[y].toString()

    /**
     * Retrieves the visible content of the terminal screen as a string.
     *
     * The method collects rows from the terminal buffer starting at the current
     * scrollback position up to the visible height of the terminal. Each row's
     * content is joined into a single string, with rows separated by newlines.
     *
     * @return A string representation of the visible terminal screen content,
     *         with each row separated by a newline.
     */
    fun getScreen(): String = rows.subList(currentScrollback, currentScrollback + height).reversed()
        .joinToString(separator = "\n") { it.toString() }

    /**
     * Returns a string representation of the object.
     *
     * @return a string constructed by joining the rows with a newline separator
     */
    override fun toString(): String = rows.reversed().joinToString(separator = "\n")
}