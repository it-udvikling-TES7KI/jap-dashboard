import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
// https://vitejs.dev/config/
// https://github.com/vitejs/vite/issues/13455
//https://dev.to/ghacosta/til-setting-up-proxy-server-on-vite-2cng
export default defineConfig(({command}) => {
    if (command === 'serve') {
        return {

            plugins: [react()],
            server: {
                cors: false,
                proxy: {
                    '/api': {
                        target: 'http://localhost:8080',
                        changeOrigin: true,
                        secure: false,
                        //rewrite: (path) => path.replace(/^\/api/, ''),
                        configure: (proxy, _options) => {
                            proxy.on('error', (err, _req, _res) => {
                                console.log('proxy error', err);
                            });
                            proxy.on('proxyReq', (proxyReq, req, _res) => {
                                console.log('Sending Request to the Target:', req.method, req.url);
                            });
                            proxy.on('proxyRes', (proxyRes, req, _res) => {
                                console.log('Received Response from the Target:', proxyRes.statusCode, req.url);
                            });
                        },
                    },
                },
            },
        }
    } else if (command === 'build') {
        // command === 'build'
        return {
            plugins: [react()],
            build: {
                manifest: true,
                outDir: '../build/resources/main/www'
            }

        }
    } else {
        return {
            plugins: [react()],

        }
    }
});
