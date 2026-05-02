package com.example.tennisscoreboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "matches") // "matches" является зарезервированным словом в некоторых СУБД. Здесь проблем не будет, но лучше не выбирать такие названия. (см. файл "Использование зарезервированных слов в качестве названий в БД.md" в этом же пакете)
@Check(constraints = "player1_id <> player2_id") // стоит также добавить проверку, что победитель один из игроков
@Getter
@Setter // для игроков и победителя тоже не нужен — достаточно конструктора с этими параметрами
@NoArgsConstructor // для Hibernate достаточно protected
@AllArgsConstructor // не нужен — позволяет создать объект с установленным id
@Builder // 1. не нужен — позволяет создать объект с установленным id 2. избыточен, так как объект матча создаётся в одном месте (а не поэтапно), а также имеет достаточно простой конструктор.
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // если в логике проекта сравнение не используется, то можно оставить реализацию по умолчанию — то есть вообще убрать аннотацию
public class Match {

    // Здесь игроки названы player1 и player2, а в NewMatchController — p1 и p2, а в OngoingMatch и OngoingMatchesService — playerOne и playerTwo. Лучше везде использовать одинаковые названия. Удачным вариантом будет firstPlayer и secondPlayer.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include // toString только с id будет очень неинформативным. Особенно пока id == null.
    @EqualsAndHashCode.Include // при сравнении только по id все несохранённые матчи (для которых БД ещё не сгенерировала id) будут считаться одинаковыми
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player1_id", nullable = false)
    private Player player1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player2_id", nullable = false)
    private Player player2;

    @ManyToOne(fetch = FetchType.LAZY) // поскольку по ТЗ сохраняются только завершённые матчи, победитель тоже является обязательным полем — стоит добавить optional = false
    @JoinColumn(name = "winner_id")
    private Player winner;

    public Match(Player player1, Player player2, Player winner) {
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
    }
}