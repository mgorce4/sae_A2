<script setup>
import { ref } from 'vue'

const afficherBoutons = ref([
    [{ show: false, status: 'Rempli' }, { show: false, status: 'Vierge' }],
    [{ show: false, status: 'Vierge' }, { show: false, status: 'Brouillon' }],
    [{ show: false, status: 'Brouillon' }, { show: false, status: 'Vierge' }]
])
</script>

<template>
    <div id="form_select_page">
        <div style="display: flex; align-items: center; height: 1vw;">
            <button id="back_arrow" onclick="document.location.href='#/dashboard-administration'">←</button>
            <p class="back" >Retour à l'accueil</p>
        </div>

        <div v-for="(year, index) in afficherBoutons" v-bind:key="index" class="blue_rect">
            <p class="semester_display">Année {{ index + 1 }} :</p>

            <div class="container-fluid spe" style="align-items: normal;">
                <div class="semester_rect" v-for="(btn, index2) in year" v-bind:key="index2" v-on:mouseover="btn.show = true" v-on:mouseout="btn.show = false">
                    <p class="semester_display">Semestre {{ 2 * index + index2 + 1 }}</p>
                    <p class="status_display" v-show="!btn.show">{{ btn.status }}</p>
                    <div v-show="btn.show" class="container-fluid spe">
                        <button class="btn_form_acces" onclick="document.location.href='#/form-mccc-UE'">UE</button>
                        <button class="btn_form_acces">Ressource</button>
                        <button class="btn_form_acces">SAÉ</button>
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
    color: #000000;
}

.back {
    font-size: 1.5vw;
    font-weight: bold;
    color: #000000;
    margin-left: 1.5vw;
}

#back_arrow{
    font-size: 2vw;
    border: none;
    background-color: white;
    color: black;
    font-weight: bold;
}

.blue_rect {
    width: 90%;
    height: fit-content;
    margin: 2vw;
    padding: 1vw 2vw 2vw 2vw;
    border-radius: 1vw;
    background-color: #3D4375;
    color: #FFFFFF;
}

.semester_rect {
    width: 40%;
    height: fit-content;
    margin: 0 5% 6% 5%;
    padding: 1% 2% 3% 2%;
    border-radius: 1.5vw;
    background-color: #2C2C3B;
    color: #FFFFFF;
}

.semester_rect:hover {
    margin-bottom: 0;
}

.semester_display {
    margin: 0.5vw 0;
    padding: 0;
    text-align: left;
    font-size: 1.5vw;
    font-weight: bold;
}

.status_display {
    text-align: right;
    margin: 0 5% 0 0;
    font-size: 1.3vw;
}

.btn_form_acces {
    width: 25%;
    height: fit-content;
    margin: 10% 0 0 0;
    padding: 2% 0;
    border-radius: 0.5vw;
    border-width: 0;
    background-color: #242222;
    color: #FFFFFF;
    text-decoration: none;
    text-align: center;
    font-size: 0.75vw;
}
</style>