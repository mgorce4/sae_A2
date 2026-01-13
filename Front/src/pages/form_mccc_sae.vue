<script setup>
    import { onMounted, ref, computed, nextTick } from 'vue'
    import axios from 'axios'
    import { status } from '../main'
    
    status.value = "Administration"

    let display_add_modify_area = ref(false)
    let display_add_ue = ref(false)

    const addModifySaeTitle = ref("Bob")
    const addModifySdSaeModified = ref(null)
    const addModifySaeLabel = ref('')
    const addModifySaeApogeeCode = ref('')
    const addModifySaeTermCode = ref('')
    const addModifySaeHours = ref('')
    const addModifySaeHoursAlternance = ref('')
    const ue_list = ref([{ id: 1, ue: '', coefficient: '' }])
    const total_hours = ref('')
    
    const checkboxStatus = ref(false)
    
    const errors = ref({
        label: false,
        apogeeCode: false,
        termCode: false,
        hours: false,
        ueCoefficients: false,
        alternanceHours: false
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

    // Filter UE data by semester
    const filteredUeTableV2 = computed(() => {
        return ueTableV2.value.filter((ue) => {
            return ue.semester == semesterNumber.value
        })
    })

    const attachAccordionListeners = () => {
        nextTick(() => {
            const acc = document.getElementsByClassName("accordion_mccc");
            for (let i = 0; i < acc.length; i++) {
                // Skip cloning for add-modify-sae to preserve Vue reactivity
                if (acc[i].getAttribute('data-accordion') === 'add-modify-sae') {
                    acc[i].addEventListener("click", function() {
                        this.classList.toggle("active");
                        const panel = this.nextElementSibling;
                        if (panel.style.maxHeight) {
                            panel.style.maxHeight = null;
                        } else {
                            panel.style.maxHeight = panel.scrollHeight + "vw";
                            panel.style.padding = "0vw 0vw 0vw";
                        }
                    });
                } else {
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
        ueTableV2.value = responseUe.data.sort((a, b) => a.label.localeCompare(b.label)) // Filter UEs by semester and sort in ascending order
        attachAccordionListeners()
        attachWorkStudyListeners()

        
        document.getElementById('save').addEventListener('click', () => {

            errors.value.ueCoefficients = false
            document.getElementById("error_ue").innerHTML = ""
        
            // Variabble for the messages
            let ues = document.querySelectorAll('#ue_select')
            let coefs = document.querySelectorAll('#coefficient')

            // add ues and coefs to the list
            for (let i = 0; i < ue_list.value.length; i++) {
            ue_list.value[i].ue = ues[i].value
            ue_list.value[i].coefficient = coefs[i].value
            }

            for (let i = 0; i < ue_list.value.length; i++) {
                if (ue_list.value[i].coefficient === '') {
                    errors.value.ueCoefficients = true
                    document.getElementById("error_ue").innerHTML = "Le coefficient de chaque UE est obligatoire"
                }
            }

            if (ue_list.value.length > 1) {
                let first_ue = ue_list.value[0].ue

                for (let i = 1; i < ue_list.value.length; i++) {
                    if (first_ue === ue_list.value[i].ue) {
                        errors.value.ueCoefficients = true
                        document.getElementById("error_ue").innerHTML = "Une resource ne peut pas être affectée plusieurs fois à la même UE"
                    }
                }
            }
            console.log("UE LIST : ", ue_list.value)
        })

        document.addEventListener('click', (event) => {
            if (event.target.id === 'button_ue_minus') {
                if (ue_list.value.length > 1) {
                    /* find all UE divs */
                    const ues = document.querySelectorAll('.ue_div')

                    /* find which div contains the clicked button */
                    let index_to_remove = -1
                    ues.forEach((div, index) => {
                    /* if the actual div is the target of the event the index is the index to remove */
                    if (div.contains(event.target)) {
                        index_to_remove = index
                    }
                    })

                    /* remove the ue from the array at the good index */
                    if (index_to_remove !== -1) {
                    ue_list.value = ue_list.value.filter((_, index) => index !== index_to_remove)
                    }
                }

            } else if (event.target.id === 'button_ue_plus' && filteredUeTableV2.value.length > ue_list.value.length) {
                /* generate new unique id */
                let id
                if (ue_list.value.length > 0) {
                    /* get the max id and add 1 */
                    id = Math.max(...ue_list.value.map(u => u.id)) + 1
                } else {
                    /* else it's the first id */
                    id = 1
                }
                ue_list.value.push({ id: id, ue: '', coefficient: '' })

            }
        })
    })

    function saveSae() {
        // Reset errors
        errors.value.label = false
        errors.value.apogeeCode = false
        errors.value.termCode = false
        errors.value.hours = false
        errors.value.ueCoefficients = false
        errors.value.alternanceHours = false
        /*document.getElementById('error_ue_coefficients').innerHTML = ''*/

        // Validation before saving
        let hasErrors = false
        if (!addModifySaeLabel.value) {
            errors.value.label = true
        }
        if (!addModifySaeApogeeCode.value) {
            errors.value.apogeeCode = true
        }
        if (addModifySaeTermCode.value === '') {
            errors.value.termCode = true
        }
        if (addModifySaeHours.value == '' || addModifySaeHours.value <= 0) {
            errors.value.hours = true
        }
        if (document.getElementById('work_study_slider').querySelector('input[type="checkbox"]').checked) {
            if (addModifySaeHoursAlternance.value == '' || addModifySaeHoursAlternance.value <= 0) {
                errors.value.alternanceHours = true
            }
        }/*
        if (addModifyUeCoef.value.length === 0) {
            document.getElementById('error_ue_coefficients').innerHTML = "Les coefficients des UE sont obligatoires"
            hasErrors = true
        }*/
        // If data is missing, do not proceed
        hasErrors = errors.value.label || errors.value.apogeeCode || errors.value.termCode || errors.value.hours || errors.value.alternanceHours
        if (hasErrors) {
            return
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
        // Reset errors
        errors.value.label = false
        errors.value.apogeeCode = false
        errors.value.termCode = false
        errors.value.hours = false
        errors.value.ueCoefficients = false
        errors.value.alternanceHours = false

        addModifySaeLabel.value = ''
        addModifySaeApogeeCode.value = ''
        addModifySaeHours.value = ''
        addModifySaeTermCode.value = ''

        checkboxStatus.value = false

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
        addModifySaeTermCode.value = sae.termsCode

        checkboxStatus.value = /*sae.blocReleaseHours >= 1 ||*/ false
        
        display_add_ue.value = false
        
        initAddModifyArea()
    }
</script>

<template>
    <p>{{ filteredUeTableV2 }}</p>
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
                                <input class="mccc_input" name="label_sae" type="text" v-model="addModifySaeLabel" :placeholder="'...'">
                            </div>
                            <p class="error_message" v-show="errors.label">Le nom de la SAÉ est obligatoire</p>
                            <div class="container-fluid spb">
                                <label for="apogee_code_sae">Code Apogée :</label>
                                <input class="mccc_input" name="apogee_code_sae" type="text" v-model="addModifySaeApogeeCode" :placeholder="'...'">
                            </div>
                            <p class="error_message" v-show="errors.apogeeCode">Le code Apogée est obligatoire</p>
                            <div class="container-fluid spb">
                                <label for="hours_sae">Nombre d'heures (formation initiale) :</label>
                                <input class="mccc_input" name="hours_sae" type="number" v-model="addModifySaeHours" :placeholder="'...'" @keydown="preventInvalidChars">
                            </div>
                            <p class="error_message" v-show="errors.hours">Le nombre d'heures est obligatoire et doit être supérieur à 0</p>
                            <div class="container-fluid spb">
                                <label for="modalite_sae">Modalité :</label>
                                <input class="mccc_input" name="modalite_sae" type="text" v-model="addModifySaeTermCode" :placeholder="'...'">
                            </div>
                            <p class="error_message" v-show="errors.termCode">La modalité est obligatoire</p>

                            <div class="container-fluid spa">
                                <input class="btn1" type="reset" value="Annuler" @click="display_add_modify_area = false; display_add_ue = false">
                                <input id="save" class="btn1" type="submit" value="Sauvegarder" @click="saveSae">
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
                                    <input type="number" class="input input_work_study" v-model="total_hours" @keydown="preventInvalidChars" disabled/> 
                                </div>
                                <p class="error_message" v-show="errors.alternanceHours">Vous devez saisir un nombre d'heures valide, <br>ou désélectionner les heures en alternance</p>
                                <!--V2: put comparator with the programme national hour and total alternance -->
                            </div>
                            
                            <div>
                                <div class="component" style="justify-content: center;">
                                    <label for="ue_select">UE affectées : </label>

                                    <button class="button_more" id="button_ue_plus">+</button>

                                    <label for="coefficient" class="component" style="margin-top: 7px">Coefficient :</label>
                                </div>
                                <p v-if="ue_list.length <= 0">Aucune UE créée</p>
                                <div v-else>
                                    <div v-for="ue in ue_list" :key="ue.id" class="component ue_div" style="margin-bottom: 1vw; margin-left: 5.9vw;">
                                        <select id="ue_select" class="input">
                                            <option v-for="ue in filteredUeTableV2" :key="ue.ueNumber">
                                            {{ ue.label }}
                                            </option>
                                        </select>
                                        <button class="button_more" id="button_ue_minus">x</button>
                                        <input id="coefficient" type="text" class="input" style="margin-top: 4px" v-model="ue.coefficient"/>
                                    </div>
                                </div>
                                <p id="error_ue" class="error_message"></p>
                            </div>

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
    height: 1vw;
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