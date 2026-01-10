<script setup>
    import { onMounted, ref, computed } from 'vue'
    import axios from 'axios'
    import { status } from '../main'
    
    status.value = "Administration"

    let display_more_area = ref(false)
    let display_modify_area = ref(false)
    let display_add_ue = ref(false)

    let saeLabel = ref('')
    let saeApogeeCode = ref('')
    let saeHours = ref('')
    let addUeLabel = ref(null)
    let addUeCoefficient = ref(0)
    let ueCoefficients = ref([])

    const errors = ref({
        saeLabel: false,
        saeApogeeCode: false,
        saeHours: false,
        ueCoefficients: false,
    })

    const saeTableV2 = ref([])
    const ueTableV2 = ref([])
    console.log("SAE TABLE V2 : ", saeTableV2)
    console.log("UE TABLE V2 : ", ueTableV2)

    /* Extract ID from hash URL parameters */
    const getQueryParam = (param) => {
    const hash = window.location.hash
    const queryString = hash.split('?')[1]
    if (!queryString) return null
    const params = new URLSearchParams(queryString)
    return params.get(param)
    }

    const semesterNumber = ref(getQueryParam('id'))
    console.log("semestre saé : ", semesterNumber.value)

    // Filter SAE data by semester
    const filteredSaeTableV2 = computed(() => {
        return saeTableV2.value.filter((sae) => {
            return sae.semester == semesterNumber.value
        })
    })
    onMounted(async () => {
        const response = await axios.get(`http://localhost:8080/api/v2/mccc/saes/institution/${localStorage.idInstitution}`)
        saeTableV2.value = response.data
        const responseUe = await axios.get(`http://localhost:8080/api/v2/mccc/ues/institution/${localStorage.idInstitution}`)
        ueTableV2.value = responseUe.data.sort((a, b) => a.label.localeCompare(b.label)) // Sort UEs in ascending order
    })

    function add_ue() {
        // Logic to add a new UE with its coefficient
        ueCoefficients.value.push({ ueLabel: addUeLabel.value.label, ueName: addUeLabel.value.name, coefficient: addUeCoefficient.value })
        display_add_ue.value = false
        addUeLabel.value = null
        addUeCoefficient.value = 0
    }

    function remove_ue(index) {
        // Logic to remove a UE from the list
        ueCoefficients.value.splice(index, 1)
    }

    function saveSae() {
        // Logic to save the new SAE
        display_more_area.value = false
        display_modify_area.value = false
        display_add_ue.value = false

        // Reset errors
        errors.value = {
            saeLabel: false,
            saeApogeeCode: false,
            saeHours: false,
            ueCoefficients: false,
        }

        // Validation before saving
        let hasErrors = false
        if (!saeLabel.value) {
            errors.value.saeLabel = true
            hasErrors = true
        }
        if (!saeApogeeCode.value) {
            errors.value.saeApogeeCode = true
            hasErrors = true
        }
        if (!saeHours.value || saeHours.value <= 0) {
            errors.value.saeHours = true
            hasErrors = true
        }
        if (ueCoefficients.value.length === 0) {
            errors.value.ueCoefficients = true
            hasErrors = true
        }

        // If data is missing, do not proceed
        if (hasErrors) {
            return
        }

        // ueCoefficients must contain a number for coefficient
        for (let index = 0; index < ueCoefficients.value.length; index++) {
            if (isNaN(ueCoefficients.value[index].coefficient) || ueCoefficients.value[index].coefficient <= 0) {
                errors.value.ueCoefficients = true
                return
            }
        }

        try {
            const payload = {
                label: saeLabel.value,
                apogeeCode: saeApogeeCode.value,
                semester: semesterNumber.value,
                idInstitution: localStorage.idInstitution,
                hours: saeHours.value,
                ueCoefficients: ueCoefficients.value,
            }
            console.log('payload completed')
            console.log('Payload SAE to save:', payload)
            /*
            axios.post('http://localhost:8080/api/saes', payload)
                .then(response => {
                    console.log('SAE saved successfully:', response.data)
                    // Optionally, refresh the SAE list or provide user feedback here
                })
                .catch(error => {
                    console.error('Error saving SAE:', error)
                })
            console.log('SAE saved successfully')*/
        }
        catch (error) {
            console.error('Erreur lors de la sauvegarde de la SAÉ :', error);
        }

        // Clear input fields after saving
        saeLabel.value = ''
        saeApogeeCode.value = ''
        saeHours.value = ''
        ueCoefficients.value = []
        
    }

    function saveModifiedSae() {
        // Logic to save the modified SAE
        display_more_area.value = false
        display_modify_area.value = false
        display_add_ue.value = false

        // Clear input fields after saving
        saeLabel.value = ''
        saeApogeeCode.value = ''
        saeHours.value = ''
        ueCoefficients.value = []        
    }

    function modifySae(sae) {
        saeLabel.value = sae.label
        saeApogeeCode.value = sae.apogeeCode
        saeHours.value = sae.hours
        ueCoefficients.value = sae.ueCoefficients
        display_modify_area.value = true
        display_add_ue.value = false
        display_more_area.value = false
    }
