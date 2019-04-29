# this basic analysis of source data is helpful for optimizing the loading process
import os


def main():
    DATA_LOCATION = os.environ['CTD2_DATA_HOME']+"\\submissions"
    for x in os.listdir(DATA_LOCATION):
        for filename in os.listdir(DATA_LOCATION+"\\"+x):
            if not filename.endswith(".txt"):
                continue
            absolute_path = DATA_LOCATION+"\\"+x+"\\"+filename
            if os.path.isdir(absolute_path):
                continue
            with open(absolute_path, "r") as f:
                num_lines = sum(1 for line in f)
            observation_number = num_lines - 7
            print(filename, observation_number)


if __name__ == '__main__':
    main()
