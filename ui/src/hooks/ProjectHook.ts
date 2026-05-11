import {ProjectPreview} from "../types/ProjectPreview";


export async function fetchProjectPreviews() {

    const options = {
        method: 'GET',
    }

    const response = await fetch(`/api/project?page=1&perPage=25`, options)
    return await response.json() as ProjectPreview[];
}