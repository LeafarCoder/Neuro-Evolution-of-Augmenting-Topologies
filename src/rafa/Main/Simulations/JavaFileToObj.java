package rafa.Main.Simulations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


public class JavaFileToObj{

	private final static String defaultPackageName = "rafa.Main.Simulations.Created";
	private static String packageName = "rafa.Main.Simulations.Created";
	private static String javaInputFolder;
	private static String classOutputFolder;
	private static String javaFileName;
	private static File sourceFile;

	public JavaFileToObj(String path_obj, String javaName, File srcFile) {
		classOutputFolder = path_obj;
		javaFileName = javaName;
		sourceFile = srcFile;
	}

	/** java File Object represents an in-memory java source file <br>
	 * so there is no need to put the source file on hard disk  **/
	public static class InMemoryJavaFileObject extends SimpleJavaFileObject
	{
		private String contents = null;

		public InMemoryJavaFileObject(String className, String contents) throws Exception{
			super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.contents = contents;
		}

		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException{
			return contents;
		}
	}

	/** Get a simple Java File Object ,<br>
	 * It is just for demo, content of the source code is dynamic in real use case */
	public static JavaFileObject getJavaFileObject(){
		StringBuilder contents = new StringBuilder();
		Scanner sc;
		try {
			sc = new Scanner(sourceFile);
			boolean empty_so_far = true;
			while(sc.hasNextLine()){
				String nxt = sc.nextLine();

				// get the package of the class
				if(empty_so_far && !nxt.equals("")){
					empty_so_far = false;
					if(nxt.contains("package ")){
						packageName = nxt.split("\\s+")[1];	// index 0 is 'package'; index 1 is the package path
						packageName = packageName.substring(0, packageName.length() - 1); // remove ';'
						
						if(!packageName.startsWith(defaultPackageName)){
							String message = "The current package ("+packageName+") does not\nin the default package ("+defaultPackageName+").\n\nNew package:";
							String ans = (String)JOptionPane.showInputDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE , null, null, defaultPackageName + "." + packageName);
							if(ans != null){packageName = ans;}
							nxt = "package " + packageName + ";";
						}
					}
				}

				contents.append(nxt + "\n");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		JavaFileObject so = null;
		try{
			so = new InMemoryJavaFileObject(javaFileName, contents.toString());
		}catch (Exception exception){
			exception.printStackTrace();
		}
		return so;
	}

	public void compile(Iterable<? extends JavaFileObject> files) {

		//get system compiler:
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics,null,null);

		//specify classes output folder
		File f_aux = new File("bin/");
		Iterable<String> options = Arrays.asList("-d", f_aux.getAbsolutePath());

		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,diagnostics, options, null,files);

		boolean status = task.call();
		if(status == true){
			
		}else{
			System.err.println("\nFound error while compiling " + javaFileName + "!\nError stack:");
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				System.err.println("\n------------------------------------------------------");
				System.err.println("Error at " + diagnostic.getSource() + " (line " + diagnostic.getLineNumber() + "):");
				System.err.println(diagnostic.getMessage(Locale.ENGLISH));
				System.err.println("------------------------------------------------------\n");
			}
		}
	}
	
	public Object instantiateObject(){
		
		// ************ INSTANTIATE *************
		
		File file = new File(classOutputFolder);

		try{
			// Convert File to a URL
			URL[] urls = new URL[] {file.toURL()}; // file:/classes/demo
			// Create a new class loader with the directory
			ClassLoader loader = new URLClassLoader(urls);
			Class<?> thisClass = loader.loadClass(packageName + "." + javaFileName);
			Object instance = thisClass.newInstance();

			return instance;

		}catch (MalformedURLException e){
			e.printStackTrace();
		}catch (ClassNotFoundException e){
			e.printStackTrace();
		}catch (Exception ex){
			ex.printStackTrace();
		}

		return null;
	}


}