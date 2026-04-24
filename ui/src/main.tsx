import App from './App.js'
import './index.css'
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {StrictMode} from "react";
import ReactDOM from 'react-dom/client'

const queryClient = new QueryClient();

const rootElement = document.getElementById('root')!
if (!rootElement.innerHTML) {
    const root = ReactDOM.createRoot(rootElement)
    root.render(
        <QueryClientProvider client={queryClient}>
            <StrictMode>
                <App/>
            </StrictMode>,
        </QueryClientProvider>
    )
}
