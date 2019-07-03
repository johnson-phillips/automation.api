package qa.automation.tests;

import org.testng.annotations.Test;
import qa.automation.core.Api;

import static qa.automation.report.TestData.*;
import static qa.automation.core.Api.*;

public class ApiTest {

    @Test
    public void Test1() throws Exception
    {
        Api.printconsole = true;
        startTest("first test",1);
        for(int i=0;i<5;i++) {
            readResponse(given().body("{\"project_id\":\"1\"}").post("http://localhost:8080/qeportal/OutputApi/getProducts"));
            readResponse(given().get("http://localhost:8080/qeportal/OutputApi/getTopics"));
        }
        endTest();
    }
}
