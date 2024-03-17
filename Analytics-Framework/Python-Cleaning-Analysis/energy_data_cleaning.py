import pandas as pd


class EnergyDataCleaning:
    def __init__(self, input_file_path, output_file_path):
        self.input_file_path = input_file_path
        self.output_file_path = output_file_path

    def clean_data(self):
        # Read the initial CSV
        df = pd.read_csv(self.input_file_path)

        # Show initial statistics
        initial_count = len(df)
        print(f"Initial data count: {initial_count}")

        # Convert 'TimeStamp' column to datetime
        df['TimeStamp'] = pd.to_datetime(df['TimeStamp'], format='%Y-%m-%d %H:%M:%S+00:00')

        # Sort by 'channel' and then by 'TimeStamp'
        df.sort_values(by=["channel", "TimeStamp"], inplace=True)

        # Filter out rows where 'Ea_Imp' is null
        df_clean = df.dropna(subset=["Ea_Imp"])

        # Show cleaned statistics
        cleaned_count = len(df_clean)
        print(f"Cleaned data count: {cleaned_count}")
        print(f"Number of removed rows due to null values: {initial_count - cleaned_count}")

        # Save the sorted and cleaned DataFrame to a CSV file
        df_clean.to_csv(self.output_file_path, index=False)

        print(f"Sorted and cleaned CSV saved to {self.output_file_path}")

        # Return the cleaned DataFrame for further processing if needed
        return df_clean


# Example usage:
input_file_path = "path/to/your/input_file.csv"
output_file_path = "path/to/your/output_file.csv"
cleaner = EnergyDataCleaning(input_file_path, output_file_path)
cleaned_df = cleaner.clean_data()
