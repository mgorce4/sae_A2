<script setup>

import { computed, nextTick, onMounted, ref } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import { status } from '@/main.js'
import { getIdFromToken, getIdInstitutionFromToken, getInstitutionNameFromToken, getInstitutionLocationFromToken } from '@/utils/jwt.js'

const teacher_acces_right = 1

let display_more_area = ref(false)
let is_modifying = ref(false)

let title = ref("")

const teacher_name = ref("")
const teacher_firstname = ref("")
const teacher_mail = ref("")
const teacher_id = ref(0)

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
        const acc = document.getElementsByClassName('accordion_teacher')
        for (let i = 0; i < acc.length; i++) {

            if (acc[i].getAttribute('data-accordion') === 'add-modify-teacher') {
                acc[i].addEventListener('click', function () {
                    this.classList.toggle('active')
                    const panel = this.nextElementSibling
                    if (panel.style.maxHeight) {
                        panel.style.maxHeight = null
                    } else {
                        panel.style.maxHeight = panel.scrollHeight + 'vw'
                        panel.style.padding = '0 18px'
                    }
                })
            } else {
                const newElement = acc[i].cloneNode(true)
                acc[i].parentNode.replaceChild(newElement, acc[i])

                newElement.addEventListener('click', function () {
                    this.classList.toggle('active')
                    const panel = this.nextElementSibling
                    if (panel.style.maxHeight) {
                        panel.style.maxHeight = null
                    } else {
                        // Calculate the actual height including error messages
                        panel.style.maxHeight = panel.scrollHeight + 'vw'
                        panel.style.padding = '0 18px'
                    }
                })
            }
        }
    })
}

