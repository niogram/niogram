grammar traces;

tokens {A, B, C, D, E}

a : b | c | d;

b : B | A b;

c : C | C? d;

d : D | A d;

