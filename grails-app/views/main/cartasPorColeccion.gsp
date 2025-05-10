<meta name="layout" content="main"/>
<h2>Álbum de la colección ${set.name}</h2>
<style>
/* Estilos existentes */
.back-button {
    display: inline-block;
    margin: 20px auto;
    background-color: #ffcb05;
    padding: 10px 20px;
    border-radius: 8px;
    text-decoration: none;
    font-weight: bold;
    color: #333;
    text-align: center;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.back-button:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

.album-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 20px;
    padding: 20px;
}
.card-slot {
    text-align: center;
    background: #f0f0f0;
    border-radius: 10px;
    padding: 15px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 200px;
    position: relative;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.card-slot:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}
.card-slot img {
    height: 120px;
    border-radius: 10px;
    margin-bottom: 10px;
    cursor: pointer;
}
.card-slot.obtained {
    border: 3px solid #4caf50; /* Borde verde para cartas obtenidas */
}
.card-slot .missing {
    font-size: 1.2rem;
    color: #888;
    font-weight: bold;
}
.card-slot .quantity {
    position: absolute;
    bottom: 10px;
    right: 10px;
    background: rgba(0, 0, 0, 0.7);
    color: white;
    font-size: 0.9rem;
    padding: 2px 6px;
    border-radius: 5px;
}

/* Estilo del buscador */
.search-bar {
    margin: 20px auto;
    text-align: center;
}
.search-bar input {
    width: 50%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 1rem;
}
</style>

<div class="search-bar">
    <input type="text" id="searchInput" placeholder="Buscar Pokémon por nombre..."/>
</div>

<div class="album-grid" id="albumGrid">
    <g:each in="${1..set.totalCards}" var="cardNumber">
        <g:if test="${cards.find { it.cardNumber?.toInteger() == cardNumber }?.quantity > 0}">
            <div class="card-slot obtained" data-name="${cards.find { it.cardNumber?.toInteger() == cardNumber }?.name?.toLowerCase()}">
                <img src="${cards.find { it.cardNumber?.toInteger() == cardNumber }?.imageUrl ?: '/images/default.png'}"
                     alt="Carta ${cardNumber}"
                     class="clickable-image"/>
                <p>${cards.find { it.cardNumber?.toInteger() == cardNumber }?.name}</p>
                <div class="quantity">x${cards.find { it.cardNumber?.toInteger() == cardNumber }?.quantity}</div>
            </div>
        </g:if>
        <g:else>
            <div class="card-slot" data-name="">
                <div class="missing">#${cardNumber}</div>
            </div>
        </g:else>
    </g:each>
</div>

<div style="display: flex; justify-content: center; margin-top: 20px;">
    <g:link controller="Main" action="pokedex" class="back-button">Volver a la Pokédex</g:link>
</div>
<!-- Modal para mostrar la imagen en grande -->
<div id="imageModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.8); justify-content: center; align-items: center; z-index: 1000;">
    <span id="closeModal" style="position: absolute; top: 20px; right: 20px; font-size: 2rem; color: white; cursor: pointer;">&times;</span>
    <img id="modalImage" src="" alt="Carta" style="max-width: 90%; max-height: 90%; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);" />
</div>

<script>
document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("imageModal");
    const modalImage = document.getElementById("modalImage");
    const closeModal = document.getElementById("closeModal");

    // Agregar evento a todas las imágenes de las cartas
    document.querySelectorAll(".card-slot img").forEach(img => {
        img.addEventListener("click", function () {
            modalImage.src = this.src; // Establecer la imagen en el modal
            modal.style.display = "flex"; // Mostrar el modal
        });
    });

    // Cerrar el modal al hacer clic en la "X"
    closeModal.addEventListener("click", function () {
        modal.style.display = "none";
    });

    // Cerrar el modal al hacer clic fuera de la imagen
    modal.addEventListener("click", function (e) {
        if (e.target === modal) {
            modal.style.display = "none";
        }
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("searchInput");
    const cards = document.querySelectorAll(".card-slot");

    searchInput.addEventListener("input", function () {
        const searchValue = searchInput.value.toLowerCase();
        cards.forEach(card => {
            const name = card.getAttribute("data-name");
            card.style.display = name.includes(searchValue) ? "" : "none";
        });
    });
});
</script>