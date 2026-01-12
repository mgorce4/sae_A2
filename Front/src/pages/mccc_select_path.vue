<script setup>
import { ref } from 'vue';

const display_more_area = ref(false);

const navigateToDashboard = () => {
  document.location.href = '#/dashboard-administration';
};
</script>
<template>
  <div id="form_select_page">
    <div style="display: flex; align-items: center; height: 1vw;">
      <button class="back_arrow" @click="navigateToDashboard">←</button>
      <p class="back" >Retour à l'accueil</p>
    </div>
    <div id="background_path">
      <div class="form">
        <div id="header_UE">
          <p id="title">Parcours</p>
        </div>
        <div id="dark_bar">
          <p>Ajouter un parcours</p>
          <button id="button_more" v-on:click="display_more_area = true">+</button>
        </div>
        <div class="panel_UE">
          <div></div>
          <p>Contenu du formulaire d'ajout de parcours</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
#form_select_page {
  margin: 3vw 14vw;
  justify-content: center;
  color: var(--main-theme-tertiary-color);
}

.back {
  font-size: 1.5vw;
  font-weight: bold;
  color: var(--main-theme-tertiary-color);
  margin-left: 1.5vw;
}

#background_path{
  height: auto;
  background-color: var(--main-theme-background-color);
  border-radius: 15px;
  overflow-x: hidden;
  overflow-y: hidden;
  box-sizing: border-box;
  padding-bottom: 1vw;
  margin-top: 1vw;
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
</style>
