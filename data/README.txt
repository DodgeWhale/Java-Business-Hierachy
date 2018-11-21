TEST DATA to go with Assignment 1
=================================


FILES

* payroll1.csv   well formed payroll data (see decription below)

* payroll2.csv   not well formed, cylic payroll data:
                   Lee,531533 reports to Harvey,377894;
		   Harvey,377894 reports to Oswald,143790;
		   Oswald,143790 reports to Lee,531533.

* payroll3.csv   not well formed, incomplete payroll data:
                   The line manager of Harvey,377894 does not exist.

* README.txt     this file


HIERARCHY of payroll1.csv

The hierarchy has 5 levels, visualised below (including salaries).

* Emil,517233,84240.73
  * Irvine,744111,29772.48
  * Ran Dom,176428,8299.96
  * Marge,510650,37976.57
  * Williams,335333,13237.23
  * Sam,196729,12662.38
* Tanya,467545,93888.19
  * Master J,450990,81766.91
    * Sam,307751,55330.61
    * Harry,218968,99439.23
    * Sr Jr,656730,26723.54
      * Jr Sr,687428,15705.69
    * Shirley,873103,86525.58
    * Fitz,408112,99628.50
  * Dr Dr,950399,24211.71
    * Oswald,143790,1898.76
      * Harvey,377894,3816.75
        * Lee,531533,1462.60

Managers who manage fewer employees than they have superiors:
* Sr Jr,656730 (2 superiors, manages 1 employee)
* Oswald,143790 (2 superiors, manages 1 employee)
* Harvey,377894 (3 superiors, manages 1 employee)

Cumulative team salaries:
* Team Emil:     186189.35
* Team Tanya:    199866.81
* Team Master J: 449414.37
* Team Sr Jr:     42429.23
* Team Dr Dr:     26110.47
* Team Oswald:     5715.51
* Team Harvey:     5279.35

The maximum cumulative team salary is 449414.37.


WHAT YOUR APPLICATION SHOULD REPORT

For payroll1.csv, your application should report:
* Managers who manage fewer employees than they have superiors:
  Sr Jr,656730; Oswald,143790; Harvey,377894
* Maximum cumulative team salary:
  449414.37

Your application should reject payroll2.csv and payroll3.csv.
An error message should indicate the reason for rejecting the input.
