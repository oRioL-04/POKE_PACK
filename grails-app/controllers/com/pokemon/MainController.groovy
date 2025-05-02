package com.pokemon

class MainController {

    def menu() {
        if (!session.userId) {
            redirect(controller: "Auth", action: "index")
            return
        }
        def currentUser = User.get(session.userId)
        render(view: "menu", model: [currentUser: currentUser])
    }

    def pokedex() {
        def user = User.get(session.userId)
        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        def allSets = Set.list()

        def sets = allSets.collect { set ->
            def totalCards = set.totalCards ?: 0
            def userCardsInSet = user.cards?.count { it.setName == set.name } ?: 0
            def percentage = totalCards > 0 ? Math.round((userCardsInSet * 1000 / totalCards)) / 10.0 : 0.0
            [
                    id: set.setId,
                    name: set.name,
                    logoUrl: set.logoUrl ?: '/images/default.png',
                    percentage: percentage
            ]
        }

        if (sets.isEmpty()) {
            flash.message = "No hay colecciones disponibles en este momento."
        }

        render(view: "pokedex", model: [sets: sets, currentUser: user])
    }

    def cartasPorColeccion(String setId) {
        def user = User.get(session.userId)
        def set = Set.findBySetId(setId)
        def userCards = user?.cards?.findAll { it.setName == set?.name }
        render(view: "cartasPorColeccion", model: [cards: userCards, set: set, currentUser: user])
    }

    def error() {
        flash.message = "No tienes suficiente saldo para abrir un sobre."
        render(view: "error")
    }

    def abrirSobres() {
        def user = User.get(session.userId)
        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        // Obtén los sets directamente desde la base de datos
        def sets = Set.list().collect { set ->
            [
                id: set.setId,
                name: set.name,
                logoUrl: set.logoUrl ?: '/images/default.png'
            ]
        }

        if (sets.isEmpty()) {
            flash.message = "No hay colecciones disponibles en este momento."
        }

        render(view: "sobres", model: [sets: sets, currentUser: user])
    }

    def abrirSobre(String setId) {
        def user = User.get(session.userId)
        def sobreCosto = 50.0

        if (user.saldo < sobreCosto) {
            redirect(controller: "main", action: "error")
            return
        }

        user.saldo -= sobreCosto
        user.save(flush: true, failOnError: true)
        session.user = user

        def cartasDelSet = AllCards.findAllBySetName(Set.findBySetId(setId)?.name)
        if (!cartasDelSet) {
            def url = "https://api.pokemontcg.io/v2/cards?q=set.id:${setId}"
            def connection = new URL(url).openConnection()
            connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
            def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

            json.data.each {
                def card = new AllCards(
                        cardId: it.id,
                        name: it.name,
                        imageUrl: it.images.small,
                        setName: it.set.name
                )
                card.save(flush: true, failOnError: true)
            }

            cartasDelSet = AllCards.findAllBySetName(Set.findBySetId(setId)?.name)
        }

        Collections.shuffle(cartasDelSet)
        def randomCards = cartasDelSet.take(10)

        def apiCards = []
        randomCards.each { card ->
            def url = "https://api.pokemontcg.io/v2/cards/${card.cardId}"
            def connection = new URL(url).openConnection()
            connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
            def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())
            apiCards << [cardId: card.cardId, rarity: json.data.rarity]
        }

        def commonCards = apiCards.findAll { it.rarity == "Common" }
        def otherCards = apiCards.findAll { it.rarity != "Common" }

        def finalCards = commonCards.take(3)
        if (!otherCards.isEmpty()) {
            finalCards << otherCards.first()
        } else if (commonCards.size() > 3) {
            finalCards << commonCards[3]
        }

        finalCards = finalCards.collect { card ->
            def dbCard = AllCards.findByCardId(card.cardId)
            [
                    cardId: card.cardId,
                    name: dbCard?.name,
                    imageUrl: dbCard?.imageUrl ?: '/images/default.png',
                    rarity: card.rarity,
                    setName: dbCard?.setName
            ]
        }

        def set = Set.findBySetId(setId)

        render(view: "sobreAbierto", model: [cards: finalCards, currentUser: user, set: set])
    }
}
