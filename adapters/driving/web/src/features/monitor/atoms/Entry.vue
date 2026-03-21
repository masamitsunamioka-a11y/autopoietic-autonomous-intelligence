<script setup lang="ts">
import Timestamp from '../../../shared/atoms/Timestamp.vue'

defineProps<{
  time?: number
  cue: string
  content: string
}>()
</script>
<template>
  <div class="mnemonic-entry">
    <Timestamp v-if="time" :value="time"/>
    <span class="mnemonic-cue">{{ cue }}</span>
    <span class="mnemonic-text">{{ content }}</span>
  </div>
</template>
<style scoped>
.mnemonic-entry {
  line-height: 1.4;
  padding: 2px 0;
}

.mnemonic-cue {
  color: #e0e0e0;
  margin-right: 6px;
}

.mnemonic-text {
  color: #888;
}
</style>
