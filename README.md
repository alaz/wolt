# Home assignment project

## Building

```
mvn compile
```

## Testing

* Unit tests: `mvn test`
* Integration tests: `mvn test -P it`

## Running

```
mvn exec:java
```

The main entry point will be http://localhost:8000/venue/hours/human (which should be interpreted as "give me human represenation of a venue's open hours"). This endpoint accepts JSON and replies with `text/plain` per the task description.