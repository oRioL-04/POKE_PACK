package com.pokemon

import java.time.LocalDateTime

class MarketListing {
    String cardId
    User seller
    Integer price
    LocalDateTime expirationDate

    static constraints = {
        cardId unique: true
        seller nullable: false
        price min: 1, nullable: false
        expirationDate nullable: false
    }
}
