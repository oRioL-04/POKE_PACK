package com.pokemon

import grails.converters.JSON

class TradeController {

    def solicitarIntercambio() {
        def user = User.get(session.userId)
        if (!user) {
            render([success: false, message: "Usuario no autenticado"] as JSON)
            return
        }

        def cardId = params.cardId
        def targetUserId = params.targetUserId
        def targetCardId = params.targetCardId

        if (!cardId || !targetUserId || !targetCardId) {
            render([success: false, message: "Datos incompletos para el intercambio"] as JSON)
            return
        }

        def userCard = user.cards.find { it.cardId?.toString() == cardId }
        def targetUser = User.get(targetUserId as Long)
        def targetCard = targetUser?.cards?.find { it.cardId?.toString() == targetCardId }

        if (!userCard || !targetCard) {
            render([success: false, message: "Cartas o usuario no encontrados"] as JSON)
            return
        }

        if (userCard.rarity != targetCard.rarity) {
            render([success: false, message: "Las cartas deben tener la misma rareza para ser intercambiadas"] as JSON)
            return
        }

        def tradeRequest = new TradeRequest(
                requester: user,
                targetUser: targetUser,
                requesterCard: userCard,
                targetCard: targetCard,
                status: "PENDING"
        )
        tradeRequest.save(flush: true, failOnError: true)

        render([success: true, message: "Solicitud de intercambio enviada"] as JSON)
    }


    def mostrarFormularioIntercambio() {
        def user = User.get(session.userId)
        if (!user) {
            flash.message = "Debes iniciar sesión para acceder a esta página."
            redirect(controller: "auth", action: "index")
            return
        }

        render(view: "solicitarIntercambio", model: [currentUser: user])
    }

    def obtenerSets() {
        def sets = Set.list()
        render(sets as JSON)
    }

    def obtenerCartasPorSet(String setId) {
        def user = User.get(session.userId)
        if (!user) {
            render([success: false, message: "Usuario no autenticado"] as JSON)
            return
        }

        // Filtrar las cartas con propiedades válidas
        def cartas = Card.findAllByOwnerAndSetNameAndQuantityGreaterThan(user, Set.findBySetId(setId)?.name, 1)
        cartas = cartas.findAll { it?.name && it?.quantity }

        // Si no hay cartas válidas, puedes retornar un mensaje adecuado o una lista vacía
        if (!cartas) {
            render([success: false, message: "No hay cartas válidas para este set."] as JSON)
            return
        }

        render(cartas as JSON)
    }





    def obtenerCartasDeUsuario(Long userId, String setId) {
        def targetUser = User.get(userId)
        if (!targetUser) {
            render([success: false, message: "Usuario no encontrado"] as JSON)
            return
        }

        def cartas = Card.findAllByOwnerAndSetNameAndQuantityGreaterThan(targetUser, Set.findBySetId(setId)?.name, 1)
        def cartasValidas = cartas.findAll { it?.name && it?.quantity } // Filtrar cartas con datos válidos
        render(cartasValidas as JSON)
    }

    def obtenerUsuarios() {
        def usuarios = User.list().findAll { it.id != session.userId }
        render(usuarios as JSON)
    }

    def responderIntercambio(Long tradeRequestId, String response) {
        def user = User.get(session.userId)
        if (!user) {
            render([success: false, message: "Usuario no autenticado"] as JSON)
            return
        }

        def tradeRequest = TradeRequest.get(tradeRequestId)
        if (!tradeRequest || tradeRequest.targetUser.id != user.id) {
            render([success: false, message: "Solicitud de intercambio no válida"] as JSON)
            return
        }

        if (response == "ACCEPT") {
            // Realizar el intercambio
            def requesterCard = tradeRequest.requesterCard
            def targetCard = tradeRequest.targetCard

            // Transferir las cartas
            requesterCard.owner = tradeRequest.targetUser
            targetCard.owner = tradeRequest.requester

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
        def user = User.get(session.userId)
        if (!user) {
            render([success: false, message: "Usuario no autenticado"] as JSON)
            return
        }

        def solicitudes = TradeRequest.findAllByTargetUserAndStatus(user, "PENDING")
        render([success: true, solicitudes: solicitudes] as JSON)
    }
}
