# Home assignment project

## TL;DR

### Building

```
mvn compile
```

### Testing

* Unit tests: `mvn test`
* Integration tests: `mvn test -P it`

### Running

```
mvn exec:java
```

The main entry point will be http://localhost:8000/venue/hours/human (which should be interpreted as "give me human representation of a venue's open hours"). This endpoint accepts JSON and replies with `text/plain` per the task description.

### Performing requests

Look into `test-j` Shell script to get an idea on how to invoke requests using Curl. Try these:

```
./test-j example1
./test-j example2
```

Try to change `locale` in `src/main/resources/config.yml` and then run the requests again.

## Notes

The best part of this task is that it is a real-life problem, so that one needs to tackle with trade offs, unclear specifications and guess a lot about all the stuff. There is a lot of assumption about how wrong data could be or what is tolerance is acceptable when processing.

The worst part of this task is that it is a real-life problem likely taken from a real production application. As such it has a lot of history behind and the real scope could be vast depending on how painful that history was or important the component is.

1. Where the input comes from? If the source is under control, then it is viable to perform strict validation of input data and fail fast on any misalignment detected. Otherwise, especially if the source is a human, the input needs to be processed carefully and probably
2. Who is the consumer of the result? Is it a human or another service (or frontend JS code)? In case of the former, it would be a good idea to localize the output and generate responses in various content types. If the latter, the output could be more appropriate for machines.

I shall be happy for you reviewing both comments in the code and the Git commit messages, they contain a lot of commentary as well.

### Algorithm

Definitely the major complexity is a result of unfortunate selection of an input format that allows for numerous mistakes. The ways the input data could malformed are as follows (and not limited to these cases):

* opening and closing hours may be not in pairs (e.g. duplicate types);
* values may be not sorted;
* ranges may be overlapping;
* next day may not have a closing hour for the previous one.

### Application

TBD

## License

This code is licensed under GPL.
