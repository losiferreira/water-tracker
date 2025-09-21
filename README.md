# WaterTracker 💧

A beautiful Android water intake tracking app built with Jetpack Compose and Material 3 design.

## Features 🚀

- 💧 **Water Tracking**: Beautiful progress visualization with animated gradients
- 📊 **History Screen**: View past water intake with gorgeous progress bars  
- ⚙️ **Customizable Goals**: Set and update your daily water intake target
- 🫧 **Floating Bubble**: YouTube-style overlay with drag-to-remove functionality
- 🔄 **Real-time Updates**: Automatic synchronization between screens
- 🎨 **Modern UI**: Material 3 dark theme with smooth animations

## Screenshots 📱

*Screenshots coming soon...*

## Download 📲

### Latest Release
Visit the [Releases page](https://github.com/losiferreira/water-tracker/releases) to download the latest APK.

### Installation Instructions
1. Download the **Release APK** for best performance
2. Enable "Install from unknown sources" in Android Settings → Security
3. Install the downloaded APK file
4. Grant overlay permission when prompted (required for floating bubble)

## System Requirements 📋

- **Android Version**: 7.0 (API 24) or higher
- **Permissions**: Overlay permission for floating bubble feature
- **Storage**: ~10MB free space

## Architecture 🏗️

- **Frontend**: Jetpack Compose with Material 3
- **Backend**: Room database with RxJava3 reactive streams
- **Pattern**: MVVM with Clean Architecture
- **DI**: Koin dependency injection

### Tech Stack
- Kotlin
- Jetpack Compose
- Material 3
- Room Database
- RxJava3
- Koin DI
- Android Architecture Components

## Development 👨‍💻

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
You can also trigger a manual build from the GitHub Actions tab using the "Build APK (No Signing)" workflow.

## Contributing 🤝

1. Fork the project
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Issues 🐛

Found a bug or have a suggestion? [Open an issue](https://github.com/losiferreira/water-tracker/issues)!

## License 📄

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author ✨

**Losi Ferreira** - [@losiferreira](https://github.com/losiferreira)

---

💡 **Tip**: Use the floating bubble to quickly add water without opening the app!