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

        def cartasDelSet = AllCards.findAllBySetName(set.name)
        if (!cartasDelSet || cartasDelSet.isEmpty()) {
            try {
                cargarCartasDeSet(setId)
                cartasDelSet = AllCards.findAllBySetName(set.name)
            } catch (Exception e) {
                log.error("Error al cargar cartas desde la API para el set ${setId}: ${e.message}", e)
                flash.message = "No se pudieron cargar las cartas del set desde la API."
                redirect(action: "abrirSobres")
                return
            }
        }

        def cartasRaras = cartasDelSet.findAll { it.rarity && it.rarity != "Common" }
        def cartasComunes = cartasDelSet.findAll { !it.rarity || it.rarity == "Common" }

        if (cartasRaras.isEmpty() || cartasComunes.size() < 3) {
            log.error("No hay suficientes cartas raras o comunes disponibles para el set ${setId}.")
            flash.message = "No hay suficientes cartas disponibles para este set."
            redirect(action: "abrirSobres")
            return
        }

        // Sistema de pesos para rarezas
        def rarezaPesos = [
            "Rare"                     : 40,
            "Uncommon"                 : 30,
            "Double Rare"              : 15,
            "Ultra Rare"               : 10,
            "Hyper Rare"               : 3,
            "Special Illustration Rare": 1,
            "Illustration Rare"        : 1
        ]

        def cartasPorRareza = rarezaPesos.collectEntries { rareza, peso ->
            [(rareza): cartasRaras.findAll { it.rarity == rareza }]
        }

        def rarezaPesosActualizados = cartasPorRareza.findAll { it.value && !it.value.isEmpty() }
        if (rarezaPesosActualizados.isEmpty()) {
            log.error("No hay cartas raras disponibles para el set ${setId}.")
            flash.message = "No hay cartas raras disponibles para este set."
            redirect(action: "abrirSobres")
            return
        }

        def totalPeso = rarezaPesosActualizados.collect { rareza, cartas ->
            rarezaPesos[rareza]
        }.sum()

        def randomPeso = new Random().nextInt(totalPeso) + 1
        def rarezaSeleccionada = rarezaPesosActualizados.find { rareza, cartas ->
            randomPeso -= rarezaPesos[rareza]
            randomPeso <= 0
        }?.key

        def cartasDeRarezaSeleccionada = cartasPorRareza[rarezaSeleccionada]
        Collections.shuffle(cartasDeRarezaSeleccionada)
        def cartaRaraSeleccionada = cartasDeRarezaSeleccionada.first()

        Collections.shuffle(cartasComunes)
        def cartasComunesSeleccionadas = cartasComunes.take(3)

        def cartasSeleccionadas = cartasComunesSeleccionadas + cartaRaraSeleccionada

        // Verificar si las cartas son nuevas ANTES de guardar
        def cartasConEstado = cartasSeleccionadas.collect { cardData ->
            def yaExiste = Card.findByCardIdAndOwnerAndUsername(cardData.cardId, user, user.username)
            if (yaExiste) {
                yaExiste.quantity += 1
                yaExiste.save(flush: true, failOnError: true)
            } else {
                def newCard = new Card(
                    cardId: cardData.cardId,
                    name: cardData.name,
                    imageUrl: cardData.imageUrl,
                    setName: cardData.setName,
                    username: user.username,
                    owner: user,
                    quantity: 1
                )
                newCard.save(flush: true, failOnError: true)
            }
            [
                cardData: cardData,
                isNew: !yaExiste
            ]
        }

        // Actualizar saldo del usuario
        user.saldo -= sobreCosto
        user.save(flush: true, failOnError: true)

        // Enviar al frontend la lista con el estado de si es nueva
        def resultado = cartasConEstado.collect { entry ->
            def cardData = entry.cardData
            [
                cardId: cardData.cardId,
                name: cardData.name,
                imageUrl: cardData.imageUrl,
                setName: cardData.setName,
                rarity: cardData.rarity,
                isNew: entry.isNew
            ]
        }

        render(view: "sobreAbierto", model: [cards: resultado, currentUser: user, set: set])
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

            flash.message = "Se han cargado todas las cartas faltantes en la base de datos."
        } catch (Exception e) {
            log.error("Error al cargar las cartas faltantes: ${e.message}", e)
            flash.message = "Error al cargar las cartas faltantes desde la API."
        }
        redirect(action: "menu")
    }
}

