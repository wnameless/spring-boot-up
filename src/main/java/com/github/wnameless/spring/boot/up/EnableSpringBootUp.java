/*
 *
 * Copyright 2020 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.spring.boot.up;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.ComponentScan;

/**
 * 
 * {@link EnableSpringBootUp} is made for Spring to activate all features brought by SpringBootUp
 * library. SpringBootUp components under package {@code com.github.wnameless.spring.boot.up} will
 * be found automatically.<br>
 * <br>
 * Add this annotation to an {@code @Configuration} class to enable SpringBootUp.<br>
 * <br>
 * For example:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableSpringBootUp
 * public class MyWebConfiguration {}
 * </pre>
 *
 * @see ApplicationContextProvider
 *
 * @author Wei-Ming Wu
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan(basePackageClasses = {ApplicationContextProvider.class})
public @interface EnableSpringBootUp {}
