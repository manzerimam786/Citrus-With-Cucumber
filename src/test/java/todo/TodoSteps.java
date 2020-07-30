/*
 * Copyright 2006-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package todo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.variable.GlobalVariables;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * @author Christoph Deppisch
 */
public class TodoSteps {

    @CitrusResource
    private TestCaseRunner runner;

    static String requestBody = "{\n" +
            "  \"name\": \"shadab\",\n" +
            "  \"deptname\": \"testing\"\n" +
            "}";
    @Given("^Employee list is empty$")
    public void empty_employees() {
        runner.given(http()
            .client("employeeListClient")
            .send()
            .delete("/emp/delete"));

        System.out.println("in empty employee method after delete");
        runner.then(http()
            .client("employeeListClient")
            .receive()
            .response(HttpStatus.NO_CONTENT));
        System.out.println("in empty employee method after response");
    }

    @When("^(?:I|user) adds? entry \"([^\"]*)\"$")
    public void add_entry(String todoName) {
    	
        runner.when(http()
            .client("employeeListClient")
            .send()
            .post("/emp/create")
            //.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
           // .payload("title=" + todoName));
             .payload(requestBody));
            System.out.println("post operation done");
        runner.then(http()
            .client("employeeListClient")
            .receive()
            .response(HttpStatus.CREATED));
        System.out.println("got response fpr post operation");
    }

    @When("^(?:I|user) removes? entry \"([^\"]*)\"$")
    //@When("^(?:I|user) removes? entry$")
    public void remove_entry(int id) throws UnsupportedEncodingException{
    	System.out.println("Id are="+id);
        runner.when(http()
            .client("employeeListClient")
            .send().delete().path(String.format("/emp/delete/%d", id)));
            //.delete("/emp/delete/{id}"));
        
        System.out.println("deleting particular resource");

        runner.then(http()
            .client("employeeListClient")
            .receive()
            .response(HttpStatus.NO_CONTENT)
            .messageType(MessageType.PLAINTEXT));
        
        System.out.println("got reposne after deleting particular resource");
    }

    @Then("^(?:the )?number of employee entries should be (\\d+)$")
    public void verify_employee(int todoCnt) {
    	System.out.println("inside get all");
        runner.then(http()
            .client("employeeListClient")
            .send()
            .get("/emp/employees"));

        System.out.println("after get all");
        runner.and(http()
            .client("employeeListClient")
            .receive()
            .response(HttpStatus.OK));
           // .messageType(MessageType.PLAINTEXT)
            //.payload(String.valueOf(todoCnt)));
        System.out.println("response of get all");
    }

    @Then("^(?:the )?employee list should be empty$")
    public void verify_empty_employee() {
        verify_employee(0);
    }

}
