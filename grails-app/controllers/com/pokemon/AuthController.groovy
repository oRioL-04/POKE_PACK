package com.pokemon

class AuthController {

    def index() {
        render(view: "index")
    }

    def register() {
        render(view: "register")
    }

    def saveUser(String username, String password) {
        if (username && password) {
            def existingUser = User.findByUsername(username)
            if (existingUser) {
                flash.message = "El usuario ya existe"
                redirect(action: "register")
                return
            }

            def user = new User(username: username, password: password)
            if (user.save(flush: true, failOnError: true)) {
                flash.message = "Usuario registrado exitosamente"
                redirect(action: "index")
            } else {
                flash.message = "Error al registrar el usuario"
                redirect(action: "register")
            }
        } else {
            flash.message = "Todos los campos son obligatorios"
            redirect(action: "register")
        }
    }

    def login(String username, String password) {
        def user = User.findByUsernameAndPassword(username, password)
        if (user) {
            session.userId = user.id
            redirect(controller: "Main", action: "menu")
        } else {
            flash.message = "Credenciales incorrectas"
            redirect(action: "index")
        }
    }

    def logout() {
        session.invalidate()
        redirect(action: "index")
    }
}