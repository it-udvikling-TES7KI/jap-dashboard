import {useInfiniteQuery} from "@tanstack/react-query";
import styles from "./ProjectDashboard.module.css";
import ProjectCard from "./ProjectCard.tsx";
import {useState} from "react";
import {ArtifactReportNavbar} from "../../components/ArtifactReportNavbar.tsx";
import {ArtifactReportFocus} from "../../types/ArtifactReportFocus.ts";
import {fetchGitlabProjects} from "../../hooks/GitlabHook.ts";

export default function ProjectDashboard() {
    const [activeTab, setActiveTab] = useState(ArtifactReportFocus.LatestMasterCommit);
    const {
        data,
        error,
        fetchNextPage,
        hasNextPage,
        isFetching,
        isFetchingNextPage,
    } =
        useInfiniteQuery({
            queryKey: ['gitlabProjects'],
            queryFn: fetchGitlabProjects,
            initialPageParam: 1,
            getNextPageParam: (lastPage, _allPages, lastPageParam) => {
                if (lastPage.length === 0) {
                    return undefined
                }
                return lastPageParam + 1
            },
            getPreviousPageParam: (_firstPage, _allPages, firstPageParam) => {
                if (firstPageParam <= 1) {
                    return undefined
                }
                return firstPageParam - 1
            },
        })

    function getLoadButtonLabel() {
        if (error != null) return 'Failed to load projects'
        if (isFetchingNextPage) return 'Loading more...'
        if (isFetching) return 'Fetching...'
        if (!hasNextPage) return 'Nothing more to load'
        return 'Load more'
    }

    const isLoadButtonDisabled =
        error != null ||
        isFetchingNextPage ||
        isFetching ||
        !hasNextPage


    return (
        <div>
            <h1 className={styles.header}>JAP Dashboard</h1>
            <ArtifactReportNavbar activeTab={activeTab} setActiveTab={setActiveTab}></ArtifactReportNavbar>
            <div className={styles.page}>
                <div className={styles.grid}>
                    {data?.pages.flatMap((group) =>
                        group.map((project, index) => (
                            <ProjectCard
                                key={index}
                                gitProject={project}
                                artifactReportFocus={activeTab}
                            />
                        ))
                    )}
                </div>
                <div className={styles.loadMoreSection}>
                    <button
                        className={styles.loadMoreButton}
                        onClick={() => fetchNextPage()}
                        disabled={isLoadButtonDisabled}>
                        {getLoadButtonLabel()}
                    </button>
                </div>
            </div>
        </div>
    );
}