package com.pokemon

class CombatCard {
    String cardId
    String name
    String imageUrl
    Integer hp
    List attacks
    String supertype
    List weaknesses

    static constraints = {
        cardId blank: false, unique: true
        name blank: false
        imageUrl nullable: true
        hp min: 1
        attacks nullable: true
        supertype blank: false
        weaknesses nullable: true
    }

    static mapping = {
        attacks type: 'text' // Store attacks as JSON
        weaknesses type: 'text' // Store weaknesses as JSON
    }
}