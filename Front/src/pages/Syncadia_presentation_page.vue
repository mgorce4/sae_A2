<script setup>
import { status } from '../main'
import { router } from '@/router'

const routes = {
    Administration: '/dashboard-administration',
    Professeur: '/teacher-dashboard',
}

const goToDashboard = () => {
    router.push(routes[status.value] || '/')
}
</script>

<template>
    <div id="justified">
        <div id="center">
            <div style="display: flex; align-items: center; height: 1vw">
                <a class="back_arrow" @click="goToDashboard" style="cursor: pointer">←</a>
                <p class="back" @click="goToDashboard" style="cursor: pointer">Retour à l'accueil</p>
            </div>
        </div>
        <div class= "background_form_mccc">
            <div id="white_text">
                <p id="title">L'histoire de Syncadia</p>
                <p>Le nom Syncadia provient de la combinaison des mots synchroniser et academia (terme 
anglais signifiant « académie »). Cette fusion illustre parfaitement le concept de l’ERP, 
centré sur la coordination et la gestion du milieu éducatif. De plus, la sonorité du nom 
évoque le mot anglais cicada (« cigale »), symbole de l’été et de la fin de l’année scolaire, ce 
qui renforce la cohérence et la pertinence de ce choix.</p>
                <p id="title">Qui sommes-nous ?</p>
                <p>Nous sommes une équipe de 6 étudiants en BUT informatique à l'IUT du Limousin. Nous réalisons ce projet dans le cadre d'une Situation d'Apprentissage Evaluée.</p>
                <p id="title">Changements à venir</p>
                <ul>
                    <li>Un envoi de mail automatique aux professeurs n'ayant pas encore rempli leurs fiches ressource à l'approche de la fin des semestres.</li>
                    <li>Un centre de control pour définir un roulement annuel.</li>
                    <li>L'ajout d'utilisateurs depuis le site et par un import de csv</li>
                </ul>
            </div>
        </div>
    </div>
    
</template>

<style>

#center {
    margin: 3vw ;
    justify-content: center;
    color: var(--main-theme-tertiary-color);
}

#justified {
    justify-content: center;
    justify-self: center;
    width: 80%;
}

#white_text {
    color: var(--main-theme-secondary-color);
    margin: 2%;
}

#title {
    font-size: 2vw;
    font-weight: bold;
    margin-bottom: 1vw;
}

</style>