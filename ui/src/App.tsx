import {BrowserRouter, Routes, Route} from "react-router-dom";
import ProjectDashboard from "./pages/dashboard/ProjectDashboard.tsx";
import Project from "./pages/project/Project.tsx";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<ProjectDashboard/>}/>
                <Route path="/project/:projectName" element={<Project/>}/>
            </Routes>
        </BrowserRouter>
    )
}

export default App
