#!/usr/bin/perl

use strict;
use warnings;

sub Eratosthene {
    my ($n) = @_;

    my @candidats = (2..$n);
    my @premiers = ();

    while (@candidats) {
        push (@premiers, $candidats[0]);
        @candidats = grep { $_ % $candidats[0] != 0} @candidats;
    }
    return @premiers;

}

my @r = Eratosthene($ARGV[0] // 50);
print join(', ', @r) . "\n";