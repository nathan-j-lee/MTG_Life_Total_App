package domain.game.timer

import kotlinx.serialization.Serializable

@Serializable
enum class TurnOrder(val step: Int) {
    CLOCKWISE(1),
    COUNTERCLOCKWISE(-1)
}
