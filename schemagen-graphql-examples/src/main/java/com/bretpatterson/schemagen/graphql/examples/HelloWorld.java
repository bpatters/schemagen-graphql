package com.bretpatterson.schemagen.graphql.examples;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.bretpatterson.schemagen.graphql.GraphQLSchemaBuilder;
import com.bretpatterson.schemagen.graphql.examples.common.JacksonTypeFactory;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * This is a hello world example of a graphQL query CLI
 */
@GraphQLController
public class HelloWorld {

	@GraphQLQuery(name="helloWorld")
	public String helloWorld() {

		return "Hello World!";
	}

	public static void main(String[] args) throws Exception {
		// the object that we want to expose it's methods as queries
		HelloWorld helloWorld = new HelloWorld();
		// we use a Jackson object mapper object for serialization of values
		ObjectMapper objectMapper = new ObjectMapper();
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder()
				// register an object mappper so that parameter datatypes can be deserialized for method invocation
				.registerTypeFactory(new JacksonTypeFactory(objectMapper))
				// register the instance of Hello World as our query handler
				.registerGraphQLControllerObjects(ImmutableList.<Object>of(helloWorld))
				.build();

		String queryString = "{ helloWorld }";

		if (args.length > 0) {
			queryString = args[0];
		}

		// now lets execute a query against the schema
		ExecutionResult result = new GraphQL(schema).execute(queryString);
		if (result.getErrors().size() != 0) {
			// if there are any errors serialize them using jackson and write them to stderr
			System.err.println(objectMapper.writeValueAsString(result.getErrors()));
		} else {
			// output your response as JSON serialized data
			System.out.println(objectMapper.writeValueAsString(result.getData()));
		}
	}
}
