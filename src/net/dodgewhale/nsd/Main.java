package net.dodgewhale.nsd;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

	// Globals
	public static HashMap<Integer, Employee> _hierarchy;
	public static final String COMMA = ",";
	public static int _hierarchySize = 0;

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		boolean quit = false;

		System.out.println("Please enter the name of the payroll or 'quit' to close the program");
		System.out.println("Available files: /");

		// Print out all of the files in the data/ folder with the extension 'csv'
		for(String file : new File("data").list()) {
			// Check file extension
			if(file.substring(file.lastIndexOf('.') + 1).equals("csv")) {
				System.out.println("\t/" + file);
			}
		}

		// While 'quit' hasn't been inputed as the filename, loop
		while(!quit) {
			System.out.print("File: ");
			String fileName = input.nextLine().trim();

			if (fileName.equalsIgnoreCase("quit")) {
				quit = true;
				break;
			}

			// Set the current hierarchy to that of which is returned by the readFile()
			// method, returns null if the file is invalid
			_hierarchy = Main.readFile(fileName);
			if(_hierarchy != null) {
				System.out.println("Hierarchy has " + (_hierarchySize + 1) + " levels:\n");

				// Loop through every employee at the top of the hierarchy, print their information and team(s)
				for(Employee employee : _hierarchy.values()) {
					if(!employee.hasManager()) {
						Main.printEmployee(employee);
					}
				}

				System.out.println("");
				Main.printLowManagers();

				System.out.println("");
				Main.printTeamSalary();

				// Reset global variables ready for a new filename to be accepted
				_hierarchy = null;
				_hierarchySize = 0;
			}
		}

		// Close the scanner
		input.close();
	}

	/**
	 * Parses a file (of type csv) and converts it into a HashMap of<br>
	 * Employee ID's and Employee objects
	 * @param fileName Name of the file in /data to process
	 * @return Hierarchy map
	 */
	public static HashMap<Integer, Employee> readFile(String fileName) {
		File payroll = new File("data/" + fileName);

		// Initialise employees map to return
		HashMap<Integer, Employee> employees;
		BufferedReader br = null;

		// Check if the file exists
		if(payroll.exists()) {
			try {
				employees = new HashMap<>();
				br = new BufferedReader(new FileReader(payroll));

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
				// Validate the map for cycles and missing data
				return Main.validateMap(employees) ? employees : null;

			} catch (FileNotFoundException e) { // Catch exceptions and display errors in the console
				System.err.println("Unable to find file '" + fileName + "'.");
				// e.printStackTrace();
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
		} else {
			System.err.println("File '" + fileName + "' doesn't exist.");
		}
		return null;
	}

	/**
	 * Prints a given employee's name and ID as well as their team's, formatted<br>
	 * with spacing depending on where they fall in the hierarchy
	 * @param employee
	 */
	public static void printEmployee(Employee employee) {
		// Add spacing to clearly display where the employee falls in the hierarchy
		for(int i = 1; i <= employee.getManagers(); i++) {
			System.out.print("  ");
		}
		// Print information and salary
		System.out.println(employee.toString() + ": " + employee.getSalary());

		// Print all of the managers's team members recursively
		if(employee.hasTeam()) {
			for(Integer id : employee.getTeam()) {
				// Recursion
				Main.printEmployee(_hierarchy.get(id));
			}
		}
	}

	/**
	 * Prints each teams cumulative salaries
	 */
	public static void printTeamSalary() {
		System.out.println("Cumulative team salaries:");

		// Set the current maximum to be overridden by the previous if it's larger
		double maximum = 0.0;
		for(Employee employee : _hierarchy.values()) {
			// Print the cumulative salary of a manager's team
			if(employee.hasTeam()) {
				double cumulative = employee.getSalary();

				for(int id : employee.getTeam()) {
					cumulative += _hierarchy.get(id).getSalary();
				}

				// Update the largest cumulative salary found
				if(cumulative > maximum) {
					maximum = cumulative;
				}
				System.out.println("\tTeam " + employee.getName() + ":\t\t" + cumulative);
			}
		}
		System.out.println("\nThe maximum cumulative team salary is " + maximum + "\n");
	}

	/**
	 * Displays which employees that have more managers than the amount of<br>
	 * employees that they manage.
	 */
	public static void printLowManagers() {
		// Find the median value of the hierarchy to compare to
		int median = _hierarchySize / 2;
		System.out.println("Managers who manage fewer employees than they have superiors:");

		for(Employee employee : _hierarchy.values()) {
			if(!employee.hasTeam()) continue;
			int teamSize = employee.getTeam().size();

			if(employee.getManagers() >= median && teamSize < median) {
				System.out.println(employee.toString() + ": " + employee.getManagers() + " superiors, manages " + teamSize + " employee(s)");
			}
		}
	}

	/**
	 * Checks the provided map of Employees for a cycle in the hierarchy
	 * Also updates the amount of managers each employee has above them
	 * @return True if a cycle is found
	 */
	public static boolean checkForCycle(Employee employee, HashMap<Integer, Employee> map) {
		Employee manager = map.get(employee.getManager());
		// Initialise whether or not a cycle has been found
		boolean cycle = false;

		while(manager != null) {
			// Check if the manager has the same ID as the employee provided as a parameter
			if(employee.getID() == manager.getID()) {
				// A cycle has been found, break from the loop
				cycle = true;
				break;
			} else {
				// Update the hierarchy while we're at it
				int count = employee.addManager();
				Main.updateHierachySize(count);

				// Set the manager variable to the current manager's manager (1 level up)
				// The while loop will stop when it reaches the top of the hierarchy as the method
				// getManager() will return 0 which doesn't exist as a key in the map
				manager = map.get(manager.getManager());
			}
		}
		// Return the result of the search
		return cycle;
	}

	/**
	 * Validates a given hierarchy for null managers and checks for cycles<br>
	 * as well as updating the number of managers above them in the hierarchy<br>
	 * and their team list 
	 * @param map Hierarchy to validate
	 * @return Returns true if the map is valid
	 */
	public static boolean validateMap(HashMap<Integer, Employee> map) {
		// Loop through every employee in the map
		for(Employee employee : map.values()) {
			// Check if manager ID hasn't been set to 0
			if(employee.hasManager()) {
				Employee manager = map.get(employee.getManager());

				if(manager != null) {
					if (Main.checkForCycle(employee, map)) {
						System.err.println("Malformed payroll: Cycle found for employee " + employee.toString());
						return false;
					} else {
						manager.addTeamMember(employee);
						continue;
					}
				} else {
					// Manager doesn't exist
					System.err.println("Malformed payroll: " + employee.getName() + "'s manager doesn't exist.");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Updates the hierarchy size if the integer passed is larger<br>
	 * than the global variable _hierarchySize
	 * @param size
	 */
	public static void updateHierachySize(int size) {
		if(size > _hierarchySize) {
			_hierarchySize = size;
		}
	}

}