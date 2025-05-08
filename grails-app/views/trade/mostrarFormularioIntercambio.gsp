<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 8/5/25
  Time: 10:23
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Intercambiar cartas</title>
</head>
<body>
<h1>Solicitar intercambio con ${targetUser.username}</h1>

<g:form controller="trade" action="solicitarIntercambio">
    <input type="hidden" name="targetUserId" value="${targetUser.id}" />

    <h3>Tu carta para ofrecer:</h3>
    <select name="miCartaId">
        <g:each in="${misCartas}" var="carta">
            <option value="${carta.id}">${carta.nombre} (x${carta.cantidad})</option>
        </g:each>
    </select>

    <h3>Carta que deseas del usuario:</h3>
    <select name="cartaDeseadaId">
        <g:each in="${cartasTarget}" var="carta">
            <option value="${carta.id}">${carta.nombre} (x${carta.cantidad})</option>
        </g:each>
    </select>

    <br/><br/>
    <g:submitButton name="enviar" value="Enviar solicitud" />
</g:form>

<br/>
<g:link controller="trade" action="mostrarUsuariosParaIntercambio">← Volver</g:link>
</body>
</html>
