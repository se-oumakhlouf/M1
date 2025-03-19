#!/usr/bin/perl

use strict;
use warnings;
use CGI;
use DBI;

my $source = 'dbi:Pg:host=sqledu.univ-eiffel.fr;dbname=selym.oumakhlouf_db';
my $base = DBI->connect($source, 'selym.oumakhlouf', '.SelymBmx15o.') or die($DBI::errstr);
my $del = 'delete from annuaire where prenom_nom = ?;';
my $req = $base->prepare($del) or die ($DBI::errstr);

my $query = CGI->new();
my $pn = $query->param('prenom_nom');
$req->execute($pn) or die ($DBI::errstr);

$base->disconnect();