<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {useChatStore} from './features/chat/stores/chat'
import NeuralPanel from './features/neural/organisms/Panel.vue'
import ChatPanel from './features/chat/organisms/Panel.vue'
import MonitorPanel from './features/monitor/organisms/Panel.vue'

const store = useChatStore()
onMounted(() => {
  store.initSse()
})
const leftWidth = ref(Math.floor(window.innerWidth * 0.25))
const rightWidth = ref(Math.floor(window.innerWidth * 0.25))
const draggingLeft = ref(false)
const draggingRight = ref(false)

function onLeftDown() {
  draggingLeft.value = true
  document.addEventListener('mousemove', onLeftMove)
  document.addEventListener('mouseup', onLeftUp)
}

function onLeftMove(e: MouseEvent) {
  leftWidth.value = Math.max(160, Math.min(e.clientX, 600))
}

function onLeftUp() {
  draggingLeft.value = false
  document.removeEventListener('mousemove', onLeftMove)
  document.removeEventListener('mouseup', onLeftUp)
}

function onRightDown() {
  draggingRight.value = true
  document.addEventListener('mousemove', onRightMove)
  document.addEventListener('mouseup', onRightUp)
}

function onRightMove(e: MouseEvent) {
  rightWidth.value = Math.max(200, Math.min(
      window.innerWidth - e.clientX, 600))
}

function onRightUp() {
  draggingRight.value = false
  document.removeEventListener('mousemove', onRightMove)
  document.removeEventListener('mouseup', onRightUp)
}
</script>
<template>
  <div class="layout"
       :class="{dragging: draggingLeft || draggingRight}">
    <div :style="{
      width: leftWidth + 'px',
      minWidth: leftWidth + 'px'}">
      <NeuralPanel/>
    </div>
    <div class="resizer" @mousedown="onLeftDown"/>
    <div class="main">
      <ChatPanel/>
    </div>
    <div class="resizer" @mousedown="onRightDown"/>
    <div :style="{
      width: rightWidth + 'px',
      minWidth: rightWidth + 'px'}">
      <MonitorPanel/>
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
  font-size: 12px;
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
