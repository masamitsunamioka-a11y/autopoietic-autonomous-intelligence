<script setup lang="ts">
const model = defineModel<number>()
const sizes = [10, 12, 14, 16, 18, 20, 24]
</script>
<template>
  <select v-model.number="model" class="font-select">
    <option v-for="s in sizes" :key="s" :value="s">
      {{ s }}px
    </option>
  </select>
</template>
<style scoped>
.font-select {
  background: #1a1a1a;
  color: #888;
  border: 1px solid #333;
  font-family: 'Courier New', Courier, monospace;
  font-size: 12px;
  padding: 2px 4px;
  cursor: pointer;
}

.font-select:hover {
  border-color: #4dd0e1;
}
</style>
