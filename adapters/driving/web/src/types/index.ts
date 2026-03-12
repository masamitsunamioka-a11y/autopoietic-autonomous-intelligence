export type MessageType =
    | 'message' | 'drive' | 'introspection' | 'user' | 'error'
    | 'neural' | 'status' | 'trace' | 'mnemonic'

export interface Message {
    id: string
    type: MessageType
    location: string | null
    content: string
}

export interface NeuralLeaf {
    id: string
    timestamp: number
}

export interface NeuralNode {
    id: string
    neurons: NeuralLeaf[]
    effectors: NeuralLeaf[]
    timestamp: number
}

export interface NeuralTreeData {
    areas: NeuralNode[]
    neurons: NeuralLeaf[]
    effectors: NeuralLeaf[]
}

export interface TraceEntry {
    id: string
    className: string
    methodName: string
    timestamp: number
}

export interface MnemonicFile {
    file: string
    entries: Array<Record<string, unknown>>
    timestamp: number
}

export interface MnemonicData {
    episodes: MnemonicFile[]
    knowledge: MnemonicFile[]
}
