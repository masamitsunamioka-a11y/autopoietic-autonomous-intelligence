export function extractCue(id: string): string {
    const at = id.lastIndexOf('@')
    return at >= 0 ? id.substring(0, at) : id
}

export function extractTime(id: string): number {
    const at = id.lastIndexOf('@')
    if (at < 0) return 0
    try {
        return new Date(id.substring(at + 1)).getTime()
    } catch {
        return 0
    }
}

export function stripImpl(name: string): string {
    return name.replace(/Impl$/, '')
}
