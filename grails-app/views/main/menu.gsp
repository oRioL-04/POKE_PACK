<meta name="layout" content="main"/>
<h2 class="page-title">Menú Principal</h2>
<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f9f9f9;
        margin: 0;
        padding: 0;
    }
    .page-title {
        text-align: center;
        color: #333;
        margin-top: 20px;
        font-size: 2rem;
    }
    ul {
        list-style: none;
        padding: 0;
        display: flex;
        justify-content: center;
        gap: 20px;
        margin-top: 30px;
    }
    li {
        background-color: #ffcb05;
        color: #333;
        padding: 12px 20px;
        border-radius: 10px;
        font-weight: bold;
        transition: background-color 0.3s, transform 0.2s;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    }
    li:hover {
        background-color: #ef5350;
        transform: scale(1.05);
    }
    a {
        color: #333;
        text-decoration: none;
        font-size: 1.1rem;
    }
</style>
<ul>
    <li><g:link controller="Main" action="pokedex">Ver Pokédex</g:link></li>
    <li><g:link controller="Main" action="abrirSobres">Abrir sobres</g:link></li>
    <li><g:link controller="Battle" action="selectTeam">Combate</g:link></li>
    <li><g:link controller="trade" action="intercambios">Intercambios</g:link></li>

</ul>