</script>

<template>
    <p>{{ saeTableV2 }}</p>
    <p>{{ ueTableV2 }}</p>
    <p>addUE : {{ ueCoefficients }}</p>
    <div id="form_mccc_sae"> 
        <div class="return_arrow">
            <button class="back_arrow" onclick="document.location.href='#/mccc-select-form'">←</button>
            <p>Retour</p>
        </div>
        <div class="background_form_mccc">
            <div class="form">

                <div class="header_form_mccc">
                    <p class="title">Situation d'Apprentissage Évaluée</p>
                </div>

                <div class="dark_bar">
                    <p>Ajouter une SAÉ</p>
                    <button class="button_more" v-on:click="display_more_area = true; display_add_ue = false; display_modify_area = false; ueCoefficients = []">+</button>
                </div>

                <form v-show="display_more_area" method="post" v-on:submit.prevent="">
                    <div class="dark_bar"><p>Ajout d'une nouvelle SAÉ</p></div>
                    <div class="panel_form_mccc container-fluid">
                        <div class="left_side container-fluid cfh spa">
                            <div class="container-fluid spb">
                                <label for="label_sae_modify">Nom de la SAÉ :</label>
                                <input class="mccc_input" id="label_sae_modify" type="text" v-model="saeLabel" :placeholder="'...'" required>
                            </div>
                            <div class="container-fluid spb">
                                <label for="apogee_code_sae_modify">Code Apogée :</label>
                                <input class="mccc_input" id="apogee_code_sae_modify" type="text" v-model="saeApogeeCode" :placeholder="'...'" required>
                            </div>
                            <div class="container-fluid spb">
                                <label for="hours_sae_modify">Nombre d'heures (formation initiale) :</label>
                                <input class="mccc_input" id="hours_sae_modify" type="number" v-model="saeHours" :placeholder="'...'" required>
                            </div>

                            <div class="container-fluid spa">
                                <input class="btn1" type="reset" value="Annuler" @click="display_more_area = false; display_add_ue = false">
                                <input class="btn1" type="submit" value="Sauvegarder" @click="saveSae">
                            </div>
                        </div>
                        <table class="ueCoefficient">
                            <tr>
                                <td>U.E. affectée(s) : </td>
                                <th class="display_coef_label" v-for="(labelUe, indexLabelUe) in ueCoefficients" v-bind:key="indexLabelUe">{{ labelUe.ueLabel }}</th>
                                <td v-show="display_more_area && !display_add_ue">
                                    <p class="button_more button_ue" @click="display_add_ue = true">+</p>
                                </td>
                                <th v-show="display_more_area && display_add_ue">
                                    <select class="select_ue" v-model="addUeLabel">
                                        <option v-for="(ue, index) in ueTableV2" v-bind:key="index" :value="ue">{{ ue.label }}</option>
                                    </select>
                                </th>
                            </tr>
                            <tr>
                                <td>Coefficient : </td>
                                <td class="display_coef_ue" v-for="(coefUe, indexCoefUe) in ueCoefficients" v-bind:key="indexCoefUe">{{ coefUe.coefficient }}</td>
                                <td v-show="display_more_area && display_add_ue">
                                    <input class="display_coef_ue" type="number" placeholder="..." v-model="addUeCoefficient">
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td v-for="(coefUe, index4) in ueCoefficients" v-bind:key="index4">
                                    <p class="button_more button_ue" @click="remove_ue(index4)">X</p>
                                </td>
                                <td v-show="display_more_area && display_add_ue">
                                    <p class="button_more button_ue" @click="add_ue()">✓</p>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>

                <form v-show="display_modify_area" method="post" v-on:submit.prevent="">
                    <div class="dark_bar"><p>Modification d'une SAÉ</p></div>
                    <div class="panel_form_mccc container-fluid">
                        <div class="left_side container-fluid cfh spa">
                            <div class="container-fluid spb">
                                <label for="label_sae_modify">Nom de la SAÉ :</label>
                                <input class="mccc_input" id="label_sae_modify" type="text" v-model="saeLabel" required>
                            </div>
                            <div class="container-fluid spb">
                                <label for="apogee_code_sae_modify">Code Apogée :</label>
                                <input class="mccc_input" id="apogee_code_sae_modify" type="text" v-model="saeApogeeCode" required>
                            </div>
                            <div class="container-fluid spb">
                                <label for="hours_sae_modify">Nombre d'heures (formation initiale) :</label>
                                <input class="mccc_input" id="hours_sae_modify" type="number" v-model="saeHours" required>
                            </div>

                            <div class="container-fluid spa">
                                <input class="btn1" type="reset" value="Annuler" @click="display_modify_area = false; display_add_ue = false">
                                <input class="btn1" type="submit" value="Sauvegarder" @click="saveModifiedSae">
                            </div>
                        </div>
                        <table class="ueCoefficient">
                            <tr>
                                <td>U.E. affectée(s) : </td>
                                <th class="display_coef_label" v-for="(labelUe, index2) in ueCoefficients" v-bind:key="index2">{{ labelUe.ueLabel }}</th>
                                <td v-show="display_modify_area && !display_add_ue" @click="display_add_ue = true">
                                    <p class="button_more button_ue">+</p>
                                </td>
                                <th class="display_coef_label" v-show="display_modify_area && display_add_ue">...</th>
                            </tr>
                            <tr>
                                <td>Coefficient : </td>
                                <td class="display_coef_ue" v-for="(coefUe, index3) in ueCoefficients" v-bind:key="index3">{{ coefUe.coefficient }}</td>
                                <td class="display_coef_ue" v-show="display_modify_area && display_add_ue">...</td>
                            </tr>
                            <tr>
                                <td></td>
                                <td v-for="(index4) in ueCoefficients" v-bind:key="index4">
                                    <p class="button_more button_ue">X</p>
                                </td>
                                <td v-show="display_modify_area && display_add_ue" @click="display_add_ue = false">
                                    <p class="button_more button_ue">✓</p>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>

                <div v-for="(value, index) in filteredSaeTableV2" v-bind:key="index" class="added_content_mccc">
                    <div class="dark_bar">
                        <p>{{ value.label }}</p>
                    </div>
                    <div class="panel_form_mccc container-fluid">
                        <div class="left_side container-fluid cfh spa">
                            <div class="container-fluid spb">
                                <label>Code apogee : </label>
                                <p class="mccc_input">{{ value.apogeeCode }}</p>
                            </div>
                            <div class="container-fluid spb">
                                <label>Nombre d'heures (formation initiale) : </label>
                                <p class="mccc_input">{{ value.hours }}</p>
                            </div>
                            <div class="container-fluid spa">
                                <input id="btn_cancel_UE" class="btn1" type="reset" value="Supprimer">
                                <input id="btn_save_UE" class="btn1" type="button" value="Modifier" @click="modifySae(value)">
                            </div>
                        </div>
                        <div  class="right_side">
                            <table class="ueCoefficient">
                                <tr>
                                    <td>U.E. affectée(s) : </td>
                                    <th class="display_coef_label" v-for="(labelUe, index2) in value.ueCoefficients" v-bind:key="index2">{{ labelUe.ueLabel }}</th>
                                </tr>
                                <tr>
                                    <td>Coefficient : </td>
                                    <td class="display_coef_ue" v-for="(coefUe, index3) in value.ueCoefficients" v-bind:key="index3">{{ coefUe.coefficient }}</td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>

