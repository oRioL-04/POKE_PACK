<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 8/5/25
  Time: 10:23
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Elegir usuario para intercambio</title>
</head>
<body>
<h1>Usuarios disponibles para intercambio</h1>

<ul>
    <g:each in="${usuarios}" var="u">
        <li>
            ${u.username}
            <g:link controller="trade" action="mostrarFormularioIntercambio" params="[targetUserId: u.id]">
                Iniciar intercambio
            </g:link>
        </li>
    </g:each>
</ul>
</body>
</html>
