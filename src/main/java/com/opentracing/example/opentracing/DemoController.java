package com.opentracing.example.opentracing;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RestController
public class DemoController {

	// https://github.com/opentracing-contrib/java-opentracing-walkthrough

	OkHttpClient client = new OkHttpClient();

	@Autowired
	private Tracer tracer;

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/hi")
	public String hello() {
		return "Hello from Spring Boot Open Tracing DEMO!";
	}

	@RequestMapping("/openapi/demo")
	public String chain1() throws Exception {
		String response1 = null, response2 = null;
		Span parentSpan = tracer.buildSpan("demo-Work1").start();

		Span span1 = tracer.buildSpan("microService1").asChildOf(parentSpan).start();
		response1 = makeRequest("http://localhost:9091/v1/microserviceA/demo");
		span1.finish();

		Span span2 = tracer.buildSpan("microService2").asChildOf(parentSpan).start();
		response2 = makeRequest("http://localhost:9092/v1/microserviceB/demo");

		span2.finish();
		
		parentSpan.finish();
		
		System.out.println("Parent Span"+parentSpan);

		return "Combined Result : + " + response1 + ":" + response2;

	}

	private String makeRequest(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();

		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}
}
