# 📱 CvFree / CV Free

Aplicación Android modular para crear, personalizar y exportar tu currículum profesional:

- 📝 Creador de CV por secciones: contacto, experiencia, educación, habilidades y resumen profesional  
- 📎 Secciones opcionales de cursos, enlaces, idiomas y referencias  
- 🎨 Plantillas profesionales: Budapest, Riga, Praga y Tokio  
- 📄 Importación desde PDF y escaneo de documentos, con extracción de texto y asistencia de IA según la configuración de acceso  
- 💾 Guardado local automático y gestión de currículums desde **Mis CV**  
- 📤 Vista previa y exportación del currículum en PDF  
- 🔐 Registro, inicio de sesión y recuperación de contraseña  
- 🌗 Temas claro y oscuro, idioma español e inglés y preferencias de notificaciones  

<br><br>

## 🧭 Tecnologías principales

- **Kotlin** con **Jetpack Compose** y **Material 3** — interfaz declarativa y componentes reutilizables  
- **Arquitectura limpia (Clean Architecture)** — separación de presentación, dominio y datos  
- **MVI (Model-View-Intent)** y **MVVM** — gestión del estado UI mediante ViewModels, intents y efectos  
- **Koin** con **KSP** — inyección de dependencias entre módulos  
- **Coroutines** y **Flow** — operaciones asíncronas y observación de cambios  
- **Navigation Compose** — grafos y rutas tipadas para los flujos de la aplicación  
- **Firebase Authentication** y **Cloud Firestore** — autenticación, perfiles y opiniones de usuarios  
- **Cloud Functions / Gemini** — importación asistida por IA, con un uso cada 7 días por dispositivo en Free  
- **Firebase Remote Config**, **App Check**, **Analytics** y **Crashlytics** — configuración remota, protección de servicios y seguimiento de la app  
- **DataStore** y **Kotlin Serialization** — persistencia local de currículums y preferencias  
- **ML Kit** y **PDFBox Android** — escaneo, reconocimiento de texto y lectura de PDF  
- **Modularización** — funcionalidades independientes y módulos `core-*`, `navigation` y `session`  

<br><br>

## 🚀 Requisitos para ejecutar el proyecto

- **Android Studio** compatible con la versión de Android Gradle Plugin configurada en el proyecto  
- **JDK 25** para el daemon de Gradle, según `gradle/gradle-daemon-jvm.properties`; compatibilidad de compilación Java **21**  
- Android SDK con `minSdk 24`, `compileSdk 37` y `targetSdk 37`  
- Emulador o dispositivo con **API 24 (Android 7.0)** o superior  
- Proyecto **Firebase** configurado para `com.devpaul.cvfree`, con su archivo `google-services.json` en `app/` o en el directorio correspondiente a la variante  
- **Email/Password** habilitado en Firebase Authentication y acceso configurado a Firestore  
- Conexión a Internet para autenticación y funciones remotas; servicios de Google Play para escaneo con ML Kit  

Abre el proyecto, sincroniza Gradle y selecciona la variante **devFreeDebug** para ejecutar la versión Free de desarrollo.

La importación con IA requiere desplegar el servicio descrito en [Configuración del cupo de IA](firebase/AI_QUOTA.md). Sin ese servicio, la importación continúa localmente sin IA.

```powershell
.\gradlew.bat :app:assembleDevFreeDebug
.\gradlew.bat :app:installDevFreeDebug
```

Las variantes combinan los ambientes **dev / prod** con el acceso **Free / Premium**. Las variantes **Premium Debug** habilitan el acceso Premium de prueba; el plan comercial se muestra en construcción y no ofrece compras en la navegación actual.

---

<br><br>

## 🛠️ Estructura de módulos

