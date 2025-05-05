<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Batalla Pokémon</title>
    <style>
    .battle-wrapper {
        display: flex;
        justify-content: space-between;
        gap: 40px;
        padding: 40px;
        flex-wrap: wrap;
    }

    .side {
        flex: 1 1 45%;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .pokemon-active {
        background-color: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 0 15px rgba(0,0,0,0.1);
        text-align: center;
        margin-bottom: 20px;
    }

    .pokemon-active img {
        width: 180px;
        height: auto;
        margin-bottom: 15px;
    }

    .hp-text {
        font-size: 18px;
        margin-bottom: 10px;
        font-weight: bold;
    }

    .attack-button {
        background-color: #ffcb05;
        border: none;
        padding: 8px 18px;
        margin: 5px;
        border-radius: 8px;
        font-weight: bold;
        cursor: pointer;
        transition: background 0.3s;
    }

    .attack-button:hover {
        background-color: #e0b200;
    }

    .bench {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
        gap: 12px;
        justify-items: center;
        margin-top: 15px;
    }

    .bench-card-form {
        background: none;
        border: none;
        padding: 0;
        cursor: pointer;
    }

    .bench-card-form img {
        width: 70px;
        height: 100px;
        border-radius: 8px;
        box-shadow: 0 0 10px rgba(0,0,0,0.1);
        transition: transform 0.2s;
    }

    .bench-card-form img:hover {
        transform: scale(1.05);
    }

    .history-box {
        border: 1px solid #ccc;
        background-color: #f9f9f9;
        border-radius: 10px;
        padding: 15px;
        margin-top: 20px;
        max-width: 100%;
        max-height: 250px;
        overflow-y: auto;
    }

    .history-box ul {
        list-style-type: none;
        padding-left: 0;
    }

    .history-box li {
        padding: 5px 0;
        font-size: 1rem;
        color: #333;
    }

    .result-box {
        text-align: center;
        font-size: 1.5em;
        font-weight: bold;
        color: green;
        margin: 20px;
    }

    .message {
        text-align: center;
        color: red;
        margin-top: 10px;
    }

    .battle-chat {
        font-size: 14px;
        background-color: #e9e9e9;
        border-radius: 8px;
        padding: 15px;
        margin-top: 30px;
        max-height: 200px;
        overflow-y: auto;
    }

    .battle-chat p {
        margin: 5px 0;
    }
    </style>
</head>
<body>

<g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
</g:if>

<g:if test="${battle.result}">
    <div class="result-box">${battle.result}</div>
    <div style="text-align: center;">
        <g:link controller="main" action="menu" class="attack-button">Volver al menú</g:link>
    </div>
</g:if>

<div class="battle-wrapper">

    <!-- LADO USUARIO -->
    <div class="side">
        <g:set var="player" value="${battle.userTeam[battle.currentUserPokemon]}"/>
        <div class="pokemon-active">
            <h2>Tu Pokémon</h2>
            <img src="${player.imageUrl}" alt="${player.name}" />
            <div class="hp-text">HP: ${player.hp}</div>
            <h3>${player.name}</h3>

            <g:if test="${!battle.result && !battle.canSwitchPokemon}">
                <form action="${createLink(action: 'attack')}" method="post">
                    <g:each in="${player.attacks}" var="atk">
                        <button type="submit" name="attackName" value="${atk.name}" class="attack-button">
                            ${atk.name} (${atk.damage} dmg)
                        </button>
                    </g:each>
                </form>
            </g:if>
        </div>

        <!-- BANQUILLO USUARIO -->
        <div class="bench">
            <g:each in="${battle.userTeam}" var="poke" status="i">
                <g:if test="${i != battle.currentUserPokemon && poke.hp > 0}">
                    <form action="${createLink(action: 'changePokemon')}" method="post" class="bench-card-form">
                        <input type="hidden" name="index" value="${i}"/>
                        <input type="hidden" name="team" value="user"/>
                        <button type="submit" class="bench-card-form">
                            <img src="${poke.imageUrl}" alt="${poke.name}" />
                        </button>
                    </form>
                </g:if>
            </g:each>
        </div>
    </div>

    <!-- LADO IA -->
    <div class="side">
        <g:set var="ia" value="${battle.iaTeam[battle.currentIaPokemon]}"/>
        <div class="pokemon-active">
            <h2>Pokémon de la IA</h2>
            <img src="${ia.imageUrl}" alt="${ia.name}" />
            <div class="hp-text">HP: ${ia.hp}</div>
            <h3>${ia.name}</h3>
        </div>

        <!-- BANQUILLO IA -->
        <div class="bench">
            <g:each in="${battle.iaTeam}" var="poke" status="i">
                <g:if test="${i != battle.currentIaPokemon && poke.hp > 0}">
                    <img src="${poke.imageUrl}" alt="${poke.name}" />
                </g:if>
            </g:each>
        </div>
    </div>
</div>

<!-- Historial de la Batalla -->
<div class="battle-chat">
    <h3>Chat de la Batalla</h3>
    <div class="history-box">
        <ul>
            <g:each in="${battle.history}" var="event">
                <li>${event}</li>
            </g:each>
        </ul>
    </div>
</div>

</body>
</html>
