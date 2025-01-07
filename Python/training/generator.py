bla = iter([ 3, 3, 6, -2, 7, 8, 4, 2, 7, 7, 2, 6, 1, 2, 2, 6])

# mon code
# def generator(sequence):
#     old = None
#     flow = list()
#     for x in sequence:
#         if old is None :
#             old = x
#         else :
#             if x == old :
#                 flow.append(0)
#             elif x < old :
#                 flow.append(-1)
#             elif x > old : 
#                 flow.append(1)
#             old = x
#     return flow

# flow = generator(bla)
# print(flow)


# correction
def f(g):
    a = next(g)
    while 1:
        try:
            b = next(g); x = b-a
            if x>0: yield 1
            elif x<0: yield -1
            else: yield 0
            a = b
        except StopIteration: break

flow = list(f(bla))
print(flow)