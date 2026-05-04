import {NomadJob} from "./NomadJob";

export async function fetchNomadJobsByProjectName(name: string) {

    const options = {
        method: 'GET',
    }

    const response = await fetch(`/api/nomad/jobs?projectName=${name}`, options)
    return await response.json() as NomadJob[];
}