export type MessageType = 'message' | 'introspection' | 'user' | 'error'

export interface Message {
    id: string
    type: MessageType
    location: string | null
    content: string
}
