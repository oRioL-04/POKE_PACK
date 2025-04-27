<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión</title>
    <style>
    body {
        font-family: 'Arial', sans-serif;
        background-color: #f8f9fa;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        margin: 0;
    }

    .login-container {
        background-color: #fff;
        padding: 30px;
        border-radius: 8px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        width: 100%;
        max-width: 400px;
    }

    h1 {
        color: #333;
        text-align: center;
        margin-bottom: 20px;
    }

    .form-group {
        margin-bottom: 15px;
    }

    .form-group label {
        font-weight: bold;
        margin-bottom: 5px;
    }

    .form-control {
        width: 100%;
        padding: 10px;
        border-radius: 5px;
        border: 1px solid #ccc;
    }

    .button {
        background-color: #007bff;
        color: white;
        padding: 10px;
        width: 100%;
        border: none;
        border-radius: 5px;
        cursor: pointer;
    }

    .button:hover {
        background-color: #0056b3;
    }

    .error-message {
        color: red;
        font-weight: bold;
        margin-bottom: 15px;
    }
    </style>
</head>
<body>
<div class="login-container">
    <h1>Iniciar Sesión</h1>

    <g:if test="${flash.message}">
        <div class="error-message">${flash.message}</div>
    </g:if>

    <g:form controller="auth" action="loginSubmit">
        <div class="form-group">
            <label for="username">Usuario:</label>
            <g:textField name="username" id="username" class="form-control"/>
        </div>

        <div class="form-group">
            <label for="password">Contraseña:</label>
            <g:passwordField name="password" id="password" class="form-control"/>
        </div>

        <g:submitButton name="login" value="Iniciar Sesión" class="button"/>
    </g:form>

    <p style="text-align: center;">¿No tienes cuenta? <g:link controller="auth" action="register">Regístrate aquí</g:link></p>
</div>
</body>
</html>
