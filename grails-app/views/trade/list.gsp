<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 5/5/25
  Time: 10:24
--%>

<meta name="layout" content="main"/>
<h2>Solicitudes de Intercambio</h2>
<div class="trade-requests">
            requesterCard.save(flush: true, failOnError: true)
            targetCard.save(flush: true, failOnError: true)

            tradeRequest.status = "ACCEPTED"
            tradeRequest.save(flush: true, failOnError: true)

            render([success: true, message: "Intercambio realizado con éxito"] as JSON)
        } else if (response == "REJECT") {
            tradeRequest.status = "REJECTED"
            tradeRequest.save(flush: true, failOnError: true)

            render([success: true, message: "Intercambio rechazado"] as JSON)
        } else {
            render([success: false, message: "Respuesta no válida"] as JSON)
        }
    }

    def listarSolicitudes() {
    <g:each in="${solicitudes}" var="solicitud">
        <div class="trade-request">
            <p>Usuario: ${solicitud.requester.username}</p>
            <p>Carta ofrecida: ${solicitud.requesterCard.name} (${solicitud.requesterCard.rarity})</p>
            <p>Carta solicitada: ${solicitud.targetCard.name} (${solicitud.targetCard.rarity})</p>
            <g:link action="responderIntercambio" params="[tradeRequestId: solicitud.id, response: 'ACCEPT']">Aceptar</g:link>
            <g:link action="responderIntercambio" params="[tradeRequestId: solicitud.id, response: 'REJECT']">Rechazar</g:link>
        </div>
    </g:each>
</div>