package org.example

class Row(width: Int) {
    var cells = Array(width) { CharacterCell(null, Color.DEFAULT, Color.DEFAULT, Style.NORMAL) }
        private set

    /**
     * Overwrites the cell at the specified position in the row with a new `CharacterCell`.
     *
     * @param pos The position in the row where the new `CharacterCell` should be placed.
     *            Must be within the bounds of the row's width.
     * @param cell The `CharacterCell` to replace the existing cell at the specified position.
     */
    fun overwrite(pos: Int, cell: CharacterCell) {
        require(pos >= 0 && pos <= cells.lastIndex) { "Position must be within the bounds of the row's width." }
        cells[pos] = cell
    }

    /**
     * Inserts a `CharacterCell` at the specified position in the row and shifts the existing cells
     * to the right if necessary. The last cell in the row is discarded and is returned by the method.
     *
     * @param pos The position in the row where the new `CharacterCell` should be inserted.
     *            Must be within the bounds of the row's width.
     * @param cell The `CharacterCell` to be inserted at the specified position.
     * @return The last `CharacterCell` in the row, which is discarded after the insertion.
     *         This could be used to preserve the removed cell's data if needed.
     */
    fun insert(pos: Int, cell: CharacterCell): CharacterCell {
        require(pos >= 0 && pos <= cells.lastIndex) { "Position must be within the bounds of the row's width." }

        // expecting null only at the end of the row
        val last = cells.last()
        if (cells[pos].character != null) {
            System.arraycopy(cells, pos, cells, pos + 1, cells.lastIndex - pos)
        }

        overwrite(pos, cell)
        return last
    }

    /**
     * Fills the entire row by replacing all cells with the specified `CharacterCell`.
     *
     * @param cell The `CharacterCell` to populate all positions in the row.
     */
    fun fill(cell: CharacterCell) {
        cells.fill(cell)
    }

    /**
     * Clears the row by filling all its positions with a default `CharacterCell`.
     *
     * The default `CharacterCell` has:
     * - A `null` character.
     * - Both foreground and background colors set to `Color.DEFAULT`.
     * - Style set to `Style.NORMAL`.
     */
    fun clear() {
        fill(CharacterCell(null, Color.DEFAULT, Color.DEFAULT, Style.NORMAL))
    }
}