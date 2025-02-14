#!/usr/bin/perl

use strict;
use warnings;
use lib '.';
use Disque;
use Anneau;
use Data::Dumper;

my $d = Disque->new();
print Dumper($d);

my $surface = $d->surface();
print "surface disque défaut: $surface\n";

my $d2 = Disque->new(2, 2, 2);
print $d2->dump()."\n";
print "$d2\n";

print "\n";
my $a1 = Anneau->new();
print Dumper($a1);

my $a2 = Anneau->new(10, 24 , 5, 2);
my $surface_a2 = $a2->surface();
print "\n";
print "a2: $a2\n";
print "surface a2: " . $surface_a2 . "\n";
