package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.GraphQLSchemaBuilder;
import com.bretpatterson.schemagen.graphql.IDataFetcherFactory;
import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLDataFetcher;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLDeprecated;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLDescription;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLIgnore;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLParam;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeConverter;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.datafetchers.CollectionConverterDataFetcher;
import com.bretpatterson.schemagen.graphql.datafetchers.DefaultMethodDataFetcher;
import com.bretpatterson.schemagen.graphql.ITypeFactory;
import com.bretpatterson.schemagen.graphql.datafetchers.DefaultTypeConverter;
import com.bretpatterson.schemagen.graphql.datafetchers.IDataFetcher;
import com.bretpatterson.schemagen.graphql.relay.RelayConnection;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
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
import static org.junit.Assert.assertTrue;
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

	private IGraphQLObjectMapper newGraphQLObjectMapper(Optional<ITypeNamingStrategy> typeNamingStrategy) {
		return new GraphQLObjectMapper(objectMapper,
				GraphQLSchemaBuilder.getDefaultTypeMappers(),
				typeNamingStrategy,
				Optional.<IDataFetcherFactory> absent(),
				Optional.<Class<? extends IDataFetcher>> absent(),
				GraphQLSchemaBuilder.getDefaultTypeConverters(),
				ImmutableList.<Class<?>> of());

	}

	private IGraphQLObjectMapper newGraphQLObjectMapper(List<IGraphQLTypeMapper> typeMappers) {
		return new GraphQLObjectMapper(objectMapper,
				typeMappers,
				Optional.<ITypeNamingStrategy> absent(),
				Optional.<IDataFetcherFactory> absent(),
				Optional.<Class<? extends IDataFetcher>> absent(),
				GraphQLSchemaBuilder.getDefaultTypeConverters(),
				ImmutableList.<Class<?>> of());

	}

	@Test
	public void testOutputMapperPrimitives() {
		IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
				GraphQLSchemaBuilder.getDefaultTypeMappers(),
				Optional.<ITypeNamingStrategy> of(new FullTypeNamingStrategy()),
				Optional.<IDataFetcherFactory> absent(),
				Optional.<Class<? extends IDataFetcher>> absent(),
				GraphQLSchemaBuilder.getDefaultTypeConverters(),
				ImmutableList.<Class<?>> of());
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
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(Optional.<ITypeNamingStrategy> of(new FullTypeNamingStrategy()));
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

	@SuppressWarnings("serial")
	@Test
	public void testRelayConnectionType() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(Optional.<ITypeNamingStrategy> of(new RelayTypeNamingStrategy()));
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

		@SuppressWarnings("unused")
		R rType;
		@SuppressWarnings("unused")
		S sType;
	}

	private class AnotherGenericObjectTest<R, S> {

		@SuppressWarnings("unused")
		R rType;
		@SuppressWarnings("unused")
		S sType;
	}

	private class GenericObjectTest<R, S> {

		@SuppressWarnings("unused")
		AnotherGenericObjectTest<S, R> srObject;
		@SuppressWarnings("unused")
		InnerGeneric<Float, Boolean> innerFloatDouble;
		@SuppressWarnings("unused")
		List<R> rList;
		@SuppressWarnings("unused")
		R rType;
		@SuppressWarnings("unused")
		S sType;
	}

	@SuppressWarnings("serial")
	@Test
	public void TestGenericObject() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(Optional.<ITypeNamingStrategy> absent());
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
		@SuppressWarnings("unused")
		private String stringField;
	}

	@Test
	public void testIgnoredFields() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(Optional.<ITypeNamingStrategy> absent());
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

	@SuppressWarnings("serial")
	@Test
	public void testGenericObjectsVariableResolution() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(Optional.<ITypeNamingStrategy> absent());

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
		@SuppressWarnings("unused")
		String myfield;
	}

	@Test
	public void testTypeMappingDataFetcher() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(ImmutableList.<IGraphQLTypeMapper> builder()
				.add(new TestTypeMappingDataFetcher())
				.addAll(GraphQLSchemaBuilder.getDefaultTypeMappers())
				.build());

		GraphQLObjectType objectType = (GraphQLObjectType) graphQLObjectMapper.getOutputType(TestType.class);

		assertNotNull(objectType.getFieldDefinition("myfield"));

	}

	private class ClassWithFieldDataFetcher {

		@GraphQLDataFetcher(dataFetcher = DefaultMethodDataFetcher.class)
		String field1;
	}

	@Test
	public void testCustomDataFetcherForField() {

		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(ImmutableList.<IGraphQLTypeMapper> builder()
				.add(new TestTypeMappingDataFetcher())
				.addAll(GraphQLSchemaBuilder.getDefaultTypeMappers())
				.build());
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

	@SuppressWarnings("serial")
	@Test
	public void testCustomTypeMappers() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(
				ImmutableList.<IGraphQLTypeMapper> builder().add(new TestTypeMapper()).addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build());
		assertEquals(Scalars.GraphQLString, graphQLObjectMapper.getOutputType(new TypeToken<TestType>() {
		}.getType()));
	}

	public class MethodRequiredFields {

		public Object getMethod(@GraphQLParam(name = "param1", required = true) String param1,
								@GraphQLParam(name = "param2") String param2) {
			return null;
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void testMethodParameterRequired() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(
				ImmutableList.<IGraphQLTypeMapper>builder().add(new TestTypeMapper()).addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build());

		GraphQLObjectType objectType = (GraphQLObjectType) graphQLObjectMapper.getOutputType(new TypeToken<MethodRequiredFields>() {
		}.getType());

		assertEquals(MethodRequiredFields.class.getSimpleName(), objectType.getName());
		assertEquals(1, objectType.getFieldDefinitions().size());
		assertNotNull(objectType.getFieldDefinition("method"));

		List<GraphQLArgument> arguments = objectType.getFieldDefinition("method").getArguments();
		assertEquals(2, arguments.size());
		assertEquals(new GraphQLNonNull(Scalars.GraphQLString), arguments.get(0).getType());
		assertEquals(Scalars.GraphQLString, arguments.get(1).getType());
	}

	public class MethodBasedFields {

		@GraphQLIgnore
		private Object ignoredObject;
		private String stringField;
		private List<String> stringList;

		public String getStringField() {
			return stringField;
		}

		public List<String> getStringList() {
			return stringList;
		}

		public Object getIgnoredObject() {
			return ignoredObject;
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void testMethodBasedFields() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(
				ImmutableList.<IGraphQLTypeMapper> builder().add(new TestTypeMapper()).addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build());

		GraphQLObjectType objectType = (GraphQLObjectType) graphQLObjectMapper.getOutputType(new TypeToken<MethodBasedFields>() {
		}.getType());

		assertEquals(MethodBasedFields.class.getSimpleName(), objectType.getName());
		assertEquals(2, objectType.getFieldDefinitions().size());
		assertNotNull(objectType.getFieldDefinition("stringField"));
		assertEquals(Scalars.GraphQLString, objectType.getFieldDefinition("stringField").getType());
		assertNotNull(objectType.getFieldDefinition("stringList"));
		assertEquals(GraphQLList.class, objectType.getFieldDefinition("stringList").getType().getClass());
		assertEquals(Scalars.GraphQLString, ((GraphQLList) objectType.getFieldDefinition("stringList").getType()).getWrappedType());
		assertNull(objectType.getFieldDefinition("ignoredObject"));

		assertEquals(DefaultMethodDataFetcher.class, objectType.getFieldDefinition("stringField").getDataFetcher().getClass());
		assertEquals(CollectionConverterDataFetcher.class, objectType.getFieldDefinition("stringList").getDataFetcher().getClass());
	}

	public class MethodOnlyFields {

		public String getStringField() {
			return "";
		}

		public List<String> getStringList() {
			return ImmutableList.of();
		}

		@GraphQLIgnore
		public Object getIgnoredObject() {
			return "";
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void testMethodOnlyFields() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(
				ImmutableList.<IGraphQLTypeMapper> builder().add(new TestTypeMapper()).addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build());

		GraphQLObjectType objectType = (GraphQLObjectType) graphQLObjectMapper.getOutputType(new TypeToken<MethodOnlyFields>() {
		}.getType());

		assertEquals(MethodOnlyFields.class.getSimpleName(), objectType.getName());
		assertEquals(2, objectType.getFieldDefinitions().size());
		assertNotNull(objectType.getFieldDefinition("stringField"));
		assertEquals(Scalars.GraphQLString, objectType.getFieldDefinition("stringField").getType());
		assertNotNull(objectType.getFieldDefinition("stringList"));
		assertEquals(GraphQLList.class, objectType.getFieldDefinition("stringList").getType().getClass());
		assertEquals(Scalars.GraphQLString, ((GraphQLList) objectType.getFieldDefinition("stringList").getType()).getWrappedType());
		assertNull(objectType.getFieldDefinition("ignoredObject"));

		assertEquals(DefaultMethodDataFetcher.class, objectType.getFieldDefinition("stringField").getDataFetcher().getClass());
		assertEquals(CollectionConverterDataFetcher.class, objectType.getFieldDefinition("stringList").getDataFetcher().getClass());
	}

	@SuppressWarnings("serial")
	@Test
	public void testEnumType() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(
				ImmutableList.<IGraphQLTypeMapper> builder().add(new TestTypeMapper()).addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build());

		GraphQLOutputType outputType =  graphQLObjectMapper.getOutputType(new TypeToken<Enum>() {
		}.getType());

		assertEquals(Scalars.GraphQLString, outputType);
	}



	public static class AppendingTypeconverter extends DefaultTypeConverter {
		public AppendingTypeconverter(DataFetcher datafetcher) {
			super(datafetcher);
		}
		@Override
		public Object convert(Object value) {
			return "prepend:"+value.toString();
		}
	}

	public class TypeConverterTest {
		@GraphQLTypeConverter(typeConverter = AppendingTypeconverter.class)
		String getSomeStrings() {
			return "1";
		}

	}

	@SuppressWarnings("serial")
	@Test
	public void testTypeConverterDetection() {

		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(
				ImmutableList.<IGraphQLTypeMapper> builder().add(new TestTypeMapper()).addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build());
		GraphQLObjectType objectType = (GraphQLObjectType) graphQLObjectMapper.getOutputType(new TypeToken<TypeConverterTest>(){}.getType());
		assertEquals(AppendingTypeconverter.class, objectType.getFieldDefinition("someStrings").getDataFetcher().getClass());
	}



	public class TestDeprecratedClass {
		@GraphQLDeprecated("Old usage")
		String deprecatedField;

		@Deprecated
		String javaDeprecatedField;
	}
	@Test
	public void testDeprecated() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(
				ImmutableList.<IGraphQLTypeMapper> builder().addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build());
		GraphQLObjectType objectType = (GraphQLObjectType) graphQLObjectMapper.getOutputType(new TypeToken<TestDeprecratedClass>(){}.getType());

		assertNotNull(objectType.getFieldDefinition("deprecatedField"));
		assertTrue(objectType.getFieldDefinition("deprecatedField").isDeprecated());
		assertEquals("Old usage", objectType.getFieldDefinition("deprecatedField").getDeprecationReason());

		assertTrue(objectType.getFieldDefinition("javaDeprecatedField").isDeprecated());
		assertEquals("", objectType.getFieldDefinition("javaDeprecatedField").getDeprecationReason());
	}


	@GraphQLDescription("The class description")
	public class TestDocumentationClass {
		@GraphQLDescription("The field description")
		String field;

		@GraphQLDescription("The method description")
		String getValue() {
			return "true";
		}
	}

	@Test
	public void testDocumentedAttributes() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(
				ImmutableList.<IGraphQLTypeMapper> builder().addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build());
		GraphQLObjectType objectType = (GraphQLObjectType) graphQLObjectMapper.getOutputType(new TypeToken<TestDocumentationClass>() {
		}.getType());

		assertNotNull(objectType.getFieldDefinition("field"));
		assertEquals("The class description",objectType.getDescription());
		assertEquals("The field description", objectType.getFieldDefinition("field").getDescription());
		assertEquals("The method description", objectType.getFieldDefinition("value").getDescription());
	}


	public abstract class AbstractBaseB {
		protected int field1 = 1;

		public abstract int getField1();
	}

	public class SubB extends AbstractBaseB {
		public int getField1() { return field1; }
	}

	@Test
	public void testAbstractMethods() {
		IGraphQLObjectMapper graphQLObjectMapper = newGraphQLObjectMapper(
				ImmutableList.<IGraphQLTypeMapper> builder().addAll(GraphQLSchemaBuilder.getDefaultTypeMappers()).build());
		GraphQLObjectType objectType = (GraphQLObjectType) graphQLObjectMapper.getOutputType(new TypeToken<SubB>() {
		}.getType());

		assertNotNull(objectType.getFieldDefinition("field1"));
		assertEquals("field1", objectType.getFieldDefinition("field1").getName());
		assertEquals(Scalars.GraphQLInt, objectType.getFieldDefinition("field1").getType());
	}
}
