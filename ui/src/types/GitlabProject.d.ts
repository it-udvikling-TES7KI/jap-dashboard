export interface GitlabProject {
    id: number;
    name: string;
    projectGroupPath: string;
    gitlabURL: string;
}

export interface GitlabCommit {
    id: number;
    shortId: string;
    title: string;
    gitlabURL: string;
    authorEmail: string;
    createdAt: string;
}