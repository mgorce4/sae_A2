<script setup>
  import { onMounted, ref, nextTick, watch, computed } from 'vue';
  import axios from 'axios';

  const display_more_area = ref(false);
  const coursName = ref('');
  const coursNb = ref('');
  const coursList = ref([]);

  const errors = ref({
    coursName: false,
    coursNb: false,
  });

  const navigateToDashboard = () => {
    document.location.href = '#/dashboard-administration';
  };

  const goToRessourceSheet = (url, semester) => {
      window.location.hash = `${url}?id=${semester}`
  }

  const attachAccordionListeners = () => {
    nextTick(() => {
      document.querySelectorAll(".accordion_UE").forEach(temp => {
        const newAcc = temp.cloneNode(true);
        temp.parentNode.replaceChild(newAcc, temp);
        newAcc.addEventListener("click", function() {
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
      });
    });
  }
  
  // Watch for error changes to update panel height
  watch(errors, () => {
    nextTick(() => {
      const panels = document.querySelectorAll('.panel_UE');
      panels.forEach(panel => {
        if (panel.style.maxHeight && panel.style.maxHeight !== '0px') {
          panel.style.maxHeight = panel.scrollHeight + "px";
        }
      });
    });
  }, { deep: true });

  // Prevent typing invalid characters in number inputs
  const preventInvalidChars = (event) => {
    const invalidChars = ['e', 'E', '+', '-', '.', ',']
      if (invalidChars.includes(event.key)) {
        event.preventDefault()
      }
  }

  const getCourses = computed(() => {
    return coursList.value;
  });

  // Watch for changes to reattach accordion listeners
  watch([coursList, display_more_area], () => {
    attachAccordionListeners();
  });

  onMounted(async () => {
    const response = await axios.get(`http://localhost:8080/api/paths`)
    // Filtrer les paths par institution
    coursList.value = response.data.filter(path => 
      path.institution && path.institution.idInstitution === parseInt(localStorage.idInstitution)
    )
    attachAccordionListeners();
  })

  const save = async () => {
    errors.value = {
      coursName: false,
      coursNb: false,
    }

    let hasErrors = false
    if (!coursName.value || coursName.value.trim() === '') {
      errors.value.coursName = true
      hasErrors = true
    }

    if (!coursNb.value || String(coursNb.value).trim() === '') {
      errors.value.coursNb = true
      hasErrors = true
    }

    if (hasErrors) {
      return
    }

    const coursNbIsNum = parseInt(coursNb.value)
    if (isNaN(coursNbIsNum)) {
      errors.value.coursNb = true
      return
    }

    try{      
      const response =  {
        name: coursName.value,
        number: parseInt(coursNb.value),
        institution: {
          idInstitution: parseInt(localStorage.idInstitution)
        }
      }
      console.log('sending UE data:', response);

      await axios.post ('http://localhost:8080/api/paths', response);
      [coursName, coursNb].forEach(refVar => refVar.value = '');
      display_more_area.value = false;

      // Recharger et filtrer les paths par institution
      const allPaths = await axios.get(`http://localhost:8080/api/paths`);
      coursList.value = allPaths.data.filter(path => 
        path.institution && path.institution.idInstitution === parseInt(localStorage.idInstitution)
      );
      attachAccordionListeners();
    }catch(error){
      console.error('Erreur de sauvegarde', error);
    }
  }
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

        <form v-show="display_more_area" method="post" v-on:submit.prevent="">
          <a class="accordion_UE" id="dark_bar">Ajout d'un parcours :</a>
          <div class="panel_UE">
             <div>
              <label>Nom du parcours : <span class="required">*</span></label>
              <input type="text" class="input" v-model="coursName" />
              <span v-if="errors.coursName" class="error-message">Merci de remplir ce champ</span>
            </div>
            <div>
              <label>Nombre associé au parcours : <span class="required">*</span></label>
              <input type="number" step="1" class="input" v-model="coursNb" @keydown="preventInvalidChars" />
              <span v-if="errors.coursNb" class="error-message">Merci de remplir ce champ</span>
            </div>
            <div>
              <input id="btn_cancel_UE" class="btn1" type="reset" value="Annuler">
              <input id="btn_save_UE" class="btn1" type="submit" value="Sauvegarder" @click="save">
            </div>
          </div>
        </form>
        
        <div v-for="cours in getCourses" :key="cours.idPath">
          <button class="btn_form_acces"  @click="goToRessourceSheet('#/mccc-select-form')">{{ cours.name }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
#form_select_page {
  margin: 3vw 14vw;
  justify-content: center;
  color: var(--main-theme-terciary-color);
}

.back {
  font-size: 1.5vw;
  font-weight: bold;
  color: var(--main-theme-terciary-color);
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

.required {
  color: white;
  font-weight: bold;
}

.error-message {
  color: red;
  font-size: 0.8vw;
  margin-top: 0.2vw;
  display: block;
}

/* Remove number input spinners/arrows */
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
  appearance: textfield;
}
</style>
