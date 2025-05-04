<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 5/4/25
  Time: 7:09 PM
--%>
<meta name="layout" content="main"/>
<h2>Combate Pokémon</h2>
<div style="display: flex; justify-content: space-around; align-items: center; margin-top: 20px;">
    <!-- User's Pokémon -->
    <div style="text-align: center;">
        <h3>Tu Pokémon</h3>
        <g:if test="${battle.userTeam && battle.userTeam[battle.currentUserPokemon]}">
            <img src="${battle.userTeam[battle.currentUserPokemon].imageUrl}"
                 alt="${battle.userTeam[battle.currentUserPokemon].name}"
                 style="width: 150px; height: auto;"/>
            <h4>${battle.userTeam[battle.currentUserPokemon].name}</h4>
            <p>HP: ${battle.userTeam[battle.currentUserPokemon].hp}</p>
            <div>
                <g:if test="${battle.userTeam[battle.currentUserPokemon].attacks?.size() > 0}">
                    <g:each in="${battle.userTeam[battle.currentUserPokemon].attacks}" var="attack">
                        <g:form controller="Battle" action="attack">
                            <input type="hidden" name="attackName" value="${attack.name}" />
                            <button type="submit">${attack.name} (${attack.damage} daño)</button>
                        </g:form>
                    </g:each>
                </g:if>
                <g:else>
                    <p>No hay ataques disponibles para este Pokémon.</p>
                </g:else>
            </div>
        </g:if>
    </div>

    <!-- VS Divider -->
    <div style="font-size: 2rem; font-weight: bold;">VS</div>

    <!-- IA's Pokémon -->
    <div style="text-align: center;">
        <h3>Pokémon de la IA</h3>
        <g:if test="${battle.iaTeam && battle.iaTeam[battle.currentIaPokemon]}">
            <img src="${battle.iaTeam[battle.currentIaPokemon].imageUrl}"
                 alt="${battle.iaTeam[battle.currentIaPokemon].name}"
                 style="width: 150px; height: auto;"/>
            <h4>${battle.iaTeam[battle.currentIaPokemon].name}</h4>
            <p>HP: ${battle.iaTeam[battle.currentIaPokemon].hp}</p>
        </g:if>
    </div>
</div>

<!-- Combat History -->
<div style="margin-top: 20px; padding: 10px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9;">
    <h3>Historial de Combate</h3>
    <ul>
        <g:each in="${battle.history}" var="event">
            <li>${event}</li>
        </g:each>
    </ul>
</div>

<!-- End Message -->
<g:if test="${battle.result}">
    <div style="position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%);
                background-color: rgba(0, 0, 0, 0.8); color: white; padding: 20px;
                border-radius: 10px; text-align: center; z-index: 1000;">
        <h2>${battle.result}</h2>
        <g:if test="${battle.result == '¡Has ganado el combate!'}">
            <p>¡Has recibido 100 monedas!</p>
        </g:if>
    </div>
    <script>
        setTimeout(() => {
            window.location.href = "${createLink(controller: 'Main', action: 'menu')}";
        }, 5000); // Redirect after 5 seconds
    </script>
</g:if>