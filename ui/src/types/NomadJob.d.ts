export interface NomadJob {
    id: string;
    name: string;
    gitCommit: string;
    serviceLink: string;
    nomadLink: string;
    nomadStatus: string;
    healthStatus: string;
    logscaleLink: string;
}