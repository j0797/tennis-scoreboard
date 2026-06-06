# Роадмап рефакторинга по файлам

Это упорядоченный список файлов, которые следует исправлять, следуя замечаниям в комментариях.

### Шаг 1: Доменная модель

Начать с ядра: исправить структуры данных и перенести в них бизнес-логику.

-   `/model/MatchScore.java`
-   `/model/OngoingMatch.java`
-   `/service/MatchScoreCalculationService.java`

### Шаг 2: Слой доступа к данным (DAO) и Сущности

Отрефакторить слой, отвечающий за связь с базой данных.

-   `/entity/Player.java`
-   `/entity/Match.java`
-   `/dao/BaseDao.java`
-   `/dao/PlayerDao.java`
-   `/dao/MatchDao.java`

### Шаг 3: Слой передачи данных (DTO)

Модернизировать объекты для передачи данных. Этот шаг можно выполнить в любой момент, но лучше до контроллеров.

-   `/dto/PlayerDto.java`
-   `/dto/MatchDto.java`
-   `/dto/MatchScoreDisplayDto.java`
-   `/dto/PaginationResponseDto.java`

### Шаг 4: Сервисный слой

Заняться сервисами и внедрить зависимости. Для каждого сервиса сначала создать интерфейс, затем исправить реализацию.

-   `/service/PlayerService.java`
-   `/service/FinishedMatchesPersistenceService.java`
-   `/service/OngoingMatchesService.java`

### Шаг 5: Мапперы и Утилиты

Обновить классы-помощники.

-   `/mapper/MatchScoreDisplayMapper.java`
-   `/util/Validator.java`
-   `/util/HibernateUtil.java`

### Шаг 6: Слой представления (Фильтры и Контроллеры)

Завершить рефакторинг исправлением "внешнего" слоя приложения.

-   `/controller/NewMatchController.java`
-   `/controller/MatchScoreController.java`
-   `/controller/FinishedMatchesController.java`
-   `/filter/TransactionFilter.java`

### Шаг 7: Интерфейс (JSP) и Тесты

Завершающий этап: исправить отображение и поведение на страницах, а также обновить тесты, чтобы они соответствовали новой "богатой" модели.

-   `/webapp/WEB-INF/jsp/new-match.jsp`
-   `/webapp/WEB-INF/jsp/matches.jsp`
-   `/webapp/WEB-INF/jsp/match-score.jsp`
-   `/test/java/com/example/tennisscoreboard/service/MatchScoreCalculationServiceTest.java`

