<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'
import {useSnapshotStore} from '../../../shared/stores/snapshot'
import type {Snapshot} from '../../../shared/types'
import Timestamp from '../../../shared/atoms/Timestamp.vue'
import Node from '../atoms/Node.vue'
import Header from '../../../shared/atoms/Header.vue'

const store = useSnapshotStore()
onMounted(() => {
  store.fetchSnapshots()
})

interface AreaNode {
  snapshot: Snapshot
  neurons: Snapshot[]
  effectors: Snapshot[]
}

const tree = computed(() => {
  const all = store.snapshots
  const snapshotMap = new Map(all.map(x => [x.name, x]))
  const areas: AreaNode[] = []
  for (const s of all) {
    const content = s.content as Record<string, unknown> | null
    if (!content || !('neurons' in content && 'effectors' in content)) {
      continue
    }
    const neuronIds = (content.neurons ?? []) as string[]
    const effectorIds = (content.effectors ?? []) as string[]
    areas.push({
      snapshot: s,
      neurons: neuronIds
          .map(id => snapshotMap.get(id + '.json'))
          .filter((x): x is Snapshot => x != null),
      effectors: effectorIds
          .map(id => snapshotMap.get(id + '.java'))
          .filter((x): x is Snapshot => x != null),
    })
  }
  return areas
})
const fresh = ref<Set<string>>(new Set())
watch(() => store.badgeGeneration, () => {
  const ids = new Set(store.badges.keys())
  fresh.value = ids
  setTimeout(() => {
    fresh.value = new Set()
  }, 500)
})
</script>
<template>
  <div>
    <div class="title">Neural</div>
    <div v-if="tree.length === 0" class="empty">
      No areas
    </div>
    <div v-for="area in tree" :key="area.snapshot.name" class="area">
      <div class="area-header">
        <Node :name="area.snapshot.name" :bold="true"
              :fresh="fresh.has(area.snapshot.name)"/>
        <span class="area-meta">
          <Timestamp :value="area.snapshot.timestamp"/>
        </span>
      </div>
      <div v-if="area.neurons.length > 0" class="group">
        <Header text="neurons"/>
        <div v-for="n in area.neurons" :key="n.name" class="leaf-row">
          <Node :name="n.name"
                :fresh="fresh.has(n.name)"/>
          <span class="leaf-meta">
            <Timestamp :value="n.timestamp"/>
          </span>
        </div>
      </div>
      <div v-if="area.effectors.length > 0" class="group">
        <Header text="effectors"/>
        <div v-for="e in area.effectors" :key="e.name" class="leaf-row">
          <Node :name="e.name"
                :fresh="fresh.has(e.name)"/>
          <span class="leaf-meta">
            <Timestamp :value="e.timestamp"/>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.title {
  text-transform: uppercase;
  letter-spacing: 1px;
  color: #888;
  margin-bottom: 12px;
}

.empty {
  color: #555;
  font-style: italic;
}

.area {
  margin-bottom: 14px;
}

.area-header {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}

.area-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}

.group {
  margin-left: 12px;
  margin-bottom: 4px;
}

.leaf-row {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-left: 8px;
  padding: 1px 0;
}

.leaf-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}
</style>
