# Firebase Analytics y Crashlytics

## Integración

El cambio anterior (historial de notificaciones) está en el commit `af9e126`.
Analytics y Crashlytics se inicializan en `AndroidApplication`. Las features usan
`AppAnalytics` de `core-platform`; no necesitan una instancia de Firebase.

Las pantallas se registran al cambiar la entrada de navegación en `MainGraph`.
`AnalyticsScreenNames.kt` asigna nombres fijos a los destinos tipados. Nunca se envían
las rutas ni sus argumentos (correo de recuperación, URI del documento, etc.).
El reporte automático de pantallas por Activity se desactiva para evitar duplicarlo.

## Eventos

Todos incluyen `screen_name`. Las propiedades `app_environment` y `access_plan`
permiten distinguir las variantes.

| Evento | Parámetros adicionales | Cuándo |
| --- | --- | --- |
| `screen_view` | `screen_class` | Pantalla visible, incluida actualización obligatoria. |
| `component_view` | `component` | Diálogos de perfil, idioma, apariencia, calificación, eliminación, enlace y creación/importación de CV. |
| `ui_click` | `action` | Acción de un botón, tarjeta, menú o herramienta. |
| `ui_selection` | `control`, `option` | Plantilla, filtro, sección opcional, trabajo actual, estrellas, idioma, apariencia, preferencias y página de onboarding (también por deslizamiento). |
| `operation_result` | `operation`, `result` | Resultado de autenticación, perfil, guardado, eliminación, exportación, importación, calificación, permiso o publicación de notificación. |
| `app_error` | `operation`, `error_type`, `error_code` | Error controlado o validación fallida. |

Las recomposiciones no generan clics. Los callbacks siguen ejecutándose si la
telemetría falla. Las cancelaciones de corrutinas no se registran como errores.
No se registran pulsaciones de teclado, contenido de campos, selección de texto ni
desplazamientos ordinarios.

## Pantallas

- Inicio: `splash`, `onboarding`, `force_update`.
- Cuenta: `login`, `register`, `forgot_password`, `password_reset_sent`.
- Home: `home`, `all_templates`, `template_preview`, `notifications`.
- CV: `my_cvs`, `resume_import`, `resume_sections`, `resume_contact`,
  `resume_experience`, `resume_education`, `resume_skills`, `resume_courses`,
  `resume_links`, `resume_languages`, `resume_references`, `resume_summary`,
  `resume_template`, `resume_preview`.
- Ajustes: `settings`, `help_center`, `privacy`.

## Mapa de acciones

El contexto de `screen_name` distingue acciones compartidas como `back`,
`continue`, `add_entry`, `save_entry` y `edit_entry`.

| Archivo | Acciones |
| --- | --- |
| `ForceUpdateScreen.kt` | `open_store` |
| `CvBottomNavigation.kt` | `navigate_<destination>` |
| `NotificationHistoryScreen.kt` | `back`, `open_my_cvs` |
| `TemplateSelectionScreen.kt` | `select_template`, `continue`, `back` |
| `ResumePreviewScreen.kt` | `download_pdf`, `save_resume`, `back` |
| `OnboardingScreen.kt` | `skip_onboarding`, `next_onboarding` |
| `HomeScreen.kt` | `open_notifications`, `view_all_templates`, `preview_template`, `open_recent_resume_menu`, `continue_recent_resume`, `view_recent_resume`, `open_my_cvs` |
| `AllTemplatesScreen.kt` | `back`, `preview_template` |
| `SettingsScreen.kt` | `open_language_dialog`, `open_appearance_dialog`, `open_help`, `open_rating_dialog`, `open_privacy`, `logout` |
| `PrivacyScreen.kt` | `back` |
| `HelpCenterScreen.kt` | `back`, `toggle_help_question` |
| `SettingsDialogs.kt` | `save_profile_name`, `cancel_profile_name`, `select_language` |
| `RatingDialog.kt` | `select_rating`, `close_rating`, `submit_rating`, `cancel_rating` |
| `ProfileCard.kt` | `edit_profile_name` |
| `AppearanceSelectionDialog.kt` | `select_appearance` |
| `RegisterScreen.kt` | `register`, `open_login` |
| `RichSummaryEditor.kt` | `apply_link`, `cancel_link`, `format_<tool>`, `select_format_color` |
| `MyCvsScreen.kt` | `open_create_options`, `filter_resumes`, `create_resume`, `scan_resume`, `import_pdf`, `confirm_ai_import`, `cancel_ai_import`, `confirm_delete_resume`, `cancel_delete_resume`, `open_resume_menu`, `delete_resume`, `edit_resume`, `view_resume` |
| `LoginScreen.kt` | `forgot_password`, `login`, `open_register` |
| `AuthComponents.kt` | `toggle_password_visibility` |
| `PasswordResetSentScreen.kt` | `back_to_login` |
| `ForgotPasswordScreen.kt` | `back`, `send_password_reset` |
| `SkillsScreen.kt` | `add_skill`, `remove_skill`, `add_suggested_skill` |
| `BuilderEntryComponents.kt` | `edit_entry`, `request_delete_entry`, `confirm_delete_entry`, `cancel_delete_entry`, `cancel_entry`, `save_entry` |
| `BuilderComponents.kt` | `back`, `continue`, `add_entry` |
| `LanguagesScreen.kt` | `open_language_options`, `select_language`, `select_language_level` |
| `LinksScreen.kt` | `select_link_preset` |
| `ResumeImportScreen.kt` | `cancel_import`, `back`, `retry_import` |
| `SectionSelectionScreen.kt` | `restore_sections`, `confirm_sections`, `toggle_section` |

