import {useQuery} from "@tanstack/react-query";
import {fetchGitlabProjects} from "./ProjectsHook";

export default function GitLabProjectOverview() {

    const {isError, error, isPending, data: gitLabProjects} = useQuery({queryKey: ['gitlabProjects'], queryFn: fetchGitlabProjects})

    if (isPending) {
        return <span>Loading...</span>
    }

    if (isError) {
        return <span>Error: {error.message}</span>
    }

    return (
        <div>
            <h1>Project Dashboard</h1>
            <div>
                {gitLabProjects.map((project, index) => (
                    <div key={index}>
                        <a
                            href={project.web_url}
                            target="_blank"
                        >
                            {project.name}
                        </a>
                    </div>
                ))}
            </div>
        </div>
    );
}