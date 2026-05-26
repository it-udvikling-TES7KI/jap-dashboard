export interface GitlabProject {
    id: number;
    name: string;
    projectGroupPath: string;
    gitlabLink: string;
}

export interface GitlabCommit {
    id: number;
    shortId: string;
    title: string;
    gitlabLink: string;
    authorEmail: string;
    createdAt: string;
}