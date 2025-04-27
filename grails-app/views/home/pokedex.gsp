<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Pokédex</title>
    <meta name="layout" content="main"/>
    <style>
    .pokedex-container {
        width: 100%;
        max-width: 1200px;
        margin: 0 auto;
        text-align: center;
        padding: 20px;
    }

    h1 {
        color: #ef5350;
        margin-bottom: 30px;
        font-size: 2.5rem;
        text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.1);
    }

    .pokemon-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
        gap: 20px;
        margin: 30px 0;
        padding: 10px;
    }

    .pokemon-card {
        background: white;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        text-align: center;
        padding: 15px;
    }

    .pokemon-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
    }

    .pokemon-card img {
        width: 120px;
        height: 120px;
        object-fit: contain;
        margin-bottom: 10px;
    }

    .pokemon-card p {
        margin: 0;
        font-weight: 600;
        color: #2d3748;
        font-size: 16px;
        text-transform: capitalize;
    }

    .button {
        display: inline-block;
        background-color: #42a5f5;
        color: white;
        padding: 12px 25px;
        margin: 20px 10px;
        text-decoration: none;
        border-radius: 30px;
        font-weight: 600;
        transition: all 0.3s ease;
        box-shadow: 0 4px 10px rgba(66, 165, 245, 0.3);
    }

    .button:hover {
        background-color: #1e88e5;
        transform: translateY(-2px);
        box-shadow: 0 6px 15px rgba(66, 165, 245, 0.4);
    }

    .empty-message {
        font-size: 1.2rem;
        color: #666;
        margin: 50px 0;
        padding: 20px;
        background: white;
        border-radius: 10px;
        display: inline-block;
    }

    .search-container {
        margin: 20px 0;
    }

    .search-input {
        padding: 10px 15px;
        border-radius: 30px;
        border: 2px solid #ddd;
        width: 300px;
        max-width: 100%;
        font-size: 16px;
        outline: none;
        transition: all 0.3s ease;
    }

    .search-input:focus {
        border-color: #42a5f5;
        box-shadow: 0 0 0 3px rgba(66, 165, 245, 0.2);
    }

    @media (max-width: 768px) {
        .pokemon-grid {
            grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
        }
    }
    </style>
</head>
<body>
<div class="pokedex-container">
    <h1>Mi Pokédex</h1>

    <div class="search-container">
        <input type="text" class="search-input" placeholder="Buscar Pokémon..." id="searchInput">
    </div>

    <g:if test="${pokemons}">
        <div class="pokemon-grid" id="pokemonGrid">
            <g:each in="${pokemons}" var="poke">
                <div class="pokemon-card" data-name="${poke.name.toLowerCase()}">
                    <img src="${poke.imageUrl}" alt="${poke.name}">
                    <p>${poke.name}</p>
                </div>
            </g:each>
        </div>
    </g:if>
    <g:else>
        <p class="empty-message">Tu Pokédex está vacía. ¡Abre algunos sobres para añadir Pokémon!</p>
    </g:else>

    <div>
        <g:link controller="home" action="index" class="button">⬅ Volver al Inicio</g:link>
    </div>
</div>

<script>
    document.getElementById('searchInput').addEventListener('input', function(e) {
        const searchTerm = e.target.value.toLowerCase();
        const cards = document.querySelectorAll('.pokemon-card');

        cards.forEach(card => {
            const pokemonName = card.getAttribute('data-name');
            if (pokemonName.includes(searchTerm)) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    });
</script>
</body>
</html>