import {defineStore} from 'pinia'
import {ref} from 'vue'
import type {TraceEntry} from '../types'

const MAX_ENTRIES = 100
export const useTraceStore = defineStore('trace', () => {
    const entries = ref<TraceEntry[]>([])

    function addTrace(className: string, methodName: string): void {
        entries.value.push({
            id: crypto.randomUUID(),
            className,
            methodName,
            timestamp: Date.now(),
        })
        if (entries.value.length > MAX_ENTRIES) {
            entries.value = entries.value.slice(-MAX_ENTRIES)
        }
    }

    return {entries, addTrace}
})
