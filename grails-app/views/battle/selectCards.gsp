<meta name="layout" content="main"/>
<h2>Selecciona 4 Pokémon</h2>
<style>
    .card-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 20px;
        justify-items: center;
    }
    .card-item {
        border: 2px solid transparent;
        border-radius: 10px;
        padding: 10px;
        text-align: center;
        cursor: pointer;
        transition: border-color 0.3s, transform 0.2s;
    }
    .card-item img {
        width: 100px;
        height: auto;
    }
    .card-item.selected {
        border-color: #ffcb05;
        transform: scale(1.05);
    }
</style>

<g:form controller="Battle" action="startBattle" method="POST" id="cardForm">
    <input type="hidden" name="setId" value="${setId}"/>
    <div class="card-grid">
        <g:each in="${userCards}" var="card" status="index">
            <div class="card-item" data-card-id="${card.cardId}" id="card-${index}">
                <img src="${card.imageUrl}" alt="${card.name}" />
                <p>${card.name}</p>
            </div>
        </g:each>
    </div>
    <input type="hidden" name="selectedCards" id="selectedCards" />
    <button type="submit" disabled id="submitButton">Iniciar Combate</button>
</g:form>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        const cardItems = document.querySelectorAll(".card-item");
        const selectedCardsInput = document.getElementById("selectedCards");
        const submitButton = document.getElementById("submitButton");
        const maxSelection = 4;
        let selectedCards = [];

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