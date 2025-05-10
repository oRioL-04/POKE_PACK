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

        def allSets = Set.list(sort: "isFavorite", order: "desc") // Ordena por favoritos primero

        def sets = allSets.collect { set ->
            def totalCards = set.totalCards ?: 0
            def userCardsInSet = user.cards?.findAll { it.setName == set.name }?.size() ?: 0
            def percentage = (totalCards > 0) ? (userCardsInSet * 100.0 / totalCards).round(1) : 0.0
            [
                id: set.setId,
                name: set.name,
                logoUrl: set.logoUrl ?: '/images/default.png',
                percentage: percentage.toString().replace(".0", ""),
                isFavorite: set.isFavorite
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
        session.tenPacksOpened = false // Reset the flag when returning to the sobres screen
        session.packOpened = false // Reset the single pack flag as well
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

        // Si no hay suficientes cartas comunes o raras, selecciona aleatorias
        if (cartasComunes.size() < 3 || cartasRaras.isEmpty()) {
            log.warn("No hay suficientes cartas raras o comunes, seleccionando aleatorias.")
            Collections.shuffle(cartasDelSet)
            def cartasSeleccionadas = cartasDelSet.take(4)

            procesarCartasSeleccionadas(cartasSeleccionadas, user, sobreCosto, set)
            return
        }

        // Sistema de pesos para rarezas
        def rarezaPesos = [
            "Uncommon"                   : 50,
            "Rare"                       : 45,
            "Rare Holo"                  : 25,
            "Rare Holo EX"               : 20,
            "Rare Holo GX"               : 20,
            "Rare Holo LV.X"             : 20,
            "Rare Holo Star"             : 15,
            "Rare Holo V"                : 20,
            "Rare Holo VMAX"             : 15,
            "Rare Holo VSTAR"            : 15,
            "Rare Prime"                 : 15,
            "Rare Prism Star"            : 10,
            "Rare Rainbow"               : 5,
            "Rare Secret"                : 5,
            "Rare Shining"               : 10,
            "Rare Shiny"                 : 10,
            "Rare Shiny GX"              : 8,
            "Rare Ultra"                 : 5,
            "Double Rare"                : 15,
            "Ultra Rare"                 : 10,
            "Hyper Rare"                 : 5,
            "Special Illustration Rare"  : 3,
            "Illustration Rare"          : 3,
            "Trainer Gallery Rare Holo"  : 10,
            "Amazing Rare"               : 8,
            "Radiant Rare"               : 8,
            "Promo"                      : 20,
            "Classic Collection"         : 10,
            "LEGEND"                     : 5,
            "ACE SPEC Rare"              : 5,
            "Rare ACE"                   : 5,
            "Rare BREAK"                 : 10
        ]

        // Asignar probabilidad promedio a rarezas no incluidas en los pesos
        def rarezasNoIncluidas = cartasRaras.findAll { !rarezaPesos.containsKey(it.rarity) }
        def probabilidadPromedio = rarezaPesos.values().sum() / rarezaPesos.size()
        rarezasNoIncluidas.each { carta ->
            rarezaPesos[carta.rarity] = probabilidadPromedio
        }

        def cartasPorRareza = rarezaPesos.collectEntries { rareza, peso ->
            [(rareza): cartasRaras.findAll { it.rarity == rareza }]
        }

        def rarezaPesosActualizados = cartasPorRareza.findAll { it.value && !it.value.isEmpty() }
        if (rarezaPesosActualizados.isEmpty()) {
            log.warn("No hay cartas raras disponibles, seleccionando aleatorias.")
            Collections.shuffle(cartasDelSet)
            def cartasSeleccionadas = cartasDelSet.take(4)

            procesarCartasSeleccionadas(cartasSeleccionadas, user, sobreCosto, set)
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

        procesarCartasSeleccionadas(cartasSeleccionadas, user, sobreCosto, set)
    }

    private void procesarCartasSeleccionadas(def cartasSeleccionadas, def user, def sobreCosto, def set) {
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

        user.saldo -= sobreCosto
        user.save(flush: true, failOnError: true)

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
    def abrirDiezSobres(String setId) {
        if (session.tenPacksOpened) {
            flash.message = "Ya has abierto 10 sobres. Recarga no permitida."
            redirect(action: "abrirSobres")
            return
        }

        session.tenPacksOpened = true // Marca los 10 sobres como abiertos

        def user = User.get(session.userId)
        def sobreCosto = 50.0 * 10 // Costo de 10 sobres

        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        if (user.saldo < sobreCosto) {
            flash.message = "No tienes suficiente saldo para abrir 10 sobres."
            redirect(action: "abrirSobres", params: [setId: setId])
            return
        }

        def set = Set.findBySetId(setId)
        if (!set) {
            flash.message = "El set seleccionado no existe."
            redirect(action: "abrirSobres", params: [setId: setId])
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
                redirect(action: "abrirSobres", params: [setId: setId])
                return
            }
        }

        def cartasRaras = cartasDelSet.findAll { it.rarity && it.rarity != "Common" }
        def cartasComunes = cartasDelSet.findAll { !it.rarity || it.rarity == "Common" }

        def resultadoFinal = []
        10.times {
            if (cartasComunes.size() < 3 || cartasRaras.isEmpty()) {
                log.warn("No hay suficientes cartas raras o comunes, seleccionando aleatorias.")
                Collections.shuffle(cartasDelSet)
                def cartasSeleccionadas = cartasDelSet.take(4)
                resultadoFinal += procesarCartasSeleccionadas(cartasSeleccionadas, user)
            } else {
                Collections.shuffle(cartasComunes)
                def cartasComunesSeleccionadas = cartasComunes.take(3)

                Collections.shuffle(cartasRaras)
                def cartaRaraSeleccionada = cartasRaras.first()

                def cartasSeleccionadas = cartasComunesSeleccionadas + cartaRaraSeleccionada
                resultadoFinal += procesarCartasSeleccionadas(cartasSeleccionadas, user)
            }
        }

        user.saldo -= sobreCosto
        user.save(flush: true, failOnError: true)

        render(view: "sobreAbierto", model: [cards: resultadoFinal, currentUser: user, set: set, setId: setId])
    }

    private List procesarCartasSeleccionadas(def cartasSeleccionadas, def user) {
        return cartasSeleccionadas.collect { cardData ->
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
                cardId: cardData.cardId,
                name: cardData.name,
                imageUrl: cardData.imageUrl,
                setName: cardData.setName,
                rarity: cardData.rarity,
                isNew: !yaExiste
            ]
        }
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

