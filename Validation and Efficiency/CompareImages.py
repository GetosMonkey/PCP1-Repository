# Using the code framwork of my CSC2001 assignment:
# Maryam Abrahams
# 27 March 2025 / 23 August 2025
# Program for collecting and formatting results - particularly pixel image comparison

# To run: python3 "/home/abrmar043/Assignment - PCP1/Validation and Efficiency/CompareImages.py"

import matplotlib.pyplot as plt
import subprocess
import os
import time
import csv
import re
from PIL import Image, ImageChops

#____________________________________________________________________________________

def load_data(filename): 
    """
    Loading all the test case values from the CSV file
    """

    test_cases = []

    with open(filename, 'r') as file: 

        header = file.readline()
        # Parsing each line
        for line in file: 
            parts = [x.strip() for x in line.split(",")]
            dungeon_size = int(parts[0])
            searches = float(parts[1])
            seed = int(parts[2])
            # description = parts[3], have to ignore the the description
            test_cases.append((dungeon_size, searches, seed))

        return test_cases

#____________________________________________________________________________________

def compile_test(directory):
    """
    Run `make all` in the given directory to compile Java files.
    """
    subprocess.run(["make", "all"], cwd=directory, check=True)
#____________________________________________________________________________________

def run_test(directory, args):
    """
    Run `make run` in a given directory with arguments.
    Shows stdout/stderr live so you can see program output.
    """
    arg_string = " ".join(str(a) for a in args)

    # Call make run in the folder
    process = subprocess.Popen(
        ["make", "run", f"ARGS={arg_string}"],
        cwd=directory,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True
    )

    # Printing each line to an output textfile instead 
    output_text = ""
    for line in process.stdout:
        print(line, end="")  # live print
        output_text += line

    process.wait()

    if process.returncode != 0:
        raise subprocess.CalledProcessError(process.returncode, process.args)

    # Taking timing, gridpoints, number of searches and coordinates alike
    time_ms, points, num_searches, coords = format_output(output_text)
    return time_ms, points, num_searches, coords
#____________________________________________________________________________________

def clean_test(directory):
    """
    Run `make clean` in a given directory to remove compiled Java class files.
    """
    subprocess.run(["make", "clean"], cwd=directory, check=True)
#____________________________________________________________________________________

def same_image(pic1_path, pic2_path):
    """
    Check if the serial and parallel images are the same
    """
    pic1 = Image.open(pic1_path)
    pic2 = Image.open(pic2_path)

    # Subtracting 1 image from the other image: if the result is completely black -> identical
    diff = ImageChops.difference(pic1, pic2)
    return not diff.getbbox()
#____________________________________________________________________________________

def format_output(output_text):
    """
    Formatting a text file so its easier to plot my speed up graphs: 
    format: Test number, DungeonSize, Density, Seed, Serial time (ms), SerialGridPoints, Parallel time (ms) ParallelGridPoints, NumSearches, Image comparison, Coords Match
    """
    time_match = re.search(r'time:\s*(\d+)\s*ms', output_text)
    points_match = re.search(r'number dungeon grid points evaluated:\s*(\d+)', output_text)
    searches_match = re.search(r'Number searches:\s*(\d+)', output_text)
    coord_match = re.search(r'Dungeon Master.*x=([\d\.\-]+)\s*y=([\d\.\-]+)', output_text)

    time_ms = int(time_match.group(1)) if time_match else 0
    points = int(points_match.group(1)) if points_match else 0
    num_searches = int(searches_match.group(1)) if searches_match else 0
    x = float(coord_match.group(1)) if coord_match else None
    y = float(coord_match.group(2)) if coord_match else None

    return time_ms, points, num_searches, (x, y)
#____________________________________________________________________________________

def main(): 

    image_comparison = []

    serial_dir = "/home/abrmar043/Assignment - PCP1"
    parallel_dir = "/home/abrmar043/Assignment - PCP1/ABRMAR043"

    filename ='/home/abrmar043/Assignment - PCP1/Validation and Efficiency/testValues.csv'
    test_cases = load_data(filename)

    # Saving the run test to a csv file for easy parsing for our plotting program

    output_file = '/home/abrmar043/Assignment - PCP1/Validation and Efficiency/ComparisonResults.csv'
    
    with open(output_file, 'w', newline='') as csvfile: 
        writer = csv.writer(csvfile)
        # header
        writer.writerow(["Test_number", "DungeonSize", "Density", "Seed", "SerialTime_(ms)", "SerialGridPoints", "ParallelTime_(ms)", "ParallelGridPoints", "NumSearches", "ImageComparison", "CoordComparison"])

        #____________________________________________________________________________________
        # For each test case run tests and compare images
        
        num_test = 1 


        for dungeon_size, searches, seed in test_cases:

            print(f"\n=== Running test (size={dungeon_size}, density={searches}, seed={seed}) ===")
            
            compile_test(serial_dir)
            compile_test(parallel_dir)

            s_time, s_points, s_searches, s_coords = run_test(serial_dir, [dungeon_size, searches, seed])
            p_time, p_points, p_searches, p_coords = run_test(parallel_dir, [dungeon_size, searches, seed])

            s_path = os.path.join(serial_dir, "visualiseSearchPath.png")
            p_path = os.path.join(parallel_dir, "visualiseSearchPath.png")
            identical = same_image(s_path, p_path)
            
            # Wait a short moment to ensure images are created
            while not os.path.exists(s_path) or not os.path.exists(p_path):
                time.sleep(0.1)

            identical = same_image(s_path, p_path)

            # Continue
            print(f"Image comparison: {'PASS' if identical else 'FAIL'}")
            image_comparison.append(f"Test {num_test}: {'PASSED' if identical else 'FAILED'}")

            # Checking if Coordinates match
            coord_match = s_coords == p_coords

            # Writing the results to CSV
            writer.writerow([
                num_test,
                dungeon_size,
                searches,
                seed,
                s_time,
                s_points,
                p_time,
                p_points,
                s_searches if s_searches == p_searches else 0,
                'PASS' if coord_match else 'FAIL',
                'PASS' if identical else 'FAIL'
            ])

            clean_test(serial_dir)
            clean_test(parallel_dir)

            num_test += 1
        #____________________________________________________________________________________


if __name__ == "__main__": 
    main()
