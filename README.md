# FlareBrightness (Fabric, Minecraft 26.3, Java 25)

Press B (rebindable) to open the settings screen. Slider -500..1500, 0 = normal.
Apply saves to config/flarebrightness.json. Cancel / Esc reverts the live preview.

## Build
1. Go to https://fabricmc.net/develop , pick Minecraft 26.3, generate the template.
2. Copy this project's `src/` folder over the template's `src/` (replace fabric.mod.json).
3. Run `./gradlew build` -> jar in build/libs/ (use the non-"sources" jar).
