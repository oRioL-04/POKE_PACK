package com.pokemon

class User {
    String username
    String password // Por simplicidad en texto plano (NO recomendado para producción)
    Integer saldo = 2000 // Saldo inicial de Pokémonedas

    static hasMany = [cards: Card]

    static constraints = {
        username unique: true
        password blank: false
        saldo min: 0 // No puede ser negativo
    }
}

