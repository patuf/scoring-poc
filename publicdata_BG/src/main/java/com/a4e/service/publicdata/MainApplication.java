package com.a4e.service.publicdata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@SpringBootApplication
public class MainApplication {
//	@Autowired
//	MongoTemplate mongoTmpl;
	private final Log log = LogFactory.getLog(getClass());

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

	@Bean
	MessageListenerContainer mlContainer(MongoTemplate mongoTmpl) {
		return new DefaultMessageListenerContainer(mongoTmpl) {
			@Override
			public boolean isAutoStartup() {
				return true;
			}
		};
	}

	@Bean
	MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
		return new MongoTransactionManager(dbFactory);
	}

	@Bean
	public RecordMessageConverter converter() {
		return new JsonMessageConverter();
	}

//	class AutoStopMessageListenerContainer extends DefaultMessageListenerContainer implements
}
