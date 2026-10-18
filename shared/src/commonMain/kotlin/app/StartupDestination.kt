package app

import model.VersionNumber

// Keep the legacy preference arguments compatible with callers, but never route
// a new session to the retired press-and-hold screen.
@Suppress("UNUSED_PARAMETER")
fun startupDestination(
    currentVersion: VersionNumber,
    lastSplashScreenShown: String,
    autoSkip: Boolean,
    gameStarted: Boolean
): LifeLinkedRoute {
    return if (!currentVersion.isSame(VersionNumber(lastSplashScreenShown))) {
        LifeLinkedRoute.Splash
    } else {
        LifeLinkedRoute.LifeCounter
    }
}
