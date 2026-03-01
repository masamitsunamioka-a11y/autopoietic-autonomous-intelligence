import type {Message, MessageType} from '../types'

interface SsePayload {
    type: MessageType
    location?: string
    content: string
}

export function useSse(onMessage: (msg: Omit<Message, 'id'>) => void): void {
    const es = new EventSource('/api/events')

    es.onmessage = (event: MessageEvent<string>) => {
        try {
            const data = JSON.parse(event.data) as SsePayload

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
