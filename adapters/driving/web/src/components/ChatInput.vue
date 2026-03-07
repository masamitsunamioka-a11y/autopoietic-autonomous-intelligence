<script setup lang="ts">
import {ref} from 'vue'
import {useChatStore} from '../stores/chat'

const store = useChatStore()
const input = ref('')

function submit(): void {
  const text = input.value.trim()
  if (!text || store.sending) return
  input.value = ''
  void store.sendMessage(text)
}

function onKeydown(event: KeyboardEvent): void {
  if (event.key === 'Enter' && !event.isComposing) {
    submit()
  }
}
</script>
<template>
  <div class="input-row">
    <span class="prompt">&gt;</span>
    <input
        v-model="input"
        type="text"
        autofocus
        placeholder="type a message…"
        class="msg-input"
        @keydown="onKeydown"
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
  align-items: center;
}

.prompt {
  color: #888;
}

.msg-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #e0e0e0;
  font-family: inherit;
  font-size: inherit;
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
