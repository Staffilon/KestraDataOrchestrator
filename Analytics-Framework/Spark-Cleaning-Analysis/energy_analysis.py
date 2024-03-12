from pyspark.sql import SparkSession
from pyspark.sql.functions import col, to_timestamp

class EnergyAnalysis:
    def __init__(self, spark, file_path):
        self.spark = spark
        self.file_path = file_path
        self.df = self.read_data()

    def read_data(self):
        return self.spark.read.csv(self.file_path, header=True, inferSchema=True)

    def preprocess_data(self):
        self.df = self.df.withColumn("TimeStamp", to_timestamp(col("TimeStamp"), "yyyy-MM-dd HH:mm:ss+00:00"))

    def total_consumption(self):
        return self.df.groupBy("channel").sum("Ea_Imp")

    def average_consumption(self):
        return self.df.groupBy("channel").avg("Ea_Imp")

    def max_consumption(self):
        return self.df.groupBy("channel").max("Ea_Imp")

    def run_analysis(self):
        # First, preprocess the data
        self.preprocess_data()

        # Perform various analyses
        total = self.total_consumption()
        average = self.average_consumption()
        maximum = self.max_consumption()

        total.show()
        average.show()
        maximum.show()