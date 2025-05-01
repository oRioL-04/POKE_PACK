<meta name="layout" content="none"/>
<h2>Saldo insuficiente</h2>
<style>
    body {
        margin: 0;
        font-family: Arial, sans-serif;
        background-color: #f9f9f9;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        text-align: center;
        flex-direction: column;
    }
    .message {
        font-size: 1.5rem;
        color: #d32f2f;
        margin-bottom: 20px;
        font-weight: bold;
    }
    .button {
        background-color: #ffcb05;
        color: #333;
        padding: 10px 20px;
        border: none;
        border-radius: 5px;
        font-size: 1rem;
        font-weight: bold;
        cursor: pointer;
        text-decoration: none;
        transition: background-color 0.3s ease, color 0.3s ease;
    }
    .button:hover {
        background-color: #ef5350;
        color: white;
    }
</style>
<div>
    <p class="message">No tienes saldo suficiente.</p>
    <g:link class="button" controller="Main" action="menu">Volver al inicio</g:link>
</div>