<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 8/5/25
  Time: 10:24
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Solicitudes de intercambio</title>
</head>
<body>
<h1>Solicitudes recibidas</h1>

<g:if test="${solicitudes}">
    <ul>
        <g:each in="${solicitudes}" var="s">
            <li>
                <strong>${s.requester.username}</strong> quiere intercambiar
            su carta <strong>${s.miCarta.nombre}</strong>
                por tu carta <strong>${s.cartaDeseada.nombre}</strong>.

                <g:form controller="trade" action="responderIntercambio">
                    <input type="hidden" name="tradeRequestId" value="${s.id}" />
                    <button name="response" value="ACEPTAR">Aceptar</button>
                    <button name="response" value="RECHAZAR">Rechazar</button>
                </g:form>
            </li>
        </g:each>
    </ul>
</g:if>
<g:else>
    <p>No tienes solicitudes pendientes.</p>
</g:else>

<br/>
<g:link controller="usuario" action="panel">← Volver al panel</g:link>
</body>
</html>
