package net.dodgewhale.nsd;

import java.io.*;
import java.util.HashMap;

public class Main {

	public static final String COMMA = ",";

	public static void main(String[] args) {
		System.out.println("Loading data...");

		HashMap<Integer, Employee> employees = Main.readFile("memes.csv");
		if(!employees.isEmpty()) {

		}

		return;
	}

	public static HashMap<Integer, Employee> readFile(String fileName) {
		BufferedReader br = null;
		// Initialize employees map to return
		HashMap<Integer, Employee> employees = new HashMap<>();

		try {
			// Try to initialize BufferedReader with a new FileReader if the file exists
			br = new BufferedReader(new FileReader(fileName));



			String line;
			// Read every line in the CSV that isn't null
			while((line = br.readLine()) != null) {
				// Split the data by the , character stored in the public variable
				String[] data = line.split(COMMA);

				// Create a new Employee object using the String array
				Employee employee = Employee.load(data);
				if(employee != null) {
					// If the employee loads correctly, put it in the map
					employees.put(employee.getID(), employee);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Close the buffered reader if it isn't null
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return employees;
	}

}