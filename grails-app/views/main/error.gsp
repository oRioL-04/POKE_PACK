<meta name="layout" content="main"/>
<h2>Error</h2>
<style>
    .error-container {
        text-align: center;
        margin-top: 50px;
    }
    .error-message {
        font-size: 1.5rem;
        color: #d32f2f;
        margin-bottom: 20px;
    }
    .error-button {
        background-color: #ffcb05;
        color: #333;
        padding: 10px 20px;
        border: none;
        border-radius: 5px;
        font-size: 1rem;
        font-weight: bold;
        cursor: pointer;
        text-decoration: none;
    }
    .error-button:hover {
        background-color: #ef5350;
        color: white;
    }
</style>
<div class="error-container">
    <p class="error-message">${flash.message}</p>
    <g:link class="error-button" controller="Main" action="menu">Aceptar</g:link>
</div>
