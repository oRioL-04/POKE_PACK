<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 1/5/25
  Time: 13:41
--%>
<meta name="layout" content="main"/>
<h2>Tu Pokédex</h2>
<style>
    .collection-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 20px;
        padding: 20px;
    }
    .collection-card {
        text-align: center;
        background: #fff;
        border-radius: 10px;
        padding: 15px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
        transition: transform 0.3s ease, box-shadow 0.3s ease;
        cursor: pointer;
    }
    .collection-card:hover {
        transform: scale(1.05);
        box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
    }
    .collection-card img {
        height: 150px;
        width: 100%;
        object-fit: contain;
        margin-bottom: 10px;
    }
    .collection-card p {
        font-size: 1rem;
        font-weight: bold;
    }
    .collection-card small {
        color: #666;
        font-size: 0.9rem;
    }
</style>
<div class="collection-grid">
    <g:each in="${sets}" var="set">
        <g:link action="cartasPorColeccion" params="[setId: set.name]" class="collection-card">
            <img src="${set.logoUrl}" alt="${set.name}"/>
            <p>${set.name}</p>
            <small>${set.percentage}% completado</small>
        </g:link>
    </g:each>
</div>