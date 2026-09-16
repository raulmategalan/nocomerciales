# No Comerciales

App Android que bloquea automáticamente las llamadas entrantes cuyo
callerID sigue la nueva numeración comercial española: empieza por
`400` y tiene 9 dígitos en total (`400 XXX XXX`).

Un producto gratuito de economiza.com (diseñado por RAUL MATE GALAN) 

## Cómo funciona

La app usa la API oficial de Android `CallScreeningService`
(`android.telecom.CallScreeningService`), que el sistema invoca por cada
llamada entrante **antes** de que suene el teléfono. Si el número
coincide con el patrón `400XXXXXX` (9 dígitos, empezando por `400`), la
llamada se rechaza automáticamente sin notificación ni vibración.

No hace falta convertir el teléfono en la app de llamadas por defecto:
solo se solicita el rol específico `ROLE_CALL_SCREENING` ("Identificador
de llamadas y aplicaciones de bloqueo de spam"), que Android permite
otorgar a una app de terceros.

- `CallBlockerService.kt`: recibe cada llamada entrante y decide si
  bloquearla.
- `PhoneNumberMatcher.kt`: normaliza el número (quita `+34`/`0034`,
  espacios, guiones) y comprueba el patrón `^400\d{6}$`.
- `MainActivity.kt`: pantalla para activar el bloqueo y ver el
  historial de llamadas bloqueadas (guardado solo en el propio
  dispositivo).

## Requisitos

- Android 10 (API 29) o superior — es la versión mínima que soporta el
  rol `ROLE_CALL_SCREENING` de forma estándar.
- Android Studio (Giraffe o superior) o el SDK de Android + JDK 17
  para compilar.

## Compilar e instalar

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

> Nota: este proyecto se ha creado en un entorno sandbox sin acceso a
> `dl.google.com` (repositorio de Google usado por el Android Gradle
> Plugin), así que no se ha podido ejecutar `./gradlew assembleDebug`
> aquí. El proyecto sigue la estructura estándar de un proyecto Android
> con Kotlin + Gradle, así que debería compilar sin cambios en un
> entorno con acceso normal a internet (tu máquina o Android Studio).
> Antes de dar por bueno el APK, compílalo y pruébalo en un dispositivo
> real.

## Activar el bloqueo

1. Abre la app **No Comerciales**.
2. Pulsa **"Activar bloqueo de llamadas"**.
3. Android mostrará un diálogo del sistema para asignar el rol de
   "aplicación de identificación de llamadas y bloqueo de spam" a esta
   app. Acéptalo.
4. Listo. A partir de ese momento, cualquier llamada entrante cuyo
   número tenga el formato `400XXXXXX` (9 dígitos) se rechazará sola.

Las llamadas bloqueadas quedan registradas (número + fecha/hora) en la
propia pantalla principal de la app, solo en el dispositivo, sin
enviarse a ningún servidor.

## Limitaciones conocidas

- Algunos fabricantes (p. ej. ciertas ROMs muy personalizadas) pueden
  alterar el flujo estándar de `CallScreeningService`. En Android
  "stock"/AOSP y en la mayoría de fabricantes funciona igual.
- Si el operador oculta u homogeniza el callerID antes de que llegue al
  teléfono, la app no puede evaluarlo (esto es una limitación del
  sistema telefónico, no de la app).
- El bloqueo se basa únicamente en el patrón numérico solicitado
  (`400` + 6 dígitos = 9 dígitos). No incluye listas negras adicionales
  ni bloqueo de números ocultos, para mantener la app simple, tal y
  como se pidió.
