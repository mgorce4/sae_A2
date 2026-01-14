<script setup>
</script>

<template>
  <p style="font-size: 2vw;">Guide utilisateur:</p>
  <div class="full">
    <p style="font-size: 1.3vw;">Tout d'abord, pas de panique, certains champs sont déjà préremplis. Cliquez dessus pour pouvoir les modifier.</p>
    <div class="min_div">
      <button class="accordion" id="dark_Bar" style="width: 13vw;" > ... </button>
      <p class="text"> - Signifie un menu déroulant</p>
    </div>
    <div class="min_div ">
      <input type="text" placeholder="Mot-clé..." class="keyword-input" style="width: 13vw; flex: 0;"/>
      <p class="text"> - Champs gris pour pouvoir entrer du texte </p>
    </div>
     <div class="min_div">
      <div class="copycat">
        <p class="hours_total_text" style="font-size: 1vw;">Nombre d'heures total : ... / ...</p>
      </div>
      <p class="text"> - Partie non modifiable, se calcule automatiquement</p>
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
            <textarea placeholder="Contenu..." class="pedagogical-input" style="cursor: default;"></textarea>
            <button class="btn-remove-pedagogical" title="Supprimer">✕</button>
          </div>
        </div>
        <div class="pedagogical-footer">
          <button class="btn-add-pedagogical">+ Ajouter</button>
        </div>
      </div>
      <div style="flex-wrap: wrap;">
        <p class="text"> - Section pour les contenue pédagogique de chaque séances</p>
        <p class="text"> - Double cliquez et maintenez la partie "contenue" pour pouvoir la changer de place avec les autres</p>
        <p class="text"> - Cliquez sur la zone de texte pour modifier et entrer votre texte (contenu dans ce cas là)</p>
      </div>
      
    </div>

    
  </div>
  
</template>

<style>
.full{
  overflow: auto;
  height: 50vh;
}

.text{
  font-size: 1.2vw;
  margin-left: 1vw;
}

.copycat{
  width: 13vw;
  height:2vw;
  display: flex;
  align-items: center;
  padding: 0.8vw 1.5vw;
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 10px;
}

.min_div{
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
