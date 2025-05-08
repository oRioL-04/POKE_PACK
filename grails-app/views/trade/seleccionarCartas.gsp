<meta name="layout" content="main"/>
<h2>Seleccionar Cartas</h2>

<p>Intercambiando con <strong>${targetUser.username}</strong> del set <strong>${set.name}</strong></p>

<g:form controller="trade" action="solicitarIntercambio" method="post">
    <input type="hidden" name="targetUserId" value="${targetUser.id}"/>

    <label>Tu carta:</label>
    <g:select name="cardId" from="${cartasUsuario}" optionKey="cardId" optionValue="name" required="true"/>

    <label>Carta del usuario:</label>
    <g:select name="targetCardId" from="${cartasTarget}" optionKey="cardId" optionValue="name" required="true"/>

    <g:submitButton name="submit" value="Enviar solicitud"/>
</g:form>
