export type MessageType =
  | "stimulus-fired"
  | "percept-generated"
  | "error"
  | "filesystem-changed"
  | "network-switched"
  | "method-invoked";

export interface Message {
  id: string;
  type: MessageType;
  location: string | null;
  content: string;
}

export interface Snapshot {
  name: string;
  mimeType: string;
  content: unknown;
  timestamp: number;
}

export interface MnemonicEntry {
  id?: string;
  content?: string;
}

export interface TraceEntry {
  id: string;
  className: string;
  methodName: string;
  timestamp: number;
}
