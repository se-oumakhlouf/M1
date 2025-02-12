#!/usr/bin/perl

use strict;
use warnings;

my @files = glob('~/.*');

foreach my $f (@files) {
    print $f . "\n";
}

print "\n";

@files = grep { not -x $_ } @files;

foreach my $f (@files) {
    print $f . "\n";
}

print "\n";

@files = sort {-s $a <=> -s $b} @files;

foreach my $f (@files) {
    print $f . "\n";
}

print "\n";

my @sizes = map { -s $_ } @files;

foreach my $s (@sizes) {
    print $s . "\n";
}