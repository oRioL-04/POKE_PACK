<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 1/5/25
  Time: 14:44
--%>

<meta name="layout" content="main"/>
<h2>Cartas en la colección ${set.name}</h2>
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
</style>
<div class="grid">
    <g:each in="${cards}" var="card">
        <div class="set-card">
            <img src="${card.imageUrl}" alt="${card.name}" onerror="this.src='/images/default.png'"/>
            <p>${card.name}</p>
            <p>Cantidad: ${card.quantity}</p>
        </div>
    </g:each>
</div>
