<meta name="layout" content="main"/>
<h2 class="page-title">Vender Carta</h2>
<style>
.page-title {
    text-align: center;
    font-size: 2rem;
    margin: 20px 0;
    color: #333;
}

.filter-container {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-bottom: 20px;
}

.filter-container select,
.filter-container input {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 1rem;
}

.sell-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
    padding: 20px;
}
.card-slot p {
    max-width: 180px; /* Limita el ancho del texto */
    white-space: normal; /* Permite que el texto ocupe varias líneas */
    overflow: hidden; /* Oculta cualquier desbordamiento */
    text-overflow: ellipsis; /* Agrega puntos suspensivos si es necesario */
    margin: 5px 0; /* Espaciado entre los párrafos */
    word-wrap: break-word; /* Permite que las palabras largas se dividan */
    font-size: 1rem; /* Ajusta el tamaño del texto */
}

.card-slot .quantity {
    font-size: 0.9rem; /* Reduce ligeramente el tamaño del texto */
    color: #555;
    margin: 5px 0;
    background: rgba(0, 0, 0, 0.1); /* Fondo para destacar la cantidad */
    padding: 2px 6px; /* Espaciado interno */
    border-radius: 5px; /* Bordes redondeados */
    display: inline-block; /* Asegura que no ocupe toda la línea */
}

.card-slot {
    text-align: center;
    background: #f0f0f0;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 400px; /* Aumentar la altura */
    position: relative;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card-slot:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

.card-slot img {
    height: 180px; /* Ajustar el tamaño de la imagen */
    border-radius: 10px;
    margin-bottom: 10px;
    cursor: pointer;
}

.card-slot .quantity {
    font-size: 1rem;
    color: #555;
    margin: 5px 0;
}

.card-slot .sell-button {
    margin-top: 10px;
    background-color: #ffcb05;
    color: #333;
    padding: 10px 15px;
    border: none;
    border-radius: 8px;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.card-slot .sell-button:hover {
    background-color: #ef5350;
    color: white;
}
</style>

<div class="filter-container">
    <form method="GET" action="${createLink(controller: 'market', action: 'venderCarta')}">
        <select name="setName" onchange="this.form.submit()">
            <option value="">Seleccionar Set</option>
            <g:each in="${availableSets}" var="set">
                <option value="${set}" ${params.setName == set ? 'selected' : ''}>${set}</option>
            </g:each>
        </select>
    </form>
    <input type="text" id="searchInput" placeholder="Buscar carta por nombre..." onkeyup="filterCards()" />
</div>

<div class="sell-grid" id="sellGrid">
    <g:each in="${filteredCards}" var="card">
        <div class="card-slot" data-name="${card.name.toLowerCase()}">
            <img src="${card.imageUrl}" alt="${card.name}" />
            <p><strong>${card.name}</strong></p>
            <p class="quantity">Cantidad: x${card.quantity}</p>
            <form action="${createLink(controller: 'market', action: 'sell')}" method="POST">
                <input type="hidden" name="cardId" value="${card.cardId}" />
                <label for="price-${card.cardId}">Precio:</label>
                <input type="number" id="price-${card.cardId}" name="price" min="1" required />
                <label for="duration-${card.cardId}">Duración:</label>
                <select id="duration-${card.cardId}" name="duration">
                    <option value="5">5 minutos</option>
                    <option value="10">10 minutos</option>
                    <option value="15">15 minutos</option>
                </select>
                <button type="submit" class="sell-button">Poner en venta</button>
            </form>
        </div>
    </g:each>
</div>

<script>
function filterCards() {
    const searchInput = document.getElementById("searchInput").value.toLowerCase();
    const cards = document.querySelectorAll(".card-slot");

    cards.forEach(card => {
        const name = card.getAttribute("data-name");
        card.style.display = name.includes(searchInput) ? "block" : "none";
    });
}
</script>