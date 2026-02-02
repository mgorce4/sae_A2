<script setup>

import { nextTick, onMounted, ref } from 'vue'
import axios from 'axios'

let display_more_area = ref(false)
let is_modifying = ref(false)

let title = ref("")

const teachers = ref([])

const attachAccordionListeners = () => {
    nextTick(() => {
        const acc = document.getElementsByClassName('accordion_teacher')
        console.log(acc.value)
        for (let i = 0; i < acc.length; i++) {

            if (acc[i].getAttribute('data-accordion') === 'add-modify-teacher') {
                acc[i].addEventListener('click', function () {
                    console.log(this)
                    console.log("1", this.classList)
                    this.classList.toggle('active')
                    console.log("2", this.classList)
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
    const response = await axios.get('http://localhost:8080/api/access-rights')
    teachers.value = response.data.filter((ar) => ar.accessRight === 1)

    await nextTick()
    attachAccordionListeners()
})

function addTeacher() {
    title.value = "Ajouter un professeur"
}

function modify() {
    title.value = "Modifier un professeur"
}

</script>

<template>
    <div id="main">
        <div id="return_arrow">
            <RouterLink id="back_arrow" to="/control-center">←</RouterLink>
            <p>Retour</p>
        </div>

        <div id="background">
            <div id="form">
                <div id="header">
                    <p id="title">Ajouter un professeur</p>
                </div>

                <div id="dark_bar">
                    <h2>Ajouter un professeur</h2>
                    <button id="button_more" v-on:click="display_more_area = !display_more_area;  addTeacher()">
                        {{ display_more_area ? '-' : '+' }}
                    </button>
                </div>

                <form v-show="display_more_area" method="post" v-on:submit.prevent="">
                    <a class="accordion_teacher dark_bar" data-accordion="add-modify-teacher">{{ title }}</a>

                    <div class="panel" style="display: flex">
                        <div style="margin-left: 15vw; padding-top: 1vw">
                            <div class="sub_div_panel">
                                <label>Nom : </label>
                                <input type="text" class="input">
                                <input style="margin-left: 11.5vw" class="btn1" type="reset" value="Annuler" v-on:click="display_more_area = !display_more_area" />
                            </div>

                            <div class="sub_div_panel">
                                <label>Prenom : </label>
                                <input type="text" class="input">
                                <input style="margin-left: 10vw" id="save" class="btn1" type="button" value="Sauvegarder" />
                            </div>
                        </div>

                    </div>
                </form>
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

                            <div style="background-color: transparent; display: flex; padding: 0; margin-bottom: 0; gap: 0.3vw; justify-content: center; align-items: center">
                                <input class="btn1" type="button" value="Spprimer"/>
                                <input class="btn1" type="button" value="Modifier" v-on:click="is_modifying = true; display_more_area = true; modify(teacher)" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<style scoped>

#background {
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
