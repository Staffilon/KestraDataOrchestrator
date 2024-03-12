from pyspark.sql import SparkSession
from pyspark.sql.functions import col, date_format, unix_timestamp, hour, dayofweek

class FermateAnalysis:
    def __init__(self, spark, file_path):
        self.spark = spark
        self.file_path = file_path
        self.df = self.read_data()

    def read_data(self):
        return self.spark.read.csv(self.file_path, header=True, inferSchema=True)

    def preprocess_data(self):
        self.df = self.df.withColumn("TimeStamp", date_format(col("SHIFT_DATE"), "yyyy-MM-dd HH:mm:ss"))

    def stop_frequency(self):
        return self.df.groupBy(date_format(col("SHIFT_DATE"), "yyyy-MM-dd")).count()

    def stop_duration(self):
        self.df = self.df.withColumn("duration", unix_timestamp("SHIFT_END") - unix_timestamp("SHIFT_START"))
        return self.df.groupBy("RESOURCE").avg("duration")

    def stop_reason_count(self):
        return self.df.groupBy("DESFERM").count().orderBy(col("count").desc())

    def time_series_analysis(self):
        return self.df.groupBy(date_format(col("SHIFT_DATE"), "yyyy-MM")).count()

    def stop_code_distribution(self):
        return self.df.groupBy("STOP_CODE").count().orderBy(col("count").desc())

    def run_analysis(self):
        # Preprocess data
        self.preprocess_data()

        # Frequency of stops
        stop_freq_df = self.stop_frequency()
        print("Stop Frequency:")
        stop_freq_df.show()

        # Duration of stops
        stop_duration_df = self.stop_duration()
        print("Stop Duration:")
        stop_duration_df.show()

        # Count of stop reasons
        stop_reason_count_df = self.stop_reason_count()
        print("Stop Reason Count:")
        stop_reason_count_df.show()

        # Time series analysis
        time_series_analysis_df = self.time_series_analysis()
        print("Time Series Analysis:")
        time_series_analysis_df.show()

        # Distribution of stop codes
        stop_code_distribution_df = self.stop_code_distribution()
        print("Stop Code Distribution:")
        stop_code_distribution_df.show()