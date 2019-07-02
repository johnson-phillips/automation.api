package qa.automation.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import qa.automation.report.TestData;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import qa.automation.method.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;

import javax.net.ssl.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Random;

/**
 * Created by johnson_phillips on 2/1/18.
 */
public class Api {

    public static Random random = new Random();
    public static int responseCode;
    public static String responseMessage;
    public static Header[] responseHeaders;
    public static boolean printconsole=false;
    public static boolean skipResponseLogging=false;
    static String version = "e479eac9-07d2-471e-b78d-bed108e90354";
    public static ObjectMapper mapper = new ObjectMapper();

    static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(30000)
            .setConnectTimeout(30000)
            .setConnectionRequestTimeout(30000)
            .build();

    public static String getVersion()
    {
        return version;
    }

    public static void postData(String url, String data,String ... optional) throws Exception {
        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Request Data-------------------------------------------------" + "\r\n");
                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + data + "\r\n");
                System.out.print("-----------------------------------End Request Data-----------------------------------------------" + "\r\n");
            }

            CloseableHttpClient httpclient = getClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setConfig(requestConfig);
            // Request Headers and other properties.
            httppost.setHeader("Content-Type","application/json");
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httppost.setEntity(entity);
            response = httpclient.execute(httppost);
            TestData.addTestStep("Post Request URL:" + url + " Request Data:" + data,null,true);
            readResponse(response);


        }
        catch(Exception ex)
        {
            TestData.addTestStep("Post Request URL:" + url + "Request Data:" + data,ex,true);
            exceptionHandling(ex, true);
        }
    }

    public static void postRequests(String urls, String data) throws Exception {
        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Request Data-------------------------------------------------" + "\r\n");
                System.out.print("request url: " + urls + "\r\n");
                System.out.print("request data: " + data + "\r\n");
                System.out.print("-----------------------------------End Request Data-----------------------------------------------" + "\r\n");
            }
            for(String url:urls.split(",")) {
                postData(url,data);
            }
        }
        catch(Exception ex)
        {
            exceptionHandling(ex, true);
        }
    }

    public static void postData(String url, String data,String JWT) throws Exception{

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");
                //
                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + data + "\r\n");
                System.out.print("JWT: " + JWT + "\r\n");
                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setConfig(requestConfig);

            // Request Headers and other properties.
            httppost.setHeader("Content-Type","application/json");
            httppost.setHeader("Authorization","Bearer " + JWT);
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httppost.setEntity(entity);
            response = httpclient.execute(httppost);
            TestData.addTestStep("Post Request URL:" + url + " Request Data:" + data + " JWT:"+JWT, null,true);
            readResponse(response);
        }
        catch(Exception ex)
        {
            TestData.addTestStep("Post Request URL:" + url + " Request Data:" + data + " JWT:"+JWT, ex,true);
            exceptionHandling(ex, true);
        }
    }

    public static InputStream downloadFilePostRequest(String url, String data,String JWT) throws Exception{

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");
                //
                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + data + "\r\n");
                System.out.print("JWT: " + JWT + "\r\n");
                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setConfig(requestConfig);

            // Request Headers and other properties.
            httppost.setHeader("Content-Type","application/json");
            httppost.setHeader("Authorization","Bearer " + JWT);
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httppost.setEntity(entity);
            InputStream source = null;
            boolean retry = true;
            int counter = 0;

            while(retry)
            {
                counter +=1;
                response = httpclient.execute(httppost);
                source = response.getEntity().getContent();
                int responsecode = response.getStatusLine().getStatusCode();
                if(responsecode == 200)
                {
                    retry = false;
                }
                else
                {
                    if(counter > 3) {
                        retry = false;
                        System.out.print("retrying " + counter);
                        Thread.sleep(2000);
                    }
                }
            }
            return source;

        }
        catch(Exception ex)
        {
            return null;
        }
    }

    public static void postData(String url, HttpEntity entity,Header[] headers) throws Exception {

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");

                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + EntityUtils.toString(entity, "UTF-8") + "\r\n");

                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setConfig(requestConfig);

            httppost.setEntity(entity);
            httppost.setHeaders(headers);
            response = httpclient.execute(httppost);
            TestData.addTestStep("Post Request URL:" + url + " Request Data:" + EntityUtils.toString(entity,"UTF-8") + " Headers:"+((headers==null)?"null":mapper.writeValueAsString(headers)), null,true);
            readResponse(response);
        }
        catch(Exception ex)
        {
            TestData.addTestStep("Post Request URL:" + url + " Request Data:" + EntityUtils.toString(entity,"UTF-8") + " Headers:"+((headers==null)?"null":mapper.writeValueAsString(headers)), ex,true);

            exceptionHandling(ex, true);
        }
    }

    public static void putData(String url, String data,String JWT) throws Exception{

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");

                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + data + "\r\n");
                System.out.print("JWT: " + JWT + "\r\n");
                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpPut httpPut = new HttpPut(url);
            httpPut.setConfig(requestConfig);

            // Request Headers and other properties.
            httpPut.setHeader("Content-Type","application/json");
            httpPut.setHeader("Authorization","Bearer " + JWT);
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httpPut.setEntity(entity);
            response = httpclient.execute(httpPut);
            TestData.addTestStep("Put Request URL:" + url + " Request Data:" + data + " JWT:"+JWT, null,true);
            readResponse(response);
        }
        catch(Exception ex)
        {
            TestData.addTestStep("Put Request URL:" + url + " Request Data:" + data + " JWT:"+JWT, ex,true);

            exceptionHandling(ex, true);
        }
    }

    public static void putData(String url, String data) throws Exception{

        HttpResponse response = null;
        try {
            if(printconsole) {
                System.out.print("-------------------------------------Request Data-------------------------------------------------" + "\r\n");
                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + data + "\r\n");
                System.out.print("-----------------------------------End Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpPut httpPut = new HttpPut(url);
            httpPut.setConfig(requestConfig);

            // Request Headers and other properties.
            httpPut.setHeader("Content-Type","application/json");
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httpPut.setEntity(entity);
            response = httpclient.execute(httpPut);
            TestData.addTestStep("Put Request URL:" + url + " Request Data:" + data,null,true);
            readResponse(response);

        }
        catch(Exception ex)
        {
            TestData.addTestStep("Put Request URL:" + url + " Request Data:" + data,ex,true);
            exceptionHandling(ex, true);
        }
    }

    public static void putData(String url, HttpEntity entity,Header[] headers) throws Exception{

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");

                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + entity.getContent().toString() + "\r\n");

                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpPut httpPut = new HttpPut(url);
            httpPut.setConfig(requestConfig);

            httpPut.setEntity(entity);
            httpPut.setHeaders(headers);
            response = httpclient.execute(httpPut);
            TestData.addTestStep("Put Request URL:" + url + " Request Data:" + EntityUtils.toString(entity,"UTF-8") + " Headers:"+((headers==null)?"null":mapper.writeValueAsString(headers)), null,true);
            readResponse(response);
        }
        catch(Exception ex)
        {
            TestData.addTestStep("Put Request URL:" + url + " Request Data:" + EntityUtils.toString(entity,"UTF-8") + " Headers:"+((headers==null)?"null":mapper.writeValueAsString(headers)), ex,true);
            exceptionHandling(ex, true);
        }
    }

    public static void patchData(String url, String data) throws Exception{

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");

                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + data + "\r\n");
                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpPatch httpPatch = new HttpPatch(url);
            httpPatch.setConfig(requestConfig);

            // Request Headers and other properties.
            httpPatch.setHeader("Content-Type","application/json");
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httpPatch.setEntity(entity);
            response = httpclient.execute(httpPatch);
            TestData.addTestStep("Patch Request URL:" + url + " Request Data:" + data,null,true);
            readResponse(response);

        }
        catch(Exception ex)
        {
            TestData.addTestStep("Patch Request URL:" + url + " Request Data:" + data,ex,true);

            exceptionHandling(ex, true);
        }
    }

    public static void patchData(String url, String data,String JWT) throws Exception{

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");

                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + data + "\r\n");
                System.out.print("JWT: " + JWT + "\r\n");
                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpPatch httpPatch = new HttpPatch(url);
            httpPatch.setConfig(requestConfig);

            // Request Headers and other properties.
            httpPatch.setHeader("Content-Type","application/json");
            httpPatch.setHeader("Authorization","Bearer " + JWT);
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httpPatch.setEntity(entity);
            response = httpclient.execute(httpPatch);
            TestData.addTestStep("Patch Request URL:" + url + " Request Data:" + data + " JWT:"+JWT, null,true);
            readResponse(response);

        }
        catch(Exception ex)
        {
            TestData.addTestStep("Put Request URL:" + url + " Request Data:" + data + " JWT:"+JWT, ex,true);
            exceptionHandling(ex, true);
        }
    }

    public static void patchData(String url, HttpEntity entity,Header[] headers) throws Exception {

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");

                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + entity.getContent().toString() + "\r\n");

                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpPatch httpPatch = new HttpPatch(url);
            httpPatch.setConfig(requestConfig);

            httpPatch.setEntity(entity);
            httpPatch.setHeaders(headers);
            response = httpclient.execute(httpPatch);
            TestData.addTestStep("Patch Request URL:" + url + " Request Data:" + EntityUtils.toString(entity,"UTF-8") + " Headers:"+((headers==null)?"null":mapper.writeValueAsString(headers)), null,true);
            readResponse(response);
        }
        catch(Exception ex)
        {
            TestData.addTestStep("Patch Request URL:" + url + " Request Data:" + EntityUtils.toString(entity,"UTF-8") + " Headers:"+((headers==null)?"null":mapper.writeValueAsString(headers)), ex,true);

            exceptionHandling(ex, true);
        }
    }

    public static void postFormData(String url, List<NameValuePair> urlParameters) throws Exception {
        try {

            HttpEntity entity = new UrlEncodedFormEntity(urlParameters);
            postData(url,entity,null);
        }
        catch(Exception ex)
        {
            TestData.addTestStep("Post Request URL:" + url + "Request Data:" +mapper.writeValueAsString(urlParameters), ex,true);
            exceptionHandling(ex, true);
        }
    }

    public static void postFormData(String url, List<NameValuePair> urlParameters,String JWT) throws Exception {
        try {

            HttpEntity entity = new UrlEncodedFormEntity(urlParameters);
            Header[] headers = new Header[] { new BasicHeader("Authorization—","Bearer " + JWT)};
            postData(url,entity,headers);
        }
        catch(Exception ex)
        {
            TestData.addTestStep("Post Request URL:" + url + "Request Data:" +mapper.writeValueAsString(urlParameters), ex,true);
            exceptionHandling(ex, true);
        }
    }

    public static void postFileUpload(String url, List<NameValuePair> urlParameters,String file) {

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Request Data-------------------------------------------------" + "\r\n");
                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + urlParameters + "\r\n");
                System.out.print("-----------------------------------End Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpPost httppost = new HttpPost(url);
            FileBody uploadFilePart = new FileBody(new File(file));
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("file1", uploadFilePart);
            for(NameValuePair pair: urlParameters)
            {
                reqEntity.addPart(pair.getName(), new StringBody(pair.getValue()));
            }
            httppost.setEntity(reqEntity);
            response = httpclient.execute(httppost);
            readResponse(response);
            if(responseCode > 499) {
                Thread.sleep(random.nextInt(5000));
                response = httpclient.execute(httppost);
                readResponse(response);
            }
        }
        catch(Exception ex)
        {
            exceptionHandling(ex, true);
        }
    }

    public static void getData(String url,String ... optional) throws Exception{

        HttpResponse response = null;
        try {
            CloseableHttpClient httpclient = getClient();
            HttpGet httpget = new HttpGet(url);
            httpget.setConfig(requestConfig);

            response = httpclient.execute(httpget);

            TestData.addTestStep("Get Request URL:" + url ,null,true);
            readResponse(response);

        }
        catch(Exception ex)
        {
            TestData.addTestStep("Get Request URL:" + url ,ex,true);
            exceptionHandling(ex, true);
        }
    }

    public static void getData(String url,String JWT) throws Exception{

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Get Request Data-------------------------------------------------" + "\r\n");

                System.out.print("request url: " + url + "\r\n");
                System.out.print("JWT: " + JWT + "\r\n");
                System.out.print("-----------------------------------End Get Request Data-----------------------------------------------" + "\r\n");
            }
            SSLContextBuilder builder = new SSLContextBuilder();
            CloseableHttpClient httpclient = getClient();
            HttpGet httpget = new HttpGet(url);
            httpget.setConfig(requestConfig);

            // Request Headers and other properties.
            httpget.setHeader("Authorization","Bearer " + JWT);
            response = httpclient.execute(httpget);
            TestData.addTestStep("Get Request URL:" + url + " JWT:" + JWT,null,true);
            readResponse(response);

        }
        catch(Exception ex)
        {
            TestData.addTestStep("Get Request URL:" + url + " JWT:" + JWT,ex,true);

            exceptionHandling(ex, true);
        }
    }

    public static void getData(String url,Header[] headers) throws Exception{

        HttpResponse response = null;
        try {
            if(printconsole) {

                System.out.print("-------------------------------------Get Request Data-------------------------------------------------" + "\r\n");

                System.out.print("request url: " + url + "\r\n");

                System.out.print("-----------------------------------End Get Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpGet httpget = new HttpGet(url);
            httpget.setConfig(requestConfig);

            httpget.setHeaders(headers);
            response = httpclient.execute(httpget);
            TestData.addTestStep("Get Request URL:" + url + " Headers:" + mapper.writeValueAsString(headers),null,true);
            readResponse(response);
        }
        catch(Exception ex)
        {
            TestData.addTestStep("Get Request URL:" + url + " Headers:" + mapper.writeValueAsString(headers),ex,true);

            exceptionHandling(ex, false);
        }
    }

    public static void deleteData(String url, String data,String ... optional) throws Exception {
        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Request Data-------------------------------------------------" + "\r\n");
                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + data + "\r\n");
                System.out.print("-----------------------------------End Request Data-----------------------------------------------" + "\r\n");
            }

            CloseableHttpClient httpclient = getClient();
            HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
            httpDelete.setConfig(requestConfig);
            // Request Headers and other properties.
            httpDelete.setHeader("Content-Type","application/json");
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httpDelete.setEntity(entity);
            response = httpclient.execute(httpDelete);
            TestData.addTestStep("Delete Request URL:" + url + " Request Data:" + data,null,true);
            readResponse(response);


        }
        catch(Exception ex)
        {
            TestData.addTestStep("Delete Request URL:" + url + "Request Data:" + data,ex,true);
            exceptionHandling(ex, true);
        }
    }

    public static void deleteData(String url, String data,String JWT) throws Exception{

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");
                //
                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + data + "\r\n");
                System.out.print("JWT: " + JWT + "\r\n");
                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }

            CloseableHttpClient httpclient = getClient();
            HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(url);
            httpDeleteWithBody.setConfig(requestConfig);

            // Request Headers and other properties.
            httpDeleteWithBody.setHeader("Content-Type","application/json");
            httpDeleteWithBody.setHeader("Authorization","Bearer " + JWT);
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httpDeleteWithBody.setEntity(entity);
            response = httpclient.execute(httpDeleteWithBody);
            TestData.addTestStep("Delete Request URL:" + url + " Request Data:" + data + " JWT:"+JWT, null,true);
            readResponse(response);
        }
        catch(Exception ex)
        {
            TestData.addTestStep("Delete Request URL:" + url + " Request Data:" + data + " JWT:"+JWT, ex,true);
            exceptionHandling(ex, true);
        }
    }

    public static void deleteData(String url, HttpEntity entity,Header[] headers) throws Exception {

        HttpResponse response = null;
        try {

            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");

                System.out.print("request url: " + url + "\r\n");
                System.out.print("request data: " + EntityUtils.toString(entity, "UTF-8") + "\r\n");

                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }
            CloseableHttpClient httpclient = getClient();
            HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(url);
            httpDeleteWithBody.setConfig(requestConfig);

            httpDeleteWithBody.setEntity(entity);
            httpDeleteWithBody.setHeaders(headers);
            response = httpclient.execute(httpDeleteWithBody);
            TestData.addTestStep("Delete Request URL:" + url + " Request Data:" + EntityUtils.toString(entity,"UTF-8") + " Headers:"+((headers==null)?"null":mapper.writeValueAsString(headers)), null,true);
            readResponse(response);
        }
        catch(Exception ex)
        {
            TestData.addTestStep("Delete Request URL:" + url + " Request Data:" + EntityUtils.toString(entity,"UTF-8") + " Headers:"+((headers==null)?"null":mapper.writeValueAsString(headers)), ex,true);

            exceptionHandling(ex, true);
        }
    }

    public static void oAuthRequest(OAuthRequest request) throws Exception
    {
        try {
            if(printconsole) {
                System.out.print("-------------------------------------Post Request Data-------------------------------------------------" + "\r\n");

                System.out.print("request url: " + request.getUrl() + "\r\n");
                System.out.print("request data: " + request.getBodyContents() + "\r\n");

                System.out.print("-----------------------------------End Post Request Data-----------------------------------------------" + "\r\n");
            }
            TestData.addTestStep("OAuth Request URL:" + request.getUrl() + " Request Data:" + request.getBodyContents() + " Headers:"+((request.getHeaders()==null)?"null":mapper.writeValueAsString(request.getHeaders()))+ " Oauth:"+mapper.writeValueAsString(request.getOauthParameters()), null,true);
            Response response = request.send();
            responseCode = response.getCode();
            responseMessage = response.getBody();
            int i=0;
            responseHeaders = new Header[response.getHeaders().keySet().size()];
            for(String key:response.getHeaders().keySet())
            {
                Header header = new BasicHeader(key,response.getHeader(key));
                responseHeaders[0] = header;
                i+=1;
            }

            TestData.addTestStep("Response Code:"+responseCode + " Response Data:" +responseMessage,null,true);
        }
        catch (Exception ex)
        {
            TestData.addTestStep("OAuth Request URL:" + request.getUrl() + " Request Data:" + request.getBodyContents() + " Headers:"+((request.getHeaders()==null)?"null":mapper.writeValueAsString(request.getHeaders()))+ " Oauth:"+mapper.writeValueAsString(request.getOauthParameters()), ex,true);
        }

    }

    public static void readResponse(HttpResponse response) throws Exception
    {
        try {

            responseCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if(entity != null) {
                responseMessage = EntityUtils.toString(entity, "UTF8");
            }
            else {
                responseMessage = "empty response from server";
            }
            responseHeaders = response.getAllHeaders();
            Header[] headers = response.getHeaders("content-type");
            for (Header header:headers)
            {
                if(header.getValue().toLowerCase().contains("text/html"))
                {
                    responseMessage = responseMessage.replace("<","");
                    responseMessage = responseMessage.replace(">","");
                    break;
                }
            }
            if(printconsole) {
                System.out.print("\r\n" + "----------------------------------------Response Data----------------------------------------" + "\r\n");

                System.out.print("Time Stamp EST: " + DateTime.now().toDateTime(DateTimeZone.UTC).toMutableDateTime(DateTimeZone.forID("EST")).toString() + "\r\n");
                System.out.print("response code: " + responseCode + "\r\n");
                System.out.print("response data: " + responseMessage + "\r\n");
                System.out.print("\r\n" + "--------------------------------------End Response Data--------------------------------------" + "\r\n");
            }
            if(!skipResponseLogging)
                TestData.addTestStep("Response Code:"+responseCode + " Response Data:" +responseMessage,null,true);
        }
        catch (Exception ex)
        {
            System.out.print(ex + "\r\n");
            TestData.addTestStep("Resonse Code:"+responseCode + " Response Data:" +responseMessage,ex,true);
        }
    }

    public static void exceptionHandling(Exception ex,boolean failTest)
    {
        Api.responseCode = 0;
        Api.responseMessage = "";
        ex.printStackTrace();
//        if(failTest)
//            Assert.fail(ex.getMessage());
    }

    static CloseableHttpClient getClient() throws Exception
    {
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();
        HostnameVerifier hnv = new NoopHostnameVerifier();
        SSLConnectionSocketFactory sslcf = new SSLConnectionSocketFactory(sslContext, hnv);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslcf).build();
        return  httpclient;
    }

    public void TrustallCertificates()
    {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

// Install the all-trusting trust manager
        //SSLContext sc = SSLContext.getInstance("SSL");
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            //SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {

            System.out.print("failed" + e.getMessage());
        }
// Now you can access an https URL without having the certificate in the truststore

        try {
            HttpClient httpClient = HttpClients.createDefault();


            URL url =new URL("");
            HttpGet httpget = new HttpGet("");
            httpget.setHeader("Content-Type","application/json");
            HttpResponse response = httpClient.execute(httpget);

        } catch (Exception ex) {

            System.out.print("failed" + ex.getMessage());
        }

    }

    public void certificate() throws  Exception
    {

        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                sslsf).build();

        HttpGet httpGet = new HttpGet("");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
        }
        finally {
            response.close();
        }
    }
}
