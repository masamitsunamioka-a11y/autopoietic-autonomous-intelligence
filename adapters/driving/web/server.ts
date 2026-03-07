import {Hono} from 'hono'
import {serve} from '@hono/node-server'
import {serveStatic} from '@hono/node-server/serve-static'
import {streamSSE} from 'hono/streaming'
import * as fs from 'node:fs'

const backend = process.env.AAI_BACKEND_URL ?? 'http://localhost:8080'
const port = Number(process.env.PORT ?? 3000)
const app = new Hono()
app.post('/api/chat', async (c) => {
    const body = await c.req.text()
    const res = await fetch(`${backend}/api/chat`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body,
    })
    return c.json(await res.json(), res.status as 200)
})
app.get('/api/events', async (c) => {
    const res = await fetch(`${backend}/api/events`)
    if (!res.body) return c.text('no body', 502)
    c.header('Content-Type', 'text/event-stream')
    c.header('Cache-Control', 'no-cache')
    c.header('Connection', 'keep-alive')
    return streamSSE(c, async (s) => {
        const reader = res.body!.getReader()
        const decoder = new TextDecoder()
        while (true) {
            const {done, value} = await reader.read()
            if (done) break
            await s.write(decoder.decode(value, {stream: true}))
        }
    })
})
app.use('/assets/*', serveStatic({root: './dist'}))
app.get('*', (c) => {
    const html = fs.readFileSync('./dist/index.html', 'utf-8')
    return c.html(html)
})
serve({fetch: app.fetch, port}, () => {
    console.log(`Hono BFF listening on http://localhost:${port}`)
})
