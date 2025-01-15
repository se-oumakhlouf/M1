#!/usr/bin/perl

use strict;
use warnings;

sub Facto {
    my ($n) = @_;
    return 1 if $n <= 1;
    return $n * Facto($n - 1);
}

sub FiboRec {
    my ($n) = @_;
    return $n if $n <= 1;
    return FiboRec($n - 1) + FiboRec($n - 2);

}

sub FiboIter {
    my ($n) = @_;
    $n //= 10;
    my @t = (0, 1);

    foreach my $i (2..$n) {
        push(@t, $t[$i - 1] + $t[$i - 2]);
    }

    return @t;
}

print "Factorielle :\n";
foreach my $i (0..10) {
    print Facto($i) . "\n";
}

print "\nFiboRec :\n";
foreach my $i (0..10) {
    print FiboRec($i) . "\n";
}

print "\nFiboIter :\n";
foreach my $i (0..10) {
    print(join(' ', FiboIter($i)). "\n");
}

my @fibo = FiboIter(@ARGV);
print "\n$fibo[-1]\n";