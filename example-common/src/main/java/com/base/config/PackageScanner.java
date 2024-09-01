package com.base.config;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @title: PackageScanner
 * @description: 包扫描工具类
 * @author: arron
 * @date: 2024/9/1 11:59
 */
public class PackageScanner {

    /**
     * 扫描指定包下的所有类，并获取它们的注解信息。
     *
     * @param packageNames    包名, 多个包名用逗号分隔
     * @param annotationClass
     * @return
     */
    public static Set<Class<?>> getAnnotatedClassesInPackages(String packageNames, Class<? extends Annotation> annotationClass) {
        Set<Class<?>> annotatedClasses = new HashSet<>();
        String[] splitPackageNames = packageNames.split(",");
        for (String packageName : splitPackageNames) {
            Set<Class<?>> annotatedClassesInPackage = getAnnotatedClassesInPackage(packageName, annotationClass);
            annotatedClasses.addAll(annotatedClassesInPackage);
        }
        return annotatedClasses;
    }

    /**
     * 扫描指定包下的所有类，并获取它们的注解信息。
     *
     * @param packageName     包名
     * @param annotationClass 注解类型
     * @return 包含注解信息的类集合
     */
    public static Set<Class<?>> getAnnotatedClassesInPackage(String packageName, Class<? extends Annotation> annotationClass) {
        Set<Class<?>> annotatedClasses = new HashSet<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(packageName) + "/" + "**/*.class";
            Resource[] resources = resolver.getResources(packageSearchPath);

            for (Resource resource : resources) {
                String className = resource.getFilename().substring(0, resource.getFilename().length() - 6); // Remove ".class" suffix
                className = packageName + '.' + className.replace('/', '.');

                Class<?> clazz = defineClass(className);
                if (clazz.isAnnotationPresent(annotationClass)) {
                    annotatedClasses.add(clazz);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while scanning package: " + packageName, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return annotatedClasses;
    }

    private static String resolveBasePackage(String packageName) {
        return packageName.replace('.', '/');
    }

    private static Class<?> defineClass(String name) throws ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl.loadClass(name);
    }

}
