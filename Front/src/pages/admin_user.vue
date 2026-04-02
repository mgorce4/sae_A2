<script setup>

import { computed, nextTick, onMounted, ref } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import { status } from '@/main.js'
import {
    getIdFromToken,
    getIdInstitutionFromToken,
    getInstitutionNameFromToken,
    getInstitutionLocationFromToken,
    getAccessRightsFromToken
} from '@/utils/jwt.js'

let display_more_area = ref(false)
let is_modifying = ref(false)

let title = ref("")

const teacher_name = ref("")
const teacher_firstname = ref("")
const teacher_mail = ref("")
const teacher_id = ref(0)
const access_right = ref(0)
const show_popup = ref(false)

const errors = ref({
    name: false,
    firstname: false,
    mail: false,
})

const error_messages = ref({
    name: "Le nom doit être renseigné",
    firstname: "Le prenom doit être renseigné",
    mail: "Le mail doit être renseigné",
})

const teachers = ref([])
const administrations = ref([])

const csvFileName = ref('')
const csvRows = ref([])
const csvHasHeader = ref(false)
const csvMapping = ref({
    lastname: 0,
    firstname: 1,
    mail: 2,
})
const csvIsImporting = ref(false)
const csvImportResult = ref({
    success: 0,
    failed: 0,
})
const csvErrorMessage = ref('')

const csvColumnCount = computed(() => {
    if (csvRows.value.length === 0) {
        return 0
    }

    return csvRows.value.reduce((max, row) => Math.max(max, row.length), 0)
})

const csvColumnOptions = computed(() => {
    return Array.from({ length: csvColumnCount.value }, (_, index) => {
        if (csvHasHeader.value && csvRows.value[0] && csvRows.value[0][index]) {
            const headerValue = String(csvRows.value[0][index]).trim()
            return {
                value: index,
                label: `Colonne ${index + 1} (${headerValue})`,
            }
        }

        return {
            value: index,
            label: `Colonne ${index + 1}`,
        }
    })
})

const csvDataRows = computed(() => {
    if (!csvHasHeader.value) {
        return csvRows.value
    }

    return csvRows.value.slice(1)
})

const csvPreviewRows = computed(() => csvDataRows.value.slice(0, 5))

const attachAccordionListeners = () => {
    nextTick(() => {
        const accordions = document.querySelectorAll('.accordion_teacher')

        accordions.forEach((accordion) => {
            accordion.onclick = function () {
                this.classList.toggle('active')
                const panel = this.nextElementSibling

                if (!panel) {
                    return
                }

                if (panel.style.maxHeight) {
                    panel.style.maxHeight = null
                    panel.style.padding = null
                } else {
                    panel.style.maxHeight = panel.scrollHeight + 'px'
                    panel.style.padding = '0 18px'
                }
            }
        })
    })
}

onMounted(async () => {
    const response = await axios.get(`${API_BASE_URL}/api/access-rights`)
    const usersInInstitution = response.data.filter((entry) => entry.user.institution.idInstitution === getIdInstitutionFromToken())
    teachers.value = usersInInstitution.filter((ar) => ar.accessRight === 1)
    administrations.value = usersInInstitution.filter((ar) => ar.accessRight === 2)

    await nextTick()
    attachAccordionListeners()
})

function addTeacher() {
    title.value = "Ajouter un professeur"
}

function splitCsvLine(line, delimiter) {
    const values = []
    let current = ''
    let inQuotes = false

    for (let i = 0; i < line.length; i++) {
        const char = line[i]

        if (char === '"') {
            const nextChar = line[i + 1]
            if (inQuotes && nextChar === '"') {
                current += '"'
                i++
            } else {
                inQuotes = !inQuotes
            }
            continue
        }

        if (char === delimiter && !inQuotes) {
            values.push(current.trim())
            current = ''
            continue
        }

        current += char
    }

    values.push(current.trim())
    return values
}

