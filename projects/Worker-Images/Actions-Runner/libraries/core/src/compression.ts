import {shell} from "@pipeline/process";

export const untar = async (file: string, directory: string) => await shell(`tar --verbose -xf ${file} -C ${directory}`)
export const unzip = async (file: string, directory: string) => await shell(`unzip -q -u ${file} -d ${directory}`)

