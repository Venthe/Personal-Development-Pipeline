import * as envfile from 'envfile';
import * as fs from 'fs';
import * as yaml from 'js-yaml';

const bufferEncoding = 'utf-8';
export const loadEnvironmentFile = <T extends object = object>(filepath: string): T => envfile.parse(fs.readFileSync(filepath, bufferEncoding)) as T;
export const loadYamlFile = <T extends object = object>(filepath: string): T => yaml.load(fs.readFileSync(filepath, bufferEncoding)) as T;
export const loadJsonFile = <T extends object = object>(filepath: string): T => JSON.parse(fs.readFileSync(filepath, bufferEncoding)) as T;
export const saveObjectAsFile = (path: string, obj: object) => {
  fs.writeFileSync(path, JSON.stringify(obj))
}