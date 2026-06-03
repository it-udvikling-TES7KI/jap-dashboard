export interface NomadJob {
    id: string;
    name: string;
    gitCommit: string;
    serviceURL: string;
    nomadURL: string;
    nomadStatus: string;
    healthStatus: string;
    healthURL: string;
    logscaleURL: string;
    docsURL: string;
}