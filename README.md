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

The main entry point will be http://localhost:8000/venue/hours/human
(which should be interpreted as "give me human representation of a
venue's open hours"). This endpoint accepts JSON and replies with
`text/plain` per the task description.

### Performing requests

Look into `test-j` Shell script to get an idea on how to invoke requests
using `curl`. Try these:

```
./test-j example1
./test-j example2
./test-j half
```

Both time format and an order of days of week are controlled by locale
setting (the motivation is explained below) In order to play with it,
change `locale` in `src/main/resources/config.yml` and then run the
requests again.

## Notes

The best part of this task is that it is a real-life problem, so that
one needs to tackle with trade offs, unclear specifications and guess a
lot about all the stuff. There could be made lots of assumptions about
the degree an input data could be wrong or what is an acceptable level
of tolerance during processing.

The worst part of this task is that it is a real-life problem likely
taken from a real production application. As such it has a lot of
history behind and the real scope could be vast depending on how painful
that history was or important the component is. As a result, the
reviewing team would pay most attention to those details I sacrificed,
because I know nothing neither about environment nor about users nor
real problems faced.

1. Where an input comes from? If the source is under control, then it is
viable to perform strict validation of input data and fail fast on any
misalignment detected. Otherwise, especially if the source is a human,
then some level of tolerance might make sense.
2. Who is the consumer of a result? Is it a human or another service (or
frontend JS code)? In case of the former, it would be a good idea to
localize the output and generate responses in various content types. If
the latter, the output should be formatted for machine processing. Since
the times in the examples are formatted in a very human-friendly manner,
I am assuming localization is a good idea and thus I have preparations
for i18n throughout the project.
3. An output format in the task description is weird. On one hand
it specifies time in a way which may be identified as English, with
AM/PM as a time of day indication. On the other hand, the week starts
with Monday, which is quite unusual for English. This conflict has
no explanation in the task description and I chose to follow a fair
localization approach here, that is both time format and an order of
days of week should be chosen based on the locale that is currently
specified in the application's configuration file.
4. Both examples of output make me think that if a day is absent in the
input data, then it should be absent in the resulting output. Contrary,
in order to have `Closed` in the output, an input data must contain an
empty array of hours. This is not clarified and I have implemented the
algorithm in this way along with the test suite.
5. To make an output even more human-readable, I would propose to skip
the first `AM/PM` as long as they are the same. This way, `8 AM -
10 AM` would become `8 - 10 AM`. (Not implemented)

I shall be happy for you reviewing both comments in the code and the Git
commit messages, as both contain a lot of commentary, including notes on
technology choices and algorithm complexity.

### Algorithm

Definitely the major complexity is a result of unfortunate selection of
an input format that allows for numerous mistakes. The ways the input
data could malformed are as follows (and not limited to these cases):

* opening and closing hours may be not in pairs (e.g. duplicate types);
* values may be not sorted;
* ranges may be overlapping;
* next day may not have a closing hour for the previous one.

It would be extremely interesting to design such an algorithm that
recovers as much as possible. I have had an experience of this kind, see
[here](https://github.com/alaz/slides-err-recovery).

In real life I would urge to reconsider the input format, but the best
opportunity is not always readily available. For example if there are
remote clients of the API, they are likely relying on it. And thus the
format change will likely be postponed till the next major version
release. `WeekScheduleProcessor` processor has a method to normalize the
flawed format into a more reliable version of it, and its result type
shows how a better input version could look like.

### Web Application

An application part is mostly a boilerplate. The most interesting here
is working around locale in a resource (aka. controller), because the
output examples give a hint that it needs to be formatted in a human
friendly manner (unlike JDK).

I have provided integration test suite also to make sure the serving
layer is working properly.

In general any normal Web application needs so many parts done right and
I am describing the ways of improvement further.

### What's next

There are a lot of things to improve, but the scope of home
assignment task is absolutely insufficient to address them all. See
https://12factor.net for inspiration and my short list of proposed
enhancemented follows -

* Response types based on `Accepts`:
  * HTML, text, JSON.
  * Templating engine to simplify the controller's code.
* I18n:
  * `Closed` message is not translated at all in the output. I could use
my own [scala-i18n](https://github.com/osinka/scala-i18n) of course, but
it would be too much bragging I guess.
  * A good Web application should look into `Accept-Language` and
provide translations based on the locale specified in there.
* Deployment:
  * Produce fat JAR.
  * `PORT` should be taken from the environment
* Security:
  * Limit the size of acceptable input JSON. It is now possible to
upload garbage of any size.
  * Protect `/admin`.
* Production
  * Logging
  * Health checks

## License

This code is licensed under GPL.
