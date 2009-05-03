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

package com.aionemu.commons.callbacks;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

/**
 * This class is used to generate class proxies. All proxies implement
 * {@link com.aionemu.commons.callbacks.EnhancedObject} interface.<br>
 * Methods that are marked with {@link com.aionemu.commons.callbacks.Enhancable} annotation will be enhanced.<br>
 * If there is no methods to enhance - IllegalArgumentException will be thrown.<br>
 * 
 * @author SoulKeeper
 */
public class ObjectEnhancer
{

	/**
	 * Logger
	 */
	private static final Logger					log		= Logger.getLogger(ObjectEnhancer.class);

	/**
	 * Map that represents cache for generated classes It's {@link java.util.WeakHashMap}, records are added as
	 * map.put(class, null);
	 */
	private static final Map<Class<?>, Object>	cache	= new WeakHashMap<Class<?>, Object>();

	/**
	 * Creates new instance of enhanced class using no-arg constructor.<br>
	 * Abstract and final classes are not allowed. Interfaces are not allowed. Enums are not allowed.
	 * 
	 * @param superClass
	 *            class that will be enhanced.
	 * @param <T>
	 *            Generic arg to return valid instance
	 * @return instance of enhanced class
	 * @throws IllegalArgumentException
	 *             if there is no methods to enhance
	 */
	public static <T> T newInstance(Class<T> superClass)
	{
		return newInstance(superClass, new Object[] {});
	}

	/**
	 * Creates new instance of enhanced class using. <br>
	 * Abstract and final classes are not allowed. Interfaces are not allowed. Enums are not allowed.
	 * 
	 * @param superClass
	 *            class that will be enhanced.
	 * @param args
	 *            constructor arguments
	 * @param <T>
	 *            Generic arg to return valid instance
	 * @return instance of enhanced class
	 * @throws IllegalArgumentException
	 *             if there is no methods to enhance
	 */
	@SuppressWarnings( { "unchecked" })
	public static <T> T newInstance(Class<T> superClass, Object... args)
	{

		if (!ObjectEnhancerUtil.isEnhantable(superClass))
		{
			IllegalArgumentException e = new IllegalArgumentException("Class " + superClass.getName()
				+ " is not enhancable.");
			log.error(e.getMessage(), e);
			throw e;
		}

		List<Method> enhancableMethods = ObjectEnhancerUtil.getEnhancableMethods(superClass);
		if (enhancableMethods.size() == 0)
		{
			IllegalArgumentException e = new IllegalArgumentException("Class " + superClass.getName()
				+ " has no methods to ehnace");
			log.error(e.getMessage(), e);
			throw e;
		}

		Class<T> clazz = (Class<T>) getGeneratedClass(superClass);
		return createNewInstance(clazz, args);
	}

	/**
	 * Returns generated class. Method firts tries to find class from cache, if failed - it runs class generation
	 * 
	 * @param superClass
	 *            class that was(or will be) enhanced
	 * @return enhanced class
	 */
	private static synchronized Class<?> getGeneratedClass(Class superClass)
	{
		for (Class<?> aClass : cache.keySet())
		{
			if (aClass.getSuperclass().equals(superClass))
			{
				return aClass;
			}
		}

		byte[] classData = generateClass(superClass);
		Class<?> result = loadClass(superClass.getClassLoader(), ObjectEnhancerUtil.getEnhancedName(superClass),
			classData);
		cache.put(result, null);
		return result;
	}

	/**
	 * Generates enhanced class from superclass
	 * 
	 * @param superClass
	 *            class to enhance
	 * @return enhanced class
	 */
	private static byte[] generateClass(Class superClass)
	{
		ClassPool classPool = new ClassPool();
		classPool.appendClassPath(new LoaderClassPath(superClass.getClassLoader()));

		String newClassName = ObjectEnhancerUtil.getEnhancedName(superClass);
		try
		{

			CtClass newClass = classPool.getAndRename(superClass.getName(), newClassName);
			newClass.setSuperclass(classPool.get(superClass.getName()));
			newClass.setInterfaces(new CtClass[] { classPool.get(EnhancedObject.class.getName()) });
			newClass.setModifiers(newClass.getModifiers() | Modifier.FINAL);

			generateFields(newClass);
			generateMethods(newClass);
			generateConstructors(newClass);

			// newClass.writeFile("D:/");
			return newClass.toBytecode();
		}
		catch (Exception e)
		{
			RuntimeException ex = new RuntimeException("Exception while enhancing class", e);
			log.error(ex.getMessage(), e);
			throw ex;
		}
	}

