<meta name="layout" content="main"/>
<h2>Selecciona una colección</h2>
<style>
.grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 20px;
    padding: 20px;
}
.favorite-button.active {
    color: gold;
}
.set-card {
    background-color: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0,0,0,0.1);
    text-align: center;
}
.set-card img {
    width: 100%;
    height: 150px;
    object-fit: contain;
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
.favorite-button {
    font-size: 24px;
    color: gray;
    background: none;
    border: none;
    cursor: pointer;
    transition: color 0.3s ease;
}
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
</style>

<div class="search-bar">
    <input type="text" id="searchInput" placeholder="Buscar colección...">
</div>

<div class="grid" id="setGrid">
    <g:each in="${sets}" var="set">
        <div class="set-card">
            <img src="${set.logoUrl}" alt="${set.name}" onerror="this.src='/images/default.png'"/>
            <p>${set.name}</p>
            <p>Costo: 50 Pokémonedas</p>
            <g:link action="abrirSobre" params="[setId: set.id]"
                    onclick="return confirm('¿Estás seguro de que deseas abrir un sobre de la colección ${set.name}?')">
                Abrir sobre
            </g:link>
            <button
                data-set-id="${set.id}"
                onclick="toggleFavorite(this)"
                class="favorite-button ${set.isFavorite ? 'active' : ''}">
                ★
            </button>
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
                const setName = card.querySelector("p").textContent.toLowerCase();
                card.style.display = setName.includes(query) ? "block" : "none";
            });
        });
    });

    function toggleFavorite(button) {
        const setId = button.getAttribute('data-set-id');
        fetch(`/main/toggleFavorite`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ setId })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    button.classList.toggle('active', data.isFavorite);
                    location.reload();
                } else {
                    alert(data.message || 'Error al actualizar el favorito.');
                }
            })
            .catch(() => alert('Error al conectar con el servidor.'));
    }
</script>