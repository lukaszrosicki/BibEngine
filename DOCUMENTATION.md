# BibEngine

## Opis
Prosta aplikacja w Javie i Spring Boot do zarządzania bazą bibliograficzną. Poniżej krótki opis funkcji.

### Technologie
- Spring Boot
- H2
- Thymeleaf + Bootstrap
- JWT do autoryzacji API

### Uruchomienie
```
mvn spring-boot:run -pl server
mvn spring-boot:run -pl client
```

### API przykladowe zapytania
- Rejestracja: `POST /api/auth/register`
- Logowanie: `POST /api/auth/login` zwraca JWT
- Moje bibliografie: `GET /api/bibliography` z nagłówkiem `Authorization: Bearer TOKEN`
- Dodanie wpisu po DOI: `GET /api/bibliography/{id}/entries/by-doi?doi=...`

### Architektura
Serwer `server` udostępnia REST API na porcie 5100, klient `client` korzysta z Thymeleaf na porcie 5200.
Baza danych to wbudowane H2.

### Deploy
Aplikację można uruchomić na serwerze produkcyjnym poleceniem `java -jar server.jar` oraz `java -jar client.jar`. Następnie należy skonfigurować reverse proxy (np. nginx) kierujące ruch z domeny na odpowiedni port.
