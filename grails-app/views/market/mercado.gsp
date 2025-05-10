<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 10/5/25
  Time: 12:36
--%>
<meta name="layout" content="main"/>
<h2>Mercado</h2>
<div style="margin-bottom: 20px;">
    <g:link controller="market" action="venderCarta" class="sell-button">Vender Carta</g:link>
</div>
<div class="market-grid">
    <g:each in="${listings}" var="listing">
        <div class="card-slot">
            <img src="${listing.seller.cards.find { it.cardId == listing.cardId }?.imageUrl}" alt="${listing.seller.cards.find { it.cardId == listing.cardId }?.name}" />
            <p><strong>${listing.seller.cards.find { it.cardId == listing.cardId }?.name}</strong></p>
            <p>Precio: ${listing.price} Pokémonedas</p>
            <p>Vendedor: ${listing.seller.username}</p>
            <g:if test="${listing.expirationDate}">
                <p>Expira en:
                    <span class="countdown">
                        ${grailsApplication.mainContext.getBean('org.grails.plugins.web.taglib.FormTagLib').formatDate(date: listing.expirationDate, format: 'dd/MM/yyyy HH:mm')}
                    </span>
                </p>
            </g:if>
            <g:if test="${listing.seller?.username == currentUser?.username}">
                <p style="color: gray;">Esta carta es tuya</p>
            </g:if>
            <g:else>
                <!-- Botón de compra vinculado al cardId -->
                <form action="${createLink(controller: 'market', action: 'buy', id: listing.cardId)}" method="POST">
                    <button type="submit" class="buy-button">
                        Comprar
                    </button>
                </form>
            </g:else>
        </div>
    </g:each>
</div>