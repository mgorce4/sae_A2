<script setup>

</script>

<template>
    <div id="main">
        <div style="display: flex; align-items: center; height: 1vw;">
            <div id="return_arrow">
                <button id="back_arrow"  onclick="window.location.hash = '#/dashboard-administration'">←</button>
                <p>Retour</p>
            </div>
        </div>

        <div style="display: flex; gap: 50px; margin-top: 1vw">
            <div id="rigth_component">
                <div id="date" style="justify-items: center">
                    <p class="title">Date de l'année scolaire en cours</p>

                    <div>
                        <p>Date de début : </p>
                        <input type="date" id="start_date" name="start_date">

                        <p>Date de fin : </p>
                        <input type="date" id="end_date" name="end_date">
                    </div>

                    <div id="button_help">
                        <button id="button" style="margin-top: 2vw; padding: 1vw; height: 7vw">Sauvegarder</button>
                    </div>
                </div>

                <div id="button_help">
                    <button id="button" style="margin-top: 2vw; padding: 1vw; height: 7vw">Centre d'aide</button>
                </div>
            </div>

            <div id="left_component">

            </div>
        </div>
    </div>
</template>

<style scoped>

#rigth_component {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 50%;
    color: white;
}

#date {
    background-color: var(--main-theme-background-color);
    border-radius: 1vw;
    padding: 1.5vw;
}

#left_component {
    width: 50%;
    background-color: var(--main-theme-background-color);
    height: 43.5vw;
    border-radius: 1vw;
}

</style>
