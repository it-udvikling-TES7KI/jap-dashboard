import {ProjectPreview} from "../types/ProjectPreview";


export async function fetchProjectPreviews({pageParam = 1}) {

    const options = {
        method: 'GET',
    }

    const response = await fetch(`/api/project?page=${pageParam}&perPage=15`, options)
    return await response.json() as ProjectPreview[];
}