package domain.game.timer

import kotlin.test.Test
import kotlin.test.assertEquals

class GameTimerTest {
    @Test
    fun bothDirectionsWrapAndCountRoundsForEveryPlayerCountAndStartingSeat() {
        for (count in 1..6) {
            for (first in 0 until count) {
                for (order in TurnOrder.entries) {
                    val timer = newTimer(count, first, order)
                    for (pass in 1..count * 2) {
                        timer.moveTimer()
                        val expected = ((first + pass * order.step) % count + count) % count
                        assertEquals(expected, timer.timerState.value.activePlayerIndex,
                            "$count players, first=$first, $order, pass=$pass")
                        assertEquals(1 + pass / count, timer.timerState.value.turnTimer?.turn)
                        assertEquals(0, timer.timerState.value.turnTimer?.seconds)
                    }
                }
            }
        }
    }

    @Test
    fun skipsEliminatedPlayersInBothDirections() {
        for (order in TurnOrder.entries) {
            val timer = newTimer(4, 0, order) { it == 1 || it == 3 }
            timer.tick()
            timer.moveTimer()
            assertEquals(2, timer.timerState.value.activePlayerIndex)
            assertEquals(1, timer.timerState.value.turnTimer?.turn)
            assertEquals(0, timer.timerState.value.turnTimer?.seconds)
            timer.moveTimer()
            assertEquals(0, timer.timerState.value.activePlayerIndex)
            assertEquals(2, timer.timerState.value.turnTimer?.turn)
        }
    }

    @Test
    fun eliminatedFirstPlayerDoesNotPreventNewRounds() {
        for (order in TurnOrder.entries) {
            var firstPlayerDead = false
            val timer = newTimer(4, 0, order) { it == 0 && firstPlayerDead }
            timer.moveTimer()
            firstPlayerDead = true
            repeat(3) { timer.moveTimer() }
            assertEquals(if (order == TurnOrder.CLOCKWISE) 1 else 3,
                timer.timerState.value.activePlayerIndex)
            assertEquals(2, timer.timerState.value.turnTimer?.turn)
        }
    }

    @Test
    fun oneRemainingPlayerAndAllEliminatedPlayersDoNotLoopForever() {
        for (order in TurnOrder.entries) {
            val remaining = newTimer(4, 2, order) { it != 2 }
            remaining.moveTimer()
            assertEquals(2, remaining.timerState.value.activePlayerIndex)
            assertEquals(2, remaining.timerState.value.turnTimer?.turn)
            val allDead = newTimer(4, 0, order) { true }
            allDead.moveTimer()
        }
    }

    @Test
    fun restoredTimerContinuesInItsChosenDirectionAndResetUsesDefault() {
        val original = newTimer(4, 0, TurnOrder.COUNTERCLOCKWISE)
        original.moveTimer()
        val restored = GameTimer(original.timerState.value)
        restored.initialize(4) { false }
        restored.moveTimer()
        assertEquals(2, restored.timerState.value.activePlayerIndex)
        restored.reset()
        assertEquals(GameTimerState(), restored.timerState.value)
    }

    private fun newTimer(
        count: Int,
        first: Int,
        order: TurnOrder,
        isDead: (Int) -> Boolean = { false },
    ) = GameTimer().apply {
        initialize(count, isDead)
        setFirstPlayer(first, order)
        setTimerEnabled(true)
    }
}
