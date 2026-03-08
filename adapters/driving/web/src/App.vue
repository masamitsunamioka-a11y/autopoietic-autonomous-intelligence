<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {useChatStore} from './stores/chat'
import NeuralTree from './components/NeuralTree.vue'
import ChatLog from './components/ChatLog.vue'
import ChatInput from './components/ChatInput.vue'

const store = useChatStore()
onMounted(() => {
  store.initSse()
})
const sidebarWidth = ref(500)
const dragging = ref(false)

function onMouseDown() {
  dragging.value = true
  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

function onMouseMove(e: MouseEvent) {
  const w = Math.max(160, Math.min(e.clientX, 600))
  sidebarWidth.value = w
}

function onMouseUp() {
  dragging.value = false
  document.removeEventListener('mousemove', onMouseMove)
  document.removeEventListener('mouseup', onMouseUp)
}
</script>
<template>
  <div class="layout" :class="{dragging}">
    <div :style="{width: sidebarWidth + 'px', minWidth: sidebarWidth + 'px'}">
      <NeuralTree/>
    </div>
    <div class="resizer" @mousedown="onMouseDown"/>
    <div class="main">
      <ChatLog/>
      <ChatInput/>
    </div>
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
  flex-direction: row;
  height: 100vh;
}

.layout.dragging {
  cursor: col-resize;
  user-select: none;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.resizer {
  width: 4px;
  cursor: col-resize;
  background: #333;
  transition: background 0.15s;
}

.resizer:hover {
  background: #4dd0e1;
}
</style>
