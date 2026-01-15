<script setup>
  import { onMounted, nextTick } from 'vue';
  
  const attachAccordionListeners = () => {
      nextTick(() => {
          const acc = document.querySelectorAll(".popup_administration .accordion_popup");
          acc.forEach((element) => {
              element.addEventListener("click", function() {
                  this.classList.toggle("active");
                  const panel = this.nextElementSibling;
                  if (panel.style.maxHeight) {
                      panel.style.maxHeight = null;
                      panel.style.padding = "0";
                  } else {
                      // Calculate the actual height including error messages
                      panel.style.height = "auto";
                      panel.style.maxHeight = panel.scrollHeight + "vw";
                      panel.style.padding = "1vw";
                  }
              });
          });
      });
  }
  
  onMounted(() => {
      attachAccordionListeners();
  });
</script>

<template>
  <p class="popup_main_title">Guide utilisateur : </p>
  <div class="popup_administration container-fluid cfh">
    <div>
      <a class="dark_bar accordion_popup">Gestion des MCCCs</a>
      <div class="panel_popup">
        <div class="container-fluid">
          <button type="button" id="MCCC_button" style="width: 10vw; height: 5vh; font-size: 1vw;">MCCC</button>
          <p>En cliquant sur ce bouton, vous accèderez à une page où sélectionner le parcours</p>
        </div>
        <p class="popup_sub_title">Parcours :</p>
        <div class="container-fluid">
          <button class="button_more">+</button>
          <p>
            En cliquant sur ce bouton, une zone d'ajout de parcours apparaîtra.<br>
            Il vous suffira de renseigner le nom du parcours et le nombre associé, puis de cliquer sur "Ajouter" pour créer un nouveau parcours.<br>
          </p>
        </div>
        <div class="container-fluid">
          <button class="btn_modify">Modifier</button>
          <p>
            En cliquant sur ce bouton, une zone de modification du parcours apparaîtra.<br>
            Il vous suffira de renseigner le nouveau nom du parcours et le nouveau nombre associé, <br>
            puis de cliquer sur <span class="btn_modify" style="height: 2vh; padding: 0.7vw; margin: 0; display: inline-block;">Sauvegarder</span> pour appliquer les changements au parcours sélectionné.<br>
          </p>
        </div>
        <p class="popup_sub_title">Page de sélection des formulaires : </p>
        <div class="container-fluid">
          <div class="semester_rect">
            <p class="semester_display">Semestre 1</p>
            <div class="container-fluid spe">
              <button class="btn_form_acces">UE</button>
              <button class="btn_form_acces">Ressource</button>
              <button class="btn_form_acces">SAÉ</button>
            </div>
          </div>
          <p>
            Vous pouvez accéder aux formulaires des UE, des ressources et des SAÉ en cliquant sur les boutons correspondants.<br>
            Pour afficher les boutons, passez la souris sur le rectangle du semestre désiré.<br>
            Si aucune UE n'existe pour le semestre, les boutons pour accéder aux formulaires des resources et des SAÉ ne seront pas affichés.
          </p>
        </div>
        <div class="popup_sub_title">Formulaires : </div>
          <p>Si vous voyez une barre noire comme ci-dessous, vous pouvez cliquer dessus pour dérouler ou replier une section.</p>
          <a class="dark_bar accordion_popup" style="width: 80%;">Menu déroulant</a>
          <div class="panel_popup" style="width: 70%; margin-right: 10vw;"></div>
        <p class="popup_sub_title">Formulaire UE : </p>
        <div class="container-fluid">
          <button class="button_more">+</button>
          <p>
            En cliquant sur ce bouton, une zone d'ajout d'UE apparaîtra.<br>
            Il vous suffira de renseigner les informations demandées, puis de cliquer sur <span class="btn_modify" style="height: 2vh; padding: 0.7vw; margin: 0; display: inline-block;">Sauvegarder</span> pour créer une nouvelle UE.<br>
          </p>
        </div>
        <div class="container-fluid">
          <p>
            Vous pouvez modifier les informations d'une UE directement dans la partie "UE créées", et cliquer sur <span class="btn_modify" style="height: 2vh; padding: 0.7vw; margin: 0; display: inline-block;">Modifier</span> pour appliquer les changements.<br>
          </p>
        </div>
      </div>
      <a class="dark_bar accordion_popup">Dates de rendu des fiches</a>
      <div class="panel_popup">
        <div class="container-fluid">
          <p>!!! Blabla pouet !!!</p>
        </div>
      </div>
      <a class="dark_bar accordion_popup">Rendu des fiches</a>
      <div class="panel_popup">
        <div class="container-fluid">
          <p>!!! Blabla pouet !!!</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.popup_main_title {
  font-size: 2vw;
  font-weight: bold;
  margin-bottom: 1vh;
}

.popup_administration {
  overflow-y: auto;
  height: 60vh;
}

.popup_sub_title {
  font-size: 1.5vw;
  font-weight: bold;
  margin-top: 1.5vh;
  margin-bottom: 1vh;
}

.popup_administration::-webkit-scrollbar {
  width: 0.8vw;
}

.popup_administration::-webkit-scrollbar-track {
  background: transparent;
}

.popup_administration::-webkit-scrollbar-thumb {
  background: var(--main-theme-secondary-color);
  border-radius: 15px;
  border: 0.2vw var(--main-theme-tertiary-color) solid;
}

.accordion_popup {
  width: 60vw;
  padding-right: 2.5vw;
  border: none;
  outline: none;
  text-align: left;
  font-family: inherit;
  font-weight: lighter;
  font-size: 1.05vw;
  cursor: pointer;
  position: relative;
}

.accordion_popup::after {
    content: '^';
    position: absolute;
    right: 1vw;
    transition: transform 0.3s ease;
    font-size: 0.9vw;
}

.accordion_popup.active::after {
    transform: rotate(180deg);
}

.panel_popup {
    width: 57vw;
    justify-self: center;
    margin: 2vw;
    padding: 0;
    background-color: rgba(0, 0, 0, 0.35);
    max-height: 0;
    overflow: hidden;
    border-bottom-left-radius: 15px;
    border-bottom-right-radius: 15px;
    color: var(--main-theme-secondary-color);
    margin-top: 0;
}

.panel_popup>p {
    margin-top: 0;
    padding-top: 1vw;
}
</style>
