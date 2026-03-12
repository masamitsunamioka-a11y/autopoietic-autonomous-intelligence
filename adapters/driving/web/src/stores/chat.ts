import {defineStore} from 'pinia'
import {ref} from 'vue'
import {useSse} from '../composables/useSse'
import {useNeuralStore} from './neural'
import {useStatusStore} from './status'
import {useTraceStore} from './trace'
import {useMnemonicStore} from './mnemonic'
import type {Message} from '../types'

export const useChatStore = defineStore('chat', () => {
    const messages = ref<Message[]>([])
    const sending = ref(false)
    const fontSize = ref(12)

    function addMessage(partial: Omit<Message, 'id'>): void {
        messages.value.push({
            id: crypto.randomUUID(),
            ...partial,
        })
    }

    function initSse(): void {
        const neural = useNeuralStore()
        const status = useStatusStore()
        const trace = useTraceStore()
        const mnemonic = useMnemonicStore()
        useSse(
            addMessage,
            neural.setTree,
            status.setStatus,
            trace.addTrace,
            mnemonic.setMnemonic,
        )
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

    return {messages, sending, fontSize, addMessage, initSse, sendMessage}
})
