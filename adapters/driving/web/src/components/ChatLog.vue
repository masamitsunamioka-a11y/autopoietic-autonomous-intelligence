<script setup lang="ts">
import {nextTick, ref, watchEffect} from 'vue'
import {useChatStore} from '../stores/chat'
import MessageItem from './MessageItem.vue'

const store = useChatStore()
const logEl = ref<HTMLDivElement | null>(null)

watchEffect(async () => {
  void store.messages.length
  await nextTick()
  if (logEl.value) {
    logEl.value.scrollTop = logEl.value.scrollHeight
  }
})
</script>

<template>
  <div ref="logEl" class="log">
    <MessageItem
        v-for="msg in store.messages"
        :key="msg.id"
        :message="msg"
    />
  </div>
</template>

<style scoped>
.log {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
</style>
