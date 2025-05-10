<meta name="layout" content="main"/>
<h2>Tu Pokédex</h2>
<style>
    .search-bar {
        margin: 20px auto;
        text-align: center;
    }
    .search-bar input {
        width: 50%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 5px;
        font-size: 1rem;
    }
    .grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
        gap: 20px;
        padding: 20px;
    }
    .set-card img {
        width: 100%;
        height: 180px;
        object-fit: contain;
    }
    .set-card {
        background-color: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 0 10px rgba(0,0,0,0.1);
        text-align: center;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 10px;
        position: relative;
        min-width: 180px; /* Ancho mínimo */
        max-width: 200px; /* Ancho máximo */
        box-sizing: border-box; /* Asegura que el padding no afecte el tamaño */
    }

    .set-card a {
        margin-top: 10px;
        background-color: #ffcb05;
        padding: 10px 15px;
        border-radius: 8px;
        text-decoration: none;
        font-weight: bold;
        color: #333;
        display: block; /* Cambiar a block para ocupar todo el ancho */
        width: 100%; /* Asegura que los botones ocupen el mismo ancho */
        text-align: center;
        box-sizing: border-box; /* Evita desbordamientos */
    }
    .set-card small {
        margin-top: auto;
        color: #666;
        font-size: 0.9rem;
    }

    .favorite-icon {
        position: absolute;
        top: 10px;
        right: 10px;
        background-color: transparent;
        border: none;
        font-size: 1.5rem;
        color: #ccc;
        cursor: pointer;
        transition: color 0.3s ease;
    }
    .favorite-icon.favorite {
        color: #ffcb05;
    }
</style>

<div class="search-bar">
    <input type="text" id="searchInput" placeholder="Buscar colección...">
</div>

<div class="grid" id="setGrid">
    <g:each in="${sets}" var="set">
        <div class="set-card">
            <button class="favorite-icon ${set.isFavorite ? 'favorite' : ''}" data-set-id="${set.id}">
                ★
            </button>
            <img src="${set.logoUrl}" alt="${set.name}" onerror="this.src='/images/default.png'"/>
            <p class="set-name">${set.name}</p>
            <small>${set.percentage}% completado</small>
            <g:link action="cartasPorColeccion" params="[setId: set.id]">
                Ver colección
            </g:link>
        </div>
    </g:each>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const searchInput = document.getElementById("searchInput");
        const setGrid = document.getElementById("setGrid");
        const setCards = setGrid.querySelectorAll(".set-card");

        // Función de búsqueda
        searchInput.addEventListener("input", function () {
            const query = searchInput.value.toLowerCase();
            setCards.forEach(card => {
                const setName = card.querySelector(".set-name").textContent.toLowerCase();
                card.style.display = setName.includes(query) ? "flex" : "none";
            });
        });

        // Función para marcar como favorito
        const favoriteIcons = document.querySelectorAll(".favorite-icon");
        favoriteIcons.forEach(icon => {
            icon.addEventListener("click", function () {
                const setId = this.getAttribute("data-set-id");

                fetch("${createLink(controller: 'Main', action: 'toggleFavorite')}", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ setId })
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        this.classList.toggle("favorite", data.isFavorite);
                    } else {
                        alert(data.message || "Error al marcar como favorito.");
                    }
                })
                .catch(error => console.error("Error:", error));
            });
        });
    });
</script>