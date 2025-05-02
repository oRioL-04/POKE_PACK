package com.pokemon

class Card {
    String cardId
    String name
    String imageUrl
    String setName
    Integer quantity = 1 // Cantidad inicial por defecto
    String username

    static belongsTo = [owner: User]

    static constraints = {
        cardId unique: ['owner']
        quantity min: 1
        username blank: false
    }
}