import {defineStore} from 'pinia'
import {ref} from 'vue'
import type {NeuralTreeData} from '../types'

export type Badge = 'added' | 'updated'
export const useNeuralStore = defineStore('neural', () => {
    const tree = ref<NeuralTreeData>({areas: [], neurons: [], effectors: []})
    const badges = ref<Map<string, Badge>>(new Map())
    const badgeGen = ref(0)

    function setTree(data: NeuralTreeData): void {
        const prevLeaves = new Map(
            tree.value.areas.flatMap(a =>
                [a, ...a.neurons, ...a.effectors])
                .map(x => [x.id, x.timestamp]))
        const next = new Map<string, Badge>()
        for (const area of data.areas) {
            if (!prevLeaves.has(area.id)) {
                next.set(area.id, 'added')
            } else if (prevLeaves.get(area.id) !== area.timestamp) {
                next.set(area.id, 'updated')
            }
            for (const leaf of [...area.neurons, ...area.effectors]) {
                if (!prevLeaves.has(leaf.id)) {
                    next.set(leaf.id, 'added')
                } else if (prevLeaves.get(leaf.id) !== leaf.timestamp) {
                    next.set(leaf.id, 'updated')
                }
            }
        }
        if (next.size > 0) {
            badges.value = next
            badgeGen.value++
        }
        tree.value = data
    }

    async function fetchTree(): Promise<void> {
        try {
            const res = await fetch('/api/monitor/neural')
            const data = await res.json() as NeuralTreeData
            tree.value = data
        } catch {
            // Silently fail — will retry on next SSE event
        }
    }

    return {tree, badges, badgeGen, setTree, fetchTree}
})
