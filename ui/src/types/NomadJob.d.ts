export interface NomadJob {
    id: string;
    name: string;
    gitCommit: string;
    serviceLink: string;
    nomadLink: string;
    nomadStatus: string;
    healthStatus: string;
    healthURL: string;
    logscaleLink: string;
    docsURL: string;
}