<script setup lang="ts">
import type { Message } from "../../../shared/types";
import Speaker from "../atoms/Speaker.vue";
import Body from "../atoms/Body.vue";
const props = defineProps<{ message: Message }>();
function cssClass(type: string): string {
  if (type === "percept-generated") return "entry msg";
  if (type === "stimulus-fired") return "entry user";
  return "entry err";
}
</script>
<template>
  <div :class="cssClass(props.message.type)">
    <Speaker v-if="props.message.type === 'stimulus-fired'" text="User" />
    <Speaker
      v-else-if="props.message.location"
      :text="props.message.location"
    />
    <Body :text="props.message.content" />
  </div>
</template>
<style scoped>
.entry {
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}
.entry.user :deep(.loc),
.entry.user :deep(.text),
.entry.msg :deep(.loc),
.entry.msg :deep(.text) {
  color: #fff;
}
.entry.err :deep(.text) {
  color: #ef5350;
}
</style>
