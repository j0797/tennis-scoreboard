# Tennis Scoreboard

Веб-приложение, реализующее табло счёта теннисного матча.

[![Java](https://img.shields.io/badge/Java-21-blue)](https://adoptium.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 🚀 Технологии

- **Backend:** Java 21, Jakarta Servlets, Lombok, HikariCP, Hibernate, MapStruct
- **Логирование:** SLF4J + Logback
- **Тестирование:** JUnit 5
- **Frontend:** JSP, JSTL, CSS
- **База данных:** H2 (in-memory)
- **Сборка:** Maven
- **Сервер:** Apache Tomcat 11
- **Деплой:** VPS (Ubuntu 24.04)

## 📦 Функциональность

- **Создание матча** – валидация имён (буквы, пробелы, дефисы, апострофы; длина 2–30 символов), защита от одинаковых игроков
- **Подсчёт очков** – по правилам тенниса: геймы, сеты (best of 3), тай-брейк при 6:6
- **Завершение матча** – автоматическое сохранение в базу данных (H2), удаление из текущих
- **Просмотр завершённых матчей** – пагинация (5 матчей на страницу) и фильтр по имени игрока

## ⚙️ База данных

Используется H2 in‑memory. Таблицы создаются автоматически Hibernate при запуске. Данные не сохраняются между перезапусками приложения.

## 🔧 Запуск локально

1. Клонируйте репозиторий:  
   `git clone https://github.com/j0797/tennis-scoreboard.git`
2. Перейдите в папку проекта и соберите WAR:  
   `mvn clean package`
3. Скопируйте `target/tennis-scoreboard-1.0-SNAPSHOT.war` в каталог `webapps` Tomcat (или переименуйте в `ROOT.war` для доступа по корню).
4. Запустите Tomcat.
5. Откройте браузер:  
   - Если оставили исходное имя → `http://localhost:8080/tennis-scoreboard-1.0-SNAPSHOT/`  
   - Если переименовали в `ROOT.war` → `http://localhost:8080/`

## 🌐 Демо

Проект будет доступен до 03.05.2026 по адресу: 
[`http://31.129.104.17:8080/tennis-scoreboard-1.0-SNAPSHOT/`](http://31.129.104.17:8080/tennis-scoreboard-1.0-SNAPSHOT/)
