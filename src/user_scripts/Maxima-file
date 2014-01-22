#!/usr/bin/python

import postgresql
import random
import sys
import getopt
import math
import pickle
import time
from PIL  import Image, ImageDraw

# db informations
db_name = "madlib"
db_user = "viod"
db_server = "localhost"
db_port = 5432
db_table_name = "k_means_test"
db_field_name = "coord"
db = postgresql.open("pq://" + db_user + "@" + db_server + ":" + str(db_port) + "/" + db_name)

# dataset informations
ds_max_groups = 10
ds_max_x = 300
ds_max_y = 300
group_max_elts = 1000
group_max_width = 100
group_max_height = 100

default_output_file = "clustered_data.png"
data_file = "clusters.dat"

colors = [
    (255, 0, 0), # red
    (0, 255, 0), # green
    (0, 0, 255), # blue
    (255, 255, 0), # yellow
    (0, 255, 255), # cyan
    (255, 0, 255), # pink
    (96, 0, 0), # dark_red
    (0, 96, 0), # dark_green
    (0, 0, 96), # dark_blue
    (96, 96, 96), # grey
    (0, 0, 0) # black
    ]

def create_test_table():
    """
    Create or replace the data table
    """
    try:
        db.execute("DROP TABLE IF EXISTS " + db_table_name + " CASCADE;")
    except UndefinedTableError:
        pass
    db.execute("CREATE TABLE " + db_table_name + " (" +
               "id SERIAL PRIMARY KEY, " + 
               db_field_name + " int[]" +
               ");")

def gaussian_random(lower_bound, upper_bound):
    """ 
    Generate a random number between lower_bound and upper_bound, assuming a gaussian repartition
    """
    mean = (upper_bound + lower_bound) / 2
    variance = (upper_bound - lower_bound) / 4
    x = random.gauss(mean, variance)
    while(x < lower_bound or x > upper_bound):
        x = random.gauss(mean, variance)
    return int(x)

def insert_random_data(nb_groups):
    """
    Populate the table with groups of points chosen randomly
    """
    clusters = []

    # for each group
    for i in range(nb_groups):
        width = random.randint(1, group_max_width)
        height = random.randint(1, group_max_height)
        nb_elts = random.randint(1, group_max_elts)
        min_x = random.randint(1, ds_max_x - width)
        min_y = random.randint(1, ds_max_y - height)
        clusters.append( ((min_x + width/2, min_y + height/2), []) )

        # points generation
        for j in range(nb_elts):
            x = gaussian_random(min_x, min_x + width)
            y = gaussian_random(min_y, min_y + height)
            clusters[i][1].append((x,y))
            db.execute("INSERT INTO " + db_table_name + " (" + db_field_name + ") VALUES (" +
                       "'{" + str(x) + "," + str(y) + "}');")

    # save clusters informations in a file
    data_dump = open(data_file, "wb")
    pickle.dump(nb_groups, data_dump)
    pickle.dump(clusters, data_dump)
    data_dump.close()
    return clusters

def get_points():
    """
    Get back the points previously generated
    """
    c = db.prepare("SELECT " + db_field_name + " FROM " + db_table_name + ";").declare()
    points = []
    for p in c:
        points.append(list(p[0]))
    return points

def apply_clustering_kmeans(nb_groups): 
    """
    Call to MADlib's k-means clustering function
    """
    c = db.prepare("SELECT * FROM madlib.kmeans_random('" + db_table_name + "', '" + 
                    db_field_name + "', " + str(nb_groups) + ");").declare()
    result = c.read()[0]
    centroids = result[0]
    #objective_fn = result[1]
    #frac_reassigned = result[2]
    #num_iterations = result[3]

    # init clusters
    clusters = []
    for c in centroids:
        clusters.append((c, []))

    # assign each point to its cluster
    points = get_points()
    for p in points:
        # compute distances
        distances = []
        for c in centroids:
            distances.append(math.pow(c[0] - p[0], 2) + math.pow(c[1] - p[1], 2))
        # get the indice of the nearest centroid
        nearest = 0
        for i in range(1, len(distances)):
            if(distances[i] < distances[nearest]):
                nearest = i
        clusters[nearest][1].append(p)

    return clusters

def apply_clustering_kmeanspp(nb_groups): 
    """
    Call to MADlib's k-means clustering function
    """
    c = db.prepare("SELECT * FROM madlib.kmeanspp('" + db_table_name + "', '" + 
                    db_field_name + "', " + str(nb_groups) + ");").declare()
    result = c.read()[0]
    centroids = result[0]
    #objective_fn = result[1]
    #frac_reassigned = result[2]
    #num_iterations = result[3]

    # init clusters
    clusters = []
    for c in centroids:
        clusters.append((c, []))

    # assign each point to its cluster
    points = get_points()
    for p in points:
        # compute distances
        distances = []
        for c in centroids:
            distances.append(math.pow(c[0] - p[0], 2) + math.pow(c[1] - p[1], 2))
        # get the indice of the nearest centroid
        nearest = 0
        for i in range(1, len(distances)):
            if(distances[i] < distances[nearest]):
                nearest = i
        clusters[nearest][1].append(p)

    return clusters

