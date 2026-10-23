# Suscripciones de Google Play sin backend

> Estado actual: las variantes Free muestran Free y las variantes Premium Debug muestran
> Premium de prueba. Mi plan presenta el futuro plan comercial Premium en
> construcción y recoge opiniones en Firestore. No hay acciones de compra, restauración
> ni administración de suscripciones en la navegación activa, ni consultas de Billing al
> entrar en la app. La infraestructura de Billing y las variantes dev/prod se conservan
> para una posible activación futura. Las instrucciones siguientes son para esa etapa.

La aplicación usa Play Billing 8.3.0 para consultar y reconocer compras directamente en Android. No usa Firestore ni Cloud Functions para acreditar Premium.

## Configuración en Play Console

1. Crea la suscripción `cvfree_premium` para `com.devpaul.cvfree`.
2. Crea y activa el plan base `monthly`, de renovación automática mensual, con precios y países disponibles. La interfaz usa el precio localizado que devuelve Google Play.
3. Si usas otros identificadores, cambia `PREMIUM_PRODUCT_ID` y `PREMIUM_BASE_PLAN_ID` en `app/build.gradle.kts`.
4. Publica la variante `prodFreeRelease` en una pista de pruebas. Configura los verificadores de licencia y las cuentas de prueba, y accede a la pista con esas cuentas.
5. Prueba compra aprobada, cancelación del diálogo, pago pendiente, restauración, renovación cancelada, vencimiento y errores de conexión. Usa Play Billing Lab para acelerar los escenarios de suscripción.

Solo se ofrece el precio normal del plan mensual. No se seleccionan pruebas gratuitas, ofertas introductorias, planes prepagados ni planes anuales.

## Estado y acceso

- `PlayBillingSubscriptionSource` mantiene un único cliente de Billing durante el proceso. Consulta las compras al entrar o regresar a primer plano, abrir Mi plan y restaurar compras.
- Solo las compras `PURCHASED` del producto configurado y reconocidas habilitan Premium. Las pendientes nunca lo habilitan. Si falla el reconocimiento, la siguiente consulta vuelve a intentarlo.
- Una consulta exitosa sin compras activas retira Premium. Un error de conexión conserva el último estado conocido durante el proceso. No se guarda un booleano permanente de Premium; después de reiniciar se necesita una consulta correcta para recuperar acceso.
- Las suscripciones canceladas siguen dando acceso mientras Google Play las devuelve como activas. La app abre Google Play para administrar la suscripción y consultar las fechas; BillingClient no proporciona una fecha de vencimiento fiable para mostrarla localmente.
- Las compras pertenecen a la cuenta de Google Play, no al usuario de Firebase Auth. Cambiar de usuario dentro de la app no transfiere ni aísla la suscripción. Para asociar compras de forma segura a usuarios de Firebase se necesita validación fuera del cliente.
- Las variantes `devPremiumDebug` y `prodPremiumDebug` conservan la simulación de Premium para desarrollo y la identifican en pantalla. La variante de publicación no puede activar ese override.
- `ai_import_enabled` controla si Android intenta IA. Free dispone de un uso cada 7 días por dispositivo, validado en Cloud Functions; `premium_paywall_enabled` no retira ese cupo. Solo un custom claim Premium confiable omite el límite en el servidor; una compra local o una variante Premium Debug no otorgan ese claim. Consulta [el cupo semanal de IA](../firebase/AI_QUOTA.md).
-
## Límites

La implementación consulta Google Play y reconoce las compras, pero no realiza verificación mediante Google Play Developer API ni recibe notificaciones de servidor. El estado se sincroniza al usar la app y existe menor resistencia a manipulación que con un backend. No incluyas credenciales de servicio de Google Play en el APK.

Referencia: https://developer.android.com/google/play/billing/integrate
