<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Match Score</title>
</head>
<body>
<h2>${match.playerOne.name} vs ${match.playerTwo.name}</h2>

<p>Points: ${displayDto.pointsPlayer1} : ${displayDto.pointsPlayer2}</p>
<p>Games: ${displayDto.games}</p>
<p>Sets: ${displayDto.sets}</p>

<c:if test="${displayDto.tieBreak}">
    <p>Tie-break: ${displayDto.tieBreakPoints}</p>
</c:if>

<c:if test="${matchOver}">
    <p><strong>Match over! Winner: ${match.winner.name}</strong></p>
</c:if>


<c:if test="${not matchOver}">
    <form method="post">
        <input type="hidden" name="id" value="${param.id}">
        <button name="player" value="1">+1 Player 1</button>
        <button name="player" value="2">+1 Player 2</button>
    </form>
</c:if>

<a href="${pageContext.request.contextPath}/">Home</a>
</body>
</html>