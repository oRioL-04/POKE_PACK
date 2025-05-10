<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 10/5/25
  Time: 12:36
--%>
<meta name="layout" content="main"/>
<h2 class="page-title">Mercado</h2>
<style>
.page-title {
    text-align: center;
    font-size: 2rem;
    margin: 20px 0;
    color: #333;
}

.sell-button-container {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;
}

.market-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
    padding: 20px;
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
    height: 320px;
    position: relative;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card-slot:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

.card-slot img {
    height: 150px;
    border-radius: 10px;
    margin-bottom: 10px;
    cursor: pointer;
}

.card-slot .price {
    font-size: 1.2rem;
    color: #333;
    font-weight: bold;
    margin: 5px 0;
}

.card-slot .seller {
    font-size: 1rem;
    color: #555;
}

.card-slot .expiration {
    font-size: 0.9rem;
    color: #888;
    margin-top: 5px;
}

.card-slot .buy-button {
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

.card-slot .buy-button:hover {
    background-color: #ef5350;
    color: white;
}

.sell-button {
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

.sell-button:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}
</style>

<div class="sell-button-container">
    <g:link controller="market" action="venderCarta" class="sell-button">Vender Carta</g:link>
</div>

<div class="market-grid">
    <g:each in="${listings}" var="listing">
        <div class="card-slot">
            <img src="${listing.seller.cards.find { it.cardId == listing.cardId }?.imageUrl}" alt="${listing.seller.cards.find { it.cardId == listing.cardId }?.name}" />
            <p><strong>${listing.seller.cards.find { it.cardId == listing.cardId }?.name}</strong></p>
            <p class="price">Precio: ${listing.price} Pokémonedas</p>
            <p class="seller">Vendedor: ${listing.seller.username}</p>
            <g:if test="${listing.expirationDate}">
                <p class="expiration">Expira en:
                    ${grailsApplication.mainContext.getBean('org.grails.plugins.web.taglib.FormTagLib').formatDate(date: listing.expirationDate, format: 'dd/MM/yyyy HH:mm')}
                </p>
            </g:if>
            <g:if test="${listing.seller?.username == currentUser?.username}">
                <p style="color: gray;">Esta carta es tuya</p>
            </g:if>
            <g:else>
                <form action="${createLink(controller: 'market', action: 'buy', id: listing.cardId)}" method="POST">
                    <button type="submit" class="buy-button">Comprar</button>
                </form>
            </g:else>
        </div>
    </g:each>
</div>