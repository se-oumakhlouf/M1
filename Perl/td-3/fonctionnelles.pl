#!/usr/bin/perl

use strict;
use warnings;

sub positif {
    my ($e) = @_;
    return $e > 0;
}

sub double {
    my ($e) = @_;
    return 2 * $e;
}

sub croissant {
    my ($a, $b) = @_;
    return $a <=> $b;
}

sub mygrep {

    my ($f, @t) = @_;

    my @res = ();
    foreach my $elem (@t) {
        if ($f->($elem)) {
            push(@res, $elem);
        }

    }
    return @res;
}

my @t = mygrep \&positif, 43,654,-43,34,32,-23,652,1,2,1,523;
print "@t \n";