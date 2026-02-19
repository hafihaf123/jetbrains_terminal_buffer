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
}