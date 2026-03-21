<script setup lang="ts">
defineProps<{
  text: string
}>()
</script>
<template>
  <span class="loc">{{ text }}&gt;</span>
</template>
<style scoped>
.loc {
  font-weight: bold;
  opacity: 0.6;
  margin-right: 4px;
}
</style>
