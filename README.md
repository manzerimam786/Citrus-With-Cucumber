Citrus Cucumber BDD sample ![Logo][1]
==============

The sample uses Cucumber behavior driven development (BDD) library. The tests combine BDD feature stories with the famous 
Gherkin syntax and Citrus integration test capabilities. Read about this feature in [reference guide][4]
 
Objectives
---------

This sample application shows the usage of both Cucumber and Citrus in combination. Step definitions are able to use *CitrusResource*
annotations for injecting a TestRunner instance. The test runner is then used in steps to build a Citrus integration test.

At the end the Citrus test is automatically executed. We can use normal step definition classes that use Gherkin annotations
(@Given, @When, @Then) provided by Cucumber.

Get started
---------

We start with a feature test using JUnit and Cucumber runner.

```java
@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = { "com.consol.citrus.cucumber.CitrusReporter" } )
public class TodoFeatureIT {
}
```

The test feature is described in a story using Gherkin syntax.

```gherkin
Feature: Todo app

  Scenario: Add todo entry
    Given Todo list is empty
    When I add entry "Code something"
    Then the number of todo entries should be 1

  Scenario: Remove todo entry
    Given Todo list is empty
    When I add entry "Remove me"
    Then the number of todo entries should be 1
    When I remove entry "Remove me"
    Then the todo list should be empty
```
        
The steps executed are defined in a separate class where a Citrus test runner is used to build integration test logic.

```java
public class TodoSteps {

    /** Test runner filled with actions by step definitions */
    @CitrusResource
    private TestRunner runner;

    @Given("^Todo list is empty$")
    public void empty_todos() {
        runner.http(httpActionBuilder -> httpActionBuilder
            .client("todoListClient")
            .send()
            .delete("/api/todolist"));

        runner.http(httpActionBuilder -> httpActionBuilder
            .client("todoListClient")
            .receive()
            .response(HttpStatus.OK));
    }

    @When("^(?:I|user) adds? entry \"([^\"]*)\"$")
    public void add_entry(String todoName) {
        runner.http(httpActionBuilder -> httpActionBuilder
            .client("todoListClient")
            .send()
            .post("/todolist")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .payload("title=" + todoName));

        runner.http(httpActionBuilder -> httpActionBuilder
            .client("todoListClient")
            .receive()
            .response(HttpStatus.FOUND));
    }
    
    [...]
}    
```

Configuration
---------

In order to enable Citrus Cucumber support we need to specify a special object factory in *cucumber.properties*.
    
```properties
cucumber.object-factory=com.consol.citrus.cucumber.backend.CitrusObjectFactory
```
    
The object factory takes care on creating all step definition instances. The object factory is able to inject *@CitrusResource*
annotated fields in step classes.
    
The usage of this special object factory is mandatory in order to combine Citrus and Cucumber capabilities. 
   
We also have the usual *citrus-context.xml* Citrus Spring configuration that is automatically loaded within the object factory.
So you can define and use Citrus components as usual within your test. In this sample we use a Http client component to call some
REST API on the [todo-list](../todo-app/README.md) application.

Run
---------

**NOTE:** This test depends on the [todo-app](../todo-app/) WAR which must have been installed into your local maven repository using `mvn clean install` beforehand.

The sample application uses Maven as build tool. So you can compile, package and test the
sample with Maven.
 
     mvn clean verify -Dsystem.under.test.mode=embedded
    
This executes the complete Maven build lifecycle. The embedded option automatically starts a Jetty web
container before the integration test phase. The todo-list system under test is automatically deployed in this phase.
After that the Citrus test cases are able to interact with the todo-list application in the integration test phase.

During the build you will see Citrus performing some integration tests.
After the tests are finished the embedded Jetty web container and the todo-list application are automatically stopped.

System under test
---------

The sample uses a small todo list application as system under test. The application is a web application
that you can deploy on any web container. You can find the todo-list sources [here](../todo-app). Up to now we have started an 
embedded Jetty web container with automatic deployments during the Maven build lifecycle. This approach is fantastic 
when running automated tests in a continuous build.
  
Unfortunately the Jetty server and the sample application automatically get stopped when the Maven build is finished. 
There may be times we want to test against a standalone todo-list application.  

You can start the sample todo list application in Jetty with this command.

     mvn jetty:run

This starts the Jetty web container and automatically deploys the todo list app. Point your browser to
 
    http://localhost:8080/todolist/

You will see the web UI of the todo list and add some new todo entries.

Now we are ready to execute some Citrus tests in a separate JVM.

Citrus test
---------

Once the sample application is deployed and running you can execute the Citrus test cases.
Open a separate command line terminal and navigate to the sample folder.

Execute all Citrus tests by calling

     mvn verify

You can also pick a single test by calling

     mvn verify -Dit.test=<testname>

You should see Citrus performing several tests with lots of debugging output in both terminals (sample application server
and Citrus test client). And of course green tests at the very end of the build.

Of course you can also start the Citrus tests from your favorite IDE.
Just start the Citrus test using the TestNG IDE integration in IntelliJ, Eclipse or Netbeans.

Further information
---------

For more information on Citrus see [www.citrusframework.org][2], including
a complete [reference manual][3].

 [1]: https://citrusframework.org/img/brand-logo.png "Citrus"
 [2]: https://citrusframework.org
 [3]: https://citrusframework.org/reference/html/
 [4]: https://citrusframework.org/reference/html#cucumber
