package com.schemagen.graphql;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import com.schemagen.graphql.annotations.GraphParam;
import com.schemagen.graphql.datafetchers.CollectionConverterDataFetcher;
import com.schemagen.graphql.datafetchers.IMethodDataFetcher;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLArgument;
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
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Created by bpatterson on 1/19/16.
 */
public class GraphQLObjectMapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLObjectMapper.class);
	// class type mappers
	ImmutableMap<Type, IGraphQLTypeMapper> classTypeMappers;
	ImmutableList<IGraphQLTypeMapper> interfaceTypeMappers;
	Map<Type, GraphQLOutputType> outputTypeMap = Maps.newHashMap();
	Map<Type, GraphQLInputType> inputTypeMap = Maps.newHashMap();


	public GraphQLObjectMapper(Optional<List<IGraphQLTypeMapper>> objectMappers,
							   Optional<List<String>> typeMappingPackageNames) {

		ImmutableList.Builder<IGraphQLTypeMapper> interfaceTypeMappersBuilder = ImmutableList.builder();
		ImmutableMap.Builder<Type, IGraphQLTypeMapper> classTypeMappersBuilder = ImmutableMap.builder();
		if (objectMappers.isPresent()) {
			for (IGraphQLTypeMapper mapper : objectMappers.get()) {
				GraphQLTypeMapper mapperAnnotation = mapper.getClass().getAnnotation(GraphQLTypeMapper.class);
				classTypeMappersBuilder.put(mapperAnnotation.type(), mapper);
			}
		}
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
		interfaceTypeMappers = interfaceTypeMappersBuilder.build();
		classTypeMappers = classTypeMappersBuilder.build();
	}

	GraphQLOutputType getReturnType(Method method) {
		return getObjectType(method.getGenericReturnType());
	}

	List<GraphQLArgument> getMethodArguments(IMethodDataFetcher dataFetcher, Method method) throws Exception {

		Type returnType = method.getGenericReturnType();

		ImmutableList.Builder<GraphQLArgument> argumentBuilder = ImmutableList.builder();
		GraphQLArgument.Builder paramBuilder;
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		int index = 0;
		for (Type paramType : method.getGenericParameterTypes()) {
			GraphParam graphParam = findAnnotation(parameterAnnotations[index], GraphParam.class);

			if (graphParam == null) {
				LOGGER.error("Missing @GraphParam annotation on parameter index {} for method {}", index, method.getName());
				continue;
			}

			paramBuilder = GraphQLArgument.newArgument().name(graphParam.name()).type(getInputObjectType(paramType));
			dataFetcher.addParam(graphParam.name(), paramType, Optional.<Object>fromNullable(GraphParam.DEFAULT_NONE.equals(graphParam.defaultValue()) ? null : graphParam.defaultValue()));

			argumentBuilder.add(paramBuilder.build());
			index++;
		}

		return argumentBuilder.build();
	}

	public IGraphQLTypeMapper getCustomTypeMapper(Type type)   {
		if (classTypeMappers.containsKey(type))  {
			return classTypeMappers.get(type);
		}
		for (IGraphQLTypeMapper typeMapper : interfaceTypeMappers) {
			if (typeMapper.handlesType(type)) {
				return typeMapper;
			}
		}

		return null;
	}

	public GraphQLInputType getInputObjectType(Type type) {
		if (inputTypeMap.containsKey(type)) {
			return inputTypeMap.get(type);
		}
		// check typemapper
		IGraphQLTypeMapper typeMapper = getCustomTypeMapper(type);
		if (typeMapper != null) {
			inputTypeMap.put(type, typeMapper.getInputType(this, type));
	 	} else {
			GraphQLOutputType outputType = getObjectType(type);
			inputTypeMap.put(type, getInputObjectType(outputType));
		}

		return inputTypeMap.get(type);
	}

	private GraphQLInputType getInputObjectType(GraphQLOutputType outputType) {
		if (GraphQLInputType.class.isAssignableFrom(outputType.getClass())) {
			// outputs can be Long but inputs can only be int
			if (Scalars.GraphQLLong == outputType) {
				return Scalars.GraphQLInt;
			}
			return (GraphQLInputType) outputType;
		} else if (outputType instanceof GraphQLObjectType) {
			GraphQLObjectType objectType = (GraphQLObjectType) outputType;
			GraphQLInputObjectType.Builder rv = GraphQLInputObjectType.newInputObject().name(objectType.getName());

			for(GraphQLFieldDefinition field : objectType.getFieldDefinitions()) {
				rv.field(GraphQLInputObjectField.newInputObjectField().name(field.getName()).type(getInputObjectType(field.getType())).build());
			}

			return rv.build();
		} else{
			throw new RuntimeException(String.format("Unknown output type %s", outputType.toString()));
		}
	}

	private <T> T findAnnotation(Annotation[] annotations, Class<T> type) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == type) {
				return (T) annotation;
			}
		}
		return null;
	}

	private GraphQLScalarType getPrimitiveType(Class classType) {
		GraphQLScalarType rv = null;
		// native types
		if (Integer.class.isAssignableFrom(classType) ||
				classType.isAssignableFrom(int.class)) {
			rv = Scalars.GraphQLInt;
		} else if (Long.class.isAssignableFrom(classType) ||
				long.class.isAssignableFrom(classType)) {
			rv = Scalars.GraphQLLong;
		} else if (Float.class.isAssignableFrom(classType) ||
				float.class.isAssignableFrom(classType)) {
			rv = Scalars.GraphQLFloat;
		} else if (Double.class.isAssignableFrom(classType) ||
				double.class.isAssignableFrom(classType)) {
			rv = Scalars.GraphQLFloat;
		} else if (String.class.isAssignableFrom(classType)) {
			rv = Scalars.GraphQLString;
		} else if (Boolean.class.isAssignableFrom(classType) ||
				  boolean.class.isAssignableFrom(classType)) {
			rv = Scalars.GraphQLBoolean;
		}

		return rv;
	}

	public GraphQLOutputType getObjectType(Type type) {
		Class classType;

		if (outputTypeMap.containsKey(type)) {
			return outputTypeMap.get(type);
		}

		IGraphQLTypeMapper typeMapper = getCustomTypeMapper(type);
		if (typeMapper != null) {
			outputTypeMap.put(type, typeMapper.getOutputType(this, type));
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType)  type;
			Type rawType = parameterizedType.getRawType();
			Class rawClass = (Class) rawType;

			typeMapper = getCustomTypeMapper(rawClass);
			if (typeMapper != null) {
				outputTypeMap.put(type, typeMapper.getOutputType(this, type));
			} else if (Collection.class.isAssignableFrom(rawClass)) {
				outputTypeMap.put(type, new GraphQLList(getObjectType(parameterizedType.getActualTypeArguments()[0])));
			} else if (Map.class.isAssignableFrom(rawClass)) {
				Type[] paramTypes = parameterizedType.getActualTypeArguments();
				if (((Class) paramTypes[0]).isEnum()) {
					GraphQLObjectType.Builder glType = GraphQLObjectType.newObject().name(rawClass.getSimpleName());
					Class enumClassType = (Class) paramTypes[0];
					for (Object value : EnumSet.allOf(enumClassType)) {
						glType.field(GraphQLFieldDefinition.newFieldDefinition().name(value.toString()).type(getObjectType(paramTypes[1])).build());
					}

					outputTypeMap.put(type, glType.build());
				} else {
					LOGGER.error(String.format("%s type mapping not implemented", parameterizedType.toString()));
				}
			} else {
				// generic objects not supported
				throw new RuntimeException(String.format("Generic Object types not supported %s", type.toString()));
			}
		} else if (Class.class.isAssignableFrom(type.getClass())) {
			classType = (Class) type;
			GraphQLScalarType graphQLType = getPrimitiveType(classType);

			if (graphQLType != null) {
				outputTypeMap.put(type, graphQLType);
			}  else if (classType.isEnum()) {
				GraphQLEnumType.Builder enumType = GraphQLEnumType.newEnum().name(classType.getSimpleName());

				for (Object value : EnumSet.allOf(classType)) {
					enumType.value(value.toString(), value);
				}
				outputTypeMap.put(type, enumType.build());
			} else if (Array.class.isAssignableFrom(classType)) {
				outputTypeMap.put(type, new GraphQLList(getPrimitiveType(classType.getComponentType())));
			} else if (EnumSet.class.isAssignableFrom(classType)) {
				Class enumClassType = classType.getComponentType();
				GraphQLEnumType.Builder enumType = GraphQLEnumType.newEnum().name(enumClassType.getSimpleName());

				for (Object value : EnumSet.allOf(enumClassType)) {
					enumType.value(value.toString(), value);
				}
				outputTypeMap.put(type, new GraphQLList(enumType.build()));
			} else {
				buildObject(type, classType);
			}
		} else {
			LOGGER.error("Unable to handle type {}", type);
		}

		return outputTypeMap.get(type);
	}

	public GraphQLObjectType buildObject(Type type, Class classType) {
		// object types we create an object type and then recursively call ourselves to get the field types
		GraphQLObjectType.Builder glType = GraphQLObjectType.newObject().name(classType.getSimpleName());
		GraphQLTypeReference glTypeReference = new GraphQLTypeReference(classType.getSimpleName());

		outputTypeMap.put(type, glTypeReference);
		ImmutableList.Builder<GraphQLFieldDefinition> fieldBuilder = ImmutableList.builder();
		Class classItem = classType;
		do {
			for (Field field : classItem.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					LOGGER.info("Ignoring types {} static field {}  ", type, field);
				} else {
					LOGGER.info("Processing type {} field {}  ", type, field);
					GraphQLOutputType fieldType = getObjectType(field.getGenericType());
					if (fieldType == null) {
						LOGGER.info("type {} not supported so ignored field named {}", field.getGenericType(), field.getName());
					} else {
						GraphQLOutputType fieldObjectType = getObjectType(field.getGenericType());
						if (fieldObjectType != null) {
							GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition().name(field.getName()).type(fieldObjectType);
							if (fieldObjectType instanceof GraphQLList) {
								builder.dataFetcher(new CollectionConverterDataFetcher(field.getName()));
							}
							fieldBuilder.add(builder.build());
						}
					}
				}
			}
			classItem = classItem.getSuperclass();
		} while (classItem != null && classItem != Object.class);

		// overwrite the type reference
		outputTypeMap.put(type, glType.fields(fieldBuilder.build()).build());
		return (GraphQLObjectType) outputTypeMap.get(type);
	}

	public GraphQLOutputType addOutputType(Type type, GraphQLOutputType outputType) {
		outputTypeMap.put(type, outputType);
		return outputTypeMap.get(type);
	}

	public GraphQLInputType addInputType(Type type, GraphQLInputType outputType) {
		inputTypeMap.put(type, outputType);
		return inputTypeMap.get(type);
	}

}
