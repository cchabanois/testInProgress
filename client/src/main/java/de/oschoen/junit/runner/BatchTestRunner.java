/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.oschoen.junit.runner;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * Define a junit 4 test suite based on pattern matching.
 *
 * <h2>Examples</h2>
 *
 * Find all classes in package com.example and sub packages, whose name ends with Test.
 *
 * <pre>
 * package com.example;
 *
 * import org.junit.runner.RunWith;
 *
 * &#64;RunWith(de.oschoen.junit.runner.BatchTestRunner.class)
 * public class AllTests { }
 * </pre>
 *
 * Find all classes in package com.example and sub packages, whose name ends with Suite.
 *
 * <pre>
 * package com.example;
 *
 * import org.junit.runner.RunWith;
 *
 * &#64;RunWith(de.oschoen.junit.runner.BatchTestRunner.class)
 * &#64;BatchTestRunner.BatchTestInclude("**.*Suite")
 * public class AllTests {}
 * </pre>
 *
 * Exclude a package:
 *
 * <pre>
 * package com.example;
 *
 * import org.junit.runner.RunWith;
 *
 * &#64;RunWith(de.oschoen.junit.runner.BatchTestRunner.class)
 * &#64;BatchTestRunner.BatchTestExclude("**.shouldBeIgnored.**")
 * public class AllTests {}
 * </pre>
 */
public class BatchTestRunner extends ParentRunner<Runner> {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface BatchTestInclude {
        /**
         * @return the include pattern
         */
        public String value();
    }

    private static String getIncludePattern(Class<?> klass) throws InitializationError {
        BatchTestInclude annotation = klass.getAnnotation(BatchTestInclude.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            return "**.*Test";
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface BatchTestExclude {
        /**
         * @return the exclude pattern
         */
        public String value();
    }

    private static String getExcludePattern(Class<?> klass) throws InitializationError {
        BatchTestExclude annotation = klass.getAnnotation(BatchTestExclude.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            return "";
        }
    }

    private final List<Runner> runners;
    private final static AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Called reflectively on classes annotated with <code>@RunWith(BatchTestRunner.class)</code>
     *
     * @param klass   the root class
     * @param builder builds runners for classes in the suite
     * @throws InitializationError
     */
    public BatchTestRunner(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        this(builder, klass, getClasses(klass, getIncludePattern(klass), getExcludePattern(klass)));
    }

    /**
     * Call this when there is no single root class (for example, multiple class names
     * passed on the command line to {@link org.junit.runner.JUnitCore}
     *
     * @param builder builds runners for classes in the suite
     * @param classes the classes in the suite
     * @throws InitializationError
     */
    public BatchTestRunner(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
        this(null, builder.runners(null, classes));
    }


    /**
     * Called by this class and subclasses once the classes making up the suite have been determined
     *
     * @param builder      builds runners for classes in the suite
     * @param klass        the root of the suite
     * @param suiteClasses the classes in the suite
     * @throws InitializationError
     */
    protected BatchTestRunner(RunnerBuilder builder, Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
        this(klass, builder.runners(klass, suiteClasses));
    }

    /**
     * Called by this class and subclasses once the runners making up the suite have been determined
     *
     * @param klass   root of the suite
     * @param runners for each class in the suite, a {@link Runner}
     * @throws InitializationError
     */
    protected BatchTestRunner(Class<?> klass, List<Runner> runners) throws InitializationError {
        super(klass);
        this.runners = runners;
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @Override
    protected Description describeChild(Runner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(Runner runner, final RunNotifier notifier) {
        runner.run(notifier);
    }

    /**
     * Scans all classes accessible from the class loader which belong to the given package and subpackages.
     *
     * @param suiteClass The suite class
     * @return The classes
     */
    private static Class[] getClasses(Class suiteClass, String includePattern, String excludePattern)
            throws InitializationError {
        try {
            ClassLoader classLoader = suiteClass.getClassLoader();
            assert classLoader != null;
            String packageName = suiteClass.getPackage().getName();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            ArrayList<Class> classes = new ArrayList<Class>();
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName, includePattern, excludePattern));
            }
            return classes.toArray(new Class[classes.size()]);
        } catch (ClassNotFoundException cnfe) {
            throw new InitializationError(cnfe);
        } catch (IOException ioe) {
            throw new InitializationError(ioe);
        }
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException if the class not exists
     */
    private static List<Class> findClasses(File directory, String packageName, String includePattern, String excludePattern) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName(), includePattern, excludePattern));
            } else if (file.getName().endsWith(".class")) {

                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);

                if (pathMatcher.match(includePattern, className) && !pathMatcher.match(excludePattern, className)) {
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }


}
