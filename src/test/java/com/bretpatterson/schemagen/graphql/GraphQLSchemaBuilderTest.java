package com.bretpatterson.schemagen.graphql;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLController;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLDescription;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLMutation;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLParam;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeConverter;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLName;
import com.bretpatterson.schemagen.graphql.datafetchers.DefaultTypeConverter;
import com.bretpatterson.schemagen.graphql.impl.common.JacksonTypeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bpatterson on 1/23/16.
 */
public class GraphQLSchemaBuilderTest {

	@GraphQLName(name = "RenamedTestInputType")
	private class RenameMe {

	}

	private class TestType {

		String myfield;
	}

	@GraphQLController
	public class TestController {

		String name = "";

		@GraphQLQuery(name = "testType")
		public TestType getTestType() {
			return new TestType();
		}

		@GraphQLQuery(name = "testQueryArguments")
		public String testQueryArguments(@GraphQLParam(name = "string") String stringType,
				@GraphQLParam(name = "int") Integer intType,
				@GraphQLParam(name = "test") RenameMe testType) {
			return "";
		}

		@GraphQLMutation
		public String setName(@GraphQLParam(name = "name") String name) {
			this.name = name;

			return this.name;
		}

		@GraphQLQuery
		public String getName() {
			return name;
		}
	}

	@Test
	public void testController() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder().registerGraphQLControllerObjects(ImmutableList.<Object> of(new TestController())).build();

		GraphQLFieldDefinition queryType = schema.getQueryType().getFieldDefinition("testType");
		assertEquals("testType", queryType.getName());
		assertEquals("TestType", queryType.getType().getName());
	}

	@Test
	public void testQueryArguments() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder().registerGraphQLControllerObjects(ImmutableList.<Object> of(new TestController())).build();

		GraphQLFieldDefinition queryType = schema.getQueryType().getFieldDefinition("testQueryArguments");
		assertEquals("testQueryArguments", queryType.getName());
		assertEquals(Scalars.GraphQLString, queryType.getArgument("string").getType());
		assertEquals(Scalars.GraphQLInt, queryType.getArgument("int").getType());
		assertEquals(GraphQLInputObjectType.class, queryType.getArgument("test").getType().getClass());
		assertEquals("RenamedTestInputType_Input", queryType.getArgument("test").getType().getName());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMutation() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder()
				.registerTypeFactory(new JacksonTypeFactory(new ObjectMapper()))
				.registerGraphQLControllerObjects(ImmutableList.<Object> of(new TestController()))
				.build();

		GraphQLFieldDefinition mutationType = schema.getMutationType().getFieldDefinition("setName");
		assertEquals("setName", mutationType.getName());
		assertEquals(Scalars.GraphQLString, mutationType.getArgument("name").getType());

		ExecutionResult result = new GraphQL(schema).execute("mutation M { setName(name: \"The new name\") }");

		String newName = ((Map<String, String>) result.getData()).get("setName");
		assertEquals(0, result.getErrors().size());
		assertEquals("The new name", newName);

		result = new GraphQL(schema).execute("query Q { getName }");

		newName = ((Map<String, String>) result.getData()).get("getName");
		assertEquals(0, result.getErrors().size());
		assertEquals("The new name", newName);
	}

	@GraphQLController(rootQueriesObjectName = "QueriesScope", rootMutationsObjectName = "MutationsScope")
	private class ControllerScoping {

		@GraphQLQuery(name = "query1")
		public String query1(@GraphQLParam(name = "param1") String param1) {
			return param1;
		}

		@GraphQLMutation(name = "mutation1")
		public String mutation1(@GraphQLParam(name = "param1") String param1) {
			return param1;
		}

	}

	@Test
	public void testControllerObjectScoping() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder()
				.registerTypeFactory(new JacksonTypeFactory(new ObjectMapper()))
				.registerGraphQLControllerObjects(ImmutableList.<Object> of(new ControllerScoping()))
				.build();

		// validate query scope
		GraphQLFieldDefinition queryScope = schema.getQueryType().getFieldDefinition("QueriesScope");
		assertNotNull(queryScope);
		assertEquals(GraphQLObjectType.class, queryScope.getType().getClass());

		GraphQLObjectType queryObject = (GraphQLObjectType) queryScope.getType();
		GraphQLFieldDefinition query1Field = queryObject.getFieldDefinition("query1");
		assertNotNull(query1Field);
		assertNotNull(query1Field.getArgument("param1"));
		assertEquals(Scalars.GraphQLString, query1Field.getArgument("param1").getType());

		// validate mutation scope
		GraphQLFieldDefinition mutationScope = schema.getMutationType().getFieldDefinition("MutationsScope");
		assertNotNull(mutationScope);
		assertEquals(GraphQLObjectType.class, mutationScope.getType().getClass());
		GraphQLObjectType mutationObject = (GraphQLObjectType) mutationScope.getType();
		GraphQLFieldDefinition mutationField = mutationObject.getFieldDefinition("mutation1");
		assertNotNull(mutationField);
		assertNotNull(mutationField.getArgument("param1"));
		assertEquals(Scalars.GraphQLString, mutationField.getArgument("param1").getType());
	}

	public static class AppendingTypeconverter extends DefaultTypeConverter {

		public AppendingTypeconverter(DataFetcher datafetcher) {
			super(datafetcher);
		}

		@Override
		public Object convert(Object value) {
			return "prepend:" + value.toString();
		}
	}

	@GraphQLController
	public class TypeConverterTest {

		@GraphQLTypeConverter(typeConverter = AppendingTypeconverter.class)
		@GraphQLQuery(name="someStrings")
		public String getSomeStrings() {
			return "1";
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testTypeConverterDetection() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder()
				.registerTypeFactory(new JacksonTypeFactory(new ObjectMapper()))
				.registerGraphQLControllerObjects(ImmutableList.<Object> of(new TypeConverterTest()))
				.build();

		assertEquals("prepend:1", ((Map<String, String>)new GraphQL(schema).execute("{ someStrings }").getData()).get("someStrings"));
	}

	@GraphQLController(rootQueriesObjectName = "Queries", rootMutationsObjectName = "Mutations",
			queryDescription="Query Description", mutationDescription="Mutation Description")
	public class DocumentedMethodsTest {

		@GraphQLQuery(name="someStrings")
		@GraphQLDescription("getSomeStrings description")
		public String getSomeStrings() {
			return "1";
		}

		@GraphQLMutation(name="someStrings")
		@GraphQLDescription("setSomeStrings description")
		public String setSomeStrings(@GraphQLParam(name = "name")
									 @GraphQLDescription("setSomeStrings param name description")
									 String name) {
			return "1";
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDocumentedControllerAndMethods() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder()
				.registerTypeFactory(new JacksonTypeFactory(new ObjectMapper()))
				.registerGraphQLControllerObjects(ImmutableList.<Object>of(new DocumentedMethodsTest()))
				.build();

		GraphQLObjectType mutationType = (GraphQLObjectType) schema.getMutationType().getFieldDefinition("Mutations").getType();
		GraphQLObjectType queryType = (GraphQLObjectType) schema.getQueryType().getFieldDefinition("Queries").getType();;
		assertEquals("Mutation Description", mutationType.getDescription());
		assertEquals("Query Description", queryType.getDescription());
	}
}
