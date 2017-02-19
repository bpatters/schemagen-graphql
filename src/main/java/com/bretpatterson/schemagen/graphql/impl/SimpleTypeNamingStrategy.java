package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLName;
import com.google.common.base.Preconditions;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Type naming strategy that uses the simple class name as the GraphQL type.
 */
public class SimpleTypeNamingStrategy implements ITypeNamingStrategy {

    private final String delimiter;
    private final String inputTypePostfix;

    public SimpleTypeNamingStrategy(String delimiter, String inputTypePostfix) {
        Preconditions.checkNotNull(delimiter, "Delimiter cannot be null.");
        Preconditions.checkNotNull(inputTypePostfix, "InputType Postfix cannot be null.");
        this.delimiter = delimiter;
        this.inputTypePostfix = inputTypePostfix;
    }

    public SimpleTypeNamingStrategy() {
        this("_", "Input");
    }

    @Override
    public String getTypeName(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
        String typeString;
        Class<?> theClass = graphQLObjectMapper.getClassFromType(type);

        GraphQLName typeName = theClass.getAnnotation(GraphQLName.class);
        if (typeName != null) {
            return typeName.name();
        }

        // start with the class name
        typeString = theClass.getSimpleName();

        // for parameterized types append the parameter types to build unique type
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;

            typeString += this.getDelimiter() +
                    getParametersTypeString(graphQLObjectMapper, pType);
        }

        return typeString;
    }

    String getParametersTypeString(IGraphQLObjectMapper graphQLObjectMapper, ParameterizedType type) {
        String parametersTypeString = "";
        Type[] subTypes = type.getActualTypeArguments();
        for (Type subType : subTypes) {
            if (parametersTypeString.length() != 0) {
                parametersTypeString += this.getDelimiter();
            }
            parametersTypeString += graphQLObjectMapper.getClassFromType(subType).getSimpleName();
            if (subType instanceof ParameterizedType) {
                parametersTypeString += this.getDelimiter() +
                        getParametersTypeString(graphQLObjectMapper, (ParameterizedType) subType);
            }
        }

        return parametersTypeString;
    }

    @Override
    public String getInputTypePostfix() {
        return inputTypePostfix;
    }

    @Override
    public String getDelimiter() {
        return delimiter;
    }
}
