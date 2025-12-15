<script setup>
import { ref } from 'vue'

const afficherBoutons = ref([
    [{ show: false, status: 'Rempli' }, { show: false, status: 'Vierge' }],
    [{ show: false, status: 'Vierge' }, { show: false, status: 'Brouillon' }],
    [{ show: false, status: 'Brouillon' }, { show: false, status: 'Vierge' }]
])
</script>

<template>
    <div id="form-select-page">
        <div style="display: flex; align-items: center; height: 1vw;">
            <button id="backArrow" onclick="document.location.href='#/dashboard-administration'">←</button>
            <p class="back" >Retour à l'accueil</p>
        </div>

        <div v-for="(year, index) in afficherBoutons" v-bind:key="index" class="blue-rect">
            <p class="semester-display">Année {{ index + 1 }} :</p>

            <div class="container-fluid spe" style="align-items: normal;">
                <div class="semester-rect" v-for="(btn, index2) in year" v-bind:key="index2" v-on:mouseover="btn.show = true" v-on:mouseout="btn.show = false">
                    <p class="semester-display">Semestre {{ 2 * index + index2 + 1 }}</p>
                    <p class="status-display" v-show="!btn.show">{{ btn.status }}</p>
                    <div v-show="btn.show" class="container-fluid spe">
                        <button class="btn-form-acces" onclick="document.location.href='#/form-mccc-UE'">UE</button>
                        <button class="btn-form-acces">Ressource</button>
                        <button class="btn-form-acces">SAÉ</button>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<style>
#form-select-page {
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

#backArrow{
    font-size: 2vw;
    border: none;
    background-color: white;
    color: black;
    font-weight: bold;
}

.blue-rect {
    width: 90%;
    height: fit-content;
    margin: 2vw;
    padding: 1vw 2vw 2vw 2vw;
    border-radius: 1vw;
    background-color: #3D4375;
    color: #FFFFFF;
}

.semester-rect {
    width: 40%;
    height: fit-content;
    margin: 0 5% 6% 5%;
    padding: 1% 2% 3% 2%;
    border-radius: 1.5vw;
    background-color: #2C2C3B;
    color: #FFFFFF;
}

.semester-rect:hover {
    margin-bottom: 0;
}

.semester-display {
    margin: 0.5vw 0;
    padding: 0;
    text-align: left;
    font-size: 1.5vw;
    font-weight: bold;
}

.status-display {
    text-align: right;
    margin: 0 5% 0 0;
    font-size: 1.3vw;
}

.btn-form-acces {
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