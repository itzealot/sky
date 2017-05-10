package com.surfilter.gamass;

import java.util.List;

public class ClassLoaderTest {

	public static void main(String[] args) {
		ClassLoader cl = ClassLoaderTest.class.getClassLoader();

		// AppClassLoader的子类加载起加载，如果把该类打包成jar，放在Java/jre/lib/ext目录下，则由ExtClassLoader
		System.out.println("ClassLoaderTest ClassLoader:" + cl.getClass().getName());
		// 因为System类，List，Map等这样的系统提供jar类都在rt.jar中，所以由BootStrap类加载器加载
		// 因为BootStrap是祖先类，不是Java编写的，所以打印出class为null
		System.out.println("System ClassLoader:" + System.class.getClassLoader());
		System.out.println("List ClassLoader:" + List.class.getClassLoader());

		while (cl != null) {
			System.out.print(cl.getClass().getName() + "->");
			cl = cl.getParent();
		}

		System.out.println(cl);
	}

}