| Módulo | Propósito |
|--------|-----------|
| `app` | Punto de entrada, configuración de Koin, grafo principal e integraciones globales |
| `features/splash` | Pantalla de inicio y entrada al flujo de sesión |
| `features/onboarding` | Presentación inicial de la aplicación |
| `features/auth` | Inicio de sesión, registro y recuperación de contraseña |
| `features/home` | Inicio, actividad reciente y exploración de plantillas |
| `features/resume` | Creación, edición, importación, guardado y gestión de CV |
| `features/templates` | Catálogo, renderizado de plantillas y vista previa del currículum |
| `features/settings` | Perfil, preferencias, plan, ayuda, privacidad y calificaciones |
| `core-domain` | Modelos, contratos de repositorios y reglas de negocio |
| `core-data` | Implementaciones de repositorios, persistencia e integraciones de datos |
| `core-designsystem` | Tema, componentes Compose y recursos visuales compartidos |
| `core-platform` | Utilidades e integraciones comunes de Android |
| `session` | Contratos y modelos de la sesión del usuario |
| `navigation` | Rutas tipadas y contratos de navegación entre funcionalidades |
| `buildSrc` | Plugins de convención y configuración compartida de compilación |

---

<br><br>

## 🎨 Diseño y demostración

### 🔐 Autenticación

Inicio de sesión, creación de cuenta y recuperación de contraseña en tema claro y oscuro.

<p align="center">
<img width="300" height="668" alt="Inicio de sesión en tema claro" src="docs/screenshots/login-claro.png" />
<img width="300" height="668" alt="Inicio de sesión en tema oscuro" src="docs/screenshots/login-oscuro.png" />

<img width="300" height="668" alt="Registro en tema claro" src="docs/screenshots/registro-claro.png" />
<img width="300" height="668" alt="Registro en tema oscuro" src="docs/screenshots/registro-oscuro.png" />

<img width="300" height="668" alt="Recuperación de contraseña en tema claro" src="docs/screenshots/recuperar-claro.png" />
<img width="300" height="668" alt="Recuperación de contraseña en tema oscuro" src="docs/screenshots/recuperar-oscuro.png" />
</p>

<br><br>

_Navigation_

Pantalla de inicio, Mis CV, catálogo de plantillas y ajustes de la aplicación.

<p align="center">
<img width="300" height="668" alt="Inicio en tema claro" src="docs/screenshots/inicio-claro.png" />
<img width="300" height="668" alt="Inicio en tema oscuro" src="docs/screenshots/inicio-oscuro.png" />

<img width="300" height="668" alt="Mis CV en tema claro" src="docs/screenshots/mis-cv-claro.png" />
<img width="300" height="668" alt="Mis CV en tema oscuro" src="docs/screenshots/mis-cv-oscuro.png" />

<img width="300" height="668" alt="Catálogo de plantillas en tema claro" src="docs/screenshots/plantillas-claro.png" />
<img width="300" height="668" alt="Catálogo de plantillas en tema oscuro" src="docs/screenshots/plantillas-oscuro.png" />

<img width="300" height="668" alt="Ajustes en tema claro" src="docs/screenshots/ajustes-claro.png" />
<img width="300" height="668" alt="Ajustes en tema oscuro" src="docs/screenshots/ajustes-oscuro.png" />
</p>

<br><br>

_Creación de currículums_

Crea un CV desde cero, escanea un documento o importa un PDF. Selecciona las secciones que necesitas y consulta el diseño antes de exportarlo.

<p align="center">
<img width="300" height="668" alt="Opciones para crear e importar un CV en tema claro" src="docs/screenshots/crear-importar-claro.png" />
<img width="300" height="668" alt="Opciones para crear e importar un CV en tema oscuro" src="docs/screenshots/crear-importar-oscuro.png" />

<img width="300" height="668" alt="Selección de secciones en tema claro" src="docs/screenshots/secciones-claro.png" />
<img width="300" height="668" alt="Selección de secciones en tema oscuro" src="docs/screenshots/secciones-oscuro.png" />

<img width="300" height="668" alt="Vista previa de la plantilla Budapest en tema claro" src="docs/screenshots/vista-previa-claro.png" />
<img width="300" height="668" alt="Vista previa de la plantilla Budapest en tema oscuro" src="docs/screenshots/vista-previa-oscuro.png" />
</p>

<br><br>

_Video avance funcionalidades_

_Video pendiente de agregar._

<br><br>

## Autor ✒️

* **Paul Guillen Acuña** - *Mi Repositorio* - [PaulGuillen](https://github.com/PaulGuillen?tab=repositories)
