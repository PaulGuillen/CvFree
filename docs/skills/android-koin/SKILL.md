---
name: android-koin
description: Configurar inyección de dependencias Koin, módulos, anotaciones KSP y scopes Android. Usar al registrar implementaciones o resolver fallos del grafo de dependencias.
---

# Configurar Koin

## Procedimiento

1. Inspecciona si el proyecto usa DSL o anotaciones. Mantén un enfoque coherente y verifica compatibilidad entre Koin, annotations, compiler y KSP.
2. Declara módulos por propietario: infraestructura, feature y composición de app. Vincula interfaces de dominio con implementaciones en el borde externo.
3. Define singleton para servicios compartidos seguros, factory para instancias independientes y ViewModel/scope para estado con dueño de lifecycle.
4. Si usas anotaciones, configura KSP y verifica imports/módulos generados. No edites código generado ni crees registros duplicados entre DSL y escaneo.
5. Inicializa Koin una vez en Application, con contexto Android solo para adaptadores que lo necesitan. Evita dependencias de Activity en singletons.
6. Para estado compartido entre pantallas, usa el mismo propietario de grafo explícito; no conviertas el ViewModel en singleton global.

## Comprobar y entregar

Compila el código generado y resuelve los puntos de entrada reales. Verifica el grafo mediante APIs compatibles con la versión instalada y pruebas con implementaciones controladas. Comprueba que logout cierre recursos asociados a sesión.
