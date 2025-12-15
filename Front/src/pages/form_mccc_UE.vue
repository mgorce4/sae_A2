<script setup>
    import { status } from '../main'
    status.value = "Administration"
</script>

<template>
    <div id="UE"> 
        <div id="returnArrow">
                <button  id="backArrow" onclick="document.location.href='#/mccc-select-form'">←</button>
                <p>Retour</p>
        </div>
        <div id="backgroundForm">
            <div id="form">
                <div style="background-color: rgb(44,49,88); height: auto; border-radius: 10px; margin: 1vw;">
                    <h1 style="color: white; text-align: center; padding-top: 1vw; font-weight: lighter;">Unités d’enseignement</h1>
                </div>
                <div id="darkBar" style="display: flex;">
                    <h1>Ajout d'une UE :</h1>
                    
                </div>
            </div>
        </div>
    </div>
</template>

<style>
#UE{
    margin: 3vw 14vw;
    justify-content: center;
    height: 100%;
}

#returnArrow{
    display: flex;
    align-items: center;
}

#returnArrow > p{
    font-size: 1.5vw;
    font-weight: bold;
    color: black;
    margin-left: 1.5vw;
}

#backgroundForm{
    height: 25vw;
    background-color: rgb(61, 67, 117);
    border-radius: 15px;
}

#form{
    height: 90%;
    margin: 1vw;
    overflow-y: scroll;
}

#form::-webkit-scrollbar {
    width: 12px;
}

#form::-webkit-scrollbar-track {
    margin: 1em;
    background: rgb(42,45,86);
    box-shadow: inset 0 0 5px rgb(24, 26, 50);
    border-radius: 10px;
}

#form::-webkit-scrollbar-thumb {
    background: rgb(254,254,254);
    border-radius: 10px;
}

</style>