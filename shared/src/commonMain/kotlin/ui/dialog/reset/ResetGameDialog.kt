package ui.dialog.reset

import androidx.compose.runtime.Composable
import ui.dialog.WarningDialog

@Composable
fun ResetGameDialog(
    onDismiss: () -> Unit,
    onChoice: (ResetGameChoice) -> Unit
) {
    WarningDialog(
        onDismiss = onDismiss,
        title = "Reset Game",
        message = "Select an option to start a new game",
        optionOneMessage = "Same players",
        optionTwoMessage = "Different players",
        dismissOnOption = false,
        onOptionOne = {
            onChoice(choiceFor(resetPlayerPreferences = false))
        },
        onOptionTwo = {
            onChoice(choiceFor(resetPlayerPreferences = true))
        },
    )
}

fun choiceFor(
    resetPlayerPreferences: Boolean,
    chooseFirstPlayer: Boolean = true
): ResetGameChoice {
    return when {
        resetPlayerPreferences && chooseFirstPlayer -> ResetGameChoice.DifferentPlayersSelectFirst
        resetPlayerPreferences -> ResetGameChoice.DifferentPlayersSkipFirst
        chooseFirstPlayer -> ResetGameChoice.SamePlayersSelectFirst
        else -> ResetGameChoice.SamePlayersSkipFirst
    }
}
