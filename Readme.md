Java GraphQL Schema Generation and Execution Framework
========

# Table of Contents
- [Overview](#overview)
- [Dependencies](#dependencies)
- [Hello World](#hello-world)
- [Getting Started](#getting-started)

### Overview

This is a java to GraphQL schema generation and execution package. This originated the week of January 18th, 2016 as a hackweek 
project I worked on.  The goal of this is a production level quality project that can be used to build
a Java based GraphQL server. 
The following principles will guide this projects evolution:

- An unopinionated view of the container the server will use.
- An unopinionated view of the serialization model you will be using
- Sensible defaults so that setup to use is extremely easy and straightforward
- Extensible enough to handle the most extreme Enterprise scenarios
- A minimal set of dependencies to utlize the framework.

Initial Versions will have a base version of Java 7 and require the Guava Module. Future versions will be based on Java 8 and
possibly require the Guava module (TBD).

### Dependencies

- Guava (regardless if you include the guava type mapping package)
- SLF4J (logging)
- GraphQL-Java


### Hello World

Here is a simple hello world usage scenario

```java

package com.schemagen.graphql.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.schemagen.graphql.GraphQLSchemaBuilder;
import com.schemagen.graphql.annotations.GraphQLQuery;
import com.schemagen.graphql.annotations.GraphQLQueryable;
import com.schemagen.graphql.examples.common.JacksonTypeFactory;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * This is a hello world example of a graphQL query CLI
 */
@GraphQLQueryable
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
				.registerObjectMapper(new JacksonTypeFactory(objectMapper))
				// register the instance of Hello World as our query handler
				.registerQueryHandlers(ImmutableList.<Object>of(helloWorld))
				.build();

		// now lets execute a query against the schema
		ExecutionResult result = new GraphQL(schema).execute("{ helloWorld }");
		if (result.getErrors().size() != 0) {
			// if there are any errors serialize them using jackson and write them to stderr
			System.err.println(objectMapper.writeValueAsString(result.getErrors()));
		} else {
			// output your response as JSON serialized data
			System.out.println(objectMapper.writeValueAsString(result.getData()));
		}
	}
}

```
### Getting Started

