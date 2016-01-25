package com.schemagen.graphql;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.schemagen.graphql.annotations.GraphQLQueryable;
import com.schemagen.graphql.datafetchers.ITypeFactory;
import com.schemagen.graphql.impl.GraphQLObjectMapper;
import com.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bpatterson on 1/18/16.
 */
public class GraphQLSchemaBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLSchemaBuilder.class);

	private GraphQLSchema schema;
	private GraphQLObjectType.Builder rootObjectBuilder;
	private GraphQLObjectMapper graphQLObjectMapper;
	private Optional<List<Object>> queryHandlers = Optional.absent();
	private Optional<List<String>> queryHandlerPackages = Optional.absent();
	private Optional<List<IGraphQLTypeMapper>> typeMappers = Optional.absent();
	private List<String> typeMapperPackages = new ArrayList<String>();
	private ITypeFactory typeFactory;

	private GraphQLSchemaBuilder() {
		typeMapperPackages.add("com.schemagen.graphql.typemappers");
		this.rootObjectBuilder = GraphQLObjectType.newObject().name("Query").description("Root of Schema");
	}

	public static GraphQLSchemaBuilder newBuilder() {
		return new GraphQLSchemaBuilder();
	}

	/**
	 * Register objects to be scanned as containing top level query fields
	 * @param queryHandlers
	 * @return
	 */
	public GraphQLSchemaBuilder registerQueryHandlers(List<Object> queryHandlers) {
		this.queryHandlers = Optional.of(queryHandlers);

		return this;
	}

	/**
	 * Register packages to be scanned for annotated classes to build top level queries from.
	 * @param queryHandlerPackages
	 * @return
	 */
	public GraphQLSchemaBuilder registerQueryHandlerPackages(List<String> queryHandlerPackages) {
		this.queryHandlerPackages = Optional.of(queryHandlerPackages);

		return this;
	}

	/**
	 * Register custom type mapper instances that can be used to convert types to GraphQLTypes.
	 * These type mappers will have precidence over any other types discovered.
	 * @param typeMappers
	 * @return
	 */
	public GraphQLSchemaBuilder registerTypeMappers(List<IGraphQLTypeMapper> typeMappers) {
		this.typeMappers = Optional.of(typeMappers);

		return this;
	}

	/**
	 * Registers a set of packages to be scanned for custom type mappers. These will have precidence over
	 * the built in type converters for the same types.
	 * @param typeMapperPackages
	 * @return
	 */
	public GraphQLSchemaBuilder registerTypeMapperPackages(List<String> typeMapperPackages) {
		this.typeMapperPackages.addAll(typeMapperPackages);

		return this;
	}

	/**
	 * This objectmapper will be used to convert between GraphQL value types to Java native value types when
	 * passing parameters to DataFetchers. This object will be assigned to all datafetchers created for
	 * GraphQL queries
	 * @param objectMapper
	 * @return
	 */
	public GraphQLSchemaBuilder registerTypeFactory(ITypeFactory objectMapper) {
		this.typeFactory = objectMapper;

		return this;
	}

	public GraphQLSchema build() {
		this.setGraphQLObjectMapper(new GraphQLObjectMapper(typeFactory, typeMappers, Optional.of(typeMapperPackages)));
		if (queryHandlerPackages.isPresent()) {
			try {
				ClassLoader classLoader = getClass().getClassLoader();
				ClassPath classPath = ClassPath.from(classLoader);
				for (String packageName : queryHandlerPackages.get()) {
					ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClassesRecursive(packageName);
					for (ClassPath.ClassInfo info : classes) {
						try {
							Class<?> type = info.load();
							GraphQLQueryable graphQLQueryable = type.getAnnotation(GraphQLQueryable.class);
							if (graphQLQueryable != null) {
								LOGGER.info("Identified {} as GraphQuerySupported type.", type.getCanonicalName());
								rootObjectBuilder.fields(
										graphQLQueryable.queryFactory().newInstance().newMethodQueriesForObject(getGraphQLObjectMapper(), type.newInstance()));
							}
						} catch (NoClassDefFoundError ex) {
							LOGGER.warn("Failed to load {}.  This is probably because of an unsatisfied runtime dependency.", ex);
						}
					}
				}
			} catch (Exception ex) {
				Throwables.propagate(ex);
			}
		}
		if (queryHandlers.isPresent()) {
			for (Object queryHandler : queryHandlers.get()) {
				GraphQLQueryable graphQLQueryable = queryHandler.getClass().getAnnotation(GraphQLQueryable.class);
				try {
					rootObjectBuilder.fields(graphQLQueryable.queryFactory().newInstance().newMethodQueriesForObject(getGraphQLObjectMapper(), queryHandler));
				} catch (InstantiationException | IllegalAccessException ex) {
					LOGGER.warn("Failed to load {}.  This is probably because of an unsatisfied runtime dependency.", ex);
				}
			}
		}

		schema = GraphQLSchema.newSchema().query(rootObjectBuilder.build()).build();

		return schema;
	}

	@VisibleForTesting
	GraphQLObjectMapper getGraphQLObjectMapper() {
		return graphQLObjectMapper;
	}

	@VisibleForTesting
	void setGraphQLObjectMapper(GraphQLObjectMapper graphQLObjectMapper) {
		this.graphQLObjectMapper = graphQLObjectMapper;
	}
}
