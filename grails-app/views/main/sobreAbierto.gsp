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
    position: relative; /* Ensure the "NUEVO" label is positioned correctly */
}
.card img {
    height: 220px;
    border-radius: 10px;
    margin-bottom: 10px;
    cursor: pointer;
}
.card:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
}

/* Estilos para el modal */
.modal {
    display: none;
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.8);
    justify-content: center;
    align-items: center;
}
.modal img {
    max-width: 90%;
    max-height: 90%;
    border-radius: 10px;
}

/* Estilo del botón */
.back-button {
    display: inline-block;
    background-color: #ffcb05;
    padding: 10px 20px;
    border-radius: 8px;
    text-decoration: none;
    font-weight: bold;
    color: #333;
    text-align: center;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    margin: 0; /* Elimina márgenes para que los botones estén pegados */
}

.back-button:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

/* Estilo para la lista de botones */
ul {
    list-style: none;
    padding: 0;
    display: flex;
    justify-content: center;
    gap: 10px; /* Separación pequeña entre los botones */
    margin-top: 20px;
}
</style>

<div class="cards-container">
    <g:each in="${cards}" var="card">
        <div class="card">
            <g:if test="${card.isNew}">
                <div style="position: absolute; background-color: #ff0000; color: white; padding: 5px; border-radius: 5px; top: 10px; left: 10px; font-size: 0.8rem;">
                    NUEVO
                </div>
            </g:if>
            <img src="${card.imageUrl ?: '/images/default.png'}" alt="${card.name}" class="clickable-image"/>
            <p>${card.name}</p>
            <small>${set?.name}</small>
            <small>Rareza: ${card.rarity ?: 'Energia'}</small>
        </div>
    </g:each>
</div>

<!-- Modal para mostrar la imagen ampliada -->
<div class="modal" id="imageModal">
    <img id="modalImage" src="" alt="Imagen ampliada"/>
</div>

<ul>
    <li><g:link controller="Main" action="abrirSobres" class="back-button">Volver al menú</g:link></li>
    <!-- Botón de "Abrir otro sobre" -->
    <li><a href="#" class="back-button" onclick="window.location.reload(); return false;">Abrir otro sobre</a></li>
</ul>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const modal = document.getElementById("imageModal");
        const modalImage = document.getElementById("modalImage");
        const images = document.querySelectorAll(".clickable-image");

        images.forEach(image => {
            image.addEventListener("click", function () {
                modalImage.src = this.src;
                modal.style.display = "flex";
            });
        });

        modal.addEventListener("click", function () {
            modal.style.display = "none";
        });
    });
</script>
