<script setup lang="ts">
defineProps<{
  text: string
}>()
</script>
<template>
  <span class="text">{{ text }}</span>
</template>
