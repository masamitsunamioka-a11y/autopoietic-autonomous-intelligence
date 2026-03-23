import { defineStore } from "pinia";
import { ref } from "vue";
import type { Snapshot } from "../types";
export type Badge = "added" | "updated";
export const useSnapshotStore = defineStore("snapshot", () => {
  const snapshots = ref<Snapshot[]>([]);
  const badges = ref<Map<string, Badge>>(new Map());
  const badgeGeneration = ref(0);
  function mergeSnapshots(incoming: Snapshot[]): void {
    const previous = new Map(snapshots.value.map((x) => [x.name, x.timestamp]));
    const next = new Map<string, Badge>();
    for (const snapshot of incoming) {
      if (!previous.has(snapshot.name)) {
        next.set(snapshot.name, "added");
      } else if (previous.get(snapshot.name) !== snapshot.timestamp) {
        next.set(snapshot.name, "updated");
      }
      const index = snapshots.value.findIndex((x) => x.name === snapshot.name);
      if (index >= 0) {
        snapshots.value[index] = snapshot;
      } else {
        snapshots.value.push(snapshot);
      }
    }
    if (next.size > 0) {
      badges.value = next;
      badgeGeneration.value++;
    }
  }
  async function fetchSnapshots(): Promise<void> {
    try {
      const res = await fetch("/api/monitor");
      const data = (await res.json()) as Snapshot[];
      snapshots.value = data;
    } catch {
      // Silently fail — will retry on next SSE event
    }
  }
  return { snapshots, badges, badgeGeneration, mergeSnapshots, fetchSnapshots };
});
