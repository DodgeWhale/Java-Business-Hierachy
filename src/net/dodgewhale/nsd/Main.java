package net.dodgewhale.nsd;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

	public static HashMap<Integer, Employee> _hierarchy;
	public static final String COMMA = ",";
	public static int _hierarchySize = 0;

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		boolean quit = false;

		// System.out.println(System.getProperty("user.dir"));
		System.out.println("Please enter the name of the payroll or 'exit' to close the program");
		System.out.println("Available files: /");

		for(String file : new File("data").list()) {
			// Check file extension
			if(file.substring(file.lastIndexOf('.') + 1).equals("csv")) {
				System.out.println("\t/" + file);
			}
		}

		while(!quit) {
			System.out.print("File: ");
			String fileName = input.nextLine().trim();

			if (fileName.equalsIgnoreCase("quit")) {
				quit = true;
				break;
			}

			_hierarchy = Main.readFile(fileName);
			if(_hierarchy != null) {
				System.out.println("Hierarchy has " + (_hierarchySize + 1) + " levels:\n");

				for(Employee employee : _hierarchy.values()) {
					// TODO Store managers in list?
					if(!employee.hasManager()) {
						Main.printEmployee(employee);
					}
				}

				System.out.println("");
				Main.printLowManagers();

				System.out.println("");
				Main.printTeamSalary();

				_hierarchy = null;
				_hierarchySize = 0;
			} else {
				// System.out.println("Malformed payroll");
			}
		}
	}

	public static HashMap<Integer, Employee> readFile(String fileName) {
		File payroll = new File("data/" + fileName);

		// Initialize employees map to return
		HashMap<Integer, Employee> employees;
		BufferedReader br = null;

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
				return Main.validateMap(employees) ? employees : null;

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
		} else {
			System.err.println("File '" + fileName + "' doesn't exist.");
		}
		return null;
	}

	public static void printEmployee(Employee employee) {
		for(int i = 1; i <= employee.getManagers(); i++) {
			System.out.print("  ");
		}
		System.out.println(employee.toString() + ": £" + employee.getSalary());

		if(employee.hasTeam()) {
			for(Integer id : employee.getTeam()) {
				Main.printEmployee(_hierarchy.get(id));
			}
		}
	}

	public static void printTeamSalary() {
		System.out.println("Cumulative team salaries:");

		double maximum = 0.0;
		for(Employee employee : _hierarchy.values()) {
			if(employee.hasTeam()) {
				double cumulative = employee.getSalary();

				for(int id : employee.getTeam()) {
					cumulative += _hierarchy.get(id).getSalary();
				}

				if(cumulative > maximum) {
					maximum = cumulative;
				}
				System.out.println("\tTeam " + employee.getName() + ":\t\t£" + cumulative);
			}
		}
		System.out.println("The maximum cumulative team salary is £" + maximum + "\n");
	}

	public static void printLowManagers() {
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
		boolean cycle = false;

		while(manager != null) {
			if(employee.getID() == manager.getID()) {
				cycle = true;
				break;
			} else {
				int count = employee.addManager();
				Main.updateHierachySize(count);

				manager = map.get(manager.getManager());
			}
		}
		return cycle;
	}

	public static boolean validateMap(HashMap<Integer, Employee> map) {
		Employee current = null;

		// Loop through every employee in the map
		for(Employee employee : map.values()) {
			// Check if manager ID hasn't been set to 0
			if(employee.hasManager()) {
				Employee manager = map.get(employee.getManager());

				if(manager != null) {
					if (Main.checkForCycle(employee, map)) {
						// yikes
						System.err.println("Malformed payroll: Cycle found for employee " + employee.toString());
						return false;
					} else {
//						manager.addTeamSalary(employee.getSalary());
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



	public static void updateHierachySize(int size) {
		if(size > _hierarchySize) {
			_hierarchySize = size;
		}
	}

}