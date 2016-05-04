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

public class UserDialogHelper {
	private JFileChooser fileChooser;
	private Set<Class> algo_list = new HashSet<Class>();
	
	public UserDialogHelper() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		algo_list.add(InsertionSorting.class);
		algo_list.add(QuickSortRandomPivot.class);
	}
	
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
		String o = JOptionPane.showInputDialog(parent, "Choose algorithm", "Algorithm required", JOptionPane.QUESTION_MESSAGE,
				null, choises, choises[0]).toString();
		if (o == null) {
			return null;
		}
		// Use numbers on beginning to determine the algorithm
		return instances.get(Integer.parseInt(o.substring(0, o.indexOf('.'))));
	}
	
	/**
	 * Get array from dialog
	 * @param parent parent frame
	 * @param MAX_ARR_LEN max array lenght
	 * @return parsed array or null if error happened
	 */
	public int[] getArray(JFrame parent, int MAX_ARR_LEN) {
		String str_arr = (String)JOptionPane.showInputDialog(
				parent,
                "Enter array of positive ints",
                "Enter array",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                "1,2,3");
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
	 * Pick directory and load algorithms
	 * @param parent parent frame
	 * @return list of loaded classes (if any) or null if user refuse to choose folder
	 */
	@SuppressWarnings("rawtypes")
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
