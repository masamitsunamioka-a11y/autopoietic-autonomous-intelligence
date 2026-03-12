import type {Message, MessageType, MnemonicData, NeuralTreeData} from '../types'

interface SsePayload {
    type: MessageType
    location?: string
    content: string | NeuralTreeData | MnemonicData
}

export function useSse(
    onMessage: (msg: Omit<Message, 'id'>) => void,
    onNeural: (data: NeuralTreeData) => void,
    onStatus: (status: string) => void,
    onTrace: (className: string, methodName: string) => void,
    onMnemonic: (data: MnemonicData) => void,
): void {
    const es = new EventSource('/api/events')
    es.onmessage = (event: MessageEvent<string>) => {
        try {
            const data = JSON.parse(event.data) as SsePayload
            if (data.type === 'neural') {
                onNeural(data.content as NeuralTreeData)
                return
            }
            if (data.type === 'status') {
                onStatus(data.content as string)
                return
            }
            if (data.type === 'trace') {
                onTrace(
                    data.location ?? '',
                    data.content as string)
                return
            }
            if (data.type === 'mnemonic') {
                onMnemonic(data.content as MnemonicData)
                return
            }
            onMessage({
                type: data.type,
                location: data.location ?? null,
                content: data.content as string,
            })
        } catch {
            // Malformed frame — silently discard
        }
    }
    es.onerror = () => {
        onMessage({
            type: 'error',
            location: null,
            content: '[connection lost — reload to reconnect]',
        })
    }
}
