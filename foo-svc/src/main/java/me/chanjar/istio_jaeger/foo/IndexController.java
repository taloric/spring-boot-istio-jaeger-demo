package me.chanjar.istio_jaeger.foo;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.skywalking.apm.toolkit.trace.SupplierWrapper;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class IndexController {

  @Autowired
  private BarGreetingService barGreetingService;

  @RequestMapping(value = "/", produces = MimeTypeUtils.TEXT_PLAIN_VALUE)
  public String index(@RequestHeader HttpHeaders headers) {

    CompletableFuture<String> result = CompletableFuture.supplyAsync(SupplierWrapper.of(() -> {
      return headers(headers) + barGreetingService.greeting();
    }));

    try {
      return result.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    return "";
    //return headers(headers) + barGreetingService.greeting();
  }

  private String headers(HttpHeaders headers) {

    StringBuilder sb = new StringBuilder();
    sb.append("Foo Request headers:\n");

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
