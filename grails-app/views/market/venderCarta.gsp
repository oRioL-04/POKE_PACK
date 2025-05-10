<meta name="layout" content="main"/>
<h2>Vender Carta</h2>
<form id="sellForm" action="${createLink(controller: 'market', action: 'sell')}" method="POST">
    <label for="set">Selecciona un set:</label>
    <select id="set" name="set" required>
        <g:each in="${setsWithCards.keySet()}" var="set">
            <option value="${set}">${set}</option>
        </g:each>
    </select>

    <label for="cardId">Selecciona una carta:</label>
    <select id="cardId" name="cardId" required>
        <g:each in="${setsWithCards}" var="entry">
            <optgroup label="${entry.key}">
                <g:each in="${entry.value}" var="card">
                    <option value="${card.cardId}">${card.name} (x${card.quantity})</option>
                </g:each>
            </optgroup>
        </g:each>
    </select>

    <label for="price">Precio:</label>
    <input type="number" id="price" name="price" min="1" required />

    <label for="duration">Duración (días):</label>
    <select id="duration" name="duration">
        <option value="1">1 día</option>
        <option value="3">3 días</option>
        <option value="7">7 días</option>
    </select>

    <button type="submit">Poner en venta</button>
</form>