package minesweeper

enum class MarkResponse(val response: String) {
    ACTION_SUCCEEDED(""),
    WIN("Congratulations! You found all the mines!"),
    LOSS("You stepped on a mine and failed!");

    fun getMessage(): String = response
}