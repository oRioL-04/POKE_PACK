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

    def selectCards(String setId) {
        def user = User.get(session.userId)
        if (!user) {
            redirect(controller: "Auth", action: "index")
            return
        }

        if (!setId) {
            flash.message = "No se ha seleccionado una colección."
            redirect(action: "selectTeam")
            return
        }

        def setName = Set.findBySetId(setId)?.name
        if (!setName) {
            flash.message = "Colección no encontrada."
            redirect(action: "selectTeam")
            return
        }

        def userCards = Card.findAllByOwnerAndSetName(user, setName)
        if (!userCards) {
            flash.message = "No tienes cartas de esta colección."
            redirect(action: "selectTeam")
            return
        }

        def userCardsData = userCards.collect { card ->
            [
                cardId: card.cardId,
                name: card.name,
                imageUrl: card.imageUrl ?: "/images/default-pokemon.png"
            ]
        }

        render(view: "/battle/selectCards", model: [userCards: userCardsData, setId: setId, currentUser: user])
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
        def userTeam = []
        for (cardId in selectedCardIds) {
            def combatCard = CombatCard.findByCardId(cardId)
            if (!combatCard) {
                def connection = new URL("$apiUrl/$cardId").openConnection()
                connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
                def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

                if (json.data.supertype != "Pokémon") {
                    flash.message = "La carta seleccionada (${json.data.name}) no es del tipo Pokémon."
                    render(view: "/battle/selectCards", model: [setId: setId, userCards: Card.findAllByOwnerAndSetName(User.get(session.userId), Set.findBySetId(setId)?.name)])
                    return
                }

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
                    } ?: [[name: "Ataque básico", damage: 10]],
                    supertype: json.data.supertype,
                    weaknesses: json.data.weaknesses?.collect { weakness ->
                        [
                            type: weakness.type,
                            value: weakness.value
                        ]
                    }
                )
                combatCard.save(flush: true, failOnError: true)
            }
            userTeam << combatCard
        }

        def set = Set.findBySetId(setId)
        def allCardsInSet = AllCards.findAllBySetName(set.name).findAll { card ->
            def combatCard = CombatCard.findByCardId(card.cardId)
            combatCard?.hp > 0 || (card.rarity != null && card.rarity != "Common")
        }
        Collections.shuffle(allCardsInSet)

        def iaCards = []
        while (iaCards.size() < 4) {
            def card = allCardsInSet.pop()
            def combatCard = CombatCard.findByCardId(card.cardId)
            if (!combatCard) {
                def connection = new URL("$apiUrl/${card.cardId}").openConnection()
                connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
                def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

                if (json.data.supertype != "Pokémon") {
                    continue
                }

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
                    } ?: [[name: "Ataque básico", damage: 10]],
                    supertype: json.data.supertype,
                    weaknesses: json.data.weaknesses?.collect { weakness ->
                        [
                            type: weakness.type,
                            value: weakness.value
                        ]
                    }
                )
                combatCard.save(flush: true, failOnError: true)
            }
            iaCards << combatCard
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

    def attack(String attackName) {
        def battle = session.battle
        if (!battle) {
            redirect(action: "selectTeam")
            return
        }

        def userAttack = battle.userTeam[battle.currentUserPokemon].attacks.find { it.name == attackName }
        if (!userAttack) {
            flash.message = "Ataque no válido."
            redirect(action: "battle")
            return
        }

        battle.iaTeam[battle.currentIaPokemon].hp -= userAttack.damage
        battle.history << "${battle.userTeam[battle.currentUserPokemon].name} usó ${attackName} e hizo ${userAttack.damage} de daño."

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
            def iaAttacks = battle.iaTeam[battle.currentIaPokemon].attacks
            def iaAttack = iaAttacks[new Random().nextInt(iaAttacks.size())]
            battle.userTeam[battle.currentUserPokemon].hp -= iaAttack.damage
            battle.history << "${battle.iaTeam[battle.currentIaPokemon].name} usó ${iaAttack.name} e hizo ${iaAttack.damage} de daño."

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