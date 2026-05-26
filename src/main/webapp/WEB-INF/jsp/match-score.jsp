<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Scoreboard | Match Score</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto+Mono:wght@300&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</head>
<body>
<header class="header">
    <section class="nav-header">
        <div class="brand">
            <div class="nav-toggle">
                <img src="${pageContext.request.contextPath}/images/menu.png" alt="Logo" class="logo">
            </div>
            <span class="logo-text">TennisScoreboard</span>
        </div>
        <div>
            <nav class="nav-links">
                <a class="nav-link" href="${pageContext.request.contextPath}/">Home</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/matches">Matches</a>
            </nav>
        </div>
    </section>
</header>
<main>
    <div class="container">
        <h1>Current match</h1>
        <div class="current-match-image"></div>

        <c:choose>
            <c:when test="${matchOver}">
                <h2>Match Over! Winner: ${displayDto.winnerName}</h2>
            </c:when>
        </c:choose>

        <section class="score">
            <table class="table">
                <thead class="result">
                <tr>
                    <th class="table-text">Player</th>
                    <th class="table-text">Sets</th>
                    <th class="table-text">Games</th>
                    <th class="table-text">Points</th>
                    <c:if test="${not matchOver}">
                        <th class="table-text">Action</th>
                    </c:if>
                </tr>
                </thead>
                <tbody>
                <tr class="player1">
                    <td class="table-text">${displayDto.playerOneName}</td>
                    <td class="table-text">${displayDto.sets.split(':')[0]}</td>
                    <td class="table-text">${displayDto.games.split(':')[0]}</td>
                    <td class="table-text">
                        <c:choose>
                            <c:when test="${displayDto.tieBreak}">
                                ${displayDto.tieBreakPoints.split(':')[0]}
                            </c:when>
                            <c:otherwise>
                                ${displayDto.pointsPlayer1}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <c:if test="${not matchOver}">
                        <td class="table-text">
                            <form method="post" action="${pageContext.request.contextPath}/match-score">
                                <input type="hidden" name="uuid" value="${param.uuid}">
                                <button class="score-btn" name="player" value="1">Score</button>
                            </form>
                        </td>
                    </c:if>
                </tr>
                <tr class="player2">
                    <td class="table-text">${displayDto.playerTwoName}</td>
                    <td class="table-text">${displayDto.sets.split(':')[1]}</td>
                    <td class="table-text">${displayDto.games.split(':')[1]}</td>
                    <td class="table-text">
                        <c:choose>
                            <c:when test="${displayDto.tieBreak}">
                                ${displayDto.tieBreakPoints.split(':')[1]}
                            </c:when>
                            <c:otherwise>
                                ${displayDto.pointsPlayer2}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <c:if test="${not matchOver}">
                        <td class="table-text">
                            <form method="post" action="${pageContext.request.contextPath}/match-score">
                                <input type="hidden" name="uuid" value="${param.uuid}">
                                <button class="score-btn" name="player" value="2">Score</button>
                            </form>
                        </td>
                    </c:if>
                </tr>
                </tbody>
            </table>
        </section>

        <c:if test="${displayDto.tieBreak}">
            <p class="tiebreak-info">Tie-break! Points: ${displayDto.tieBreakPoints}</p>
        </c:if>
    </div>
</main>
<footer>
    <div class="footer">
        <p>&copy; Tennis Scoreboard, project from <a href="https://zhukovsd.github.io/java-backend-learning-course/">zhukovsd/java-backend-learning-course</a>
            roadmap.</p>
    </div>
</footer>
</body>
</html>