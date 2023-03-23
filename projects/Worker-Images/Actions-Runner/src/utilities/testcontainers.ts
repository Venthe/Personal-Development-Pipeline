import {Container} from "@pipeline/types";
import {GenericContainer} from "testcontainers";
import {PortWithOptionalBinding} from "testcontainers/dist/port";
import {BindMode, BindMount} from "testcontainers/dist/docker/types";

export const toContainer = (containerDefinition: Container, name: string): GenericContainer => {
    const container = new GenericContainer(containerDefinition.image);

    if (containerDefinition.env) {
        console.debug(`Adding env variables to ${name}`, containerDefinition.env)
        container.withEnvironment(containerDefinition.env)
    }

    if (containerDefinition.ports) {
        const ports: PortWithOptionalBinding[] = containerDefinition.ports?.map(port => mapPortToPortWithOptionalBinding(port));
        console.debug(`Adding ports to ${name}`, ports)
        container.withExposedPorts(...ports)
    }

    if (containerDefinition.volumes) {
        const volumes = containerDefinition.volumes?.map(volume => mapVolume(volume));
        console.debug(`Adding volumes to ${name}`, volumes)
        container.withBindMounts(volumes)
    }

    if (containerDefinition.credentials) {
        console.error("Unsupported option: Container credential")
    }

    if (containerDefinition.command) {
        console.debug(`Setting cmd to ${name}`, containerDefinition.command)
        container.withCommand(containerDefinition.command)
    }

    return container
}

export const mapPortToPortWithOptionalBinding = (port: string | number): PortWithOptionalBinding => {
    const {
        port: _port,
        host,
        container
    } = ((typeof port === "string" ? port : `${port}`).match(/^(?<port>\d+)$|^(?<host>\d+):(?<container>\d+)$/))?.groups ?? {};
    if (_port) {
        return +_port;
    } else {
        return {
            container: +container,
            host: +host
        }
    }
}

export const mapVolume = (mapping: string): BindMount => {
    const {
        source,
        target,
        mode
    } = (mapping.match(/^(?<source>[^:]+):(?<target>[^:]+)(:(?<mode>rw|ro|z|Z))?$/))?.groups ?? {};
    return {
        source,
        target,
        ...(mode ? {mode: mode as BindMode} : {})
    }
}
