<script setup>
    import { onMounted, ref } from 'vue'
    import axios from 'axios'
    import { status } from '../main'
    
    status.value = "Administration"

    let display_more_area = ref(false)

    let added_values = ref([])
    
    onMounted(async () => {
        axios.get('http://localhost:8080/api/').then(response => (added_values.value = response.data))
    })
</script>

<template>
    <div id="form_mccc_sae"> 
        <div class="return_arrow" onclick="document.location.href='#/mccc-select-form'">
            <button class="back_arrow">←</button>
            <p>Retour</p>
        </div>
        <div class="background_form_mccc">
            <div class="form">
                <div class="header_form_mccc">
                    <p class="title">Situation d'Apprentissage Évaluée</p>
                </div>
                <div class="dark_bar">
                    <p>Ajouter une SAÉ</p>
                    <button class="button_more" v-on:click="display_more_area = true">+</button>
                </div>
                <form v-show="display_more_area" method="post" v-on:submit.prevent="">
                </form>
                <div v-for="(value, index) in added_values" v-bind:key="index" class="added_content_mccc">
                    <div class="dark_bar">
                        <p></p>
                    </div>
                    <div class="panel_form_mccc">
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
</style>