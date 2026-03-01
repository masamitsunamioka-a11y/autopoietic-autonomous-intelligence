import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()],

    build: {
        outDir: '../api/src/main/resources',
        emptyOutDir: false,
    },

    server: {
        proxy: {
            '/api': 'http://localhost:8080',
        },
    },
})
