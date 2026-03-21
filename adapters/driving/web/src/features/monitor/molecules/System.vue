<script setup lang="ts">
import {nextTick, ref, watchEffect} from 'vue'
import {useMonitorStore} from '../stores/monitor'
import {stripImpl} from '../../../shared/composables/useText'
import Header from '../../../shared/atoms/Header.vue'
import Log from '../atoms/Log.vue'

const monitor = useMonitorStore()
const el = ref<HTMLDivElement | null>(null)
watchEffect(async () => {
  void monitor.entries.length
  await nextTick()
  if (el.value) {
    el.value.scrollTop = el.value.scrollHeight
  }
})
</script>
<template>
  <div class="section">
    <Header text="Trace"/>
    <div ref="el" class="section-body">
      <div v-if="monitor.entries.length === 0" class="empty">
        No traces
      </div>
      <TransitionGroup name="highlight" tag="div">
        <Log v-for="t in monitor.entries" :key="t.id"
             :time="t.timestamp"
             :class-name="stripImpl(t.className)"
             :method-name="t.methodName"/>
      </TransitionGroup>
    </div>
  </div>
</template>
<style scoped>
.section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.section-body {
  flex: 1;
  overflow-y: auto;
  padding: 4px 12px 8px;
}

.empty {
  color: #555;
  font-style: italic;
}

.highlight-enter-active,
.highlight-enter-active :deep(*) {
  transition: color 0.5s ease-out;
}

.highlight-enter-from,
.highlight-enter-from :deep(*) {
  color: #00ffff;
}
</style>
