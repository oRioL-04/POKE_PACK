<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 5/8/25
  Time: 8:37 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="main"/>
<h2 class="page-title">Error de Rareza</h2>
<style>
    .error-container {
        text-align: center;
        margin-top: 50px;
    }
    .error-message {
        color: #ef5350;
        font-size: 1.5rem;
        margin-bottom: 20px;
    }
    .btn-back {
        background: #ef5350;
        color: #fff;
        padding: 10px 20px;
        border-radius: 6px;
        text-decoration: none;
        font-weight: bold;
        transition: background 0.3s;
    }
    .btn-back:hover {
        background: #ffcb05;
        color: #333;
    }
</style>
<div class="error-container">
    <p class="error-message">Las cartas seleccionadas tienen rarezas diferentes. No se puede realizar el intercambio.</p>
    <a href="${createLink(controller: 'trade', action: 'intercambios')}" class="btn-back">Volver a Intercambios</a>
</div>