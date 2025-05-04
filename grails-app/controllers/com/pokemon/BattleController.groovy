package com.pokemon

import grails.converters.JSON

class BattleController {

    def selectTeam() {
        def user = User.get(session.userId)
        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        def sets = Set.list()
        if (sets.isEmpty()) {
            flash.message = "No hay colecciones disponibles."
            redirect(controller: "Main", action: "menu")
            return
        }

        render(view: "/battle/selectSet", model: [sets: sets, currentUser: user])
    }

    def startBattle(String setId, String selectedCards) {
        if (!selectedCards) {
            flash.message = "Debes seleccionar exactamente 4 Pokémon."
            redirect(action: "selectCards", params: [setId: setId])
            return
        }

        def selectedCardIds = selectedCards.split(",")
        if (selectedCardIds.size() != 4) {
            flash.message = "Debes seleccionar exactamente 4 Pokémon."
            redirect(action: "selectCards", params: [setId: setId])
            return
        }

        def apiUrl = "https://api.pokemontcg.io/v2/cards"
        def userTeam = selectedCardIds.collect { cardId ->
            def combatCard = CombatCard.findByCardId(cardId)
            if (!combatCard) {
                def connection = new URL("$apiUrl/$cardId").openConnection()
                connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
                def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

                combatCard = new CombatCard(
                    cardId: json.data.id,
                    name: json.data.name ?: "Nombre desconocido",
                    imageUrl: json.data.images?.small ?: "/images/default-pokemon.png",
                    hp: json.data.hp?.toInteger() ?: 50,
                    attacks: json.data.attacks?.collect { attack ->
                        [
                            name: attack.name ?: "Ataque desconocido",
                            damage: attack.damage?.replaceAll("[^0-9]", "")?.isInteger() ? attack.damage.replaceAll("[^0-9]", "").toInteger() : 10
                        ]
                    } ?: [[name: "Ataque básico", damage: 10]]
                )
                combatCard.save(flush: true, failOnError: true)
            }
            combatCard
        }

        // Obtener todas las cartas del set y filtrarlas por hp > 0
        def set = Set.findBySetId(setId)
        def allCardsInSet = AllCards.findAllBySetName(set.name).findAll { card ->
            def combatCard = CombatCard.findByCardId(card.cardId)
            combatCard?.hp > 0 || (card.rarity != null && card.rarity != "Common")
        }
        Collections.shuffle(allCardsInSet) // Mezclar las cartas del set

        // Seleccionar 4 cartas aleatorias para la IA
        def randomIaCards = allCardsInSet.take(4)

        def iaCards = randomIaCards.collect { card ->
            def combatCard = CombatCard.findByCardId(card.cardId)
            if (!combatCard) {
                def connection = new URL("$apiUrl/${card.cardId}").openConnection()
                connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
                def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

                combatCard = new CombatCard(
                    cardId: json.data.id,
                    name: json.data.name ?: "Nombre desconocido",
                    imageUrl: json.data.images?.small ?: "/images/default-pokemon.png",
                    hp: json.data.hp?.toInteger() ?: 50,
                    attacks: json.data.attacks?.collect { attack ->
                        [
                            name: attack.name ?: "Ataque desconocido",
                            damage: attack.damage?.replaceAll("[^0-9]", "")?.isInteger() ? attack.damage.replaceAll("[^0-9]", "").toInteger() : 10
                        ]
                    } ?: [[name: "Ataque básico", damage: 10]]
                )
                combatCard.save(flush: true, failOnError: true)
            }
            combatCard
        }

        session.battle = [
            userTeam: userTeam,
            iaTeam: iaCards,
            currentUserPokemon: 0,
            currentIaPokemon: 0,
            history: []
        ]

        redirect(action: "battle")
    }

