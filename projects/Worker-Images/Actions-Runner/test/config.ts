// @ts-ignore
import console from "console"
global.console = console

process.env.DEBUG = "testcontainers,testcontainers:exec,testcontainers:containers"
process.env.DEBUG = "testcontainers*"
