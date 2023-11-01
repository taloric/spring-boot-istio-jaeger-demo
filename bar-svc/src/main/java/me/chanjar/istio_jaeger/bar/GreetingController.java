package me.chanjar.istio_jaeger.bar;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class GreetingController {

  private static final String template = "Hello, %s! I'm Bar. I'll query redis and h2.\n";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private LooGreetingService looGreetingService;

  @RequestMapping("/greeting")
  public String greeting(@RequestParam(value = "name", defaultValue = "World") String name,
      @RequestHeader HttpHeaders httpHeaders) {

    ExecutorService executor = Executors.newFixedThreadPool(2);
    executor.submit(RunnableWrapper.of(new Runnable() {
      @Override public void run() {
        redisTemplate.boundValueOps("bar").get();
      }
    }));

    executor.submit(RunnableWrapper.of(new Runnable() {
      @Override public void run() {
        jdbcTemplate.queryForObject("select 'bar' from dual", String.class);
      }
    }));

    String looGreeting = looGreetingService.greeting();

    return headers(httpHeaders) + String.format(template, name) + looGreeting;
  }

  private String headers(HttpHeaders headers) {

    StringBuilder sb = new StringBuilder();
    sb.append("Bar Request headers:\n");

    Set<String> headerNames = headers.keySet();
    for (String headerName : headerNames) {
      List<String> headerValues = headers.getValuesAsList(headerName);
      sb.append('\t')
          .append(headerName).append(": ")
          .append(StringUtils.join(headerValues, ',')).append('\n');
    }

    return sb.toString();

  }

}
