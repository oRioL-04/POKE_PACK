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
        def allSetsUrl = "https://api.pokemontcg.io/v2/sets"
        def connection = new URL(allSetsUrl).openConnection()
        connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
        def setsJson = new groovy.json.JsonSlurper().parse(connection.getInputStream())

        def sets = setsJson.data.collect { set ->
            def totalCards = set.total
            def userCardsInSet = user?.cards?.count { it.setName == set.name } ?: 0
            def percentage = totalCards > 0 ? Math.round((userCardsInSet * 1000 / totalCards)) / 10.0 : 0.0
            [
                id: set.id,
                name: set.name,
                logoUrl: set.images?.logo,
                percentage: percentage == 0.0 ? "0.0" : percentage
            ]
        }

        render(view: "pokedex", model: [sets: sets, currentUser: user])
    }

    def cartasPorColeccion(String setId) {
        def user = User.get(session.userId)
        def userCards = user?.cards?.findAll { it.setName == setId }
        render(view: "cartasPorColeccion", model: [cards: userCards, setId: setId, currentUser: user])
    }

    def abrirSobres() {
        def user = User.get(session.userId)
        def url = "https://api.pokemontcg.io/v2/sets"
        def connection = new URL(url).openConnection()
        connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
        def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

        def sets = json.data.collect { set ->
            [
                id: set.id,
                name: set.name,
                logoUrl: set.images?.logo
            ]
        }

        render(view: "sobres", model: [sets: sets, currentUser: user])
    }

    def abrirSobre(String setId) {
        def user = User.get(session.userId)
        def sobreCosto = 50.0

        if (user.saldo < sobreCosto) {
            flash.message = "No tienes suficiente saldo para abrir este sobre."
            redirect(action: "error")
            return
        }

        // Descontar el saldo y guardar en la base de datos
        user.saldo -= sobreCosto
        user.save(flush: true, failOnError: true)

        // Actualizar el usuario en la sesión
        session.user = user

        def url = "https://api.pokemontcg.io/v2/cards?q=set.id:${setId}"
        def connection = new URL(url).openConnection()
        connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
        def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

        def allCards = json.data
        Collections.shuffle(allCards)
        def randomCards = allCards.take(4)

        randomCards.each {
            def card = new Card(
                cardId: it.id,
                name: it.name,
                imageUrl: it.images.small,
                setName: it.set.name,
                owner: user
            )
            card.save(flush: true, failOnError: true)
        }

        render(view: "sobreAbierto", model: [cards: randomCards, currentUser: user])
    }
}