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
                usuarios: usuarios,
                sets: sets,
                cartasUsuario: cartasUsuario
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
                targetUser: targetUser,
                set: set,
                cartasUsuario: cartasUsuario,
                cartasTarget: cartasTarget
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
        redirect(controller: 'main', action: 'menu') // o a una vista de confirmación
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
            def requesterCard = tradeRequest.requesterCard
            def targetCard = tradeRequest.targetCard

            requesterCard.owner = tradeRequest.targetUser
            targetCard.owner = tradeRequest.requester

            if (!requesterCard.save(flush: true)) {
                flash.message = "Error al guardar la carta del solicitante"
                redirect(controller: 'main', action: 'menu')
                return
            }

            if (!targetCard.save(flush: true)) {
                flash.message = "Error al guardar la carta del objetivo"
                redirect(controller: 'main', action: 'menu')
                return
            }

            tradeRequest.status = "ACCEPTED"
            tradeRequest.save(flush: true)

            flash.message = "Intercambio realizado con éxito"
            redirect(controller: 'main', action: 'menu')
        } else if (response == "REJECT") {
            tradeRequest.status = "REJECTED"
            tradeRequest.save(flush: true)

            flash.message = "Intercambio rechazado"
            redirect(controller: 'main', action: 'menu')
        } else {
            flash.message = "Respuesta no válida"
            redirect(controller: 'main', action: 'menu')
        }
    }


    def listarSolicitudes() {
        def user = User.get(session.userId)
        if (!user) {
            render([success: false, message: "Usuario no autenticado"] as JSON)
            return
        }

        def solicitudes = TradeRequest.findAllByTargetUserAndStatus(user, "PENDING")
        render view: 'solicitudesPendientes', model: [solicitudes: solicitudes]
    }



}
