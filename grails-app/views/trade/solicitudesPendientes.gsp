<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 5/5/25
  Time: 10:24
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="main"/>
<h2 class="page-title">Solicitudes de Intercambio</h2>
<style>
    .solicitudes-list {
        max-width: 600px;
        margin: 30px auto;
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 4px 16px rgba(0,0,0,0.08);
        padding: 30px 25px;
    }
    .solicitud {
        border-bottom: 1px solid #eee;
        padding: 18px 0;
    }
    .solicitud:last-child {
        border-bottom: none;
    }
    .solicitud strong {
        color: #ef5350;
    }
    .solicitud-actions {
        margin-top: 10px;
    }
    .solicitud-actions a {
        background: #ef5350;
        color: #fff;
        padding: 7px 18px;
        border-radius: 6px;
        margin-right: 10px;
        text-decoration: none;
        font-weight: bold;
        transition: background 0.2s;
    }
    .solicitud-actions a:hover {
        background: #ffcb05;
        color: #333;
    }
    .no-solicitudes {
        text-align: center;
        color: #888;
        margin-top: 30px;
    }
</style>
<div class="solicitudes-list">
    <g:if test="${solicitudes && solicitudes.size() > 0}">
        <g:each in="${solicitudes}" var="s">
            <div class="solicitud">
                <p>
                    <strong>${s.requester?.username ?: 'Usuario desconocido'}</strong> ofrece
                    <strong>${s.requesterCard?.name ?: 'Carta eliminada'}</strong>
                    por tu carta
                    <strong>${s.targetCard?.name ?: 'Carta eliminada'}</strong>
                </p>
                <g:if test="${s.requesterCard && s.targetCard}">
                    <div class="solicitud-actions">
                        <g:link action="responderIntercambio" params="[tradeRequestId: s.id, response: 'ACCEPT']">Aceptar</g:link>
                        <g:link action="responderIntercambio" params="[tradeRequestId: s.id, response: 'REJECT']">Rechazar</g:link>
                    </div>
                </g:if>
                <g:else>
                    <p><em>Esta solicitud no se puede procesar porque falta una carta.</em></p>
                </g:else>
            </div>
        </g:each>
    </g:if>
    <g:else>
        <div class="no-solicitudes">
            No tienes solicitudes pendientes.
        </div>
    </g:else>
</div>