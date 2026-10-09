# Verified Payment Settlement Setup

The backend never treats payment registration as proof of settlement. A registered payment remains `PENDING` until `POST /api/payments/settlement` receives a provider event signed with the configured shared secret.

## Configuration

Set `PAYMENT_WEBHOOK_SECRET` in the backend environment and configure the payment provider to use the same secret. Generate a unique secret with a secret manager or `openssl rand -hex 32`. Do not commit the value to source control.

- Docker Compose requires `PAYMENT_WEBHOOK_SECRET` before starting the backend.
- The `prod` Spring profile requires it at startup.
- Other profiles fail closed: settlement verification returns an error if the secret is missing.

## Provider event signature

The backend verifies HMAC-SHA256 over the exact raw UTF-8 request body. Send the hexadecimal digest in `X-PropertyPilot-Signature`, optionally prefixed with `sha256=`. The event body must include an event ID, provider, provider payment ID, invoice ID, amount, currency, settlement status, and settlement timestamp. Retries with the same event ID are idempotent.

Settlement callbacks must be delivered over HTTPS in deployed environments. Keep provider secrets in the deployment secret store and rotate the backend/provider value together.
