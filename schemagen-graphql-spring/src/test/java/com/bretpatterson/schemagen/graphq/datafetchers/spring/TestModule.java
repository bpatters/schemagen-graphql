package com.bretpatterson.schemagen.graphq.datafetchers.spring;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

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
