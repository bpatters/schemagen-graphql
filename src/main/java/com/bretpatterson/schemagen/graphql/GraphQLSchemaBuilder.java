package com.bretpatterson.schemagen.graphql;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLController;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.datafetchers.ITypeFactory;
import com.bretpatterson.schemagen.graphql.impl.GraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.relay.IRelayNodeFactory;
import com.bretpatterson.schemagen.graphql.relay.annotations.RelayNodeFactory;
import com.bretpatterson.schemagen.graphql.relay.impl.RelayDefaultNodeHandler;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.utils.AnnotationUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by bpatterson on 1/18/16.
 */
public class GraphQLSchemaBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLSchemaBuilder.class);

	private GraphQLSchema schema;
	private GraphQLObjectType.Builder rootQueryBuilder;
	private GraphQLObjectType.Builder rootMutationBuilder;
	private GraphQLObjectMapper graphQLObjectMapper;
	private List<Object> graphQLControllers = new LinkedList<>();
	private List<IGraphQLTypeMapper> typeMappers = new LinkedList<>();
	private Optional<ITypeNamingStrategy> typeNamingStrategy = Optional.absent();
	private RelayDefaultNodeHandler.Builder relayDefaultNodeHandler = RelayDefaultNodeHandler.builder();
	private List<Class> relayNodeTypes = Lists.newArrayList();
	private ITypeFactory typeFactory;
	ClassLoader classLoader;
	ClassPath classPath;

	private GraphQLSchemaBuilder() {
		this.rootQueryBuilder = GraphQLObjectType.newObject().name("Query").description("Root of Query Schema");
		this.rootMutationBuilder = GraphQLObjectType.newObject().name("Mutation").description("Root of Mutation Schema");
		try {
			classLoader = getClass().getClassLoader();
			classPath = ClassPath.from(classLoader);
		} catch (IOException ex) {
			Throwables.propagate(ex);
		}
	}

	public static GraphQLSchemaBuilder newBuilder() {
		return new GraphQLSchemaBuilder();
	}

	/**
	 * Register objects to be scanned as containing top level query fields
	 * 
	 * @param queryHandlers
	 * @return
	 */
	public GraphQLSchemaBuilder registerGraphQLContollerObjects(List<Object> queryHandlers) {
		this.graphQLControllers.addAll(queryHandlers);

		return this;
	}

	/**
	 * Register custom type mapper instances that can be used to convert types to GraphQLTypes. These type mappers will have precidence over
	 * any other types discovered.
	 * 
	 * @param typeMappers
	 * @return
	 */
	public GraphQLSchemaBuilder registerTypeMappers(List<IGraphQLTypeMapper> typeMappers) {
		this.typeMappers.addAll(typeMappers);

		return this;
	}

	/**
	 * This typeFactory will be used to convert between GraphQL value types to Java native value types when passing parameters to
	 * DataFetchers. This object will be assigned to all datafetchers created for GraphQL queries and mutations.
	 * 
	 * @param typeFactory
	 * @return
	 */
	public GraphQLSchemaBuilder registerTypeFactory(ITypeFactory typeFactory) {
		this.typeFactory = typeFactory;

		return this;
	}

	/**
	 * Register a new Type Naming strategy to be used in place of the default SimpleTypeNamingStrategy See {@link ITypeNamingStrategy}
	 * 
	 * @param strategy
	 * @return
	 */
	public GraphQLSchemaBuilder registerTypeNamingStrategy(ITypeNamingStrategy strategy) {
		typeNamingStrategy = Optional.fromNullable(strategy);

		return this;
	}

	/**
	 * Register your Relay Node Factory instances for handling mapping from node id's to objects.
	 * 
	 * @see <a href="https://facebook.github.io/relay/graphql/objectidentification.htm">Graph QL Relay Object Identification</a>
	 *
	 * @param nodeFactories the factories to register
	 * @return
	 */
	public GraphQLSchemaBuilder registerNodeFactories(List<IRelayNodeFactory> nodeFactories) {
		for (IRelayNodeFactory nodeFactory : nodeFactories) {
			RelayNodeFactory factoryAnnotation = nodeFactory.getClass().getAnnotation(RelayNodeFactory.class);
			relayDefaultNodeHandler.registerFactory(nodeFactory);
			relayNodeTypes.addAll(Lists.newArrayList(factoryAnnotation.types()));
		}

		return this;
	}

	@VisibleForTesting
	public static List<IGraphQLTypeMapper> getDefaultTypeMappers() {
		// install all of the default type mappers we include in our packages
		ImmutableList.Builder<IGraphQLTypeMapper> builder = ImmutableList.builder();
		try {
			Set<Class> defaultTypeMappers = AnnotationUtils.getClassesWithAnnotation(GraphQLTypeMapper.class, IGraphQLTypeMapper.class.getPackage().getName())
					.keySet();
			for (Class typeMapper : defaultTypeMappers) {
				builder.add((IGraphQLTypeMapper) typeMapper.newInstance());
			}

		} catch (InstantiationException | IllegalAccessException ex) {
			Throwables.propagate(ex);
		}
		return builder.build();
	}

	public GraphQLSchema build() {

		this.typeMappers.addAll(0, getDefaultTypeMappers());
		this.setGraphQLObjectMapper(new GraphQLObjectMapper(typeFactory, typeMappers, typeNamingStrategy, relayNodeTypes));
		// add our node handler first, as it's used by relay and we want people to be able to override it if they really want to
		graphQLControllers.add(0, relayDefaultNodeHandler.build());
		List<GraphQLFieldDefinition> mutations = null;
		List<GraphQLFieldDefinition> queries = null;

		GraphQLFieldDefinition.Builder viewerField = GraphQLFieldDefinition.newFieldDefinition().name("Views").staticValue(new Object());
		GraphQLObjectType.Builder viewerObject = GraphQLObjectType.newObject().name("Views");
		GraphQLFieldDefinition.Builder mutatorField = GraphQLFieldDefinition.newFieldDefinition().name("Mutations").staticValue(new Object());
		GraphQLObjectType.Builder mutatorObject = GraphQLObjectType.newObject().name("Mutations");


		for (Object queryHandler : graphQLControllers) {
			GraphQLController graphQLController = queryHandler.getClass().getAnnotation(GraphQLController.class);
			try {
				viewerObject.fields(graphQLController.queryFactory().newInstance().newMethodQueriesForObject(getGraphQLObjectMapper(), queryHandler));
				mutatorObject.fields(graphQLController.mutationFactory().newInstance().newMethodMutationsForObject(getGraphQLObjectMapper(), queryHandler));
			} catch (InstantiationException | IllegalAccessException ex) {
				LOGGER.warn("Failed to load {}.  This is probably because of an unsatisfied runtime dependency.", ex);
			}
		}
		GraphQLSchema.Builder builder = GraphQLSchema.newSchema();

		viewerField.type(viewerObject.build());
		rootQueryBuilder.field(viewerField.build());
		builder.query(rootQueryBuilder.build());

		mutatorField.type(mutatorObject.build());
		rootMutationBuilder.field(mutatorField.build());
		builder.mutation(rootMutationBuilder.build());
		schema = builder.build(graphQLObjectMapper.getInputTypes());

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
