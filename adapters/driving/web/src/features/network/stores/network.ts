import { defineStore } from "pinia";
import { ref } from "vue";

export type StatusValue = "DMN" | "CEN" | "SLEEP";
export const useNetworkStore = defineStore("network", () => {
  const current = ref<StatusValue>("DMN");

  function setStatus(value: string): void {
    if (value === "CEN" || value === "DMN" || value === "SLEEP") {
      current.value = value;
    }
  }

  return { current, setStatus };
});
