
# Kestra Data Orchestrator


This is a Project for the Technologies for Big Data Management (TBDM) course of the UNICAM Master's Degree Program (BIDS branch)


![Logo](https://i.im.ge/2024/03/17/RPR8q8.pipeline.png)

In collaboration with the University of Camerino and Tormatic SRL, based in the Marche region of Italy, this project involves a company recognized for possessing one of the nation's most sophisticated arrays of machinery. The company has seen a consistent expansion over the years and demonstrates expertise in handling an extensive selection of materials, including steel, stainless steel, brass, aluminum, and various plastics.

## Authors

- [@Stanislav Teghipco](https://github.com/Staffilon)
- [@Fabio Michele De Vitis](https://github.com/FabioDevIsTyping)
- [@Adnane Draibine](https://github.com/Ad-Dra)


## Project Outline

Our exploration centered on a dataset provided by Tormatic SRL. The investigation unfolded in several phases:

- Initially, we engaged in feature engineering and exploratory data analysis using the raw datasets. Our tools of choice were Python and its powerful libraries such as matplotlib and pandas, which enabled us to craft various graphs and analytical solutions.

- Our research also probed the feasibility of employing the data in developing an intelligent machine learning model dedicated to predictive maintenance.
- We designed a conceptual data pipeline tailored to meet the company's specific requirements. This endeavor employed a diverse suite of technologies, including AWS, MongoDB, PySpark, Kestra, and PowerBI.We prototyped an ideal data pipeline that could work to solve the company needs. This was done by using many different technologies: AWS, MongoDB, PySpark, Kestra and PowerBI.

## Documentation
Comprehensive documentation that encompasses the source code and its detailed explanation is accessible via the following link:
[Documentation](https://linktodocumentation)

## Run Locally

### Prerequisites
Before starting, ensure you have the following installed:
- **Java**: Spark requires Java to be installed on your machine.
- **Python**: As PySpark is the Python API for Spark, make sure you have Python installed.
- **Spark**: Download and install Apache Spark.
- **Hadoop**: Spark needs Hadoop's core library. You can download Hadoop and set the necessary environment variables.
- **Git**: To clone the repository, you'll need Git installed.
### Environment Setup
- **1.** **Install Java**
Download and install Java (JDK) from Oracle's website or use OpenJDK.
Set JAVA_HOME environment variable to the path of your Java installation.
- **2. Install Python**
Download and install Python from the official website.
Ensure Python is added to your system path.
- **3. Install Spark**
Download Spark from the Apache Spark website.
Extract the Spark package to a location on your machine.
Set SPARK_HOME environment variable to this location.
Add %SPARK_HOME%\bin to your system path.
- **4. Install Hadoop (Windows users)**
Download Hadoop and extract it to a location.
Set HADOOP_HOME to this location.
Add %HADOOP_HOME%\bin to your system path.
Download winutils.exe and place it in %HADOOP_HOME%\bin.
### Clone the Project

Clone the project using git:

```bash
git clone https://github.com/Staffilon/KestraDataOrchestrator/tree/main
```

### Go to the Project Directory

Navigate to your project directory:

```bash
cd my-project
```

### Install PySpark

If PySpark is not included in the project, install it:

```bash
pip install pyspark
```

### Run the Spark Application

Run the application using `spark-submit`:

```bash
spark-submit main.py
```


## FAQ

#### Have you encountered any challenges during the execution of this project?

Indeed, we faced several challenges. Initially, we discovered that the dataset provided required extensive cleaning. We tried various strategies to merge the energy consumption data with the 'fermate' (stop) dataset, but this resulted in having only a few data points for each stop code. This scarcity of data points posed a significant challenge in developing a machine learning model that wouldn't overfit.

#### Did you find any correlations between energy consumption and the fermate dataframe?

Our analysis revealed that the most significant correlation is with the quality of the produced goods. We employed various correlation techniques, including T-Tests to compare averages and different types of correlation matrices. 

#### What recommendations would you make to the company?

We advise acquiring new and more detailed data. For a project of this nature, the quality and clarity of data are crucial. The dataset needs refinement in several areas, as outlined in our documentation. Additionally, considering we are dealing with various types of machinery, it would be beneficial to categorize these machines. This would enable us to develop specific machine learning models for each category. Unfortunately, in the current dataset, we could only identify machines by their codes without additional information. Once the data is enhanced, we can establish a pipeline for cleaning the data through an ETL process, loading it into a database, and then conducting analysis and visualizations on the refined data. Subsequently, we could develop a machine learning model with an application to recommend maintenance schedules for each machinery type.

#### What are the advantages of this approach?

The primary advantage of predictive maintenance models is the establishment of a clear maintenance schedule. This allows the maintenance personnel to know precisely when to perform specific actions. The savings are both in terms of time and human resources, which naturally translates into monetary savings, leading to a positive return on investment for the company.


## License

[MIT](https://choosealicense.com/licenses/mit/)



