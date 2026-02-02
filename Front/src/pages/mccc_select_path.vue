<script setup>
import { onMounted, ref, nextTick, watch, computed } from 'vue'
import axios from 'axios'
import { router } from '@/router'

const display_more_area = ref(false)
const coursName = ref('')
const coursNb = ref('')
const coursList = ref([])
const click = ref(false)

const errors = ref({
    coursName: false,
    coursNb: false,
})



const goToRessourceSheet = (url, pathId) => {
    localStorage.pathId = pathId
    router.push({
        path: url,
        query: {
            pathId: pathId
        }
    })
}

const attachAccordionListeners = () => {
    nextTick(() => {
        document.querySelectorAll('.accordion_UE').forEach((temp) => {
            const newAcc = temp.cloneNode(true)
            temp.parentNode.replaceChild(newAcc, temp)
            newAcc.addEventListener('click', function () {
                this.classList.toggle('active')
                const panel = this.nextElementSibling
                if (panel.style.maxHeight) {
                    panel.style.maxHeight = null
                    panel.style.padding = '0 18px'
                } else {
                    panel.style.maxHeight = panel.scrollHeight + 'px'
                    panel.style.padding = '3px 18px 15px'
                }
            })
        })
    })
}

// Watch for error changes to update panel height
watch(
    errors,
    () => {
        nextTick(() => {
            const panels = document.querySelectorAll('.panel_UE')
            panels.forEach((panel) => {
                if (panel.style.maxHeight && panel.style.maxHeight !== '0px') {
                    panel.style.maxHeight = panel.scrollHeight + 'px'
                }
            })
        })
    },
    { deep: true },
)

// Prevent typing invalid characters in number inputs
const preventInvalidChars = (event) => {
    const invalidChars = ['e', 'E', '+', '-', '.', ',']
    if (invalidChars.includes(event.key)) {
        event.preventDefault()
    }
}

const getCourses = computed(() => {
    return coursList.value
})

// Watch for changes to reattach accordion listeners
watch([coursList, display_more_area], () => {
    attachAccordionListeners()
})

onMounted(async () => {
    try {
        const response = await axios.get(`http://localhost:8080/api/paths`)
        // Filtrer les paths par institution et ajouter la propriété show
        coursList.value = response.data
            .filter(
                (path) =>
                    path.institution &&
                    path.institution.idInstitution === parseInt(localStorage.idInstitution),
            )
            .map((path) => ({
                ...path,
                show: false,
                edit: false,
            }))
        attachAccordionListeners()
    } catch (error) {
        console.error('Erreur lors du chargement des parcours:', error)
        coursList.value = []
    }
})

const isNameTaken = (name, excludeId = null) => {
    return coursList.value.some(
        (c) => c.name.trim().toLowerCase() === name.trim().toLowerCase() && c.idPath !== excludeId,
    )
}

const save = async () => {
    errors.value = {
        coursName: false,
        coursNb: false,
    }

    let hasErrors = false
    if (!coursName.value || coursName.value.trim() === '') {
        errors.value.coursName = true
        hasErrors = true
    }

    if (!coursNb.value || String(coursNb.value).trim() === '') {
        errors.value.coursNb = true
        hasErrors = true
    }

    if (hasErrors) {
        return
    }

    const coursNbIsNum = parseInt(coursNb.value)
    if (isNaN(coursNbIsNum)) {
        errors.value.coursNb = true
        return
    }

    if (isNameTaken(coursName.value)) {
        errors.value.nameUse = true
        return
    }

    // Vérifier que l'institution est définie
    if (!localStorage.idInstitution) {
        console.error('Institution non définie. Veuillez vous reconnecter.')
        alert('Erreur : Institution non définie. Veuillez vous reconnecter.')
        return
    }

    try {
        const response = {
            name: coursName.value,
            number: parseInt(coursNb.value),
            institution: {
                idInstitution: parseInt(localStorage.idInstitution),
            },
        }
        console.log('Envoi des données du parcours:', response)

        await axios.post('http://localhost:8080/api/paths', response)

        // Réinitialiser les champs
        coursName.value = ''
        coursNb.value = ''
        display_more_area.value = false

        // Recharger et filtrer les paths par institution
        const allPaths = await axios.get(`http://localhost:8080/api/paths`)
        coursList.value = allPaths.data
            .filter(
                (path) =>
                    path.institution &&
                    path.institution.idInstitution === parseInt(localStorage.idInstitution),
            )
            .map((path) => ({
                ...path,
                show: false,
            }))
        attachAccordionListeners()

        console.log('Parcours sauvegardé avec succès')
    } catch (error) {
        console.error('Erreur de sauvegarde:', error)
        console.error('Détails:', error.response?.data)
        alert('Erreur lors de la sauvegarde : ' + (error.response?.data?.message || error.message))
    }
}

