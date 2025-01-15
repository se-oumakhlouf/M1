#!/usr/bin/perl

use strict;
use warnings;

sub SommeTest {
    my ($x, $y, $n) = @_;
    return $x . $y + $x == $n
}

if (SommeTest(@ARGV)) {
    print "Ok\n";
} else {
    print "not Ok\n";
}
