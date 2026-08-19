# RELEASE_PROCESS.md — Hermes Android

## Local toolchain (Termux)
```bash
pkg install openjdk-17 gradle
# Android SDK via commandline-tools:
curl -LO https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-*-latest.zip -d $HOME/android-sdk
yes | $HOME/android-sdk/cmdline-tools/bin/sdkmanager --licenses
$HOME/android-sdk/cmdline-tools/bin/sdkmanager \
  "platform-tools" "platforms;android-34" "build-tools;34.0.0"
export ANDROID_HOME=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
```
Use `gradle wrapper --gradle-version 8.9` then `./gradlew` (downloads Gradle 8.9).

## Version bump
Edit `app/build.gradle.kts`:
```kotlin
android.defaultConfig.versionCode = N+1
android.defaultConfig.versionName = "X.Y.Z"
```
Update `CHANGELOG.md`.

## Tag & release
```bash
git tag vX.Y.Z
git push origin vX.Y.Z
```
→ `release.yml` triggers:
1. `./gradlew assembleRelease bundleRelease`
2. Sign with keystore (from GitHub secrets)
3. Create GitHub Release with auto changelog
4. Upload `app-debug.apk`, `app-release.aab`, universal release APK

## Keystore
Generate once:
```bash
keytool -genkey -v -keystore hermes-release.keystore -alias hermes \
  -keyalg RSA -keysize 2048 -validity 10000
```
Base64 to secret:
```bash
base64 -w0 hermes-release.keystore > keystore.b64
```
GitHub secrets:
- `SIGNING_KEYSTORE_BASE64`
- `KEY_ALIAS`
- `KEY_PASSWORD`
- `STORE_PASSWORD`
`release.yml` base64-decodes into file, signs.

## In-app update check
`GET https://api.github.com/repos/sahinmehemood/App-agnets/releases/latest`
parse `tag_name` (semver) vs installed `versionName` → if newer, prompt download
→ `PackageInstaller` (API 21+) for sideload install.

## APK size budget
Debug < 25MB, Release < 12MB (R8 + baseline profiles in CI).

## Distribution
- GitHub Releases (primary, sideload)
- (Optional later) Play Console internal testing, F-Droid metadata
