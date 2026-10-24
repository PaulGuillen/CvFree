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

Capturas actuales de la app organizadas por flujo. Las imágenes se muestran con una altura de **380 px** para consultar varias pantallas juntas sin ocupar tanto espacio.

### 🚀 Inicio y bienvenida

Splash con iluminación azul y fondos adaptados al tema, seguido de los tres pasos de presentación: plantillas, perfil y descarga del CV.

**Modo claro**

<p align="center">
<img height="380" alt="Splash con degradado claro" src="docs/screenshots/splash-claro.png" />
<img height="380" alt="Onboarding: elige una plantilla profesional" src="docs/screenshots/onboarding-1-claro.png" />
<img height="380" alt="Onboarding: completa tu perfil" src="docs/screenshots/onboarding-2-claro.png" />
<img height="380" alt="Onboarding: prepara tu próximo empleo" src="docs/screenshots/onboarding-3-claro.png" />
</p>

**Modo oscuro**

<p align="center">
<img height="380" alt="Splash con degradado azul marino en modo oscuro" src="docs/screenshots/splash-oscuro.png" />
<img height="380" alt="Onboarding de plantillas en modo oscuro" src="docs/screenshots/onboarding-1-oscuro.png" />
<img height="380" alt="Onboarding de perfil en modo oscuro" src="docs/screenshots/onboarding-2-oscuro.png" />
<img height="380" alt="Onboarding de descarga en modo oscuro" src="docs/screenshots/onboarding-3-oscuro.png" />
</p>

### 🔐 Acceso y seguridad

Inicio de sesión, creación de cuenta y recuperación de contraseña con selector de idioma. Registro y recuperación comparten la cabecera de seguridad con botón para retroceder.

**Modo claro**

<p align="center">
<img height="380" alt="Inicio de sesión en tema claro" src="docs/screenshots/login-claro.png" />
<img height="380" alt="Crear cuenta en tema claro" src="docs/screenshots/registro-claro.png" />
<img height="380" alt="Recuperar contraseña en tema claro" src="docs/screenshots/recuperar-claro.png" />
</p>

**Modo oscuro**

<p align="center">
<img height="380" alt="Inicio de sesión en tema oscuro" src="docs/screenshots/login-oscuro.png" />
<img height="380" alt="Crear cuenta en tema oscuro" src="docs/screenshots/registro-oscuro.png" />
<img height="380" alt="Recuperar contraseña en tema oscuro" src="docs/screenshots/recuperar-oscuro.png" />
</p>

### 🏠 Inicio y catálogo de plantillas

Panel de inicio con consejos, plantillas destacadas y actividad reciente. El catálogo permite filtrar, reordenar, seleccionar una tarjeta y abrir su vista previa.

<p align="center">
<img height="380" alt="Panel de inicio en tema claro" src="docs/screenshots/inicio-claro.png" />
<img height="380" alt="Panel de inicio en tema oscuro" src="docs/screenshots/inicio-oscuro.png" />
<img height="380" alt="Galería de plantillas en tema claro" src="docs/screenshots/plantillas-claro.png" />
<img height="380" alt="Galería de plantillas en tema oscuro" src="docs/screenshots/plantillas-oscuro.png" />
</p>

### 📂 Gestión de currículums

Mis CV permite alternar entre lista y cuadrícula, buscar documentos, consultar las secciones completadas y marcar un CV como activo.

<p align="center">
<img height="380" alt="Mis CV en vista de lista y tema claro" src="docs/screenshots/mis-cv-claro.png" />
<img height="380" alt="Mis CV en vista de lista y tema oscuro" src="docs/screenshots/mis-cv-oscuro.png" />
<img height="380" alt="Mis CV en cuadrícula y tema claro" src="docs/screenshots/mis-cv-cuadricula-claro.png" />
<img height="380" alt="Mis CV en cuadrícula y tema oscuro" src="docs/screenshots/mis-cv-cuadricula-oscuro.png" />
</p>

<details>
<summary>Ver el selector de CV para las acciones proactivas</summary>

Cuando hay varios documentos, duplicar o cambiar la plantilla solicita elegir el CV al que se aplicará la acción.

<p align="center">
<img height="380" alt="Selector de documento para duplicar un CV" src="docs/screenshots/seleccionar-cv.png" />
</p>

</details>

### ⚙️ Perfil y preferencias

Ajustes comparte la cabecera de Inicio y Mis CV, sin iconos de acción a la derecha. Incluye perfil, plan, idioma, apariencia, notificaciones y guardado automático.

<p align="center">
<img height="380" alt="Ajustes y perfil en tema claro" src="docs/screenshots/ajustes-claro.png" />
<img height="380" alt="Ajustes y perfil en tema oscuro" src="docs/screenshots/ajustes-oscuro.png" />
</p>

### 📝 Creación y vista previa

Crea un CV desde cero, escanea un documento o importa un PDF. Selecciona sus secciones y explora la vista previa de Budapest con información de ejemplo.

<p align="center">
<img height="380" alt="Opciones para crear e importar un CV en tema claro" src="docs/screenshots/crear-importar-claro.png" />
<img height="380" alt="Selección de secciones del CV en tema claro" src="docs/screenshots/secciones-claro.png" />
<img height="380" alt="Vista previa de Budapest con datos de ejemplo en tema claro" src="docs/screenshots/vista-previa-claro.png" />
</p>

<details>
<summary>Ver el flujo de creación en modo oscuro</summary>

<p align="center">
<img height="380" alt="Opciones para crear e importar un CV en tema oscuro" src="docs/screenshots/crear-importar-oscuro.png" />
<img height="380" alt="Selección de secciones del CV en tema oscuro" src="docs/screenshots/secciones-oscuro.png" />
<img height="380" alt="Vista previa de Budapest con datos de ejemplo en tema oscuro" src="docs/screenshots/vista-previa-oscuro.png" />
</p>

</details>

### 🎬 Video de funcionalidades

_Video pendiente de agregar._

<br><br>

## Autor ✒️

* **Paul Guillen Acuña** - *Mi Repositorio* - [PaulGuillen](https://github.com/PaulGuillen?tab=repositories)
