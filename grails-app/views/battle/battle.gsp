<meta name="layout" content="main"/>

<style>
.battle-container {
    display: flex;
    justify-content: space-between;
    padding: 20px;
    gap: 20px;
}

.team-column {
    width: 35%;
}

.team-title {
    font-weight: bold;
    font-size: 1.3rem;
    margin-bottom: 10px;
}

.active-pokemon, .bench {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20px;
}

.active-pokemon img {
    width: 200px;
    height: auto;
    border: 3px solid #4caf50;
    border-radius: 15px;
}

.bench {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 10px;
    justify-content: center;
}


.bench .bench-card {
    width: 80px;
    text-align: center;
    position: relative;
}

.bench img {
    width: 100%;
    border: 2px solid #aaa;
    border-radius: 10px;
}

.bench .hp {
    font-size: 0.8rem;
    margin-top: 5px;
}

.bench-card button {
    margin-top: 5px;
    font-size: 0.7rem;
}

.chat-box {
    width: 30%;
    background-color: #f3f3f3;
    border: 1px solid #ccc;
    border-radius: 15px;
    padding: 15px;
    height: 400px;
    overflow-y: auto;
}

.attack-list {
    display: flex;
    justify-content: center;
    gap: 10px;
    flex-wrap: wrap;
    margin-top: 10px;
}

.attack-list button {
    padding: 10px 15px;
    background-color: #4caf50;
    border: none;
    color: white;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.attack-list button:hover {
    background-color: #45a049;
}

.victory-message {
    margin: 20px auto;
    padding: 20px;
    background-color: #e0ffe0;
    border: 2px solid #66cc66;
    border-radius: 10px;
    text-align: center;
    font-size: 1.5rem;
    color: #336633;
    max-width: 600px;
    animation: fadeIn 1s ease-in-out;
}

@keyframes fadeIn {
    from { opacity: 0; }
    to   { opacity: 1; }
}
</style>

<g:set var="current" value="${battle.userTeam[battle.currentUserPokemon]}"/>
<g:set var="opponent" value="${battle.iaTeam[battle.currentIaPokemon]}"/>

<div class="battle-container">

    <!-- Equipo del Usuario -->
    <div class="team-column">
        <div class="team-title">Tu equipo</div>

        <div class="active-pokemon">
            <g:if test="${current.hp > 0}">
                <img src="${current.imageUrl}" alt="${current.name}"/>
                <p>${current.name} - HP: ${current.hp}</p>
            </g:if>
        </div>

        <div class="bench">
            <g:each in="${battle.userTeam}" var="poke" status="i">
                <g:if test="${i != battle.currentUserPokemon && poke.hp > 0}">
                    <div class="bench-card">
                        <img src="${poke.imageUrl}" alt="${poke.name}"/>
                        <p class="hp">${poke.name}<br/>HP: ${poke.hp}</p>
                        <g:form controller="battle" action="changePokemon">
                            <input type="hidden" name="index" value="${i}"/>
                            <input type="hidden" name="team" value="user"/>
                            <button type="submit" onclick="return confirm('¿Cambiar a ${poke.name}?')">Cambiar</button>
                        </g:form>
                    </div>
                </g:if>
            </g:each>
        </div>

    <!-- Botones debajo del banquillo -->
        <g:if test="${!battle.result && battle.userTeam.any { it.hp > 0 } && battle.iaTeam.any { it.hp > 0 }}">
            <div class="attack-list">
                <g:each in="${current.attacks}" var="atk">
                    <g:form controller="battle" action="attack">
                        <input type="hidden" name="attackName" value="${atk.name}"/>
                        <button type="submit">${atk.name} (${atk.damage})</button>
                    </g:form>
                </g:each>
            </div>
        </g:if>
    </div>

    <!-- Equipo de la IA -->
    <div class="team-column">
        <div class="team-title">Equipo rival</div>

        <div class="active-pokemon">
            <g:if test="${opponent.hp > 0}">
                <img src="${opponent.imageUrl}" alt="${opponent.name}"/>
                <p>${opponent.name} - HP: ${opponent.hp}</p>
            </g:if>
        </div>

        <div class="bench">
            <g:each in="${battle.iaTeam}" var="poke" status="j">
                <g:if test="${j != battle.currentIaPokemon && poke.hp > 0}">
                    <div class="bench-card">
                        <img src="${poke.imageUrl}" alt="${poke.name}"/>
                        <p class="hp">${poke.name}<br/>HP: ${poke.hp}</p>
                    </div>
                </g:if>
            </g:each>
        </div>
    </div>

    <!-- Chat -->
    <div class="chat-box">
        <g:each in="${battle.history}" var="log">
            <p>${log}</p>
        </g:each>
    </div>
</div>

<!-- Mensaje final y redirección automática -->
<g:if test="${battle.result}">
    <div class="victory-message">${battle.result}</div>
    <audio autoplay>
        <source src="/sounds/victory.mp3" type="audio/mpeg"/>
    </audio>
    <script>
        setTimeout(function () {
            window.location.href = '${createLink(controller: "main", action: "menu")}';
        }, 3000);
    </script>
</g:if>
