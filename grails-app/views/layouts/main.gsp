<!doctype html>
<html lang="es" class="no-js">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title><g:layoutTitle default="Pokémon App"/></title>
    <style>
    body {
        font-family: 'Poppins', sans-serif;
        background: linear-gradient(to bottom, #83a4d4, #b6fbff);
        margin: 0;
        padding: 0;
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        color: #2d3748;
    }

    .navbar {
        background-color: #ef5350;
        padding: 1rem;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        position: sticky;
        top: 0;
        z-index: 100;
        display: flex;
        justify-content: center;
    }

    .navbar-container {
        width: 100%;
        max-width: 1200px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .navbar a {
        color: white;
        font-weight: 600;
        margin: 0 15px;
        text-decoration: none;
        padding: 8px 12px;
        border-radius: 6px;
        transition: all 0.3s ease;
    }

    .navbar a:hover {
        background-color: rgba(255, 255, 255, 0.2);
    }

    .footer-nav {
        background-color: #ef5350;
        padding: 1rem;
        box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
        position: sticky;
        bottom: 0;
        z-index: 100;
        text-align: center;
        color: white;
    }

    .main-content {
        flex: 1;
        padding: 2rem 1rem;
        overflow-y: auto;
    }

    .user-info {
        display: flex;
        align-items: center;
    }

    .username {
        margin-right: 15px;
        font-weight: 600;
    }

    @media (max-width: 768px) {
        .navbar {
            flex-direction: column;
            padding: 0.5rem;
        }

        .navbar-container {
            flex-direction: column;
        }

        .navbar a {
            margin: 5px;
            font-size: 0.9rem;
        }

        .user-info {
            margin-top: 10px;
        }
    }
    </style>

    <!-- Inyección del servicio para acceder al usuario de forma segura -->


    <!-- Obtener el usuario actual desde el servicio -->
    <g:set var="currentUser" value="${userService.getCurrentUser(session)}"/>

    <g:layoutHead/>
</head>

<body>
<!-- Barra de navegación superior -->
<nav class="navbar">
    <div class="navbar-container">
        <div class="nav-links">
            <g:if test="${currentUser}">
                <g:link controller="home" action="index">Inicio</g:link>
                <g:link controller="pack" action="openPack">Abrir Sobre</g:link>
                <g:link controller="home" action="pokedex">Mi Pokédex</g:link>
            </g:if>
        </div>

        <g:if test="${currentUser}">
            <div class="user-info">
                <span class="username">${currentUser.username}</span>
                <span class="saldo">
                    <img src="/images/moneda.png" alt="Pokémoneda" style="height: 20px; vertical-align: middle; margin-left: 10px;"/>
                    ${currentUser.saldo} Pokémonedas
                </span>
                <g:link controller="auth" action="logout" style="margin-left: 15px;">Cerrar Sesión</g:link>
            </div>
        </g:if>
        <g:else>
            <div class="user-info">
                <g:link controller="auth" action="index">Iniciar Sesión</g:link>
            </div>
        </g:else>
    </div>
</nav>

<!-- Contenido principal -->
<div class="main-content">
    <g:layoutBody/>
</div>

<!-- Pie de página -->
<footer class="footer-nav">
    <div>© 2023 Pokémon App - Todos los derechos reservados</div>
</footer>
</body>
</html>
