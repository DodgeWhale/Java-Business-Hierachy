package net.dodgewhale.nsd;

import java.util.ArrayList;
import java.util.List;

public class Employee {

	private String name;
	private int id;
	private double salary;

	private int managerId;
	private int managers = 0;

	private List<Integer> team = new ArrayList<>();

	// Constructor for line managers
	public Employee(String name, int id, double salary) {
		this(name, id, salary, 0);
	}

	// Constructor
	public Employee(String name, int id, double salary, int managerId) {
		this.name = name;
		this.id = id;
		this.salary = salary;
		this.managerId = managerId;
	}

	public String getName() {
		return this.name;
	}

	public int getID() {
		return this.id;
	}

	public double getSalary() {
		return this.salary;
	}

	public int getManager() {
		return this.managerId;
	}

	public boolean hasManager() {
		return this.getManager() != 0;
	}

	public int getManagers() {
		return this.managers;
	}

	public int addManager() {
		this.managers++;
		return this.managers;
	}

	public List<Integer> getTeam() {
		return this.team;
	}

	public void addTeamMember(Employee employee) {
		this.getTeam().add(employee.getID());
	}

	public boolean hasTeam() {
		return !this.getTeam().isEmpty();
	}

	@Override
	public String toString() {
		return this.getName() + " (#" + this.getID() + ")";
	}

	/**
	 * Returns a new Employee object by converting a line provided by the CSV file
	 * @param rawData Data in each column
	 * @return New Employee object
	 */
	public static Employee load(String[] rawData) {
		String name = rawData[0];

		// Catch the exceptions that may be made from parsing
		// the employee ID's (int) and salaries (double)
		try {
			int id = Integer.parseInt(rawData[1]);
			double salary = Double.parseDouble(rawData[2]);

			if (rawData.length > 3) {
				int managerId = Integer.parseInt(rawData[3]);
				return new Employee(name, id, salary, managerId);
			} else {
				// 4th column in CSV is empty = no manager
				return new Employee(name, id, salary);
			}
		} catch(NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

}