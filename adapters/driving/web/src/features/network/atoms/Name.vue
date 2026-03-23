<script setup lang="ts">
defineProps<{
  text: string;
}>();
</script>
<template>
  <span class="label">{{ text }}</span>
</template>
<style scoped>
.label {
  color: #888;
  letter-spacing: 1px;
}
</style>
