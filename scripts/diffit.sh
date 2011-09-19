#!/bin/bash

cat $1 | sed -e 's/ but was:/\nbut was:\n/' | sed -n '1,/but was:/ { /but was:/b; 1s/^[^:]*: expected:// ; p }' >expected.txt

cat $1 | sed -e 's/ but was:/\nbut was:\n/' | sed -n '/but was:/,$ { /but was:/b; /^\tat /b; p }' >actual.txt

./diffable.sh expected.txt >expected.dff
./diffable.sh actual.txt >actual.dff
diff expected.dff actual.dff
