import {NomadJob} from "../types/NomadJob";

export async function fetchNomadJobsByProjectName(name: string) {

    const options = {
        method: 'GET',
    }

    const response = await fetch(`/api/nomad/jobs?projectName=${name}`, options)
    return await response.json() as NomadJob[];
}

export async function fetchProdNomadJobByProjectName(name: string) {

    const options = {
        method: 'GET',
    }

    const response = await fetch(`/api/nomad/job/prod?projectName=${name}`, options)
    return await response.json() as NomadJob;
}