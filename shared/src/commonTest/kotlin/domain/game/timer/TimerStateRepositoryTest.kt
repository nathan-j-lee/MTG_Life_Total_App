package domain.game.timer

import domain.storage.TestSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TimerStateRepositoryTest {
    @Test
    fun savesLoadsAndClearsTimerState() {
        val settings = TestSettings()
        val repository = TimerStateRepository(settings)
        val state = GameTimerState(
            firstPlayer = 1,
            activePlayerIndex = 2,
            turnTimer = TurnTimer(seconds = 12, turn = 3),
            turnOrder = TurnOrder.COUNTERCLOCKWISE
        )

        repository.save(state)

        assertEquals(state, repository.load())

        repository.save(null)

        assertNull(repository.load())
        assertNull(settings.getStringOrNull(TimerStateRepository.StateKey))
    }

    @Test
    fun legacyTimerStateWithoutDirectionDefaultsToClockwise() {
        val settings = TestSettings()
        settings.putString(TimerStateRepository.StateKey,
            """{"firstPlayer":0,"activePlayerIndex":1,"turnTimer":{"seconds":12,"turn":3}}""")
        val restored = TimerStateRepository(settings).load()
        assertEquals(TurnOrder.CLOCKWISE, restored?.turnOrder)
        assertEquals(1, restored?.activePlayerIndex)
        assertEquals(TurnTimer(12, 3), restored?.turnTimer)
    }

    @Test
    fun preservesCorruptTimerPayloadAndResetsTimerOnly() {
        val settings = TestSettings()
        settings.putString(TimerStateRepository.StateKey, "{broken")
        val repository = TimerStateRepository(settings)

        val state = repository.load()

        assertNull(state)
        assertEquals("{broken", settings.getString(TimerStateRepository.CorruptStateKey, ""))
        assertNull(settings.getStringOrNull(TimerStateRepository.StateKey))
    }
}
