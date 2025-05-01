package com.pokemon

class Set {
    String setId
    String name
    String logoUrl
    Integer totalCards

    static constraints = {
        setId unique: true
        name blank: false
        logoUrl nullable: true
        totalCards nullable: true
    }
}