package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.IGraphQLTypeCache;
import com.bretpatterson.schemagen.graphql.IQueryFactory;
import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLController;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.datafetchers.CollectionConverterDataFetcher;
import com.bretpatterson.schemagen.graphql.datafetchers.ITypeFactory;
import com.bretpatterson.schemagen.graphql.exceptions.NotMappableException;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import graphql.Scalars;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import relay.INode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Created by bpatterson on 1/19/16.
 */
public class GraphQLObjectMapper implements IGraphQLObjectMapper, TypeResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLObjectMapper.class);
	// class type mappers
	private ImmutableMap<Type, IGraphQLTypeMapper> classTypeMappers;
	private ImmutableList<IGraphQLTypeMapper> interfaceTypeMappers;
	private IGraphQLTypeCache<GraphQLOutputType> outputTypeCache = new GraphQLTypeCache<>();
	private IGraphQLTypeCache<GraphQLInputType> inputTypeCache = new GraphQLTypeCache<>();
	private ITypeFactory objectMapper;
	private ITypeNamingStrategy typeNamingStrategy = new SimpleTypeNamingStrategy();
	private List<Class> relayNodeTypes;

	public GraphQLObjectMapper(ITypeFactory objectMapper, List<IGraphQLTypeMapper> graphQLTypeMappers, Optional<ITypeNamingStrategy> typeNamingStrategy,
			List<Class> relayNodeTypes) {

		this.objectMapper = objectMapper;
		this.relayNodeTypes = relayNodeTypes;

		if (typeNamingStrategy.isPresent()) {
			this.typeNamingStrategy = typeNamingStrategy.get();
		}

		ImmutableList.Builder<IGraphQLTypeMapper> interfaceTypeMappersBuilder = ImmutableList.builder();
		Map<Type, IGraphQLTypeMapper> classTypeMappersBuilder = Maps.newHashMap();

		for (IGraphQLTypeMapper mapper : graphQLTypeMappers) {
			GraphQLTypeMapper mapperAnnotation = mapper.getClass().getAnnotation(GraphQLTypeMapper.class);
			if (mapperAnnotation.type().isInterface()) {
				interfaceTypeMappersBuilder.add(mapper);
			} else{
				classTypeMappersBuilder.put(mapperAnnotation.type(), mapper);
			}
		}
		this.interfaceTypeMappers = interfaceTypeMappersBuilder.build();
		this.classTypeMappers = ImmutableMap.copyOf(classTypeMappersBuilder);

		// store the Node type interface mapping
		this.getOutputTypeCache().put(INode.class,
				GraphQLInterfaceType.newInterface()
						.name(getTypeNamingStrategy().getTypeName(INode.class))
						.typeResolver(this)
						.field(GraphQLFieldDefinition.newFieldDefinition().name("id").type(Scalars.GraphQLString).build())
						.build());
	}

	private Optional<GraphQLFieldDefinition> getFieldType(Type type, Field field) {
		if (Modifier.isStatic(field.getModifiers())) {
			LOGGER.info("Ignoring types {} static field {}  ", type, field);
		}
		if ("this$0".equals(field.getName())) {
			// this is a dirty hack but we don't want to expose the parent pointer of inner classes...
			return Optional.absent();
		} else {
			LOGGER.info("Processing types {} field {}  ", type, field);
			try {
				GraphQLOutputType fieldType = getOutputType(field.getGenericType());
				GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition().name(field.getName()).type(fieldType);
				if (fieldType instanceof GraphQLList) {
					builder.dataFetcher(new CollectionConverterDataFetcher(field.getName()));
				}
				return Optional.of(builder.build());
			} catch (NotMappableException ex) {
				LOGGER.info("types field type {} not supported so ignored field named {}", type, field.getGenericType(), field.getName());
			}
		}
		return Optional.absent();
	}

	private Optional<IGraphQLTypeMapper> getCustomTypeMapper(Type type) {
		if (getClassTypeMappers().containsKey(type)) {
			return Optional.fromNullable(getClassTypeMappers().get(type));
		}
		for (IGraphQLTypeMapper typeMapper : getInterfaceTypeMappers()) {
			if (typeMapper.handlesType(type)) {
				return Optional.of(typeMapper);
			}
		}

		return Optional.absent();
	}

	@Override
	public GraphQLInputType getInputType(Type type) {
		if (getInputTypeCache().containsKey(type)) {
			return getInputTypeCache().get(type);
		}
		// check typemapper
		Optional<IGraphQLTypeMapper> typeMapper = getCustomTypeMapper(type);
		if (typeMapper.isPresent()) {
			getInputTypeCache().put(type, typeMapper.get().getInputType(this, type));
		} else {
			GraphQLOutputType outputType = getOutputType(type);
			getInputTypeCache().put(type, getInputType(outputType));
		}

		return getInputTypeCache().get(type);
	}

	@Override
	public GraphQLOutputType getOutputType(Type type) {
		Class classType;

		if (getOutputTypeCache().containsKey(type)) {
			return getOutputTypeCache().get(type);
		}

		Optional<IGraphQLTypeMapper> typeMapper = getCustomTypeMapper(type);
		if (typeMapper.isPresent()) {
			getOutputTypeCache().put(type, typeMapper.get().getOutputType(this, type));
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			Class rawClass = (Class) rawType;

			typeMapper = getCustomTypeMapper(rawClass);
			if (typeMapper.isPresent()) {
				getOutputTypeCache().put(type, typeMapper.get().getOutputType(this, type));
			} else {
				// generic objects not supported
				throw new NotMappableException(String.format("Generic Object %s types requires a custom type mapper.", type.toString()));
			}
		} else {
			classType = (Class) type;
			Optional<GraphQLScalarType> graphQLType = getIfPrimitiveType(classType);

			if (graphQLType.isPresent()) {
				getOutputTypeCache().put(type, graphQLType.get());
			} else if (classType.isEnum()) {
				GraphQLEnumType.Builder enumType = GraphQLEnumType.newEnum().name(typeNamingStrategy.getTypeName(classType));
				for (Object value : EnumSet.allOf(classType)) {
					enumType.value(value.toString(), value);
				}
				getOutputTypeCache().put(type, enumType.build());
			} else {
				buildObject(type, classType);
			}
		}

		return getOutputTypeCache().get(type);
	}

	@Override
	public IGraphQLTypeCache<GraphQLInputType> getInputTypeCache() {
		return inputTypeCache;
	}

	@Override
	public IGraphQLTypeCache<GraphQLOutputType> getOutputTypeCache() {
		return outputTypeCache;
	}

	public ITypeFactory getObjectMapper() {
		return this.objectMapper;
	}

	private GraphQLInputType getInputType(GraphQLOutputType outputType) {
		if (GraphQLInputType.class.isAssignableFrom(outputType.getClass())) {
			// outputs can be Long but inputs can only be int
			if (Scalars.GraphQLLong == outputType) {
				return Scalars.GraphQLInt;
			}
			return (GraphQLInputType) outputType;
		} else if (outputType instanceof GraphQLObjectType) {
			GraphQLObjectType objectType = (GraphQLObjectType) outputType;
			GraphQLInputObjectType.Builder rv = GraphQLInputObjectType.newInputObject().name(objectType.getName());

			for (GraphQLFieldDefinition field : objectType.getFieldDefinitions()) {
				rv.field(GraphQLInputObjectField.newInputObjectField().name(field.getName()).type(getInputType(field.getType())).build());
			}

			return rv.build();
		} else {
			throw new RuntimeException(String.format("Unknown output type %s", outputType.toString()));
		}
	}

	private Optional<GraphQLScalarType> getIfPrimitiveType(Class classType) {
		GraphQLScalarType rv = null;
		// native types
		if (Integer.class.isAssignableFrom(classType) || classType.isAssignableFrom(int.class)) {
			rv = Scalars.GraphQLInt;
		} else if (Long.class.isAssignableFrom(classType) || long.class.isAssignableFrom(classType)) {
			rv = Scalars.GraphQLLong;
		} else if (Float.class.isAssignableFrom(classType) || float.class.isAssignableFrom(classType)) {
			rv = Scalars.GraphQLFloat;
		} else if (Double.class.isAssignableFrom(classType) || double.class.isAssignableFrom(classType)) {
			rv = Scalars.GraphQLFloat;
		} else if (String.class.isAssignableFrom(classType)) {
			rv = Scalars.GraphQLString;
		} else if (Boolean.class.isAssignableFrom(classType) || boolean.class.isAssignableFrom(classType)) {
			rv = Scalars.GraphQLBoolean;
		}

		return Optional.fromNullable(rv);
	}

	private GraphQLObjectType buildObject(Type type, Class classType) {
		try {
			// object types we create an object type and then recursively call ourselves to get the field types
			GraphQLObjectType.Builder glType = GraphQLObjectType.newObject().name(typeNamingStrategy.getTypeName(classType));
			GraphQLTypeReference glTypeReference = new GraphQLTypeReference(typeNamingStrategy.getTypeName(classType));
			ImmutableList.Builder<GraphQLFieldDefinition> fields = ImmutableList.builder();

			getOutputTypeCache().put(type, glTypeReference);
			Class classItem = classType;
			Optional<GraphQLController> graphQLQueryable = Optional.fromNullable((GraphQLController) classItem.getAnnotation(GraphQLController.class));
			Optional<IQueryFactory> queryFactory = Optional.absent();
			Object objectInstance = null;

			// if it's queryable create a factory and instance of the object that we will execute queries upon
			if (graphQLQueryable.isPresent()) {
				queryFactory = Optional.of(graphQLQueryable.get().queryFactory().newInstance());
				objectInstance = classItem.newInstance();
			}

			do {
				for (Field field : classItem.getDeclaredFields()) {
					Optional<GraphQLFieldDefinition> fieldDefinitionOptional = getFieldType(type, field);
					if (fieldDefinitionOptional.isPresent()) {
						fields.add(fieldDefinitionOptional.get());
					}
				}
				classItem = classItem.getSuperclass();

				if (queryFactory.isPresent()) {
					fields.addAll(queryFactory.get().newMethodQueriesForObject(this, objectInstance));
				}
			} while (classItem != null && classItem != Object.class);
			glType.fields(fields.build());

			// for classes that implement Node we need to declare them of type interface
			if (INode.class.isAssignableFrom(classType)) {
				glType.withInterface((GraphQLInterfaceType) getOutputTypeCache().get(INode.class));
			}
			getOutputTypeCache().put(type, glType.build());
		} catch (InstantiationException | IllegalAccessException ex) {
			LOGGER.error("Unable to instantiate query factory for type class {}", classType.getName(), ex);
			Throwables.propagate(ex);
		}

		return (GraphQLObjectType) getOutputTypeCache().get(type);
	}

	@Override
	public ITypeNamingStrategy getTypeNamingStrategy() {
		return typeNamingStrategy;
	}

	public void setTypeNamingStrategy(ITypeNamingStrategy typeNamingStrategy) {
		this.typeNamingStrategy = typeNamingStrategy;
	}

	@VisibleForTesting
	ImmutableMap<Type, IGraphQLTypeMapper> getClassTypeMappers() {
		return classTypeMappers;
	}

	@VisibleForTesting
	ImmutableList<IGraphQLTypeMapper> getInterfaceTypeMappers() {
		return interfaceTypeMappers;
	}

	public List<Class> getRelayNodeTypes() {
		return relayNodeTypes;
	}

	@Override
	public GraphQLObjectType getType(Object object) {
		return (GraphQLObjectType) getOutputType(object.getClass());
	}
}
