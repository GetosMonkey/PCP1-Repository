# Using the code framwork of my CSC2001 assignment:
# Maryam Abrahams
# 27 March 2025 / 23 August 2025
# Graphing program 


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

    # Print output line by line in real time
    for line in process.stdout:
        print(line, end="")  # already has newline

    process.wait()  # wait until it finishes

    if process.returncode != 0:
        raise subprocess.CalledProcessError(process.returncode, process.args)
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

def main(): 

    image_comparison = []

    serial_dir = "/home/abrmar043/Assignment - PCP1"
    parallel_dir = "/home/abrmar043/Assignment - PCP1/ABRMAR043"

    filename ='/home/abrmar043/Assignment - PCP1/Validation and Efficiency/testValues.csv'
    test_cases = load_data(filename)

    # For each test case run tests and compare images

    num_test = 1 

    for dungeon_size, searches, seed in test_cases:

        print(f"\n=== Running test (size={dungeon_size}, density={searches}, seed={seed}) ===")
        
        compile_test(serial_dir)
        compile_test(parallel_dir)
        run_test(serial_dir, [dungeon_size, searches, seed])
        run_test(parallel_dir, [dungeon_size, searches, seed])

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

        clean_test(serial_dir)
        clean_test(parallel_dir)

        num_test += 1


if __name__ == "__main__": 
    main()
