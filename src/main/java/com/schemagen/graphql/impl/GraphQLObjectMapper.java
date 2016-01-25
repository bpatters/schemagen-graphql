package com.schemagen.graphql.impl;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import com.schemagen.graphql.IGraphQLObjectMapper;
import com.schemagen.graphql.IGraphQLTypeCache;
import com.schemagen.graphql.IQueryFactory;
import com.schemagen.graphql.annotations.GraphQLQueryable;
import com.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.schemagen.graphql.datafetchers.CollectionConverterDataFetcher;
import com.schemagen.graphql.datafetchers.ITypeFactory;
import com.schemagen.graphql.exceptions.NotMappableException;
import com.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
public class GraphQLObjectMapper implements IGraphQLObjectMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLObjectMapper.class);
	// class type mappers
	ImmutableMap<Type, IGraphQLTypeMapper> classTypeMappers;
	ImmutableList<IGraphQLTypeMapper> interfaceTypeMappers;
	IGraphQLTypeCache<GraphQLOutputType> outputTypeCache = new GraphQLTypeCache<>();
	IGraphQLTypeCache<GraphQLInputType> inputTypeCache = new GraphQLTypeCache<>();
	ITypeFactory objectMapper;

	public GraphQLObjectMapper(ITypeFactory objectMapper, Optional<List<IGraphQLTypeMapper>> graphQLTypeMappers, Optional<List<String>> typeMappingPackageNames) {

		this.objectMapper = objectMapper;

		ImmutableList.Builder<IGraphQLTypeMapper> interfaceTypeMappersBuilder = ImmutableList.builder();
		Map<Type, IGraphQLTypeMapper> classTypeMappersBuilder = Maps.newHashMap();

		if (typeMappingPackageNames.isPresent()) {
			try {

				ClassLoader classLoader = getClass().getClassLoader();
				ClassPath classPath = ClassPath.from(classLoader);
				for (String packageName : typeMappingPackageNames.get()) {
					ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClassesRecursive(packageName);
					for (ClassPath.ClassInfo info : classes) {
						try {
							Class<?> type = info.load();
							GraphQLTypeMapper graphQLTypeMapper = type.getAnnotation(GraphQLTypeMapper.class);
							if (graphQLTypeMapper != null) {
								try {
									if (graphQLTypeMapper.type().isInterface()) {
										interfaceTypeMappersBuilder.add((IGraphQLTypeMapper) type.newInstance());
									} else {
										classTypeMappersBuilder.put(graphQLTypeMapper.type(), (IGraphQLTypeMapper) type.newInstance());
									}
								} catch (Exception ex) {
									LOGGER.error(String.format("Unexpected Exception instantiation type %s ", type.toString()), ex);
								}
							}
						} catch (NoClassDefFoundError ex) {
							LOGGER.warn("Failed to load {}.  This is probably because of an unsatisfied runtime dependency.", info);
						}
					}
				}
			} catch (IOException ex) {
				LOGGER.error("Unexpected exception.", ex);
			}
		}
		if (graphQLTypeMappers.isPresent()) {
			for (IGraphQLTypeMapper mapper : graphQLTypeMappers.get()) {
				GraphQLTypeMapper mapperAnnotation = mapper.getClass().getAnnotation(GraphQLTypeMapper.class);
				classTypeMappersBuilder.put(mapperAnnotation.type(), mapper);
			}
		}
		interfaceTypeMappers = interfaceTypeMappersBuilder.build();
		classTypeMappers = ImmutableMap.copyOf(classTypeMappersBuilder);
	}

	private Optional<GraphQLFieldDefinition> getFieldType(Type type, Field field) {
		if (Modifier.isStatic(field.getModifiers())) {
			LOGGER.info("Ignoring types {} static field {}  ", type, field);
		} if ("this$0".equals(field.getName())) {
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
			} catch(NotMappableException ex) {
				LOGGER.info("types field type {} not supported so ignored field named {}", type, field.getGenericType(), field.getName());
			}
		}
		return Optional.absent();
	}

	private Optional<IGraphQLTypeMapper> getCustomTypeMapper(Type type) {
		if (classTypeMappers.containsKey(type)) {
			return Optional.fromNullable(classTypeMappers.get(type));
		}
		for (IGraphQLTypeMapper typeMapper : interfaceTypeMappers) {
			if (typeMapper.handlesType(type)) {
				return Optional.of(typeMapper);
			}
		}

		return Optional.absent();
	}

	@Override
	public GraphQLInputType getInputType(Type type) {
		if (inputTypeCache.containsKey(type)) {
			return inputTypeCache.get(type);
		}
		// check typemapper
		Optional<IGraphQLTypeMapper> typeMapper = getCustomTypeMapper(type);
		if (typeMapper.isPresent()) {
			inputTypeCache.put(type, typeMapper.get().getInputType(this, type));
		} else {
			GraphQLOutputType outputType = getOutputType(type);
			inputTypeCache.put(type, getInputType(outputType));
		}

		return inputTypeCache.get(type);
	}

	@Override
	public GraphQLOutputType getOutputType(Type type) {
		Class classType;

		if (outputTypeCache.containsKey(type)) {
			return outputTypeCache.get(type);
		}

		Optional<IGraphQLTypeMapper> typeMapper = getCustomTypeMapper(type);
		if (typeMapper.isPresent()) {
			outputTypeCache.put(type, typeMapper.get().getOutputType(this, type));
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			Class rawClass = (Class) rawType;

			typeMapper = getCustomTypeMapper(rawClass);
			if (typeMapper.isPresent()) {
				outputTypeCache.put(type, typeMapper.get().getOutputType(this, type));
			} else {
				// generic objects not supported
				throw new NotMappableException(String.format("Generic Object %s types requires a custom type mapper.", type.toString()));
			}
		} else {
			classType = (Class) type;
			Optional<GraphQLScalarType> graphQLType = getIfPrimitiveType(classType);

			if (graphQLType.isPresent()) {
				outputTypeCache.put(type, graphQLType.get());
			} else if (classType.isEnum()) {
				GraphQLEnumType.Builder enumType = GraphQLEnumType.newEnum().name(classType.getSimpleName());
				for (Object value : EnumSet.allOf(classType)) {
					enumType.value(value.toString(), value);
				}
				outputTypeCache.put(type, enumType.build());
			} else {
				buildObject(type, classType);
			}
		}

		return outputTypeCache.get(type);
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
			GraphQLObjectType.Builder glType = GraphQLObjectType.newObject().name(classType.getSimpleName());
			GraphQLTypeReference glTypeReference = new GraphQLTypeReference(classType.getSimpleName());
			ImmutableList.Builder<GraphQLFieldDefinition> fields = ImmutableList.builder();

			outputTypeCache.put(type, glTypeReference);
			Class classItem = classType;
			Optional<GraphQLQueryable> graphQLQueryable = Optional.fromNullable((GraphQLQueryable) classItem.getAnnotation(GraphQLQueryable.class));
			Optional<IQueryFactory> queryFactory = Optional.absent();
			Object objectInstance = null;

			// if it's queryable create a factory and instance of the object that we will execute queries upon
			if (graphQLQueryable.isPresent()) {
				queryFactory=Optional.of(graphQLQueryable.get().queryFactory().newInstance());
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

			outputTypeCache.put(type, glType.build());
		} catch (InstantiationException | IllegalAccessException ex) {
			LOGGER.error("Unable to instantiate query factory for type class {}", classType.getName(), ex);
			Throwables.propagate(ex);
		}

		return (GraphQLObjectType) outputTypeCache.get(type);
	}

}
