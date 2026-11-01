# Sony Alpha Matrix

Native matrix and tone-curve looks for Sony PlayMemories camera apps. This fork of
[jegly/sonyalpha-film](https://github.com/jegly/sonyalpha-film) replaces the film
catalog and LUT conversion with a simple SD-card library of matrix files.

The camera's own image processor applies a shared 1,024-point tone curve and a
3×3 RGB matrix. No full-image software grading or second JPEG copy is required.
RAW sensor data does not have the look baked in.

## Create a camera look

Open **[Alpha Matrix Studio](https://1096bimu.github.io/sonyalpha-matrix/)** to design
a look in your browser. Drag the six-color hexagon to shape the RGB matrix, adjust
its two depth controls, and use five slope bars to build a strictly increasing
tone curve anchored at black and white. Images stay in your browser.

Choose **Export .BIN**, copy the file to **`/MATRIX` at the SD-card root**, and
reopen **Matrix Test** to select it. The export contains the shared 1,024-point
tone table and 3×3 matrix in the binary format below. The browser preview uses
tone → matrix; camera rendering may differ.

The studio is hosted with GitHub Pages from [`docs/`](docs/). It is a static site
with no build step.

## Download and install

Download **MatrixTest.apk** from [Releases](https://github.com/1096bimu/sonyalpha-matrix/releases/latest).
The camera application is named **Matrix Test** and uses package
`com.filmsim.matrixcustom`, separate from the upstream Film Sim app.

### Apple Silicon Macs

Our native macOS GUI, [PMCA Swift](https://github.com/1096bimu/PMCA-Swift), can install
the APK on modern Apple Silicon platforms. Its current build requires **macOS 27**;
check that project's requirements for your Mac. It bundles its runtime, so running
it does not require Homebrew, system Python, Tk, or Terminal commands.

1. Close Photos, Lightroom and other applications accessing the camera.
2. If PMCA Swift has an active settings session, click **Disconnect**.
3. Connect a compatible camera by USB in **MTP or Mass Storage** mode.
4. In PMCA Swift, choose **Install application**, select `MatrixTest.apk`, then click **Install**.
5. Allow the requested macOS authorization/privacy prompts and leave the camera
   connected until installation completes. Unplug and launch **Matrix Test** from
   the camera's application list.

PMCA Swift installation was confirmed on an RX1R II; other bodies require testing.
Alternatively use [Sony-PMCA-RE](https://github.com/ma1co/Sony-PMCA-RE).

## Matrix files

Put files in **`/MATRIX` at the SD-card root**, not inside DCIM. The app creates the
folder and copies its bundled **CHEN.BIN** there if that filename is absent. Existing
files are never overwritten. Reopen the app to scan files added to the card.

The list starts with **No Change**: identity matrix and straight-line tone table.
A clean card initially has two choices: No Change and CHEN. The last selection and
strength are remembered. The crazy test look, brands, favourites, .cube imports,
halation, grain, light leaks and reprocessed-copy playback have been removed.

Use short filenames such as `CHEN-2.BIN` for the camera's SD-card filesystem.
Dotfiles are ignored. The current loader recognizes looks by valid binary contents,
so camera filename aliases do not need to retain a particular extension. Rejected
files and scan results are logged in **`FILMSIM/LOG.TXT`**.

### Binary specification

Exactly **4,136 bytes**, all fields signed **32-bit big-endian** integers:

| Offset | Contents |
| --- | --- |
| 0 | Format version: 1 |
| 4 | 1,024 shared RGB tone-table entries, each 0–1023 |
| 4100 | Nine row-major RGB matrix coefficients, scaled by 1024 |

Tone input sample `i` corresponds to `i/1023`; identity is `table[i] = i`.
Quantize curve output as `clamp(floor(1023*f(i/1023)+0.5), 0, 1023)`.
Matrix order is `RR RG RB / GR GG GB / BR BG BB`; quantize each coefficient with
`floor(1024*m+0.5)`. Negative matrix coefficients are supported by the file format;
body-specific hardware limits have not been established. No bias terms are stored.
The loader requires exactly the specified payload, with no trailing bytes.

The seed CHEN matrix is `[910,53,60,19,891,114,91,22,911]`; its shared curve is
extracted from the neutral axis of the supplied Dark Melon LUT, interpolated to
1,024 samples. It does not reproduce that entire 3D LUT. Identity custom gamma does
not bypass Sony's normal image development. Physical matrix/curve ordering remains
to be characterized on each body.

## Controls

- **Fn:** Matrix, Strength, WB, D-Range. Up/down selects a row; left/right changes it.
- **WB:** 5500K, 3200K, AWB.
- **Control wheel:** previous/next matrix. **C2:** strength. **C1:** bypass comparison.
- **Half-press/full-press:** autofocus/capture. **Center:** focus magnifier.
- **Play:** original camera-file playback only. **MENU:** close menu or exit.

Button availability varies by body. Color settings are reset on camera release.
Shutdown runs off the UI thread; a normal finishing exit ends the process after
cleanup, and reopening waits for outstanding cleanup.

## Focus and compatibility

Development/testing has used a **Sony RX1R II (DSC-RX1RM2), firmware 1.00**.
The upstream app was tested on an A7 II. This is not a claim of universal PMCA support.

AF status is displayed separately from white area outlines. The overlay uses
standard Android focus areas, falling back to enabled Sony rectangles when absent.
Sony rectangle mapping provisionally assumes normalized −1000…1000 coordinates;
physical alignment still needs validation. AF-result point IDs do not match the
reported rectangle indices in the captured RX1R II log, so individual confirmed
lock points are not highlighted. Raw AF details are recorded in `FILMSIM/LOG.TXT`.

The latest file-discovery and focus-overlay changes have build/signature verification;
loading additional files and overlay alignment still require camera confirmation.

## Build

Command-line build; Android Studio, Gradle and the NDK are not required:

- JDK 17
- Android SDK `platforms;android-10` and `build-tools;36.0.0`
- A compile-only Sony API stub JAR; this build used
  [OpenMemories-Framework](https://github.com/ma1co/OpenMemories-Framework) stubs
  assembled with Jasmin 2.4. Stubs are not bundled in the APK.

```sh
ANDROID_SDK_ROOT=/path/to/sdk SONY_STUBS=/path/to/sony-stubs.jar bash app/build.sh
```

Output: `app/MatrixTest.apk`, signed with the local `app/debug.keystore` using the
legacy v1 signature required by PMCA cameras. Preserve your signing key for updates;
it is excluded from Git. A separately signed build cannot update an existing install.

## Credits and license

Based on [sonyalpha-film](https://github.com/jegly/sonyalpha-film) by jegly.
Thanks to [OpenMemoriesLUT](https://github.com/Pichuworks/OpenMemoriesLUT),
[Sony-PMCA-RE](https://github.com/ma1co/Sony-PMCA-RE) and
[OpenMemories-Framework](https://github.com/ma1co/OpenMemories-Framework).
Original license notices are retained; see **LICENSE** (GPL-3.0) and notices in tools/.
This is an independent project, not affiliated with Sony. No Sony firmware is included.
