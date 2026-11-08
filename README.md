# FamilyCall Android Test v0.1

Supervised test app for BLU View 2 / Android 10.

## Build
Open Actions > Build FamilyCall Test APK. The workflow runs on push to main, or use Run workflow. Download the FamilyCall-Test-APK artifact and extract the APK.

## Test
Install the debug APK on the phone. Parent Mode PIN: 1234 (temporary test PIN only). Add an approved contact and tap Call. Note whether Android opens Google Voice, Phone, or an app chooser. Also test Open Google Voice separately.

**Important:** This is not a secure child kiosk. The PIN is a demonstration PIN, and contacts are stored locally in WebView storage. This first integration test does not yet include contact photos, quiet hours, or actual call history. Google Voice may not accept ACTION_DIAL intents; opening Google Voice separately does not auto-dial an approved number. Do not give the phone to children unsupervised until restrictions are verified.
