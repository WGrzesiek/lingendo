# Local JWT keys

JWT private keys must never be committed or copied into `src/main/resources`.

Generate a local development pair from the `api-gateway` directory:

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:3072 -out secrets/private_key.pem
openssl rsa -pubout -in secrets/private_key.pem -out secrets/public_key.pem
```

The default local configuration reads these two ignored files. Kubernetes reads the
same filenames from the `gateway-jwt-keys` Secret mounted at `/etc/keys`.

After any disclosure, generate a completely new pair and replace the Kubernetes
Secret. Removing a key from Git does not revoke copies already present in history.
