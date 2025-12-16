<template>
    <footer id="footer-red-rect" class="container-fluid spb">
        <p id="establishment-name">Université de Limoges</p>
        <p id="edit-year">{{new Date().getFullYear()}}</p>
    </footer>
</template>

<style>
/* -- Footer -- */
#footer-red-rect {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 94%;
    height: 3.5vw;
    padding: 0 3%;
    background-color: #B51621;
}

#establishment-name {
    color: #FFFFFF;
    text-shadow: 0 0 5px black;
    font-size: 1.5vw;
}

#edit-year {
    font-size: 1.5vw;
    color: #FFFFFF;
    text-shadow: 0 0 5px black;
}
</style>