onMounted(async () => {
    const response = await axios.get(`${API_BASE_URL}/api/access-rights`)
    teachers.value = response.data.filter((ar) => ar.accessRight === 1)
    teachers.value = teachers.value.filter((teacher) => teacher.user.institution.idInstitution === getIdInstitutionFromToken())

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
    const usedUsernames = new Set(
        teachers.value
            .map((entry) => entry?.user?.username)
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
                await createUserWithAccessRight(payload, teacher_acces_right)
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
    const base = baseRaw.toLowerCase().replace(/\s+/g, '') || 'user'
    let candidate = base
    let suffix = 1
    while (usedUsernames.has(candidate)) {
        candidate = `${base}${suffix}`
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
        // is user logged in
        if (!getIdFromToken()) {
            alert('Erreur : Veuillez vous reconnecter.')
            return
        }

        const usedUsernames = new Set(
            teachers.value
                .map((entry) => entry?.user?.username)
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
            await createUserWithAccessRight(payload, teacher_acces_right)
            [teacher_firstname, teacher_name, teacher_mail].forEach((r) => r.value = '')
            display_more_area.value = false
        } else {
            await axios.put(`${API_BASE_URL}/api/users/${teacher_id.value}`, payload)

            [teacher_firstname, teacher_name, teacher_mail].forEach((r) => r.value = '')
            display_more_area.value = false
            is_modifying.value = false
        }

        await reloadTeachers()
        attachAccordionListeners()

        console.log('professeur sauvegardée avec succès')
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
    teachers.value = response.data
        .filter((ar) => ar.accessRight === teacher_acces_right)
        .filter((teacher) => teacher.user.institution.idInstitution === getIdInstitutionFromToken())
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
        await axios.delete(`${API_BASE_URL}/api/access-rights/${teacher_acces_right}/${id}`)

        await reloadTeachers()

        attachAccordionListeners()
    } catch (error) {
        console.error('Erreur lors de la suppression', error)
    }
}


</script>

<template>
    <div id="main">
        <div id="return_arrow">
            <RouterLink v-if="status==='Administration'" id="back_arrow" to="/control-center">←</RouterLink>
            <RouterLink v-else-if="status==='Admin'" id="back_arrow" to="/admin-dashboard">←</RouterLink>
            <p>Retour</p>
        </div>

        <div class="background">
            <div id="form">
                <div id="header">
                    <p id="title">Ajouter un professeur</p>
                </div>

                <div id="dark_bar">
                    <p>Ajouter un professeur</p>
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
                                <input type="text" class="input" v-model="teacher_name">
                                <input style="margin-left: 11.5vw" class="btn1" type="reset" value="Annuler" v-on:click="display_more_area = !display_more_area" />
                            </div>

                            <p v-if="errors.name" class="error_message" style="text-align: left">{{ error_messages.name }}</p>

                            <div class="sub_div_panel">
                                <label style="font-size: 1vw;">Prenom : </label>
                                <input type="text" class="input" v-model="teacher_firstname">
                                <input style="margin-left: 10vw" id="save" class="btn1" type="button" value="Sauvegarder" v-on:click="save()" />
                            </div>

                            <p v-if="errors.firstname" class="error_message" style="text-align: left">{{ error_messages.firstname }}</p>

                            <div class="sub_div_panel">
                                <label style="font-size: 1vw;">Mail : </label>
                                <input type="text" class="input" style="width: 17vw;" v-model="teacher_mail">
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
                        <input id="csv-header-admin" type="checkbox" v-model="csvHasHeader">
                        <label for="csv-header-admin" style="color: white; font-size: 0.9vw;">Le CSV contient une ligne d'entete</label>
                    </div>

                    <div v-if="csvColumnOptions.length > 0" style="margin-top: 1.2vh;">
                        <p style="font-size: 1.5vw; color: white; margin-bottom: 2vh;">Associer les colonnes :</p>

                        <div class="sub_div_panel" style="display: flex; align-items: center; gap: 1vw;">
                            <label style="color: white; width: 9vw;">Nom: </label>
                            <select class="input" v-model.number="csvMapping.lastname" style="height: 3vh; width: 14vw; font-size: 1.4vh;">
                                <option v-for="option in csvColumnOptions" :key="`lastname-${option.value}`" :value="option.value">{{ option.label }}</option>
                            </select>
                        </div>

                        <div class="sub_div_panel" style="display: flex; align-items: center; gap: 1vw;">
                            <label style="color: white; width: 9vw;">Prenom: </label>
                            <select class="input" v-model.number="csvMapping.firstname" style="height: 3vh; width: 14vw; font-size: 1.4vh;">
                                <option v-for="option in csvColumnOptions" :key="`firstname-${option.value}`" :value="option.value">{{ option.label }}</option>
                            </select>
                        </div>

                        <div class="sub_div_panel" style="display: flex; align-items: center; gap: 1vw;">
                            <label style="color: white; width: 9vw;">Adresse mail: </label>
                            <select class="input" v-model.number="csvMapping.mail" style="height: 3vh; width: 14vw; font-size: 1.4vh;">
                                <option v-for="option in csvColumnOptions" :key="`mail-${option.value}`" :value="option.value">{{ option.label }}</option>
                            </select>
                        </div>

                        <div v-if="csvPreviewRows.length > 0" style="margin-top: 1vh; color: white; font-size: 0.85vw;">
                            <p style="margin-bottom: 0.4vh;">Apercu (5 lignes) :</p>
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

            <div id="form_resources">
                <p v-if="teachers.length > 0">Professeurs enregistrés : </p>
                <p v-else>Aucun professeurs n'a été enregistré</p>

                <div v-for="teacher in teachers" :key="teacher.idUser" style="color: white" >
                    <a class="accordion_teacher" id="dark_bar">{{teacher.user.firstname}} {{teacher.user.lastname}}</a>

                    <div class="panel">
                        <div class="hours_grid" style="gap: 1vw">
                            <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                <p>Nom : </p>
                                <p>{{teacher.user.firstname}}</p>
                            </div>

                            <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                <p>Prenom : </p>
                                <p>{{teacher.user.lastname}}</p>
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

        </div>
    </div>
</template>

<style>

.background {
    height: auto;
    background-color: var(--main-theme-background-color);
    border-radius: 15px;
    overflow-x: hidden;
    overflow-y: hidden;
    box-sizing: border-box;
    padding-bottom: 1vw;
}

#header {
    background-color: var(--main-theme-secondary-background-color);
    height: auto;
    border-radius: 10px;
    margin: 1vw;
    display: flex;
    justify-content: center;
    align-items: center;
}

.accordion_teacher,
#dark_bar > p {
    font-weight: lighter;
    font-size: 1.05vw;
}

.accordion_teacher {
    cursor: pointer;
    position: relative;
}

.accordion_teacher::after {
    content: '^';
    position: absolute;
    right: 1vw;
    transition: transform 0.3s ease;
    font-size: 0.9vw;
}

.accordion_teacher.active::after {
    transform: rotate(180deg);
}

.sub_div_panel {
    gap: 10px;
    margin-bottom: 0.7vw;
}
</style>
