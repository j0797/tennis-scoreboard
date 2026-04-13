<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head><title>Finished Matches</title></head>
<body>
<h1>Completed Matches</h1>

<form method="get">
    <label>
        <input type="text" name="playerName" placeholder="Player name" value="${param.playerName}">
    </label>
    <button type="submit">Filter</button>
</form>

<table border="1">
    <tr>
        <th>Player 1</th>
        <th>Player 2</th>
        <th>Winner</th>
    </tr>
    <c:forEach var="match" items="${matches}">
        <tr>
            <td>${match.player1.name}</td>
            <td>${match.player2.name}</td>
            <td>${match.winner != null ? match.winner.name : "—"}</td>
        </tr>
    </c:forEach>
</table>

<c:if test="${currentPage > 1}">
    <a href="?page=${currentPage-1}&playerName=${param.playerName}">Previous</a>
</c:if>
<c:if test="${currentPage < totalPages}">
    <a href="?page=${currentPage+1}&playerName=${param.playerName}">Next</a>
</c:if>
</body>
</html>