// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JdkCompiler.java

package com.autohome.turbo.common.compiler.support;

import com.autohome.turbo.common.utils.ClassHelper;
import java.io.*;
import java.net.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import javax.tools.*;

// Referenced classes of package com.autohome.turbo.common.compiler.support:
//			AbstractCompiler, ClassUtils

public class JdkCompiler extends AbstractCompiler
{
	private static final class JavaFileManagerImpl extends ForwardingJavaFileManager
	{

		private final ClassLoaderImpl classLoader;
		private final Map fileObjects = new HashMap();

		public FileObject getFileForInput(javax.tools.JavaFileManager.Location location, String packageName, String relativeName)
			throws IOException
		{
			FileObject o = (FileObject)fileObjects.get(uri(location, packageName, relativeName));
			if (o != null)
				return o;
			else
				return super.getFileForInput(location, packageName, relativeName);
		}

		public void putFileForInput(StandardLocation location, String packageName, String relativeName, JavaFileObject file)
		{
			fileObjects.put(uri(location, packageName, relativeName), file);
		}

		private URI uri(javax.tools.JavaFileManager.Location location, String packageName, String relativeName)
		{
			return ClassUtils.toURI((new StringBuilder()).append(location.getName()).append('/').append(packageName).append('/').append(relativeName).toString());
		}

		public JavaFileObject getJavaFileForOutput(javax.tools.JavaFileManager.Location location, String qualifiedName, javax.tools.JavaFileObject.Kind kind, FileObject outputFile)
			throws IOException
		{
			JavaFileObject file = new JavaFileObjectImpl(qualifiedName, kind);
			classLoader.add(qualifiedName, file);
			return file;
		}

		public ClassLoader getClassLoader(javax.tools.JavaFileManager.Location location)
		{
			return classLoader;
		}

		public String inferBinaryName(javax.tools.JavaFileManager.Location loc, JavaFileObject file)
		{
			if (file instanceof JavaFileObjectImpl)
				return file.getName();
			else
				return super.inferBinaryName(loc, file);
		}

		public Iterable list(javax.tools.JavaFileManager.Location location, String packageName, Set kinds, boolean recurse)
			throws IOException
		{
			Iterable result = super.list(location, packageName, kinds, recurse);
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			List urlList = new ArrayList();
			for (Enumeration e = contextClassLoader.getResources("com"); e.hasMoreElements(); urlList.add(e.nextElement()));
			ArrayList files = new ArrayList();
			if (location == StandardLocation.CLASS_PATH && kinds.contains(javax.tools.JavaFileObject.Kind.CLASS))
			{
				Iterator i$ = fileObjects.values().iterator();
				do
				{
					if (!i$.hasNext())
						break;
					JavaFileObject file = (JavaFileObject)i$.next();
					if (file.getKind() == javax.tools.JavaFileObject.Kind.CLASS && file.getName().startsWith(packageName))
						files.add(file);
				} while (true);
				files.addAll(classLoader.files());
			} else
			if (location == StandardLocation.SOURCE_PATH && kinds.contains(javax.tools.JavaFileObject.Kind.SOURCE))
			{
				Iterator i$ = fileObjects.values().iterator();
				do
				{
					if (!i$.hasNext())
						break;
					JavaFileObject file = (JavaFileObject)i$.next();
					if (file.getKind() == javax.tools.JavaFileObject.Kind.SOURCE && file.getName().startsWith(packageName))
						files.add(file);
				} while (true);
			}
			JavaFileObject file;
			for (Iterator i$ = result.iterator(); i$.hasNext(); files.add(file))
				file = (JavaFileObject)i$.next();

			return files;
		}

		public JavaFileManagerImpl(JavaFileManager fileManager, ClassLoaderImpl classLoader)
		{
			super(fileManager);
			this.classLoader = classLoader;
		}
	}

	private static final class JavaFileObjectImpl extends SimpleJavaFileObject
	{

		private ByteArrayOutputStream bytecode;
		private final CharSequence source;

		public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws UnsupportedOperationException
		{
			if (source == null)
				throw new UnsupportedOperationException("source == null");
			else
				return source;
		}

		public InputStream openInputStream()
		{
			return new ByteArrayInputStream(getByteCode());
		}

		public OutputStream openOutputStream()
		{
			return bytecode = new ByteArrayOutputStream();
		}

		public byte[] getByteCode()
		{
			return bytecode.toByteArray();
		}

