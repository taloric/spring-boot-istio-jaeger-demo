package me.chanjar.istio_jaeger.bar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LooGreetingService {

  private final RestTemplate restTemplate;

  public LooGreetingService(RestTemplateBuilder restTemplateBuilder, @Value("${loo-svc.url}") String looSvcUrl) {
    this.restTemplate = restTemplateBuilder.rootUri(looSvcUrl).build();

  }

  public String greeting(String name) {
    return restTemplate.getForObject(String.format("/greeting?name=%s", name), String.class);
  }
}
