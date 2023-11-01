package me.chanjar.istio_jaeger.loo;

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

  private static final String template = "Hello, %s! I'm Loo. I'll query redis and h2.\n";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @RequestMapping("/greeting")
  public String greeting(@RequestParam(value = "name", defaultValue = "World") String name,
      @RequestHeader HttpHeaders headers) {
    
    ExecutorService executor = Executors.newFixedThreadPool(2);
    executor.submit(RunnableWrapper.of(new Runnable() {
      @Override public void run() {
        redisTemplate.boundValueOps("loo").get();
      }
    }));

    executor.submit(RunnableWrapper.of(new Runnable() {
      @Override public void run() {
        jdbcTemplate.queryForObject("select 'loo' from dual", String.class);
      }
    }));
    
    return headers(headers) + String.format(template, name);
  }

  private String headers(HttpHeaders headers) {

    StringBuilder sb = new StringBuilder();
    sb.append("Loo Request headers:\n");

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
