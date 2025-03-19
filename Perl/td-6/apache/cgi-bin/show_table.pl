#!/usr/bin/perl

use strict;
use warnings;
use DBI;

my $source = 'dbi:Pg:host=sqledu.univ-eiffel.fr;dbname=selym.oumakhlouf_db';
my $base = DBI->connect($source, 'selym.oumakhlouf', '.SelymBmx15o.') or die($DBI::errstr);

my $select = 'select prenom_nom, numero_tel from annuaire;';
my $req = $base->prepare($select) or die ($DBI::errstr);
$req->execute() or die ($DBI::errstr);

print "\nAnnuaire:\n\n";
while (my $reft = $req->fetchrow_arrayref()) {
    print "\tPrenom - Nom: $reft->[0]\n\tNumero de telephone: $reft->[1]\n\n";
}

$base->disconnect();
