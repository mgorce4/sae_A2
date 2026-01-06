<script setup>
    import { ref, onMounted, computed } from 'vue'
    import axios from 'axios'

    import { status, institutionLocation } from '../main' /*add , userName next to status to import it*/
    status.value = "Professeur"
    institutionLocation.value = localStorage.institutionLocation

    const resourceSheets = ref([]) //get the data from database ressources-sheets
    const mainTeacherForRessource = ref([])

    onMounted(async () => {
        console.log('🔄 Loading data from APIs...')

        try {
            const resourceSheetsResponse = await axios.get('http://localhost:8080/api/resource-sheets')
            resourceSheets.value = resourceSheetsResponse.data
            console.log('✅ Resource sheets loaded:', resourceSheets.value.length, 'items')
            console.log('📋 Resource sheets data:', resourceSheets.value)
        } catch (error) {
            console.error('❌ Error loading resource sheets:', error)
        }

        try {
            const mainTeachersResponse = await axios.get('http://localhost:8080/api/main-teachers-for-resource')
            mainTeacherForRessource.value = mainTeachersResponse.data
            console.log('✅ Main teachers loaded:', mainTeacherForRessource.value.length, 'items')
            console.log('👥 Main teachers data:', mainTeacherForRessource.value)
        } catch (error) {
            console.error('❌ Error loading main teachers:', error)
        }
    })

    const joinTables = computed(() => {
        console.log('🔍 Computing joinTables...')
        console.log('📋 resourceSheets.value:', resourceSheets.value)
        console.log('👥 mainTeacherForRessource.value:', mainTeacherForRessource.value)
        console.log('🏢 localStorage.idInstitution:', localStorage.idInstitution)

        // Si mainTeacherForRessource est vide, afficher toutes les resource sheets de l'institution
        if (mainTeacherForRessource.value.length === 0) {
            console.log('⚠️ No main teachers found, showing all resource sheets for institution')
            const allSheets = resourceSheets.value
                .filter(sheet => {
                    const hasResource = sheet.resource && sheet.resource.idResource
                    console.log('📝 Resource sheet:', sheet.idResourceSheet, '-> has resource?', hasResource)
                    return hasResource
                })
                .map(sheet => ({
                    resource: sheet.resource,
                    resourceSheet: [sheet],
                    user: {
                        institution: { idInstitution: localStorage.idInstitution },
                        firstname: 'Unknown'
                    }
                }))
            console.log('✅ Fallback result (all sheets):', allSheets)
            return allSheets
        }

        // Join between users and access_rights where users.idUser = access_rights.idUser
        const mapped = mainTeacherForRessource.value.map((teacher) => {
            const resourceSheet = resourceSheets.value.filter((ar) => ar.resource && ar.resource.idResource === teacher.resource.idResource)
            console.log(`📝 Teacher ${teacher.user?.firstname} -> resourceSheets found:`, resourceSheet.length)
            return {
                ...teacher,
                resourceSheet: resourceSheet
            }
        })

        console.log('📊 Mapped data (before institution filter):', mapped)

        const result = mapped.filter(teacher => {
            const matches = teacher.user && teacher.user.institution && teacher.user.institution.idInstitution == localStorage.idInstitution
            console.log(`🏢 Institution check for ${teacher.user?.firstname}: ${teacher.user?.institution?.idInstitution} == ${localStorage.idInstitution} ? ${matches}`)
            return matches
        })

        console.log('✅ Final joinTables result:', result)
        return result
    })

    const goToRessourceSheet = (id) => {
        console.log('📋 Navigating to resource sheet with ID:', id)
        if (!id) {
            console.error('❌ No ID provided to goToRessourceSheet')
            return
        }
        window.location.hash = `#/form-ressource-sheet?id=${id}`
    }
</script>

<template>
    <div id="ressources" >
        <div id="for_scroll_bar" style="overflow-y: scroll; margin: 1vw; height: 24vw;">
            <p id="title">Vos ressources : </p>
            <div id="div_sheets" >
                <p v-if="joinTables.length === 0" style="color: white; padding: 1vw;">
                    Aucune ressource trouvée pour votre institution.
                </p>
                <template v-for="u in joinTables" :key="u.resource?.idResource">
                    <button
                        v-if="u.resourceSheet && u.resourceSheet.length > 0"
                        id="sheets"
                        @click="goToRessourceSheet(u.resourceSheet[0]?.idResourceSheet)"
                    >
                        <p>{{ u.resource?.label }}</p>
                    </button>
                </template>
            </div>
        </div>
    </div>
</template>

<style>


#ressources{
    height: 25vw;
    margin: 3vw 14vw;
    justify-content: center;
    background-color: var(--main-theme-background-color);
    border-radius: 15px;
}

#title{
    color: var(--main-theme-secondary-color);
    padding-left: 10px ;
    font-size: 1.5vw;
    width: fit-content;
}

#div_sheets{
    display: flex;
    flex-wrap: wrap;
    padding: 0;
}

#sheets{
    min-height: 7vw;
    max-height: 10vw;
    min-width: 20vw;
    margin: 1em;
    background-color: var(--sub-section-background-color);
    border-radius: 15px;
    align-items: center;
}

#sheets > p{
    font-size: 4.5vw;
    color: var(--main-theme-secondary-color);
    text-align: center;
    margin: 2vw;
}

#for_scroll_bar::-webkit-scrollbar {
    width: 12px;
}

#for_scroll_bar::-webkit-scrollbar-track {
    margin: 1em;
    background: var(--main-theme-secondary-background-color);
    box-shadow: inset 0 0 5px rgb(24, 26, 50);
    border-radius: 10px;
}

#for_scroll_bar::-webkit-scrollbar-thumb {
    background: var(--main-theme-secondary-color);
    border-radius: 10px;
}
</style>
