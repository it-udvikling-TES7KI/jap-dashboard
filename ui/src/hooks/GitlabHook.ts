import {GitlabCommit, GitlabProject} from "../types/GitlabProject";

const gitlabUrl = '/api/gitlab/'

export async function fetchGitlabProjects() {
    const options = {
        method: 'GET',
    }
    const response = await fetch(gitlabUrl, options)
    return await response.json() as GitlabProject[];

}

export async function fetchGitlabProjectById(id: string): Promise<GitlabProject> {
    const options = {
        method: 'GET',
    }

    const response = await fetch(gitlabUrl + 'projects/' + id, options)
    return await response.json() as GitlabProject;
}

export async function fetchCommit(projectId: number, commitId: string): Promise<GitlabCommit> {
    const options = {
        method: 'GET',
    }

    const response = await fetch(gitlabUrl + 'project/' + projectId + '/commit/' + commitId, options)
    return await response.json() as GitlabCommit;
}

