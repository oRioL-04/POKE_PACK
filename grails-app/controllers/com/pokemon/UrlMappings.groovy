package com.pokemon

class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "main", action: "menu")
        "500"(view:'/error')
        "404"(view:'/notFound')
        "/trade/request"(controller: "trade", action: "solicitarIntercambio")
        "/trade/respond"(controller: "trade", action: "responderIntercambio")
        "/trade/list"(controller: "trade", action: "listarSolicitudes")
        "/trade/mostrarFormularioIntercambio"(controller: "trade", action: "mostrarFormularioIntercambio")
        "/trade/solicitudesPendientes"(controller: "trade", action: "listarSolicitudes")
        "/market/sell"(controller: "market", action: "sell")
        "/market/buy/$cardId"(controller: "market", action: "buy", method: "POST")
        "/market"(controller: "market", action: "index")
        "/market/mercado"(controller: "market", action: "mercado")
        "/market/venderCarta"(controller: "market", action: "venderCarta")
        "/api/cardsBySet"(controller: "market", action: "getCardsBySet")

    }
}

