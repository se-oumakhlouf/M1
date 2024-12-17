#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Sudoku"""

from itertools import combinations
from math import sqrt

def var(i,j,k):
    """Return the literal Xijk.
    """
    return (1,i,j,k)

def neg(l):
    """Return the negation of the literal l.
    """
    (s,i,j,k) = l
    return (-s,i,j,k)

def initial_configuration():
    """Return the initial configuration of the example in td6.pdf
    
    >>> cnf = initial_configuration()
    >>> [(1, 1, 4, 4)] in cnf
    True
    >>> [(1, 2, 1, 2)] in cnf
    True
    >>> [(1, 2, 3, 1)] in cnf
    False
    """
    return [[var(1, 4, 4)], 
            [var(2, 1, 2)], 
            [var(3 ,2, 1)], 
            [var(4, 3, 1)]]

    #  [[0, 0, 0, 4],
    #   [2, 0, 0, 0],
    #   [0, 1, 0, 0],
    #   [0, 0, 1, 0]]

def at_least_one(L):
    """Return a cnf that represents the constraint: at least one of the
    literals in the list L is true.
    
    >>> lst = [var(1, 1, 1), var(2, 2, 2), var(3, 3, 3)]
    >>> cnf = at_least_one(lst)
    >>> len(cnf)
    1
    >>> clause = cnf[0]
    >>> len(clause)
    3
    >>> clause.sort()
    >>> clause == [var(1, 1, 1), var(2, 2, 2), var(3, 3, 3)]
    True
    """
    return [[l for l in L]]

def at_most_one(L):
    """Return a cnf that represents the constraint: at most one of the
    literals in the list L is true
    
    >>> lst = [var(1, 1, 1), var(2, 2, 2), var(3, 3, 3)]
    >>> cnf = at_most_one(lst)
    >>> len(cnf)
    3
    >>> cnf[0].sort()
    >>> cnf[1].sort()
    >>> cnf[2].sort()
    >>> cnf.sort()
    >>> cnf == [[neg(var(1,1,1)), neg(var(2,2,2))], \
    [neg(var(1,1,1)), neg(var(3,3,3))], \
    [neg(var(2,2,2)), neg(var(3,3,3))]]
    True
    """
    return [[neg(l) for l in lst] for lst in combinations(L, 2)]

def assignment_rules(N):
    """Return a list of clauses describing the rules for the assignment (i,j) -> k.
    """
    cnf = []
    for i in range(1,N+1):
        for j in range(1,N+1):
            # add clauses to cnf saying that (i,j) contains 
            # *exactly* one of the digits k=1..N
            cnf += at_least_one([var(i, j, k) for k in range(1, N+1)])
            cnf += at_most_one([var(i, j, k) for k in range(1, N+1)])
    return cnf

def row_rules(N):
    """Return a list of clauses describing the rules for the rows.
    """
    rows = []
    for i in range(1, N+1):
        for k in range(1, N+1) :
            rows += at_least_one([var(i, j, k) for j in range(1, N+1)])
            rows += at_most_one([var(i, j, k) for j in range(1, N+1)])
    return rows

def column_rules(N):
    """Return a list of clauses describing the rules for the columns.
    """
    cols = []
    for j in range(1, N+1):
        for k in range(1, N+1) :
            cols += at_least_one([var(i, j, k) for i in range(1, N+1)])
            cols += at_most_one([var(i, j, k) for i in range(1, N+1)])
    return cols

def subgrid_rules(N):
    """Return a list of clauses describing the rules for the subgrids.
    """
    subgrid = []
    racine = sqrt(N)
    start, end = 1, racine
    for q in range(racine):
        for l in range (racine):
            for i in range(1, racine):
                for j in range(1, racine):
                    subgrid += at_least_one([var(i + l*racine, j + q*racine, k) for k in range(1, N+1)])
                    subgrid += at_most_one([var(i + l*racine, j + q*racine, k) for k in range (1, +1)])
    return subgrid

def generate_rules(N):
    """Return a list of clauses describing the rules of the game.
    """
    cnf = []    
    cnf.extend(assignment_rules(N))
    cnf.extend(row_rules(N))
    cnf.extend(column_rules(N))
    cnf.extend(subgrid_rules(N))
    return cnf

def literal_to_integer(l, N):
    """Return the external representation of the literal l.

    >>> literal_to_integer(var(1,2,3), 4)
    7
    >>> literal_to_integer(neg(var(3,2,1)), 4)
    -37
    """
    s, i, j, k = l
    return s * ((N * N) * (i -1) + N * (j - 1) + (k - 1) +1)

def generate_cnf_file():
    with open("sudoku.cnf", 'w') as file:
        pass
    pass 


def main():
    import doctest
    doctest.testmod()

if __name__ == "__main__":
    main()
