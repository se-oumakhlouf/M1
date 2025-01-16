#!/usr/bin/perl

use strict;
use warnings;

sub Modif {
    my ($texte, $ancien, $nouveau) = @_;
    my $i = index($texte, $ancien);

    while ($i != -1) {
        my $len = length($ancien);
        substr($texte, $i, $len, $nouveau);
        $i = index($texte, $ancien, $i + length($nouveau));
    }

    return $texte;
}


my $new = Modif('bonjour vous, bonjour', 'bonjour', 'allo');
print "$new\n";

my $spicy = Modif('bonjour vous, bonjour', 'bonjour', 'bonjour bonjour');
print "$spicy\n";