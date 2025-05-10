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

    .nav-links {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        align-items: center;

    }

    .navbar a {
        color: white;
        font-weight: 600;
        text-decoration: none;
        padding: 8px 12px;
        border-radius: 6px;
        transition: all 0.3s ease;
        background: none;
        margin: 0;
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
        flex-wrap: wrap;
        gap: 10px;
        margin-left: auto; /* Esto los empuja a la derecha */
    }

    .username {
        font-weight: 600;
    }

    @media (max-width: 900px) {
        .navbar-container {
            flex-direction: column;
            align-items: stretch;
        }
        .nav-links, .user-info {
            width: 100%;
            justify-content: flex-start;
            margin-right: 0;
            margin-bottom: 10px;
        }
        .user-info {
            justify-content: flex-end;
            margin-top: 0;
        }
    }

    @media (max-width: 600px) {
        .main-content {
            padding: 1rem 0.2rem;
        }
        .navbar {
            padding: 0.5rem;
        }
        .nav-links {
            gap: 5px;
        }
        .navbar a {
            font-size: 0.9rem;
            padding: 6px 8px;
        }
    }
    </style>
    <g:layoutHead/>
</head>

<body>
<nav class="navbar">
    <div class="navbar-container">
        <div class="nav-links">
            <g:if test="${currentUser}">
                <g:link controller="main" action="menu">Inicio</g:link>
                <g:link controller="main" action="abrirSobres">Abrir Sobre</g:link>
                <g:link controller="main" action="pokedex">Mi Pokédex</g:link>
                <g:link controller="battle" action="selectTeam">Combate</g:link>
                <g:link controller="trade" action="intercambios">Intercambio</g:link>
                <g:link controller="market" action="mercado">Mercado</g:link>
            </g:if>
        </div>
        <g:if test="${currentUser}">
            <div class="user-info">
                <span class="username">${currentUser.username}</span>
                <span class="saldo">
                    <img src="${resource(dir: 'images', file: 'moneda.png')}" alt="Pokémoneda" style="height: 20px; vertical-align: middle; margin-left: 10px;"/>
                    ${currentUser?.saldo}
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

<div class="main-content">
    <g:layoutBody/>
</div>

<footer class="footer-nav">
    <div>© 2025 Pokémon App - Todos los derechos reservados</div>
</footer>
</body>
</html>