package com.opentracing.example.opentracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.internal.samplers.ConstSampler;

@SpringBootApplication
public class OpentracingApplication {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.build();
	}

	/*
	 * @Bean public io.opentracing.Tracer zipkinTracer() {
	 * 
	 * OkHttpSender okHttpSender =
	 * OkHttpSender.create("http://localhost:9411/api/v1/spans");
	 * 
	 * AsyncReporter<Span> reporter = AsyncReporter.builder(okHttpSender).build();
	 * 
	 * Tracing braveTracer =
	 * Tracing.newBuilder().localServiceName("opentracing-demo").reporter(reporter).
	 * build();
	 * 
	 * return BraveTracer.create(braveTracer);
	 * 
	 * }
	 */

	@Bean
	public io.opentracing.Tracer jaegerTracer() {
		SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType(ConstSampler.TYPE).withParam(1);

		ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withLogSpans(true);

		Configuration config = new Configuration("opentracing-demo").withSampler(samplerConfig)
				.withReporter(reporterConfig);

		return config.getTracer();

	}

	public static void main(String[] args) {
		SpringApplication.run(OpentracingApplication.class, args);
	}

}
