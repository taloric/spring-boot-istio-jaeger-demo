package me.chanjar.istio_jaeger.bar;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    if (name.equals("cache")) {
        redisTemplate.boundValueOps("bar").get();

        String uid = getUUID();
        long result = redisTemplate.boundSetOps(uid).add(Long.toString(System.currentTimeMillis()));
        System.out.printf("set %s result success with %d\n", uid, result);

        String value = redisTemplate.boundSetOps(uid).randomMember();
        System.out.printf("get uid %s value %s \n ", uid, value);
    }

    if (name.equals("user")) {
      jdbcTemplate.queryForObject("select 'bar' from dual", String.class);
      SqlRowSet set = jdbcTemplate.queryForRowSet("select user from sys.processlist");
      int count = 0;
      while (set.next()) {
        count++;
      }
      System.out.printf("get users length: %d \n ", count);
    }

    String looGreeting = looGreetingService.greeting(name);

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

  private String getUUID() {
    return UUID.randomUUID().toString().replace("-", "").toLowerCase();
  }

}
