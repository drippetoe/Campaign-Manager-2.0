#!/usr/bin/env python

import re, sys

all = []

if len(sys.argv) != 2:
    print "Usage: %s input_file" % sys.argv[0]
    raise SystemExit

infile = sys.argv[1]
outfile = infile + ".out"

with open(outfile, "w") as outf:
    with open(infile, "r") as inf:
        for line in inf:
            number = re.sub('[^0-9]', '', line.strip())
            if len(number) > 11 or len(number) < 10:
                print "Bad number: %s" % number
                continue
            elif len(number) == 10:
                number = "1" + number
            if not number in all:
                all.append(number)

    all.sort()
    for number in all:
        outf.write("%s\n" % number)
