package com.pokemon

class AuthController {

    def index() {
        render(view: "index")
        log.info("Usuario en sesión: ${session.user}")
    }

    def login() {
        def user = User.findByUsernameAndPassword(params.username, params.password)
        if (user) {
            session.user = user
            session.userId = user.id
            redirect(controller: "Main", action: "menu")
        } else {
            flash.message = "Usuario o contraseña incorrectos"
            redirect(action: "index")
        }
    }

    def register() {
        render(view: "register")
    }

    def saveUser() {
        def user = new User(username: params.username, password: params.password)
        if (user.save(flush: true)) {
            session.user = user
            session.userId = user.id
            redirect(controller: "Main", action: "menu")
            log.info("Usuario en sesión: ${session.user}")
        } else {
            flash.message = "Error al registrar usuario"
            redirect(action: "register")
        }
    }

    def logout() {
        session.invalidate()
        redirect(action: "index")
    }
}