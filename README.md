# WaterTracker

An Android app for tracking daily water intake, built with Jetpack Compose.

## Features

- Water intake tracking with progress visualization
- History screen showing past daily intake
- Customizable daily water intake goals
- Floating bubble overlay for quick water logging
- Real-time updates between screens
- Material 3 dark theme

## Screenshots 📱

*Screenshots coming soon...*

## Download 📲

### Latest Release
Visit the [Releases page](https://github.com/losiferreira/water-tracker/releases) to download the latest APK.

### Installation Instructions
1. Download the Release APK from the releases page
2. Enable "Install from unknown sources" in Android Settings → Security
3. Install the downloaded APK file
4. Grant overlay permission when prompted (needed for floating bubble)

## System Requirements

- Android 7.0 (API 24) or higher
- Overlay permission for floating bubble feature
- ~10MB free space

## Architecture

- Frontend: Jetpack Compose with Material 3
- Database: Room with RxJava3 reactive streams
- Pattern: MVVM with Clean Architecture
- DI: Koin dependency injection

### Tech Stack
- Kotlin
- Jetpack Compose
- Material 3
- Room Database
- RxJava3
- Koin DI
- Android Architecture Components

## Development

### Building the Project
```bash
git clone git@github.com:losiferreira/water-tracker.git
cd water-tracker
./gradlew assembleDebug
```

### Creating a Release
1. Create a new tag: `git tag v1.0.0`
2. Push the tag: `git push origin v1.0.0`
3. GitHub Actions will automatically build and create a release

### Manual Release
You can trigger a manual build from the GitHub Actions tab.

## Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

## Issues

Found a bug or have a suggestion? [Open an issue](https://github.com/losiferreira/water-tracker/issues).

## License

This project is open source. See the LICENSE file for details.

## Author

Losi Ferreira - [@losiferreira](https://github.com/losiferreira)