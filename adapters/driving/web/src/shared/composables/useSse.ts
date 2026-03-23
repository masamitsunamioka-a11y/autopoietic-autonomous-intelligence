import type { Message, MessageType, Snapshot } from "../types";
interface BaseEvent {
  type: MessageType;
}
interface MethodInvokedData extends BaseEvent {
  type: "method-invoked";
  className: string;
  methodName: string;
}
interface NetworkSwitchedData extends BaseEvent {
  type: "network-switched";
  status: string;
}
interface StimulusFiredData extends BaseEvent {
  type: "stimulus-fired";
  content: string;
}
interface PerceptGeneratedData extends BaseEvent {
  type: "percept-generated";
  location?: string;
  content: string;
}
interface FileSystemChangedData extends BaseEvent {
  type: "filesystem-changed";
  content: Snapshot[];
}
type Event =
  | MethodInvokedData
  | NetworkSwitchedData
  | StimulusFiredData
  | PerceptGeneratedData
  | FileSystemChangedData;
export function useSse(
  onMessage: (msg: Omit<Message, "id">) => void,
  onSnapshot: (data: Snapshot[]) => void,
  onStatus: (status: string) => void,
  onTrace: (className: string, methodName: string) => void,
): void {
  const es = new EventSource("/api/events");
  es.onmessage = (event: MessageEvent<string>) => {
    try {
      const data = JSON.parse(event.data) as Event;
      if (data.type === "filesystem-changed") {
        onSnapshot(data.content);
        return;
      }
      if (data.type === "network-switched") {
        onStatus(data.status);
        return;
      }
      if (data.type === "method-invoked") {
        onTrace(data.className, data.methodName);
        return;
      }
      onMessage({
        type: data.type,
        location: "location" in data ? (data.location ?? null) : null,
        content: data.content,
      });
    } catch {
      // Malformed frame — silently discard
    }
  };
  es.onerror = () => {
    onMessage({
      type: "error",
      location: null,
      content: "[connection lost — reload to reconnect]",
    });
  };
}
