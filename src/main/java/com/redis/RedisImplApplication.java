package com.redis;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class RedisImplApplication {
//   
//	Sentry.init(options -> {
//		options.setDsn("https://ecd6886fb2f0dabaf624b46e3680dbbf@o4509762616623104.ingest.us.sentry.io/4509762618195968");
//    options.setTracesSampleRate(1.0);
//    options.setDebug(true); // Show internal Sentry logs in console
//    options.setEnvironment("prod");
//});
	public static void main(String[] args) {
		SpringApplication.run(RedisImplApplication.class, args);
	}

}
