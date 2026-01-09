<script setup>
    import { status } from '../main'
    import { computed, onMounted, ref, nextTick, watch} from 'vue'
    import axios from 'axios'
    
    status.value = "Administration"

    let display_more_area = ref(false)
    
    //the input fields for the function
    const nb_UE = ref('')
    const apogee_code = ref('')
    const name_comp = ref('')
    const comp_level = ref('')
    
    const ueList = ref([])

    const attachAccordionListeners = () => {
        nextTick(() => {
            const acc = document.getElementsByClassName("accordion_UE");
            for (let i = 0; i < acc.length; i++) {
                const newElement = acc[i].cloneNode(true);
                acc[i].parentNode.replaceChild(newElement, acc[i]);
                    
                newElement.addEventListener("click", function() {
                    this.classList.toggle("active");
                    const panel = this.nextElementSibling;
                    if (panel.style.maxHeight) {
                        panel.style.maxHeight = null;
                        panel.style.padding = "0 18px";
                    } else {
                        panel.style.maxHeight = panel.scrollHeight + "px";
                        panel.style.padding = "3px 18px 15px";
                    }
                });
            }
        });
    }
    
    const getQueryParam = (param) => {
        const hash = window.location.hash
        const queryString = hash.split('?')[1]
        if (!queryString) return null
            const params = new URLSearchParams(queryString)
        return params.get(param)
    }

    const getUEForSemester = computed(() => {
        return ueList.value.filter((ue) => { 
            return ue.semester == getQueryParam('id')
        })
    })

    // Watch for changes to reattach accordion listeners
    watch([ueList, display_more_area], () => {
        attachAccordionListeners();
    })

    onMounted(async () => {
        const response = await axios.get(`http://localhost:8080/api/v2/mccc/ues/institution/${localStorage.idInstitution}`)
        ueList.value = response.data
        attachAccordionListeners();
    })

    const save = async () => {
        try{
            await axios.post('http://localhost:8080/api/ues', {
                euApogeeCode: apogee_code.value,
                label: nb_UE.value,
                name: name_comp.value,
                competenceLevel: parseInt(comp_level.value),
                semester: parseInt(getQueryParam('id')),
                institutionId: parseInt(localStorage.idInstitution)
            });
            
            [nb_UE, apogee_code, name_comp, comp_level].forEach(f => f.value = ''); 
            display_more_area.value = false;
            
            await axios.get(`http://localhost:8080/api/v2/mccc/ues/institution/${localStorage.idInstitution}`).then(response => (ueList.value = response.data));
            attachAccordionListeners();
        }
        catch (error){
            console.error('Erreur lors de la sauvegarde', error);
        }
    }

    const del = async (id) =>{
        if (!confirm('Cette action est irréversible (pour le moment), continuer à vos risques et périls.')) {
            return;
        }
        try{
            await axios.delete(`http://localhost:8080/api/ues/${id}`);
            await axios.get(`http://localhost:8080/api/v2/mccc/ues/institution/${localStorage.idInstitution}`).then(response => (ueList.value = response.data));
        
            attachAccordionListeners();
        }
        catch (error){
            console.error('Erreur lors de la suppression', error);
        }
    }
</script>


