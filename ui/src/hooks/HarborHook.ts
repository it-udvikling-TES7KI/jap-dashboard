import {ArtifactReport} from "../types/ArtifactReport";

const harborUrl = '/api/harbor/';

export async function fetchArtifactReportFromLatestMasterCommit(projectName: string): Promise<ArtifactReport> {
    const options = {
        method: 'GET',
    }
    const response = await fetch(harborUrl + 'project/' + projectName.toLowerCase() +'/artifactReport/latestMasterCommit', options)
    if (!response.ok) {
        throw new Error(`Failed to fetch artifact report: ${response.statusText}`);
    }
    return await response.json()
}

export async function fetchArtifactReportFromLatestProdDeploy(projectName: string): Promise<ArtifactReport> {
    const options = {
        method: 'GET',
    }
    const response = await fetch(harborUrl + 'project/' + projectName.toLowerCase() +'/artifactReport/latestProdDeploy', options)
    if (!response.ok) {
        throw new Error(`Failed to fetch artifact report: ${response.statusText}`);
    }
    return await response.json()
}