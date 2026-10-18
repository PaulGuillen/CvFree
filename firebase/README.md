# Calificaciones de CV Free

Firestore guarda la última calificación en `app_ratings/{uid}` y un historial inmutable en
`app_ratings/{uid}/submissions/{submissionId}`. Ambos documentos se escriben en una sola
transacción. Los campos son `uid`, `stars` (1–5), `comment` (máximo 500 caracteres),
`submittedAt` (timestamp del servidor) y `submissionId`.

El límite es de un envío cada 24 horas por UID, compartido entre dispositivos. Se requiere
conexión para consultar y enviar. El cliente comprueba la última fecha para mostrar la alerta;
las reglas usan la hora del servidor para imponer el límite aunque se cambie el reloj del dispositivo.

## Configuración necesaria en Firebase

Este repositorio no contiene las reglas actualmente publicadas ni una configuración de despliegue.
En cada proyecto Firebase usado por las variantes dev/prod, integra el bloque de `ratings.rules`
dentro de `service cloud.firestore { match /databases/{database}/documents { ... } }`
de tus reglas existentes y publícalas desde Firebase Console. Conserva las reglas existentes
para las demás colecciones. No publiques `ratings.rules` como archivo completo: es un bloque
para integrar, no reemplaza las reglas de toda la base de datos.

Comprueba que ninguna regla general permita escribir sin restricciones en `app_ratings`,
porque los permisos de reglas coincidentes se combinan con OR.

Verifica con dos sesiones del mismo UID: el primer envío debe guardar ambos documentos;
un segundo envío antes de 24 horas debe rechazarse y mostrar la alerta. Otro UID debe poder
enviar su propia calificación, pero no leer ni modificar las de otro usuario. Las actualizaciones
y eliminaciones del historial deben rechazarse, al igual que valores fuera de 1–5 estrellas,
comentarios de más de 500 caracteres y envíos sin autenticación.

Referencia: https://firebase.google.com/docs/firestore/manage-data/transactions
