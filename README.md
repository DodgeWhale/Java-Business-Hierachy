# Java Business Hierachy
# Marcus Wadge-Dale B7034135
# Software Engineering Group 2

Project contents:
/src
	Employee.java
		Employee object containing their ID, name, salary and manager's ID (where applicable)
	Main.java
		Contains all of the methods for reading a file (CSV), converting it into a HashMap hierarchy,
		validating it and printing information about said hierarchy. Catches errors for invalid (malformed) payrolls
		and displays them to the user.
/data
	Payroll CSV files which the program looks in after providing a file name

Compilation:
	- For the command line, go root folder of the project and run the following: javac -d out\ src\net\dodgewhale\nsd\*.java
	  (If the 'out' directory doesn't exist from zipping, use the command "mkdir out" to create it)
	- For Eclipse, import the project then go to Project > Clean... which will rebuild all the class files
	
Running:
	- From the root folder run the command: java -cp out\ net.dodgewhale.nsd.Main
	  to test the program.
	- For Eclipse (after cleaning the project), go to Run > Run (Ctrl+F11)