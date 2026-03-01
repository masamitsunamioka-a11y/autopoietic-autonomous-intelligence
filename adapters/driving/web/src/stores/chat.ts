import {defineStore} from 'pinia'
import {ref} from 'vue'
import {useSse} from '../composables/useSse'
import type {Message} from '../types'

export const useChatStore = defineStore('chat', () => {
    const messages = ref<Message[]>([])
    const sending = ref(false)

    function addMessage(partial: Omit<Message, 'id'>): void {
        messages.value.push({
            id: crypto.randomUUID(),
            ...partial,
        })
    }

    function initSse(): void {
        useSse(addMessage)
    }

    async function sendMessage(input: string): Promise<void> {
        const trimmed = input.trim()
        if (!trimmed || sending.value) return

        sending.value = true
        try {
            await fetch('/api/chat', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({input: trimmed}),
            })
        } catch (e) {
            addMessage({
                type: 'error',
                location: null,
                content: `[send failed: ${String(e)}]`,
            })
        } finally {
            sending.value = false
        }
    }

    return {messages, sending, addMessage, initSse, sendMessage}
})
