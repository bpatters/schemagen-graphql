package com.bretpatterson.schemagen.graphql.typemappers.relay;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.relay.ConnectionCursor;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;

/**
 * Specification Compliant ConnectionCursor mapper
 *
 * @see <a href="https://facebook.github.io/relay/graphql/connections.htm">Connection Specification</a>
 */
@GraphQLTypeMapper(type = ConnectionCursor.class)
public class ConnectionCursorMapper implements IGraphQLTypeMapper {

    @Override
    public boolean handlesType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
        return ConnectionCursor.class.isAssignableFrom(graphQLObjectMapper.getClassFromType(type));
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
