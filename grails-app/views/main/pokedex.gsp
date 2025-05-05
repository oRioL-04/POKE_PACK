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
        height: 150px;
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
        justify-content: space-between;
        height: 250px;
    }
    .set-card small {
        margin-top: auto;
        color: #666;
        font-size: 0.9rem;
    }
    .set-card a {
        margin-top: 10px;
        background-color: #ffcb05;
        padding: 8px;
        border-radius: 8px;
        text-decoration: none;
        font-weight: bold;
        color: #333;
    }
</style>

<div class="search-bar">
    <input type="text" id="searchInput" placeholder="Buscar colección...">
</div>

<div class="grid" id="setGrid">
    <g:each in="${sets}" var="set">
        <div class="set-card">
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

        searchInput.addEventListener("input", function () {
            const query = searchInput.value.toLowerCase();
            setCards.forEach(card => {
                const setName = card.querySelector(".set-name").textContent.toLowerCase();
                if (setName.includes(query)) {
                    card.style.display = "flex"; // Muestra el elemento
                } else {
                    card.style.display = "none"; // Oculta el elemento
                }
            });
        });
    });
</script>