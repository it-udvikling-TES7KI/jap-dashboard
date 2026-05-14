import {useQuery} from "@tanstack/react-query";
import {fetchArtifactReportFromLatestMasterCommit} from "../../../hooks/HarborHook.ts";
import VulnerabilityBar from "./VulnerabilityBar.tsx";
import styles from "./ArtifactReportSection.module.css";
import ProjectSection from "../ProjectSection.tsx";
import harbor_icon from "../../../assets/harbor_icon.svg";


export interface ArtifactReportSectionProps {
    projectName: string;
}

export function ArtifactReportSection({projectName}: ArtifactReportSectionProps) {

    const {data: artifactReport} = useQuery({
        queryKey: ['artifactReport', projectName],
        queryFn: async () => {
            if (projectName) return fetchArtifactReportFromLatestMasterCommit(projectName);
        },
        enabled: !!projectName
    });

    if (artifactReport) {
        //todo add link to gitlab commit / gitlab job that made the build
        return (
            <ProjectSection title={"Harbor Artifact Report from latest commit to Master"}>
                {artifactReport ? (
                    <div className={styles.container}>
                        <a className={styles.commitInfo} href={artifactReport.artifactLink} target="_blank" rel="noopener noreferrer">
                            <img
                                src={harbor_icon}
                                alt="Harbor Logo"/>
                            <p><strong>Commit:</strong> {artifactReport.commitShortId}</p>
                            <p><strong>Severity:</strong> {artifactReport.severity}</p>
                        </a>
                        <ul>
                            <li>Critical: {artifactReport.critical}</li>
                            <li>High: {artifactReport.high}</li>
                            <li>Medium: {artifactReport.medium}</li>
                            <li>Low: {artifactReport.low}</li>
                            <li>Total: {artifactReport.total}</li>
                            <li>Fixable: {artifactReport.fixable}</li>
                        </ul>
                        <div className={styles.vulnerabilityBarContainer}>
                            <VulnerabilityBar artifactReport={artifactReport}></VulnerabilityBar>
                        </div>
                    </div>
                ) : (
                    <span>No artifact report found.</span>
                )}
            </ProjectSection>
        )
    }
}