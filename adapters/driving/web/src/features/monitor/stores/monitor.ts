import {defineStore} from 'pinia'
import {ref} from 'vue'
import type {TraceEntry} from '../../../shared/types'

const MAX_ENTRIES = 100
export const useMonitorStore = defineStore('monitor', () => {
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
