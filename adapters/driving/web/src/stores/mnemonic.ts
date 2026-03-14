import {defineStore} from 'pinia'
import {ref} from 'vue'
import type {MnemonicData, MnemonicFile} from '../types'

export const useMnemonicStore = defineStore('mnemonic', () => {
    const episodes = ref<MnemonicFile[]>([])
    const knowledge = ref<MnemonicFile[]>([])

    function setMnemonic(data: MnemonicData): void {
        episodes.value = data.episodes ?? []
        knowledge.value = data.knowledge ?? []
    }

    async function fetchMnemonic(): Promise<void> {
        try {
            const response = await fetch('/api/monitor/mnemonic')
            const data = await response.json() as MnemonicData
            setMnemonic(data)
        } catch {
            // Silently fail — will retry on next SSE event
        }
    }

    return {episodes, knowledge, setMnemonic, fetchMnemonic}
})
