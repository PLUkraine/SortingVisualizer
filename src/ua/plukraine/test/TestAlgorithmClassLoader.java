package ua.plukraine.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import ua.plukraine.algos.AlgorithmClassLoader;

public class TestAlgorithmClassLoader {

	@Test
	public void testCorrectLoad() {
		File f = new File("D:/Testing/");
		AlgorithmClassLoader loader = new AlgorithmClassLoader();
		try {
			List<Class> loaded = null;
			loaded = loader.loadFromFolder(f);
			assertEquals("Not 1 class loaded", 1, loaded.size());
			assertEquals("Wrong class loaded", loaded.get(0), Class.forName("QuickSortRightPivot"));
		} catch (IOException e) {
			fail(String.format("IOException thrown, message: %s", e.getMessage()));
		} catch (ClassNotFoundException e) {
			fail(String.format("Test error, no QuickSortRightPivot in runtime"));
		}
	}

	@Test
	public void testFolderEmpty() {
		File f = new File("D:/Testing/empty");
		AlgorithmClassLoader loader = new AlgorithmClassLoader();
		try {
			@SuppressWarnings("rawtypes")
			List<Class> loaded = null;
			loaded = loader.loadFromFolder(f);
			assertTrue("List should be empty", loaded.isEmpty());
		} catch (IOException e) {
			fail(String.format("IOException thrown, message: %s", e.getMessage()));
		}
	}
}
