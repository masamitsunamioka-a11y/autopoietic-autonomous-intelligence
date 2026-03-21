<script setup lang="ts">
defineProps<{ text: string }>()
</script>
<template>
  <div class="section-title">{{ text }}</div>
</template>
<style scoped>
.section-title {
  text-transform: uppercase;
  letter-spacing: 1px;
  color: #888;
  padding: 8px 12px 4px;
  flex-shrink: 0;
}
</style>
