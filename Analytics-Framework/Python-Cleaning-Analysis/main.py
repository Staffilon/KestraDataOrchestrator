from energy_analysis import EnergyAnalysis
from fermate_analysis import FermateAnalysis
from energy_data_cleaning import EnergyDataCleaning
from fermate_data_cleaning import FermateDataCleaning

try:
    # Clean the energy data
    energy_data_cleaner = EnergyDataCleaning("path_to_energy_input_file.csv", "path_to_cleaned_energy_output_file.csv")
    cleaned_energy_df = energy_data_cleaner.clean_data()

    # Perform the energy analysis on cleaned data
    energy_analysis = EnergyAnalysis("path_to_cleaned_energy_output_file.csv")
    energy_analysis.run_analysis()

    # Clean the fermate data
    fermate_data_cleaner = FermateDataCleaning("path_to_fermate_input_file.csv", "path_to_cleaned_fermate_output_file.csv")
    cleaned_fermate_df = fermate_data_cleaner.clean_data()

    # Perform the fermate analysis on cleaned data
    fermate_analysis = FermateAnalysis("path_to_cleaned_fermate_output_file.csv")
    fermate_analysis.run_analysis()

except Exception as e:
    print(f"An error occurred: {e}")
