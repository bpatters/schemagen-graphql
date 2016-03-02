package com.bretpatterson.schemagen.graphql.datafetchers.spring;

import com.bretpatterson.schemagen.graphql.datafetchers.DefaultMethodDataFetcher;
import com.google.common.base.Throwables;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 *
 */
public class SpringDataFetcher extends DefaultMethodDataFetcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMethodDataFetcher.class);
	private String expression;
	private ApplicationContext context;
	ExpressionParser parser = new SpelExpressionParser();
	BeanFactoryResolver beanFactoryResolver;

	private EvaluationContext getEvaluationContext(Object rootObject, Object[] arguments) {
		StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
		standardEvaluationContext.setBeanResolver(beanFactoryResolver);

		// add the parameter values as variables
		int index = 0;
		for (String name : argumentTypeMap.keySet()) {
			standardEvaluationContext.setVariable(name, arguments[index++]);
		}
		standardEvaluationContext.setRootObject(rootObject);

		return standardEvaluationContext;
	}

	@Override
	public Object invokeMethod(DataFetchingEnvironment environment, Method method, Object targetObject, Object[] arguments) {
		try {
			// here we execute the spring expression
			return parser.parseExpression(expression).getValue(getEvaluationContext(environment.getSource(), arguments));
		} catch (Exception ex) {
			LOGGER.error("Unexpected exception.", ex);
			throw Throwables.propagate(ex);
		}
	}

	public SpringDataFetcher setExpression(String expression) {
		this.expression = expression;

		return this;
	}

	public SpringDataFetcher setApplicationContext(ApplicationContext context) {
		this.context = context;
		// setup the bean factory resolver
		beanFactoryResolver = new BeanFactoryResolver(context);

		return this;
	}
}
