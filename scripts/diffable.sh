#!/bin/bash

sed 's/@[0-9a-f]*//g' $1 | sed 's/\], /\],\n/g'
