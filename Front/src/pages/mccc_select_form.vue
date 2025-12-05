<script setup>
import { ref } from 'vue'
const afficherBoutons = ref([
    [{ show: false, status: 'Remplit' }, { show: false, status: 'Vierge' }],
    [{ show: false, status: 'Vierge' }, { show: false, status: 'Brouillon' }],
    [{ show: false, status: 'Brouillon' }, { show: false, status: 'Vierge' }]
])
</script>

<template>
    <div id="form-select-page">
        <div style="display: flex; align-items: center;">
            <a href="/" id="back" class="btn1">←</a>
            <p class="back">Retour à l'accueil</p>
        </div>

        <div v-for="(year, index) in afficherBoutons" v-bind:key="index" class="blue-rect">
            <p class="semester-display">Année {{ index + 1 }} :</p>

            <div class="container-fluid spe" style="align-items: normal;">
                <div class="semester-rect" v-for="(btn, index2) in year" v-bind:key="index2" v-on:mouseover="btn.show = true" v-on:mouseout="btn.show = false">
                    <p class="semester-display">Semestre {{ 2 * index + index2 + 1 }}</p>
                    <p class="status-display" v-show="!btn.show">{{ btn.status }}</p>
                    <div v-show="btn.show" class="container-fluid spe">
                        <a class="btn-form-acces" href="/">UE</a>
                        <a class="btn-form-acces" href="/">Ressource</a>
                        <a class="btn-form-acces" href="/">SAÉ</a>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<style>
#form-select-page {
    width: 60vw;
    height: 100%;
    margin: 5vw 20vw;
    padding: 0;
    color: #000000;
}

.back {
    margin: 0 1vw;
    padding: 0;
    font-size: 2vw;
    font-weight: bold;
    color: #000000;
}

.blue-rect {
    width: 90%;
    height: fit-content;
    margin: 2vw 0;
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
    font-size: 2vw;
    font-weight: bold;
}

.status-display {
    text-align: right;
    margin: 0 5% 0 0;
    font-size: 1.5vw;
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