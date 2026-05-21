import styles from "./ProjectSection.module.css"
import * as React from "react";

interface ProjectSectionProps {
    title: string
    children: React.ReactNode
}

export default function ProjectSection({title, children}: ProjectSectionProps) {


    return (
        <section className={styles.section}>
            <h2 className={styles.title}>{title}</h2>
            {children}
        </section>
    )

}