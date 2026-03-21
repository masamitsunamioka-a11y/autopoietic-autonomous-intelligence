<script setup lang="ts">
defineProps<{
  name: string
  bold?: boolean
  fresh?: boolean
}>()
</script>
<template>
  <span class="node" :class="{bold, fresh}">{{ name }}</span>
</template>
<style scoped>
.node {
  color: #ccc;
  transition: color 0.5s ease-out;
}

.node.bold {
  color: #e0e0e0;
  font-weight: bold;
}

.node:not(.bold)::before {
  content: '\251C ';
  color: #555;
}

.node.fresh {
  color: #00ffff;
}
</style>
