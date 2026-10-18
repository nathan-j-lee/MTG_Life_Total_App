package domain.game.timer

import domain.storage.PreferencesRepository
import domain.storage.TestSettings
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TimerManagerTest {
    @Test
    fun restartCanSelectFirstPlayerWithoutEnablingTheTimer() = runTest {
        val preferences = PreferencesRepository(TestSettings()).also { it.setNumPlayers(4) }
        val repository = TimerStateRepository(TestSettings())
        val manager = TimerManager(repository, preferences)
        val host = FakeTimerManagerHost(playerCount = 4)
        manager.attach(host)
        manager.onTimerEnabledChange(false)
        manager.reset(chooseFirstPlayer = true)
        assertEquals(1, host.promptCount)
        manager.handleFirstPlayerSelection(2, TurnOrder.COUNTERCLOCKWISE)
        assertNull(host.activePlayerIndex)
        assertNull(host.activeTimer)
        assertEquals(2, repository.load()?.firstPlayer)
        assertEquals(TurnOrder.COUNTERCLOCKWISE, repository.load()?.turnOrder)

        manager.onTimerEnabledChange(true)
        assertEquals(2, host.activePlayerIndex)
        assertNotNull(host.activeTimer)
        assertEquals(1, host.promptCount)
        manager.detach()
    }

    @Test
    fun selectedCounterclockwiseOrderIsUsedAndPersisted() = runTest {
        val preferences = PreferencesRepository(TestSettings()).also { it.setNumPlayers(4) }
        val repository = TimerStateRepository(TestSettings())
        val manager = TimerManager(repository, preferences)
        val host = FakeTimerManagerHost(playerCount = 4)
        manager.attach(host)
        manager.onTimerEnabledChange(true)
        manager.handleFirstPlayerSelection(0, TurnOrder.COUNTERCLOCKWISE)
        manager.moveTimer()
        assertEquals(3, host.activePlayerIndex)
        assertEquals(TurnOrder.COUNTERCLOCKWISE, repository.load()?.turnOrder)
        manager.detach()
    }

    @Test
    fun promptsForFirstPlayerAndPublishesActiveTimerThroughHost() = runTest {
        val preferencesRepository = PreferencesRepository(TestSettings()).also {
            it.setNumPlayers(2)
        }
        val manager = TimerManager(
            timerStateRepository = TimerStateRepository(TestSettings()),
            preferencesRepository = preferencesRepository
        )
        val host = FakeTimerManagerHost(playerCount = 2)
        manager.attach(host)

        manager.onTimerEnabledChange(true)
        manager.handleFirstPlayerSelection(1)

        assertEquals(1, host.promptCount)
        assertEquals(1, host.clearFirstPlayerPromptCount)
        assertEquals(1, host.activePlayerIndex)
        assertNotNull(host.activeTimer)
        manager.detach()
    }

    @Test
    fun skipsDeadPlayersWhenMovingTimer() = runTest {
        val preferencesRepository = PreferencesRepository(TestSettings()).also {
            it.setNumPlayers(3)
        }
        val manager = TimerManager(
            timerStateRepository = TimerStateRepository(TestSettings()),
            preferencesRepository = preferencesRepository
        )
        val host = FakeTimerManagerHost(playerCount = 3, deadPlayers = setOf(2))
        manager.attach(host)

        manager.onTimerEnabledChange(true)
        manager.handleFirstPlayerSelection(1)
        manager.moveTimer()

        assertEquals(0, host.activePlayerIndex)
        manager.detach()
    }

    @Test
    fun resetClearsVisibleAndPersistedTimerBeforePromptingAgain() = runTest {
        val preferencesRepository = PreferencesRepository(TestSettings()).also {
            it.setNumPlayers(2)
            it.setTurnTimer(true)
        }
        val timerStateRepository = TimerStateRepository(TestSettings())
        val manager = TimerManager(
            timerStateRepository = timerStateRepository,
            preferencesRepository = preferencesRepository
        )
        val host = FakeTimerManagerHost(playerCount = 2)
        manager.attach(host)

        manager.onTimerEnabledChange(true)
        manager.handleFirstPlayerSelection(0)
        manager.reset()

        assertEquals(2, host.promptCount)
        assertNull(host.activePlayerIndex)
        assertNull(host.activeTimer)
        val savedState = timerStateRepository.load()
        assertNull(savedState?.firstPlayer)
        assertNull(savedState?.activePlayerIndex)
        assertNull(savedState?.turnTimer)
        manager.detach()
    }
}

private class FakeTimerManagerHost(
    override val playerCount: Int,
    private val deadPlayers: Set<Int> = emptySet()
) : TimerManagerHost {
    var promptCount = 0
        private set
    var clearFirstPlayerPromptCount = 0
        private set
    var activePlayerIndex: Int? = null
        private set
    var activeTimer: TurnTimer? = null
        private set

    override fun isPlayerDead(index: Int): Boolean {
        return index in deadPlayers
    }

    override fun promptForFirstPlayer() {
        promptCount += 1
    }

    override fun clearFirstPlayerPrompt() {
        clearFirstPlayerPromptCount += 1
    }

    override fun showTimer(activePlayerIndex: Int?, timer: TurnTimer?) {
        this.activePlayerIndex = activePlayerIndex
        activeTimer = timer
    }
}
