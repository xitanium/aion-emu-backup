package com.aionemu.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * This class contains collection of thread utilities
 * 
 * @author SoulKeeper
 */
public class ThreadUtils
{

	/**
	 * Returns stacktrace of current thread represented as String
	 * 
	 * @return stacktrace of current thread represented as String
	 */
	public static String getStackTrace()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(baos);
		// noinspection ThrowableInstanceNeverThrown
		new Exception("Stack trace").printStackTrace(printStream);
		printStream.close();
		return new String(baos.toByteArray());
	}
}