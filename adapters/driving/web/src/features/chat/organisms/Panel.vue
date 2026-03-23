<script setup lang="ts">
import { useChatStore } from "../stores/chat";
import Zoom from "../atoms/Zoom.vue";
import Messages from "../molecules/Messages.vue";
import Input from "../molecules/Input.vue";
const store = useChatStore();
</script>
<template>
  <div class="chat-panel">
    <div class="chat-header">
      <Zoom v-model="store.fontSize" />
    </div>
    <Messages />
    <Input />
  </div>
</template>
<style scoped>
.chat-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.chat-header {
  display: flex;
  justify-content: flex-end;
  padding: 4px 16px 0;
  flex-shrink: 0;
}
</style>
