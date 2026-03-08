<script setup lang="ts">
import {nextTick, ref} from 'vue'
import {useChatStore} from '../stores/chat'

const store = useChatStore()
const input = ref('')
const textarea = ref<HTMLTextAreaElement | null>(null)

function resize(): void {
  const el = textarea.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = el.scrollHeight + 'px'
}

async function submit(): Promise<void> {
  const text = input.value.trim()
  if (!text || store.sending) return
  input.value = ''
  await nextTick()
  resize()
  void store.sendMessage(text)
}

function onKeydown(event: KeyboardEvent): void {
  if (event.key === 'Enter' && !event.shiftKey
      && !event.isComposing) {
    event.preventDefault()
    submit()
  }
}
</script>
<template>
  <div class="input-row">
    <span class="prompt">&gt;</span>
    <textarea
        ref="textarea"
        v-model="input"
        rows="1"
        autofocus
        placeholder="type a message…"
        class="msg-input"
        @keydown="onKeydown"
        @input="resize"
    />
    <button
        class="send-btn"
        :disabled="store.sending"
        @click="submit"
    >Send
    </button>
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

.prompt {
  color: #888;
  line-height: 1.4;
}

.msg-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #e0e0e0;
  font-family: inherit;
  font-size: inherit;
  line-height: 1.4;
  resize: none;
  overflow-y: hidden;
  max-height: 200px;
}

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