	/**
	 * Generates class fields. It adds fields called "callbacks" and "callbackLock"
	 * 
	 * @param clazz
	 *            class to add fields
	 * @throws NotFoundException
	 *             never thrown
	 * @throws CannotCompileException
	 *             never thrown
	 */
	private static void generateFields(CtClass clazz) throws NotFoundException, CannotCompileException
	{
		for (CtField field : clazz.getFields())
		{
			clazz.removeField(field);
		}

		ClassPool cp = clazz.getClassPool();

		CtField cbField = new CtField(cp.get(List.class.getName()), "callbacks", clazz);
		cbField.setModifiers(Modifier.PRIVATE);
		clazz.addField(cbField, CtField.Initializer.byExpr("new " + HashMap.class.getName() + "();"));

		CtField cblField = new CtField(cp.get(ReentrantReadWriteLock.class.getName()), "callbackLock", clazz);
		cblField.setModifiers(Modifier.PRIVATE);
		clazz.addField(cblField, CtField.Initializer.byExpr("new " + ReentrantReadWriteLock.class.getName() + "();"));
	}

	/**
	 * Generates methods for superclass. It implements all methods from
	 * {@link com.aionemu.commons.callbacks.EnhancedObject}<br>
	 * and overrides public/protected methods with annotation {@link com.aionemu.commons.callbacks.Enhancable}
	 * 
	 * @param clazz
	 *            class to add methods
	 * @throws NotFoundException
	 *             never thrown
	 * @throws ClassNotFoundException
	 *             never thrown
	 * @throws CannotCompileException
	 *             never thrown
	 */
	private static void generateMethods(CtClass clazz) throws NotFoundException, ClassNotFoundException,
		CannotCompileException
	{

		ClassPool cp = clazz.getClassPool();
		CtClass objectClass = cp.get(Object.class.getName());
		CtClass enhancedObjectClass = cp.get(EnhancedObject.class.getName());

		// remove not valid methods
		for (CtMethod m : clazz.getMethods())
		{

			if (Modifier.isNative(m.getModifiers()))
			{
				continue;
			}

			CtClass dc = m.getDeclaringClass();
			if (dc.equals(objectClass) || dc.equals(enhancedObjectClass))
			{
				continue;
			}

			if (Modifier.isFinal(m.getModifiers()) || Modifier.isStatic(m.getModifiers()))
			{
				clazz.removeMethod(m);
				continue;
			}

			if (!(Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers())))
			{
				clazz.removeMethod(m);
				continue;
			}

			if (!isAnnotationPresent(m.getAnnotations(), Enhancable.class))
			{
				clazz.removeMethod(m);
				// noinspection UnnecessaryContinue
				continue;
			}
		}

		CtClass callbackClass = cp.get(Callback.class.getName());
		CtClass mapClass = cp.get(Map.class.getName());
		CtClass reentrantReadWriteLockClass = cp.get(ReentrantReadWriteLock.class.getName());

