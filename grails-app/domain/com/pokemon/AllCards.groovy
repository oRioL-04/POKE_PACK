package com.pokemon

class AllCards {
    String cardId
    String name
    String imageUrl
    String setName

    static constraints = {
        cardId unique: true, blank: false
        name blank: false
        imageUrl nullable: true
        setName blank: false
    }
}