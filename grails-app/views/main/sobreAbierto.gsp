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
    .card.rare {
        border: 2px solid gold;
        animation: glow 1s infinite alternate;
    }
    @keyframes glow {
        from {
            box-shadow: 0 0 10px gold;
        }
        to {
            box-shadow: 0 0 20px gold;
        }
    }
</style>
<div class="cards-container">
    <g:each in="${cards}" var="card">
        <div class="card ${card?.rarity == 'Common' ? '' : 'rare'}">
            <img src="${card.imageUrl ?: '/images/default.png'}" alt="${card.name}"/>
            <p>${card.name}</p>
            <small>${set?.name}</small>
        </div>
    </g:each>
</div>
<g:link controller="Main" action="menu">Volver al menú</g:link>