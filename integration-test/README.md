## Integration Test Instructions

These integration tests need the __command-side__ and __query-side__ app containers to be UP and running. 

To run the integration tests...

```bash
./gradlew integration-test:integrationTest
```

The tests use ***RestAssured*** to send commands to the command side before following up on their success by sending queries to the query-side. Because the app features 'eventual-consistency' a small delay is introduced between each test to give time for the event messages to propagate from the command-side to the query-side.

The tests also use the @FixMethodOrder annotation from JUnit to allow the tests to be a little bit more modular but still execute in the correct order for testing.



