<script setup lang="ts">
import {onMounted} from 'vue'
import {useNeuralStore} from '../stores/neural'

const store = useNeuralStore()
onMounted(() => {
  store.fetchTree()
})

function formatTime(ts: number): string {
  if (!ts) return ''
  const d = new Date(ts)
  const Y = d.getFullYear()
  const M = String(d.getMonth() + 1).padStart(2, '0')
  const D = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  const ss = String(d.getSeconds()).padStart(2, '0')
  return `${Y}/${M}/${D} ${hh}:${mm}:${ss}`
}
</script>
<template>
  <div class="sidebar">
    <div class="title">Neural</div>
    <div v-if="store.tree.areas.length === 0" class="empty">
      No areas
    </div>
    <div v-for="area in store.tree.areas" :key="area.id" class="area">
      <div class="area-header">
        <span class="area-id">{{ area.id }}</span>
        <span class="area-meta">
          <span v-if="store.badges.get(area.id) === 'added'"
                :key="'a-' + area.id + store.badgeGen"
                class="badge badge-added">Added</span>
          <span v-else-if="store.badges.get(area.id) === 'updated'"
                :key="'u-' + area.id + store.badgeGen"
                class="badge badge-updated">Updated</span>
          <span v-if="area.timestamp" class="timestamp">
            {{ formatTime(area.timestamp) }}
          </span>
        </span>
      </div>
      <div v-if="area.neurons.length > 0" class="group">
        <div class="group-label">neurons</div>
        <div v-for="n in area.neurons" :key="n.id" class="leaf-row">
          <span class="leaf">{{ n.id }}</span>
          <span class="leaf-meta">
            <span v-if="store.badges.get(n.id) === 'added'"
                  :key="'a-' + n.id + store.badgeGen"
                  class="badge badge-added">Added</span>
            <span v-else-if="store.badges.get(n.id) === 'updated'"
                  :key="'u-' + n.id + store.badgeGen"
                  class="badge badge-updated">Updated</span>
            <span v-if="n.timestamp || area.timestamp" class="timestamp">
              {{ formatTime(n.timestamp || area.timestamp) }}
            </span>
          </span>
        </div>
      </div>
      <div v-if="area.effectors.length > 0" class="group">
        <div class="group-label">effectors</div>
        <div v-for="e in area.effectors" :key="e.id" class="leaf-row">
          <span class="leaf">{{ e.id }}</span>
          <span class="leaf-meta">
            <span v-if="store.badges.get(e.id) === 'added'"
                  :key="'a-' + e.id + store.badgeGen"
                  class="badge badge-added">Added</span>
            <span v-else-if="store.badges.get(e.id) === 'updated'"
                  :key="'u-' + e.id + store.badgeGen"
                  class="badge badge-updated">Updated</span>
            <span v-if="e.timestamp || area.timestamp" class="timestamp">
              {{ formatTime(e.timestamp || area.timestamp) }}
            </span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.sidebar {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding: 12px;
}

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

.area-id {
  color: #4dd0e1;
  font-weight: bold;
}

.badge {
  font-size: 10px;
  padding: 0 4px;
  border-radius: 3px;
  font-weight: bold;
  text-transform: uppercase;
  animation: pulse 0.8s ease-in-out 3;
}

.badge-added {
  color: #a5d6a7;
  background: rgba(102, 187, 106, 0.15);
}

.badge-updated {
  color: #ef9a9a;
  background: rgba(239, 154, 154, 0.15);
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.timestamp {
  color: #666;
  font-size: 11px;
}

.group {
  margin-left: 12px;
  margin-bottom: 4px;
}

.group-label {
  color: #888;
  margin-bottom: 2px;
}

.leaf-row {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-left: 8px;
  padding: 1px 0;
}

.leaf-row > .leaf {
  margin-left: 0;
  padding: 0;
}

.leaf-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}

.leaf {
  color: #ccc;
  margin-left: 8px;
  padding: 1px 0;
}

.leaf::before {
  content: '\251C ';
  color: #555;
}
</style>
