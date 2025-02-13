import * as fs from 'fs';
import * as yaml from 'js-yaml';

const bufferEncoding = 'utf-8';
export const loadYamlFile = <T extends object = object>(filepath: string): T => yaml.load(fs.readFileSync(filepath, bufferEncoding)) as T;
