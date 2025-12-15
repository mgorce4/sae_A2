<script setup>
    import { status } from '../main'

    import { userName} from '../main'
    userName.value =""
</script>

<template>
    <header id="header-div">
        <div id="header-top" class="container-fluid spb">
            <div id="app-name-and-logo" class="container-fluid">
                <a id="unilim-logo">
                    <img src="./../../media/unilim_logo.webp" style="margin: 0; padding: 0; width: 100%; height: 100%;" alt="logo">
                </a>
                <div id="dividing-line"></div>
                <h1 id="app-name">Syncadia</h1>
            </div>
            <div v-show="status" id="user-name-and-pp" class="container-fluid">
                <h2 id="user-name">{{userName}}</h2>
                <img id="profile-picture" src="./../../media/no_profile_picture.webp" alt="profile_picture">
            </div>
        </div>
        <div id="red-rect" class="container-fluid spb">
            <h2 v-show="status" id="user-status">Statut : {{ status }}</h2>
            <a v-show="status" id="btn-disconnect" href="#/">
                Déconnexion
            </a>
        </div>
    </header>
</template>

<style>
/* -- Header -- */
#header-div {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 8vw;
    background-color: #FFFFFF;
}

#header-top {
    width: 94%;
    height: 70%;
    padding: 0 3%;
}

#app-name-and-logo {
    width: 50%;
}

#unilim-logo {
    cursor: pointer;
    height: 80%;
    width: calc(height);
    margin: 0 2.5vw 0 0;
}

#dividing-line {
    background-color: #4B575F;
    width: 0.2vw;
    height: 70%;
}

#app-name {
    font-size: 3vw;
    height: fit-content;
    margin: 0 0 0 2.5vw;
}

#user-name-and-pp {
    justify-content: flex-end;
    width: 50%;
}

#user-name {
    font-size: 1.2vw;
    margin: 0 0.5vw;
}

#profile-picture {
    cursor: pointer;
    border-radius: 3vw;
    height: 60%;
    width: calc(height);
    margin: 0;
}

#red-rect {
    background-color: #B51621;
    width: 94%;
    height: 30%;
    padding: 0 3%;
}

#user-status {
    font-size: 1.5vw;
    color: #FFFFFF;
    text-shadow: 0 0 5px black;
}

#btn-disconnect {
    height: fit-content;
    padding: 0.2vw;
    border-width: 0.15vw;
    border-radius: 0.5vw;
    font-size: 1vw;
    text-decoration: none;
    text-align: center;
    background-color: #4B575F;
    color: #FFFFFF;
}

#btn-disconnect:hover {
    cursor: pointer;
}
</style>