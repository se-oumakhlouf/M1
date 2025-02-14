#!/usr/bin/perl

use strict;
use warnings;
use Data::Dumper;
use lib '.';
use Personne;
use Soiree;
use Fetard;


my $p1 = Personne->new(nom => 'Paul', boisson => 'Gin');
print Dumper($p1) . "\n";

my $party = Soiree->new(capacite => 10);
$party->entrer($p1);
print Dumper($party) . "\n";

print "Nb Potes: " . $party->nbPotes . "\n";

$party->expulser();
$party->fetes();

print "\n";
my $p2 = Personne->new(nom => 'Sauron', boisson => 'Larmes de Frodon');
$party->entrer($p1);
$party->entrer($p2);
$party->fetes();

for (my $i = 0; $i < 7; $i++) {
    $party->entrer($p1);
}

my $p10 = Personne->new(nom => 'Garfield', boisson => 'Eau du Robinet');
my $p11 = Personne->new(nom => 'La personne en trop', boisson => 'Rien');
$party->entrer($p10);
$party->entrer($p11);

$party->fetes();