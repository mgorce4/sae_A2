<script setup>
    import { ref, onMounted } from 'vue'
    import axios from 'axios'

    import { status, institutionLocation } from '../main'
    status.value = "Professeur"
    institutionLocation.value = localStorage.institutionLocation

    const resourceSheetsDTO = ref([]) // DTOs with all data pre-loaded

    onMounted(async () => {
        try {
            const userId = localStorage.idUser

            if (!userId) {
                console.error('No user ID found in localStorage')
                return
            }

            // Charger uniquement les fiches où l'utilisateur est professeur principal
            const response = await axios.get(`http://localhost:8080/api/v2/resource-sheets/teacher/${userId}`)
            resourceSheetsDTO.value = response.data

            console.log(`✅ Loaded ${resourceSheetsDTO.value.length} resource sheets for teacher ${userId}`)
        } catch (error) {
            console.error('Error loading resource sheets:', error)
        }
    })


    const goToRessourceSheet = (id) => {
        if (!id) return
        window.location.hash = `#/form-ressource-sheet?id=${id}`
    }
</script>

<template>
    <div id="ressources">
        <div id="for_scroll_bar" style="overflow-y: scroll; margin: 1vw; height: 24vw;">
            <p id="title">Vos ressources :</p>
            <div id="div_sheets">
                <p v-if="resourceSheetsDTO.length === 0" style="color: white; padding: 1vw;">
                    Aucune ressource trouvée. Vous n'êtes professeur principal d'aucune ressource.
                </p>
                <button
                    v-for="sheet in resourceSheetsDTO"
                    :key="sheet.id"
                    id="sheets"
                    @click="goToRessourceSheet(sheet.id)"
                >
                    <p>{{ sheet.resourceLabel }}</p>
                </button>
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
    box-shadow: inset 0 0 5px var(--sub-scrollbar-color);
    border-radius: 10px;
}

#for_scroll_bar::-webkit-scrollbar-thumb {
    background: var(--main-theme-secondary-color);
    border-radius: 10px;
}
</style>
