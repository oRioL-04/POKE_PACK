<%--
  Created by IntelliJ IDEA.
  User: oriol
  Date: 5/4/25
  Time: 7:20 PM
--%>

<meta name="layout" content="main"/>
<h2>Selecciona una colección</h2>
<ul>
    <g:each in="${sets}" var="set">
        <li>
            <g:link controller="Battle" action="selectCards" params="[setId: set.setId]">
                <img src="${set.logoUrl}" alt="${set.name}" style="width: 100px;"/>
                ${set.name}
            </g:link>
        </li>
    </g:each>
</ul>