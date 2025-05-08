<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="main"/>
<h2 class="page-title">Seleccionar Cartas</h2>
<style>
    .page-title {
        text-align: center;
        color: #333;
        margin-top: 20px;
        font-size: 2rem;
    }
    .cards-select-container {
        display: flex;
        gap: 40px;
        justify-content: center;
        margin: 30px 0;
        flex-wrap: wrap;
    }
    .card-list {
        background: #fff;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        padding: 20px;
        min-width: 220px;
    }
    .card-list h3 {
        text-align: center;
        margin-bottom: 15px;
        color: #ef5350;
    }
    .card-option {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 12px;
        padding: 6px 0;
        border: 2px solid transparent;
        border-radius: 8px;
        transition: border 0.2s;
    }
    .card-option.selected {
        border: 5px solid red;
    }
    .card-option img {
        width: 60px;
        height: 60px;
        object-fit: contain;
        border-radius: 8px;
        background: #f9f9f9;
        border: 1px solid #eee;
    }
    .card-option label {
        flex: 1;
        font-size: 1rem;
    }
    .card-option:has(input:checked) {
        border: 5px solid red;
    }
    .submit-btn {
        display: block;
        margin: 30px auto 0 auto;
        background: #ef5350;
        color: #fff;
        font-weight: bold;
        border: none;
        border-radius: 8px;
        padding: 12px 30px;
        font-size: 1.1rem;
        cursor: pointer;
        transition: background 0.2s;
    }
    .submit-btn:hover {
        background: #ffcb05;
        color: #333;
    }
</style>
<p style="text-align:center;">
    Intercambiando con <strong>${targetUser.username}</strong> del set <strong>${set.name}</strong>
</p>
<g:form controller="trade" action="solicitarIntercambio" method="post">
    <input type="hidden" name="targetUserId" value="${targetUser.id}"/>

    <div class="cards-select-container">
        <div class="card-list">
            <h3>Tu carta</h3>
            <input type="text" id="searchUserCards" placeholder="Buscar tu carta..." oninput="filterCards('userCards', this.value)" />
            <div id="userCards">
                <g:each in="${cartasUsuario}" var="carta" status="i">
                    <label class="card-option" for="miCarta${i}">
                        <input type="radio" name="cardId" id="miCarta${i}" value="${carta.cardId}" required="${i == 0}" style="display: none;"/>
                        <img src="${carta.imageUrl ?: '/images/default.png'}" alt="${carta.name}"/>
                        <span>
                            ${carta.name} (x${carta.quantity})<br/>
                            <small style="color: #888;">Rareza: ${carta.rarity}</small>
                        </span>
                    </label>
                </g:each>
            </div>
        </div>
        <div class="card-list">
            <h3>Carta del usuario</h3>
            <input type="text" id="searchTargetCards" placeholder="Buscar carta del usuario..." oninput="filterCards('targetCards', this.value)" />
            <div id="targetCards">
                <g:each in="${cartasTarget}" var="carta" status="j">
                    <label class="card-option" for="targetCarta${j}">
                        <input type="radio" name="targetCardId" id="targetCarta${j}" value="${carta.cardId}" required="${j == 0}" style="display: none;"/>
                        <img src="${carta.imageUrl ?: '/images/default.png'}" alt="${carta.name}"/>
                        <span>
                            ${carta.name} (x${carta.quantity})<br/>
                            <small style="color: #888;">Rareza: ${carta.rarity}</small>
                        </span>
                    </label>
                </g:each>
            </div>
        </div>
    </div>
    <button type="submit" class="submit-btn">Enviar solicitud</button>
</g:form>

<script>
    function filterCards(containerId, searchText) {
        const container = document.getElementById(containerId);
        const cards = container.querySelectorAll('.card-option');
        const lowerSearchText = searchText.toLowerCase();

        cards.forEach(card => {
            const cardText = card.textContent.toLowerCase();
            card.style.display = cardText.includes(lowerSearchText) ? '' : 'none';
        });
    }
</script>