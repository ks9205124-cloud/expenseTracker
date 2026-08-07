import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/oauth2': { // PKCE authorization endpoints
        target: 'http://localhost:8080',
        changeOrigin: true,
        xfwd: true, // send X-Forwarded-Host so Spring's redirect Location uses 5173, not 8080
      },
      '/login': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        xfwd: true,
        bypass: (req) => {
          if (req.method === 'GET') {
            return '/index.html'; // let Vite/React Router serve LoginPage
          }
        }
      },
      '/register': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        xfwd: true,
        bypass: (req) => {
          if (req.method === 'GET') {
            return '/index.html'; // let Vite/React Router serve RegisterPage
          }
        }
      },
      '/logout': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        xfwd: true,
      }
    }
  }
})