		public JavaFileObjectImpl(String baseName, CharSequence source)
		{
			super(ClassUtils.toURI((new StringBuilder()).append(baseName).append(".java").toString()), javax.tools.JavaFileObject.Kind.SOURCE);
			this.source = source;
		}

		JavaFileObjectImpl(String name, javax.tools.JavaFileObject.Kind kind)
		{
			super(ClassUtils.toURI(name), kind);
			source = null;
		}

		public JavaFileObjectImpl(URI uri, javax.tools.JavaFileObject.Kind kind)
		{
			super(uri, kind);
			source = null;
		}
	}

	private final class ClassLoaderImpl extends ClassLoader
	{

		private final Map classes = new HashMap();
		final JdkCompiler this$0;

		Collection files()
		{
			return Collections.unmodifiableCollection(classes.values());
		}

		protected Class findClass(String qualifiedClassName)
			throws ClassNotFoundException
		{
			JavaFileObject file = (JavaFileObject)classes.get(qualifiedClassName);
			if (file != null)
			{
				byte bytes[] = ((JavaFileObjectImpl)file).getByteCode();
				return defineClass(qualifiedClassName, bytes, 0, bytes.length);
			}
			return ClassHelper.forNameWithCallerClassLoader(qualifiedClassName, getClass());
			ClassNotFoundException nf;
			nf;
			return super.findClass(qualifiedClassName);
		}

		void add(String qualifiedClassName, JavaFileObject javaFile)
		{
			classes.put(qualifiedClassName, javaFile);
		}

		protected synchronized Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException
		{
			return super.loadClass(name, resolve);
		}

		public InputStream getResourceAsStream(String name)
		{
			if (name.endsWith(".class"))
			{
				String qualifiedClassName = name.substring(0, name.length() - ".class".length()).replace('/', '.');
				JavaFileObjectImpl file = (JavaFileObjectImpl)classes.get(qualifiedClassName);
				if (file != null)
					return new ByteArrayInputStream(file.getByteCode());
			}
			return super.getResourceAsStream(name);
		}

		ClassLoaderImpl(ClassLoader parentClassLoader)
		{
			this$0 = JdkCompiler.this;
			super(parentClassLoader);
		}
	}


	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private final DiagnosticCollector diagnosticCollector = new DiagnosticCollector();
	private final ClassLoaderImpl classLoader;
	private final JavaFileManagerImpl javaFileManager;
	private volatile List options;

	public JdkCompiler()
	{
		options = new ArrayList();
		options.add("-target");
		options.add("1.6");
		StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if ((loader instanceof URLClassLoader) && !loader.getClass().getName().equals("sun.misc.Launcher$AppClassLoader"))
			try
			{
				URLClassLoader urlClassLoader = (URLClassLoader)loader;
				List files = new ArrayList();
				URL arr$[] = urlClassLoader.getURLs();
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					URL url = arr$[i$];
					files.add(new File(url.getFile()));
				}

				manager.setLocation(StandardLocation.CLASS_PATH, files);
			}
			catch (IOException e)
			{
				throw new IllegalStateException(e.getMessage(), e);
			}
		classLoader = (ClassLoaderImpl)AccessController.doPrivileged(new PrivilegedAction() {

			final ClassLoader val$loader;
			final JdkCompiler this$0;

			public ClassLoaderImpl run()
			{
				return new ClassLoaderImpl(loader);
			}

			public volatile Object run()
			{
				return run();
			}

			
			{
				this$0 = JdkCompiler.this;
				loader = classloader;
				super();
			}
		});
		javaFileManager = new JavaFileManagerImpl(manager, classLoader);
	}

	public Class doCompile(String name, String sourceCode)
		throws Throwable
	{
		int i = name.lastIndexOf('.');
		String packageName = i >= 0 ? name.substring(0, i) : "";
		String className = i >= 0 ? name.substring(i + 1) : name;
		JavaFileObjectImpl javaFileObject = new JavaFileObjectImpl(className, sourceCode);
		javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, packageName, (new StringBuilder()).append(className).append(".java").toString(), javaFileObject);
		Boolean result = compiler.getTask(null, javaFileManager, diagnosticCollector, options, null, Arrays.asList(new JavaFileObject[] {
			javaFileObject
		})).call();
		if (result == null || !result.booleanValue())
			throw new IllegalStateException((new StringBuilder()).append("Compilation failed. class: ").append(name).append(", diagnostics: ").append(diagnosticCollector).toString());
		else
			return classLoader.loadClass(name);
	}
}
