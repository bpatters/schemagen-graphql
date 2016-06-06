Java GraphQL Schema Generation and Execution Framework
========
The versioning follows [Semantic Versioning](http://semver.org).
[![Build Status](https://travis-ci.org/bpatters/schemagen-graphql.svg?branch=master)](https://travis-ci.org/bpatters/schemagen-graphql)
[![Latest Release](https://maven-badges.herokuapp.com/maven-central/com.bretpatterson/schemagen-graphql/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bretpatterson/schemagen-graphql/)

# Table of Contents
- [Overview](#overview)
- [Dependencies](#dependencies)
- [Hello World](#hello-world)
- [Basic Concepts](#basic-concepts)
- [GraphQL Controllers](#graphql-controllers)
- [Type Mappers](#type-mappers)
- [Companies using in Production](#companies-using-in-production)

### Overview

This is a java to GraphQL schema generation and execution package. This originated the week of January 18th, 2016 as a HackWeek 
project I worked on.  The goal of this is a production level quality project that can be used to build
a Java based GraphQL server. 
The following principles will guide this projects evolution:

- An un-opinionated view of the container the server will use.
- An un-opinionated view of the serialization model you will be using
- Sensible defaults so that setup is extremely easy and straightforward
- Extensible enough to handle the most extreme Enterprise scenarios
- A minimal set of dependencies to utilize the framework.

Initial Versions will have a base version of Java 7 and require the Guava Module. Future versions will possibly be based on Java 8 and
possibly require the Guava module (TBD).

### Dependencies

- Guava (regardless if you include the guava type mapping package)
- SLF4J (logging)
- GraphQL-Java


### Hello World

The Hello World program below is the simplest GraphQL server you can write. It exposes the GraphQL schema as a CLI
that you can run from the command line.

Here is a simple hello world usage scenario

```java

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

```
### Basic Concepts

> GraphQL is a data query language and runtime designed and used at Facebook to request and deliver data to mobile and web apps since 2012.

Schemagen GraphQL is a package that allows you to turn POJO's into a GraphQL Queryable set of objects. Typically these objects consist of two types:
- Objects with methods that perform Queries or Mutations
- Plain Data Objects used as return values and/or Parameters to Queries/Mutations.

Queryable objects are POJO's where the class is annotated with [@GraphQLController](https://github.com/bpatters/schemagen-graphql/blob/master/src/main/java/com/bretpatterson/schemagen/graphql/annotations/GraphQLController.java) which signals that it contains methods annotated with [@GraphQLQuery](https://github.com/bpatters/schemagen-graphql/blob/master/src/main/java/com/bretpatterson/schemagen/graphql/annotations/GraphQLQuery.java) or [@GraphQLMutation](https://github.com/bpatters/schemagen-graphql/blob/master/src/main/java/com/bretpatterson/schemagen/graphql/annotations/GraphQLMutation.java). These objects have their annotated methods exposed as top level GraphQL fields as Queries or Mutations. 

Plain Data Objects are used to pass data back and forth between the server and the front end code. These require no annotations, but do need to be strongly typed and adhere to the limitations of GraphQL data types. For Java code this primarily means Maps must have Enum's as keys or must not be used at all. This is because Maps are transformed into objects as schema generation time and the keys must be a finite set of known possible values so they can be converted into property fields.

Schemagen-GraphQL can convert most objects into sensible GraphQL data types, however, it sometimes needs help for objects that you need mapped in a custom manner. This is accomplished by writing custom [Type Mappers](#type-mappers).


Sensible defaults are provided for most things. However, there are a things that you must provide:
- ITypeFactory 
-- You must provide an object that knows how to convert objects from GraphQL generic deserialized parameters into Java specific types. The examples package provides a simple [Jackson based Type Factory](https://github.com/bpatters/schemagen-graphql/blob/master/schemagen-graphql-examples/src/main/java/com/bretpatterson/schemagen/graphql/examples/common/JacksonTypeFactory.java).
- [@GraphQLController annotated objects](https://github.com/bpatters/schemagen-graphql/blob/master/src/main/java/com/bretpatterson/schemagen/graphql/annotations/GraphQLController.java)
-- with [@GraphQLQuery annotated methods](https://github.com/bpatters/schemagen-graphql/blob/master/src/main/java/com/bretpatterson/schemagen/graphql/annotations/GraphQLQuery.java)
-- with [@GraphQLMutation annotated methods](https://github.com/bpatters/schemagen-graphql/blob/master/src/main/java/com/bretpatterson/schemagen/graphql/annotations/GraphQLMutation.java)


To build your schema you use the [GraphQLSchemaBuilder](https://github.com/bpatters/schemagen-graphql/blob/master/src/main/java/com/bretpatterson/schemagen/graphql/GraphQLSchemaBuilder.java) in conjunction with the required objects above. To create your GraphQL schema do the following:

```java

GraphQLSchemaBuilder schemaBuilder = GraphQLSchemaBuilder.newBuilder();

// First register your Type Factory so your Schema knows how to convert query/mutation parameters received into java types.
schemaBuilder.registerTypeFactory(new JacksonTypeFactory(objectMapper));

// Next pass in a list of Controller objects that you want to use to expose Queries and Mutations through.
// register the instance of Hello World as our query handler
.registerGraphQLControllerObjects(ImmutableList.<Object>of(helloWorld))


// Finally Build your GraphQL schema
GraphQLSchema schema - schemaBuilder.build();
```

That's all!

Now it's up to you as to how you expose your schema to the outside world, but assuming you have a query string you want to execute within your schema you can run the schema using:

```java

ExecutionResult result = new GraphQL(schema).execute(queryString);

```

That's it! Any errors encountered can be obtained via ```result.getErrors()```  and your data results can be obtained via the ```result.getData()```.



### GraphQL Controllers

GraphQL Controllers are how you expose Top level Queries and Mutations via the GraphQL schema. These objects contain three types of annotations:
[@GraphQLController ](https://github.com/bpatters/schemagen-graphql/blob/master/src/main/java/com/bretpatterson/schemagen/graphql/annotations/GraphQLController.java)
-- with [@GraphQLQuery annotated methods](https://github.com/bpatters/schemagen-graphql/blob/master/src/main/java/com/bretpatterson/schemagen/graphql/annotations/GraphQLQuery.java)
-- with [@GraphQLMutation annotated methods](https://github.com/bpatters/schemagen-graphql/blob/master/src/main/java/com/bretpatterson/schemagen/graphql/annotations/GraphQLMutation.java)

#### @GraphQLController annotation
This annotation identifies the type as a controller and optionally specifies a rootObjectName to wrapper it's query in at the top level. 
For example if you take the following class:

```java 

@GraphQLController
public class KeyValueStoreController {
  @GraphQLIgnore
  private Map<String,String> dataStore;
  
  @GraphQLQuery(name="get")
  public String getValue(String key) {
     return dataStore.get(key);
  }
  
  @GraphQLMutation(name="put")
  public String setValue(String key, String value) {
  	dataStore.put(key,value);
  	
  	return value;
  }
}
```

This class exposes a simple key -> value store via GraphQL. There is one query operation, get, and one mutation operation named put. The ```@GraphQLQuery``` and ```@GraphQLMutation``` annotations by default will name the filed the name of the method. However in this case we have chosen to rename the field exposed for the query and mutations respectively to "get" and "put". 

You can then store key/values using:
```mutation Store { put(key:"key1", value:"value1") }```
which outputs: ```{"put":"value1"}```

You could then retrieve the value with:
```query MyQuery { get(key:"key1") }```
which outputs: ```{"get":"value1"}```

You can also do schema queries like:
``` 
{
  __schema {
    queryType {
      name,
      fields {
        name
      }
    }
  }
}
```

which outputs:
```json
{
  "__schema": {
    "queryType": {
      "name": "Query",
      "fields": [
        {
          "name": "node"
        },
        {
          "name": "get"
        }
      ]
    }
  }
```

# Companies using in Production
The following companies have been using in production

- Spredfast
