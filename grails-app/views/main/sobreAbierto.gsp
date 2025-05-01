<meta name="layout" content="main"/>
<h2>Cartas obtenidas</h2>
<style>
    .cards-container {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 20px;
        margin: 20px;
    }
    .card {
        text-align: center;
        background: #fff;
        border-radius: 10px;
        padding: 15px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
        transition: transform 0.3s ease, box-shadow 0.3s ease;
        width: 200px;
    }
    .card img {
        height: 220px;
        border-radius: 10px;
        margin-bottom: 10px;
    }
    .card:hover {
        transform: scale(1.05);
        box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
    }
    .card p {
        font-size: 1.1rem;
        font-weight: bold;
        margin: 5px 0;
    }
    .card small {
        color: #666;
        font-size: 0.9rem;
    }
</style>
<div class="cards-container">
    <g:each in="${cards}" var="card">
        <div class="card">
            <img src="${card.images.small}" alt="${card.name}"/>
            <p>${card.name}</p>
            <small>${card.setName}</small>
        </div>
    </g:each>
</div>
<g:link controller="Main" action="menu">Volver al menú</g:link>