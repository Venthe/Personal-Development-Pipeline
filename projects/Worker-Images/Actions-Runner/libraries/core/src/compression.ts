import {shell} from "@pipeline/process";

export const untar = async (file: string, directory: string, params = []) => await shell(`tar ${params.join(" ")} -xf ${file} -C ${directory}`)
export const unzip = async (file: string, directory: string) => await shell(`unzip -q -u ${file} -d ${directory}`)

