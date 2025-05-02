package com.pokemon

class AllCards {
    String cardId
    String name
    String imageUrl
    String setName
    String rarity
    String cardNumber

    static constraints = {
        cardId blank: false
        name blank: false
        imageUrl nullable: true
        setName blank: false
        rarity nullable: true // Puede ser nulo si no se proporciona
        cardNumber nullable: true
    }
}