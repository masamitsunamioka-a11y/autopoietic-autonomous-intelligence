export function extractCue(id: string): string {
  const underscore = id.indexOf("_");
  return underscore >= 0 ? id.substring(underscore + 1) : id;
}
export function extractTime(id: string): number {
  const underscore = id.indexOf("_");
  if (underscore < 0) return 0;
  const raw = id.substring(0, underscore);
  if (raw.length !== 14) return 0;
  const iso =
    raw.substring(0, 4) +
    "-" +
    raw.substring(4, 6) +
    "-" +
    raw.substring(6, 8) +
    "T" +
    raw.substring(8, 10) +
    ":" +
    raw.substring(10, 12) +
    ":" +
    raw.substring(12, 14);
  return new Date(iso).getTime();
}
export function stripImpl(name: string): string {
  return name.replace(/Impl$/, "");
}
