package ua.plukraine.algos;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class AlgorithmClassLoader {

	public static void main(String[] args) throws Exception {
		String path = "/home/plukraine/programming/eclipse/workspace/SortingVisualizer/bin";
		AlgorithmClassLoader loader = new AlgorithmClassLoader();
		List<Class> list = loader.loadFromFolder(new File(path));
		for (Class c : list ){
			System.out.println(c.getName());
		}
	}
	
	/**
	 * Load all non-abstract subclasses of ISortingAlgorithm type from folder (not recursively). Classes shouldn't be in a packages.
	 * @param directory - folder to load classes from
	 * @return loaded classes
	 * @throws IOException if directory don't exist or there was an error during loading
	 */
	public List<Class> loadFromFolder(File directory) throws IOException {
		URL file = directory.toURI().toURL();
		FileFilter f_filter = (f) -> f.getName().endsWith(".class");
		List<Class> loadedClasses = new ArrayList<>();
		
		try (URLClassLoader loader = new URLClassLoader(new URL[] {file})) {
			for (File f : directory.listFiles(f_filter)) {
				try {
					String classname = f.getName().replaceFirst("\\..+$", "");
					Class c = loader.loadClass(classname);
		
					if (!Modifier.isAbstract(c.getModifiers())
						&& ISortingAlgortihm.class.isAssignableFrom(c)) {
						loadedClasses.add(c);
					}
				}
				catch (ClassNotFoundException ex) {}
			}
		}
		
		return loadedClasses;
	}
	
	
	

}
