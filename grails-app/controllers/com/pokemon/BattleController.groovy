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

        selectedCardIds.each { cardId ->
            def combatCard = CombatCard.findByCardId(cardId)
            if (!combatCard) {
                try {
                    def connection = new URL("$apiUrl/$cardId").openConnection()
                    connection.setRequestProperty("X-Api-Key", "218f5856-a28b-44c8-9809-43e51bbeeefc")
                    def json = new groovy.json.JsonSlurper().parse(connection.getInputStream())

                    if (json.data.supertype != "Pokémon") {
                        flash.message = "La carta seleccionada (${json.data.name}) no es del tipo Pokémon."
                        render(view: "/battle/selectCards", model: [
                                setId   : setId,
                                userCards: Card.findAllByOwnerAndSetName(User.get(session.userId), Set.findBySetId(setId)?.name)
                        ])
                        return
                    }

                    combatCard = new CombatCard(
                            cardId: json.data.id,
                            name: json.data.name ?: "Nombre desconocido",
                            imageUrl: json.data.images?.small ?: "/images/default-pokemon.png",
                            hp: json.data.hp?.isInteger() ? json.data.hp.toInteger() : 50,
                            maxHp: json.data.hp?.isInteger() ? json.data.hp.toInteger() : 50,
                            attacks: json.data.attacks?.collect { attack ->
                                [
                                        name: attack.name ?: "Ataque desconocido",
                                        damage: extractDamage(attack.damage)
                                ]
                            } ?: [[name: "Ataque básico", damage: 10]],
                            supertype: json.data.supertype,
                            weaknesses: json.data.weaknesses?.collect { w -> [type: w.type, value: w.value] },
                            type: json.data.types?.first() ?: "Unknown"
                    )
                    combatCard.save(flush: true, failOnError: true)
                } catch (IOException e) {
                    println "Error al acceder a la API para $cardId: ${e.message}"
                    return
                }
            }
            userTeam << combatCard
        }

        def set = Set.findBySetId(setId)
        def allCardsInSet = AllCards.findAllBySetName(set.name).findAll { card ->
            def cc = CombatCard.findByCardId(card.cardId)
            cc?.hp > 0 || (card.rarity != null && card.rarity != "Common")
        }
        Collections.shuffle(allCardsInSet)

        def iaCards = []
        def attempts = 0

        while (iaCards.size() < 4 && attempts < 20) {
            def card = allCardsInSet.pop()
            def combatCard = CombatCard.findByCardId(card.cardId)
            if (!combatCard) {
                try {
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
                            hp: json.data.hp?.isInteger() ? json.data.hp.toInteger() : 50,
                            maxHp: json.data.hp?.isInteger() ? json.data.hp.toInteger() : 50,
                            attacks: json.data.attacks?.collect { attack ->
                                [
                                        name: attack.name ?: "Ataque desconocido",
                                        damage: extractDamage(attack.damage)
                                ]
                            } ?: [[name: "Ataque básico", damage: 10]],
                            supertype: json.data.supertype,
                            weaknesses: json.data.weaknesses?.collect { w -> [type: w.type, value: w.value] },
                            type: json.data.types?.first() ?: "Unknown"
                    )
                    combatCard.save(flush: true, failOnError: true)
                } catch (IOException e) {
                    println "Error al acceder a la API para ${card.cardId}: ${e.message}"
                    continue
                }
            }
            iaCards << combatCard
            attempts++
        }

        session.battle = [
                userTeam: userTeam,
                iaTeam: iaCards,
                currentUserPokemon: 0,
                currentIaPokemon: 0,
                history: [],
                canSwitchPokemon: false,
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

        battle.userTeam.each { p ->
            if (p.attacks instanceof String) {
                p.attacks = new groovy.json.JsonSlurper().parseText(p.attacks)
            }
        }

        battle.iaTeam.each { p ->
            if (p.attacks instanceof String) {
                p.attacks = new groovy.json.JsonSlurper().parseText(p.attacks)
            }
        }

        render(view: "battle", model: [battle: battle, currentUser: user])
    }

    def attack() {
        def battle = session.battle

        def currentPokemon = battle.userTeam[battle.currentUserPokemon]
        def enemyPokemon = battle.iaTeam[battle.currentIaPokemon]

        if (!currentPokemon || currentPokemon.hp <= 0) {
            battle.canSwitchPokemon = true
            flash.message = "Tu Pokémon está debilitado. Debes cambiarlo."
            redirect(action: "battle")
            return
        }

        def attackName = params.attackName
        def selectedAttack = currentPokemon.attacks.find { it.name == attackName }

        if (!selectedAttack) {
            flash.message = "Ataque no válido."
            redirect(action: "battle")
            return
        }

        battle.history << "${currentPokemon.name} usó ${selectedAttack.name}."
        enemyPokemon.hp -= selectedAttack.damage ?: 10

        if (enemyPokemon.hp <= 0) {
            enemyPokemon.hp = 0
            battle.history << "${enemyPokemon.name} se ha debilitado."
            def nextIa = battle.iaTeam.findIndexOf { it.hp > 0 }
            if (nextIa != -1) {
                battle.currentIaPokemon = nextIa
            } else {
                battle.result = "¡Has ganado el combate!"
            }
        }

        if (!battle.result) {
            iaTurn(battle)
        }

        redirect(action: "battle")
    }

    def changePokemon() {
        def battle = session.battle
        def index = params.int("index")

        if (params.team != "user" || index < 0 || index >= battle.userTeam.size()) {
            flash.message = "Selección inválida."
            redirect(action: "battle")
            return
        }

        def selected = battle.userTeam[index]
        if (selected.hp <= 0) {
            flash.message = "Ese Pokémon está debilitado."
            redirect(action: "battle")
            return
        }

        battle.currentUserPokemon = index
        battle.canSwitchPokemon = false
        battle.history << "¡Has cambiado a ${selected.name}!"

        if (!battle.history.last()?.contains("se ha debilitado")) {
            iaTurn(battle)
        }

        redirect(action: "battle")
    }

    // Simula el turno de la IA
    private void iaTurn(def battle) {
        def iaPokemon = battle.iaTeam[battle.currentIaPokemon]
        def userPokemon = battle.userTeam[battle.currentUserPokemon]

        def attack = iaPokemon.attacks?.find { it.damage > 0 } ?: [name: "Placaje", damage: 10]
        battle.history << "${iaPokemon.name} usó ${attack.name}."
        userPokemon.hp -= attack.damage

        if (userPokemon.hp <= 0) {
            userPokemon.hp = 0
            battle.history << "${userPokemon.name} se ha debilitado."
            def next = battle.userTeam.findIndexOf { it.hp > 0 }
            if (next != -1) {
                battle.currentUserPokemon = next
            } else {
                battle.result = "¡La IA ha ganado el combate!"
            }
        }
    }

    private Integer extractDamage(String raw) {
        def dmg = raw?.replaceAll("[^0-9]", "")
        return dmg?.isInteger() ? dmg.toInteger() : 10
    }
}
