<script setup lang="ts">
defineProps<{
  disabled?: boolean;
}>();
defineEmits<{
  click: [];
}>();
</script>
<template>
  <button class="send-btn" :disabled="disabled" @click="$emit('click')">
    Send
  </button>
</template>
<style scoped>
.send-btn {
  background: #333;
  border: 1px solid #555;
  color: #e0e0e0;
  font-family: inherit;
  font-size: inherit;
  padding: 2px 10px;
  cursor: pointer;
}
.send-btn:hover:not(:disabled) {
  background: #444;
}
.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
