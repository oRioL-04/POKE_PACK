<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bienvenido - Pokémon App</title>
    <meta name="layout" content="main"/>
    <style>
    :root {
        --primary-color: #ef5350;
        --secondary-color: #42a5f5;
        --accent-color: #ffcb05;
        --light-bg: #f8f9fa;
        --dark-text: #2d3748;
    }

    .welcome-container {
        max-width: 800px;
        margin: 0 auto;
        padding: 40px 20px;
        text-align: center;
        animation: fadeIn 0.8s ease-out;
    }

    @keyframes fadeIn {
        from { opacity: 0; transform: translateY(20px); }
        to { opacity: 1; transform: translateY(0); }
    }

    h1 {
        color: #ef5350;
        font-size: 2.8rem;
        margin-bottom: 30px;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
        position: relative;
        display: inline-block;
    }

    h1::after {
        content: '';
        position: absolute;
        bottom: -10px;
        left: 50%;
        transform: translateX(-50%);
        width: 100px;
        height: 4px;
        background: #ffcb05;
        border-radius: 2px;
    }

    .welcome-message {
        font-size: 1.2rem;
        color: #2d3748;
        margin-bottom: 40px;
        line-height: 1.6;
    }

    .button-container {
        display: flex;
        justify-content: center;
        flex-wrap: wrap;
        gap: 20px;
        margin-top: 40px;
    }

    .button {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        background-color: #42a5f5;
        color: white;
        padding: 15px 30px;
        border-radius: 50px;
        text-decoration: none;
        font-weight: 600;
        font-size: 1.1rem;
        transition: all 0.3s ease;
        box-shadow: 0 4px 10px rgba(66, 165, 245, 0.3);
        min-width: 200px;
        border: none;
        cursor: pointer;
    }

    .button:hover {
        background-color: #1e88e5;
        transform: translateY(-3px);
        box-shadow: 0 6px 15px rgba(66, 165, 245, 0.4);
    }

    .button-primary {
        background-color: #ef5350;
        box-shadow: 0 4px 10px rgba(239, 83, 80, 0.3);
    }

    .button-primary:hover {
        background-color: #d32f2f;
        box-shadow: 0 6px 15px rgba(239, 83, 80, 0.4);
    }

    .button-accent {
        background-color: #ffcb05;
        color: #2d3748;
        box-shadow: 0 4px 10px rgba(255, 203, 5, 0.3);
    }

    .button-accent:hover {
        background-color: #e6b800;
        box-shadow: 0 6px 15px rgba(255, 203, 5, 0.4);
    }

    .button-icon {
        margin-right: 10px;
        font-size: 1.3rem;
    }

    .pokeball-decoration {
        width: 150px;
        height: 150px;
        margin: 0 auto 30px;
        background: url('https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png') no-repeat center;
        background-size: contain;
        opacity: 0.8;
        animation: float 3s ease-in-out infinite;
    }

    @keyframes float {
        0%, 100% { transform: translateY(0); }
        50% { transform: translateY(-15px); }
    }

    @media (max-width: 768px) {
        h1 {
            font-size: 2.2rem;
        }

        .button {
            width: 100%;
        }

        .pokeball-decoration {
            width: 120px;
            height: 120px;
        }
    }
    </style>
</head>
<body>
<div class="welcome-container">
    <div class="pokeball-decoration"></div>

    <h1>¡Bienvenido, ${user.username}!</h1>

    <p class="welcome-message">
        Prepárate para comenzar tu aventura Pokémon.<br>
        Abre sobres para añadir nuevos Pokémon a tu colección y completa tu Pokédex.
    </p>

    <div class="button-container">
        <g:link controller="pack" action="openPack" class="button button-primary">
            <span class="button-icon">🔓</span> Abrir Sobre
        </g:link>

        <g:link controller="home" action="pokedex" class="button button-accent">
            <span class="button-icon">📖</span> Mi Pokédex
        </g:link>

        <g:link controller="auth" action="logout" class="button">
            <span class="button-icon">🚪</span> Cerrar Sesión
        </g:link>
    </div>
</div>
</body>
</html>