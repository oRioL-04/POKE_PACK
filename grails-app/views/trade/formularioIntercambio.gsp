<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="main"/>
<h2 class="page-title">Iniciar Intercambio</h2>
<style>
    .page-title {
        text-align: center;
        color: #333;
        margin-top: 20px;
        font-size: 2rem;
    }
    .form-container {
        max-width: 400px;
        margin: 30px auto;
        background: #fff;
        padding: 30px 25px;
        border-radius: 12px;
        box-shadow: 0 4px 16px rgba(0,0,0,0.08);
    }
    label {
        font-weight: bold;
        margin-top: 10px;
        display: block;
    }
    select, button {
        width: 100%;
        padding: 10px;
        margin-top: 8px;
        border-radius: 6px;
        border: 1px solid #ccc;
        font-size: 1rem;
    }
    button, .g-submitButton {
        background: #ef5350;
        color: #fff;
        font-weight: bold;
        border: none;
        cursor: pointer;
        margin-top: 18px;
        transition: background 0.2s;
    }
    button:hover, .g-submitButton:hover {
        background: #ffcb05;
        color: #333;
    }
</style>
<div class="form-container">
        <g:form controller="trade" action="cargarCartasIntercambio" method="get">
            <label>Selecciona el usuario con quien intercambiar:</label>
            <g:select name="targetUserId" from="${usuarios}" optionKey="id" optionValue="username" required="true"/>

            <label>Selecciona el set:</label>
            <div style="position: relative;">
                <input type="text" id="searchSetInput" placeholder="Buscar set..." style="width: 100%; padding: 10px; margin-bottom: 8px; border-radius: 6px; border: 1px solid #ccc; font-size: 1rem;" oninput="filterSets()" />
                <select id="setDropdown" name="setId" style="width: 100%; padding: 10px; border-radius: 6px; border: 1px solid #ccc; font-size: 1rem;">
                    <g:each in="${sets}" var="set">
                        <option value="${set.setId}">${set.name}</option>
                    </g:each>
                </select>
            </div>
            <button type="submit" class="g-submitButton">Siguiente</button>
        </g:form>
    </div>
    <script>
        function filterSets() {
            const input = document.getElementById('searchSetInput').value.toLowerCase();
            const dropdown = document.getElementById('setDropdown');
            const options = dropdown.options;

            for (let i = 0; i < options.length; i++) {
                const optionText = options[i].text.toLowerCase();
                options[i].style.display = optionText.includes(input) ? '' : 'none';
            }
        }
    </script>