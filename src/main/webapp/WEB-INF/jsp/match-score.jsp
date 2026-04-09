<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
<h2>${match.player1.name} vs ${match.player2.name}</h2>
<p>Points: ${score.playerOnePoints} : ${score.playerTwoPoints}</p>
<form method="post">
  <input type="hidden" name="id" value="${param.id}">
  <button name="player" value="1">+1 Player 1</button>
  <button name="player" value="2">+1 Player 2</button>
</form>
</body>
</html>
