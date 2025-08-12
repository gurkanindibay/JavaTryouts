# Java Collections Challenge (Spring Boot)

This starter Spring Boot project sets up a simple library API to practice Java collections:
- `ArrayList` for ordered books
- `HashSet` to prevent duplicates
- `HashMap` for borrow counts

## Endpoints
- `POST /api/books` — add a book `{ "title": "...", "author": "..." }`
- `GET /api/books` — list all books
- `POST /api/books/borrow/{title}` — borrow by title

## Run locally
Use Maven (Java 21+ recommended):

```powershell
mvn -q -e -DskipTests spring-boot:run
```

Then try requests (PowerShell):

```powershell
# Add a book
curl -Method POST -Uri http://localhost:8080/api/books -ContentType 'application/json' -Body '{"title":"The Great Gatsby","author":"F. Scott Fitzgerald"}'

# List books
curl http://localhost:8080/api/books

# Borrow
curl -Method POST http://localhost:8080/api/books/borrow/The%20Great%20Gatsby
```

## Tests
```powershell
mvn -q -Dtest=LibraryServiceTest test
```

## Next steps
- Add search by title endpoint
- Persist data with Spring Data JPA (swap collections with a repository)
- Add validation (Bean Validation) and error handling (ControllerAdvice)