		for (CtMethod m : clazz.getMethods())
		{

			CtClass dc = m.getDeclaringClass();
			if (!(dc.equals(clazz) || dc.equals(enhancedObjectClass)))
			{
				continue;
			}

			if (dc.equals(enhancedObjectClass))
			{
				if (m.getName().equals("addCallback"))
				{
					CtMethod method = new CtMethod(CtClass.voidType, "addCallback", new CtClass[] { callbackClass },
						clazz);
					method.setModifiers(Modifier.PUBLIC);
					method.setBody("com.aionemu.commons.callbacks.CallbackHelper.addCallback($1, this);");
					clazz.addMethod(method);
				}
				else if (m.getName().equals("removeCallback"))
				{
					CtMethod method = new CtMethod(CtClass.voidType, "removeCallback", new CtClass[] { callbackClass },
						clazz);
					method.setModifiers(Modifier.PUBLIC);
					method.setBody("com.aionemu.commons.callbacks.CallbackHelper.removeCallback($1, this);");
					clazz.addMethod(method);
				}
				else if (m.getName().equals("getCallbacks"))
				{
					CtMethod method = new CtMethod(mapClass, "getCallbacks", new CtClass[] {}, clazz);
					method.setModifiers(Modifier.PUBLIC);
					method.setBody("return callbacks;");
					clazz.addMethod(method);
				}
				else if (m.getName().equals("getCallbackLock"))
				{
					CtMethod method = new CtMethod(reentrantReadWriteLockClass, "getCallbackLock", new CtClass[] {},
						clazz);
					method.setModifiers(Modifier.PUBLIC);
					method.setBody("return callbackLock;");
					clazz.addMethod(method);
				}
			}
			else if (dc.equals(clazz))
			{
				int paramLength = m.getParameterTypes().length;
				StringBuilder sb = new StringBuilder();
				sb.append('{');
				sb.append('\n');

				Annotation enhancable = null;
				for (Object o : m.getMethodInfo().getAttributes())
				{
					if (o instanceof AnnotationsAttribute)
					{
						AnnotationsAttribute attribute = (AnnotationsAttribute) o;
						enhancable = attribute.getAnnotation(Enhancable.class.getName());
						break;
					}
				}
				// noinspection ConstantConditions
				String listenerClassName = enhancable.getMemberValue("callback").toString();
				listenerClassName = listenerClassName.substring(1, listenerClassName.length() - 7);

				sb.append(CallbackResult.class.getName()).append(" cbr = ");
				sb.append(CallbackHelper.class.getName()).append(".beforeCall(this, Class.forName(\"");
				sb.append(listenerClassName).append("\", true, getClass().getClassLoader()), ");
				if (paramLength > 0)
				{
					sb.append("new Object[]{");
					for (int i = 1; i <= paramLength; i++)
					{
						sb.append("($w)$").append(i);

						if (i < paramLength)
						{
							sb.append(',');
						}
					}
					sb.append("}");
				}
				else
				{
					sb.append("null");
				}
				sb.append(");");
				sb.append('\n');
				sb.append("Object result = null;\n");
				sb.append("if(!cbr.isBlockingCaller()){\n");
				if (!m.getReturnType().equals(CtClass.voidType))
				{
					sb.append("\tresult = ($w)");
				}
				sb.append("\tsuper.").append(m.getName()).append("(");
				for (int i = 1; i <= paramLength; i++)
				{
					sb.append('$').append(i);
					if (i < paramLength)
					{
						sb.append(',');
					}
				}
				sb.append(");\n");
				sb.append("} else {\n");
				sb.append("\tresult = cbr.getResult();\n}\n");
				sb.append("cbr = ");
				sb.append(CallbackHelper.class.getName()).append(".afterCall(this, Class.forName(\"");
				sb.append(listenerClassName).append("\", true, getClass().getClassLoader()), ");
				if (paramLength > 0)
				{
					sb.append("new Object[]{");
					for (int i = 1; i <= paramLength; i++)
					{
						sb.append("($w)$").append(i);

						if (i < paramLength)
						{
							sb.append(',');
						}
					}
					sb.append("}");
				}
				else
				{
					sb.append("null");
				}
				sb.append(", result);");
				sb.append('\n');
				if (!m.getReturnType().equals(CtClass.voidType))
				{
					sb.append("if(cbr.isBlockingCaller()){\n");
					sb.append("\treturn ($r)($w)cbr.getResult();\n}");
					sb.append(" else {\n");
					sb.append("\treturn ($r)($w)result; \n}");
				}

				sb.append('\n');
				sb.append('}');
				// System.out.println(sb.toString());
				m.setBody(sb.toString());
			}
		}
	}

	/**
	 * Returns true if annotation is present
	 * 
	 * @param annotations
	 *            class annotations
	 * @param annotationClass
	 *            anntotaion to find
	 * @return true if annotation is present
	 */
	private static boolean isAnnotationPresent(Object[] annotations,
		Class<? extends java.lang.annotation.Annotation> annotationClass)
	{
		for (Object o : annotations)
		{
			try
			{
				if (o.toString().startsWith("@" + annotationClass.getName()))
				{
					return true;
				}
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		return false;
	}

	/**
	 * Generates constructors for class. All primitive type arguments are subclassed by their wrappers.<br>
	 * Generated constructors are delegating to superclasses.
	 * 
	 * @param clazz
	 *            class to generate constructors
	 */
	private static void generateConstructors(CtClass clazz)
	{
		CtConstructor[] constructors = clazz.getConstructors();

		for (CtConstructor c : constructors)
		{
			try
			{
				clazz.removeConstructor(c);

				CtClass[] orgParams = c.getParameterTypes();
				CtClass[] wrappedParams = new CtClass[orgParams.length];
				System.arraycopy(orgParams, 0, wrappedParams, 0, orgParams.length);

				for (int i = 0; i < orgParams.length; i++)
				{
					wrappedParams[i] = getPrimitiveTypeWrapper(wrappedParams[i], clazz.getClassPool());
				}

				CtConstructor nc = new CtConstructor(wrappedParams, clazz);
				StringBuilder sb = new StringBuilder();
				sb.append("{\n");

				sb.append("super(");
				for (int i = 1; i <= wrappedParams.length; i++)
				{
					sb.append("$").append(i);
					CtClass orgParam = orgParams[i - 1];
					CtClass wrappedParam = wrappedParams[i - 1];

					if (!orgParam.equals(wrappedParam))
					{
						if (orgParam.equals(CtClass.byteType))
						{
							sb.append(".byteValue()");
						}
						else if (orgParam.equals(CtClass.booleanType))
						{
							sb.append(".booleanValue()");
						}
						else if (orgParam.equals(CtClass.charType))
						{
							sb.append(".charValue()");
						}
						else if (orgParam.equals(CtClass.shortType))
						{
							sb.append(".shortValue()");
						}
						else if (orgParam.equals(CtClass.intType))
						{
							sb.append(".intValue()");
						}
						else if (orgParam.equals(CtClass.floatType))
						{
							sb.append(".floatValue()");
						}
						else if (orgParam.equals(CtClass.longType))
						{
							sb.append(".longValue()");
						}
						else if (orgParam.equals(CtClass.doubleType))
						{
							sb.append(".doubleValue()");
						}
					}

					if (i < wrappedParams.length)
					{
						sb.append(',');
					}
				}
				sb.append(");");
				sb.append("\n}");
				// System.out.println(sb.toString());
				nc.setBody(sb.toString());
				clazz.addConstructor(nc);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Returns primitive type wrapper for class or same class if there is no primitive type wrapper
	 * 
	 * @param clazz
	 *            class to get primitive type wrapper
	 * @param cp
	 *            class pool
	 * @return primitive type wrapper or same class
	 */
	private static CtClass getPrimitiveTypeWrapper(CtClass clazz, ClassPool cp)
	{
		if (clazz instanceof CtPrimitiveType)
		{
			CtPrimitiveType ctPrimitiveType = (CtPrimitiveType) clazz;
			try
			{
				return cp.get(ctPrimitiveType.getWrapperName());
			}
			catch (NotFoundException e)
			{
				throw new RuntimeException(e);
			}
		}

		return clazz;
	}

	/**
	 * Loads class using given ClassLoader
	 * 
	 * @param cl
	 *            classLoader
	 * @param className
	 *            className
	 * @param data
	 *            class binary representation
	 * @return loaded class
	 */
	private static Class<?> loadClass(ClassLoader cl, String className, byte[] data)
	{
		try
		{
			Method m = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class,
				Integer.TYPE, Integer.TYPE });
			boolean oldAccessible = m.isAccessible();
			m.setAccessible(true);
			Class<?> result = (Class<?>) m.invoke(cl, className, data, 0, data.length);
			m.setAccessible(oldAccessible);
			return result;
		}
		catch (NoSuchMethodException e)
		{
			Error err = new Error("Can't find method ClassLoader#defineClass(String, byte[], int, int)", e);
			log.fatal(err.getMessage(), e);
			throw err;
		}
		catch (InvocationTargetException e)
		{
			RuntimeException ex = new RuntimeException("Exception while loading class data", e);
			log.error(ex.getMessage(), e);
			throw ex;
		}
		catch (IllegalAccessException e)
		{
			Error err = new Error("Exception while accessing method", e);
			log.fatal(err.getMessage(), e);
			throw err;
		}
	}

	/**
	 * Creates new instance of the class using given args
	 * 
	 * @param clazz
	 *            class to create new instance
	 * @param args
	 *            cunstructor arguments
	 * @param <T>
	 *            instance type
	 * @return created instance
	 */
	@SuppressWarnings( { "unchecked" })
	private static <T> T createNewInstance(Class<? extends T> clazz, Object... args)
	{
		Class[] paramTypes = new Class[args.length];
		for (int i = 0; i < args.length; i++)
		{
			paramTypes[i] = args[i].getClass();
		}

		try
		{
			Constructor c = clazz.getConstructor(paramTypes);
			return (T) c.newInstance(args);
		}
		catch (Exception e)
		{
			RuntimeException ex = new RuntimeException("Can't create enhanced instance of class: "
				+ clazz.getSuperclass().getName(), e);
			log.error(ex.getMessage(), e);
			throw ex;
		}
	}
}
