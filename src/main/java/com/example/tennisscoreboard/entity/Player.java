package com.example.tennisscoreboard.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "players") // можно задать индекс через аннотацию, чтобы у него было понятное имя — @Table(name = "players", indexes = @Index(...))
@Getter
@Setter // для name тоже не нужен — достаточно конструктора с одним параметром — name
@NoArgsConstructor // для Hibernate достаточно protected
@AllArgsConstructor // не нужен — позволяет создать объект с установленным id
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // если в логике проекта сравнение не используется, то можно оставить реализацию по умолчанию — то есть вообще убрать аннотацию
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(unique = true, nullable = false) // можно добавить length = 30, чтобы ограничения были одинаковыми и в бизнес-логике (валидаторе) и на уровне БД
    private String name;

    public Player(String name) {
        this.name = name;
    }
}