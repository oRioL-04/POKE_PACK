<meta name="layout" content="main"/>
<h2>Tu Pokédex</h2>
<style>
.grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 20px;
    padding: 20px;
}
.set-card {
    background-color: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0,0,0,0.1);
    text-align: center;
}
.set-card img {
    width: 100%;
    height: 150px;
    object-fit: contain;
}
.set-card a {
    margin-top: 10px;
    background-color: #ffcb05;
    padding: 8px;
    border-radius: 8px;
    text-decoration: none;
    font-weight: bold;
    color: #333;
}
.set-card small {
    display: block;
    margin-top: 10px;
    color: #666;
    font-size: 0.9rem;
}
</style>
<div class="grid">
    <g:each in="${sets}" var="set">
        <div class="set-card">
            <img src="${set.logoUrl}" alt="${set.name}" onerror="this.src='/images/default.png'"/>
            <p>${set.name}</p>
            <small>${set.percentage}% completado</small>
            <g:link action="cartasPorColeccion" params="[setId: set.id]">
                Ver colección
            </g:link>
        </div>
    </g:each>
</div>
