package com.pokemon

class CombatCard {
    String cardId
    String name
    String imageUrl
    Integer hp
    List attacks

    static constraints = {
        cardId blank: false, unique: true
        name blank: false
        imageUrl nullable: true
        hp min: 1
        attacks nullable: true
    }

    static mapping = {
        attacks type: 'text' // Almacenar ataques como JSON
    }
}