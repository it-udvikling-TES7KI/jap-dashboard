import styles from "./ProjectCard.module.css"
import {Link} from "react-router-dom";
import gitlab_icon from "../../assets/gitlab_icon.svg";
import harbor_icon from "../../assets/harbor_icon.svg";
import {ArtifactReportFocus} from "../../types/ArtifactReportFocus.ts";
import {GitlabProject} from "../../types/GitlabProject";
import {useQuery} from "@tanstack/react-query";
import {fetchArtifactReportFromLatestMasterCommit, fetchArtifactReportFromLatestProdDeploy} from "../../hooks/HarborHook.ts";
import VulnerabilityCardSection from "./card_sections/VulnerabilityCardSection.tsx";

export interface ProjectCardProps {
    gitProject: GitlabProject;
    artifactReportFocus: ArtifactReportFocus;
}

export default function ProjectCard({gitProject, artifactReportFocus}: ProjectCardProps) {

    const {data: artifactReport, isLoading: isArtifactLoading} = useQuery({
        queryKey: ['artifactReport', gitProject.id, artifactReportFocus],
        queryFn: determineArtifactRequest,
        enabled: !!gitProject.id,
        retry: (failureCount) => {
            return failureCount < 1;
        }
    })

    function determineArtifactRequest() {
        switch (artifactReportFocus) {
            case ArtifactReportFocus.LatestMasterCommit:
                return fetchArtifactReportFromLatestMasterCommit(gitProject.id);
            case ArtifactReportFocus.LatestProdDeploy:
                return fetchArtifactReportFromLatestProdDeploy(gitProject.id)
        }
    }

    return (
        <div className={styles.card}>
            <Link
                className={styles.projectTitle}
                to={`/project/${gitProject.id}`}>

                <div className={styles.projectName}>
                    {gitProject.projectGroupPath && (
                        <div>{gitProject.projectGroupPath} </div>
                    )}
                    <div className={styles.projectName}> {gitProject.name}</div>
                </div>
            </Link>
            <VulnerabilityCardSection isLoading={isArtifactLoading} artifactReport={artifactReport?? undefined}/>
            <div className={styles.externalLinks}>
                <div className={styles.iconContainer}>
                    <a
                        href={gitProject.gitlabLink}
                        target="_blank"
                        rel="noopener noreferrer">
                        <img src={gitlab_icon} alt="Gitlab Logo"/>
                    </a>
                </div>
                {artifactReport?.repositoryLink && (
                    <div className={styles.iconContainer}>
                        <a
                            href={artifactReport.repositoryLink}
                            target="_blank"
                            rel="noopener noreferrer">
                            <img
                                src={harbor_icon}
                                alt="Harbor Logo"/>
                        </a>
                    </div>
                )}
            </div>
        </div>
    )

}