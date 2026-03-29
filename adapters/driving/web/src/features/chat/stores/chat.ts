import { defineStore } from "pinia";
import { ref } from "vue";
import { useSse } from "../../../shared/composables/useSse";
import { useSnapshotStore } from "../../../shared/stores/snapshot";
import { useNetworkStore } from "../../network/stores/network";
import { useMonitorStore } from "../../monitor/stores/monitor";
import type { Message } from "../../../shared/types";

export const useChatStore = defineStore("chat", () => {
  const messages = ref<Message[]>([]);
  const sending = ref(false);
  const fontSize = ref(12);

  function addMessage(partial: Omit<Message, "id">): void {
    messages.value.push({
      id: crypto.randomUUID(),
      ...partial,
    });
  }

  function initSse(): void {
    const snapshot = useSnapshotStore();
    const network = useNetworkStore();
    const monitor = useMonitorStore();
    useSse(
      addMessage,
      snapshot.mergeSnapshots,
      network.setStatus,
      monitor.addTrace,
    );
  }

  async function sendMessage(input: string): Promise<void> {
    const trimmed = input.trim();
    if (!trimmed || sending.value) return;
    sending.value = true;
    try {
      await fetch("/api/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ payload: trimmed }),
      });
    } catch (e) {
      addMessage({
        type: "error",
        location: null,
        content: `[send failed: ${String(e)}]`,
      });
    } finally {
      sending.value = false;
    }
  }

  return { messages, sending, fontSize, addMessage, initSse, sendMessage };
});
