import styles from "./NomadSection.module.css";
import nomad_icon from "../../../assets/nomad_icon.svg";
import gitlab_icon from "../../../assets/gitlab_icon.svg";
import logscale_icon from "../../../assets/logscale_icon.svg";
import {useQuery} from "@tanstack/react-query";
import {NomadJob} from "../../../types/NomadJob";
import {fetchCommit} from "../../../hooks/GitlabHook.ts";

interface NomadJobCardProps {
    projectId: number;
    nomadJob: NomadJob;
}

export default function NomadJobCard({projectId, nomadJob}: NomadJobCardProps) {

    const {data: gitCommit} = useQuery({
        queryKey: ['commit', nomadJob.gitCommit],
        queryFn: () => {
            return fetchCommit(projectId, nomadJob.gitCommit)
        },
        enabled: (!!nomadJob.gitCommit && !!projectId)
    })

    return (
        <div className={styles.jobCard}>
            <a href={nomadJob.nomadLink} target="_blank" rel="noopener noreferrer" className={styles.cardHeader}>
                <div className={styles.jobName}>{nomadJob.name}</div>
                <div className={styles.nomadIconContainer}>
                    <img src={nomad_icon} alt="Nomad Logo"/>
                </div>
                <div className={styles.jobStatus}>
                    Nomad Status: <strong> {nomadJob.nomadStatus || "unknown"} </strong>
                </div>
            </a>
            <a
                href={nomadJob.serviceLink}
                target="_blank"
                rel="noopener noreferrer"
                className={styles.serviceLinkContainer}
            >
                <span className={styles.serviceLink}> {nomadJob.serviceLink} </span>
                <a className={styles.jobStatus}
                   href={nomadJob.healthURL}
                   target="_blank"
                   rel="noopener noreferrer"
                >
                    Health status: <strong> {nomadJob.healthStatus || "unknown"} </strong>
                </a>
            </a>
            <div className={styles.bottomSection}>
                <div className={styles.commitContainer}>
                    {gitCommit?.gitlabLink && (
                        <a
                            href={gitCommit?.gitlabLink}
                            target="_blank"
                            rel="noopener noreferrer"
                            className={styles.iconContainer}
                        >
                            <img src={gitlab_icon} alt="Gitlab logo"/>
                        </a>
                    )}
                    <div className={styles.commitInfo}>
                        {gitCommit ? (
                            <>
                                <span className={styles.commitAuthor}>{gitCommit.authorEmail} - {gitCommit.createdAt}</span>
                                <span className={styles.commitTitle}>{gitCommit.title}</span>
                            </>
                        ) : (
                            <span className={styles.commitFallback}>No commit information available</span>
                        )}
                    </div>
                </div>
                <a
                    href={nomadJob.logscaleLink}
                    target="_blank"
                    rel="noopener noreferrer"
                    className={styles.iconContainer}
                >
                    <img src={logscale_icon} alt="LogScale logo"/>
                </a>
            </div>
        </div>
    );
}