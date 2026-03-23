<script setup lang="ts">
import { ref } from "vue";
const model = defineModel<string>();
const emit = defineEmits<{ submit: [] }>();
const textarea = ref<HTMLTextAreaElement | null>(null);
function resize(): void {
  const el = textarea.value;
  if (!el) return;
  el.style.height = "auto";
  el.style.height = el.scrollHeight + "px";
}
function onKeydown(event: KeyboardEvent): void {
  if (event.key === "Enter" && !event.shiftKey && !event.isComposing) {
    event.preventDefault();
    emit("submit");
  }
}
defineExpose({ resize });
</script>
<template>
  <textarea
    ref="textarea"
    v-model="model"
    rows="1"
    autofocus
    placeholder="type a message..."
    class="msg-input"
    @keydown="onKeydown"
    @input="resize"
  />
</template>
<style scoped>
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
</style>
