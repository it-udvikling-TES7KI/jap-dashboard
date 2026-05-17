import {ArtifactReportFocus, getArtifactReportLabel} from "../types/ArtifactReportFocus.ts";
import styles from "./ArtifactReportNavbar.module.css";

export interface ArtifactReportNavbarProps {
    activeTab: ArtifactReportFocus;
    setActiveTab: (activeTab: ArtifactReportFocus) => void;
}

const tabOptions = Object.values(ArtifactReportFocus)
    .filter((value) => typeof value === "number")
    .map((value) => ({
        key: value as ArtifactReportFocus,
        label: getArtifactReportLabel(value as ArtifactReportFocus)
    }));

export function ArtifactReportNavbar({activeTab, setActiveTab}: ArtifactReportNavbarProps) {
    return (
        <nav className={styles.tabs}>
            <div className={`${styles.tab} ${styles.tabHeader}`}>Focused Artifact Report:</div>
            {tabOptions.map(tab => (
                <div
                    key={tab.key}
                    onClick={() => setActiveTab(tab.key)}
                    className={`${styles.tab} ${activeTab === tab.key ? styles.activeTab : ""}`}>
                    {tab.label}
                </div>
            ))}
        </nav>
    );
}