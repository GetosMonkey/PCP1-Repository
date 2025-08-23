# Using the code framwork of my CSC2001 assignment:
# Maryam Abrahams
# 27 March 2025 / 23 August 2025
# Graphing

# To run: python3 "/home/abrmar043/Assignment - PCP1/Validation and Efficiency/Graphing.py"

import matplotlib.pyplot as plt
import numpy as np 
from scipy.optimize import curve_fit

def decay(x, a, b): 
    # where x: input, a: asymptotic max, b: growth rate
    return a * ( 1 - np.exp(-b * x))

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
    params, _ = curve_fit(decay, size, speedup, p0=[max(speedup), 0.001])
    x_smooth = np.linspace(min(size), max(size), 200)
    y_smooth = decay(x_smooth, *params)
    plt.plot(x_smooth, y_smooth, color='red', label='Decaying Growth Fit')

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
    params2, _ = curve_fit(decay, num_searches, speedup, p0=[max(speedup), 0.00001])
    x_smooth2 = np.linspace(min(num_searches), max(num_searches), 200)
    y_smooth2 = decay(x_smooth2, *params2)
    plt.plot(x_smooth2, y_smooth2, color='green', label='Decaying Growth Fit')
    
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