<template>
    <div id="UE">
        <div id="return_arrow">
            <button id="back_arrow" onclick="document.location.href='#/mccc-select-form'">←</button>
            <p>Retour</p>
        </div>
        <div id="background_form_UE">
            <div id="form">
                <div id="header_UE">
                    <p id="title">Unités d’enseignement</p>
                </div>
                <div id="dark_bar">
                    <p>Ajouter une UE</p>
                    <button id="button_more" v-on:click="display_more_area = true">+</button>
                </div>
                <form v-show="display_more_area" method="post" v-on:submit.prevent="">
                    <a class="accordion_UE" id="dark_bar">Ajout d'une UE :</a>
                    <div class="panel_UE">
                        <div id="left">
                            <div>
                                <label>Numéro de l'UE :</label>
                                <input type="text" class="input" v-model="nb_UE" required/>
                            </div>
                            <div>
                                <label>Code apogee :</label>
                                <input type="text" class="input" v-model="apogee_code" required/>
                            </div>
                            <div>
                                <label>Intitulé de la compétence :</label>
                                <input type="text" class="input" v-model="name_comp" required/>
                            </div>
                            <div>
                                <label>Niveau de la compétence :</label>
                                <input type="text" class="input" v-model="comp_level" required/>
                            </div>
                        </div>

                        <div id="right">
                            <input id="btn_cancel_UE" class="btn1" type="reset" value="Annuler">
                            <input id="btn_save_UE" class="btn1" type="submit" value="Sauvegarder" @click="save">
                        </div>
                    </div>
                </form>

                <div v-for="ueACord in getUEForSemester" :key="ueACord.ueNumber">
                    <a class="accordion_UE" id="dark_bar">{{ueACord.label}} {{ueACord.name}}</a>
                    <div class="panel_UE">
                        <div id="left">
                            <div>
                                <p>Numéro de l'UE : </p>
                                <input type="text" class="input" :value="ueACord.label" />
                            </div>
                            <div>
                                <p>Code apogee : </p>
                                <input type="text" class="input" :value="ueACord.euApogeeCode" />
                            </div>
                            <div>
                                <p>Intitulé de la compétence : </p>
                                <input type="text" class="input" :value="ueACord.name" />
                            </div>
                            <div>
                                <p>Niveau de la compétence : </p>
                                <input type="text" class="input" :value="ueACord.competenceLevel" />
                            </div>
                        </div>

                        <div id="right">
                            <input id="btn_cancel_UE" class="btn1" type="reset" value="Supprimer" @click="del(ueACord.ueNumber)">
                            <input id="btn_save_UE" class="btn1" type="submit" value="Modifier" @click="save">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style>
#UE{
  margin: 3vw 14vw;
  justify-content: center;
}

#return_arrow{
    display: flex;
    align-items: center;
}

#return_arrow > p{
    font-size: 1.5vw;
    font-weight: bold;
    color: var(--main-theme-terciary-color);
    margin-left: 1.5vw;
}

#background_form_UE{
    height: auto;
    background-color: var(--main-theme-background-color);
    border-radius: 15px;
    overflow-x: hidden;
    overflow-y: hidden;
    box-sizing: border-box;
    padding-bottom: 1vw;
}

#header_UE{
    background-color: var(--main-theme-secondary-background-color);
    height: auto;
    border-radius: 10px;
    margin: 1vw;
    display: flex;
    justify-content: center;
    align-items: center;
}

#title{
    color: var(--main-theme-secondary-color);
    text-align: center;
    padding-top: 0.5vw;
    padding-bottom: 0.5vw;
    font-weight: lighter;
    font-size: 2.1vw;
    margin: 0;
}

.accordion_UE, #dark_bar >p{
    margin: 0vw;
    font-weight: lighter;
    font-size: 1.05vw;
}

.accordion_UE {
    cursor: pointer;
    position: relative;
}

.accordion_UE::after {
    content: '^';
    position: absolute;
    right: 1vw;
    transition: transform 0.3s ease;
    font-size: 0.9vw;
}

.accordion_UE.active::after {
    transform: rotate(180deg);
}

#dark_bar{
    color: var(--main-theme-secondary-color);
    height: auto;
    border-radius: 10px;
    margin: 1vw 0 0 0;
    padding: 1vw;
    background-color: var(--clickable-background-color);
    display: flex;
    justify-content: space-between;
    align-items: center;
}

#button_more{
    border: 2px solid var(--main-theme-secondary-color);
    background-color: var(--clickable-background-color);
    height: 2vw;
    width: 2vw;
    border-radius: 50%;
    font-size: 1.2vw;
    font-weight: bold;
    color: var(--main-theme-secondary-color);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: all 0.3s ease;
}

#button_more:hover {
  background-color: rgb(50,50,50);
  transform: scale(1.1);
}

.panel_UE {
  width: 90%;
  justify-self: center;
  background-color: rgba(0,0,0,0.35);
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.2s ease-out;
  border-bottom-left-radius: 15px;
  border-bottom-right-radius: 15px;
  color: var(--main-theme-secondary-color);
  margin-top: 0;
  display: flex;
}

.panel_UE > p {
  margin-top: 0;
  padding-top: 1vw;
}

</style>
