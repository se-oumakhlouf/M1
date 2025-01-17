#!/usr/bin/perl

use strict;
use warnings;

my %days = (janvier => 31,
            fevrier => 28,
            mars => 30,
            avril => 30);

foreach my $month (@ARGV) {
    if (exists $days{$month}) {
        print "$month a $days{$month} jours\n";
    } else {
        print "$month n'est pas valide\n";
    }
}

delete $days{fevrier};
