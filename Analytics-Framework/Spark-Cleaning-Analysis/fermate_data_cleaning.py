from pyspark.sql import SparkSession
from pyspark.sql.functions import col, to_timestamp, expr


class FermateDataCleaning:
    def __init__(self, spark, input_file_path, output_file_path):
        self.spark = spark
        self.input_file_path = input_file_path
        self.output_file_path = output_file_path

    def clean_data(self):
        # Read the initial CSV
        df = self.spark.read.csv(self.input_file_path, header=True, inferSchema=True)

        # Show initial statistics
        initial_count = df.count()
        print(f"Initial data count: {initial_count}")

        # Drop the duplicate columns START_DATE and END_DATE
        df = df.drop('START_DATE', 'END_DATE')

        # Convert 'RESOURCE' to string and strip leading zeros, then convert to integer
        df = df.withColumn('RESOURCE', expr("int(ltrim(RESOURCE, '0'))"))

        # Convert timezone from 'Europe/Rome' to 'UTC'
        df = df.withColumn('SHIFT_DATE', to_timestamp('SHIFT_DATE', 'yyyy-MM-dd HH:mm:ss').cast('timestamp'))
        df = df.withColumn('SHIFT_START', to_timestamp('SHIFT_START', 'yyyy-MM-dd HH:mm:ss').cast('timestamp'))
        df = df.withColumn('SHIFT_END', to_timestamp('SHIFT_END', 'yyyy-MM-dd HH:mm:ss').cast('timestamp'))

        # Assuming 'all_data_sorted' is a DataFrame of your sorted and cleaned energy data
        all_data_sorted = self.spark.read.csv("path_to_sorted_cleaned_energy_data.csv", header=True, inferSchema=True)
        all_data_sorted = all_data_sorted.withColumn('channel', col('channel').cast('string'))

        # Filter to only include machines present in 'all_data_sorted'
        df = df.join(all_data_sorted, df.RESOURCE == all_data_sorted.channel, 'inner')

        # Save the cleaned DataFrame to a CSV file
        df.write.csv(self.output_file_path, header=True, mode='overwrite')

        # Show final statistics
        final_count = df.count()
        print(f"Final data count: {final_count}")
        print(f"Number of rows after cleaning: {final_count - initial_count}")

        print(f"Cleaned CSV saved to {self.output_file_path}")

        # Return the cleaned DataFrame for further processing if needed
        return df


# Initialize Spark session and specify app name and master as needed
spark = SparkSession.builder.appName("FermateDataCleaning").getOrCreate()

# Create an instance of the cleaning class with the appropriate file paths
fermate_cleaner = FermateDataCleaning(spark, "path_to_your_all_uncleaned_fermate.csv",
                                      "path_to_your_output_cleaned_fermate_data.csv")

# Perform the cleaning process
cleaned_fermate_df = fermate_cleaner.clean_data()

# Stop the Spark session when done
spark.stop()
