package com.schemagen.graphql;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.schemagen.graphql.annotations.GraphQuery;
import com.schemagen.graphql.annotations.GraphQuerySupported;
import com.schemagen.graphql.datafetchers.IMethodDataFetcher;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;
import graphql.GraphQL;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bpatterson on 1/18/16.
 */
public class GraphQLSchemaManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLSchemaManager.class);

	GraphQLSchema schema;
	GraphQLObjectType.Builder rootObjectBuilder;
	private GraphQLObjectMapper graphQLObjectMapper;
	Optional<List<Object>> queryHandlers = Optional.absent();
	Optional<List<String>> queryHandlerPackages = Optional.absent();
	Optional<List<IGraphQLTypeMapper>> typeMappers = Optional.absent();
	List<String> typeMapperPackages = new ArrayList<String>();

	public GraphQLSchemaManager() {
		typeMapperPackages.add("com.schemagen.graphql.mappers");
		this.rootObjectBuilder = GraphQLObjectType.newObject().name("Query").description("Root of Schema");
	}

	public void addQueryHandlers(List<Object> queryHandlers) {
		this.queryHandlers = Optional.of(queryHandlers);
	}
	public void addQueryHandlerPackages(List<String> queryHandlerPackages) {
		this.queryHandlerPackages = Optional.of(queryHandlerPackages);
	}
	public void addTypeMappers(List<IGraphQLTypeMapper> typeMappers) {
		this.typeMappers = Optional.of(typeMappers);

	}
	public void addTypeMapperPackages(List<String> typeMapperPackages) {
		this.typeMapperPackages.addAll(typeMapperPackages);

	}

	public void build() {
		this.graphQLObjectMapper = new GraphQLObjectMapper(typeMappers, Optional.of(typeMapperPackages));
		if (queryHandlerPackages.isPresent()) {
			try {
				ClassLoader classLoader = getClass().getClassLoader();
				ClassPath classPath = ClassPath.from(classLoader);
				for (String packageName : queryHandlerPackages.get()) {
					ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClassesRecursive(packageName);
					for (ClassPath.ClassInfo info : classes) {
						try {
							Class<?> type = info.load();
							GraphQuerySupported graphQuerySupported = type.getAnnotation(GraphQuerySupported.class);
							if (graphQuerySupported != null) {
								LOGGER.info("Identified {} as GraphQuerySupported type.", type.getCanonicalName());
								addQueries(type.newInstance(), type);
							}
						} catch (NoClassDefFoundError ex) {
							LOGGER.warn("Failed to load {}.  This is probably because of an unsatisfied runtime dependency.", info);
						}
					}
				}
			} catch (Exception ex) {
				Throwables.propagate(ex);
			}
		}
		if (queryHandlers.isPresent()) {
			for (Object queryHandler : queryHandlers.get()) {
				addQueries(queryHandler, queryHandler.getClass());
			}
		}

		schema = GraphQLSchema.newSchema().query(rootObjectBuilder.build()).build();
	}

	public GraphQL query() {
		return new GraphQL(schema);
	}

	private void addQueries(Object sourceObject, Class type) {
		for (Method method : type.getDeclaredMethods()) {
			try {
				GraphQuery graphQueryAnnotation = method.getAnnotation(GraphQuery.class);
				if (graphQueryAnnotation != null) {
					IMethodDataFetcher dataFetcher = graphQueryAnnotation.dataFetcher().newInstance();
					dataFetcher.setSourceObject(sourceObject);
					dataFetcher.setMethod(method);
					dataFetcher.setFieldName(graphQueryAnnotation.name());

					GraphQLFieldDefinition.Builder newField = GraphQLFieldDefinition.newFieldDefinition()
							.name(graphQueryAnnotation.name())
							.type(graphQLObjectMapper.getReturnType(method));
					newField.dataFetcher(dataFetcher);
					List<GraphQLArgument> arguments = graphQLObjectMapper.getMethodArguments(dataFetcher, method);

					newField.argument(arguments);

					getRootObjectBuilder().field(newField.build());
				}
			} catch (Exception ex) {
				Throwables.propagate(ex);
			}
		}

	}

	public GraphQLObjectType.Builder getRootObjectBuilder() {
		return rootObjectBuilder;
	}
}
