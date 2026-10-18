package ui.dialog

import kotlin.test.Test
import kotlin.test.assertEquals
import ui.dialog.reset.ResetGameChoice
import ui.dialog.reset.choiceFor

class DialogNavigationTest {
    @Test
    fun resetDifferentPlayersAndSelectFirstPlayerHasExplicitChoice() {
        val choice = choiceFor(resetPlayerPreferences = true)

        assertEquals(ResetGameChoice.DifferentPlayersSelectFirst, choice)
    }

    @Test
    fun resetSamePlayersSelectsFirstPlayerWithoutAnExtraSkipStep() {
        val choice = choiceFor(resetPlayerPreferences = false)

        assertEquals(ResetGameChoice.SamePlayersSelectFirst, choice)
    }
}
