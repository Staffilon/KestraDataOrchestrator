import pandas as pd
from datetime import datetime

class FermateAnalysis:
    def __init__(self, file_path):
        self.file_path = file_path
        self.df = self.read_data()

    def read_data(self):
        return pd.read_csv(self.file_path)

    def preprocess_data(self):
        self.df['TimeStamp'] = pd.to_datetime(self.df['SHIFT_DATE']).dt.strftime('%Y-%m-%d %H:%M:%S')

    def stop_frequency(self):
        return self.df.groupby(self.df['SHIFT_DATE'].dt.strftime('%Y-%m-%d')).size().reset_index(name='count')

    def stop_duration(self):
        self.df['duration'] = (pd.to_datetime(self.df['SHIFT_END']) - pd.to_datetime(self.df['SHIFT_START'])).dt.total_seconds()
        return self.df.groupby("RESOURCE")['duration'].mean().reset_index(name='avg_duration')

    def stop_reason_count(self):
        return self.df.groupby("DESFERM").size().reset_index(name='count').sort_values(by='count', ascending=False)

    def time_series_analysis(self):
        return self.df.groupby(self.df['SHIFT_DATE'].dt.strftime('%Y-%m')).size().reset_index(name='count')

    def stop_code_distribution(self):
        return self.df.groupby("STOP_CODE").size().reset_index(name='count').sort_values(by='count', ascending=False)

    def run_analysis(self):
        # Preprocess data
        self.preprocess_data()

        # Frequency of stops
        stop_freq_df = self.stop_frequency()
        print("Stop Frequency:")
        print(stop_freq_df)

        # Duration of stops
        stop_duration_df = self.stop_duration()
        print("\nStop Duration:")
        print(stop_duration_df)

        # Count of stop reasons
        stop_reason_count_df = self.stop_reason_count()
        print("\nStop Reason Count:")
        print(stop_reason_count_df)

        # Time series analysis
        time_series_analysis_df = self.time_series_analysis()
        print("\nTime Series Analysis:")
        print(time_series_analysis_df)

        # Distribution of stop codes
        stop_code_distribution_df = self.stop_code_distribution()
        print("\nStop Code Distribution:")
        print(stop_code_distribution_df)