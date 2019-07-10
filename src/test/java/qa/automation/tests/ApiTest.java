package qa.automation.tests;

import org.testng.annotations.Test;
import qa.automation.core.Api;
import static qa.automation.report.TestData.*;
import static qa.automation.core.Api.*;

public class ApiTest {

    @Test
    public void Test1() throws Exception
    {
        startTest("sample post request",1);
        Api.printconsole = true;
        readResponse(given().body("{\"name\":\"afbffg\",\"salary\":\"123\",\"age\":\"23\"}").post("http://dummy.restapiexample.com/api/v1/create"));
        endTest();
    }
}
