ll = [2,2,2,2,1,1,2,2,1,1,1,4,4,4,4,2]

# correction
from itertools import groupby
def occurrences(lst):
    return [(x[0], len(list(x[1]))) for x in groupby(lst)]
# print(occurrences(ll))

mm = [38, 25, 36, 36, 40, 22, 8, 26, 32, 1, 7, 31, 46, 10, 21, 41, 47, 23, 24, 22, 5, 42, 41, 24, 36, 20, 27, 46, 6, 25]


def rearrange(mm):
    return [x for x in mm if x % 2 == 1] + [x for x in mm if x % 2 == 0]
print(rearrange(mm))


def is_stricly_increasing(lst):
    return len(lst) - 1 == len([lst[i] for i in range(len(lst) - 1) if lst[i] < lst[i + 1]])

print(is_stricly_increasing([1,3,5,6,8,9,7]))
print(is_stricly_increasing([2,4,5,9,15,18,25,51]))