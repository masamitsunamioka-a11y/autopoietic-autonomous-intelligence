<script setup lang="ts">
import {nextTick, ref} from 'vue'
import {useChatStore} from '../stores/chat'
import Caret from '../atoms/Caret.vue'
import Textarea from '../atoms/Textarea.vue'
import Send from '../atoms/Send.vue'

const store = useChatStore()
const input = ref('')
const textareaRef = ref<InstanceType<typeof Textarea> | null>(null)

async function submit(): Promise<void> {
  const text = input.value.trim()
  if (!text || store.sending) return
  input.value = ''
  await nextTick()
  textareaRef.value?.resize()
  void store.sendMessage(text)
}
</script>
<template>
  <div class="input-row">
    <Caret/>
    <Textarea ref="textareaRef"
              v-model="input"
              @submit="submit"/>
    <Send :disabled="store.sending"
          @click="submit"/>
  </div>
</template>
<style scoped>
.input-row {
  display: flex;
  border-top: 1px solid #333;
  padding: 8px 12px;
  gap: 8px;
  align-items: flex-end;
}
</style>
