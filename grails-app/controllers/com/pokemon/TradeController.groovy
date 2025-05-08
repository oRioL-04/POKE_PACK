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
            currentUser  : usuarioActual // Asegúrate de incluir el usuario actual
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

        def cartasUsuario = Card.findAllByOwnerAndSetNameAndQuantityGreaterThan(usuarioActual, set.name, 1).collect { carta ->
            def allCard = AllCards.findByCardId(carta.cardId)
            [
                cardId: carta.cardId,
                name: carta.name,
                imageUrl: carta.imageUrl,
                quantity: carta.quantity,
                rarity: allCard?.rarity ?: "Desconocida"
            ]
        }

        def cartasTarget = Card.findAllByOwnerAndSetNameAndQuantityGreaterThan(targetUser, set.name, 1).collect { carta ->
            def allCard = AllCards.findByCardId(carta.cardId)
            [
                cardId: carta.cardId,
                name: carta.name,
                imageUrl: carta.imageUrl,
                quantity: carta.quantity,
                rarity: allCard?.rarity ?: "Desconocida"
            ]
        }

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

        if (!userCard || !targetCard) {
            flash.message = "Error: Cartas no válidas"
            redirect(action: 'mostrarFormularioIntercambio')
            return
        }

        // Fetch rarities from AllCards
        def userCardRarity = AllCards.findByCardId(userCard.cardId)?.rarity
        def targetCardRarity = AllCards.findByCardId(targetCard.cardId)?.rarity

        if (!userCardRarity || !targetCardRarity || userCardRarity != targetCardRarity) {
            flash.message = "Error: Las cartas tienen rarezas diferentes"
            render(view: 'errorRarity')
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

            // Check if both users have at least 100 coins
            if (requester.saldo < 100 || targetUser.saldo < 100) {
                flash.message = "Error: Ambos usuarios deben tener al menos 100 monedas para completar el intercambio."
                render(view: 'intercambios', model: [delayRedirect: true])
                return
            }

            // Deduct 100 coins from both users
            requester.saldo -= 100
            targetUser.saldo -= 100
            requester.save(flush: true, failOnError: true)
            targetUser.save(flush: true, failOnError: true)

            // Proceed with the card exchange
            def requesterCard = tradeRequest.requesterCard
            def targetCard = tradeRequest.targetCard

            // Transfer cards between users
            def cardToTarget = Card.findByOwnerAndCardId(targetUser, requesterCard.cardId)
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

            def cardToRequester = Card.findByOwnerAndCardId(requester, targetCard.cardId)
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

            // Update trade request status
            tradeRequest.status = "ACCEPTED"
            tradeRequest.save(flush: true, failOnError: true)

            flash.message = "Intercambio completado exitosamente. Se han descontado 100 monedas a cada usuario."
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

    def intercambios() {
        def user = User.get(session.userId)
        if (!user) {
            flash.message = "Debe iniciar sesión"
            redirect(controller: 'auth', action: 'index')
            return
        }

        def intercambios = TradeRequest.findAllByRequesterOrTargetUser(user, user).collect { trade ->
            [
                requester: trade.requester,
                targetUser: trade.targetUser,
                requesterCard: Card.get(trade.requesterCard?.id), // Carga la carta del solicitante
                targetCard: Card.get(trade.targetCard?.id),       // Carga la carta del destinatario
                status: trade.status
            ]
        }

        render(view: 'intercambios', model: [
            intercambios: intercambios,
            currentUser: user // Pasa el usuario actual al modelo
        ])
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
            flash.message = "Debe iniciar sesión"
            redirect(controller: 'auth', action: 'index')
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

        render view: 'solicitudesPendientes', model: [
            solicitudes: solicitudes,
            currentUser: user // Incluye el usuario actual en el modelo
        ]
    }
    def eliminarIntercambiosAceptados() {
        def user = User.get(session.userId)
        if (!user) {
            flash.message = "Debe iniciar sesión"
            redirect(controller: 'auth', action: 'index')
            return
        }

        TradeRequest.where { status == "ACCEPTED" }.deleteAll()
        flash.message = "Intercambios aceptados eliminados exitosamente."
        redirect(controller: 'trade', action: 'intercambios')
    }
}