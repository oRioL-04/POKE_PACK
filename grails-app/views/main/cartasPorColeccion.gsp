<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 1/5/25
  Time: 14:44
--%>

<meta name="layout" content="main"/>
<h2>Cartas de la colección: ${setId}</h2>
<style>
    .cards-container {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
        gap: 15px;
        padding: 20px;
    }
    .card {
        text-align: center;
        background: #fff;
        border-radius: 8px;
        padding: 10px;
        box-shadow: 0 0 5px #ccc;
    }
    .card img {
        height: 120px;
    }
</style>
<div class="cards-container">
    <g:each in="${cards}" var="card">
        <div class="card">
            <img src="${card.imageUrl}" alt="${card.name}"/>
            <p>${card.name}</p>
        </div>
    </g:each>
</div>
<g:link controller="Main" action="pokedex">Volver a la Pokédex</g:link>