# Release 1.0.3

Validated on 2026-11-15. Production version code: **4**. The existing production suffix is retained, so the packaged version name is **1.0.3-prod**. Development versions are unchanged.

## Changes

- Add Cambridge, Berlin, Vienna, Oslo and Lima with distinct monochrome layouts and matching thumbnails.
- Group recently added templates under a New heading without adding a category filter.
- Label links explicitly, render link names in bold, and apply the requested section order per template.
- Fix clipped language content in column layouts and move dashboard quick actions before the CV list.
- Offer A4 and continuous PDF export, preserve a software snapshot before opening the format dialog, and release that snapshot after cancellation or export.
- Add pinch zoom up to 4x and horizontal panning while keeping exported document dimensions independent of preview zoom.
- Mark the application brand name as non-translatable to resolve the production lint error.
- Exclude generated release baseline-profile artifacts from Git.

## Validation results

| Check | Result |
| --- | --- |
| Android unit tests | 241 executions, no failures/errors/skips |
| Device instrumentation | 12 tests passed on Samsung SM-S916B, Android 16 |
| Backend quota and extraction tests | 19 passed |
| Production lint | 0 errors, 32 warnings |
| Production AAB | Built with R8 optimization |
| Production manifest | versionCode 4, versionName 1.0.3-prod |
| Native ELF load-segment alignment | All 6 packaged 64-bit libraries checked have alignment of at least 16 KiB; this is not a complete runtime page-size certification |
| Whitespace checks | Passed |

Unit tests cover app access/analytics/notifications, CV progress, draft editing and autosave, import parsing and AI quota handling, template resolution and column allocation, PDF splitting, authentication and data repositories. Home, resume, template and app tests were exercised with both Dev and Prod configurations; the count includes executions of the same tests under both configurations. Core-domain has no runnable tests. This AGP configuration exposes unit tests for Debug build types; production Release is separately compiled, linted and optimized.

Device tests cover continuous PDF rendering at the top and bottom, multi-page A4 dimensions/margins/content, opening/cancelling/reopening the download dialog, pinch zoom with unchanged export dimensions, link section visibility, long language columns, skills placement, and resume form editing.

The final successful release checks were:

```powershell
./gradlew.bat :app:lintProdFreeRelease :app:bundleProdFreeRelease
```

The broader validation also ran unit-test tasks for app (DevFreeDebug and ProdFreeDebug), home/resume/templates (DevDebug and ProdDebug), core-data and auth (DevDebug), core-domain:test, and connectedDevDebugAndroidTest for templates and resume. Backend validation used `npm --prefix firebase/functions test`.

Remaining lint warnings concern dependency update notices, unused resources, monochrome launcher icons, a KTX suggestion, and trust-manager implementations in the third-party Bouncy Castle dependency. No broad dependency upgrades or lint suppressions were added during release validation.

## Before uploading to Play

1. Sign the generated AAB with the existing upload key through Android Studio. The Gradle output contains no signing certificate entries and cannot be uploaded as-is. See [Android app signing](https://developer.android.com/studio/publish/app-signing).
2. Complete the backend setup in [AI_QUOTA.md](../firebase/AI_QUOTA.md). Both deployed endpoint checks still returned HTTP 404: `getResumeAiQuota` and `enhanceResume` in project `cvfree-4a17a`, region `us-central1`. Local import remains available. Tests do not certify a live Gemini request, Firebase permissions or real cloud synchronization.
3. Validate the signed, optimized build through a [Play internal test](https://support.google.com/googleplay/android-developer/answer/9845334), including sign-in, cloud sync, App Check/Play Integrity, actual PDF/scanner imports and saved-file sharing/printing. Device instrumentation ran Debug test APKs, not the Play-distributed Release artifact.
4. Review the privacy policy and Play Data safety declaration against the deployed AI behavior before enabling it for users. See [Play Data safety](https://support.google.com/googleplay/android-developer/answer/10787469).
5. Confirm versionCode 4 has not already been used in Play Console; only the repository and generated manifest were available for version verification.

Artifacts (generated, not committed):

- `app/build/outputs/bundle/prodFreeRelease/app-prod-free-release.aab`
- `app/build/outputs/mapping/prodFreeRelease/mapping.txt`
- `app/build/reports/lint-results-prodFreeRelease.html`

## Suggested Play release notes

Spanish:

> Nuevas plantillas profesionales, zoom táctil en la vista previa y descarga de CV en A4 o página continua. Mejoramos la organización de secciones, la visualización de idiomas y el acceso a las acciones de tus CV.

English:

> New professional templates, pinch-to-zoom previews, and A4 or continuous-page PDF downloads. Improved section layouts, language visibility, and access to resume actions.