const updateCourse = async (cours) => {
    if (isNameTaken(coursName.value)) {
        errors.value.nameUse = true
        alert('Nom de parcours est déjà utilisé.')
        return
    }

    try {
        const response = {
            name: cours.name,
            number: parseInt(cours.number),
            institution: {
                idInstitution: parseInt(localStorage.idInstitution),
            },
        }

        await axios.put(`http://localhost:8080/api/paths/${cours.idPath}`, response)
        click.value = false
        cours.show = false

        // Recharger la liste
        const allPaths = await axios.get(`http://localhost:8080/api/paths`)
        coursList.value = allPaths.data
            .filter(
                (path) =>
                    path.institution &&
                    path.institution.idInstitution === parseInt(localStorage.idInstitution),
            )
            .map((path) => ({
                ...path,
                show: false,
            }))
    } catch (error) {
        console.error('Erreur lors de la modification:', error)
    }
}

const del = async (id) => {
    if (
        !confirm(
            'Cette action est irréversible (pour le moment), continuer à vos risques et périls.',
        )
    ) {
        return
    }
    try {
        await axios.delete(`http://localhost:8080/api/paths/${id}`)

        // Recharger la liste après suppression
        const allPaths = await axios.get(`http://localhost:8080/api/paths`)
        coursList.value = allPaths.data
            .filter(
                (path) =>
                    path.institution &&
                    path.institution.idInstitution === parseInt(localStorage.idInstitution),
            )
            .map((path) => ({
                ...path,
                show: false,
            }))
    } catch (error) {
        console.error('Erreur lors de la suppression', error)
    }
}
</script>

<template>
    <div id="form_select_page">
        <div style="display: flex; align-items: center; height: 1vw">
            <RouterLink class="back_arrow" to="/dashboard-administration">←</RouterLink>
            <p class="back">Retour à l'accueil</p>
        </div>
        <div id="background_path">
            <div class="form">
                <div id="header_UE">
                    <p id="title">Parcours</p>
                </div>
                <div id="dark_bar">
                    <p>Ajouter un parcours</p>
                    <button id="button_more" v-on:click="display_more_area = true">+</button>
                </div>

                <!--creer le formulaire-->
                <form v-show="display_more_area" method="post" v-on:submit.prevent="">
                    <a class="accordion_UE" id="dark_bar">Ajout d'un parcours :</a>
                    <div class="panel_UE" style="padding-top: 0.6vw; flex-wrap: wrap">
                        <div style="margin-right: 0.5vw">
                            <label>Nom du parcours : <span class="required">*</span></label>
                            <input type="text" class="input" v-model="coursName" />
                            <span v-if="errors.coursName" class="error-message"
                                >Merci de remplir ce champ</span
                            >
                            <span v-if="errors.nameUse" class="error-message"
                                >Nom de parcours est déjà utilisé.</span
                            >
                        </div>

                        <div style="margin-right: 0.5vw">
                            <label
                                >Nombre associé au parcours : <span class="required">*</span></label
                            >
                            <input
                                type="number"
                                step="1"
                                class="input"
                                v-model="coursNb"
                                @keydown="preventInvalidChars"
                            />
                            <span v-if="errors.coursNb" class="error-message"
                                >Merci de remplir ce champ</span
                            >
                        </div>

                        <div
                            style="
                                display: flex;
                                margin: 0.5vw 1vw;
                                justify-content: center;
                                width: 100%;
                            "
                        >
                            <input
                                class="btn1"
                                type="button"
                                value="Annuler"
                                @click="display_more_area = false"
                                style="margin-right: 1vw"
                            />
                            <input class="btn1" type="submit" value="Sauvegarder" @click="save" />
                        </div>
                    </div>
                </form>
                <!--here's your path -->
                <div v-for="cours in getCourses" :key="cours.idPath">
                    <div
                        class="path"
                        v-on:mouseover="cours.show = true"
                        v-on:mouseout="!cours.edit ? (cours.show = false) : null"
                        @click="goToRessourceSheet('/mccc-select-form', cours.idPath)"
                    >
                        <p>{{ cours.name }}</p>
                        <div v-show="cours.show || cours.edit" @click.stop>
                            <div
                                style="
                                    display: flex;
                                    margin-right: 4vw;
                                    justify-content: center;
                                    width: 100%;
                                "
                            >
                                <button
                                    v-if="!cours.edit"
                                    @click="cours.edit = true"
                                    class="btn_modify"
                                >
                                    Modifier
                                </button>
                                <button
                                    v-if="!cours.edit"
                                    class="btn_modify"
                                    @click="del(cours.idPath)"
                                >
                                    Supprimer
                                </button>
                            </div>
                            <div v-if="cours.edit" style="margin: 1vw 4vw">
                                <div>
                                    <label style="font-size: 0.9vw"
                                        >Nom du parcours : <span class="required">* </span></label
                                    >
                                    <input type="text" class="inp_path" v-model="cours.name" />
                                </div>
                                <div>
                                    <label style="font-size: 0.9vw"
                                        >Nombre associé au parcours :
                                        <span class="required">* </span></label
                                    >
                                    <input
                                        type="number"
                                        step="1"
                                        class="inp_path"
                                        v-model="cours.number"
                                        @keydown="preventInvalidChars"
                                    />
                                </div>
                                <div style="display: flex">
                                    <button
                                        class="btn_path"
                                        type="button"
                                        @click="cours.edit = false"
                                    >
                                        Annuler
                                    </button>
                                    <button
                                        class="btn_path"
                                        type="submit"
                                        @click="updateCourse(cours)"
                                    >
                                        Sauvegarder
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style>
#form_select_page {
    margin: 3vw 14vw;
    justify-content: center;
    color: var(--main-theme-tertiary-color);
}

