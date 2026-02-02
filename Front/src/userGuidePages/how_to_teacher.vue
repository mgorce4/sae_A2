<script setup></script>

<template>
    <p style="font-size: 2vw">Guide utilisateur :</p>
    <div class="full">
        <p style="font-size: 1.4vw; margin-top: 0; margin-bottom: 1vh">
            Certains champs sont déjà préremplis. Cliquez dessus pour pouvoir les modifier.<br />
            La fiche reste constamment modifiable, tant que vous sauvegardez à la fin.<br />
            N'oubliez pas de sauvegarder pour enregistrer tout changement.
        </p>
        <div class="min_div">
            <button class="accordion" id="dark_Bar" style="width: 13vw">...</button>
            <p class="text">
                En cliquant sur une barre noire comme celle-ci, vous pouvez faire dérouler ou
                replier une section.
            </p>
        </div>
        <div class="min_div">
            <input type="text" class="keyword-input" style="width: 13vw; flex: 0" />
            <p class="text">
                Vous pouvez entrer du texte en cliquant dans les champs gris à votre disposition.
            </p>
        </div>
        <div class="min_div">
            <div class="copycat">
                <p class="hours_total_text" style="font-size: 1vw">
                    Nombre d'heures total : ... / ...
                </p>
            </div>
            <p class="text">
                Cette partie est non modifiable, elle se calculera automatiquement selon les
                informations remplies.
            </p>
        </div>
        <!-- DS Section -->
        <div class="min_div">
            <div class="pedagogical-section">
                <div class="pedagogical-header">
                    <p class="pedagogical-title">Titre</p>
                </div>
                <p class="pedagogical-subtitle">Détailler ici le contenu pédagogique</p>
                <div class="pedagogical-items-container">
                    <div class="pedagogical-item">
                        <span class="pedagogical-number">1</span>
                        <textarea
                            placeholder="Contenu..."
                            class="pedagogical-input"
                            style="cursor: default"
                        ></textarea>
                        <button class="btn-remove-pedagogical" title="Supprimer">✕</button>
                    </div>
                </div>
                <div class="pedagogical-footer">
                    <button class="btn-add-pedagogical">+ Ajouter</button>
                </div>
            </div>
            <div style="flex-wrap: wrap">
                <p class="text">
                    Cette section est pour le contenu pédagogique de chaque séance, chaque zone
                    "contenu" représente une séance.
                </p>
                <p class="text">
                    Vous pouvez double-cliquer et maintenir la partie "contenu" pour pouvoir la
                    changer de place avec les autres zones.
                </p>
                <p class="text">
                    Vous pouvez aussi cliquer sur la zone de texte ("contenu") pour modifier et
                    entrer votre texte.
                </p>
            </div>
        </div>
    </div>
</template>

<style>
.full {
    overflow: auto;
    height: 50vh;
}

.text {
    font-size: 1.2vw;
    margin-left: 1vw;
}

.copycat {
    width: 13vw;
    height: 2vw;
    display: flex;
    align-items: center;
    padding: 0.8vw 1.5vw;
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: 10px;
}

.min_div {
    display: flex;
    margin-bottom: 1.5vw;
}

.full::-webkit-scrollbar {
    width: 12px;
}

.full::-webkit-scrollbar-track {
    background: transparent;
}

.full::-webkit-scrollbar-thumb {
    background: var(--div-rect-background-color);
    border-radius: 6px;
}

.full::-webkit-scrollbar-thumb:hover {
    background: blue;
}
</style>
