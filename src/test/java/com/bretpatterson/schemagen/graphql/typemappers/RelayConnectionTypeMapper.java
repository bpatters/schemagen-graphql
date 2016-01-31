package com.bretpatterson.schemagen.graphql.typemappers;

/**
 * Created by bpatterson on 1/31/16.
 */
//@GraphQLTypeMapper(type = RelayConnection.class)
public class RelayConnectionTypeMapper {//implements IGraphQLTypeMapper {
/*
	@Override
	public boolean handlesType(Type type) {
		Class typeClass = (Class) (type instanceof ParameterizedType ? ((ParameterizedType) type).getRawType() : type);
		return RelayConnection.class.isAssignableFrom(typeClass);
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		ParameterizedType parameterizedType = (ParameterizedType) type;
		Class rawClass = (Class)parameterizedType.getRawType();
		final Type pType = ((ParameterizedType) type).getActualTypeArguments()[0];
		GraphQLObjectType.Builder rv =  GraphQLObjectType.newObject().name(graphQLObjectMapper.getTypeNamingStrategy().getTypeName(rawClass));
		GraphQLList edges = new GraphQLList(graphQLObjectMapper.getOutputType(new ParameterizedType() {
																				  @Override
																				  public Type[] getActualTypeArguments() {
																					  Type[] types =  new Type[1];
																					  types[0]= pType;
																					  return types;
																				  }

																				  @Override
																				  public Type getRawType() {
																					  return Edge.class;
																				  }

																				  @Override
																				  public Type getOwnerType() {
																					  return null;
																				  }
																			  }
				rv.field(GraphQLFieldDefinition.newFieldDefinition().name("edges")
						.type(edges).build());
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		return null;
	}
	*/
}
