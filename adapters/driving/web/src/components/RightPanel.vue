<script setup lang="ts">
import {nextTick, onMounted, ref, watchEffect} from 'vue'
import {useTraceStore} from '../stores/trace'
import {useMnemonicStore} from '../stores/mnemonic'

const trace = useTraceStore()
const mnemonic = useMnemonicStore()
const traceEl = ref<HTMLDivElement | null>(null)
onMounted(() => {
  mnemonic.fetchMnemonic()
})
watchEffect(async () => {
  void trace.entries.length
  await nextTick()
  if (traceEl.value) {
    traceEl.value.scrollTop = traceEl.value.scrollHeight
  }
})

function formatTime(ts: number): string {
  if (!ts) return ''
  const d = new Date(ts)
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  const ss = String(d.getSeconds()).padStart(2, '0')
  return `${hh}:${mm}:${ss}`
}

interface TraceEntry {
  id?: string
  content?: string
}

function extractCue(id: string): string {
  const at = id.lastIndexOf('@')
  return at >= 0 ? id.substring(0, at) : id
}

function extractTime(id: string): string {
  const at = id.lastIndexOf('@')
  if (at < 0) return ''
  try {
    const d = new Date(id.substring(at + 1))
    const hh = String(d.getHours()).padStart(2, '0')
    const mm = String(d.getMinutes()).padStart(2, '0')
    const ss = String(d.getSeconds()).padStart(2, '0')
    return `${hh}:${mm}:${ss}`
  } catch {
    return ''
  }
}

function stripImpl(name: string): string {
  return name.replace(/Impl$/, '')
}
</script>
<template>
  <div class="panel">
    <div class="section">
      <div class="section-title">Episode</div>
      <div class="section-body">
        <div v-if="mnemonic.episodes.length === 0" class="empty">
          No episodes
        </div>
        <div v-for="ep in mnemonic.episodes" :key="ep.file"
             class="mnemonic-item">
          <div class="mnemonic-header">
            <span class="mnemonic-file">{{ ep.file }}</span>
            <span class="mnemonic-time">
              {{ formatTime(ep.timestamp) }}
            </span>
          </div>
          <div v-for="(entry, i) in (ep.entries as TraceEntry[])"
               :key="i" class="mnemonic-entry">
            <span class="mnemonic-time">{{
                extractTime(entry.id ?? '')
              }}</span>
            <span class="mnemonic-cue">{{
                extractCue(entry.id ?? '')
              }}</span>
            <span class="mnemonic-text">{{
                entry.content ?? ''
              }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="section">
      <div class="section-title">Knowledge</div>
      <div class="section-body">
        <div v-if="mnemonic.knowledge.length === 0" class="empty">
          No knowledge
        </div>
        <div v-for="kn in mnemonic.knowledge" :key="kn.file"
             class="mnemonic-item">
          <div class="mnemonic-header">
            <span class="mnemonic-file">{{ kn.file }}</span>
            <span class="mnemonic-time">
              {{ formatTime(kn.timestamp) }}
            </span>
          </div>
          <div v-for="(entry, i) in (kn.entries as TraceEntry[])"
               :key="i" class="mnemonic-entry">
            <span class="mnemonic-cue">{{
                extractCue(entry.id ?? '')
              }}</span>
            <span class="mnemonic-text">{{
                entry.content ?? ''
              }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="section">
      <div class="section-title">Trace</div>
      <div ref="traceEl" class="section-body trace-body">
        <div v-if="trace.entries.length === 0" class="empty">
          No traces
        </div>
        <div v-for="t in trace.entries" :key="t.id" class="trace-line">
          <span class="trace-time">{{ formatTime(t.timestamp) }}</span>
          <span class="trace-class">{{ stripImpl(t.className) }}</span>
          <span class="trace-dot">.</span>
          <span class="trace-method">{{ t.methodName }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.panel {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  border-bottom: 1px solid #333;
}

.section:last-child {
  border-bottom: none;
}

.section-title {
  text-transform: uppercase;
  letter-spacing: 1px;
  color: #888;
  padding: 8px 12px 4px;
  flex-shrink: 0;
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

.mnemonic-item {
  margin-bottom: 8px;
}

.mnemonic-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 2px;
}

.mnemonic-file {
  color: #4dd0e1;
}

.mnemonic-time {
  color: #666;
}

.mnemonic-entry {
  line-height: 1.4;
  padding: 2px 0;
}

.mnemonic-cue {
  color: #e0e0e0;
  margin-right: 6px;
}

.mnemonic-text {
  color: #888;
}

.trace-body {
}

.trace-line {
  line-height: 1.4;
  white-space: nowrap;
}

.trace-time {
  color: #555;
  margin-right: 6px;
}

.trace-class {
  color: #4dd0e1;
}

.trace-dot {
  color: #666;
}

.trace-method {
  color: #aaa;
}
</style>
