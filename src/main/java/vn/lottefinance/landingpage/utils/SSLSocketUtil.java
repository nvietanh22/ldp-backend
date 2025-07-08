package vn.lottefinance.landingpage.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class SSLSocketUtil {
    private static final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };
    private static SSLContext trustAllSslContext = null;

    static {
        try {
            trustAllSslContext = SSLContext.getInstance("TLSv1.2");
            trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();
    public static final ConnectionSocketFactory connectionSocketFactory =
            new SSLConnectionSocketFactory(trustAllSslContext);


    public static OkHttpClient trustAllSslClient(OkHttpClient client) {
        OkHttpClient.Builder builder = client.newBuilder();
        builder.sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return hostname.equalsIgnoreCase(session.getPeerHost());
            }
        });
        return builder.build();
    }

}
