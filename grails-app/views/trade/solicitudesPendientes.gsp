<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 5/5/25
  Time: 10:24
--%>

<h2>Solicitudes de Intercambio</h2>
<g:each in="${solicitudes}" var="s">
    <div>
        <p>De: ${s.requester.username}</p>
        <p>Ofrece: ${s.requesterCard.name}</p>
        <p>Solicita: ${s.targetCard.name}</p>
        <g:link action="responderIntercambio" params="[tradeRequestId: s.id, response: 'ACCEPT']">Aceptar</g:link>
        <g:link action="responderIntercambio" params="[tradeRequestId: s.id, response: 'REJECT']">Rechazar</g:link>
    </div>
</g:each>
