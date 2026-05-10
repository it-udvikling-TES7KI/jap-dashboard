import {useQuery} from "@tanstack/react-query";
import {fetchArtifactReportFromLatestMasterCommit} from "../../../hooks/HarborHook.ts";

export interface ArtifactReportSectionProps {
    projectName: string;
}

export function ArtifactReportSection({ projectName }: ArtifactReportSectionProps) {

    const {isError, error, isPending, data: artifactReport} = useQuery({
        queryKey: ['artifactReport', projectName],
        queryFn: () => {
            if (projectName) return fetchArtifactReportFromLatestMasterCommit(projectName);
        },
        enabled: !!projectName
    });

    if (isPending) {
        return <span>Loading...</span>
    }

    if (isError) {
        return <span>Error: {error.message}</span>
    }


    return (
        <section>
            <h2>Harbor Section - {projectName}</h2>
            {artifactReport ? (
                <div>
                    <p>
                        <strong>Harbor Link:</strong>{" "}
                        <a href={artifactReport.harborLink} target="_blank" rel="noopener noreferrer">
                            {artifactReport.harborLink}
                        </a>
                    </p>
                    <p><strong>Commit:</strong> {artifactReport.commitShortId}</p>
                    <p><strong>Severity:</strong> {artifactReport.severity}</p>
                    <ul>
                        <li>Critical: {artifactReport.critical}</li>
                        <li>High: {artifactReport.high}</li>
                        <li>Medium: {artifactReport.medium}</li>
                        <li>Low: {artifactReport.low}</li>
                        <li>Total: {artifactReport.total}</li>
                    </ul>
                </div>
            ) : (
                <span>No artifact report found.</span>
            )}
        </section>
    )
}