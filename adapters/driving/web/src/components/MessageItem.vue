<script setup lang="ts">
import type {Message} from '../types'

const props = defineProps<{ message: Message }>()

function cssClass(type: string): string {
  if (type === 'message') return 'entry msg'
  if (type === 'drive') return 'entry drv'
  if (type === 'introspection') return 'entry intro'
  if (type === 'user') return 'entry user'
  return 'entry err'
}
</script>
<template>
  <div :class="cssClass(props.message.type)">
    <span v-if="props.message.type === 'user'" class="loc">User&gt;</span>
    <span v-else-if="props.message.location" class="loc">{{ props.message.location }}&gt;</span>
    <span class="text">{{ props.message.content }}</span>
  </div>
</template>
<style scoped>
.entry {
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.loc {
  font-weight: bold;
  opacity: 0.6;
  margin-right: 4px;
}

.entry.user .loc,
.entry.user .text,
.entry.msg .loc,
.entry.msg .text {
  color: #fff;
}

.entry.drv .loc,
.entry.drv .text {
  color: #4dd0e1;
}

.entry.intro .loc,
.entry.intro .text {
  color: #555;
  font-style: italic;
}

.entry.err .text {
  color: #ef5350;
}
</style>