<style>
#form_mccc_sae{
  margin: 3vw 14vw;
  justify-content: center;
}

.left_side {
    width: 45%;
    margin: 1vw 5vw 1vw 2vw;
}

.mccc_input {
    border: 0.1vw solid var(--main-theme-terciary-color);
    background-color: var(--div-rect-background-color);
    border-radius: 0.5vw;
    padding: 0.5vw;
    margin: 0.5vw 0;
    width: fit-content;
    min-width: 10vw;
    text-align: center;
    color: var(--main-theme-secondary-color);
}

.mccc_input::placeholder {
    color: var(--main-theme-secondary-color);
    opacity: 0.6;
}

.ueCoefficient {
    border-collapse: separate;
    border-spacing: 0.6vw;
    height: 3.5vw;
    text-align: center;
    align-items: center;
}

.display_coef_label, .display_coef_ue {
    width: 2vw;
    height: 1.25vw;
    border: 0.1vw solid var(--main-theme-terciary-color);
    background-color: var(--div-rect-background-color);
    padding: 0.5vw;
}

.display_coef_label {
    border-top-left-radius: 0.8vw;
    border-top-right-radius: 0.8vw;
}

.display_coef_ue {
    border-bottom-left-radius: 0.8vw;
    border-bottom-right-radius: 0.8vw;
}

.select_ue {
    height: 2vw;
    border: 0.1vw solid var(--main-theme-terciary-color);
    background-color: var(--div-rect-background-color);
    border-top-left-radius: 0.8vw;
    border-top-right-radius: 0.8vw;
    text-align: center;
    color: var(--main-theme-secondary-color);
}

.button_ue {
    width: 2vw !important;
    height: 2vw !important;
    margin: 0 auto;
}
</style>