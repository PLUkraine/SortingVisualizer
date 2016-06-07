package ua.plukraine.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ua.plukraine.algos.AlgorithmClassLoader;
import ua.plukraine.algos.ISortingAlgortihm;
import ua.plukraine.algos.InsertionSorting;
import ua.plukraine.algos.QuickSortRandomPivot;

@SuppressWarnings("rawtypes")
public class UserDialogHelper {
	private JFileChooser fileChooser;
	private Set<Class> algo_list = new HashSet<Class>();
	
	public UserDialogHelper() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		algo_list.add(InsertionSorting.class);
		algo_list.add(QuickSortRandomPivot.class);
	}
	/**
	 * Pick algorithm from existing list
	 * @param parent parent frame
	 * @return picked algorithm or null if no algorithm was picked
	 */
	public ISortingAlgortihm pickAlgorithm(JFrame parent) {
		List<ISortingAlgortihm> instances = new ArrayList<>();
		for (Class a : algo_list) {
			try {
				ISortingAlgortihm algo = (ISortingAlgortihm)a.newInstance();
				instances.add(algo);
			} catch ( IllegalAccessException 
					| InstantiationException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
		// Add numbers to the names of algorithms
		Object[] choises = IntStream.range(0, instances.size())
			.mapToObj((i) -> i + ". " + instances.get(i).getName()).toArray();
		Object choise = JOptionPane.showInputDialog(parent, "Choose algorithm", "Algorithm required", JOptionPane.QUESTION_MESSAGE,
				null, choises, choises[0]);
		if (choise == null) {
			return null;
		}
		String o = choise.toString();
		// Use numbers on beginning to determine the algorithm
		return instances.get(Integer.parseInt(o.substring(0, o.indexOf('.'))));
	}
	
	/**
	 * Get array from dialog
	 * @param parent parent frame
	 * @param MAX_ARR_LEN max array lenght
	 * @param prevArr previous array (will be displayed in a dialog)
	 * @return parsed array or null if error happened
	 */
	public int[] getArray(JFrame parent, int MAX_ARR_LEN, int[] prevArr) {
		String str_arr = (String)JOptionPane.showInputDialog(
				parent,
                "Enter an array of positive ints",
                "Enter an array",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                arrayToString(prevArr));
		if (str_arr == null) {
			return null;
		}
		String[] tokens = str_arr.split(",");
		
		if (tokens.length == 0) {
			String mes = "Array is empty";
			JOptionPane.showMessageDialog(parent, mes);
			return null;
		}
		
		if (tokens.length > MAX_ARR_LEN) {
			String mes = String.format("Array must be less than %d elements long", MAX_ARR_LEN);
			JOptionPane.showMessageDialog(parent, mes);
			return null;
		}
		
		int[] arr = new int[tokens.length];
		for (int i=0; i<tokens.length; ++i) {
			try {
				arr[i] = Integer.parseUnsignedInt(tokens[i].trim());
				if (arr[i] == 0)
					throw new NumberFormatException("Number isn't positive");
			} catch (NumberFormatException ex) {
				String mes = String.format("Element #%d, \"%s\" namely, isn't positive integer", i+1, tokens[i]);
				JOptionPane.showMessageDialog(parent, mes);
				return null;
			}
		}
		return arr;
	}
	
	/**
	 * Convert array to string. Goes like this: 1,2,3
	 * @param arr - array to convert
	 * @return converted array
	 */
	protected static String arrayToString(int[] arr) {
		StringBuilder strArr = new StringBuilder();
		if (arr == null) return strArr.toString();
		for (int i=0; i<arr.length; ++i) {
			if (i == 0)
				strArr.append(arr[i]);
			else {
				strArr.append(", ");
				strArr.append(arr[i]);
			}
		}
		return strArr.toString();
	}
	
	/**
	 * Get array from dialog
	 * @param parent parent frame
	 * @param MAX_ARR_LEN max array lenght
	 * @return parsed array or null if error happened
	 */
	public int[] getArray(JFrame parent, int MAX_ARR_LEN) {
		int[] sampleArr = new int[]{ 1,2,3 };
		return getArray(parent, MAX_ARR_LEN, sampleArr);
	}
	
	/**
	 * Pick directory and load algorithms
	 * @param parent parent frame
	 * @return list of loaded classes (if any) or null if user refuses to choose folder
	 */
	public void loadAlgorithms(JFrame parent) {
		int dialogRes = fileChooser.showOpenDialog(parent);
		if (dialogRes == JFileChooser.APPROVE_OPTION) {
			try {
				File folder = fileChooser.getSelectedFile();
				List<Class> classes = null;
				
				if (folder.exists() && folder.isDirectory()) {
					classes = new AlgorithmClassLoader().loadFromFolder(folder);
				}
				
				String mes = "Not loaded";
				// form list of loaded classes
				if (classes != null && !classes.isEmpty()) {
					StringBuilder listOfClasses = new StringBuilder();
					for (Class c : classes) {
						listOfClasses.append(c.getName());
						listOfClasses.append(", ");
					}
					mes = "Loaded " + listOfClasses.substring(0, listOfClasses.length()-2);
				}
				
				// add algorithms
				if (classes != null) {
					algo_list.addAll(classes);
				}
				
				// show if loaded something
				JOptionPane.showMessageDialog(parent, mes);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(parent, ex.getMessage());
			}
		}
	}
}
