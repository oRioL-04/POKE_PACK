package com.pokemon

import grails.converters.JSON

class MainController {

    def menu() {
        if (!session.userId) {
            redirect(controller: "Auth", action: "index")
            return
        }
        def currentUser = User.get(session.userId)
        render(view: "menu", model: [currentUser: currentUser])
    }
    def syncCollectionsFromApi() {
        try {
            def apiUrl = "https://api.pokemontcg.io/v2/sets"
            def connection = new URL(apiUrl).openConnection()
            connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
            def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

            json.data.each { apiSet ->
                try {
                    def existingSet = Set.findBySetId(apiSet.id)
                    if (!existingSet) {
                        def newSet = new Set(
                            setId: apiSet.id,
                            name: apiSet.name,
                            logoUrl: apiSet.images?.logo,
                            totalCards: apiSet.total
                        )
                        newSet.save(flush: true, failOnError: true)
                    }
                } catch (Exception e) {
                    log.error("Error saving set with ID ${apiSet.id}: ${e.message}", e)
                }
            }

            // Sincronizar las cartas después de los sets
            def cardsApiUrl = "https://api.pokemontcg.io/v2/cards"
            def cardsConnection = new URL(cardsApiUrl).openConnection()
            cardsConnection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
            def cardsJson = new groovy.json.JsonSlurper().parse(cardsConnection.getInputStream())

            cardsJson.data.each { apiCard ->
                try {
                    def existingCard = AllCards.findByCardId(apiCard.id)
                    if (!existingCard) {
                        def newCard = new AllCards(
                            cardId: apiCard.id,
                            name: apiCard.name,
                            imageUrl: apiCard.images?.small,
                            setName: apiCard.set?.name,
                            rarity: apiCard.rarity
                        )
                        newCard.save(flush: true, failOnError: true)
                    }
                } catch (Exception e) {
                    log.error("Error saving card with ID ${apiCard.id}: ${e.message}", e)
                }
            }

            flash.message = "Sets y cartas sincronizados correctamente desde la API."
        } catch (Exception e) {
            log.error("Error syncing collections from API: ${e.message}", e)
            flash.message = "Error al sincronizar los sets y cartas desde la API."
        }
        redirect(action: "menu")
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
            def userCardsInSet = user.cards?.findAll { it.setName == set.name }?.size() ?: 0
            def percentage = (totalCards > 0) ? (userCardsInSet * 100.0 / totalCards).round(1) : 0.0
            [
                id: set.setId,
                name: set.name,
                logoUrl: set.logoUrl ?: '/images/default.png',
                percentage: percentage.toString().replace(".0", "") // Formatea el porcentaje
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
        if (!user || !set) {
            flash.message = "No se encontraron datos para esta colección."
            redirect(action: "pokedex")
            return
        }

        def cartasCruzadas = obtenerCartasPorColeccion(setId)
        render(view: "cartasPorColeccion", model: [cards: cartasCruzadas, set: set, currentUser: user])
    }

    def abrirSobres() {
        def user = User.get(session.userId)
        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        if (Set.count() == 0) {
            syncCollectionsFromApi()
        }

        def sets = Set.list(sort: "isFavorite", order: "desc").collect { set ->
            [
                id: set.setId,
                name: set.name,
                logoUrl: set.logoUrl ?: '/images/default.png',
                isFavorite: set.isFavorite
            ]
        }

        if (sets.isEmpty()) {
            flash.message = "No hay colecciones disponibles en este momento."
        }

        render(view: "sobres", model: [sets: sets, currentUser: user])
    }

    def cargarCartasDeSet(String setId) {
        def apiUrl = "https://api.pokemontcg.io/v2/cards?q=set.id:$setId"
        def connection = new URL(apiUrl).openConnection()
        connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
        def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

        json.data.each { apiCard ->
            def existingCard = AllCards.findByCardId(apiCard.id)
            if (!existingCard) {
                def newCard = new AllCards(
                    cardId: apiCard.id,
                    name: apiCard.name,
                    imageUrl: apiCard.images?.small,
                    setName: apiCard.set?.name,
                    rarity: apiCard.rarity,
                    cardNumber: apiCard.number // Extraer el número de carta
                )
                newCard.save(flush: true, failOnError: true)
            }
        }
    }

    def abrirSobre(String setId) {
        def user = User.get(session.userId)
        def sobreCosto = 50.0

        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        if (user.saldo < sobreCosto) {
            flash.message = "No tienes suficiente saldo para abrir un sobre."
            redirect(action: "abrirSobres")
            return
        }

        def set = Set.findBySetId(setId)
        if (!set) {
            flash.message = "El set seleccionado no existe."
            redirect(action: "abrirSobres")
            return
        }

        // Verificar si las cartas del set están en la base de datos
        def cartasDelSet = AllCards.findAllBySetName(set.name)
        if (!cartasDelSet || cartasDelSet.isEmpty()) {
            try {
                cargarCartasDeSet(setId) // Llamada a la API para cargar las cartas
                cartasDelSet = AllCards.findAllBySetName(set.name)
            } catch (Exception e) {
                log.error("Error al cargar cartas desde la API para el set ${setId}: ${e.message}", e)
                flash.message = "No se pudieron cargar las cartas del set desde la API."
                redirect(action: "abrirSobres")
                return
            }
        }

        if (!cartasDelSet || cartasDelSet.isEmpty()) {
            flash.message = "No hay cartas disponibles para este set."
            redirect(action: "abrirSobres")
            return
        }

        def cartasRaras = cartasDelSet.findAll { it.rarity && it.rarity != "Common" }
        def cartasComunes = cartasDelSet.findAll { !it.rarity || it.rarity == "Common" }

        if (cartasRaras.isEmpty() || cartasComunes.size() < 3) {
            flash.message = "No hay suficientes cartas disponibles para este set."
            redirect(action: "abrirSobres")
            return
        }

        // Seleccionar una carta rara
        Collections.shuffle(cartasRaras)
        def cartaRara = cartasRaras.first()

        // Seleccionar tres cartas comunes
        Collections.shuffle(cartasComunes)
        def cartasComunesSeleccionadas = cartasComunes.take(3)

        // Combinar las cartas seleccionadas
        def cartasSeleccionadas = cartasComunesSeleccionadas + cartaRara

        // Actualizar el saldo del usuario
        user.saldo -= sobreCosto
        user.save(flush: true, failOnError: true)

        // Guardar las cartas en la colección del usuario
        cartasSeleccionadas.each { cardData ->
            def existingCard = user.cards?.find { it.cardId == cardData.cardId }
            if (existingCard) {
                existingCard.quantity += 1
                existingCard.save(flush: true, failOnError: true)
            } else {
                def newCard = new Card(
                    cardId: cardData.cardId,
                    name: cardData.name,
                    imageUrl: cardData.imageUrl,
                    setName: cardData.setName,
                    username: user.username,
                    owner: user
                )
                newCard.save(flush: true, failOnError: true)
            }
        }

        render(view: "sobreAbierto", model: [cards: cartasSeleccionadas, currentUser: user, set: set])
    }

    def obtenerCartasPorColeccion(String setId) {
        def user = User.get(session.userId)
        def set = Set.findBySetId(setId)
        if (!user || !set) {
            return []
        }

        def allCardsInSet = AllCards.findAllBySetName(set.name)
        def userCards = user.cards?.findAll { it.setName == set.name } ?: []

        // Cruza los datos de las cartas del usuario con las cartas del set
        def cartasCruzadas = allCardsInSet.collect { allCard ->
            def userCard = userCards.find { it.cardId == allCard.cardId }
            return [
                cardNumber: allCard.cardNumber,
                name: allCard.name,
                imageUrl: allCard.imageUrl,
                quantity: userCard?.quantity ?: 0 // Si el usuario no tiene la carta, la cantidad es 0
            ]
        }

        return cartasCruzadas
    }

    def toggleFavorite() {
        def requestBody = request.JSON
        def setId = requestBody?.setId

        if (!setId) {
            render([success: false, message: "Set ID no proporcionado"] as JSON)
            return
        }

        def set = Set.findBySetId(setId)
        if (!set) {
            render([success: false, message: "Set no encontrado"] as JSON)
            return
        }

        set.isFavorite = !set.isFavorite
        set.save(flush: true, failOnError: true)

        render([success: true, isFavorite: set.isFavorite] as JSON)
    }

    def cargarTodasLasCartas() {
        try {
            def apiUrl = "https://api.pokemontcg.io/v2/cards"
            def page = 1
            def pageSize = 250
            def totalCount = 0

            do {
                def connection = new URL("${apiUrl}?page=${page}&pageSize=${pageSize}").openConnection()
                connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
                def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

                totalCount = json.totalCount
                json.data.each { apiCard ->
                    def existingCard = AllCards.findByCardId(apiCard.id)
                    if (!existingCard) {
                        def newCard = new AllCards(
                            cardId: apiCard.id,
                            name: apiCard.name,
                            imageUrl: apiCard.images?.small,
                            setName: apiCard.set?.name,
                            rarity: apiCard.rarity,
                            cardNumber: apiCard.number
                        )
                        newCard.save(flush: true, failOnError: true)
                    }
                }

                page++
            } while ((page - 1) * pageSize < totalCount)

            flash.message = "Todas las cartas se han cargado correctamente en la base de datos."
        } catch (Exception e) {
            log.error("Error al cargar todas las cartas: ${e.message}", e)
            flash.message = "Error al cargar las cartas desde la API."
        }
        redirect(action: "menu")
    }
}
