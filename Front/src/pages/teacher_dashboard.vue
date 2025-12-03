<script setup>
    import { status } from '../main'
    status.value = "Professeur"

    import { rx } from '../main' //pour le nom des ressources
    rx.value = 'R1.01' //exemple

</script>

<template>
    <div id="ressources" >
        <h1 id="titre">Vos ressources : </h1>
        <div id="divFiches">
            <div id="fiches">
                <p>{{ rx }}</p>
            </div>
            <div id="fiches">
                <p>{{ rx }}</p>
            </div>
            <div id="fiches">
                <p>{{ rx }}</p>
            </div>
            <div id="fiches">
                <p>{{ rx }}</p>
            </div>
            <div id="fiches">
                <p>{{ rx }}</p>
            </div>
        </div>
    </div>
</template>

<style>

#ressources{
    max-height: 18.5vw;
    margin: 1em;
    padding-left: 0.5vw;
    padding-right: 2vw;
    background-color: rgb(61, 67, 117);
    border-radius: 15px;
    overflow-y: scroll;
}

#titre{
    color: white;
    padding-left: 10px ;
    font-size: 1.5vw;
    width: fit-content;
}

#divFiches{
    display: flex;
    flex-wrap: wrap;
    width: 80%;
    padding: 0;
}

#fiches{
    min-height: 10vw;
    max-height: 13vw;
    min-width: 23vw;
    margin: 1em;
    background-color: rgb(47, 47, 70);
    border-radius: 15px;
    align-items: center;
}

#fiches > p{
    font-size: 5vw;
    color: white;
    text-align: center;
    margin: 3vw;
}

#ressources::-webkit-scrollbar {
    width: 12px;
}

#ressources::-webkit-scrollbar-track {
    margin: 1em;
    background: rgb(42,45,86);
    box-shadow: inset 0 0 5px rgb(24, 26, 50);
    border-radius: 10px;
}

#ressources::-webkit-scrollbar-thumb {
    background: rgb(254,254,254);
    border-radius: 10px;
}

</style>
