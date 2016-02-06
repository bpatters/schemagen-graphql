package com.bretpatterson.schemagen.graphql.examples;

import com.bretpatterson.schemagen.graphql.GraphQLSchemaBuilder;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLController;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLIgnore;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLMutation;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLParam;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import com.bretpatterson.schemagen.graphql.examples.common.JacksonTypeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * This is a hello world example of a graphQL query CLI
 */
@GraphQLController
public class KeyValueStoreController {

	@GraphQLIgnore
	private Map<String, String> dataStore = Maps.newHashMap();

	@GraphQLQuery(name = "get")
	public String getValue(@GraphQLParam(name = "key") String key) {
		return dataStore.get(key);
	}

	@GraphQLMutation(name = "put")
	public String setValue(@GraphQLParam(name = "key") String key, @GraphQLParam(name = "value") String value) {
		dataStore.put(key, value);

		return value;
	}

	public static void main(String[] args) throws Exception {
		// the object that we want to expose it's methods as queries
		KeyValueStoreController keyValueStoreController = new KeyValueStoreController();
		// we use a Jackson object mapper object for serialization of values
		ObjectMapper objectMapper = new ObjectMapper();
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder()
				// register an object mappper so that parameter datatypes can be deserialized for method invocation
				.registerTypeFactory(new JacksonTypeFactory(objectMapper))
				// register the instance of Hello World as our query handler
				.registerGraphQLControllerObjects(ImmutableList.<Object> of(keyValueStoreController))
				.build();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		do {

			System.out.print("Query> ");
			String queryString = br.readLine();
			if ("quit".equals(queryString) || queryString.length() == 0) {
				break;
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
		} while (true);
	}
}
