<script setup lang="ts">
import {onMounted} from 'vue'
import {useChatStore} from './stores/chat'
import ChatLog from './components/ChatLog.vue'
import ChatInput from './components/ChatInput.vue'

const store = useChatStore()
onMounted(() => {
  store.initSse()
})
</script>
<template>
  <div class="layout">
    <ChatLog/>
    <ChatInput/>
  </div>
</template>
<style>
*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  background: #0d0d0d;
  color: #e0e0e0;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  height: 100vh;
  overflow: hidden;
}

.layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
</style>
