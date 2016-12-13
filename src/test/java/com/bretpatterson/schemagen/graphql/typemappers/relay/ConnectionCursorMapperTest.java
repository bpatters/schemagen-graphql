package com.bretpatterson.schemagen.graphql.typemappers.relay;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.relay.ConnectionCursor;
import graphql.Scalars;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLOutputType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Type;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionCursorMapperTest {

    @Mock
    private IGraphQLObjectMapper mockMapper;

    @Test
    public void test_handlesType_ConnectionCursor_true() throws Exception {
        // given
        Type type = ConnectionCursor.class;
        willReturn(ConnectionCursor.class).given(mockMapper).getClassFromType(type);
        ConnectionCursorMapper cut = createCut();

        // when
        boolean result = cut.handlesType(mockMapper, type);

        // then
        assertTrue(result);
    }

    @Test
    public void test_handlesType_Other_false() throws Exception {
        // given
        Type type = Object.class;
        willReturn(Object.class).given(mockMapper).getClassFromType(type);
        ConnectionCursorMapper cut = createCut();

        // when
        boolean result = cut.handlesType(mockMapper, type);

        // then
        assertFalse(result);
    }

    @Test
    public void test_getOutputType() throws Exception {
        // given
        ConnectionCursorMapper cut = createCut();

        // when
        GraphQLOutputType result = cut.getOutputType(null, null);

        // then
        assertEquals(result, Scalars.GraphQLString);
    }

    @Test
    public void test_getInputType() throws Exception {
        // given
        ConnectionCursorMapper cut = createCut();

        // when
        GraphQLOutputType result = cut.getOutputType(null, null);

        // then
        assertEquals(result, Scalars.GraphQLString);
    }

    private ConnectionCursorMapper createCut() {
        return new ConnectionCursorMapper();
    }
}