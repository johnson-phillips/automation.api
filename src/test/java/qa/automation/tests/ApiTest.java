package qa.automation.tests;

import org.testng.annotations.Test;
import qa.automation.core.Api;
import qa.automation.report.TestData;

public class ApiTest {

    @Test
    public void Test1() throws Exception
    {
        TestData.startTest("first test",1);
        Api.printconsole = true;
        Api.getData("http://dummy.restapiexample.com/api/v1/employees");
        TestData.endTest();
    }
}