function detectCsvDelimiter(text) {
    const sampleLine = text
        .split(/\r?\n/)
        .map((line) => line.trim())
        .find((line) => line.length > 0)

    if (!sampleLine) {
        return ','
    }

    const delimiters = [',', ';', '\t']
    let bestDelimiter = ','
    let bestCount = -1

    delimiters.forEach((delimiter) => {
        const count = sampleLine.split(delimiter).length
        if (count > bestCount) {
            bestCount = count
            bestDelimiter = delimiter
        }
    })

    return bestDelimiter
}

function parseCsvText(text) {
    const delimiter = detectCsvDelimiter(text)

    return text
        .split(/\r?\n/)
        .map((line) => line.trim())
        .filter((line) => line.length > 0)
        .map((line) => splitCsvLine(line, delimiter))
}

function resetCsvMapping() {
    const defaults = [0, 1, 2]

    csvMapping.value = {
        lastname: defaults[0] < csvColumnCount.value ? defaults[0] : 0,
        firstname: defaults[1] < csvColumnCount.value ? defaults[1] : 0,
        mail: defaults[2] < csvColumnCount.value ? defaults[2] : 0,
    }
}

function onCsvFileChange(event) {
    const file = event.target.files?.[0]

    csvErrorMessage.value = ''
    csvImportResult.value = { success: 0, failed: 0 }

    if (!file) {
        csvFileName.value = ''
        csvRows.value = []
        return
    }

    csvFileName.value = file.name

    const reader = new FileReader()
    reader.onload = (loadEvent) => {
        const content = String(loadEvent.target?.result || '')
        const parsedRows = parseCsvText(content)

        if (parsedRows.length === 0) {
            csvRows.value = []
            csvErrorMessage.value = 'Le fichier CSV est vide ou invalide.'
            return
        }

        csvRows.value = parsedRows
        resetCsvMapping()
    }

    reader.onerror = () => {
        csvRows.value = []
        csvErrorMessage.value = 'Impossible de lire le fichier CSV.'
    }

    reader.readAsText(file, 'UTF-8')
}

async function createUserWithAccessRight(payload, selectedAccessRight) {
    const userResponse = await axios.post(`${API_BASE_URL}/api/users`, payload)
    const user = userResponse.data

    await axios.post(`${API_BASE_URL}/api/access-rights`, {
        accessRight: selectedAccessRight,
        idUser: user.idUser,
    })
}

async function importTeachersFromCsv() {
    csvErrorMessage.value = ''
    csvImportResult.value = { success: 0, failed: 0 }

    if (csvDataRows.value.length === 0) {
        csvErrorMessage.value = 'Veuillez charger un fichier CSV avant import.'
        return
    }

    const selectedIndexes = [
        csvMapping.value.lastname,
        csvMapping.value.firstname,
        csvMapping.value.mail,
    ]

    if (new Set(selectedIndexes).size !== selectedIndexes.length) {
        csvErrorMessage.value = 'Chaque champ doit utiliser une colonne differente.'
        return
    }

    if (!getIdFromToken()) {
        csvErrorMessage.value = 'Veuillez vous reconnecter avant d\'importer.'
        return
    }

    csvIsImporting.value = true

    let success = 0
    let failed = 0
    const allUsersResp = await axios.get(`${API_BASE_URL}/api/users`)
    const allUsers = Array.isArray(allUsersResp.data) ? allUsersResp.data : []
    const usedUsernames = new Set(
        allUsers
            .map((user) => user?.username)
            .filter((username) => typeof username === 'string' && username.length > 0)
            .map((username) => username.toLowerCase()),
    )

    try {
        for (const row of csvDataRows.value) {
            const lastname = String(row[csvMapping.value.lastname] || '').trim()
            const firstname = String(row[csvMapping.value.firstname] || '').trim()
            const mail = String(row[csvMapping.value.mail] || '').trim()

            if (!lastname || !firstname || !mail) {
                failed++
                continue
            }

            const username = getUsername(firstname, lastname, usedUsernames)
            const payload = {
                firstname,
                lastname,
                username,
                password: `${username}123`,
                mail,
                institution: {
                    idInstitution: getIdInstitutionFromToken(),
                    name: getInstitutionNameFromToken(),
                    location: getInstitutionLocationFromToken(),
                },
            }

            try {
                await createUserWithAccessRight(payload, 1)
                success++
            } catch (error) {
                failed++
                console.error('Erreur import ligne CSV:', error)
            }
        }

        csvImportResult.value = { success, failed }
        await reloadTeachers()
        attachAccordionListeners()
    } finally {
        csvIsImporting.value = false
    }
}

