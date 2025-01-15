#!/usr/bin/perl

use strict;
use warnings;

my @t = (4, -5, 7);
push(@t, -2, 3);
print join(', ', @t) . "\n";

unshift(@t, 0, -1);
print join(', ', @t) . "\n";

$t[3] = 9;
print join(', ', @t) . "\n";

@t = map { $_ * 2 } @t;
print join(', ', @t) . "\n";

@t = grep { $_ > 0 } @t;
print join(', ', @t) . "\n";

@t = sort { $b <=> $a } @t;
# @t = reverse (@t);
# @t = reverse(@t);
print join(', ', @t) . "\n";