#!/usr/bin/perl

use strict;
use warnings;

use DBI;

my $source = 'dbi:Pg:host=sqledu.univ-eiffel.fr;dbname=selym.oumakhlouf_db';
my $base = DBI->connect($source, 'selym.oumakhlouf', '.SelymBmx15o.') or die($DBI::errstr);


my $clean = 'DROP TABLE if exists annuaire CASCADE;';

my $sql = 'CREATE TABLE annuaire (
                prenom_nom VARCHAR(40),
                numero_tel VARCHAR(20)
            );';

$base->do($clean) or die($DBI::errstr);

$base->do($sql) or die($DBI::errstr);

my $insert = 'insert into annuaire(prenom_nom, numero_tel) values(?, ?);';
my $req = $base->prepare($insert);

$req->execute('Remi Forax', '01-02-03-04-05') or die($DBI::errstr);
$req->execute('Lewis Hamilton', '01-10-11-12-13') or die($DBI::errstr);
$req->execute('Max Verstappen', '07-401-503-11') or die($DBI::errstr);
$req->execute('Toto Wolf', '04-4569-33-14') or die($DBI::errstr);

my $select = 'select * from annuaire;';
$req = $base->prepare($select) or die ($DBI::errstr);
$req->execute() or die ($DBI::errstr);

while (my $reft = $req->fetchrow_arrayref()) {
    print "$reft->[0] \t$reft->[1]\n";
}

$base->disconnect();