function getUsername(firstname, lastname, usedUsernames) {
    const baseRaw = `${firstname?.trim()?.charAt(0) || ''}${lastname?.trim() || ''}`
    const baseUsername = baseRaw.toLowerCase().replace(/\s+/g, '') || 'user'

    let candidate = baseUsername
    let suffix = 1
    while (usedUsernames.has(candidate)) {
        candidate = `${baseUsername}${suffix}`
        suffix++
    }
    usedUsernames.add(candidate)
    return candidate
}

const save = async () => {

    // reste all errors
    errors.value = {
        name: false,
        firstname: false,
        mail: false,
    }

    let hasError = false

    if (teacher_name.value === "") {
        errors.value.name = true
        hasError = true
    }

    if (teacher_firstname.value === "") {
        errors.value.firstname = true
        hasError = true
    }

    if (teacher_mail.value === "") {
        errors.value.mail = true
        hasError = true
    }

    if (hasError) {
        return
    }

    try {
        if (!getIdFromToken()) {
            alert('Erreur : Veuillez vous reconnecter.')
            return
        }

        // Récupère tous les users côté serveur pour éviter toute collision non détectée
        const allUsersResp = await axios.get(`${API_BASE_URL}/api/users`)
        const allUsers = Array.isArray(allUsersResp.data) ? allUsersResp.data : []
        const usedUsernames = new Set(
            allUsers
                .map((user) => user?.username)
                .filter((username) => typeof username === 'string' && username.length > 0)
                .map((username) => username.toLowerCase()),
        )
        const username_calc = getUsername(teacher_firstname.value, teacher_name.value, usedUsernames)

        const payload = {
            firstname : teacher_firstname.value,
            lastname : teacher_name.value,
            username : username_calc,
            password : username_calc + '123',
            mail : teacher_mail.value,
            institution : {
                idInstitution : getIdInstitutionFromToken(),
                name : getInstitutionNameFromToken(),
                location : getInstitutionLocationFromToken(),
            },
        }

        if (!is_modifying.value) {
            await createUserWithAccessRight(payload, access_right.value)
            ;[teacher_firstname, teacher_name, teacher_mail].forEach((r) => {
                r.value = ''
            })
            display_more_area.value = false
        } else {
            await axios.put(`${API_BASE_URL}/api/users/${teacher_id.value}`, payload)

            ;[teacher_firstname, teacher_name, teacher_mail].forEach((r) => {
                r.value = ''
            })
            display_more_area.value = false
            is_modifying.value = false
        }

        await reloadTeachers()
        attachAccordionListeners()

        console.log('professeur sauvegardé avec succès')
    } catch (error) {
        console.error('Erreur lors de la sauvegarde:', error)
        if (error.response) {
            console.error("Détails de l'erreur:", error.response.data)
            console.error('Status:', error.response.status)
        }
        alert('Erreur lors de la sauvegarde. Consultez la console pour plus de détails.')
    }
}

async function reloadTeachers() {
    const response = await axios.get(`${API_BASE_URL}/api/access-rights`)
    const usersInInstitution = response.data.filter((entry) => entry.user.institution.idInstitution === getIdInstitutionFromToken())
    teachers.value = usersInInstitution.filter((ar) => ar.accessRight === 1)
    administrations.value = usersInInstitution.filter((ar) => ar.accessRight === 2)
}

function modify(teacher) {
    title.value = "Modifier un professeur"
    teacher_name.value = teacher.user.lastname
    teacher_firstname.value = teacher.user.firstname
    teacher_mail.value = teacher.user.mail
    teacher_id.value = teacher.idUser
}

