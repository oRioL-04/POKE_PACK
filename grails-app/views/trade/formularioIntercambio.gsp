<meta name="layout" content="main"/>
<h2>Iniciar Intercambio</h2>

<g:form controller="trade" action="cargarCartasIntercambio" method="get">
    <label>Selecciona el usuario con quien intercambiar:</label>
    <g:select name="targetUserId" from="${usuarios}" optionKey="id" optionValue="username" required="true"/>

    <label>Selecciona el set:</label>
    <g:select name="setId" from="${sets}" optionKey="setId" optionValue="name" required="true"/>

    <g:submitButton name="submit" value="Siguiente"/>
</g:form>
