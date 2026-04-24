import {useEffect, useState} from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import GitLabProjectOverview from "./pages/gitlab/Overview.tsx";

function App() {
    return (
            <div>
                <GitLabProjectOverview />
            </div>
    )
}

async function sendCount(count) {

    try {
        const resp = await fetch("/api/baseline/count", {
            method: 'POST', // or 'PUT'
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({"count" : count})
        });
        if (!resp.ok) {
            throw new Error("Network response was not OK");
        }
        const contentType = resp.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            throw new TypeError("Oops, we haven't got JSON!");
        }
        const jsonData = await resp.json();
        return jsonData
    } catch (error) {
        console.error("There has been a problem with your fetch operation:", error)
        throw new Error(error);
    }
}

export default App