const deleteTeacher = async (id) => {
    if (
        !confirm(
            'Cette action est irréversible (pour le moment), continuer à vos risques et périls.',
        )
    ) {
        return
    }
    try {
        let main_resources = await axios.get(`${API_BASE_URL}/api/main-teachers-for-resource/user/${id}`)
        let main_resources_data = main_resources.data

        if (main_resources_data.length > 0) {

            for (let i = 0; i < main_resources_data.length; i++) {
                await axios.delete(`${API_BASE_URL}/api/main-teachers-for-resource/user/${id}/resource/${main_resources_data[i].idResource}`)
            }

            for (let i = 0; i < main_resources_data.length; i++) {
                alert('Vous venez de supprimer un professeur référent de la ressource' + main_resources_data[i].resourceLabel + '. Veuillez rajouter un nouveau professeur référent pour cette ressource.')
            }
        }

        let normal_resources = await axios.get(`${API_BASE_URL}/api/teachers-for-resource/user/${id}`)
        let normal_resources_data = normal_resources.data

        if (normal_resources_data.length > 0) {

            for (let i = 0; i < normal_resources_data.length; i++) {
                await axios.delete(`${API_BASE_URL}/api/teachers-for-resource/user/${id}/resource/${normal_resources_data[i].idResource}`)
            }

            for (let i = 0; i < normal_resources_data.length; i++) {
                alert('Vous venez de supprimer un professeur enseignant de la ressource' + normal_resources_data[i].resourceLabel + '. Si besoin, veuillez rajouter un nouveau professeur enseignant pour cette ressource si nécessaire.')
            }
        }

        await axios.delete(`${API_BASE_URL}/api/users/${id}`)
        await axios.delete(`${API_BASE_URL}/api/access-rights/${access_right.value || 1}/${id}`)

        await reloadTeachers()

        attachAccordionListeners()
    } catch (error) {
        console.error('Erreur lors de la suppression', error)
    }
}

function toggleShowPopUp() {
    show_popup.value = !show_popup.value
}

const access_right_list = getAccessRightsFromToken()

</script>

