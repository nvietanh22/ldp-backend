package vn.lottefinance.landingpage.client.custom;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ProxyCustomizer implements RestTemplateCustomizer {

    private String host;
    private int port;

    public ProxyCustomizer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        HttpHost proxy = new HttpHost(host, port);
        HttpClient httpClient = HttpClientBuilder.create()
                .setRoutePlanner(new DefaultProxyRoutePlanner(proxy) {
                    @Override
                    public HttpHost determineProxy(HttpHost target, HttpContext context) throws HttpException {
                        return super.determineProxy(target, context);
                    }
                })
                .build();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

}