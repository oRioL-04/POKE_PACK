package com.pokemon

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import java.time.LocalDateTime

class MarketController {

    def index() {
        redirect(action: "mercado")
    }

    @Transactional
    private void handleExpiredListings() {
        def expiredListings = MarketListing.findAllByExpirationDateLessThan(LocalDateTime.now())
        expiredListings.each { listing ->
            def seller = listing.seller
            def card = Card.findByCardIdAndOwner(listing.cardId, seller)
            if (card) {
                card.quantity += 1
                card.save(flush: true)
            }
            listing.delete(flush: true)
        }
    }

    def mercado() {
        def user = User.get(session.userId)
        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        handleExpiredListings() // Manejar cartas expiradas antes de cargar el mercado

        def listings = MarketListing.findAllByExpirationDateGreaterThan(LocalDateTime.now())
        render(view: "mercado", model: [listings: listings, currentUser: user])
    }

    def venderCarta() {
        def user = User.get(session.userId)
        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        // Obtener todos los sets disponibles
        def availableSets = user.cards?.collect { it.setName }?.unique() ?: []

        // Filtrar cartas por set seleccionado
        def selectedSet = params.setName
        def filteredCards = user.cards?.findAll {
            it.quantity > 1 && (!selectedSet || it.setName == selectedSet)
        } ?: []

        render(view: "venderCarta", model: [availableSets: availableSets, filteredCards: filteredCards, currentUser: user])
    }

    def getCardsBySet(String setName) {
        def user = User.get(session.userId)
        if (!user) {
            render(status: 401, text: "Usuario no autenticado")
            return
        }

        def cards = user.cards?.findAll { it.setName == setName && it.quantity > 1 && it.cardId && it.name }?.collect {
            [
                cardId: it.cardId,
                name: it.name,
                quantity: it.quantity
            ]
        } ?: []

        render cards as JSON
    }

    def sell() {
        if (!params.cardId || !params.price || !params.duration) {
            flash.message = "Error: Faltan datos para realizar la venta."
            redirect(action: "venderCarta")
            return
        }

        def card = Card.findByCardIdAndOwner(params.cardId, User.get(session.userId))
        if (card && card.quantity > 1) {
            def durationMinutes = params.int('duration') ?: 5
            def expirationDate = LocalDateTime.now().plusMinutes(durationMinutes)

            def listing = new MarketListing(
                cardId: card.cardId,
                seller: card.owner,
                price: params.int('price'),
                expirationDate: expirationDate
            )

            if (listing.validate() && listing.save(flush: true)) {
                card.quantity -= 1
                card.save(flush: true)
                flash.message = "Carta puesta en venta correctamente."
            } else {
                flash.message = "Error al crear la entrada en el mercado."
            }
        } else {
            flash.message = "No se puede vender esta carta. Cantidad insuficiente."
        }

        redirect(action: "mercado") // Redirige a mercado tras completar la venta
    }

    def buy(String cardId) {
        if (!cardId) {
            log.error("El cardId del MarketListing no fue proporcionado. Parámetros recibidos: ${params}")
            flash.message = "Error: No se pudo procesar la compra. cardId no válido."
            redirect(action: "mercado")
            return
        }

        def listing = MarketListing.findByCardId(cardId)
        if (!listing) {
            log.error("No se encontró el MarketListing con cardId: ${cardId}. Parámetros recibidos: ${params}")
            flash.message = "Error: No se encontró la carta en el mercado."
            redirect(action: "mercado")
            return
        }

        def buyer = User.get(session.userId)
        if (!buyer) {
            log.error("No se encontró el usuario comprador con ID de sesión: ${session.userId}")
            flash.message = "Error: Usuario no autenticado."
            redirect(controller: "Auth", action: "index")
            return
        }

        if (buyer.saldo < listing.price) {
            log.error("Saldo insuficiente para el comprador (${buyer.username}). Saldo: ${buyer.saldo}, Precio: ${listing.price}")
            flash.message = "Error: Saldo insuficiente para realizar la compra."
            redirect(action: "mercado")
            return
        }

        if (listing.seller == buyer) {
            log.error("El comprador (${buyer.username}) no puede comprar su propia carta.")
            flash.message = "Error: No puedes comprar tu propia carta."
            redirect(action: "mercado")
            return
        }

        try {
            // Actualizar saldo del comprador y vendedor
            buyer.saldo -= listing.price
            listing.seller.saldo += listing.price

            // Buscar o crear la carta para el comprador
            def card = Card.findByCardIdAndOwner(cardId, buyer)
            if (card) {
                card.quantity += 1
            } else {
                card = new Card(
                    cardId: cardId,
                    name: listing.seller.cards.find { it.cardId == cardId }?.name,
                    imageUrl: listing.seller.cards.find { it.cardId == cardId }?.imageUrl,
                    setName: listing.seller.cards.find { it.cardId == cardId }?.setName,
                    username: buyer.username,
                    owner: buyer,
                    quantity: 1
                )
            }
            card.save(flush: true, failOnError: true)

            // Eliminar la entrada del mercado
            listing.delete(flush: true)

            // Guardar los cambios en el saldo del vendedor y comprador
            listing.seller.save(flush: true, failOnError: true)
            buyer.save(flush: true, failOnError: true)

            flash.message = "Compra realizada con éxito."
        } catch (Exception e) {
            log.error("Error durante el proceso de compra: ${e.message}", e)
            flash.message = "Error: No se pudo completar la compra. Inténtalo de nuevo."
        }

        redirect(action: "mercado")
    }
}

