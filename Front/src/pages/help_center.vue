<script setup>

</script>

<template>

    <div id="main">
        <div style="display: flex; align-items: center; height: 1vw">
            <div id="return_arrow">
                <button id="back_arrow" onclick="window.location.hash = '#/control-center'">←</button>
                <p>Retour</p>
            </div>
        </div>

        <div id="div">
            <p id="title">Comment contacter l'assitance de la DSI : </p>
            <ul>
                <li class="li_component">
                    <p>Par mail : svp-dsi@unilim.fr</p>
                </li>
                <li>
                    <p>Ou via cette <a href="https://rt4.unilim.fr/SelfService" style="color: white">adresse</a></p>
                </li>
            </ul>
        </div>
    </div>

</template>

<style scoped>
#div {
    background-color: var(--main-theme-background-color);
    border-radius: 1vw;
    padding: 2vw;
    margin-top: 2vw;
    color: white;
}
</style>
