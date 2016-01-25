package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.datafetchers.ITypeFactory;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
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
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
	ITypeFactory objectMapper;
	GraphQLObjectMapper graphQLObjectMapper;
	GraphQLObjectType expectedObjectType;

	private enum TestEnum {
		KEY1,
		KEY2,
		KEY3
	}

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
				Optional.<List<String>>of(ImmutableList.of(IGraphQLTypeMapper.class.getPackage().getName())));
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

	private void assertGenericMapEnumTypeMapping(String name, GraphQLOutputType expectedValueType, GraphQLOutputType graphQLOutputType) {

		GraphQLObjectType objectType = (GraphQLObjectType) graphQLOutputType;

		// verify we contstructed an object from the map with the enum values as fields
		// and their type the expected value type
		assertEquals(GraphQLObjectType.class, graphQLOutputType.getClass());
		assertEquals(name, graphQLOutputType.getName());
		assertNotNull(objectType.getFieldDefinition(TestEnum.KEY1.toString()));
		assertNotNull(objectType.getFieldDefinition(TestEnum.KEY2.toString()));
		assertNotNull(objectType.getFieldDefinition(TestEnum.KEY3.toString()));
		assertEquals(GraphQLFieldDefinition.class, objectType.getFieldDefinition(TestEnum.KEY1.toString()).getClass());
		assertEquals(GraphQLFieldDefinition.class, objectType.getFieldDefinition(TestEnum.KEY2.toString()).getClass());
		assertEquals(GraphQLFieldDefinition.class, objectType.getFieldDefinition(TestEnum.KEY3.toString()).getClass());
		assertEquals(expectedValueType.getName(), objectType.getFieldDefinition(TestEnum.KEY1.toString()).getType().getName());
		for (GraphQLFieldDefinition field : objectType.getFieldDefinitions()) {
			if (expectedValueType instanceof GraphQLObjectType) {
				GraphQLObjectType valueObjectType = (GraphQLObjectType) field.getType();

				assertEquals("field1", valueObjectType.getFieldDefinition("field1").getName());
				assertEquals(Scalars.GraphQLString, valueObjectType.getFieldDefinition("field1").getType());
				assertEquals("field2", valueObjectType.getFieldDefinition("field2").getName());
				assertEquals(Scalars.GraphQLInt, valueObjectType.getFieldDefinition("field2").getType());
			} else {
				assertEquals(expectedValueType, field.getType());
			}
		}

	}


	@Test
	public void testGenericCollectionsMapping() {

		assertGenericListTypeMapping(Scalars.GraphQLString.getName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<Collection<String>>() { }.getType())); assertGenericListTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(new TypeToken<ArrayList<Integer>>() {
		}.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(new TypeToken<Collection<Long>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<Collection<Float>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<Collection<Double>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(new TypeToken<Collection<Boolean>>() { }.getType()));
		assertGenericListTypeMapping(expectedObjectType.getName(), expectedObjectType, graphQLObjectMapper.getOutputType(new TypeToken<Collection<CollectionsTestObject>>() { }.getType()));
	}

	@Test
	public void testGenericListMappings() {

		assertGenericListTypeMapping(Scalars.GraphQLString.getName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<List<String>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(new TypeToken<List<Integer>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(new TypeToken<List<Long>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<List<Float>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<List<Double>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(new TypeToken<List<Boolean>>() { }.getType()));
		assertGenericListTypeMapping(expectedObjectType.getName(), expectedObjectType, graphQLObjectMapper.getOutputType(new TypeToken<List<CollectionsTestObject>>() { }.getType()));
	}

	@Test
	public void testGenericSetMappings() {

		assertGenericListTypeMapping(Scalars.GraphQLString.getName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<List<String>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(new TypeToken<List<Integer>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(new TypeToken<List<Long>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<List<Float>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<List<Double>>() { }.getType()));
		assertGenericListTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(new TypeToken<List<Boolean>>() { }.getType()));
		assertGenericListTypeMapping(expectedObjectType.getName(), expectedObjectType, graphQLObjectMapper.getOutputType(new TypeToken<List<CollectionsTestObject>>() { }.getType()));
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


	@Test
	public void testGenericMapEnum() {

		assertGenericMapEnumTypeMapping(Map.class.getSimpleName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<Map<TestEnum, String>>(){}.getType()));
		assertGenericMapEnumTypeMapping(Map.class.getSimpleName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(new TypeToken<Map<TestEnum, Integer>>(){}.getType()));
		assertGenericMapEnumTypeMapping(Map.class.getSimpleName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(new TypeToken<Map<TestEnum,Long>>(){}.getType()));
		assertGenericMapEnumTypeMapping(Map.class.getSimpleName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<Map<TestEnum,Float>>(){}.getType()));
		assertGenericMapEnumTypeMapping(Map.class.getSimpleName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(new TypeToken<Map<TestEnum,Double>>(){}.getType()));
		assertGenericMapEnumTypeMapping(Map.class.getSimpleName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(new TypeToken<Map<TestEnum,Boolean>>(){}.getType()));
		assertGenericMapEnumTypeMapping(Map.class.getSimpleName(), expectedObjectType, graphQLObjectMapper.getOutputType(new TypeToken<Map<TestEnum, CollectionsTestObject>>(){}.getType()));
	}

	@Test
	public void testRecursiveTypes() {
		GraphQLOutputType outputType = graphQLObjectMapper.getOutputType(new TypeToken<Map<TestEnum, List<List<List<List<List<List<Map<TestEnum, List<String>>>>>>>>>>(){}.getType());

		assertEquals(GraphQLObjectType.class, outputType.getClass());
		assertEquals(Map.class.getSimpleName(), outputType.getName());

		for (TestEnum fieldKey : EnumSet.allOf(TestEnum.class)) {
			GraphQLObjectType objectType = (GraphQLObjectType)outputType;
			GraphQLFieldDefinition fieldDefinition = objectType.getFieldDefinition(fieldKey.name());
			assertEquals(fieldKey.name(), fieldDefinition.getName());
			assertEquals(GraphQLList.class, fieldDefinition.getType().getClass());
			GraphQLType tempType = fieldDefinition.getType();
			int depth = 0;
		 	while(tempType.getClass() == GraphQLList.class) {
				depth++;
				tempType = ((GraphQLList)tempType).getWrappedType();
			};
			assertEquals(6,depth);

			// now we verify the map type
			objectType = (GraphQLObjectType) tempType;
			assertEquals(Map.class.getSimpleName(), objectType.getName());
			assertEquals(GraphQLFieldDefinition.class, objectType.getFieldDefinition(TestEnum.KEY1.toString()).getClass());
			assertEquals(GraphQLFieldDefinition.class, objectType.getFieldDefinition(TestEnum.KEY2.toString()).getClass());
			assertEquals(GraphQLFieldDefinition.class, objectType.getFieldDefinition(TestEnum.KEY3.toString()).getClass());
			assertEquals(GraphQLList.class, objectType.getFieldDefinition(TestEnum.KEY1.toString()).getType().getClass());
			assertEquals(Scalars.GraphQLString, ((GraphQLList)objectType.getFieldDefinition(TestEnum.KEY1.toString()).getType()).getWrappedType());
			assertEquals(Scalars.GraphQLString, ((GraphQLList)objectType.getFieldDefinition(TestEnum.KEY2.toString()).getType()).getWrappedType());
			assertEquals(Scalars.GraphQLString, ((GraphQLList)objectType.getFieldDefinition(TestEnum.KEY3.toString()).getType()).getWrappedType());
		}
	}


}
