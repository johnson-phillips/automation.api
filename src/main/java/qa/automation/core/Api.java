package qa.automation.core;

import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import qa.automation.report.TestData;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by johnson_phillips on 2/1/18.
 */
public class Api extends RestAssured {

    public static boolean printconsole = Boolean.parseBoolean(System.getProperty("printconsole"));
    public static RequestLoggingFilter requestLoggingFilter;
    public static ResponseLoggingFilter responseLoggingFilter;
    static ByteArrayOutputStream request;
    static ByteArrayOutputStream response;

    static
    {
        try {
            request = new ByteArrayOutputStream();
            response = new ByteArrayOutputStream();
            PrintStream requestStream = new PrintStream(request);
            PrintStream responseStream = new PrintStream(response);
            requestLoggingFilter = new RequestLoggingFilter(LogDetail.ALL,false,requestStream);
            responseLoggingFilter = new ResponseLoggingFilter(LogDetail.ALL,false,responseStream);
            filters(requestLoggingFilter, responseLoggingFilter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static Response readResponse(Response resp) throws Exception
    {
        String req = request.toString();
        String res = response.toString();
        long time = resp.time();
        if(printconsole) {
            System.out.print("\r\n" + "----------------------------------------Request Data----------------------------------------" + "\r\n");
            System.out.println(req);
            System.out.print("\r\n" + "----------------------------------------End Request Data-----------------------------------" + "\r\n");
            System.out.print("\r\n" + "----------------------------------------Response Data----------------------------------------" + "\r\n");
            System.out.println("Duration in Milliseconds:" + time);
            System.out.println(res);
            System.out.print("\r\n" + "----------------------------------------End Response Data-----------------------------------" + "\r\n");

        }
        TestData.addTestStep(req ,null);
        TestData.addTestStep("duration_in_milliSeconds:" + time + "\r\n" + res,null,true);
        request.reset();
        response.reset();
        return resp;

    }
}
