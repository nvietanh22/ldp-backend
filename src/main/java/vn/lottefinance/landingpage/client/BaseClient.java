package vn.lottefinance.landingpage.client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vn.lottefinance.landingpage.dto.lead.LeadDto;
import vn.lottefinance.landingpage.dto.lead.LeadValidateDto;
import vn.lottefinance.landingpage.properties.BaseProperties;
import vn.lottefinance.landingpage.utils.ConverterUtil;
import vn.lottefinance.landingpage.utils.ProfileUtil;
import vn.lottefinance.landingpage.utils.SSLSocketUtil;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class BaseClient {
    private OkHttpClient okHttpClient;

    @PostConstruct
    public void buildClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("User-Agent", "Mozilla/5.0")
                        .header("Accept-Language", "en-US,en;q=0.5");
                // .header("Authorization", Credentials.basic(baseProperties.getUser(), baseProperties.getPass()));
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        httpClient.addNetworkInterceptor(logging);
        this.okHttpClient = SSLSocketUtil.trustAllSslClient(httpClient.build());
    }
}
