<meta name="layout" content="main"/>
<h2>Solicitar Intercambio</h2>

<style>
form {
    display: flex;
    flex-direction: column;
    gap: 15px;
    max-width: 400px;
    margin: 0 auto;
}
label {
    font-weight: bold;
}
select, button {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    font-size: 1rem;
}
button {
    background-color: #ffcb05;
    color: #333;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s;
}
button:hover {
    background-color: #ef5350;
    color: white;
}
</style>

<form action="${createLink(controller: 'trade', action: 'solicitarIntercambio')}" method="post">
    <label for="setId">Selecciona un set:</label>
    <select id="setId" name="setId" required>
        <option value="">Selecciona un set</option>
    </select>

    <label for="cardId">Selecciona tu carta:</label>
    <select id="cardId" name="cardId" required>
        <option value="">Selecciona una carta</option>
    </select>

    <label for="targetUserId">Selecciona un usuario:</label>
    <select id="targetUserId" name="targetUserId" required>
        <option value="">Selecciona un usuario</option>
    </select>

    <label for="targetCardId">Selecciona la carta del usuario:</label>
    <select id="targetCardId" name="targetCardId" required>
        <option value="">Selecciona una carta</option>
    </select>

    <button type="submit">Enviar Solicitud</button>
</form>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const setIdSelect = document.getElementById("setId");
        const cardIdSelect = document.getElementById("cardId");
        const targetUserIdSelect = document.getElementById("targetUserId");
        const targetCardIdSelect = document.getElementById("targetCardId");

        // Cargar sets
        fetch("${createLink(controller: 'trade', action: 'obtenerSets')}")
            .then(response => response.json())
            .then(sets => {
                sets.forEach(set => {
                    if (set && set.setId && set.name) {
                        const option = document.createElement("option");
                        option.value = set.setId;
                        option.textContent = set.name;
                        setIdSelect.appendChild(option);
                    }
                });
            });

        // Cargar cartas del usuario
        setIdSelect.addEventListener("change", function () {
            cardIdSelect.innerHTML = '<option value="">Selecciona una carta</option>';
            targetUserIdSelect.innerHTML = '<option value="">Selecciona un usuario</option>';
            targetCardIdSelect.innerHTML = '<option value="">Selecciona una carta</option>';

            if (this.value) {
                fetch(`${createLink(controller: 'trade', action: 'obtenerCartasPorSet')}?setId=${this.value}`)
                    .then(response => response.json())
                    .then(cartas => {
                        if (Array.isArray(cartas)) {
                            cartas
                                .filter(carta => carta && typeof carta.name === 'string' && carta.cardId && carta.quantity != null)
                                .forEach(carta => {
                                    const option = document.createElement("option");
                                    option.value = carta.cardId;
                                    option.textContent = `${carta.name} (${carta.quantity})`;
                                    cardIdSelect.appendChild(option);
                                });
                        } else {
                            console.error("Respuesta inesperada de cartas:", cartas);
                        }
                    })
                    .catch(error => {
                        console.error("Error al obtener cartas del usuario:", error);
                    });

            }
        });

        // Cargar usuarios
        cardIdSelect.addEventListener("change", function () {
            targetUserIdSelect.innerHTML = '<option value="">Selecciona un usuario</option>';
            targetCardIdSelect.innerHTML = '<option value="">Selecciona una carta</option>';

            fetch("${createLink(controller: 'trade', action: 'obtenerUsuarios')}")
                .then(response => response.json())
                .then(usuarios => {
                    usuarios.forEach(usuario => {
                        if (usuario && usuario.id && usuario.username) {
                            const option = document.createElement("option");
                            option.value = usuario.id;
                            option.textContent = usuario.username;
                            targetUserIdSelect.appendChild(option);
                        }
                    });
                });
        });

        // Cargar cartas del otro usuario
        targetUserIdSelect.addEventListener("change", function () {
            targetCardIdSelect.innerHTML = '<option value="">Selecciona una carta</option>';

            if (this.value && setIdSelect.value) {
                fetch(`${createLink(controller: 'trade', action: 'obtenerCartasPorSet')}?setId=${this.value}`)
                    .then(response => response.json())
                    .then(cartas => {
                        if (Array.isArray(cartas)) {
                            cartas
                                .filter(carta => carta && typeof carta.name === 'string' && carta.cardId && carta.quantity != null)
                                .forEach(carta => {
                                    const option = document.createElement("option");
                                    option.value = carta.cardId;
                                    option.textContent = `${carta.name} (${carta.quantity})`;
                                    cardIdSelect.appendChild(option);
                                });
                        } else {
                            console.error("Respuesta inesperada de cartas:", cartas);
                        }
                    })
                    .catch(error => {
                        console.error("Error al obtener cartas del usuario:", error);
                    });


            }
        });
    });
</script>
