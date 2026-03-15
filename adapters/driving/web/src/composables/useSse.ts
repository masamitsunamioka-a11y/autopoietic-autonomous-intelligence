import type {Message, MessageType, MnemonicData, NeuralTreeData} from '../types'

interface BaseEvent {
    type: MessageType
}

interface TraceEventData extends BaseEvent {
    type: 'trace'
    className: string
    methodName: string
}

interface StatusEventData extends BaseEvent {
    type: 'status'
    status: string
}

interface MessageEventData extends BaseEvent {
    type: 'message' | 'user' | 'error'
    location?: string
    content: string
}

interface NeuralEventData extends BaseEvent {
    type: 'neural'
    content: NeuralTreeData
}

interface MnemonicEventData extends BaseEvent {
    type: 'mnemonic'
    content: MnemonicData
}

type Event =
    | TraceEventData
    | StatusEventData
    | MessageEventData
    | NeuralEventData
    | MnemonicEventData

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
            const data = JSON.parse(event.data) as Event
            if (data.type === 'neural') {
                onNeural(data.content)
                return
            }
            if (data.type === 'status') {
                onStatus(data.status)
                return
            }
            if (data.type === 'trace') {
                onTrace(data.className, data.methodName)
                return
            }
            if (data.type === 'mnemonic') {
                onMnemonic(data.content)
                return
            }
            onMessage({
                type: data.type,
                location: data.location ?? null,
                content: data.content,
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
