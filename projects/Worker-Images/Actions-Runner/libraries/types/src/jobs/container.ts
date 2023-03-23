export type Container = {
    image: string
    env?: { [key: string]: string }
    ports?: (number | string)[]
    volumes?: string[]
    credentials?: { [key: string]: string }
    command?: string[]
};
