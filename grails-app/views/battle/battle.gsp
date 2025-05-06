<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="main"/>

<style>
.battle-container {
    display: flex;
    flex-direction: row;
    justify-content: center;
    padding: 20px;
    gap: 30px;
}

.left-panel {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 2;
}

.teams {
    display: flex;
    justify-content: space-between;
    width: 100%;
    max-width: 1000px;
    gap: 40px;
}

.team {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.team h3 {
    margin-bottom: 10px;
}

.active-pokemon {
    width: 180px;
    margin: 10px;
}

.bench {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
    margin-top: 10px;
}

.bench-pokemon {
    text-align: center;
}

.bench img {
    width: 70px;
    height: auto;
    cursor: pointer;
    transition: transform 0.3s ease;
    display: block;
    margin: 0 auto;
}

.bench img:hover {
    transform: scale(1.1);
}

.attack-list {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    margin-top: 15px;
    gap: 10px;
}

.attack-list form {
    display: inline-block;
}

.attack-list button {
    padding: 10px 15px;
    border: none;
    border-radius: 10px;
    background-color: #007bff;
    color: white;
    cursor: pointer;
}

.attack-list button:hover {
    background-color: #0056b3;
}

.chat-box {
    flex: 1;
    max-width: 350px;
    background: #fff;
    padding: 20px;
    border-radius: 15px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    height: fit-content;
}

.chat-box h4 {
    margin-top: 0;
}

.chat-box p {
    margin: 5px 0;
    font-family: monospace;
    font-size: 0.9rem;
}

.victory {
    font-size: 1.5rem;
    color: green;
    margin-top: 20px;
}
</style>

<div class="battle-container">
    <div class="left-panel">
        <h2>¡Batalla Pokémon!</h2>

        <div class="teams">
            <!-- Usuario -->
            <div class="team">
                <h3>Tu equipo</h3>
                <g:if test="${battle.userTeam[battle.currentUserPokemon].hp > 0}">
                    <img class="active-pokemon" src="${battle.userTeam[battle.currentUserPokemon].imageUrl}" alt="${battle.userTeam[battle.currentUserPokemon].name}">
                    <p>${battle.userTeam[battle.currentUserPokemon].name} - HP: ${battle.userTeam[battle.currentUserPokemon].hp}</p>
                </g:if>

                <div class="bench">
                    <g:each var="poke" in="${battle.userTeam}" status="i">
                        <g:if test="${i != battle.currentUserPokemon && poke.hp > 0}">
                            <div class="bench-pokemon">
                                <img src="${poke.imageUrl}" alt="${poke.name}" title="${poke.name}" onclick="confirmSwitch(${i}, '${poke.name}')"/>
                                <p>${poke.hp} HP</p>
                            </div>
                        </g:if>
                    </g:each>
                </div>

                <div class="attack-list">
                    <g:each in="${battle.userTeam[battle.currentUserPokemon].attacks}" var="atk">
                        <g:form controller="battle" action="attack">
                            <input type="hidden" name="attackName" value="${atk.name}"/>
                            <button type="submit">${atk.name} (${atk.damage})</button>
                        </g:form>
                    </g:each>
                </div>
            </div>

            <!-- IA -->
            <div class="team">
                <h3>Equipo IA</h3>
                <g:if test="${battle.iaTeam[battle.currentIaPokemon].hp > 0}">
                    <img class="active-pokemon" src="${battle.iaTeam[battle.currentIaPokemon].imageUrl}" alt="${battle.iaTeam[battle.currentIaPokemon].name}">
                    <p>${battle.iaTeam[battle.currentIaPokemon].name} - HP: ${battle.iaTeam[battle.currentIaPokemon].hp}</p>
                </g:if>

                <div class="bench">
                    <g:each var="poke" in="${battle.iaTeam}" status="i">
                        <g:if test="${i != battle.currentIaPokemon && poke.hp > 0}">
                            <div class="bench-pokemon">
                                <img src="${poke.imageUrl}" alt="${poke.name}" title="${poke.name}"/>
                                <p>${poke.hp} HP</p>
                            </div>
                        </g:if>
                    </g:each>
                </div>
            </div>
        </div>

        <g:if test="${battle.result}">
            <div class="victory">${battle.result}</div>
        </g:if>
    </div>

    <!-- Chat -->
    <div class="chat-box">
        <h4>Historial de batalla</h4>
        <g:each in="${battle.history}" var="line">
            <p>${line}</p>
        </g:each>
    </div>
</div>

<script>
    function confirmSwitch(index, name) {
        if (confirm("¿Quieres cambiar a " + name + "?")) {
            window.location.href = "${createLink(controller: 'battle', action: 'changePokemon')}?index=" + index + "&team=user";
        }
    }
</script>
