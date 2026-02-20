package org.example


fun main() {
    val buffer = TerminalBuffer(width = 20, height = 5, maxScrollback = 10)

    println("--- 1. Writing basic text ---")
    buffer.write("Hello Kotlin!")
    println(buffer.getScreen())
    println("Cursor is now at: ${buffer.cursorPosition}")

    println("\n--- 2. Moving cursor and writing ---")
    buffer.cursorPosition = Pair(2, 3)
    buffer.foregroundColor = Color.BLUE
    buffer.write("Blue Text")
    buffer.foregroundColor = Color.DEFAULT
    println(buffer.getScreen())

    println("\n--- 3. Changing background color and style and overwriting ---")
    buffer.moveCursorLeft(4)
    buffer.backgroundColor = Color.BRIGHT_GREEN
    buffer.style = Style.BOLD
    buffer.write("Done")
    buffer.backgroundColor = Color.DEFAULT
    buffer.style = Style.NORMAL
    println(buffer.getScreen())

    println("\n--- 3. Demonstrating Text Insertion (Shifting) ---")
    buffer.cursorPosition = Pair(0, 3)
    buffer.insert("[INSERTED] ")
    println(buffer.getScreen())

    println("\n--- 4. Scrolling and Buffer Management ---")
    buffer.scrollUp(4)
    println("Current visible screen after scrolling up 4 lines:")
    println(buffer.getScreen())

    println("\n--- 5. Row Operations ---")
    buffer.cursorPosition = Pair(0, 4)
    buffer.fillRow('-')
    println(buffer.getScreen())

    println("\n--- 6. Inspection ---")
    var cellChar = buffer.getCharacterAt(0, 4)
    println("Character at (0,4): $cellChar")
    cellChar = buffer.getCharacterAt(1, 3)
    println("Character at (1,3): $cellChar")

    println("\n--- 7. Clearing ---")
    buffer.clearScreen()
    println("Cleared screen.")
    println("Current visible screen:")
    println(buffer.getScreen())
    println("Current buffer:")
    println(buffer)
    buffer.clearBuffer()
    println("Buffer cleared.")
    println("Current buffer:")
    println(buffer)
}