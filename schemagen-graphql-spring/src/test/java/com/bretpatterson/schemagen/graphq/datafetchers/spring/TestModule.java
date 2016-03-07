package com.bretpatterson.schemagen.graphq.datafetchers.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by bpatterson on 3/1/16.
 */
@Configuration
public class TestModule {

	@Bean(name="echoBean")
	public EchoBean getEchoBean() {
		return new EchoBean();
	}
}
