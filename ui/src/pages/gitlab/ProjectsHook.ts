import {GitlabProject} from "./GitlabProject";

const gitlabUrl = '/api/gitlab'

export async function fetchGitlabProjects() {
    const options = {
        method: 'GET',
    }
    const response = await fetch(gitlabUrl, options)
    return await response.json() as GitlabProject[];

}