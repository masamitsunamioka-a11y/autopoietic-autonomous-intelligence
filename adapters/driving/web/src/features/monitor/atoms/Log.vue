<script setup lang="ts">
import Timestamp from "../../../shared/atoms/Timestamp.vue";
defineProps<{
  time: number;
  className: string;
  methodName: string;
}>();
</script>
<template>
  <div class="trace-line">
    <Timestamp :value="time" />
    <span class="trace-name">{{ className }}</span>
    <span class="trace-dot">.</span>
    <span class="trace-name">{{ methodName }}</span>
  </div>
</template>
<style scoped>
.trace-line {
  line-height: 1.4;
  white-space: nowrap;
}
.trace-name {
  color: #aaa;
}
.trace-dot {
  color: #666;
}
</style>
