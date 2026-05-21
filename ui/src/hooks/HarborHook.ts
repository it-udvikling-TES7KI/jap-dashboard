import {ArtifactReport} from "../types/ArtifactReport";

const harborUrl = '/api/harbor/';

export async function fetchArtifactReportFromLatestMasterCommit(projectId: number): Promise<ArtifactReport | null> {
    const options = {
        method: 'GET',
    }
    const response = await fetch(harborUrl + 'project/' + projectId + '/artifactReport/latestMasterCommit', options)

    // 404 is an expected "no report" state
    if (response.status === 404) {
        return null;
    }

    if (!response.ok) {
        throw new Error(`Failed to fetch artifact report: ${response.status} ${response.statusText}`);
    }
    return await response.json()
}

export async function fetchArtifactReportFromLatestProdDeploy(projectId: number): Promise<ArtifactReport | null> {
    const options = {
        method: 'GET',
    }
    const response = await fetch(harborUrl + 'project/' + projectId + '/artifactReport/latestProdDeploy', options)

    // 404 is an expected "no report" state
    if (response.status === 404) {
        return null;
    }

    if (!response.ok) {
        throw new Error(`Failed to fetch artifact report: ${response.status} ${response.statusText}`);
    }

    return await response.json()
}