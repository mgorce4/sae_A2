<template>
    <footer id="footer_red_rect" class="container-fluid spb">
        <p id="establishment_name">Université de Limoges</p>
        <p id="edit_year">{{new Date().getFullYear()}}</p>
    </footer>
</template>

<style>
/* -- Footer -- */
#footer_red_rect {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 94%;
    height: 3.5vw;
    padding: 0 3%;
    background-color: #B51621;
}

#establishment_name {
    color: #FFFFFF;
    text-shadow: 0 0 5px black;
    font-size: 1.5vw;
}

#edit_year {
    font-size: 1.5vw;
    color: #FFFFFF;
    text-shadow: 0 0 5px black;
}
</style>