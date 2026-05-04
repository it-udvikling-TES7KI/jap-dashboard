import {useParams} from "react-router-dom";
import {useQuery} from "@tanstack/react-query";
import {fetchNomadJobsByProjectName} from "../nomad/NomadHook.ts";
import styles from "./Project.module.css";
import nomad_icon from "../../assets/nomad_icon.svg";

export default function Project() {

    const {projectName} = useParams()

    const {isError, error, isPending, data: nomadJobs} = useQuery({
        queryKey: ['nomadJobs', projectName],
        queryFn: () => {
            if (projectName) return fetchNomadJobsByProjectName(projectName);
        },
        enabled: !!projectName
    });

    if (isPending) {
        return <span>Loading...</span>
    }

    if (isError) {
        return <span>Error: {error.message}</span>
    }

    if (!projectName) return <div>No project data found.</div>;

    return (
        <div className={styles.container}>
            <h1 className={styles.projectName}>{projectName}</h1>
            <div className={styles.jobList}>
                <div className={styles.listTitle}>NomadJobs</div>
                {nomadJobs?.map((job) => (
                    <div key={job.id} className={styles.jobCard}>
                        <div className={styles.cardHeader}>
                            <div className={styles.jobName}>{job.name}</div>
                            <div className={styles.iconContainer}>
                                <a href={job.nomadLink} target="_blank" rel="noopener noreferrer">
                                    <img src={nomad_icon} alt={"Nomad Logo"}/>
                                </a>
                            </div>
                        </div>
                        <div className={styles.links}>
                            {job.serviceLink ?
                                <a href={job.serviceLink}
                                   target="_blank"
                                   rel="noopener noreferrer">
                                    {job.serviceLink}
                                </a>
                                :
                                "no link to service was found"}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}