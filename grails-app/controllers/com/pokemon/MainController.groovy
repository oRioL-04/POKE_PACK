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
    def syncCollectionsFromApi() {
        def apiUrl = "https://api.pokemontcg.io/v2/sets" // URL de la API para obtener los sets
        def connection = new URL(apiUrl).openConnection()
        connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc") // Clave de API
        def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

        json.data.each { apiSet ->
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
        }

        // Sincronizar las cartas después de los sets
        def cardsApiUrl = "https://api.pokemontcg.io/v2/cards"
        def cardsConnection = new URL(cardsApiUrl).openConnection()
        cardsConnection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
        def cardsJson = new groovy.json.JsonSlurper().parse(cardsConnection.getInputStream())

        cardsJson.data.each { apiCard ->
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
        }

        flash.message = "Sets y cartas sincronizados correctamente desde la API."
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

        // Verifica si la base de datos de sets está vacía
        if (Set.count() == 0) {
            syncCollectionsFromApi() // Sincroniza las colecciones desde la API
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

        if (user.saldo < sobreCosto) {
            redirect(controller: "main", action: "error")
            return
        }

        user.saldo -= sobreCosto
        user.save(flush: true, failOnError: true)
        session.user = user

        def set = Set.findBySetId(setId)
        if (!set) {
            flash.message = "El set seleccionado no existe."
            redirect(action: "abrirSobres")
            return
        }

        def cartasDelSet = AllCards.findAllBySetName(set.name)
        if (!cartasDelSet || cartasDelSet.isEmpty()) {
            cargarCartasDeSet(setId)
            cartasDelSet = AllCards.findAllBySetName(set.name)
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


}