    def battle() {
        def battle = session.battle
        if (!battle) {
            redirect(action: "selectTeam")
            return
        }

        def user = User.get(session.userId)
        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        // Deserialize attacks for user and IA Pokémon
        battle.userTeam.each { pokemon ->
            if (pokemon.attacks instanceof String) {
                pokemon.attacks = new groovy.json.JsonSlurper().parseText(pokemon.attacks)
            }
        }
        battle.iaTeam.each { pokemon ->
            if (pokemon.attacks instanceof String) {
                pokemon.attacks = new groovy.json.JsonSlurper().parseText(pokemon.attacks)
            }
        }

        if (battle.result) {
            if (battle.result == "¡Has ganado el combate!") {
                user.saldo += 100
                user.save(flush: true, failOnError: true)
            }
        }

        render(view: "battle", model: [battle: battle, currentUser: user])
    }

    def selectCards(String setId) {
        if (!setId) {
            flash.message = "No se ha seleccionado una colección."
            redirect(action: "selectTeam")
            return
        }

        def user = User.get(session.userId)
        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        def userCards = user.cards.findAll { it.setName == Set.findBySetId(setId)?.name }
        if (!userCards) {
            flash.message = "No tienes cartas de esta colección."
            redirect(action: "selectTeam")
            return
        }

        def apiUrl = "https://api.pokemontcg.io/v2/cards"
        def userCardsData = userCards.collect { card ->
            def connection = new URL("$apiUrl/${card.cardId}").openConnection()
            connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
            def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

            [
                cardId: card.cardId,
                name: json.name ?: card.name,
                imageUrl: json.images?.small ?: card.imageUrl
            ]
        }

        render(view: "/battle/selectCards", model: [userCards: userCardsData, setId: setId])
    }

    def attack(String attackName) {
        def battle = session.battle
        if (!battle) {
            redirect(action: "selectTeam")
            return
        }

        // User's attack
        def userAttack = battle.userTeam[battle.currentUserPokemon].attacks.find { it.name == attackName }
        if (!userAttack) {
            flash.message = "Ataque no válido."
            redirect(action: "battle")
            return
        }

        battle.iaTeam[battle.currentIaPokemon].hp -= userAttack.damage
        battle.history << "${battle.userTeam[battle.currentUserPokemon].name} usó ${attackName} e hizo ${userAttack.damage} de daño."

        // Check if IA Pokémon is defeated
        if (battle.iaTeam[battle.currentIaPokemon].hp <= 0) {
            battle.history << "${battle.userTeam[battle.currentUserPokemon].name} derrotó a ${battle.iaTeam[battle.currentIaPokemon].name}."
            battle.currentIaPokemon++
            if (battle.currentIaPokemon >= battle.iaTeam.size()) {
                battle.result = "¡Has ganado el combate!"
                session.battle = battle
                CombatCard.deleteAll(CombatCard.list())
                redirect(action: "battle")
                return
            }
        } else {
            // Pause before IA attack
            sleep(500) // 1-second delay

            // IA's random attack
            def iaAttacks = battle.iaTeam[battle.currentIaPokemon].attacks
            def iaAttack = iaAttacks[new Random().nextInt(iaAttacks.size())]
            battle.userTeam[battle.currentUserPokemon].hp -= iaAttack.damage
            battle.history << "${battle.iaTeam[battle.currentIaPokemon].name} usó ${iaAttack.name} e hizo ${iaAttack.damage} de daño."

            // Check if user's Pokémon is defeated
            if (battle.userTeam[battle.currentUserPokemon].hp <= 0) {
                battle.history << "${battle.iaTeam[battle.currentIaPokemon].name} derrotó a ${battle.userTeam[battle.currentUserPokemon].name}."
                battle.currentUserPokemon++
                if (battle.currentUserPokemon >= battle.userTeam.size()) {
                    battle.result = "¡Has perdido el combate!"
                    session.battle = battle
                    CombatCard.deleteAll(CombatCard.list())
                    redirect(action: "battle")
                    return
                }
            }
        }

        session.battle = battle
        redirect(action: "battle")
    }
}