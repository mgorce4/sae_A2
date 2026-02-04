<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'

import { status, institutionLocation } from '../main'
status.value = 'Professeur'
institutionLocation.value = localStorage.institutionLocation

const resourceSheetsDTO = ref([]) // DTOs with all data pre-loaded

onMounted(async () => {
    try {
        const userId = localStorage.idUser

        if (!userId) {
            console.error('No user ID found in localStorage')
            return
        }

        const firstName = localStorage.firstName || localStorage.firstname || ''
        const lastName = localStorage.lastName || localStorage.lastname || ''
        const userName = `${firstName} ${lastName}`.trim()

        console.log('userId:', userId, '| userName:', userName)

        //all the resources-sheets
        const response = await axios.get(`${API_BASE_URL}/api/v2/resource-sheets`)
        
        console.log('Total fiches:', response.data.length)
        
        //To get the mainTeacher and the teacher
        resourceSheetsDTO.value = response.data.filter(sheet => {
            const isMainTeacher = sheet.mainTeacher === userName
            const isInTeachers = sheet.teachers && sheet.teachers.includes(userName)
            
            if (isMainTeacher || isInTeachers) {
                console.log('Match:', sheet.resourceLabel, '| mainTeacher:', sheet.mainTeacher, '| teachers:', sheet.teachers)
            }
            return isMainTeacher || isInTeachers
        })

        console.log(`${resourceSheetsDTO.value.length} fiche(s) pour ${userName}`)
    } catch (error) {
        console.error('Error loading resource sheets:', error)
    }
})


</script>

<template>
    <div id="ressources">
        <div id="for_scroll_bar" style="overflow-y: auto; overflow-x: hidden; margin: 1vw; height: 24vw">
            <p id="title">Vos ressources :</p>
            <div id="div_sheets">
                <p v-if="resourceSheetsDTO.length === 0" style="color: white; padding: 1vw">
                    Aucune ressource trouvée. Vous n'êtes professeur d'aucune ressource.
                </p>
                <RouterLink v-for="sheet in resourceSheetsDTO" :key="sheet.id" id="sheets" :to="`/form-ressource-sheet?id=${sheet.id}`">
                    <p>{{ sheet.resourceLabel }}</p>
                </RouterLink>
            </div>
        </div>
    </div>
</template>

<style>
#ressources {
    height: 25vw;
    margin: 3vw 14vw;
    justify-content: center;
    background-color: var(--main-theme-background-color);
    border-radius: 15px;
}

#title {
    color: var(--main-theme-secondary-color);
    padding-left: 10px;
    font-size: 1.5vw;
    width: fit-content;
}

#div_sheets {
    display: flex;
    flex-wrap: wrap;
}

#sheets {
    height: auto;
    min-height: 7vw;
    min-width: 20vw;
    max-width: 20vw;
    margin: 1em;
    padding-bottom: 1.2vw;
    background-color: var(--sub-section-background-color);
    border-radius: 15px;
    align-items: center;
    display: flex;
    overflow-x: auto;
    overflow-y: hidden;
}

#sheets > p {
    font-size: 4.5vw;
    color: var(--main-theme-secondary-color);
    text-align: center;
    margin: 2vw 2vw 0 2vw;
    white-space: nowrap;
}

#for_scroll_bar::-webkit-scrollbar {
    width: 12px;
}

#for_scroll_bar::-webkit-scrollbar-track {
    margin: 1em;
    background: var(--main-theme-secondary-background-color);
    box-shadow: inset 0 0 5px var(--sub-scrollbar-color);
    border-radius: 10px;
}

#for_scroll_bar::-webkit-scrollbar-thumb {
    background: var(--main-theme-secondary-color);
    border-radius: 10px;
}

#sheets::-webkit-scrollbar {
    height: 8px;
}

#sheets::-webkit-scrollbar-track {
    margin: 0 0.8vw 0.5vw 0.8vw;
    background: var(--main-theme-secondary-background-color);
    box-shadow: inset 0 0 5px var(--sub-scrollbar-color);
    border-radius: 10px;
}

#sheets::-webkit-scrollbar-thumb {
    background: var(--main-theme-secondary-color);
    border-radius: 10px;
}
</style>
