#!/usr/bin/env bash

vault secrets enable -path=secret/ kv

function initialSecret {
  tee "${1}.json" <<EOF
{
  "password": "secret",
  "username": "admin"
}
EOF

  vault kv put -format=json "secret/infrastructure/${1}" "@${1}.json"
}
