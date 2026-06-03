export interface ArtifactReport {
    repositoryURL: string;
    artifactURL: string;
    commitShortId: string;
    commitURL: string;
    severity: string;
    critical: number;
    high: number;
    medium: number;
    low: number;
    total: number;
    fixable: number;
}