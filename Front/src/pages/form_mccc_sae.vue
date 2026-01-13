<script setup>
    import { onMounted, ref, computed, nextTick } from 'vue'
    import axios from 'axios'
    import { status } from '../main'
    
    status.value = "Administration"

    let display_more_area = ref(false)
    let display_add_modify_area = ref(false)
    let display_add_ue = ref(false)

    const addModifySaeTitle = ref("Bob")
    const addModifySdSaeModified = ref(null)
    const addModifySaeLabel = ref('')
    const addModifySaeApogeeCode = ref('')
    const addModifySaeHours = ref('')
    const total_hours = ref('')
    const addModifyUeLabel = ref(null)
    const addModifyUeCoefficient = ref(null)
    const addModifyUeCoef = ref([])
    const saeModalite = ref('')
    const checkboxStatus = ref(false)

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

    const attachAccordionListeners = () => {
        nextTick(() => {
            const acc = document.getElementsByClassName("accordion_mccc");
            for (let i = 0; i < acc.length; i++) {
                const newElement = acc[i].cloneNode(true);
                acc[i].parentNode.replaceChild(newElement, acc[i]);

                newElement.addEventListener("click", function() {
                    this.classList.toggle("active");
                    const panel = this.nextElementSibling;
                    if (panel.style.maxHeight) {
                        panel.style.maxHeight = null;
                    } else {
                        // Calculate the actual height including error messages
                        panel.style.maxHeight = panel.scrollHeight + "vw";
                        panel.style.padding = "0vw 0vw 0vw";
                    }
                });
            }
        });
    }

    const attachWorkStudyListeners = () => {
        const workStudySlider = document.getElementById('work_study_slider')
        if (workStudySlider) {
            workStudySlider.addEventListener('click', () => {
                const inputs = document.querySelectorAll('.input_work_study')
                const checkbox = document.querySelector('#work_study_slider input[type="checkbox"]')

                inputs.forEach((input) => {
                if (checkbox.checked) {
                    input.disabled = false
                } else {
                    input.disabled = true
                }
                })
            })
        }
    }

    // Prevent typing invalid characters in number inputs
    const preventInvalidChars = (event) => {
        const invalidChars = ['e', 'E', '+', '-', '.', ',']
        if (invalidChars.includes(event.key)) {
            event.preventDefault()
        }
    }

    onMounted(async () => {
        const response = await axios.get(`http://localhost:8080/api/v2/mccc/saes/institution/${localStorage.idInstitution}`)
        saeTableV2.value = response.data
        const responseUe = await axios.get(`http://localhost:8080/api/v2/mccc/ues/institution/${localStorage.idInstitution}`)
        ueTableV2.value = responseUe.data.sort((a, b) => a.label.localeCompare(b.label)) // Sort UEs in ascending order
        attachAccordionListeners()
        attachWorkStudyListeners()
    })

    function add_ue() {
        // Logic to add a new UE with its coefficient
        addModifyUeCoef.value.push({ ueLabel: addModifyUeLabel.value.label, ueName: addModifyUeLabel.value.name, coefficient: addModifyUeCoefficient.value })
        display_add_ue.value = false
        addModifyUeLabel.value = null
        addModifyUeCoefficient.value = null
    }

    function remove_ue(index) {
        // Logic to remove a UE from the list
        addModifyUeCoef.value.splice(index, 1)
    }

    function saveSae() {
        // Logic to save the new SAE
        display_more_area.value = false
        display_add_modify_area.value = false
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
        if (!addModifySaeLabel.value) {
            errors.value.saeLabel = true
            hasErrors = true
        }
        if (!addModifySaeApogeeCode.value) {
            errors.value.saeApogeeCode = true
            hasErrors = true
        }
        if (!addModifySaeHours.value || addModifySaeHours.value <= 0) {
            errors.value.saeHours = true
            hasErrors = true
        }
        if (addModifyUeCoef.value.length === 0) {
            errors.value.ueCoefficients = true
            hasErrors = true
        }

        // If data is missing, do not proceed
        if (hasErrors) {
            return
        }

        // ueCoefficients must contain a number for coefficient
        for (let index = 0; index < addModifyUeCoef.value.length; index++) {
            if (isNaN(addModifyUeCoef.value[index].coefficient) || addModifyUeCoef.value[index].coefficient <= 0) {
                errors.value.ueCoefficients = true
                return
            }
        }

        try {
            const payload = {
                label: addModifySaeLabel.value,
                apogeeCode: addModifySaeApogeeCode.value,
                semester: semesterNumber.value,
                idInstitution: localStorage.idInstitution,
                hours: addModifySaeHours.value,
                ueCoefficients: addModifyUeCoef.value,
                modalite: saeModalite.value,
                userId: parseInt(localStorage.idUser)
            }
            console.log('Payload SAE to save:', payload)
            axios.post('http://localhost:8080/api/saes', payload)
                .then(async response => {
                    console.log('✅ SAE saved successfully:', response.data)
                    
                    // Clear input fields after saving
                    addModifySaeLabel.value = ''
                    addModifySaeApogeeCode.value = ''
                    addModifySaeHours.value = ''
                    addModifyUeCoef.value = []
                    
                    // Close the form
                    display_more_area.value = false
                    display_add_modify_area.value = false
                    display_add_ue.value = false
                    
                    // Refresh the SAE list from the database
                    const refreshResponse = await axios.get(`http://localhost:8080/api/v2/mccc/saes/institution/${localStorage.idInstitution}`)
                    saeTableV2.value = refreshResponse.data
                    
                    // Reattach listeners to the updated accordion elements
                    attachAccordionListeners()
                    
                    alert('SAE créée avec succès !')
                })
                .catch(error => {
                    console.error('❌ Error saving SAE:', error)
                    alert('Erreur lors de la sauvegarde de la SAE : ' + (error.response?.data?.message || error.message))
                })
        }
        catch (error) {
            console.error('❌ Erreur lors de la sauvegarde de la SAÉ :', error);
            alert('Une erreur est survenue lors de la sauvegarde')
        }
        
    }

    function initAddModifyArea() {
        display_add_modify_area.value = true

        nextTick(() => {
            // Uncheck the work study checkbox
            const checkbox = document.querySelector('#work_study_slider input[type="checkbox"]')
            if (checkbox) {
                checkbox.checked = checkboxStatus.value
            }
            
            // Open accordion after rendering
            const accordions = document.querySelectorAll('[data-accordion="add-modify-sae"]')
            accordions.forEach(accordion => {
                accordion.classList.add('active')
                const panel = accordion.nextElementSibling
                if (panel) {
                    panel.style.maxHeight = panel.scrollHeight + "vw"
                    panel.style.padding = "0vw 0vw 0vw"
                }
            })
        })
    }

    function addSae() {
        addModifySaeLabel.value = ''
        addModifySaeApogeeCode.value = ''
        addModifySaeHours.value = ''
        saeModalite.value = ''

        checkboxStatus.value = false

        addModifyUeCoef.value = []
        display_add_ue.value = false

        nextTick(() => {
            addModifySaeTitle.value = "Ajout d'une nouvelle SAÉ"
        })
        
        initAddModifyArea()
    }

    function modifySae(sae) {
        addModifySaeTitle.value = "Modification d'une SAÉ"

        addModifySdSaeModified.value = sae.saeId
        addModifySaeLabel.value = sae.label
        addModifySaeApogeeCode.value = sae.apogeeCode
        addModifySaeHours.value = sae.hours
        saeModalite.value = sae.modalite

        checkboxStatus.value = /*sae.blocReleaseHours >= 1 ||*/ false
        
        addModifyUeCoef.value = sae.ueCoefficients.map(ue => ({ ueLabel: ue.ueLabel, coefficient: ue.coefficient })) // take a copy of sae.ueCoefficients
        display_add_ue.value = false
        
        initAddModifyArea()
    }
