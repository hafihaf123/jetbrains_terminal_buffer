# JetBrains Terminal Buffer

A core data structure implementation for a terminal emulator, written in Kotlin. This project fulfills the requirements for a terminal text buffer that manages a visible screen grid, configurable scrollback history, text insertion, and ANSI styling attributes.

## Features

* **Zero-Allocation Rendering Cycle:** Utilizes a fixed-size `ArrayDeque` with row-recycling to prevent Garbage Collection pauses during high-throughput text output.
* **Scrollback History:** Preserves history up to a configurable maximum, allowing simulated scrolling up and down.
* **Rich Text Attributes:** Supports 16 standard terminal foreground/background colors and text styles (Bold, Italic, Underline, etc.).
* **Smart Text Insertion:** Handles line wrapping, character shifting, and overflow into new lines automatically while maintaining cursor bounds.
* **Comprehensive Test Suite:** High coverage using JUnit 5, testing edge cases like boundary cursor movements, text overflow, and scrollback constraints.

## Architecture Highlights

* **Bottom-Up Coordinates:** The internal buffer treats `y = 0` as the **bottom-most** row of the terminal, and `y = height - 1` as the top of the visible screen. This allows pushing new lines to the bottom to be a simple `addFirst()` operation on the internal Deque.
* **Data Classes:** Cells are managed via the `CharacterCell` data class, making state manipulation strictly typed and readable.

## Getting Started

### Prerequisites
* JDK 25 (configured via Gradle Toolchains)
* Gradle (Wrapper included)

### Building the Project
To compile the project and download dependencies:

```bash
./gradlew build
```

### Running the Tests
To execute the JUnit test suite:

```bash
./gradlew test
```

### Running the Demo
A simple demonstration of the buffer manipulating text and colors is provided in `Main.kt`.

```bash
./gradlew run
```

## Project Structure

* `src/main/kotlin/org/example/TerminalBuffer.kt`: Core buffer implementation.
* `src/main/kotlin/org/example/Row.kt`: Manages an individual line of terminal cells.
* `src/main/kotlin/org/example/CharacterCell.kt`: Data model for a single terminal character and its attributes.
* `src/test/kotlin/TerminalBufferTest.kt`: Comprehensive unit tests.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
