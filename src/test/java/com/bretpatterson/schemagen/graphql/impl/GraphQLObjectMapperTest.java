package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.GraphQLSchemaBuilder;
import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLDataFetcher;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLIgnore;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.datafetchers.DefaultMethodDataFetcher;
import com.bretpatterson.schemagen.graphql.datafetchers.ITypeFactory;
import com.bretpatterson.schemagen.graphql.relay.RelayConnection;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 * Created by bpatterson on 1/23/16.
 */
public class GraphQLObjectMapperTest {

	@Mock
	ITypeFactory objectMapper;

	@Before
	public void setup() {
		objectMapper = mock(ITypeFactory.class);
	}

	private void assertTypeMapping(String name, GraphQLOutputType expectedType, GraphQLOutputType graphQLOutputType) {
		assertEquals(name, graphQLOutputType.getName());
		assertEquals(expectedType.getClass(), graphQLOutputType.getClass());
	}

	@Test
	public void testOutputMapperPrimitives() {
		IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
				GraphQLSchemaBuilder.getDefaultTypeMappers(),
				Optional.<ITypeNamingStrategy> of(new FullTypeNamingStrategy()),
				ImmutableList.<Class> of());
		assertTypeMapping(Scalars.GraphQLString.getName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(String.class));
		assertTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(Integer.class));
		assertTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(int.class));
		assertTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(Long.class));
		assertTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(long.class));
		assertTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(Float.class));
		assertTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(float.class));
		assertTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(Double.class));
		assertTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(Double.class));
		assertTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(Boolean.class));
		assertTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(boolean.class));

	}

	@Test
	public void testFullTypeMappingStrategy() {
		IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
				GraphQLSchemaBuilder.getDefaultTypeMappers(),
				Optional.<ITypeNamingStrategy> of(new FullTypeNamingStrategy()),
				ImmutableList.<Class> of());
		assertTypeMapping("String", Scalars.GraphQLString, graphQLObjectMapper.getOutputType(String.class));
		assertTypeMapping("Int", Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(Integer.class));
		assertTypeMapping("Int", Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(int.class));
		assertTypeMapping("Long", Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(Long.class));
		assertTypeMapping("Long", Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(long.class));
		assertTypeMapping("Float", Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(Float.class));
		assertTypeMapping("Float", Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(float.class));
		assertTypeMapping("Float", Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(Double.class));
		assertTypeMapping("Float", Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(double.class));
		assertTypeMapping("Boolean", Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(Boolean.class));
		assertTypeMapping("com_bretpatterson_schemagen_graphql_impl_GraphQLObjectMapperTest",
				GraphQLObjectType.newObject().name("com.bretpatterson_schemagen_graphql_impl_GraphQLObjectMapperTest").build(),
				graphQLObjectMapper.getOutputType(this.getClass()));
	}

	@Test
	public void testRelayConnectionType() {
		IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
				GraphQLSchemaBuilder.getDefaultTypeMappers(),
				Optional.<ITypeNamingStrategy> of(new RelayTypeNamingStrategy()),
				ImmutableList.<Class> of());
		GraphQLObjectType type = (GraphQLObjectType) graphQLObjectMapper.getOutputType(new TypeToken<RelayConnection<String>>() {
		}.getType());

		assertEquals("Relay_String_Connection", type.getName());
		assertNotNull(type.getFieldDefinition("edges"));
		assertNotNull(type.getFieldDefinition("pageInfo"));
		assertEquals(GraphQLList.class, type.getFieldDefinition("edges").getType().getClass());
		assertEquals("Edge_String", ((GraphQLList) type.getFieldDefinition("edges").getType()).getWrappedType().getName());
		assertEquals(GraphQLObjectType.class, ((GraphQLList) type.getFieldDefinition("edges").getType()).getWrappedType().getClass());
		GraphQLObjectType edgeObject = (GraphQLObjectType) ((GraphQLList) type.getFieldDefinition("edges").getType()).getWrappedType();
		assertNotNull(edgeObject.getFieldDefinition("node"));
		assertNotNull(edgeObject.getFieldDefinition("cursor"));
		assertEquals(Scalars.GraphQLString, edgeObject.getFieldDefinition("node").getType());
		assertEquals(GraphQLObjectType.class, edgeObject.getFieldDefinition("cursor").getType().getClass());
		assertNotNull(((GraphQLObjectType) edgeObject.getFieldDefinition("cursor").getType()).getFieldDefinition("value"));
		assertEquals(Scalars.GraphQLString, ((GraphQLObjectType) edgeObject.getFieldDefinition("cursor").getType()).getFieldDefinition("value").getType());
	}

	private class InnerGeneric<R, S> {

		R rType;
		S sType;
	}

	private class AnotherGenericObjectTest<R, S> {

		R rType;
		S sType;
	}

	private class GenericObjectTest<R, S> {

		AnotherGenericObjectTest<S, R> srObject;
		InnerGeneric<Float, Boolean> innerFloatDouble;
		List<R> rList;
		R rType;
		S sType;
	}

	@Test
	public void TestGenericObject() {
		IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
				GraphQLSchemaBuilder.getDefaultTypeMappers(),
				Optional.<ITypeNamingStrategy> absent(),
				ImmutableList.<Class> of());
		GraphQLObjectType type = (GraphQLObjectType) graphQLObjectMapper.getOutputType(new TypeToken<GenericObjectTest<Integer, String>>() {
		}.getType());

		assertEquals("GenericObjectTest_Integer_String", type.getName());
		assertEquals(5, type.getFieldDefinitions().size());
		GraphQLFieldDefinition field;

		// verify the inner object type that propogates this generic objects type
		field = type.getFieldDefinition("srObject");
		assertNotNull(field);
		assertEquals(GraphQLObjectType.class, field.getType().getClass());
		assertEquals("AnotherGenericObjectTest_String_Integer", field.getType().getName());
		GraphQLObjectType anotherType = (GraphQLObjectType) field.getType();
		field = anotherType.getFieldDefinition("rType");
		assertNotNull(field);
		assertEquals(Scalars.GraphQLString, field.getType());
		field = anotherType.getFieldDefinition("sType");
		assertNotNull(field);
		assertEquals(Scalars.GraphQLInt, field.getType());

		// now verify the inner generic object with new type arguments
		field = type.getFieldDefinition("innerFloatDouble");
		assertNotNull(field);
		assertEquals(GraphQLObjectType.class, field.getType().getClass());
		assertEquals("InnerGeneric_Float_Boolean", field.getType().getName());
		assertEquals(2, ((GraphQLObjectType) field.getType()).getFieldDefinitions().size());
		assertEquals(Scalars.GraphQLFloat, ((GraphQLObjectType) field.getType()).getFieldDefinition("rType").getType());
		assertEquals(Scalars.GraphQLBoolean, ((GraphQLObjectType) field.getType()).getFieldDefinition("sType").getType());

		// now verify rest of fields on our object
		field = type.getFieldDefinition("rList");
		assertNotNull(field);
		assertEquals(GraphQLList.class, field.getType().getClass());
		assertEquals(Scalars.GraphQLInt, ((GraphQLList) field.getType()).getWrappedType());

		field = type.getFieldDefinition("rType");
		assertNotNull(field);
		assertEquals(Scalars.GraphQLInt, field.getType());

		field = type.getFieldDefinition("sType");
		assertNotNull(field);
		assertEquals(Scalars.GraphQLString, field.getType());
	}

	private class TestIgnoredFields {

		@GraphQLIgnore
		private Map<String, String> keyValueStore;
		private String stringField;
	}

	@Test
	public void testIgnoredFields() {
		IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
				GraphQLSchemaBuilder.getDefaultTypeMappers(),
				Optional.<ITypeNamingStrategy> absent(),
				ImmutableList.<Class> of());
		GraphQLObjectType type = (GraphQLObjectType) graphQLObjectMapper.getOutputType(TestIgnoredFields.class);

		assertEquals(TestIgnoredFields.class.getSimpleName(), type.getName());
		assertEquals(1, type.getFieldDefinitions().size());
		assertNull(type.getFieldDefinition("keyValueStore"));
		assertNotNull(type.getFieldDefinition("stringField"));
		assertEquals("stringField", type.getFieldDefinition("stringField").getName());
		assertEquals(Scalars.GraphQLString, type.getFieldDefinition("stringField").getType());
	}

	public class GenericObjectVariableResolution1<R, S> {

		InnerGeneric<R, S> innerGeneric;
	}

	@Test
	public void testGenericObjectsVariableResolution() {
		IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
				GraphQLSchemaBuilder.getDefaultTypeMappers(),
				Optional.<ITypeNamingStrategy> absent(),
				ImmutableList.<Class> of());

		GraphQLObjectType outputType1 = (GraphQLObjectType) graphQLObjectMapper
				.getOutputType(new TypeToken<GenericObjectVariableResolution1<String, String>>() {
				}.getType());
		GraphQLObjectType outputType2 = (GraphQLObjectType) graphQLObjectMapper
				.getOutputType(new TypeToken<GenericObjectVariableResolution1<Integer, Integer>>() {
				}.getType());

		assertNotNull(outputType1.getFieldDefinition("innerGeneric"));
		assertEquals("InnerGeneric_String_String", outputType1.getFieldDefinition("innerGeneric").getType().getName());

		assertNotNull(outputType2.getFieldDefinition("innerGeneric"));
		assertEquals("InnerGeneric_Integer_Integer", outputType2.getFieldDefinition("innerGeneric").getType().getName());
	}

	@GraphQLTypeMapper(type = String.class, dataFetcher = DefaultMethodDataFetcher.class)
	private class TestTypeMappingDataFetcher implements IGraphQLTypeMapper {

		@Override
		public boolean handlesType(final IGraphQLObjectMapper graphQLObjectMapper, final Type type) {
			return String.class.isAssignableFrom(graphQLObjectMapper.getClassFromType(type));
		}

		@Override
		public GraphQLOutputType getOutputType(final IGraphQLObjectMapper graphQLObjectMapper, final Type type) {
			return Scalars.GraphQLString;
		}

		@Override
		public GraphQLInputType getInputType(final IGraphQLObjectMapper graphQLObjectMapper, final Type type) {
			return Scalars.GraphQLString;
		}
	}

	private class TestType {

		String myfield;
	}

	@Test
	public void testTypeMappingDataFetcher() {
		IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
				ImmutableList.<IGraphQLTypeMapper> builder().add(new TestTypeMappingDataFetcher()).addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build(),
				Optional.<ITypeNamingStrategy> absent(),
				ImmutableList.<Class> of());

		GraphQLObjectType objectType = (GraphQLObjectType) graphQLObjectMapper.getOutputType(TestType.class);

		assertNotNull(objectType.getFieldDefinition("myfield"));
		assertEquals(DefaultMethodDataFetcher.class, objectType.getFieldDefinition("myfield").getDataFetcher().getClass());

	}

	private class ClassWithFieldDataFetcher {

		@GraphQLDataFetcher(dataFetcher = DefaultMethodDataFetcher.class)
		String field1;
	}

	@Test
	public void testCustomDataFetcherForField() {

		IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
				ImmutableList.<IGraphQLTypeMapper> builder().add(new TestTypeMappingDataFetcher()).addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build(),
				Optional.<ITypeNamingStrategy> absent(),
				ImmutableList.<Class> of());
		GraphQLObjectType objectType = (GraphQLObjectType) graphQLObjectMapper.getOutputType(ClassWithFieldDataFetcher.class);

		assertNotNull(objectType.getFieldDefinition("field1"));
		assertEquals(DefaultMethodDataFetcher.class, objectType.getFieldDefinition("field1").getDataFetcher().getClass());
	}

	@GraphQLTypeMapper(type = TestType.class)
	private class TestTypeMapper implements IGraphQLTypeMapper {

		@Override
		public boolean handlesType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
			return type == TestType.class;
		}

		@Override
		public GraphQLOutputType getOutputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
			return Scalars.GraphQLString;
		}

		@Override
		public GraphQLInputType getInputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
			return Scalars.GraphQLString;
		}
	}

	@Test
	public void testCustomTypeMappers() {
		IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
						ImmutableList.<IGraphQLTypeMapper> builder().add(new TestTypeMapper()).addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build(),
						Optional.<ITypeNamingStrategy> absent(),
						ImmutableList.<Class> of());
		assertEquals(Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<TestType>(){}.getType()));
	}

}
