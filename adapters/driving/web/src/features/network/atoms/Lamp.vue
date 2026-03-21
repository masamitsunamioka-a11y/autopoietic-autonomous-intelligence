<script setup lang="ts">
defineProps<{
  status: string
}>()
</script>
<template>
  <span class="dot" :class="status.toLowerCase()"/>
</template>
<style scoped>
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

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}
</style>
