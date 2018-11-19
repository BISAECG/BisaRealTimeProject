package com.bisa.health.rest;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.persistentcookie.PersistentCookieJar;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

import static java.lang.String.valueOf;

/**
 * Created by Administrator on 2017/7/29.
 */

public abstract class HttpHelp {

    private static final String TAG = "HttpHelp";



    /**
     * 真实环境
     */
    //public final static String SERVER_URL = "http://192.168.1.3:8080/health-filckr";
    public final static String SERVER_URL = "http://www.bisahealth.com";
    public final static String DOWN_APP_URL=SERVER_URL+"/mi/call/app/down";
    public final static String SERVER_ROOT_PATH = "";
    public final static String SHOP_ROOT_PATH = "";
    public final static String DATA_ROOT_PATH = "";


    public final static int SSL = 443;
    public final static int HTTP = 80;
    public final static int DEUFALT_PORT = SSL;

    private final static String SSL_PASSWORD = "changeit";
    private final static long CONNECT_TIMEOUT = 15 * 1000;
    private final static long WRITE_TIMEOUT = 15 * 1000;
    private final static long READ_TIMEOUT = 15 * 1000;
    private final static String BASIC_USERNAME = "admin";
    private final static String BASIC_PASSWORD = "bisahealth";
    //NETWORK_GET表示发送GET请求
    public static final String NETWORK_GET = "NETWORK_GET";
    //NETWORK_POST_KEY_VALUE表示用POST发送键值对数据
    public static final String NETWORK_POST_KEY_VALUE = "NETWORK_POST_KEY_VALUE";
    //NETWORK_POST_XML表示用POST发送XML数据
    public static final String NETWORK_POST_XML = "NETWORK_POST_XML";
    //NETWORK_POST_JSON表示用POST发送JSON数据
    public static final String NETWORK_POST_JSON = "NETWORK_POST_JSON";

    public String getBaseUrl() {
        return baseUrl;
    }
    public static String HTTP_PREFIX="https://";
    private String baseUrl;
    private Context context;
    private SharedPersistor sharedPersistor;
    private String token = "";
    private String lang;
    private static final String UserAgent = "android";

    private TokenInterceptor tokenInterceptor;

    public HttpHelp(Context context, HealthServer healthServer, int flag) {

        if (healthServer != null && !StringUtils.isEmpty(healthServer.loadToken())) {
            token = healthServer.loadToken();
        }
        switch (flag) {
            case 1:
                if (healthServer != null && !StringUtils.isEmpty(healthServer.getDomain())) {
                    this.baseUrl =healthServer.getDomain();
                }
                break;
            case 2:
                if (healthServer != null && !StringUtils.isEmpty(healthServer.getDatServer())) {
                    this.baseUrl = healthServer.getDatServer();
                }
                break;
            case 3:
                if (healthServer != null && !StringUtils.isEmpty(healthServer.getShopserver())) {
                    this.baseUrl = healthServer.getShopserver();
                }
                break;
        }

        this.context = context;

        sharedPersistor = new SharedPersistor(context);

        tokenInterceptor = new TokenInterceptor(context);

        String lan = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        lang = lan + "-" + country;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }



    /**
     * Http带basic连接
     *
     * @param isAuth
     * @return
     */
    private OkHttpClient buildClient(boolean isAuth) {

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(tokenInterceptor);
        client.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        client.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
        client.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);

