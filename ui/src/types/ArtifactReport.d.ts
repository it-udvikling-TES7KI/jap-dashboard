export interface ArtifactReport {
    repositoryLink: string;
    artifactLink: string;
    commitShortId: string;
    commitLink: string;
    severity: string;
    critical: number;
    high: number;
    medium: number;
    low: number;
    total: number;
    fixable: number;
}