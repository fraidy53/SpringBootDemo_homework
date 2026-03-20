# Spring Boot Book API - Practice 2-4

## What is implemented

- `Book` - `BookDetail` 1:1 association
  - `Book`: `@OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)`
  - `BookDetail`: `@OneToOne(fetch = FetchType.LAZY)` + `@JoinColumn(name = "book_id", unique = true)`
- Repository query extensions for detail fetch, search, and ISBN duplicate check
- Service methods for CRUD + search by author/title + duplicate ISBN validation
- Controller endpoints for all required APIs
- `@DataJpaTest` repository tests for 1:1 mapping and query behavior

## Key files

- `src/main/java/com/example/myspringbootlab/entity/Book.java`
- `src/main/java/com/example/myspringbootlab/entity/BookDetail.java`
- `src/main/java/com/example/myspringbootlab/Repository/BookRepository.java`
- `src/main/java/com/example/myspringbootlab/Repository/BookDetailRepository.java`
- `src/main/java/com/example/myspringbootlab/dto/BookDTO.java`
- `src/main/java/com/example/myspringbootlab/mapper/BookMapper.java`
- `src/main/java/com/example/myspringbootlab/service/BookService.java`
- `src/main/java/com/example/myspringbootlab/controller/BookController.java`
- `src/test/java/com/example/myspringbootlab/BookRepositoryTest.java`

## Quick test

```powershell
cd "C:\4. Java_Spring\3. SpringBoot\demo"
.\mvnw.cmd -q -Dtest=BookRepositoryTest test
```

## Run app

```powershell
cd "C:\4. Java_Spring\3. SpringBoot\demo"
.\mvnw.cmd spring-boot:run
```

## API endpoints

- `GET /api/books`
- `GET /api/books/{id}`
- `GET /api/books/isbn/{isbn}`
- `GET /api/books/search/author?author={author}`
- `GET /api/books/search/title?title={title}`
- `POST /api/books`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`

## Sample request body (POST/PUT)

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "price": 45,
  "publishDate": "2008-08-01",
  "detailRequest": {
    "description": "A handbook of agile software craftsmanship",
    "language": "English",
    "pageCount": 464,
    "publisher": "Prentice Hall",
    "coverImageUrl": "https://example.com/cleancode.jpg",
    "edition": "1st"
  }
}
```

