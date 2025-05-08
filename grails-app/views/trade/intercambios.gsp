<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 5/8/25
  Time: 4:26 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="main"/>
<h2 class="page-title">Gestión de Intercambios</h2>
<style>
    .action-buttons {
        display: flex;
        justify-content: center;
        gap: 20px;
        margin-top: 20px;
    }
    .action-buttons a {
        background: #ef5350;
        color: #fff;
        padding: 10px 20px;
        border-radius: 6px;
        text-decoration: none;
        font-weight: bold;
        transition: background 0.3s;
    }
    .action-buttons a:hover {
        background: #ffcb05;
        color: #333;
    }
    .historial-container {
        max-width: 600px;
        margin: 30px auto;
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 4px 16px rgba(0,0,0,0.08);
        padding: 20px;
    }
    .historial-container ul {
        list-style: none;
        padding: 0;
    }
    .historial-container li {
        border-bottom: 1px solid #eee;
        padding: 10px 0;
    }
    .historial-container li:last-child {
        border-bottom: none;
    }
</style>

<div class="historial-container">
    <h3>Historial de Intercambios</h3>
    <g:if test="${intercambios}">
        <ul>
            <g:each in="${intercambios}" var="i">
                <li>
                    <strong>${i.requester?.username ?: 'Usuario desconocido'}</strong> ofreció
                    <img src="${i.requesterCard?.imageUrl ?: '/images/default.png'}" alt="Carta no disponible" style="height: 60px; vertical-align: middle; border-radius: 8px; margin: 0 10px;"/>
                    a <strong>${i.targetUser?.username ?: 'Usuario desconocido'}</strong>
                    por <img src="${i.targetCard?.imageUrl ?: '/images/default.png'}" alt="Carta no disponible" style="height: 60px; vertical-align: middle; border-radius: 8px; margin: 0 10px;"/>.
                    Estado: <em>${i.status ?: 'Estado desconocido'}</em>
                </li>
            </g:each>
        </ul>
    </g:if>
    <g:else>
        <p>No hay intercambios registrados.</p>
    </g:else>
</div>

<div class="action-buttons">
    <g:link controller="trade" action="mostrarFormularioIntercambio">Iniciar Nuevo Intercambio</g:link>
    <g:link controller="trade" action="listarSolicitudes">Ver Solicitudes de Intercambio</g:link>
</div>