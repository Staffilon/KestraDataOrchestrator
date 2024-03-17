import pandas as pd
from datetime import datetime

class FermateDataCleaning:
    def __init__(self, input_file_path, output_file_path):
        self.input_file_path = input_file_path
        self.output_file_path = output_file_path

    def clean_data(self):
        # Read the initial CSV
        df = pd.read_csv(self.input_file_path)

        # Show initial statistics
        initial_count = len(df)
        print(f"Initial data count: {initial_count}")

        # Drop the duplicate columns START_DATE and END_DATE
        df = df.drop(columns=['START_DATE', 'END_DATE'])

        # Convert 'RESOURCE' to string and strip leading zeros, then convert to integer
        df['RESOURCE'] = df['RESOURCE'].astype(str).str.lstrip('0').astype(int)

        # Convert timezone from 'Europe/Rome' to 'UTC'
        df['SHIFT_DATE'] = pd.to_datetime(df['SHIFT_DATE'], format='%Y-%m-%d %H:%M:%S').dt.tz_localize('Europe/Rome').dt.tz_convert('UTC')
        df['SHIFT_START'] = pd.to_datetime(df['SHIFT_START'], format='%Y-%m-%d %H:%M:%S').dt.tz_localize('Europe/Rome').dt.tz_convert('UTC')
        df['SHIFT_END'] = pd.to_datetime(df['SHIFT_END'], format='%Y-%m-%d %H:%M:%S').dt.tz_localize('Europe/Rome').dt.tz_convert('UTC')

        # Assuming 'all_data_sorted' is a DataFrame of your sorted and cleaned energy data
        all_data_sorted = pd.read_csv("path_to_sorted_cleaned_energy_data.csv")
        all_data_sorted['channel'] = all_data_sorted['channel'].astype(str)

        # Filter to only include machines present in 'all_data_sorted'
        df = df[df['RESOURCE'].isin(all_data_sorted['channel'])]

        # Save the cleaned DataFrame to a CSV file
        df.to_csv(self.output_file_path, index=False)

        # Show final statistics
        final_count = len(df)
        print(f"Final data count: {final_count}")
        print(f"Number of rows after cleaning: {final_count - initial_count}")

        print(f"Cleaned CSV saved to {self.output_file_path}")

        # Return the cleaned DataFrame for further processing if needed
        return df
