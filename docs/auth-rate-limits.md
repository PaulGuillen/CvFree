# Authentication attempt limits

Login, registration and password recovery each allow up to three outgoing attempts in a
rolling 15-minute window per app installation. Each action has its own counter, shared
across email addresses and repository instances. Input rejected by use-case validation
does not consume an attempt. Successful, failed and interrupted outgoing requests do.

`AuthAttemptLimiter` reserves each attempt before calling Firebase. It serializes access
within the app process and persists timestamps in private SharedPreferences, so reopening
the app or signing out does not reset the limit. Attempts expire individually 15 minutes
after they were reserved. Denied attempts do not extend the window. Moving the device clock
backward does not clear stored attempts. Failure to persist the reservation prevents the
Firebase request.

The UI displays a localized limit message through the existing authentication error flow.
Firebase throttling remains independent and is also mapped to the too-many-requests message.

This is a client-side restriction, not protection for Firebase endpoints. Clearing app data,
reinstalling, advancing the device clock or modifying the client can bypass it. Use Firebase
service protections and trusted server-side enforcement if limits must hold across devices
or against direct API requests. No email addresses, passwords or tokens are stored by this limiter.
