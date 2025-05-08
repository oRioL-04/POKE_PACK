package com.pokemon

import grails.converters.JSON

class TradeController {

    def mostrarFormularioIntercambio() {
        def usuarioActual = User.get(session.userId)
        if (!usuarioActual) {
            redirect(controller: 'auth', action: 'index')
            return
        }

        def usuarios = User.findAllByIdNotEqual(usuarioActual.id)
        def sets = Set.list()
        def cartasUsuario = Card.findAllByOwnerAndQuantityGreaterThan(usuarioActual, 0)

        render(view: 'formularioIntercambio', model: [
                usuarios     : usuarios,
                sets         : sets,
                cartasUsuario: cartasUsuario,
                currentUser  : usuarioActual
        ])
    }

    def cargarCartasIntercambio() {
        def usuarioActual = User.get(session.userId)
        if (!usuarioActual) {
            flash.message = "Debe iniciar sesión"
            redirect(controller: 'auth', action: 'index')
            return
        }

        def targetUser = User.get(params.targetUserId as Long)
        def set = Set.findBySetId(params.setId)
        if (!targetUser || !set) {
            flash.message = "Datos inválidos"
            redirect(action: 'mostrarFormularioIntercambio')
            return
        }

        def cartasUsuario = Card.findAllByOwnerAndSetNameAndQuantityGreaterThan(usuarioActual, set.name, 1)
        def cartasTarget = Card.findAllByOwnerAndSetNameAndQuantityGreaterThan(targetUser, set.name, 1)

        render(view: 'seleccionarCartas', model: [
                targetUser   : targetUser,
                set          : set,
                cartasUsuario: cartasUsuario,
                cartasTarget : cartasTarget,
                currentUser  : usuarioActual
        ])
    }

    def solicitarIntercambio() {
        def user = User.get(session.userId)
        if (!user) {
            flash.message = "Usuario no autenticado"
            redirect(controller: 'auth', action: 'index')
            return
        }

        def userCard = Card.findByCardIdAndOwner(params.cardId, user)
        def targetUser = User.get(params.targetUserId as Long)
        def targetCard = Card.findByCardIdAndOwner(params.targetCardId, targetUser)

        if (!userCard || !targetCard || userCard.rarity != targetCard.rarity) {
            flash.message = "Error: Cartas no válidas o con rarezas distintas"
            redirect(action: 'mostrarFormularioIntercambio')
            return
        }

        def solicitud = new TradeRequest(
                requester: user,
                targetUser: targetUser,
                requesterCard: userCard,
                targetCard: targetCard,
                status: "PENDING"
        )

        solicitud.save(flush: true, failOnError: true)
        flash.message = "Solicitud de intercambio enviada"
        redirect(controller: 'main', action: 'menu')
    }

    def responderIntercambio(Long tradeRequestId, String response) {
        def user = User.get(session.userId)
        if (!user) {
            flash.message = "Usuario no autenticado"
            redirect(controller: 'auth', action: 'index')
            return
        }

        def tradeRequest = TradeRequest.get(tradeRequestId)
        if (!tradeRequest || tradeRequest.targetUser.id != user.id) {
            flash.message = "Solicitud de intercambio no válida"
            redirect(controller: 'main', action: 'menu')
            return
        }

        if (response == "ACCEPT") {
            def requester = tradeRequest.requester
            def targetUser = tradeRequest.targetUser
            def requesterCard = tradeRequest.requesterCard
            def targetCard = tradeRequest.targetCard

            // Función auxiliar para buscar carta por owner y cardId
            def getCard = { owner, cardId ->
                Card.findByOwnerAndCardId(owner, cardId)
            }

            // 1. Transferir carta del requester al targetUser
            def cardToTarget = getCard(targetUser, requesterCard.cardId)
            if (cardToTarget) {
                cardToTarget.quantity += 1
                cardToTarget.save(flush: true, failOnError: true)
            } else {
                new Card(
                        cardId: requesterCard.cardId,
                        name: requesterCard.name,
                        rarity: requesterCard.rarity,
                        setName: requesterCard.setName,
                        imageUrl: requesterCard.imageUrl,
                        quantity: 1,
                        owner: targetUser,
                        username: targetUser.username
                ).save(flush: true, failOnError: true)
            }
            requesterCard.quantity -= 1
            requesterCard.save(flush: true, failOnError: true)

            // 2. Transferir carta del targetUser al requester
            def cardToRequester = getCard(requester, targetCard.cardId)
            if (cardToRequester) {
                cardToRequester.quantity += 1
                cardToRequester.save(flush: true, failOnError: true)
            } else {
                new Card(
                        cardId: targetCard.cardId,
                        name: targetCard.name,
                        rarity: targetCard.rarity,
                        setName: targetCard.setName,
                        imageUrl: targetCard.imageUrl,
                        quantity: 1,
                        owner: requester,
                        username: requester.username
                ).save(flush: true, failOnError: true)
            }
            targetCard.quantity -= 1
            targetCard.save(flush: true, failOnError: true)

            // 3. Eliminar la solicitud
            tradeRequest.delete(flush: true)

            flash.message = "Intercambio completado exitosamente"
            redirect(controller: 'main', action: 'menu')

        } else if (response == "REJECT") {
            tradeRequest.delete(flush: true)
            flash.message = "Intercambio rechazado y solicitud eliminada"
            redirect(controller: 'main', action: 'menu')
        } else {
            flash.message = "Respuesta no válida"
            redirect(controller: 'main', action: 'menu')
        }
    }





    def obtenerSets() {
        render(Set.list() as JSON)
    }

    def obtenerCartasPorSet(String setId) {
        def user = User.get(session.userId)
        if (!user) {
            render([success: false, message: "Usuario no autenticado"] as JSON)
            return
        }

        def cartas = Card.findAllByOwnerAndSetNameAndQuantityGreaterThan(user, Set.findBySetId(setId)?.name, 1)
        render(cartas as JSON)
    }

    def obtenerCartasDeUsuario(Long userId, String setId) {
        def targetUser = User.get(userId)
        if (!targetUser) {
            render([success: false, message: "Usuario no encontrado"] as JSON)
            return
        }

        def cartas = Card.findAllByOwnerAndSetNameAndQuantityGreaterThan(targetUser, Set.findBySetId(setId)?.name, 1)
        render(cartas as JSON)
    }

    def obtenerUsuarios() {
        def usuarios = User.list().findAll { it.id != session.userId }
        render(usuarios as JSON)
    }

    def listarSolicitudes() {
        def user = User.get(session.userId)
        if (!user) {
            render([success: false, message: "Usuario no autenticado"] as JSON)
            return
        }

        def solicitudes = TradeRequest.findAllByTargetUserAndStatus(user, "PENDING").findAll {
            try {
                // Asegura que los objetos referenciados existen (proxy se puede inicializar)
                it.requesterCard && it.requesterCard.name &&
                        it.targetCard && it.targetCard.name
            } catch (e) {
                false
            }
        }

        render view: 'solicitudesPendientes', model: [solicitudes: solicitudes]
    }

}