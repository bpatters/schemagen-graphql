package com.bretpatterson.schemagen.graphql.utils;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class AnnotationUtilsTest {

    @Test
    public void getClassesWithAnnotation() throws Exception {
        Map<Class<?>, GraphQLTypeMapper> classes = AnnotationUtils.getClassesWithAnnotation(
                GraphQLTypeMapper.class, "com.bretpatterson.schemagen");

        assertFalse(classes.isEmpty());
    }

}