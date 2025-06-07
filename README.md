# BibEngine

Aplikacja do zarządzania bazą bibliograficzną. Serwer działa na porcie 5100, klient na 5200.
Baza danych to H2 i jest tworzona w katalogu `server/data` dzięki konfiguracji
`jdbc:h2:file:./data/bibengine`.

## Uruchomienie
```
mvn spring-boot:run -pl server
mvn spring-boot:run -pl client
```
