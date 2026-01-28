<script setup>
import { onMounted, nextTick } from 'vue'

const attachAccordionListeners = () => {
    nextTick(() => {
        const acc = document.querySelectorAll('.popup_administration .accordion_popup')
        acc.forEach((element) => {
            element.addEventListener('click', function () {
                this.classList.toggle('active')
                const panel = this.nextElementSibling
                if (panel.style.maxHeight) {
                    panel.style.maxHeight = null
                    panel.style.padding = '0'
                } else {
                    // Calculate the actual height including error messages
                    panel.style.height = 'auto'
                    panel.style.maxHeight = panel.scrollHeight + 'vw'
                    panel.style.padding = '1vw'
                }
            })
        })
    })
}

onMounted(() => {
    attachAccordionListeners()
})
</script>

<template>
    <p class="popup_main_title">Guide utilisateur :</p>
    <div class="popup_administration container-fluid cfh">
        <div>
            <a class="dark_bar accordion_popup">Gestion des MCCCs</a>
            <div class="panel_popup">
                <div class="container-fluid">
                    <button
                        type="button"
                        id="button"
                        style="width: 10vw; height: 5vh; font-size: 1vw"
                    >
                        MCCC
                    </button>
                    <p class="popup_text">
                        En cliquant sur ce bouton, vous accèderez à une page où sélectionner le
                        parcours
                    </p>
                </div>
                <p class="popup_sub_title">Parcours :</p>
                <div class="container-fluid">
                    <button class="button_more">+</button>
                    <p class="popup_text">
                        En cliquant sur ce bouton, une zone d'ajout de parcours apparaîtra.<br />
                        Il vous suffira de renseigner le nom du parcours et le nombre associé, puis
                        de cliquer sur "Ajouter" pour créer un nouveau parcours.<br />
                    </p>
                </div>
                <div class="container-fluid">
                    <button class="btn_modify">Modifier</button>
                    <p class="popup_text">
                        En cliquant sur ce bouton, une zone de modification du parcours
                        apparaîtra.<br />
                        Il vous suffira de renseigner le nouveau nom du parcours et le nouveau
                        nombre associé, <br />
                        puis de cliquer sur
                        <span class="btn_modify btn_how_to_admini">Sauvegarder</span> pour appliquer les
                        changements au parcours sélectionné.<br />
                    </p>
                </div>
                <p class="popup_sub_title">Page de sélection des formulaires :</p>
                <div class="container-fluid">
                    <div class="semester_rect">
                        <p class="semester_display">Semestre 1</p>
                        <div class="container-fluid spe">
                            <button class="btn_form_acces">UE</button>
                            <button class="btn_form_acces">Ressource</button>
                            <button class="btn_form_acces">SAÉ</button>
                        </div>
                    </div>
                    <p class="popup_text">
                        Vous pouvez accéder aux formulaires des UE, des ressources et des SAÉ en
                        cliquant sur les boutons correspondants.<br />
                        Pour afficher les boutons, passez la souris sur le rectangle du semestre
                        désiré.<br />
                        Si aucune UE n'existe pour le semestre, les boutons pour accéder aux
                        formulaires des ressources et des SAÉ ne seront pas affichés.
                    </p>
                </div>
                <div class="popup_sub_title">Formulaires :</div>
                <p class="popup_text">
                    Si vous voyez une barre noire comme ci-dessous, vous pouvez cliquer dessus pour
                    dérouler ou replier une section.
                </p>
                <a class="dark_bar accordion_popup" style="width: 80%">Menu déroulant</a>
                <div class="panel_popup" style="width: 70%; margin-right: 10vw"></div>
                <p class="popup_sub_title">Formulaire UE :</p>
                <div class="container-fluid">
                    <button class="button_more">+</button>
                    <p class="popup_text">
                        En cliquant sur ce bouton, une zone d'ajout d'UE apparaîtra.<br />
                        Il vous suffira de renseigner les informations demandées, puis de cliquer
                        sur <span class="btn_modify btn_how_to_admini">Sauvegarder</span> pour créer une
                        nouvelle UE.<br />
                    </p>
                </div>
                <div class="container-fluid">
                    <p class="popup_text">
                        Vous pouvez modifier les informations d'une UE directement dans la partie
                        "UE créées", et cliquer sur
                        <span class="btn_modify btn_how_to_admini">Modifier</span> pour appliquer les
                        changements.<br />
                    </p>
                </div>
                <p class="popup_sub_title">Formulaire Ressource :</p>
                <div class="container-fluid">
                    <button class="button_more">+</button>
                    <p class="popup_text">
                        En cliquant sur ce bouton, une zone d'ajout de ressource apparaîtra.<br />
                        Il vous suffira de renseigner les informations demandées, puis de cliquer
                        sur <span class="btn_modify btn_how_to_admini">Sauvegarder</span> pour créer une
                        nouvelle ressource.<br />
                    </p>
                </div>
                <div class="container-fluid">
                    <p class="popup_text">
                        Pour modifier les informations d'une ressource, cliquez sur le bouton
                        <span class="btn_modify btn_how_to_admini">Modifier</span> correspondant à la
                        ressource.<br />
                        Une zone de modification apparaîtra, il vous suffira de renseigner les
                        nouvelles informations, puis de cliquer sur
                        <span class="btn_modify btn_how_to_admini">Sauvegarder</span> pour appliquer les
                        changements.<br />
                    </p>
                </div>
                <p class="popup_sub_title">Formulaire SAÉ :</p>
                <div class="container-fluid">
                    <button class="button_more">+</button>
                    <p class="popup_text">
                        En cliquant sur ce bouton, une zone d'ajout de SAÉ apparaîtra.<br />
                        Il vous suffira de renseigner les informations demandées, puis de cliquer
                        sur <span class="btn_modify btn_how_to_admini">Sauvegarder</span> pour créer une
                        nouvelle SAÉ.<br />
                    </p>
                </div>
                <div class="container-fluid">
                    <p class="popup_text">
                        Pour modifier les informations d'une SAÉ, cliquez sur le bouton
                        <span class="btn_modify btn_how_to_admini">Modifier</span> correspondant à la
                        SAÉ.<br />
                        Une zone de modification apparaîtra, il vous suffira de renseigner les
                        nouvelles informations, puis de cliquer sur
                        <span class="btn_modify btn_how_to_admini">Sauvegarder</span> pour appliquer les
                        changements.<br />
                    </p>
                </div>
            </div>
            <a class="dark_bar accordion_popup">Dates de rendu des fiches</a>
            <div class="panel_popup">
                <div class="container-fluid">
                    <p class="popup_text">
                        Les dates de rendu des fiches correspondent à une date avant laquelle toutes
                        les fiches ressource des professeurs doivent être soumises pour l'année
                        scolaire en cours.
                    </p>
                </div>
                <div class="container-fluid">
                    <input
                        type="date"
                        v-model="new Date().toISOString().split('T')[0]"
                        name="popup_date"
                    />
                    <p class="popup_text">
                        En cliquant sur ce champ, un calendrier apparaîtra vous permettant de
                        sélectionner une date.<br />
                        Après avoir sélectionné la date désirée, cliquez sur le bouton
                        <span class="btn_modify btn_how_to_admini">Valider</span> pour appliquer la
                        nouvelle date de rendu des fiches.<br />
                    </p>
                </div>
            </div>

            <a class="dark_bar accordion_popup">Centre de contrôle</a>
            <div class="panel_popup">
                <div class="container-fluid">
                <p class="popup_text">
                    Le centre de contrôle permet de renseigner les dates de début et de fin de l'année scolaire en cours, de contacter le support technique, et gérer l'ajout et la suppression de professeurs.
                </p>
                </div>
                <p class="popup_sub_title">Dates de l'année scolaire en cours : </p>
                <div class="container-fluid">
                <input type="date" v-model="new Date().toISOString().split('T')[0]" name="popup_date"/>
                <p class="popup_text">
                    En cliquant sur ce champ, un calendrier apparaîtra vous permettant de sélectionner une date.<br>
                    Après avoir sélectionné la date désirée, cliquez sur le bouton <span class="btn_modify btn_how_to_admini">Sauvegarder</span> pour appliquer les nouvelles dates de l'année scolaire.<br>
                </p>
                </div>
                <p class="popup_sub_title">Centre d'aide : </p>
                <p class="popup_text">
                    Si vous rencontrez des problèmes techniques ou avez des questions concernant l'utilisation de la plateforme, vous pouvez contacter le support technique via le centre d'aide.<br>
                </p>
                <p class="popup_sub_title">Vues professeur : </p>
                <p class="popup_text">
                    Cette section permet de gérer l'ajout et la suppression des professeurs ayant accès à la plateforme.<br>
                </p>
                <div class="container-fluid">
                <button class="button_more">+</button>
                <p class="popup_text">
                    En cliquant sur ce bouton, une zone d'ajout de professeur apparaîtra.<br>
                    Il vous suffira de renseigner les informations demandées, puis de cliquer sur <span class="btn_modify btn_how_to_admini">Ajouter</span> pour créer un nouveau compte professeur.<br>
                </p>
                </div>
                <div class="container-fluid">
                <button class="btn_modify">Modifier</button>
                <p class="popup_text">
                    En cliquant sur ce bouton, une zone de modification des informations du professeur apparaîtra.<br>
                    Il vous suffira de renseigner les nouvelles informations, puis de cliquer sur <span class="btn_modify btn_how_to_admini">Sauvegarder</span> pour appliquer les changements au compte professeur sélectionné.<br>
                </p>
                </div>
                <div class="container-fluid">
                <button class="btn_modify">Supprimer</button>
                <p class="popup_text">
                    En cliquant sur ce bouton, le compte du professeur sélectionné sera supprimé de la plateforme.<br>
                    Veuillez confirmer la suppression lorsque vous y êtes invité.<br>
                </p>
                </div>
                <p class="popup_sub_title">Importation/Exportation CSV : </p>
                <div class="container-fluid">
                <button class="btn1">Importer CSV</button>
                <p class="popup_text">
                    En cliquant sur ce bouton, vous pourrez sélectionner un fichier CSV contenant les informations des professeurs à ajouter.<br>
                    Après avoir sélectionné le fichier, cliquez sur <span class="btn_modify btn_how_to_admini">Importer</span> pour ajouter les professeurs à la plateforme.<br>
                </p>
                </div>
                <div class="container-fluid">
                <button class="btn1">Exporter CSV</button>
                <p class="popup_text">
                    En cliquant sur ce bouton, un fichier CSV contenant les informations de tous les professeurs actuellement inscrits sur la plateforme sera généré et téléchargé sur votre ordinateur.<br>
                </p>
                </div>
            </div>

            <a class="dark_bar accordion_popup">Rendu des fiches</a>
            <div class="panel_popup">
                <div class="container-fluid">
                    <p class="popup_text">
                        Cette section permet de visualiser les fiches ressources soumises par les professeurs et de les télecharger.
                    </p>
                    </div>

                    <div class="container-fluid">
                    <div id="return_sheets_div" style="height: 6vw; width: 30vw;">
                        <div id="return_sheets_div_header">

                        <div id="option_path">
                            <select name="paths" class="paths">
                            <option :value="'selection_parcours'">Sélectionner un parcours</option>
                            <option :value="'Parcours 1'">Parcours 1</option>
                            <option :value="'Parcours 2'">Parcours 2</option>
                            </select>  
                        </div>

                        <div id="semesters_div">
                            <select name="semesters" class="semesters" :value="'S1'">
                            <option :value="'S1'">S1</option>
                            <option :value="'S2'">S2</option>
                            </select>
                        </div>
                        </div>
                    </div>
                    <p class="popup_text">
                        Utilisez les menus déroulants pour sélectionner le parcours et le semestre souhaités.<br>
                        Les fiches ressources soumises par les professeurs pour le parcours et le semestre sélectionnés seront affichées ci-dessous.<br>
                    </p>
                    </div>

                    <div class="container-fluid">
                    <button class="btn1">Visualiser</button>
                    <p class="popup_text">
                        En cliquant sur ce bouton, une nouvelle fenêtre s'ouvrira affichant la fiche ressource soumise par le professeur.<br>
                        Vous pourrez ainsi examiner la fiche avant de décider de la télecharger.<br>
                    </p>
                    </div>
                    <div class="container-fluid">
                    <input type="checkbox"/>
                    <p class="popup_text">
                        En cochant cette case, vous sélectionnez la fiche ressource soumise par le professeur.<br>
                        Vous pourrez ensuite cliquer sur le bouton <span><img src="../../media/download.webp" width="35" height="35" alt="download"/></span> pour télecharger toutes les fiches sélectionnées.<br>
                    </p>
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

.popup_text {
    font-size: 1vw;
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

.btn_how_to {
    height: 2vh;
    width: auto;
    padding: 0.7vw;
    margin: 0;
    display: inline-block;
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

.panel_popup > p {
    margin-top: 0;
    padding-top: 1vw;
}
</style>
