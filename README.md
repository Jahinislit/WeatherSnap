# WeatherSnap

A polished Android app for creating live weather reports with camera evidence. Built with Kotlin, Jetpack Compose, and modern Android architecture.

## Features

- **Live Weather Search** — Search cities using Open-Meteo geocoding API with debounced autocomplete
- **City Suggestion Caching** — Room DB cache prevents repeated API calls for the same query
- **Weather Display** — Temperature, condition, humidity, wind speed, and pressure
- **Custom Camera** — CameraX-based camera (no device camera intent) with capture functionality
- **Image Compression** — Captured images are compressed with configurable quality; original and compressed sizes are displayed
- **Weather Reports** — Save reports with weather snapshot, captured photo, and field notes to Room DB
- **Saved Reports** — View all saved reports with full details, timestamps, and image sizes

## Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Primary language |
| Jetpack Compose | UI framework |
| MVVM | Architecture pattern |
| ViewModel + StateFlow | State management |
| Coroutines | Async operations |
| Hilt | Dependency injection |
| Navigation Compose | Screen navigation |
| Retrofit + Gson + OkHttp | Networking |
| Room | Local database |
| CameraX | Custom camera |
| Material 3 | Design system |
| Coil | Image loading |

## Setup & Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34
- An Android device or emulator (API 26+)

### Steps
1. Clone or extract the project
2. Open the `WeatherSnap` folder in Android Studio
3. Let Gradle sync complete
4. Connect a device or start an emulator
5. Click **Run** (▶️) or use `./gradlew installDebug`

### API
No API key setup is needed. The app uses [Open-Meteo](https://open-meteo.com/) which is free and keyless.

## Architecture

```
com.weathersnap/
├── di/              # Hilt dependency injection modules
├── data/
│   ├── local/       # Room database, DAOs, entities
│   ├── remote/      # Retrofit API, DTOs
│   └── repository/  # Data repositories
├── domain/model/    # Domain models
├── ui/
│   ├── theme/       # Material 3 theme, colors, typography
│   ├── components/  # Shared composables
│   ├── navigation/  # NavGraph
│   ├── weather/     # Weather search screen
│   ├── report/      # Create report screen
│   ├── camera/      # Custom CameraX screen
│   └── saved/       # Saved reports screen
└── util/            # Utilities (image compression, weather codes)
```

## Debug Features

- **Network Logging**: OkHttp logging interceptor is active only in debug builds, logging full request/response bodies for development debugging

## Screens

1. **Weather Screen** — City search with autocomplete, weather display, report actions
2. **Create Report** — Weather snapshot, photo capture/preview, notes, save
3. **Custom Camera** — CameraX live preview with capture
4. **Saved Reports** — All saved reports with full details