.back {
    font-size: 1.5vw;
    font-weight: bold;
    color: var(--main-theme-tertiary-color);
    margin-left: 1.5vw;
}

#background_path {
    height: auto;
    background-color: var(--main-theme-background-color);
    border-radius: 15px;
    overflow-x: hidden;
    overflow-y: hidden;
    box-sizing: border-box;
    padding-bottom: 1vw;
    margin-top: 1vw;
}

#title {
    color: var(--main-theme-secondary-color);
    text-align: center;
    padding-top: 0.5vw;
    padding-bottom: 0.5vw;
    font-weight: lighter;
    font-size: 2.1vw;
    margin: 0;
}

.required {
    color: white;
    font-weight: bold;
}

.error-message {
    color: red;
    font-size: 0.8vw;
    margin-top: 0.2vw;
    display: block;
}

/* Remove number input spinners/arrows */
input[type='number']::-webkit-inner-spin-button,
input[type='number']::-webkit-outer-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

input[type='number'] {
    -moz-appearance: textfield;
    appearance: textfield;
}

.path {
    height: 5.5vw;
    background-color: var(--sub-scrollbar-color);
    border-radius: 0.5vw;
    margin: 2% 0 0 0;
    color: var(--main-theme-secondary-color);
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.path p {
    font-size: 1.5vw;
    margin: 3.5vw;
}

.btn_path {
    cursor: pointer;
    background-color: var(--clickable-background-color) /*#242222*/;
    border-color: var(--main-theme-tertiary-color);
    height: 1.3vw;
    color: white;
    font-size: 0.8vw;
    border-radius: 0.5vw;
    padding: 0 1vw;
    display: flex;
    align-items: center;
    margin-right: 0.5vw;
}

.inp_path {
    border-radius: 5px;
    background-color: rgba(117, 117, 117, 100);
    color: var(--main-theme-secondary-color);
    width: 5vw;
    font-size: 0.8vw;
}

.btn_modify {
    cursor: pointer;
    background-color: var(--clickable-background-color) /*#242222*/;
    border-color: var(--main-theme-tertiary-color);
    height: 3vw;
    color: white;
    font-size: 0.8vw;
    border-radius: 0.5vw;
    padding: 0 1vw;
    margin-right: 1vw;
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>
