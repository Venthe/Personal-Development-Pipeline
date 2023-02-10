import clc from 'cli-color'

export const title = clc.underline.black.bgWhiteBright
export const subtitle = clc.black.bgWhite
export const error = clc.red
export const forceRun = clc.yellow
export const running = clc.green
export const stderr = clc.blackBright
export const step = clc.white

export const throwThis = (error?: any) => {
    throw new Error(error)
}