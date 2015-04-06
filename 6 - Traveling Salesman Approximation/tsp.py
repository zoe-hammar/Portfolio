#!/usr/bin/python
import sys
import math

sys.argv = ["tsp.py", "tsp_example_1.txt"]

coord = []          # list of coordinates as tuples
tour = []           # list of city identifiers in the order they are visited
tourDistance = 0

# Read coordinates from input file
if len(sys.argv) == 1:
    print "Please supply an input file as the first argument"
    sys.exit

ifname = sys.argv[1]
fi = open(ifname, "r")

for line in fi:
    line = line.strip('\n\r ')
    n, x, y = [int(i) for i in line.split()]
    # save them to a n x 2 array
    coord.append((x, y))
    # print coord[n] #test line to check tuples of coords, comment out
fi.close()


# calculate every distance and store it in an adjacency matrix
distances = [[0 for i in xrange(n+1)] for j in xrange(n+1)]

for i in xrange(n+1):
    x1, y1 = coord[i]
    for j in xrange(i+1, n+1):
        x2, y2 = coord[j]
        distances[i][j] = int( round( math.sqrt((x1-x2)**2 + (y1-y2)**2) ) )
        # also set the equivalent edge
        distances[j][i] = distances[i][j]
        
# Nearest Neighbor
# create a boolean array with n spots
visited = [0]*(n+1)

u = 0
tour.append(u)
visited[u] = 1

for i in range(1, n+1):
    nearDistance = sys.maxint
    for v in range(n+1):
        # if that neighbor hasn't already been visited and it's the closest so far
        if visited[v] == 0 and distances[u][v] < nearDistance:
            # make it new closest neighbor
            nearNeighbor = v
            nearDistance = distances[u][v]
    visited[nearNeighbor] += 1
    tour.append(nearNeighbor)
    tourDistance += nearDistance
    # repeat with the newly visited vertex
    u = nearNeighbor
# add the first vertex to the end of the tour array to complete the tour
tour.append(0)
# add that distance to the tour distance
tourDistance += distances[u][0]


# 2-opt
# for every vertex i except the starting/ending vertex
i = 0
while i < (len(tour)-2):
    # print tourDistance
    u1 = tour[i]
    u2 = tour[i+1]
    # for every vertex j after i except the ending vertex
    for j in range(i+1, len(tour)-2):
        v1 = tour[j]
        v2 = tour[j+1]
        # determine if swapping on i,j shortens the tour
        diff = ((distances[u1][v1] + distances[u2][v2]) - (distances[u1][u2] + distances[v1][v2]))
        if diff < 0:
            k = 0

            # swap vertex i+1 and j, swap i+2 and j-1, ...
            while i+1+k < j-k:
                tmp = tour[i+1+k]
                tour[i+1+k] = tour[j-k]
                tour[j-k] = tmp
                k += 1
            # adjust the running tour distance
            tourDistance += diff
            
            # restart both of the loops (i = 1)
            i = 0
            break
    i += 1
        
# when you get to this point, 2-opt cannot make any further improvements.

# remove the vertex from the tour so that it doesn't get printed
tour.pop()


# print tour distance and tour to a file
ofname = ifname + '.tour'
fo = open(ofname, "w")
fo.write(str(tourDistance) + '\n')
for item in tour:
    fo.write(str(item) + '\n')
fo.close()
