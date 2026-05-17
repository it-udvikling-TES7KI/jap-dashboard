import {useQuery} from "@tanstack/react-query";
import {fetchArtifactReportFromLatestMasterCommit, fetchArtifactReportFromLatestProdDeploy} from "../../../hooks/HarborHook.ts";
import VulnerabilityBar from "./VulnerabilityBar.tsx";
import styles from "./ArtifactReportSection.module.css";
import ProjectSection from "../ProjectSection.tsx";
import harbor_icon from "../../../assets/harbor_icon.svg";
import {useState} from "react";
import {ArtifactReportFocus, getArtifactReportLabel} from "../../../types/ArtifactReportFocus.ts";
import {ArtifactReportNavbar} from "../../../components/ArtifactReportNavbar.tsx";


export interface ArtifactReportSectionProps {
    projectName: string;
}

export function ArtifactReportSection({projectName}: ArtifactReportSectionProps) {
    const [activeTab, setActiveTab] = useState(ArtifactReportFocus.LatestMasterCommit);

    const {data: masterCommitArtifactReport} = useQuery({
        queryKey: ['masterCommitArtifactReport', projectName],
        queryFn: async () => {
            if (projectName) return fetchArtifactReportFromLatestMasterCommit(projectName);
        },
        enabled: !!projectName
    });

    const {data: latestProdDeployArtifactReport} = useQuery({
        queryKey: ['prodDeployArtifactReport', projectName],
        queryFn: async () => {
            if (projectName) return fetchArtifactReportFromLatestProdDeploy(projectName);
        },
        enabled: !!projectName
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
                            <a className={styles.commitInfo} href={focusedArtifactReport.artifactLink} target="_blank" rel="noopener noreferrer">
                                <img
                                    src={harbor_icon}
                                    alt="Harbor Logo"/>
                                <p><strong>Commit:</strong> {focusedArtifactReport.commitShortId}</p>
                                <p><strong>Severity:</strong> {focusedArtifactReport.severity}</p>
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
                    <div>Report not found</div>
                }
            </ProjectSection>
        </div>
    )

}