<meta name="layout" content="main"/>
<h2 class="page-title">Selecciona 4 Pokémon</h2>

<g:if test="${flash.message}">
    <div class="flash-message">
        ${flash.message}
    </div>
</g:if>

<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f9f9f9;
        margin: 0;
        padding: 0;
    }
    .page-title {
        text-align: center;
        color: #333;
        margin-top: 20px;
        font-size: 2rem;
    }
    .flash-message {
        color: red;
        font-weight: bold;
        margin-bottom: 20px;
        text-align: center;
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
    .card-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
        gap: 20px;
        justify-items: center;
        padding: 20px;
    }
    .card-item {
        background: #fff;
        border: 2px solid transparent;
        border-radius: 10px;
        padding: 15px;
        text-align: center;
        cursor: pointer;
        transition: transform 0.3s ease, box-shadow 0.3s ease, border-color 0.3s;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    }
    .card-item img {
        width: 120px;
        height: auto;
        border-radius: 10px;
        margin-bottom: 10px;
    }
    .card-item:hover {
        transform: scale(1.05);
        box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
    }
    .card-item.selected {

        border-radius: 10px;
        border: 5px solid #ffcb05;
        transform: scale(1.05);
    }
    .submit-button {
        display: block;
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
        border: none;
        cursor: pointer;
    }
    .submit-button:disabled {
        background-color: #ccc;
        cursor: not-allowed;
    }
    .submit-button:hover:enabled {
        transform: scale(1.05);
        box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
    }
</style>

<div class="search-bar">
    <input type="text" id="searchInput" placeholder="Buscar Pokémon...">
</div>

<g:form controller="battle" action="startBattle" method="POST" id="cardForm">
    <input type="hidden" name="setId" value="${setId}"/>
    <div class="card-grid" id="cardGrid">
        <g:each in="${userCards}" var="card" status="index">
            <div class="card-item" data-card-id="${card.cardId}" id="card-${index}">
                <img src="${card.imageUrl}" alt="${card.name}"/>

                <p>${card.name}</p>
            </div>
        </g:each>
    </div>
    <input type="hidden" name="selectedCards" id="selectedCards"/>
    <button type="submit" disabled id="submitButton" class="submit-button">Iniciar Combate</button>
</g:form>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        const searchInput = document.getElementById("searchInput");
        const cardGrid = document.getElementById("cardGrid");
        const cardItems = cardGrid.querySelectorAll(".card-item");
        const selectedCardsInput = document.getElementById("selectedCards");
        const submitButton = document.getElementById("submitButton");
        const maxSelection = 4;
        let selectedCards = [];

        // Search functionality
        searchInput.addEventListener("input", () => {
            const query = searchInput.value.toLowerCase();
            cardItems.forEach(card => {
                const cardName = card.querySelector("p").textContent.toLowerCase();
                card.style.display = cardName.includes(query) ? "block" : "none";
            });
        });

        // Card selection functionality
        cardItems.forEach(card => {
            card.addEventListener("click", () => {
                const cardId = card.getAttribute("data-card-id");
                if (selectedCards.includes(cardId)) {
                    selectedCards = selectedCards.filter(id => id !== cardId);
                    card.classList.remove("selected");
                } else if (selectedCards.length < maxSelection) {
                    selectedCards.push(cardId);
                    card.classList.add("selected");
                }
                selectedCardsInput.value = selectedCards.join(",");
                submitButton.disabled = selectedCards.length !== maxSelection;
            });
        });
    });
</script>