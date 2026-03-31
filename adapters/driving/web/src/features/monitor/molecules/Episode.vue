<script setup lang="ts">
import { computed, nextTick, ref, TransitionGroup, watchEffect } from "vue";
import { useSnapshotStore } from "../../../shared/stores/snapshot";
import type { MnemonicEntry } from "../../../shared/types";
import { extractCue, extractTime } from "../../../shared/composables/useText";
import Header from "../../../shared/atoms/Header.vue";
import Entry from "../atoms/Entry.vue";
const snapshot = useSnapshotStore();
const el = ref<HTMLDivElement | null>(null);
interface Engram {
  trace: MnemonicEntry;
  strength: number;
}
const episodes = computed(() =>
  snapshot.snapshots.filter((x) => x.name.startsWith("episode/")),
);
const traceOf = (ep: unknown): MnemonicEntry => {
  const engram = ep as Engram | null;
  return engram?.trace ?? (ep as MnemonicEntry);
};
watchEffect(async () => {
  void episodes.value.length;
  await nextTick();
  if (el.value) {
    el.value.scrollTop = el.value.scrollHeight;
  }
});
</script>
<template>
  <div class="section">
    <Header text="Episode" />
    <div ref="el" class="section-body">
      <div v-if="episodes.length === 0" class="empty">No episodes</div>
      <TransitionGroup name="highlight">
        <div v-for="ep in episodes" :key="ep.name" class="mnemonic-item">
          <Entry
            :time="extractTime(traceOf(ep.content)?.id ?? '')"
            :cue="extractCue(traceOf(ep.content)?.id ?? '')"
            :content="String(traceOf(ep.content)?.content ?? '')"
          />
        </div>
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
  border-bottom: 1px solid #333;
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
  color: #888;
}
.mnemonic-time {
  color: #666;
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