</script>

<template>
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
                    <button class="button_more" v-on:click="addSae()">+</button>
                </div>

                <form v-show="display_add_modify_area" method="post" v-on:submit.prevent="">
                    <!--Area to add-->
                    <a class="dark_bar accordion_mccc" data-accordion="add-modify-sae">{{ addModifySaeTitle }}</a>
                    <div class="panel_form_mccc container-fluid">
                        <div class="left_side container-fluid cfh spa">
                            <div class="container-fluid spb">
                                <label for="label_sae">Nom de la SAÉ :</label>
                                <input class="mccc_input" id="label_sae" type="text" v-model="addModifySaeLabel" :placeholder="'...'" required>
                            </div>
                            <div class="container-fluid spb">
                                <label for="apogee_code_sae">Code Apogée :</label>
                                <input class="mccc_input" id="apogee_code_sae" type="text" v-model="addModifySaeApogeeCode" :placeholder="'...'" required>
                            </div>
                            <div class="container-fluid spb">
                                <label for="hours_sae">Nombre d'heures (formation initiale) :</label>
                                <input class="mccc_input" id="hours_sae" type="number" v-model="addModifySaeHours" :placeholder="'...'" @keydown="preventInvalidChars" required>
                            </div>
                            <div class="container-fluid spb">
                                <label for="modalite_sae">Modalité :</label>
                                <input class="mccc_input" id="modalite_sae" type="text" v-model="saeModalite" :placeholder="'...'" required>
                            </div>

                            <div class="container-fluid spa">
                                <input class="btn1" type="reset" value="Annuler" @click="display_add_modify_area = false; display_add_ue = false">
                                <input class="btn1" type="submit" value="Sauvegarder" @click="saveSae">
                            </div>
                        </div>
                        <div class="right_side" style="">
                            <div id="work_study" style="padding: 1vw;">
                                <div class="component">
                                    <label class="switch" id="work_study_slider">
                                        <input type="checkbox"/>
                                        <span class="slider"></span>
                                    </label>
                                    <p>Alternance</p>
                                </div>
                                <div class="container-fluid spb" id="work_study_hours">
                                    <p>Nombre d'heures (alternance) : </p>
                                    <input type="text" class="input input_work_study" v-model="total_hours" required disabled/> 
                                </div>
                                <!--V2: put comparator with the programme national hour and total alternance -->
                            </div>
                        
                            <table class="ueCoefficient">
                                <tr>
                                    <td>U.E. affectée(s) : </td>
                                    <th class="display_coef_label" v-for="(labelUe, indexLabelUe) in addModifyUeCoef" v-bind:key="indexLabelUe">{{ labelUe.ueLabel }}</th>
                                    <td v-show="display_add_modify_area && !display_add_ue">
                                        <p class="button_more button_ue" @click="display_add_ue = true">+</p>
                                    </td>
                                    <th v-show="display_add_modify_area && display_add_ue">
                                        <select class="select_ue" v-model="addModifyUeLabel">
                                            <option v-for="(ue, index) in ueTableV2" v-bind:key="index" :value="ue">{{ ue.label }}</option>
                                        </select>
                                    </th>
                                </tr>
                                <tr>
                                    <td>Coefficient : </td>
                                    <td class="display_coef_ue" v-for="(coefUe, indexCoefUe) in addModifyUeCoef" v-bind:key="indexCoefUe">{{ coefUe.coefficient }}</td>
                                    <td v-show="display_add_modify_area && display_add_ue">
                                        <input class="display_coef_ue" type="number" :placeholder="'...'" v-model="addModifyUeCoefficient" @keydown="preventInvalidChars">
                                    </td>
                                </tr>
                                <tr>
                                    <td></td>
                                    <td v-for="(coefUe, index4) in addModifyUeCoef" v-bind:key="index4">
                                        <p class="button_more button_ue" @click="remove_ue(index4)">X</p>
                                    </td>
                                    <td v-show="display_add_modify_area && display_add_ue">
                                        <p class="button_more button_ue" @click="add_ue()">✓</p>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </form>
                
                <!-- Display existing SAEs for the semester -->
                <div v-for="(value, index) in filteredSaeTableV2" v-bind:key="index" class="added_content_mccc">
                    <a class="dark_bar accordion_mccc">{{ value.label }}</a>
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
    margin: 1vw 4vw 1vw 2vw;
}

.right_side {
    margin: 1vw;
    flex-wrap: wrap;
    margin: 1vw;
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

.mccc_input::placeholder, .display_coef_ue::placeholder {
    color: var(--main-theme-secondary-color);
    opacity: 0.7;
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