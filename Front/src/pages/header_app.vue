<script setup>
    import { status } from '../main'

    import { userName} from '../main'
    userName.value =""
</script>

<template>
    <header id="header_div">
        <div id="header_top" class="container-fluid spb">
            <div id="app_name_and_logo" class="container-fluid">
                <a id="unilim_logo">
                    <img src="./../../media/unilim_logo.webp" style="margin: 0; padding: 0; width: 100%; height: 100%;" alt="logo">
                </a>
                <div id="dividing_line"></div>
                <p id="app_name">Syncadia</p>
            </div>
            <div v-show="status" id="user_name_and_pp" class="container-fluid">
                <p id="user_name">{{userName}}</p>
                <img id="profile_picture" src="./../../media/no_profile_picture.webp" alt="profile_picture">
            </div>
        </div>
        <div id="red_rect" class="container-fluid spb">
            <p v-show="status" id="user_status">Statut : {{ status }}</p>
            <a v-show="status" id="btn_disconnect" href="#/">
                Déconnexion
            </a>
        </div>
    </header>
</template>

<style>
/* -- Header -- */
#header_div {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 8vw;
    background-color: #FFFFFF;
}

#header_top {
    width: 94%;
    height: 70%;
    padding: 0 3%;
}

#app_name_and_logo {
    width: 50%;
}

#unilim_logo {
    cursor: pointer;
    height: 80%;
    width: calc(height);
    margin: 0 2.5vw 0 0;
}

#dividing_line {
    background-color: #4B575F;
    width: 0.2vw;
    height: 70%;
}

#app_name {
    font-size: 2.8vw;
    height: fit-content;
    margin: 0 0 0 2.5vw;
    color: rgb(75, 87, 95);
    font-weight: bold;
}

#user_name_and_pp {
    justify-content: flex-end;
    width: 50%;
}

#user_name {
    font-size: 1.2vw;
    margin: 0 0.5vw;
}

#profile_picture {
    cursor: pointer;
    border-radius: 3vw;
    height: 60%;
    width: calc(height);
    margin: 0;
}

#red_rect {
    background-color: #B51621;
    width: 94%;
    height: 3vw;
    padding: 0 3%;
}

#user_status {
    font-size: 1.5vw;
    color: #FFFFFF;
    text-shadow: 0 0 5px black;
    padding-top: 0;
    justify-content: center;
}

#btn_disconnect {
    height: 1.5vw;
    width: 7vw;
    padding: 0.2vw;
    border-width: 0.15vw;
    border-radius: 0.5vw;
    font-size: 1vw;
    text-decoration: none;
    align-content: center; /*center the text in the button */
    text-align: center;
    background-color: #4B575F;
    color: #FFFFFF;
}

#btn_disconnect:hover {
    cursor: pointer;
}
</style>