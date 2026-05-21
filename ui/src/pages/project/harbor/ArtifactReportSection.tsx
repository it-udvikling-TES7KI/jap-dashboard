import {useQuery} from "@tanstack/react-query";
import {fetchArtifactReportFromLatestMasterCommit, fetchArtifactReportFromLatestProdDeploy} from "../../../hooks/HarborHook.ts";
import VulnerabilityBar from "./VulnerabilityBar.tsx";
import styles from "./ArtifactReportSection.module.css";
import ProjectSection from "../ProjectSection.tsx";
import harbor_icon from "../../../assets/harbor_icon.svg";
import gitlab_icon from "../../../assets/gitlab_icon.svg";
import {useState} from "react";
import {ArtifactReportFocus, getArtifactReportLabel} from "../../../types/ArtifactReportFocus.ts";
import {ArtifactReportNavbar} from "../../../components/ArtifactReportNavbar.tsx";


export interface ArtifactReportSectionProps {
    projectId: number;
}

export function ArtifactReportSection({projectId}: ArtifactReportSectionProps) {
    const [activeTab, setActiveTab] = useState(ArtifactReportFocus.LatestMasterCommit);

    const {data: masterCommitArtifactReport} = useQuery({
        queryKey: ['masterCommitArtifactReport', projectId],
        queryFn: async () => {
            if (projectId) return fetchArtifactReportFromLatestMasterCommit(projectId);
        },
        enabled: !!projectId,
        retry: (failureCount) => {
            return failureCount < 1;
        }
    });

    const {data: latestProdDeployArtifactReport} = useQuery({
        queryKey: ['prodDeployArtifactReport', projectId],
        queryFn: async () => {
            if (projectId) return fetchArtifactReportFromLatestProdDeploy(projectId);
        },
        enabled: !!projectId,
        retry: (failureCount) => {
            return failureCount < 1;
        }
    });

    function determineFocusedArtifactReport() {
        switch (activeTab) {
            case ArtifactReportFocus.LatestProdDeploy:
                return latestProdDeployArtifactReport;
            case ArtifactReportFocus.LatestMasterCommit:
                return masterCommitArtifactReport
            default:
                return masterCommitArtifactReport
        }
    }

    const focusedArtifactReport = determineFocusedArtifactReport();

    function getSectionTitle() {
        return "Harbor Artifact Report from " + getArtifactReportLabel(activeTab);
    }

    //todo add link to gitlab commit / gitlab job that made the build
    return (
        <div>
            <ArtifactReportNavbar activeTab={activeTab} setActiveTab={setActiveTab}></ArtifactReportNavbar>
            <ProjectSection title={getSectionTitle()}>
                {focusedArtifactReport ? (
                        <div className={styles.container}>
                            <div className={styles.gitDetails}>
                                <a className={styles.reportInfo}
                                   href={focusedArtifactReport.commitLink}
                                   target="_blank"
                                   rel="noopener noreferrer">
                                    <div className={styles.iconContainer}>
                                        <img
                                            src={gitlab_icon}
                                            alt="Gitlab Logo"/>
                                    </div>
                                    <p>
                                        <strong>Commit: </strong>
                                        {focusedArtifactReport.commitShortId}
                                    </p>
                                </a>
                            </div>
                            {focusedArtifactReport.severity ? (
                                    <div className={styles.artifactDetails}>
                                        <a className={styles.reportInfo}
                                           href={focusedArtifactReport.artifactLink}
                                           target="_blank"
                                           rel="noopener noreferrer">
                                            <div className={styles.iconContainer}>
                                                <img
                                                    src={harbor_icon}
                                                    alt="Harbor Logo"/>
                                            </div>
                                            <p>
                                                <strong>Severity: </strong>
                                                {focusedArtifactReport.severity}
                                            </p>
                                        </a>
                                        <ul>
                                            <li>Critical: {focusedArtifactReport.critical}</li>
                                            <li>High: {focusedArtifactReport.high}</li>
                                            <li>Medium: {focusedArtifactReport.medium}</li>
                                            <li>Low: {focusedArtifactReport.low}</li>
                                            <li>Total: {focusedArtifactReport.total}</li>
                                            <li>Fixable: {focusedArtifactReport.fixable}</li>
                                        </ul>
                                        <div className={styles.vulnerabilityBarContainer}>
                                            <VulnerabilityBar artifactReport={focusedArtifactReport}></VulnerabilityBar>
                                        </div>
                                    </div>
                                ) :
                                (<div> No Report found </div>)}
                        </div>

                    ) :
                    <div>Report not found</div>
                }
            </ProjectSection>
        </div>
    )

}