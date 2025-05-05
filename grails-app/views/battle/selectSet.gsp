<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 5/4/25
  Time: 7:20 PM
--%>

<meta name="layout" content="main"/>
<h2 class="page-title">Selecciona una colección</h2>

<style>
    .search-bar {
        margin: 20px auto;
        text-align: center;
    }
    .search-bar input {
        width: 50%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 5px;
        font-size: 1rem;
    }
    .grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 25px;
        padding: 30px;
        background-color: #f0f0f0;
        border-radius: 15px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }
    .set-card {
        background: linear-gradient(135deg, #ffffff, #f9f9f9);
        padding: 20px;
        border-radius: 15px;
        text-align: center;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }
    .set-card img {
        width: 120px;
        height: auto;
        margin-bottom: 15px;
        border-radius: 10px;
        border: 2px solid #ddd;
    }
    .set-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
    }
    .set-card p {
        font-size: 1.1rem;
        color: #333;
        margin: 10px 0;
    }
    .set-card a {
        display: inline-block;
        margin-top: 10px;
        background-color: #ffffff;
        color: white;
        padding: 10px 15px;
        border-radius: 8px;
        text-decoration: none;
        font-weight: bold;
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .set-card a:hover {
        background-color: #ffffff;
        transform: scale(1.05);
    }
</style>

<div class="search-bar">
    <input type="text" id="searchInput" placeholder="Buscar colección...">
</div>

<div class="grid" id="setGrid">
    <g:each in="${sets}" var="set">
        <div class="set-card">
            <g:link controller="Battle" action="selectCards" params="[setId: set.setId]">
                <img src="${set.logoUrl}" alt="${set.name}" />
                <p>${set.name}</p>
            </g:link>
        </div>
    </g:each>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const searchInput = document.getElementById("searchInput");
        const setGrid = document.getElementById("setGrid");
        const setCards = setGrid.querySelectorAll(".set-card");

        searchInput.addEventListener("input", function () {
            const query = searchInput.value.toLowerCase();
            setCards.forEach(card => {
                const setName = card.querySelector("p").textContent.toLowerCase();
                if (setName.includes(query)) {
                    card.style.display = "block"; // Show the element
                } else {
                    card.style.display = "none"; // Hide the element
                }
            });
        });
    });
</script>