        if (isAuth) {
            client.authenticator(new Authenticator() {
                @Nullable
                @Override
                public Request authenticate(Route route, Response response) throws IOException {
                    if (responseCount(response) >= 3) {
                        return null;
                    }
                    String credential = Credentials.basic(BASIC_USERNAME, BASIC_PASSWORD);
                    return response.request().newBuilder().header("Authentication", credential)
                            .addHeader("Accept-Language", lang)
                            .addHeader("User-Agent", UserAgent).build();
                }
            });
        }
        return client.build();
    }

    /**
     * SSL连接
     *
     * @return
     */
    private OkHttpClient buildClient() {

        Log.i(TAG, PersistentCookieJar.class.getName());

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(tokenInterceptor);
        client.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        client.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
        client.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        client.sslSocketFactory(createSSLSocketFactory(), mMyTrustManager)
                .hostnameVerifier(new TrustAllHostnameVerifier());
        return client.build();
    }


    /**
     * 对外提供的获取支持自签名的okhttp客户端
     *
     * @param certificate 自签名证书的输入流
     * @return 支持自签名的客户端
     */
    private OkHttpClient buildClient(InputStream certificate) {

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(tokenInterceptor);
        client.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        client.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
        client.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);

        X509TrustManager trustManager;
        SSLSocketFactory sslSocketFactory;
        try {
            trustManager = trustManagerForCertificates(certificate);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            //使用构建出的trustManger初始化SSLContext对象
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            //获得sslSocketFactory对象
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return client
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build();

    }


    public OkHttpClient buildClient(int type) {
        switch (type) {
            case HttpFinal.CONN_BASIC_HTTP://basic 连接
                return buildClient(true);
            case HttpFinal.CONN_HTTPS://SSL 连接
                return buildClient();
            case HttpFinal.CONN_CUST_HTTPS://自定义证书连接
                try {
                    InputStream inputStream = context.getResources().getAssets().open("bisahealthcom.crt");
                    if (inputStream != null) {
                        return buildClient(inputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            case HttpFinal.DEFALUT_CONN_HTTP:
                return buildClient(false);
            default:
                return null;

        }
    }

    public <N extends Object> N post(String url, RequestBody body, final Class<?> clz, final int connType) {
        url = getBasicUrl(url);
        OkHttpClient client = buildClient(connType);
        try {
            Request request = null;
            if (body != null) {
                request = new Request.Builder().url(url).header("Authentication", token)
                        .addHeader("Accept-Language", lang)
                        .addHeader("User-Agent", UserAgent)
                        .post(body).build();
            } else {
                request = new Request.Builder().url(url).header("Authentication", token)
                        .addHeader("Accept-Language", lang)
                        .addHeader("User-Agent", UserAgent)
                        .build();
            }

            if (clz != null) {
                Response response = client.newCall(request).execute();
                String json = response.body().string();
                if (json != null && !json.isEmpty()) {
                    if (clz != null) {
                        Gson gson = new Gson();
                        return (N) gson.fromJson(json, clz);
                    }
                }
                return (N) json;
            } else {
                return (N) client.newCall(request);
            }
        } catch (Exception e) {

            return null;
        }

    }


    public <N extends Object> N get(String url, Map<String, String> param, final Class<?> clz, final int connType) {
        url = getBasicUrl(url);

        OkHttpClient client = buildClient(connType);
        if (param != null && param.size() > 0) {
            try {
                String paramStr = getUrlParamsFromMap(param);
                if (url.indexOf("?") != -1) {
                    url += "&" + paramStr;
                } else {
                    url += "?" + paramStr;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "get: >>>>>>>>>>>" + url);
        try {
            Request request = new Request.Builder().header("Authentication", token)
                    .addHeader("Accept-Language", lang)
                    .addHeader("User-Agent", UserAgent)
                    .url(url).build();

            if (clz != null) {

                Response response = client.newCall(request).execute();
                String json = response.body().string();


                if (json != null && !json.isEmpty()) {
                    if (clz != null) {
                        Gson gson = new Gson();
                        return (N) gson.fromJson(json, clz);
                    }

                }
                return (N) json;
            } else {

                return (N) client.newCall(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String get(String url, Map<String, String> param, final int connType) {
        url = getBasicUrl(url);
        Log.i(TAG, url);
        OkHttpClient client = buildClient(connType);
        if (param != null && param.size() > 0) {
            try {
                String paramStr = getUrlParamsFromMap(param);
                if (url.indexOf("?") != -1) {
                    url += "&" + paramStr;
                } else {
                    url += "?" + paramStr;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Request request = new Request.Builder().header("Authentication", token)
                    .addHeader("Accept-Language", lang)
                    .addHeader("User-Agent", UserAgent)
                    .url(url).build();

            Response response = client.newCall(request).execute();
            String json = response.body().string();
            return json;

        } catch (Exception e) {
            return null;
        }
    }


    public String post(String url, RequestBody body, final int connType) {
        url = getBasicUrl(url);
        Log.i(TAG, url);
        OkHttpClient client = buildClient(connType);
        try {
            Request request = null;
            if (body != null) {
                request = new Request.Builder().header("Authentication", token)
                        .addHeader("Accept-Language", lang)
                        .addHeader("User-Agent", UserAgent)
                        .url(url).post(body).build();
            } else {
                request = new Request.Builder().header("Authentication", token)
                        .addHeader("Accept-Language", lang)
                        .addHeader("User-Agent", UserAgent)
                        .url(url).build();
            }
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            return json;
        } catch (Exception e) {
            return null;
        }
    }


    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    private String getBasicUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        } else {
            return (this.baseUrl != null ? this.baseUrl : "") +url;
        }
    }

    private String getUrlParamsFromMap(Map<String, String> map) throws Exception {
        try {
            if (null != map) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    stringBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                }
                String content = stringBuilder.toString();
                if (content.endsWith("&")) {
                    content = StringUtils.substringBeforeLast(content, "&");
                }
                return content;
            }
        } catch (Exception e) {
            Log.e(HttpHelp.class.getName(), e.getMessage().toString());
            return null;

        }
        return null;
    }

    public static String buildUrl(String domain, int port, String rootPath) {
        return getRequestUrl(domain, port, rootPath);

    }

    private static String getRequestUrl(String domain, int port, String rootPath) {
        StringBuilder baseUrl = new StringBuilder();

        if (port == HttpHelp.SSL) {
            baseUrl.append("https://");
        } else {
            baseUrl.append("http://");
        }


        baseUrl.append(domain);

        if (StringUtils.isEmpty(rootPath)) {
            baseUrl.append(rootPath);
        }


        return baseUrl.toString();

    }


    private MyTrustManager mMyTrustManager;

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            mMyTrustManager = new MyTrustManager();
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{mMyTrustManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return ssfFactory;
    }

    //实现X509TrustManager接口
    public class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    //实现HostnameVerifier接口
    private class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    /**
     * 获去信任自签证书的trustManager
     *
     * @param in 自签证书输入流
     * @return 信任自签证书的trustManager
     * @throws GeneralSecurityException
     */
    private X509TrustManager trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        //通过证书工厂得到自签证书对象集合
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }
        //为证书设置一个keyStore
        char[] password = SSL_PASSWORD.toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        //将证书放入keystore中
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }
        // Use it to build an X509 trust manager.
        //使用包含自签证书信息的keyStore去构建一个X509TrustManager
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(null, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }


    public static String toDomain(String basicUrl) {
        String[] strAry = basicUrl.split("\\:");
        return strAry[0];
    }


    public Call doFilePost(String url, Map<String,String> param, File file, final int connType) {

        try {


            url = getBasicUrl(url);
            Log.i(TAG, url);
            OkHttpClient client = buildClient(connType);
            MediaType type = MediaType.parse("application/octet-stream");
            RequestBody fileBody = RequestBody.create(type, file);

            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            requestBody.addFormDataPart("file",file.getName(),fileBody);

            if (param != null) {
                // map 里面是请求中所需要的 key 和 value
                Set<Map.Entry<String, String>> entries = param.entrySet();
                for (Map.Entry entry : entries) {
                    String key = valueOf(entry.getKey());
                    String value = valueOf(entry.getValue());
                    requestBody.addFormDataPart(key,value);
                }
            }
            Request request = new Request.Builder().url(url)
                    .header("Authentication", token)
                    .addHeader("Accept-Language", lang)
                    .addHeader("User-Agent", UserAgent)
                    .post(requestBody.build())//传参数、文件或者混合，改一下就行请求体就行
                    .build();
            return client.newCall(request);
        } catch (Exception e) {
            return null;
        }

    }


     /*
    原生文件上传
     */

    public String doFilePost(String urlstr, Map<String, String> map,
                             Map<String, File> files, ICallUploadInterface callUploadInterface) throws IOException {
        int fixedLength = getBodyLength(map, files);

        urlstr = getBasicUrl(urlstr);

        Log.i(TAG, "doFilePost: " + urlstr);
        String BOUNDARY = "----WebKitFormBoundaryDwvXSRMl0TBsL6kW"; // 定义数据分隔线
        URL url = new URL(urlstr);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(createSSLSocketFactory());
        connection.setHostnameVerifier(new TrustAllHostnameVerifier());


        // 发送POST请求必须设置如下两行
        connection.setConnectTimeout((int) CONNECT_TIMEOUT);
        connection.setReadTimeout((int) READ_TIMEOUT);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setFixedLengthStreamingMode(fixedLength);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent", UserAgent);
        connection.setRequestProperty("Charsert", "UTF-8");
        connection.setRequestProperty("Authentication", token);
        connection.setRequestProperty("Accept-Language", lang);
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
        connection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + BOUNDARY);


        OutputStream out = new DataOutputStream(connection.getOutputStream());
        byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线

        // 文件
        if (files != null && !files.isEmpty()) {
            for (Map.Entry<String, File> entry : files.entrySet()) {
                File file = entry.getValue();
                String fileName = entry.getKey();

                StringBuilder sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data;name=file;filename=\"" + fileName
                        + "\"\r\n");
                sb.append("Content-Type: application/octet-stream\r\n\r\n");
                byte[] data = sb.toString().getBytes();
                out.write(data);

                DataInputStream in = new DataInputStream(new FileInputStream(
                        file));
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                    callUploadInterface.callUploadSize(bytes);
                    out.write(bufferOut, 0, bytes);
                }
                out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
                in.close();
            }
        }
        // 数据参数
        if (map != null && !map.isEmpty()) {

            for (Map.Entry<String, String> entry : map.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\"");
                sb.append("\r\n");
                sb.append("\r\n");
                sb.append(entry.getValue());
                sb.append("\r\n");
                byte[] data = sb.toString().getBytes();
                out.write(data);
            }
        }
        out.write(end_data);
        out.flush();
        out.close();

        Log.i("----", ">>>" + connection.getResponseCode());

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inStream = connection.getInputStream();
            byte[] number = read(inStream);
            String json = new String(number);
            return json;
        }

        throw new IOException("System error|" + connection.getResponseCode() + "|" + urlstr);
    }

    private byte[] read(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    private int getBodyLength(Map<String, String> map, Map<String, File> files) {

        int bodyLength = 0;
        String BOUNDARY = "----WebKitFormBoundaryDwvXSRMl0TBsL6kW";
        byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// Length1
        bodyLength += end_data.length;
        // 文件
        if (files != null && !files.isEmpty()) {
            for (Map.Entry<String, File> entry : files.entrySet()) {
                File file = entry.getValue();
                String fileName = entry.getKey();

                StringBuilder sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data;name=file;filename=\"" + fileName + "\"\r\n");
                sb.append("Content-Type: application/octet-stream\r\n\r\n");
                byte[] data = sb.toString().getBytes();

                bodyLength += data.length;

                bodyLength += (file.length() + ("\r\n".getBytes()).length);

            }
        }

        // 数据参数
        if (map != null && !map.isEmpty()) {

            for (Map.Entry<String, String> entry : map.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"");
                sb.append("\r\n");
                sb.append("\r\n");
                sb.append(entry.getValue());
                sb.append("\r\n");
                byte[] data = sb.toString().getBytes();
                bodyLength += data.length;
            }
        }

        return bodyLength;

    }


}
