from pyspark.sql import SparkSession
from pyspark.sql.functions import col, to_timestamp

class EnergyDataCleaning:
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

        # Convert 'TimeStamp' column to datetime
        df = df.withColumn("TimeStamp", to_timestamp("TimeStamp", "yyyy-MM-dd HH:mm:ss+00:00"))

        # Sort by 'channel' and then by 'TimeStamp'
        df = df.orderBy("channel", "TimeStamp")

        # Filter out rows where 'Ea_Imp' is null
        df_clean = df.filter(df["Ea_Imp"].isNotNull())

        # Show cleaned statistics
        cleaned_count = df_clean.count()
        print(f"Cleaned data count: {cleaned_count}")
        print(f"Number of removed rows due to null values: {initial_count - cleaned_count}")

        # Save the sorted and cleaned DataFrame to a CSV file
        df_clean.write.csv(self.output_file_path, header=True, mode='overwrite')

        print(f"Sorted and cleaned CSV saved to {self.output_file_path}")

        # Return the cleaned DataFrame for further processing if needed
        return df_clean
