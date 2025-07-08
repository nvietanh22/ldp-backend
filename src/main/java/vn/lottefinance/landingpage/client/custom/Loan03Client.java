package vn.lottefinance.landingpage.client.custom;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.lottefinance.landingpage.dto.lead.LeadDto;
import vn.lottefinance.landingpage.dto.lead.LeadValidateDto;
import vn.lottefinance.landingpage.properties.Loan2Properties;
import vn.lottefinance.landingpage.properties.Loan3Properties;
import vn.lottefinance.landingpage.utils.ConverterUtil;
import vn.lottefinance.landingpage.utils.SSLSocketUtil;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class Loan03Client {
    @Autowired
    private Loan3Properties loan3Properties;

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

    public Object send(Object obj, Class clazz) {
        try {
            log.info("==================== " + loan3Properties.getSendUrl() + " ====================");
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), ConverterUtil.toJsonString(obj));
            Request request = new Request.Builder()
                    .url(loan3Properties.getSendUrl())
                    .post(requestBody)
                    .build();
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            return ConverterUtil.fromJSON(clazz, responseBody.string());
        } catch (IOException ioe) {
            ioe.getMessage();
            log.error(ioe.getMessage());
            LeadDto.Response response = LeadDto.Response.builder()
                    .request_id(UUID.randomUUID().toString())
                    .rslt_cd("error")
                    .rslt_msg("Lỗi hệ thống").build();
            return response;
        }
    }

    public Object sendValidate(Object obj, Class clazz) {
        try {
            log.info("==================== " + loan3Properties.getValidateUrl() + " ====================");
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), ConverterUtil.toJsonString(obj));
            Request request = new Request.Builder()
                    .url(loan3Properties.getValidateUrl())
                    .post(requestBody)
                    .build();
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            return ConverterUtil.fromJSON(clazz, responseBody.string());
        } catch (IOException ioe) {
            ioe.getMessage();
            log.error(ioe.getMessage());
            LeadValidateDto.Response response = LeadValidateDto.Response.builder()
                    .request_id(UUID.randomUUID().toString())
                    .rslt_cd("f")
                    .reason_code("R")
                    .rslt_msg("Lỗi hệ thống").build();
            return response;
        }
    }
}
