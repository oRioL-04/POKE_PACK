package com.pokemon

// grails-app/domain/pokedex/Card.groovy
class Card {
    String cardId
    String name
    String imageUrl
    String setName

    static belongsTo = [owner: User]

    static constraints = {
        cardId unique: ['owner']
    }
}
