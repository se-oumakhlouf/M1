#!/usr/bin/perl

use strict;
use warnings;

# format : 
# 164.84.203.51 - - [18/Mar/2038:06:22:33 +0100] "GET /enigmes/ HTTP/1.1" 200 16413 "http://cednad.com/primaire/mathematiques.htm" "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Orange 8.0; NaviWoo2.0; .NET CLR 1.1.4322)"

my $nb_ligne = 0;
my $nb_errors = 0; # statut autre que 200;
my $sum_volume = 0;
my %access = ();

open (my $fd , '<', 'access_log.txt') or die ("open: $!");
while (defined (my $ligne = <$fd>)) {
    chomp $ligne;
    $nb_ligne++;

    if (my ($ip, $url, $status, $volume) = $ligne =~  m/^(.*?) .*?".*? (.*?) .*?" (\d{3}) (.*?) /) {
        $sum_volume += $volume;
        $access{$url}++;
        if ($status != 200) {
            $nb_errors++;
        }
    }
}
close($fd);
print "Nombre de lignes : $nb_ligne\n";
print "Nombre d'erreurs : $nb_errors\n";
print "Nombre total d'octets transférés : $sum_volume\n";

my $index = 0;
print "Urls dans l'ordre decroissant du nombre d'accès : \n";
foreach my $url ( sort { $access{$b} <=> $access{$a} } keys %access) {
    print "$url -> $access{$url}\n";
    $index++;
    last if $index == 4;
}
