<%--
  Created by IntelliJ IDEA.
  User: uliafilippova
  Date: 05.04.2026
  Time: 02:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>New Match</title></head>
<body>
<form action="new-match" method="post">
    <label>Player 1: <input type="text" name="playerOneName" required></label><br/>
    <label>Player 2: <input type="text" name="playerTwoName" required></label><br/>
    <input type="submit" value="Start">
</form>
</body>
</html>