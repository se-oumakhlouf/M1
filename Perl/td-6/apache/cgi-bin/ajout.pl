#!/usr/bin/perl

use strict;
use warnings;
use CGI;
use DBI;

my $source = 'dbi:Pg:host=sqledu.univ-eiffel.fr;dbname=selym.oumakhlouf_db';
my $base = DBI->connect($source, 'selym.oumakhlouf', '.SelymBmx15o.') or die($DBI::errstr);
my $insert = 'insert into annuaire(prenom_nom, numero_tel) values(?, ?);';
my $req = $base->prepare($insert) or die($DBI::errstr);
my $del = 'delete from annuaire where prenom_nom = ?;';
my $del_req = $base->prepare($del) or die ($DBI::errstr);

my $query = CGI->new();
my $pn = $query->param('prenom_nom');
my $num = $query->param('tel');
print "Content-Type: text/html; charset=utf-8\n";
print "\n";
if ( !defined($pn) or !defined($num) ) {
    print "<html>\n";
    print "<body>\n";
    print    '<form method="GET" action="/cgi-bin/ajout.pl">';
    print        'Prenom Nom : <input type="text" name="prenom_nom"><br />';
    print        'Numero de Telephone : <input type="text" name="tel"> <br />';
    print        '<input type="reset" value="Effacer">';
    print        '<input type="submit" value="Envoyer">';
    print    '</form>';
    print "<body>\n";
    print "<html>\n";
} else {
    print "Bonjour $pn dont le numéro de téléphone est $num.\n";
    $req->execute($pn, $num) or die($DBI::errstr);
}
