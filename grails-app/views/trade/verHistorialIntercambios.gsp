<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 8/5/25
  Time: 10:24
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Historial de intercambios</title>
</head>
<body>
<h1>Historial de Intercambios</h1>

<g:if test="${intercambios}">
    <ul>
        <g:each in="${intercambios}" var="i">
            <li>
                <strong>${i.requester.username}</strong> ofreció
                <strong>${i.miCarta.nombre}</strong> a
                <strong>${i.targetUser.username}</strong>
                por <strong>${i.cartaDeseada.nombre}</strong>.
            Estado: <em>${i.estado}</em>
            </li>
        </g:each>
    </ul>
</g:if>
<g:else>
    <p>No hay intercambios registrados.</p>
</g:else>

<br/>
<g:link controller="usuario" action="panel">← Volver al panel</g:link>
</body>
</html>
