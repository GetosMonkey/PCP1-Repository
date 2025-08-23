# Using the code framwork of my CSC2001 assignment:
# Maryam Abrahams
# 27 March 2025 / 23 August 2025
# Graphing

# To run: python3 "/home/abrmar043/Assignment - PCP1/Validation and Efficiency/Graphing.py"

import matplotlib.pyplot as plt
import numpy as np 

def load_data(filename): 

    test_n = []
    size = []
    density = []
    seed = []
    s_time = []
    s_points = []
    p_time = []
    p_points = []
    num_searches = []
    validated_image= []
    validated_coords = []

    with open(filename, 'r') as file: 

        header = file.readline()

        # Parsing each line
        for line in file: 
            parts = [x.strip() for x in line. split (",")]
            
            test_n.append(int(parts[0]))
            size.append(int(parts[1]))
            density.append(float(parts[2]))
            seed.append(int(parts[3]))
            s_time.append(int(parts[4]))
            s_points.append(int(parts[5]))
            p_time.append(int(parts[6]))
            p_points.append(int(parts[7]))
            num_searches.append(int(parts[8]))
            validated_image.append(parts[9])
            validated_coords.append(parts[10])

        return size, s_time, p_time, num_searches

def plot_data( size, s_time, p_time, num_searches): 
    
    # We want to plot speedup graphs: Benchmarking
    # Graph 1 ( x: grid size, y: speedup, where speedup = T1/Tp )
    # Graph 2 ( x: searches/density, y: speedup, where speedup = T1/Tp )
    
    speedup = [s/p if p != 0 else 0 for s, p in zip(s_time, p_time)]
    
    # Graph 1: Speedup vs Dungeon Size
    plt.figure(figsize=(10, 6))
    plt.scatter(size, speedup, c = 'pink', label = 'Speedup')
    
    # Regression line
    """plt.plot(size, speedup, color='red', label=f'Fitted line')"""
    size_sorted = np.array(sorted(size))
    coeffs = np.polyfit(size, speedup, 2)  # quadratic fit for smooth curve
    fitted = np.polyval(coeffs, size_sorted)
    plt.plot(size_sorted, fitted, color='red', label='Quadratic Fit')

    plt.xlabel('Dungeon Size (n*n)')
    plt.ylabel('Speedup (ms)')
    plt.title('Speedup vs Dungeon Size')
    plt.legend()
    plt.grid(True)
    plt.show()

    # Graph 2: Speedup vs Numberof Searches
    plt.figure(figsize=(10, 6))
    plt.scatter(num_searches, speedup, c = 'blue', label = 'Speedup')
    
    # Regression line
    """plt.plot(num_searches, speedup, color='green', label=f'Fitted line')"""
    searches_sorted = np.array(sorted(num_searches))
    coeffs2 = np.polyfit(num_searches, speedup, 2)  # quadratic fit
    fitted2 = np.polyval(coeffs2, searches_sorted)
    plt.plot(searches_sorted, fitted2, color='green', label='Quadratic Fit')
    
    plt.xlabel('Number of Searches (n)')
    plt.ylabel('Speedup (ms)')
    plt.title('Speedup vs Number of Searches')
    plt.legend()
    plt.grid(True)
    plt.show()
    
def main(): 

    filename ='/home/abrmar043/Assignment - PCP1/Validation and Efficiency/ComparisonResults.csv'
    size, s_time, p_time, num_searches = load_data(filename)

    # Plotting the data

    plot_data(size, s_time, p_time, num_searches)

if __name__ == "__main__": 
    main()

