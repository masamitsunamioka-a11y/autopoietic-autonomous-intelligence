<script setup lang="ts">
import {nextTick, ref, watchEffect} from 'vue'
import {useChatStore} from '../stores/chat'
import MessageItem from './MessageItem.vue'

const store = useChatStore()
const logEl = ref<HTMLDivElement | null>(null)
const sizes = [10, 12, 14, 16, 18, 20, 24]
watchEffect(async () => {
  void store.messages.length
  await nextTick()
  if (logEl.value) {
    logEl.value.scrollTop = logEl.value.scrollHeight
  }
})
</script>
<template>
  <div class="log-header">
    <select v-model.number="store.fontSize" class="font-select">
      <option v-for="s in sizes" :key="s" :value="s">
        {{ s }}px
      </option>
    </select>
  </div>
  <div ref="logEl" class="log"
       :style="{fontSize: store.fontSize + 'px'}">
    <MessageItem
        v-for="msg in store.messages"
        :key="msg.id"
        :message="msg"
    />
  </div>
</template>
<style scoped>
.log-header {
  display: flex;
  justify-content: flex-end;
  padding: 4px 16px 0;
  flex-shrink: 0;
}

.font-select {
  background: #1a1a1a;
  color: #888;
  border: 1px solid #333;
  font-family: 'Courier New', Courier, monospace;
  font-size: 12px;
  padding: 2px 4px;
  cursor: pointer;
}

.font-select:hover {
  border-color: #4dd0e1;
}

.log {
  flex: 1;
  overflow-y: auto;
  padding: 8px 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
</style>
