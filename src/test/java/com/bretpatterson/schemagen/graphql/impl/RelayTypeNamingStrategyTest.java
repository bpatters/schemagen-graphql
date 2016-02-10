package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;
import com.bretpatterson.schemagen.graphql.relay.RelayConnection;
import com.google.common.reflect.TypeToken;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

/**
 * Created by bpatterson on 2/10/16.
 */
public class RelayTypeNamingStrategyTest {
	IGraphQLObjectMapper graphQLObjectMapper = mock(IGraphQLObjectMapper.class);

	private class Connection<T> {
		T field;
	}

	@Test
	public void testRelayNamingTypes() {
		ITypeNamingStrategy strategy = new RelayTypeNamingStrategy();


		given(graphQLObjectMapper.getClassFromType(eq(String.class))).willReturn(String.class);

		assertEquals("String", strategy.getTypeName(graphQLObjectMapper, String.class));

		given(graphQLObjectMapper.getClassFromType(eq(new TypeToken<RelayConnection<String>>(){}.getType()))).willReturn(RelayConnection.class);
		assertEquals("Relay_String_Connection", strategy.getTypeName(graphQLObjectMapper, new TypeToken<RelayConnection<String>>(){}.getType()));

		given(graphQLObjectMapper.getClassFromType(eq(new TypeToken<Connection<String>>(){}.getType()))).willReturn(Connection.class);
		assertEquals("String_Connection", strategy.getTypeName(graphQLObjectMapper, new TypeToken<Connection<String>>(){}.getType()));
	}
}
