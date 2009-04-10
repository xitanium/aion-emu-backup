/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.commons.scripting.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import com.aionemu.commons.scripting.metadata.OnClassLoad;
import com.aionemu.commons.scripting.metadata.OnClassUnload;

/**
 * This class is responsible for scanning loaded classes for annotations
 * {@link com.aionemu.commons.scripting.metadata.OnClassLoad} and
 * {@link com.aionemu.commons.scripting.metadata.OnClassUnload}
 *
 * Methods with such annotations will be invoked.
 *
 * @author SoulKeeper
 */
public class ClassAnnotationProcessor {

    /**
     * logger for this class
     */
    private static final Logger log = Logger.getLogger(ClassAnnotationProcessor.class);

    /**
     * This method is invoked in the process of initialization of script contex
     * @param clazz classes to scan
     */
    static void postLoad(Class<?>... clazz) {
        for (Class<?> c : clazz) {
            doMethodInvoke(c.getDeclaredMethods(), OnClassLoad.class);
        }
    }

    /**
     * This method is invoked in the process of shutdown of script context
     * @param clazz classes to scan
     */
    static void preUnload(Class<?>... clazz) {
        for (Class<?> c : clazz) {
            doMethodInvoke(c.getDeclaredMethods(), OnClassUnload.class);
        }
    }

    /**
     * Actually invokes method where given annotation class is present.
     * Only static methods can be invoked
     * @param methods Methods to scan for annotations
     * @param annotationClass class of annotation to search for
     */
    static void doMethodInvoke(Method[] methods, Class<? extends Annotation> annotationClass) {
        for (Method m : methods) {

            if (!Modifier.isStatic(m.getModifiers())) {
                continue;
            }

            boolean accessible = m.isAccessible();
            m.setAccessible(true);

            if (m.getAnnotation(annotationClass) != null) {
                try {
                    m.invoke(null);
                }
                catch (IllegalAccessException e) {
                    log.error("Can't access method " + m.getName() + " of class " + m.getDeclaringClass().getName(), e);
                }
                catch (InvocationTargetException e) {
                    log.error("Can't invoke method " + m.getName() + " of class " + m.getDeclaringClass().getName(), e);
                }
            }

            m.setAccessible(accessible);
        }
    }
}