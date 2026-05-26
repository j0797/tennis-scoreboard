package com.example.tennisscoreboard.service.impl;

import com.example.tennisscoreboard.model.MatchScore;
import com.example.tennisscoreboard.model.OngoingMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchScoreCalculationService {

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // TODO: Класс содержит в себе всю бизнес-логику по подсчёту очков, геймов и сетов.
    // Объекты, которыми он оперирует (`MatchScore`, `OngoingMatch`), являются "анемичными" моделями —
    // простыми контейнерами данных практически без собственного поведения. Сервис напрямую читает и записывает их поля.
    // Это главная архитектурная проблема этой части логики. По этим причинам:
    //
    //  - Нарушение инкапсуляции: Данные (в `MatchScore`) и поведение (в `MatchScoreCalculationService`) полностью разделены.
    //  Любой другой сервис может так же напрямую изменить счёт матча, и объект `MatchScore` не сможет себя защитить.
    //  - Процедурный стиль: Вместо объектно-ориентированного подхода, где объекты сами управляют своим состоянием
    //  (и начисление очков происходит в духе `matchScore.setPlayerOnePoints(...)`), получается процедурный код,
    //  который манипулирует внешними структурами данных.
    //  - Жёсткая связанность (Tight Coupling) и низкая связность (Low Cohesion):
    //  Сервис тесно связан с внутренним устройством `OngoingMatch`. При этом логика,
    //  относящаяся к одному понятию (счёт), размазана по разным классам (модели и сервису).
    //  - Сложность тестирования: Чтобы протестировать один конкретный сценарий (например, переход от "ровно" к "преимуществу"),
    //  нужно разбираться во множестве `if` и переходов по методам. Это сложно и хрупко.
    //
    // Как исправить: Провести рефакторинг классов моделей с переходом к "богатой" доменной модели.

    // TODO: Сервис слишком много знает о внутреннем устройстве класса OngoingMatch, а также полностью управляет внутренним состоянием MatchScore,
    // что повышает связанность кода и делает его сложнее в поддержке и рефакторинге.
    // Решение — реализация методов для работы с собственными данными в классах доменной модели.
    // Также имеет смысл перейти на реализацию в ООП стиле — провести детальную декомпозицию предметной области,
    // выделить соответствующие абстракции и наделить их нужным состоянием и поведением.

    // TODO: Класс "кодирует" счёт в гейме, что способствует процедурному стилю. Поскольку в гейме особый счёт,
    // ООП подходом было бы создать специальный enum с константами ZERO, FIFTEEN, THIRTY, FORTY, ADVANTAGE для хранения счёта в гейме.

    // Составные условия из `if` требуют усилий для понимания. Сложные логические выражения ухудшают читаемость кода
    // и увеличивают вероятность ошибки при их написании или изменении. Лучше выносить такие условия
    // в отдельный `private`-метод с понятным названием, которое описывает бизнес-правило.

    private static final Logger log = LoggerFactory.getLogger(MatchScoreCalculationService.class);

    private static final int POINTS_DEUCE_THRESHOLD = 3;
    private static final int GAMES_TO_WIN_SET = 6;
    private static final int MIN_GAMES_DIFFERENCE_FOR_SET = 2;
    private static final int TIEBREAK_TRIGGER_GAMES = 6;
    private static final int TIEBREAK_POINTS_TO_WIN = 7;
    private static final int TIEBREAK_MIN_DIFFERENCE = 2;
    private static final int SETS_TO_WIN_MATCH = 2;

    public static void addPoint(OngoingMatch match, int playerNumber) {
        if (match.isMatchOver()) {
            log.warn("Attempt to add point to finished match");
            return;
        }
        log.debug("Adding point");

        boolean isPlayerOne = (playerNumber == 1);

        if (match.isTieBreak()) {
            handleTieBreakPoint(match, isPlayerOne);
        } else {
            handleRegularPoint(match, isPlayerOne);
        }
    }

    private static void handleRegularPoint(OngoingMatch match, boolean isPlayerOne) {

        MatchScore score = match.getScore();
        int scoringPoints = isPlayerOne ? score.getPlayerOnePoints() : score.getPlayerTwoPoints();
        int opponentPoints = isPlayerOne ? score.getPlayerTwoPoints() : score.getPlayerOnePoints();

        // вместо вложенных if лучше писать 'if (внешнее условие && внутреннее условие)', а также выносить их во вспомогательный метод
        if (scoringPoints == POINTS_DEUCE_THRESHOLD) {
            if (opponentPoints == POINTS_DEUCE_THRESHOLD) {
                match.setDeuce(true);

                // Здесь уже два уровня вложенности (if внутри if) и вызывается метод, в котором в свою очередь тоже
                // ветвистая if-else логика с двумя уровнями вложенности — такой код сложно читать, тестировать и поддерживать.
                handleDeuce(match, isPlayerOne);
            } else {
                winGame(match, isPlayerOne);
            }
        } else {
            if (isPlayerOne) {
                score.setPlayerOnePoints(scoringPoints + 1);
            } else {
                score.setPlayerTwoPoints(scoringPoints + 1);
            }
        }
    }

    private static void handleDeuce(OngoingMatch match, boolean isPlayerOne) {
        MatchScore score = match.getScore();

        // вместо вложенных if лучше писать 'if (внешнее условие && внутреннее условие)', а также выносить их во вспомогательный метод
        if (isPlayerOne) {
            if (score.isPlayerOneAdvantage()) {
                resetAdvantage(score);
                winGame(match, true);
                match.setDeuce(false);
            } else if (score.isPlayerTwoAdvantage()) {
                resetAdvantage(score);
            } else {
                score.setPlayerOneAdvantage(true);
            }
        } else {
            if (score.isPlayerTwoAdvantage()) {
                resetAdvantage(score);
                log.debug("Advantage lost, back to deuce");
                winGame(match, false);
                match.setDeuce(false);
            } else if (score.isPlayerOneAdvantage()) {
                resetAdvantage(score);
                log.debug("Advantage lost, back to deuce");
            } else {
                score.setPlayerTwoAdvantage(true);
            }
        }
    }

    private static void winGame(OngoingMatch match, boolean isPlayerOne) {
        MatchScore score = match.getScore();

        // Больше подошли бы названия currentPlayerOneGames и currentPlayerTwoGames
        int oldPlayerOneGames = score.getPlayerOneGames();
        int oldPlayerTwoGames = score.getPlayerTwoGames();

        // Составные условия из if лучше выносить во вспомогательный метод с понятным названием
        if (oldPlayerOneGames == TIEBREAK_TRIGGER_GAMES && oldPlayerTwoGames == TIEBREAK_TRIGGER_GAMES) {
            match.setTieBreak(true);
            score.setPlayerOnePoints(0);
            score.setPlayerTwoPoints(0);
            resetAdvantage(score);
            match.setDeuce(false);
            log.debug("Tiebreak started");
            return;
        }
        score.setPlayerOnePoints(0);
        score.setPlayerTwoPoints(0);
        resetAdvantage(score);
        match.setDeuce(false);

        if (isPlayerOne) {
            score.setPlayerOneGames(oldPlayerOneGames + 1);
        } else {
            score.setPlayerTwoGames(oldPlayerTwoGames + 1);
        }
        log.debug("Player {} wins game. Games: {}-{}", isPlayerOne ? "1" : "2",
                score.getPlayerOneGames(), score.getPlayerTwoGames());
        checkSetWinner(match);
    }

    private static void checkSetWinner(OngoingMatch match) {
        MatchScore score = match.getScore();

        // Названия p1Games и p2Games сложнее перепутать при чтении и использовании, чем например firstPlayerGames и secondPlayerGames.
        // Лучше давать переменным полные и легко читаемые имена.
        int p1Games = score.getPlayerOneGames();
        int p2Games = score.getPlayerTwoGames();

        // Составные условия из if лучше выносить во вспомогательный метод с понятным названием
        if (p1Games >= GAMES_TO_WIN_SET && (p1Games - p2Games) >= MIN_GAMES_DIFFERENCE_FOR_SET) {
            winSet(match, true);
        } else if (p2Games >= GAMES_TO_WIN_SET && (p2Games - p1Games) >= MIN_GAMES_DIFFERENCE_FOR_SET) {
            winSet(match, false);
        }
    }

    private static void winSet(OngoingMatch match, boolean isPlayerOne) {
        MatchScore score = match.getScore();

        // Тело блоков if-else всегда следует оборачивать в {}
        if (isPlayerOne) {
            score.setPlayerOneSets(score.getPlayerOneSets() + 1);
        } else {
            score.setPlayerTwoSets(score.getPlayerTwoSets() + 1);
        }

        log.debug("Player {} wins set. Sets: {}-{}", isPlayerOne ? "1" : "2",
                score.getPlayerOneSets(), score.getPlayerTwoSets());

        score.setPlayerOneGames(0);
        score.setPlayerTwoGames(0);
        score.setPlayerOneTieBreakPoints(0);
        score.setPlayerTwoTieBreakPoints(0);
        match.setTieBreak(false);

        // Составные условия из if лучше выносить во вспомогательный метод с понятным названием
        if (score.getPlayerOneSets() == SETS_TO_WIN_MATCH || score.getPlayerTwoSets() == SETS_TO_WIN_MATCH) {
            match.setMatchOver(true);
            log.debug("Match over after set win");
        }
    }

    private static void handleTieBreakPoint(OngoingMatch match, boolean isPlayerOne) {
        MatchScore score = match.getScore();

        // Тело блоков if-else всегда следует оборачивать в {}
        if (isPlayerOne) score.setPlayerOneTieBreakPoints(score.getPlayerOneTieBreakPoints() + 1);
        else score.setPlayerTwoTieBreakPoints(score.getPlayerTwoTieBreakPoints() + 1);

        // Лучше давать переменным полные и легко читаемые имена. Например, можно просто winnerScore и loserScore
        int scoringTB = isPlayerOne ? score.getPlayerOneTieBreakPoints() : score.getPlayerTwoTieBreakPoints();
        int opponentTB = isPlayerOne ? score.getPlayerTwoTieBreakPoints() : score.getPlayerOneTieBreakPoints();
        log.debug("After tiebreak point: P1 TB={}, P2 TB={}",
                match.getScore().getPlayerOneTieBreakPoints(),
                match.getScore().getPlayerTwoTieBreakPoints());

        // Составные условия из if лучше выносить во вспомогательный метод с понятным названием
        if (scoringTB >= TIEBREAK_POINTS_TO_WIN && (scoringTB - opponentTB) >= TIEBREAK_MIN_DIFFERENCE) {
            winSet(match, isPlayerOne);
        }
    }

    private static void resetAdvantage(MatchScore score) {
        score.setPlayerOneAdvantage(false);
        score.setPlayerTwoAdvantage(false);
    }
}