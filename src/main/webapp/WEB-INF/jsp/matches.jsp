<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Scoreboard | Finished Matches</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
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
        <h1>Matches</h1>
        <form method="get" action="${pageContext.request.contextPath}/matches" class="input-container">
            <label>
                <input class="input-filter"
                       name="filter_by_player_name"
                       placeholder="Filter by name"
                       type="text"
                       value="${param.filter_by_player_name}">
            </label>
            <button class="btn-filter" type="submit">Filter</button>
            <button class="btn-filter" type="submit" onclick="this.form.filter_by_player_name.value=''">Reset Filter
            </button>
        </form>
        <table class="table-matches">
            <tr>
                <th>Player One</th>
                <th>Player Two</th>
                <th>Winner</th>
            </tr>
            <c:forEach var="match" items="${pagination.items}">
                <tr>
                    <td>${match.player1.name}</td>
                    <td>${match.player2.name}</td>
                    <td>
                        <c:choose>
                            <c:when test="${match.winner != null}">
                                <span class="winner-name-td">${match.winner.name}</span>
                            </c:when>
                            <c:otherwise>—</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty pagination.items}">
                <tr>
                    <td colspan="3">No matches found.</td>
                </tr>
            </c:if>
        </table>
        <div class="pagination">
            <c:if test="${pagination.currentPage > 1}">
                <a class="prev"
                   href="?page=${pagination.currentPage-1}&filter_by_player_name=${param.filter_by_player_name}">
                    &lt;
                </a>
            </c:if>

            <c:if test="${pagination.currentPage > 3}">
                <a class="num-page"
                   href="?page=1&filter_by_player_name=${param.filter_by_player_name}">1</a>
                <c:if test="${pagination.currentPage > 4}">
                    <span class="ellipsis">...</span>
                </c:if>
            </c:if>

            <c:forEach begin="1" end="${pagination.totalPages}" var="pageNum">
                <c:if test="${pageNum >= pagination.currentPage - 2
                   && pageNum <= pagination.currentPage + 2}">
                    <c:choose>
                        <c:when test="${pageNum == pagination.currentPage}">
                            <a class="num-page current"
                               href="?page=${pageNum}&filter_by_player_name=${param.filter_by_player_name}">${pageNum}</a>
                        </c:when>
                        <c:otherwise>
                            <a class="num-page"
                               href="?page=${pageNum}&filter_by_player_name=${param.filter_by_player_name}">${pageNum}</a>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </c:forEach>

            <c:if test="${pagination.currentPage < pagination.totalPages - 2}">
                <c:if test="${pagination.currentPage < pagination.totalPages - 3}">
                    <span class="ellipsis">...</span>
                </c:if>
                <a class="num-page"
                   href="?page=${pagination.totalPages}&filter_by_player_name=${param.filter_by_player_name}">${pagination.totalPages}</a>
            </c:if>

            <c:if test="${pagination.currentPage < pagination.totalPages}">
                <a class="next"
                   href="?page=${pagination.currentPage+1}&filter_by_player_name=${param.filter_by_player_name}">
                    &gt;
                </a>
            </c:if>
        </div>
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