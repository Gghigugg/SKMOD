# iLock Liquid Glass Lock Screen

A standalone Android project inside SKMOD for a premium iOS-inspired Liquid Glass lock-screen experience.

## Scope
- Lock-screen UI only; no launcher, home screen, or Control Center.
- Large adaptive clock/date, wallpaper depth-ready layout, translucent glass surfaces, notification cards, and swipe-up interaction.
- Android's real device credential / biometric security remains authoritative. This project must never store or imitate a PIN, password, or fingerprint.
- Minimum Android version: Android 8.0 (API 26); target SDK 35.

## Important implementation note
Android does not let ordinary third-party apps replace the system Keyguard completely. The first milestone is a buildable, interactive lock-screen UI preview. A later integration milestone can add an opt-in screen-on/overlay flow with explicit permissions while preserving the real system Keyguard. Do not claim that the preview currently replaces the system lock screen.

## Build
Open this folder as an Android Gradle project, or run the **Build iLock Liquid Glass APK** GitHub Actions workflow. The workflow uploads the debug APK as an artifact.

## Reference APK
The supplied iLock APK is treated as a visual/behavior reference only. Its proprietary code and assets are not copied into this clean implementation. Original APK decompilation is lossy and requires separate review/authorization before reuse.
