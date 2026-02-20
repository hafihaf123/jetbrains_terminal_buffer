import org.example.Color
import org.example.Style
import org.example.TerminalBuffer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class TerminalBufferTest {
    private lateinit var terminalBuffer: TerminalBuffer
    private val width = 20
    private val height = 10
    private val maxScrollback = 15

    private fun TerminalBuffer.assertCursorAt(x: Int, y: Int) {
        assert(cursorPosition == x to y) { "Expected cursor to be at ($x, $y), but was at (${cursorPosition.first}, ${cursorPosition.second})" }
    }

    private fun TerminalBuffer.assertBuffer(expected: String) {
        val buffer = this.toDebugString().trim('\n')
        assert(buffer == expected) { "Expected buffer to be:\n'$expected'\nbut was:\n'$buffer'" }
    }

    @BeforeEach
    fun setUp() {
        terminalBuffer = TerminalBuffer(width, height, maxScrollback)
    }

    @Nested
    inner class Initialization {
        @Test
        fun `correctly initialized all terminal buffer properties`() {
            assert(terminalBuffer.width == width)
            assert(terminalBuffer.height == height)
            assert(terminalBuffer.maxScrollback == maxScrollback)
            assert(terminalBuffer.cursorPosition == 0 to height - 1)
            assert(terminalBuffer.foregroundColor == Color.DEFAULT)
            assert(terminalBuffer.backgroundColor == Color.DEFAULT)
            assert(terminalBuffer.style == Style.NORMAL)
        }
    }

    @Nested
    inner class CursorMovement {
        @Test
        fun `cursor movement up`() = with(terminalBuffer) {
            assertCursorAt(0, height - 1)

            // trying to move the cursor out of screen bounds
            moveCursorUp()
            assertCursorAt(0, height - 1)

            cursorPosition = 3 to height - 3

            moveCursorUp(2)
            assertCursorAt(3, height - 1)
        }

        @Test
        fun `cursor movement down`() = with(terminalBuffer) {
            assertCursorAt(0, height - 1)

            moveCursorDown(2)
            assertCursorAt(0, height - 3)

            cursorPosition = 3 to 0

            // trying to move the cursor out of screen bounds
            moveCursorDown(2)
            assertCursorAt(3, 0)

        }

        @Test
        fun `cursor movement left`() = with(terminalBuffer) {
            cursorPosition = 0 to height - 3

            // moving to the left when at the left-most column should correctly handle moving to the previous row
            moveCursorLeft(3)
            assertCursorAt(width - 3, height - 2)

            moveCursorLeft()
            assertCursorAt(width - 4, height - 2)

            // trying to move the cursor out of screen bounds
            cursorPosition = 0 to height - 1
            moveCursorLeft(2)
            assertCursorAt(0, height - 1)
        }

        @Test
        fun `cursor movement right`() = with(terminalBuffer) {
            cursorPosition = width - 4 to height - 2

            moveCursorRight(2)
            assertCursorAt(width - 2, height - 2)

            // moving to the right when at the right-most column should correctly handle moving to the next row
            moveCursorRight(5)
            assertCursorAt(3, height - 3)

            // trying to move the cursor out of screen bounds
            cursorPosition = width - 1 to 0
            moveCursorRight(2)
            assertCursorAt(width - 1, 0)
        }
    }

    @Nested
    inner class Scrolling {
        @Test
        fun `scrolling up`() = with(terminalBuffer) {
            cursorPosition = 0 to 0
            assertCursorAt(0, 0)

            scrollUp(2)
            assertCursorAt(0, 2)

            // scrolling up when at the top of the buffer should not change the cursor position
            scrollUp(maxScrollback)
            assertCursorAt(0, maxScrollback)
            scrollUp(5)
            assertCursorAt(0, maxScrollback)
        }

        @Test
        fun `scrolling down`() = with(terminalBuffer) {
            cursorPosition = 0 to height - 1
            assertCursorAt(0, height - 1)

            // scrolling down when at the bottom of the buffer should not change the cursor position
            scrollDown(2)
            assertCursorAt(0, height - 1)
        }
    }

    @Nested
    inner class Editing {
        @Test
        fun `adding a new row to the terminal buffer should remove the last one`() = with(terminalBuffer) {
            scrollUp(maxScrollback)
            cursorPosition = 0 to height + maxScrollback - 1
            assertCursorAt(0, height + maxScrollback - 1)
            write("test")
            assertBuffer("test")

            addRow()
            assertBuffer("")
        }

        @Test
        fun `clearing the visible portion of the terminal screen`() = with(terminalBuffer) {
            scrollUp(maxScrollback)
            cursorPosition = 0 to height + maxScrollback - 1
            assertCursorAt(0, height + maxScrollback - 1)
            write("test")
            assertBuffer("test")

            // shouldn't clear outside the screen bounds
            scrollDown()
            clearScreen()
            assertBuffer("test")

            scrollUp()
            clearScreen()
            assertBuffer("")
        }

        @Test
        fun `writing text to the terminal buffer`() = with(terminalBuffer) {
            assertCursorAt(0, height - 1)
            assertBuffer("")

            write("Hello, world!")
            assertBuffer("Hello, world!")
            assertCursorAt(13, height - 1)

            // overwriting "world" with "Brian"
            cursorPosition = 7 to height - 1
            write("Brian")
            assertBuffer("Hello, Brian!")
            assertCursorAt(12, height - 1)
        }

        @Test
        fun `writing text at the end of buffer`() = with(terminalBuffer) {
            scrollUp(maxScrollback)
            cursorPosition = 0 to height + maxScrollback - 1
            assertCursorAt(0, height + maxScrollback - 1)
            write("test")
            assertBuffer("test")

            // writing text at the end of the buffer should add a new row, deleting the last one
            scrollDown(maxScrollback)
            cursorPosition = width - 1 to 0
            assertCursorAt(width - 1, 0)
            write("Hello, world!")
            assertBuffer("H\nello, world!")
        }

        @Test
        fun `inserting text to the terminal buffer`() = with(terminalBuffer) {
            assertCursorAt(0, height - 1)
            assertBuffer("")

            insert("World")
            assertBuffer("World")
            assertCursorAt(5, height - 1)

            // inserting "Hello " before "World"
            cursorPosition = 0 to height - 1
            insert("Hello ")
            assertBuffer("Hello World")
            assertCursorAt(6, height - 1)
        }

        @Test
        fun `filling a row with a character`() = with(terminalBuffer) {
            assertBuffer("")
            fillRow('_')
            moveCursorDown()
            fillRow('-')
            assertBuffer("_".repeat(width) + "\n" + "-".repeat(width))
        }

        @Test
        fun `inserting text overflowing logic`() = with(terminalBuffer) {
            assertCursorAt(0, height - 1)
            assertBuffer("")

            // simulate some input on the first two lines
            fillRow('_')
            moveCursorDown()
            fillRow('-')

            moveCursorUp()
            insert("Hello, world!")
            assertBuffer(
                "Hello, world!" + "_".repeat(width - 13) + "\n" + "_".repeat(13) + "-".repeat(width - 13) + "\n" + "-".repeat(
                    13
                )
            )
        }

        @Test
        fun `inserting text at the end of buffer`() = with(terminalBuffer) {
            scrollUp(maxScrollback)
            cursorPosition = 0 to height + maxScrollback - 1
            assertCursorAt(0, height + maxScrollback - 1)
            insert("test")
            assertBuffer("test")

            // inserting text at the end of the buffer should add a new row, deleting the last one
            scrollDown(maxScrollback)
            cursorPosition = width - 1 to 0
            assertCursorAt(width - 1, 0)
            fillRow('_')
            insert("Hello, world!")
            assertBuffer("_".repeat(width - 1) + "H" + "\n" + "ello, world!_")
        }
    }

    @Nested
    inner class ContentAccess {
        @Test
        fun `getting the character at a given position`() = with(terminalBuffer) {
            assertCursorAt(0, height - 1)
            write("Hello, world!")
            assert(getCharacterAt(0, height - 1) == 'H')
            assert(getCharacterAt(12, height - 1) == '!')
        }

        @Test
        fun `getting the foreground color at a given position`() = with(terminalBuffer) {
            assertCursorAt(0, height - 1)
            foregroundColor = Color.RED
            write("Hello, ")
            foregroundColor = Color.DEFAULT
            write("world!")
            assert(getFgColorAt(0, height - 1) == Color.RED)
            assert(getFgColorAt(12, height - 1) == Color.DEFAULT)
        }

        @Test
        fun `getting the background color at a given position`() = with(terminalBuffer) {
            assertCursorAt(0, height - 1)
            backgroundColor = Color.RED
            write("Hello, ")
            backgroundColor = Color.DEFAULT
            write("world!")
            assert(getBgColorAt(0, height - 1) == Color.RED)
            assert(getBgColorAt(12, height - 1) == Color.DEFAULT)
        }

        @Test
        fun `getting the style at a given position`() = with(terminalBuffer) {
            assertCursorAt(0, height - 1)
            style = Style.BOLD
            write("Hello, ")
            style = Style.NORMAL
            write("world!")
            assert(getStyleAt(0, height - 1) == Style.BOLD)
            assert(getStyleAt(12, height - 1) == Style.NORMAL)
        }
    }
}