<template>
    <div id="main">
        <div id="return_arrow">
            <div v-if="Number(access_right_list.length) === 1">
                <RouterLink v-if="status==='Administration'" id="back_arrow" to="/dashboard-administration">←</RouterLink>
                <RouterLink v-else-if="status==='Admin'" id="back_arrow" to="/admin-dashboard">←</RouterLink>
                <RouterLink v-else-if="status==='Super Admin'" id="back_arrow" to="/sup-admin-dashboard">←</RouterLink>
            </div>
            <RouterLink to="/multi-access-right-dashboard" id="back_arrow" v-else>←</RouterLink>
            <p>Retour</p>
        </div>

        <div class="background">
            <div id="form">
                <div id="header">
                    <p id="title">Ajouter un utilisateur</p>
                </div>

                <div id="dark_bar">
                    <p>Ajout d'un utilisateur</p>
                    <button id="button_more" v-on:click="display_more_area = !display_more_area;  addTeacher()">
                        {{ display_more_area ? '-' : '+' }}
                    </button>
                </div>

                <form v-show="display_more_area" method="post" v-on:submit.prevent="">
                    <a class="accordion_teacher dark_bar" data-accordion="add-modify-teacher">{{ title }}</a>

                    <div class="panel" style="display: flex">
                        <div style="margin-left: 15vw; padding-top: 1vw">
                            <div class="sub_div_panel">
                                <label style="font-size: 1vw;">Nom : </label>
                                <input type="text" class="input" style=" height: 3vh; width: 17vw; font-size: 1.5vh;" v-model="teacher_name">
                                <input style="margin-left: 11.5vw" class="btn1" type="reset" value="Annuler" v-on:click="display_more_area = !display_more_area" />
                            </div>

                            <p v-if="errors.name" class="error_message" style="text-align: left">{{ error_messages.name }}</p>

                            <div class="sub_div_panel">
                                <label style="font-size: 1vw;">Prenom : </label>
                                <input type="text" class="input" style=" height: 3vh; width: 17vw; font-size: 1.5vh;" v-model="teacher_firstname">
                                <input style="margin-left: 10vw" id="save" class="btn1" type="button" value="Sauvegarder" v-on:click="save()" />
                            </div>

                            <p v-if="errors.firstname" class="error_message" style="text-align: left">{{ error_messages.firstname }}</p>

                            <div class="sub_div_panel">
                                <label style="font-size: 1vw;">Mail : </label>
                                <input type="text" class="input" style=" height: 3vh; width: 17vw; font-size: 1.5vh;" v-model="teacher_mail">
                            </div>

                            <p v-if="errors.mail" class="error_message" style="text-align: left">{{ error_messages.mail }}</p>

                            <div class="sub_div_panel container-fluid">
                                <label style="font-size: 1vw;">Statut : </label>
                                <div class="component ue_div">
                                    <select id="ue_select" class="input" v-model="access_right" style=" height: 3vh; width: 8vw; font-size: 1.5vh;">
                                        <option value="">Sélectionner un statut</option>
                                        <option value="1">professeur</option>
                                        <option value="2">Administration</option>
                                    </select>
                                </div>
                            </div>

                            <p v-if="errors.mail" class="error_message" style="text-align: left">{{ error_messages.mail }}</p>

                        </div>

                    </div>
                </form>

                <div style="margin-top: 2vh; border-top: 1px solid rgba(255,255,255,0.2); padding-top: 2vh;">
                    <p style="font-size: 1.5vw; color: white; margin-bottom: 1vh;">Importer des professeurs depuis un CSV :</p>

                    <div class="sub_div_panel" style="display: flex; align-items: center; gap: 0.8vw;">
                        <input style="color: white; margin-top: 1vw;" type="file" accept=".csv,text/csv" @change="onCsvFileChange">
                    </div>

                    <div class="sub_div_panel" style="display: flex; align-items: center; gap: 0.5vw; margin-top: 1vh;">
                        <input id="csv-header" type="checkbox" v-model="csvHasHeader">
                        <label for="csv-header" style="color: white; font-size: 0.9vw;">Le CSV contient une ligne d'entete</label>
                    </div>

                    <div v-if="csvColumnOptions.length > 0" style="margin-top: 1.2vh;">
                        <p style="font-size: 1.5vw; color: white; margin-bottom: 2vh;">Associer les colonnes :</p>

                        <div class="sub_div_panel" style="display: flex; align-items: center; gap: 1vw;">
                            <label style="color: white; width: 9vw;">Nom</label>
                            <select class="input" v-model.number="csvMapping.lastname" style="height: 3vh; width: 14vw; font-size: 1.4vh;">
                                <option v-for="option in csvColumnOptions" :key="`lastname-${option.value}`" :value="option.value">{{ option.label }}</option>

                            </select>
                        </div>

                        <div class="sub_div_panel" style="display: flex; align-items: center; gap: 1vw;">
                            <label style="color: white; width: 9vw;">Prenom</label>
                            <select class="input" v-model.number="csvMapping.firstname" style="height: 3vh; width: 14vw; font-size: 1.4vh;">
                                <option v-for="option in csvColumnOptions" :key="`firstname-${option.value}`" :value="option.value">{{ option.label }}</option>
                            </select>
                        </div>

                        <div class="sub_div_panel" style="display: flex; align-items: center; gap: 1vw;">
                            <label style="color: white; width: 9vw;">Adresse mail</label>
                            <select class="input" v-model.number="csvMapping.mail" style="height: 3vh; width: 14vw; font-size: 1.4vh;">
                                <option v-for="option in csvColumnOptions" :key="`mail-${option.value}`" :value="option.value">{{ option.label }}</option>
                            </select>
                        </div>

                        <div v-if="csvPreviewRows.length > 0" style="margin-top: 1vh; color: white; font-size: 0.85vw;">
                            <p style="margin-bottom: 0.4vh;">Apercu (5 lignes max) :</p>
                            <div v-for="(row, index) in csvPreviewRows" :key="`preview-${index}`" style="opacity: 0.9;">
                                {{ row.join(' | ') }}
                            </div>
                        </div>
                    </div>

                    <div style="display: flex; align-items: center; gap: 1vw; margin-top: 1.2vh;">
                        <input
                            class="btn1"
                            type="button"
                            :value="csvIsImporting ? 'Import en cours...' : 'Importer CSV'"
                            :disabled="csvIsImporting"
                            @click="importTeachersFromCsv"
                        />
                        <span style="color: white; font-size: 0.9vw;" v-if="csvImportResult.success || csvImportResult.failed">
                            Import termine: {{ csvImportResult.success }} ajoute(s), {{ csvImportResult.failed }} echec(s)
                        </span>
                    </div>

                    <p v-if="csvErrorMessage" class="error_message" style="text-align: left; margin-top: 0.8vh;">{{ csvErrorMessage }}</p>
                </div>
            </div>

            <div class="container-fluid" style="align-items: start;">
                <div id="form_resources" style="width: 50%;">
                    <div class="container-fluid spb" style="font-size: 1.5vw; color: var(--main-theme-secondary-color);">
                        <p v-if="teachers.length > 0">Professeurs enregistrés : </p>
                        <p v-else>Aucun professeurs n'a été enregistré</p>

                        <div style="display: flex; align-items: center">
                            <div v-show="show_popup" id="popup">
                                Vous pouvez modifier le statut/droit d'accès d'un utilisateur en cliquant sur le bouton modifier.
                            </div>
                            <p v-if="status" class="btn_how_to" @click="toggleShowPopUp">ⓘ</p>
                        </div>
                    </div>

                    <div v-for="teacher in teachers" :key="teacher.idUser" style="color: white" >
                        <a class="accordion_teacher" id="dark_bar">{{teacher.user.firstname}} {{teacher.user.lastname}}</a>

                        <div class="panel">
                            <div class="hours_grid" style="gap: 1vw">
                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Nom : </p>
                                    <p>{{teacher.user.lastname}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Prenom : </p>
                                    <p>{{teacher.user.firstname}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Identifiant : </p>
                                    <p>{{teacher.user.username}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Mail : </p>
                                    <p>{{teacher.user.mail}}</p>
                                </div>
                            </div>
                            <div style="background-color: transparent; display: flex; padding: 0; margin-top: 0; margin-bottom: 1vh; justify-content: center; align-items: center">
                                <input class="btn1" type="button" value="Supprimer" v-on:click="deleteTeacher(teacher.idUser)"/>
                                <input class="btn1" type="button" value="Modifier" v-on:click="is_modifying = true; display_more_area = true; modify(teacher)" />
                            </div>
                        </div>
                    </div>
                </div>

                <div id="form_resources" style="width: 50%;">
                    <div class="container-fluid spb" style="font-size: 1.5vw; color: var(--main-theme-secondary-color);">
                        <p v-if="administrations.length > 0">Utilisateurs administration enregistrés : </p>
                        <p v-else>Aucun utilisateur n'a été enregistré</p>

                        <div style="display: flex; align-items: center">
                            <div v-show="show_popup" id="popup">
                                Vous pouvez modifier le statut/droit d'accès d'un utilisateur en cliquant sur le bouton modifier.
                            </div>
                            <p v-if="status" class="btn_how_to" @click="toggleShowPopUp">ⓘ</p>
                        </div>
                    </div>

                    <div v-for="adminUser in administrations" :key="adminUser.idUser" style="color: white" >
                        <a class="accordion_teacher" id="dark_bar">{{adminUser.user.firstname}} {{adminUser.user.lastname}}</a>

                        <div class="panel">
                            <div class="hours_grid" style="gap: 1vw">
                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Nom : </p>
                                    <p>{{adminUser.user.lastname}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Prenom : </p>
                                    <p>{{adminUser.user.firstname}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Identifiant : </p>
                                    <p>{{adminUser.user.username}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Mail : </p>
                                    <p>{{adminUser.user.mail}}</p>
                                </div>
                            </div>
                            <div style="background-color: transparent; display: flex; padding: 0; margin-top: 0; margin-bottom: 1vh; justify-content: center; align-items: center">
                                <input class="btn1" type="button" value="Supprimer" v-on:click="deleteTeacher(adminUser.idUser)"/>
                                <input class="btn1" type="button" value="Modifier" v-on:click="is_modifying = true; display_more_area = true; modify(adminUser)" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<style scoped>
</style>
