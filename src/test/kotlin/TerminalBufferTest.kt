import org.example.Color
import org.example.Style
import org.example.TerminalBuffer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class TerminalBufferTest {
    private lateinit var terminalBuffer: TerminalBuffer
    private val with = 100
    private val height = 50
    private val maxScrollback = 150

    @BeforeEach
    fun setUp() {
        terminalBuffer = TerminalBuffer(with, height, maxScrollback)
    }

    @Nested
    inner class Initialization {
        @Test
        fun `correctly initialized all terminal buffer properties`() {
            with(terminalBuffer) {
                assert(width == 100)
                assert(height == 50)
                assert(maxScrollback == 150)
                assert(cursorPosition == 0 to 49)
                assert(foregroundColor == Color.DEFAULT)
                assert(backgroundColor == Color.DEFAULT)
                assert(style == Style.NORMAL)
            }
        }
    }

    @Nested
    inner class CursorMovement {
        fun TerminalBuffer.assertCursorAt(x: Int, y: Int) {
            assert(cursorPosition == x to y) { "Expected cursor to be at ($x, $y), but was at (${cursorPosition.first}, ${cursorPosition.second})" }
        }

        @Test
        fun `cursor movement up`() = with(terminalBuffer) {
            assertCursorAt(0, 49)

            // trying to move the cursor out of screen bounds
            moveCursorUp()
            assertCursorAt(0, 49)

            cursorPosition = 3 to 47

            moveCursorUp(2)
            assertCursorAt(3, 49)
        }

        @Test
        fun `cursor movement down`() = with(terminalBuffer) {
            assertCursorAt(0, 49)

            moveCursorDown(2)
            assertCursorAt(0, 47)

            cursorPosition = 3 to 0

            // trying to move the cursor out of screen bounds
            moveCursorDown(2)
            assertCursorAt(3, 0)

        }

        @Test
        fun `cursor movement left`() = with(terminalBuffer) {
            cursorPosition = 0 to 47

            // moving to the left when at the left-most column should correctly handle moving to the previous row
            moveCursorLeft(3)
            assertCursorAt(97, 48)

            moveCursorLeft()
            assertCursorAt(96, 48)

            // trying to move the cursor out of screen bounds
            cursorPosition = 0 to 49
            moveCursorLeft(2)
            assertCursorAt(0, 49)
        }

        @Test
        fun `cursor movement right`() = with(terminalBuffer) {
            cursorPosition = 96 to 48

            moveCursorRight(2)
            assertCursorAt(98, 48)

            // moving to the right when at the right-most column should correctly handle moving to the next row
            moveCursorRight(5)
            assertCursorAt(3, 47)

            // trying to move the cursor out of screen bounds
            cursorPosition = 99 to 0
            moveCursorRight(2)
            assertCursorAt(99, 0)
        }
    }
}