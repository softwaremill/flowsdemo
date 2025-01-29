# Flows Demo Project

This is a demo project for [Jox's Flow](https://github.com/softwaremill/jox?tab=readme-ov-file#lazy-streaming---flows) feature,
solving simple task of report generation.

## Task description 
Let's assume we've got a task following task to implement:
- create new REST API endpoint that will generate `csv` trading report and return it
- make sure user always gets full report
- report is a summary of bought items by `John Doe` in given day
- each row should contain total amount for given day only for one item
- report have following header
    - day - day of the summary in format `dd-MM-yyyy`
    - product - name of the product
    - amount - total amount of bought items in given day
For further reference, please consider the following sections:

## Stack
Project uses `Gradle` build tool with `Spring Boot` application and `JUnit5` testing framework.

## How to run? 

### Running Test
To run the test you can use following command in the root folder of repository:

```bash
./gradlew cleanTest test
```

or use help of you favourite IDE

### Running The Application
To run the application you can use following command in the root folder of repository:

```bash
./gradlew bootRun
```

or use help of you favourite IDE and run `FlowsDemoApplication`

#### Testing
After running application it should be available under `http://localhost:8080`.

Endpoint for report generation is configured to be `GET /report` so you can simply open the browser with address `http://localhost:8080/report` and file downloading will start.

You can also use following command if you prefer console:
```bash
 curl 'http://localhost:8080/report' > my_report.csv
```
Report will be saved in `my_report.csv` file in current directory

# Project sponsor
We offer commercial development services. [Contact us](https://softwaremill.com/) to learn more!

# Copyright
Copyright (C) 2025 SoftwareMill https://softwaremill.com.