package com.bretpatterson.schemagen.graphq.datafetchers.spring;

import com.bretpatterson.schemagen.graphql.GraphQLSchemaBuilder;
import com.bretpatterson.schemagen.graphql.IDataFetcherFactory;
import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLParam;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLSpringELDataFetcher;
import com.bretpatterson.schemagen.graphql.datafetchers.DefaultMethodDataFetcher;
import com.bretpatterson.schemagen.graphql.datafetchers.IDataFetcher;
import com.bretpatterson.schemagen.graphql.datafetchers.spring.SpringDataFetcher;
import com.bretpatterson.schemagen.graphql.datafetchers.spring.SpringDataFetcherFactory;
import com.bretpatterson.schemagen.graphql.impl.GraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.impl.SimpleTypeFactory;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by bpatterson on 3/1/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestModule.class})
public class SpringDataFetcherFactoryTest {
	@Autowired
	ApplicationContext context;


	public class SpringDataFetcherTest {
		@GraphQLSpringELDataFetcher(value="@echoBean.echo('Hello World!')")
		String fieldTest = "Invalid Value!";

		@GraphQLSpringELDataFetcher("@echoBean.echo(#param1, #param2)")
		public String getData(@GraphQLParam(name = "param1") String param1, @GraphQLParam(name = "param2") Integer param2) {
			return "data value";
		}

		public String getDataNoSpring(@GraphQLParam(name = "param1") String param1, @GraphQLParam(name = "param2") Integer param2) {
			return "data value";
		}
	}

	@Test
	public void testDefaultDataFetcher() throws NoSuchMethodException {
		SpringDataFetcherFactory factory = new SpringDataFetcherFactory(context);
		GraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(new SimpleTypeFactory(),
						GraphQLSchemaBuilder.getDefaultTypeMappers(),
						Optional.<ITypeNamingStrategy>absent(),
						Optional.<IDataFetcherFactory> of(factory),
						Optional.<Class<? extends IDataFetcher>> absent(),
						GraphQLSchemaBuilder.getDefaultTypeConverters(),
						ImmutableList.<Class> of());

		Collection<GraphQLFieldDefinition> fieldDefinitions = graphQLObjectMapper.getGraphQLFieldDefinitions(Optional.<Object>of(new SpringDataFetcherTest()), SpringDataFetcherTest.class, SpringDataFetcherTest.class,
																											 Optional.<List<java.lang.reflect.Field>>absent(),
																											 Optional.<List<Method>>absent());

		DataFetchingEnvironment environment = mock(DataFetchingEnvironment.class);
		Field field = mock(Field.class);

		willReturn(this).given(environment).getSource();
		willReturn(ImmutableList.of(field)).given(environment).getFields();
		willReturn("value1").given(environment).getArgument("param1");
		willReturn(1234).given(environment).getArgument("param2");

		for (GraphQLFieldDefinition graphQLFieldDefinition : fieldDefinitions) {
			if (graphQLFieldDefinition.getName().equals("data")) {
				willReturn("data").given(field).getName();
				String value = (String)graphQLFieldDefinition.getDataFetcher().get(environment);
				assertEquals(":value1:1234", value);
			} else if (graphQLFieldDefinition.getName().equals("dataNoSpring")) {
				willReturn("dataNoSpring").given(field).getName();
				String value = (String)graphQLFieldDefinition.getDataFetcher().get(environment);
				assertEquals("data value", value);
			} else if (graphQLFieldDefinition.getName().equals("fieldTest")) {
				willReturn("fieldTest").given(field).getName();
				String value = (String)graphQLFieldDefinition.getDataFetcher().get(environment);
				assertEquals(":Hello World!", value);
			} else {
				fail("fields not named properly");
			}
		}
	}

}


