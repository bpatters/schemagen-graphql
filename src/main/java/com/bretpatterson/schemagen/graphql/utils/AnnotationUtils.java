package com.bretpatterson.schemagen.graphql.utils;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Common Annotaiton related utility methods.
 */
public class AnnotationUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationUtils.class);
	public static final String DEFAULT_NULL = "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n";
	private static ClassLoader classLoader;
	private static ClassPath classPath;
	public static final class DEFAULT_NULL_CLASS  {};

	static  {
		try {
			classLoader = AnnotationUtils.class.getClassLoader();
			classPath = ClassPath.from(classLoader);
		} catch (IOException ex) {
			Throwables.propagate(ex);
		}
	}

	public static <T extends Annotation> Map<Class, T> getClassesWithAnnotation(Class<T> annotation, String packageName) {
		ImmutableMap.Builder<Class, T> results = ImmutableMap.builder();
		try {
			ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClassesRecursive(packageName);
			for (ClassPath.ClassInfo info : classes) {
				try {
					Class<?> type = info.load();
					T classAnnotation = (T) type.getAnnotation(annotation);
					if (classAnnotation != null) {
						LOGGER.info("Found {} with annotation {}.", type.getCanonicalName(), annotation.getClass());
						results.put(type, classAnnotation);
					}
				} catch (NoClassDefFoundError ex) {
					LOGGER.warn("Failed to load {}.  This is probably because of an unsatisfied runtime dependency.", ex);
				}
			}
		} catch (Exception ex) {
			Throwables.propagate(ex);
		}

		return results.build();
	}

	public static boolean isNullValue(String value) {
		return DEFAULT_NULL.equals(value);
	}

	public static boolean isNullValue(Class value) {
		return DEFAULT_NULL_CLASS.class.equals(value);
	}
}
