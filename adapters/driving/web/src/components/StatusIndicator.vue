<script setup lang="ts">
import {useStatusStore} from '../stores/status'

const store = useStatusStore()
</script>
<template>
  <div class="status">
    <span class="dot" :class="store.current.toLowerCase()"/>
    <span class="label">{{ store.current }}</span>
  </div>
</template>
<style scoped>
.status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: blink 1.5s ease-in-out infinite;
}

.dot.cen {
  background: #66bb6a;
  box-shadow: 0 0 6px #66bb6a;
}

.dot.dmn {
  background: #4dd0e1;
  box-shadow: 0 0 6px #4dd0e1;
}

.dot.sleep {
  background: #ab47bc;
  box-shadow: 0 0 6px #ab47bc;
}

.label {
  color: #888;
  letter-spacing: 1px;
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}
</style>
