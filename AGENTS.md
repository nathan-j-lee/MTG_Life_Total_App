# LifeLinked project guidance

## Project

LifeLinked is a Magic: The Gathering life-tracking app built with Kotlin
Multiplatform and Compose Multiplatform for Android and iOS.

- Shared UI and game logic: `shared/src/commonMain/kotlin/`.
- Android app: `androidApp/` and `shared/src/androidMain/`.
- iOS app: `iosApp/` and `shared/src/iosMain/`.
- Team repository (`origin`): https://github.com/nathan-j-lee/MTG_Life_Total_App
- Original project (`upstream`): https://github.com/ntietje1/MTG_Life_Total_App
- Android development is configured on Windows. iOS builds require macOS and Xcode.

## Features added so far

### Life controls

- Left and right are relative to each player's text, including rotated counters.
- The left 35% of the player's counter decreases life by one per tap.
- The right 35% increases life by one per tap.
- Holding either side repeats the life change and retains haptic feedback.
- The middle 30% is separate from the life controls and never changes life.
- Commander damage retains its existing top/bottom controls.
- The tutorial describes the new life controls and middle-tap turn passing.

### Turn timer

- The active player's timer appears at the top center of their counter,
  relative to that player's text.
- Its compact badge displays elapsed time and turn number. Player information
  has space below the badge so the timer does not cover the player name.
- With the timer enabled, tapping the middle of the active player's counter
  passes the turn to the next living player using the existing timer logic.
- Holding the middle passes only once; turn passing must not auto-repeat.
- Inactive players' middle areas do not pass the turn.
- With the timer disabled, middle taps do nothing.
- The timer badge itself remains tappable to pass the turn.
- Passing turns leaves all life totals unchanged.

## Development environment

- Gradle JVM: JetBrains JDK 21.0.11; the project requests Java 21.
- Gradle: 9.5.1, supplied by the repository's Gradle wrapper.
- Android compile/target SDK: 36; minimum SDK: 28.
- Installed Android build-tools: 36.0.0.
- IDE installed during setup: Android Studio Rabbit 1 (2026.2.1).
- Configure `JAVA_HOME`, `ANDROID_HOME`, and the Android SDK location in the
  ignored `local.properties` file on each computer. Do not commit machine paths
  or credentials.

### Virtual phone used for testing

- AVD name: `LifeLinked_Pixel_6_API_36`.
- Display name: LifeLinked Pixel 6 (Android 16).
- Device profile: Google Pixel 6.
- Android version: Android 16, API 36.
- System image: `system-images;android-36;google_apis;x86_64`.
- Architecture: x86_64; RAM: 2 GB.
- Emulator version installed during setup: 37.2.12.
- Acceleration on this Windows computer: Windows Hypervisor Platform (WHPX).
- Graphics used when launching: SwiftShader.
- ADB serial used: `emulator-5554`; check `adb devices` before assuming the serial.
- Android Studio may display the connected device as `Google sdk_gphone64_x86_64`.

## Build and run

Run these PowerShell commands from the repository root with Java and the Android
SDK configured. On macOS/Linux, use `./gradlew` instead of `.\gradlew.bat`.

### Build

```powershell
.\gradlew.bat :androidApp:assembleDebug --console=plain
```

The successful builds in this session used the cached dependencies:

```powershell
.\gradlew.bat :androidApp:assembleDebug --offline --console=plain
```

Use `--offline` only after dependencies have been downloaded. The APK is written
to `androidApp/build/outputs/apk/debug/androidApp-debug.apk`.

### Start the virtual phone

```powershell
& "$env:ANDROID_HOME\emulator\emulator.exe" -avd LifeLinked_Pixel_6_API_36 -gpu swiftshader -no-audio
```

Launch the emulator from the user's Windows desktop or Android Studio when a
visible window is needed. Emulator processes started inside the Codex execution
session previously ran without a window visible on the user's desktop. Do not
start another copy of an already-running AVD.

### Install and launch

With Android platform-tools on `PATH`:

```powershell
adb devices
adb -s emulator-5554 shell getprop sys.boot_completed
```

Wait for `sys.boot_completed` to return `1`, then run:

```powershell
adb -s emulator-5554 install -r -t .\androidApp\build\outputs\apk\debug\androidApp-debug.apk
adb -s emulator-5554 shell am start -W -n com.hypeapps.lifelinked/.MainActivity
```

Alternatively, build and install using Gradle, then launch using ADB:

```powershell
.\gradlew.bat :androidApp:installDebug --console=plain
adb -s emulator-5554 shell am start -n com.hypeapps.lifelinked/.MainActivity
```

### Local helpers used during this setup

The parent workspace contains `outputs/`, `tools/`, and `work/` directories.
These are outside the Git repository and are not included when a collaborator
clones it. Use the portable commands above on other computers.

On this computer, the environment can be loaded from the repository root with:

```powershell
. ..\outputs\dev-env.ps1
```

Windows launchers in that parent `outputs/` folder include `Open Project.cmd`,
`Start Virtual Device.cmd`, `Build Debug.cmd`, and `Run App.cmd`. Double-click
the `.cmd` file in File Explorer; it calls the companion PowerShell script.

When the native ADB client could not resolve its Windows profile inside Codex,
the following commands successfully used Android Studio's existing local ADB
server. They require the local `work/adb-client.cjs` helper and a running server:

```powershell
node ..\work\adb-client.cjs devices
node ..\work\adb-client.cjs push androidApp\build\outputs\apk\debug\androidApp-debug.apk /data/local/tmp/lifelinked-debug.apk
node ..\work\adb-client.cjs shell pm install -r -t /data/local/tmp/lifelinked-debug.apk
node ..\work\adb-client.cjs shell am start -W -n com.hypeapps.lifelinked/.MainActivity
```

## Verification

For control changes, build the debug APK and test on the virtual phone. Verify
left/right taps, repeating side holds, and that the middle never changes life.
For timer changes, verify active-player passing, inactive-player taps, a single
advance on a middle hold, correct timer placement, and unchanged life totals.
Restore life totals changed during testing. These behaviors were checked on the
Android 16 virtual phone after the life-control and timer changes.

Additional existing automated checks can be run when relevant; these are not a
claim that the full suite was run during this setup:

```powershell
.\gradlew.bat :shared:testAndroidHostTest :androidApp:testDebugUnitTest --console=plain
.\gradlew.bat :androidApp:connectedDebugAndroidTest --console=plain
```

## TODO
