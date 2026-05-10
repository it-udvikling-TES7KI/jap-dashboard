import {ArtifactReport} from "../types/ProjectPreview";

const harborUrl = '/api/harbor/';

export async function fetchArtifactReportFromLatestMasterCommit(projectName: string) {
    const options = {
        method: 'GET',
    }
    const response = await fetch(harborUrl + 'project/' + projectName.toLowerCase() +'/artifactReport/latestMasterCommit', options)
    return await response.json() as ArtifactReport;
}