import pandas as pd


class EnergyAnalysis:
    def __init__(self, file_path):
        self.file_path = file_path
        self.df = self.read_data()

    def read_data(self):
        return pd.read_csv(self.file_path)

    def preprocess_data(self):
        self.df['TimeStamp'] = pd.to_datetime(self.df['TimeStamp'], format='yyyy-MM-dd HH:mm:ss+00:00')

    def total_consumption(self):
        return self.df.groupby("channel").agg({"Ea_Imp": "sum"})

    def average_consumption(self):
        return self.df.groupby("channel").agg({"Ea_Imp": "mean"})

    def max_consumption(self):
        return self.df.groupby("channel").agg({"Ea_Imp": "max"})

    def run_analysis(self):
        # First, preprocess the data
        self.preprocess_data()

        # Perform various analyses
        total = self.total_consumption()
        average = self.average_consumption()
        maximum = self.max_consumption()

        print("Total Consumption:")
        print(total)
        print("\nAverage Consumption:")
        print(average)
        print("\nMaximum Consumption:")
        print(maximum)
