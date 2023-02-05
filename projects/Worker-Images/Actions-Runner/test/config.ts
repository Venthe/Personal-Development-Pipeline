// @ts-ignore
import console from "console"

global.console = console

process.env.DEBUG = "testcontainers,testcontainers:exec,testcontainers:containers"
process.env.DEBUG = "testcontainers*"
process.env.DEBUG = "testcontainers*"
process.env.NODE_EXTRA_CA_CERTS = "/etc/ssl/certs/ca-certificates.crt"