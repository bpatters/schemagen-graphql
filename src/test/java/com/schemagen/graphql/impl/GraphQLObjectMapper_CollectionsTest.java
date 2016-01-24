package com.schemagen.graphql.impl;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.schemagen.graphql.datafetchers.IObjectMapper;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import static org.junit.Assert.assertEquals;

/**
 *	Tests that generic version of the following collection implements can be mapped correctly.
 * @see     java.util.ArrayList
 * @see     java.util.LinkedList
 * @see     java.util.HashSet
 * @see     java.util.TreeSet
 * @see     java.util.Vector
 */
public class GraphQLObjectMapper_CollectionsTest {
	@Mock
	IObjectMapper objectMapper;
	GraphQLObjectMapper graphQLObjectMapper;
	GraphQLObjectType expectedObjectType;

	private class CollectionsTestObject {
		String field1;
		Integer field2;
	}

	@Before
	public void setup() {
		expectedObjectType = GraphQLObjectType.newObject().name(CollectionsTestObject.class.getSimpleName())
				.field(GraphQLFieldDefinition.newFieldDefinition().name("field1").type(Scalars.GraphQLString).build())
				.field(GraphQLFieldDefinition.newFieldDefinition().name("field2").type(Scalars.GraphQLInt).build())
				.build();

		graphQLObjectMapper = new GraphQLObjectMapper(objectMapper, Optional.<List<IGraphQLTypeMapper>>absent(),
				Optional.<List<String>>of(ImmutableList.of("com.schemagen.graphql.mappers")));
	}

	private void assertGenericListTypeMapping(String name, GraphQLOutputType expectedWrappedType, GraphQLOutputType graphQLOutputType) {
		assertEquals(GraphQLList.class, graphQLOutputType.getClass());

		GraphQLList listType = (GraphQLList) graphQLOutputType;
		GraphQLType wrappedType = listType.getWrappedType();

		assertEquals(name, wrappedType.getName());
		assertEquals(expectedWrappedType.getClass(), wrappedType.getClass());

		if (expectedWrappedType instanceof GraphQLObjectType) {
			GraphQLObjectType objectType = (GraphQLObjectType) wrappedType;

			assertEquals("field1", objectType.getFieldDefinition("field1").getName());
			assertEquals(Scalars.GraphQLString, objectType.getFieldDefinition("field1").getType());
			assertEquals("field2", objectType.getFieldDefinition("field2").getName());
			assertEquals(Scalars.GraphQLInt, objectType.getFieldDefinition("field2").getType());
		}
	}

	@Test
	public void testGenericArrayListMapping() {

		assertGenericListTypeMapping(Scalars.GraphQLString.getName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<ArrayList<String>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(new TypeToken<ArrayList<Integer>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(new TypeToken<ArrayList<Long>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<ArrayList<Float>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<ArrayList<Double>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(new TypeToken<ArrayList<Boolean>>(){}.getType()));
		assertGenericListTypeMapping(expectedObjectType.getName(), expectedObjectType, graphQLObjectMapper.getOutputType(new TypeToken<ArrayList<CollectionsTestObject>>(){}.getType()));
	}

	@Test
	public void testGenericLinkedListMapping() {

		assertGenericListTypeMapping(Scalars.GraphQLString.getName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<LinkedList<String>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(new TypeToken<LinkedList<Integer>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(new TypeToken<LinkedList<Long>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<LinkedList<Float>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<LinkedList<Double>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(new TypeToken<LinkedList<Boolean>>(){}.getType()));
		assertGenericListTypeMapping(expectedObjectType.getName(), expectedObjectType, graphQLObjectMapper.getOutputType(new TypeToken<LinkedList<CollectionsTestObject>>(){}.getType()));
	}


	@Test
	public void testGenericHashSetMapping() {

		assertGenericListTypeMapping(Scalars.GraphQLString.getName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<HashSet<String>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(new TypeToken<HashSet<Integer>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(new TypeToken<HashSet<Long>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<HashSet<Float>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<HashSet<Double>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(new TypeToken<HashSet<Boolean>>(){}.getType()));
		assertGenericListTypeMapping(expectedObjectType.getName(), expectedObjectType, graphQLObjectMapper.getOutputType(new TypeToken<HashSet<CollectionsTestObject>>(){}.getType()));
	}

	@Test
	public void testGenericTreeSetMapping() {

		assertGenericListTypeMapping(Scalars.GraphQLString.getName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<TreeSet<String>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(new TypeToken<TreeSet<Integer>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(new TypeToken<TreeSet<Long>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<TreeSet<Float>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<TreeSet<Double>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(new TypeToken<TreeSet<Boolean>>(){}.getType()));
		assertGenericListTypeMapping(expectedObjectType.getName(), expectedObjectType, graphQLObjectMapper.getOutputType(new TypeToken<TreeSet<CollectionsTestObject>>(){}.getType()));
	}

	@Test
	public void testGenericVectorMapping() {

		assertGenericListTypeMapping(Scalars.GraphQLString.getName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<Vector<String>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(new TypeToken<Vector<Integer>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(new TypeToken<Vector<Long>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<Vector<Float>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<Vector<Double>>(){}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(new TypeToken<Vector<Boolean>>(){}.getType()));
		assertGenericListTypeMapping(expectedObjectType.getName(), expectedObjectType, graphQLObjectMapper.getOutputType(new TypeToken<Vector<CollectionsTestObject>>(){}.getType()));
	}
}
