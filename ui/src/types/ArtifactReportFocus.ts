export enum ArtifactReportFocus {
    LatestMasterCommit,
    LatestProdDeploy
}

export function getArtifactReportLabel(focus: ArtifactReportFocus) {
    switch (focus) {
        case ArtifactReportFocus.LatestMasterCommit:
            return "Latest Master Commit";
        case ArtifactReportFocus.LatestProdDeploy:
            return "Latest Prod Deploy";
        default:
            return "Unknown Focus";
    }
}