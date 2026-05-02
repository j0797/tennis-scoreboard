package com.example.tennisscoreboard.model;

import com.example.tennisscoreboard.entity.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // сеттеры позволяют бесконтрольно изменять состояние модели
public class OngoingMatch {

    // Класс является анемичной моделью — он является лишь контейнером для данных, а вся логика находится в сервисном слое.
        // Если бы у класса вместо простых сеттеров были методы, совершающие необходимую работу над полями,
        // это больше соответствовало бы ООП стилю и обязанности класса (в роли доменной модели).
        // Также, эту часть логики было бы легче тестировать.
        // (см. файл "Анемичная vs Богатая модель предметной области.md" в этом же пакете)

    // Класс хранит ссылки на JPA-сущности (`Player`). Использование объектов JPA Entity в доменной логике
        // создаёт прямую зависимость доменного слоя от слоя персистентности (долговременного хранения данных)
        // и смешивает слои приложения, что нарушает чистоту архитектуры.
        // Это может привести к проблемам с ленивой загрузкой (`LazyInitializationException`)
        // или к неожиданным изменениям в базе данных, если состояние `Player` будет изменено в ходе бизнес-логики.
        // Доменные модели должны оперировать другими доменными моделями, а не сущностями, привязанными к базе данных.

    private Player playerOne;
    private Player playerTwo;
    private Player winner;
    private MatchScore score;
    private boolean matchOver;
    private boolean isTieBreak;
    private boolean isDeuce;

    public OngoingMatch(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.score = new MatchScore();
        this.matchOver = false;
        this.isTieBreak = false;
        this.isDeuce = false;
    }
}
