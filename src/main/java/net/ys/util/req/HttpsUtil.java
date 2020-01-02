package net.ys.util.req;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.X509Certificate;

public class HttpsUtil {

    private static final String ENCODING = "UTF-8";
    private static final int TIMEOUT = 20000;

    private static final String METHOD_GET = "GET";

    private static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";

    public static HttpResponse doGet(String address) {

        HttpsURLConnection connection = null;
        try {
            connection = genConnection(address, METHOD_GET, CONTENT_TYPE_URL_ENCODED);
            int responseCode = connection.getResponseCode();
            String result = genResult(connection.getInputStream());
            return new HttpResponse(responseCode, result);
        } catch (Exception e) {
            return new HttpResponse(9999, e.getMessage());
        } finally {
            close(connection, null);
        }
    }

    public static HttpsURLConnection genConnection(String address, String method, String contentType) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
        URL url = new URL(address);

        HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        TrustManager[] tm = {ignoreCertificationTrustManger};
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        connection.setSSLSocketFactory(ssf);

        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("ENCODING", ENCODING);
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", contentType);
        return connection;
    }

    public static String genResult(InputStream inputStream) throws IOException {
        StringBuffer result = new StringBuffer();
        int len;
        byte[] bytes = new byte[1024];
        while ((len = inputStream.read(bytes)) > 0) {
            result.append(new String(bytes, 0, len, ENCODING));
        }
        inputStream.close();
        return result.toString();
    }

    public static void close(HttpURLConnection connection, OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        } catch (Exception e) {
        }
    }

    private static HostnameVerifier ignoreHostnameVerifier = (s, sslSession) -> true;

    private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {
        private X509Certificate[] certificates;

        @Override
        public void checkClientTrusted(X509Certificate certificates[], String authType) {
            if (this.certificates == null) {
                this.certificates = certificates;
            }
        }

        @Override
        public void checkServerTrusted(X509Certificate[] ax509certificate, String s) {
            if (this.certificates == null) {
                this.certificates = ax509certificate;
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };
}