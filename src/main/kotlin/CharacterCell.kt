package org.example

data class CharacterCell(
    val character: Char?,
    val foregroundColor: Color,
    val backgroundColor: Color,
    val style: Style
) {
    override fun toString(): String {
        val charToPrint = character ?: return ""
        val ansiPrefix = "\u001B[${style.code};${foregroundColor.fgCode};${backgroundColor.bgCode}m"
        val ansiReset = "\u001B[0m"

        return "$ansiPrefix$charToPrint$ansiReset"
    }
}
