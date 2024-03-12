from pyspark.sql import SparkSession
from energy_analysis import EnergyAnalysis
from fermate_analysis import FermateAnalysis
from energy_data_cleaning import EnergyDataCleaning
from fermate_data_cleaning import FermateDataCleaning

# Initialize Spark session
spark = SparkSession.builder.appName("YourAppName").getOrCreate()

try:
    # Clean the energy data using Kestra's file paths
    energy_data_cleaner = EnergyDataCleaning(spark, "{{inputs.files.energy_data.path}}", "{{outputs.files.cleaned_energy_data.path}}")
    cleaned_energy_df = energy_data_cleaner.clean_data()

    # Perform the energy analysis on cleaned data
    energy_analysis = EnergyAnalysis(spark, "{{outputs.files.cleaned_energy_data.path}}")
    energy_analysis.run_analysis()

    # Clean the fermate data using Kestra's file paths
    fermate_data_cleaner = FermateDataCleaning(spark, "{{inputs.files.fermate_data.path}}", "{{outputs.files.cleaned_fermate_data.path}}")
    cleaned_fermate_df = fermate_data_cleaner.clean_data()

    # Perform the fermate analysis on cleaned data
    fermate_analysis = FermateAnalysis(spark, "{{outputs.files.cleaned_fermate_data.path}}")
    fermate_analysis.run_analysis()

except Exception as e:
    print(f"An error occurred: {e}")

finally:
    # Stop the Spark session
    spark.stop()
