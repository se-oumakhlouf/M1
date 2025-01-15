#!/usr/bin/perl

use strict;
use warnings;

sub Intervalle {
    my ($n, $x) = @_;

    my @t = (1..$n);
    @t = grep { $_ != $x } @t;

    return @t;
    # return grep { $_ != $x } (1..$n);
}

sub NonMult {
    my ($n, $x) = @_;
    return grep { $_ % $x != 0} (1..$n);
}

my @t = Intervalle($ARGV[0], $ARGV[1]);
print "@t\n";

@t = NonMult($ARGV[0], $ARGV[1]);
print "@t\n";