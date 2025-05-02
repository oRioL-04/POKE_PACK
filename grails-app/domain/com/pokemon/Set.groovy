package com.pokemon

class Set {
    String setId
    String name
    String logoUrl
    Integer totalCards
    boolean isFavorite = false

    static constraints = {
        setId unique: true
        name blank: false
        logoUrl nullable: true
        totalCards nullable: true
        isFavorite nullable: true
    }
}