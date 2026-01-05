<script setup>
    import { ref, onMounted } from 'vue'
    import axios from 'axios'

    import { status, institutionLocation } from '../main' /*add , userName next to status to import it*/
    status.value = "Professeur"
    institutionLocation.value = localStorage.institutionLocation

    const users = ref([]) //get the data from database ressources-sheets

    onMounted(async () => {
        axios.get('http://localhost:8080/api/resource-sheets').then(response => (users.value = response.data))
    })

    const goToRessourceSheet = (id) => {
        window.location.hash = `#/form-ressource-sheet?id=${id}`
    }

    /*
    const name = ref([]) //to get the username from who's connected
    onMounted(async () => {
        axios.get('http://localhost:8080/api/users').then(response => (name.value = response.data))
    })
    userName.value = name.value.username
    */
</script>

<template>
    <div id="ressources" >
        <div id="for_scroll_bar" style="overflow-y: scroll; margin: 1vw; height: 24vw;">
            <p id="title">Vos ressources : </p>
            <div id="div_sheets" >
                <button id="sheets" @click="goToRessourceSheet(u.idResourceSheet)" v-for="u in users" :key="u.idResourceSheet">
                    <p>{{ u.resource.label}}</p>
                </button>
            </div>
        </div>
    </div>
</template>

<style>


#ressources{
    height: 25vw;
    margin: 3vw 14vw;
    justify-content: center;
    background-color: rgb(61, 67, 117);
    border-radius: 15px;
}

#title{
    color: white;
    padding-left: 10px ;
    font-size: 1.5vw;
    width: fit-content;
}

#div_sheets{
    display: flex;
    flex-wrap: wrap;
    padding: 0;
}

#sheets{
    min-height: 7vw;
    max-height: 10vw;
    min-width: 20vw;
    margin: 1em;
    background-color: rgb(47, 47, 70);
    border-radius: 15px;
    align-items: center;
}

#sheets > p{
    font-size: 4.5vw;
    color: white;
    text-align: center;
    margin: 2vw;
}

#for_scroll_bar::-webkit-scrollbar {
    width: 12px;
}

#for_scroll_bar::-webkit-scrollbar-track {
    margin: 1em;
    background: rgb(42,45,86);
    box-shadow: inset 0 0 5px rgb(24, 26, 50);
    border-radius: 10px;
}

#for_scroll_bar::-webkit-scrollbar-thumb {
    background: rgb(254,254,254);
    border-radius: 10px;
}
</style>
