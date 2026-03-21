<script setup lang="ts">
defineProps<{
  value: number
}>()

function format(ts: number): string {
  if (!ts) return ''
  const d = new Date(ts)
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
}
</script>
<template>
  <span class="timestamp">{{ format(value) }}</span>
</template>
<style scoped>
.timestamp {
  color: #666;
  font-size: 11px;
}
</style>
