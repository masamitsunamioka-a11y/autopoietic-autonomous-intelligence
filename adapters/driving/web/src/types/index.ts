export type MessageType = 'message' | 'drive' | 'introspection' | 'user' | 'error' | 'neural'

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