Los componentes compartidos de formularios, botones y navegación registran el clic
en un solo punto para no duplicarlo. También se registran cambios de interruptores,
checkboxes opcionales y estado de trabajo actual. Cerrar las confirmaciones de IA y
eliminación desde fuera registra `dismiss_ai_import` y `dismiss_delete_resume`.

## Errores y privacidad

- Analytics recibe operación, tipo y código; nunca el mensaje de la excepción.
- Los errores técnicos controlados se envían a Crashlytics con stack trace, sin
  mensaje original, causas ni excepciones suprimidas.
- Los fallos de validación se registran en Analytics sin crear un informe técnico.
- Crashlytics captura los cierres inesperados mediante su SDK.
- Se cubren autenticación, perfil, lectura/decodificación de CV, borrador,
  guardado manual/automático, eliminación, escáner, importación, IA, exportación,
  vista previa, opiniones, notificaciones y actualizaciones.
- Algunos flujos registran etapas distintas de un fallo: por ejemplo,
  `auth_request` registra el fallo técnico y `login` su resultado visible.
  Para contar operaciones fallidas, filtrar por la operación final.
- No se envían nombres, correos, contraseñas, comentarios, nombres de archivos,
  URLs del usuario, identificadores de CV ni contenido del currículum en eventos.
- No se usa `setUserId`. Los identificadores técnicos automáticos de los SDK se
  rigen por la configuración del proyecto Firebase.
- El texto de Privacidad en español e inglés describe esta recopilación.

## Firebase Console

Usar el proyecto asociado al `google-services.json` de la variante instalada.
Confirmar que Google Analytics esté habilitado y que Crashlytics esté disponible.
Para explorar parámetros en informes de Analytics, crear dimensiones personalizadas
de ámbito evento para `action`, `control`, `option`, `operation`, `result`,
`error_type`, `error_code` y `component`. `screen_name` corresponde a los
informes de pantallas. No hace falta registrar cada acción como un tipo de evento
diferente.

Para observar eventos en DebugView desde un dispositivo conectado:

```powershell
adb shell setprop debug.firebase.analytics.app com.devpaul.cvfree
adb shell am force-stop com.devpaul.cvfree
adb shell am start -n com.devpaul.cvfree/.MainActivity
```

Abrir Firebase Console → Analytics → DebugView y recorrer los flujos.
Para ver el diagnóstico en Logcat:

```powershell
adb shell setprop log.tag.FA VERBOSE
adb shell setprop log.tag.FA-SVC VERBOSE
adb logcat -v brief -s CvAnalytics FA FA-SVC
```

En Android Studio se puede usar el filtro
`tag:CvAnalytics | tag:FA | tag:FA-SVC`.
Los logs usan Timber, inicializado antes que Analytics. El tag `CvAnalytics`
está habilitado solo en builds de debug e imprime el proyecto,
el entorno y los eventos entregados al SDK. Este log no confirma la recepción
remota. Los mensajes del SDK sobre carga o respuesta del servidor permiten
diagnosticar el transporte; DebugView confirma que el proyecto recibió el evento.

Al terminar:

```powershell
adb shell setprop debug.firebase.analytics.app .none.
```

Las pruebas automatizadas usan un reporter en memoria; verifican el contrato local,
no la recepción de eventos por el proyecto remoto. No se provoca un crash real de
la app del usuario.

## Validación

```powershell
.\gradlew.bat :app:testDevFreeDebugUnitTest :features:resume:testDevDebugUnitTest :features:settings:testDevDebugUnitTest :features:auth:testDevDebugUnitTest
.\gradlew.bat :app:connectedDevFreeDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.devpaul.cvfree.analytics.AnalyticsNavigationTest,com.devpaul.cvfree.notifications.NotificationHistoryStoreTest,com.devpaul.cvfree.notifications.ResumeNotificationManagerTest'
```

Al añadir una pantalla: incorporar su destino a `analyticsScreenName()`.
Al añadir una acción: envolver su callback con `trackedClick("accion", callback)`
en el componente o su consumidor, una sola vez.
Al registrar selecciones: enviar únicamente valores de catálogo o enums.
Al gestionar un fallo: llamar `AppAnalytics.error("operacion", error)`, conservando
el comportamiento del flujo y propagando las cancelaciones.

Referencias:
[Eventos](https://firebase.google.com/docs/analytics/android/events),
[Pantallas](https://firebase.google.com/docs/analytics/screenviews),
[Crashlytics](https://firebase.google.com/docs/crashlytics/android/get-started),
[Errores controlados](https://firebase.google.com/docs/crashlytics/android/customize-crash-reports).
