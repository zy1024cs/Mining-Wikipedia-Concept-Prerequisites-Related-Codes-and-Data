import numpy as np
from scipy.sparse import csr_matrix, find

def _read_directed_graph(X):
    row  = X[:, 0]
    col  = X[:, 1]
    data = X[:, 2]

    n = int(np.amax(X[:, 0:2]) + 1) # assume id starts from 0

    A = csr_matrix((data, (row, col)), shape=(n, n))
    return A

def _read_undirected_graph(X):
    _A = _read_directed_graph(X)
    A = _A + _A.T
    I, J, K = find(A)

    A = csr_matrix((np.ones(len(K)), (I, J)), shape=A.shape)

    return A

def read_graph(path, graph_type):
    '''
    Read the graph from the path

    inputs
        path : str
            path for the graph
        graph_type : str
            type of graph {'directed', 'undirected', 'bipartite'}
    outputs
        A : csr_matrix
            sparse adjacency matrix
        base : int
            base of node ids of the graph
    '''
    X = np.loadtxt(path, dtype=float, comments='#')

    m, n = X.shape

    if n == 2:
        # the graph is unweighted
        X = np.c_[ X, np.ones(m) ]
    elif n <= 1 or n >= 4:
        # undefined type, invoerror
        raise FormatError('Invalid input format')

    base = np.amin(X[:, 0:2])
    max_weight = np.amin(X[:, 2])

    if base < 0:
        raise ValueError('Out of range of node ids: negative base')
    # if min_weight < 0:
    #     raise ValueError('Negative edge weights')

    X[:, 0:2] = X[:, 0:2] - base

    if graph_type is "directed":
        A = _read_directed_graph(X)
    elif graph_type is "undirected":
        A = _read_undirected_graph(X)
    elif graph_type is "bipartite":
        pass
    #    A = _read_bipartite_graph(X)
    else:
        raise ValueError('graph_type sould be directed, undirected, or bipartite')

    # print(A.count_nonzero())

    return A, base.astype(int)
