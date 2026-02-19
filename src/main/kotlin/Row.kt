package org.example

class Row(width: Int) {
    val cells = Array(width) { CharacterCell(null, Color.DEFAULT, Color.DEFAULT, Style.NORMAL) }
}