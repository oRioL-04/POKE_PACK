<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Abrir Sobre Pokémon</title>

    <style>
    body {
        font-family: 'Arial', sans-serif;
        background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        min-height: 100vh;
        margin: 0;
        text-align: center;
        padding: 20px;
    }

    h1 {
        color: #2c3e50;
        margin-bottom: 20px;
        text-shadow: 1px 1px 3px rgba(0,0,0,0.1);
    }

    .content-wrapper {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        min-height: 70vh; /* Aumentamos el tamaño del contenedor */
        width: 100%;
    }

    .pack-container {
        width: 250px; /* Ajustamos el tamaño de la caja */
        height: 450px; /* Ajustamos la altura de la caja */
        margin: 20px auto;
        position: relative;
        transition: all 0.5s ease;
    }

    .pack {
        background: url('${resource(dir: 'images', file: 'sobre.png')}') no-repeat center center;
        background-size: contain; /* Cambiamos para que se ajuste mejor */
        width: 100%;
        height: 100%;
        cursor: pointer;
        animation: pulse 1.5s ease-in-out infinite;
        transition: all 0.3s ease;
    }

    .pack:hover {
        transform: scale(1.1);
    }

    @keyframes pulse {
        0%, 100% { transform: scale(1); }
        50% { transform: scale(1.1); }
    }

    .cards-container {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        align-items: center;
        gap: 20px;
        margin: 20px 0;
        max-width: 800px;
        min-height: 200px;
    }

    .card-reveal {
        display: none;
        animation: reveal 0.8s forwards;
    }

    @keyframes reveal {
        0% { transform: translateY(50px) scale(0.8); opacity: 0; }
        100% { transform: translateY(0) scale(1); opacity: 1; }
    }

    .card {
        background: white;
        border-radius: 15px;
        padding: 15px;
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        width: 140px;
    }

    .card:hover {
        transform: translateY(-5px);
        box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
    }

    .card img {
        width: 100px;
        height: 100px;
        object-fit: contain;
        margin-bottom: 10px;
    }

    .card p {
        margin: 0;
        font-weight: bold;
        color: #2c3e50;
        font-size: 14px;
    }

    .button {
        display: inline-block;
        margin-top: 20px;
        padding: 12px 25px;
        background: #3498db;
        color: white;
        text-decoration: none;
        border-radius: 30px;
        font-weight: bold;
        transition: all 0.3s ease;
        box-shadow: 0 4px 10px rgba(52, 152, 219, 0.3);
    }

    .button:hover {
        background: #2980b9;
        transform: translateY(-2px);
        box-shadow: 0 6px 15px rgba(52, 152, 219, 0.4);
    }

    .instructions {
        margin-top: 20px;
        color: #7f8c8d;
        font-style: italic;
    }
    </style>
</head>
<body>
<h1>¡Abre tu Sobre Pokémon!</h1>
<p class="instructions">Haz clic en el sobre para revelar tus Pokémon</p>

<div class="content-wrapper">
    <div class="pack-container">
        <div id="pack" class="pack" onclick="openPack()"></div>
    </div>

    <div class="cards-container" id="cardsContainer">
        <g:each in="${newPokemons}" var="poke" status="i">
            <div class="card-reveal" style="animation-delay: ${i*0.3}s;">
                <div class="card">
                    <img src="${poke.imageUrl}" alt="${poke.name}">
                    <p>${poke.name}</p>
                </div>
            </div>
        </g:each>
    </div>
</div>

<a href="/home/index" class="button">⬅ Volver a Inicio</a>

<script>
    function openPack() {
        const pack = document.getElementById('pack');
        const cardsContainer = document.getElementById('cardsContainer');

        // Cambiar el título después de abrir
        document.querySelector('h1').textContent = '¡Has obtenido estos Pokémon!';
        document.querySelector('.instructions').style.display = 'none';

        // Animación de apertura
        pack.style.animation = 'none';
        pack.style.transform = 'scale(1.5)';
        pack.style.opacity = '0';

        setTimeout(() => {
            pack.style.display = 'none';
            pack.parentElement.style.height = '0';

            // Mostrar las cartas
            let cards = document.querySelectorAll('.card-reveal');
            let delay = 0;
            cards.forEach(card => {
                setTimeout(() => {
                    card.style.display = 'block';
                }, delay);
                delay += 300;
            });

            // Ajustar el contenedor para que no quede espacio
            cardsContainer.style.margin = '0';
            cardsContainer.style.padding = '20px 0';
        }, 500);
    }
</script>
</body>
</html>