def export_to_png(clusters):
    """
    Visualize the result in a PNG file
    """
    def display_centroid(bitmap, x, y, color):
        """ 
        Display a big colored square to represent a centroid
        """
        # Draw a black square

        # vertical lines
        for i in max(0, int(x)-3), min(ds_max_x, int(x)+3):
            for j in range(max(0,int(y)-3),min(ds_max_y,int(y)+4)):
                bitmap[j * ds_max_x + i] = colors[10] # black
        # horizontal lines
        for i in range(max(0,int(x)-3), min(ds_max_x,int(x)+4)):
            for j in max(0,int(y)-3), min(ds_max_y, int(y)+3):
                bitmap[j * ds_max_x + i] = colors[10] # black

        # Fill this square with the color
        for i in range(max(0, int(y)-2), min(ds_max_y, int(y)+3)):
            for j in range(max(0, int(x)-2), min(ds_max_x, int(x)+3)):
                bitmap[i * ds_max_x + j] = color

    bitmap = [(255,255,255)] * ds_max_x * ds_max_y

    i = 0
    for centroid, points in clusters:
        # display points
        for p in points:
            bitmap[p[1] * ds_max_x + p[0]] = colors[i]
        # display centroid
        display_centroid(bitmap, centroid[0], centroid[1], colors[i])
        i += 1

    img = Image.new("RGB", (ds_max_x, ds_max_y))
    img.putdata(bitmap)
    return img

def parse_args(argv):
    """
    Interpret the command line
    """
    try:
        opts, args = getopt.getopt(argv, "ho:rn:", 
                                   ["regen", "help", "output-file=", "nb-groups="])
    except getopt.GetOptError:
        usage()
        sys.exit(2)

    regen = False
    nb_groups = 0
    output_file = default_output_file
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            usage()
            sys.exit(0)
        elif opt in ("-o", "--output-file"):
            output_file = arg
        elif opt in ("-r", "--regen"):
            regen = True
        elif opt in ("-n", "--nb-groups"):
            nb_groups = arg

    return regen, nb_groups, output_file

def generate_output(output_file, clusters_set):
    """
    Display all the clustering results on a single image
    """
    def add_title(img, title):
        draw = ImageDraw.Draw(img)
        draw.text((10, 10), description, fill=colors[10]) # black

    result_img = Image.new("RGB", (ds_max_x * len(clusters_set), ds_max_y))

    i = 0
    for clusters, description in clusters_set:
        tmp_img = export_to_png(clusters)
        add_title(tmp_img, description)
        result_img.paste(tmp_img, (i * (ds_max_x + 1), 0))
        i += 1
    result_img.save(output_file)

def print_line(line):
    """
    Same as print, but allows to rewrite at the end of the line
    """
    print(line, end = "")
    sys.stdout.flush()

def count_points(clusters):
    """
    Counts the points in a cluster set
    """
    nb_points = 0
    for c in clusters:
        nb_points += len(c[1])
    return nb_points

def usage():
    print("""
Usage:
    ./k-means_test.py -o output_file.png -n 4 -r

Options:
    -o, --output-file output_file.png:
        The resulting PNG image.
    -r, --regen:
        Generate new points. You should use it at your first run.
    -n, --nb-groups n:
        Generate n groups of points. If not generating points, classify in n 
        clusters. 
    -h, --help:
        Display this help message.
""")
          
def main(args):
    regen, nb_groups, output_file = parse_args(args)

    if(regen):
        nb_groups = random.randint(2, ds_max_groups)
        print("Creating test table...")
        create_test_table()
        print_line("Generating random data... ")
        start = time.time()
        original_clusters = (insert_random_data(nb_groups), "Original clustering")
        finish = time.time()

        # nb_points = 0
        # for cluster in original_clusters[0]:
        #     nb_points += len(cluster[1])
        print("Generated " + str(count_points(original_clusters[0])) + " points partitioned into " + 
              str(len(original_clusters[0])) + " clusters in " +
              str(finish - start)[:6] + " seconds.")
    else:
        try:
            print_line("Loading data from " + data_file + "... ")
            start = time.time()
            data_dump = open(data_file, "rb")
            nb_groups = pickle.load(data_dump)
            original_clusters = (pickle.load(data_dump), "Original clustering")
            data_dump.close
            finish = time.time()
            
            print("Data loaded in " + str(finish - start)[:5] + " seconds.")
        except FileNotFoundError:
            print("Cannot load data, you need to generate some data first. Use --regen argument.")
            exit(3)

    # k-means clustering
    print_line("Clustering data using k-means algorithm... ")
    start = time.time()
    kmeans_clusters = (apply_clustering_kmeans(nb_groups), "K-means clustering")
    finish = time.time()
    print("Data clustered in " + str(finish - start)[:5] + " seconds.")

    # k-means++ clustering
    print_line("Clustering data using k-means++ algorithm... ")
    start = time.time()
    kmeanspp_clusters = (apply_clustering_kmeanspp(nb_groups), "K-means++ clustering")
    finish = time.time()
    print("Data clustered in " + str(finish - start)[:5] + " seconds.")

    # output generation
    print_line("Exporting to " + output_file + "...")
    start = time.time()
    generate_output(output_file, [ original_clusters, kmeans_clusters, kmeanspp_clusters])
    finish = time.time()
    print("File generated in " + str(finish - start)[:5] + " seconds.")

    print("Done.")

if(__name__ == "__main__"):
    main(sys.argv[1:])
