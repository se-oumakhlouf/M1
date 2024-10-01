#!/usr/bin/python3
# -*- coding: utf-8 -*-
'''
dico.py: look for words in a dictionnary

Usage: dico.py dictionnaire mot1 mot2 ...
Exemple: dico.py /usr/share/dict/american-english Luke, I am your father. NotaWord !
'''

import sys

f = open(sys.argv[1])
data = f.read()
f.close()

for string in sys.argv[2:]:
    if string in data :
        print(string + " is in the dictionnary")
    else :
        print(string + " isn't in the dictionnary")
