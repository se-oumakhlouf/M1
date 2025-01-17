#!/usr/bin/perl

use strict;
use warnings;

# format : 
# 164.84.203.51 - - [18/Mar/2038:06:22:33 +0100] "GET /enigmes/ HTTP/1.1" 200 16413 "http://cednad.com/primaire/mathematiques.htm" "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Orange 8.0; NaviWoo2.0; .NET CLR 1.1.4322)"

my $nb_ligne = 0;
my $nb_errors = 0; # statut autre que 200;
my $sum_volume = 0;
my %access_url = ();
my %access_ip = ();
my %ip_volume = ();

open (my $fd , '<', 'access_log.txt') or die ("open: $!");
while (defined (my $ligne = <$fd>)) {
    chomp $ligne;
    $nb_ligne++;

    if (my ($ip, $url, $status, $volume) = $ligne =~  m/^(.*?) .*?".*? (.*?) .*?" (\d{3}) (.*?) /) {
        $sum_volume += $volume;
        $access_url{$url}++;
        $access_ip{$ip}++;
        $ip_volume{$ip} += $volume;
        if ($status != 200) {
            $nb_errors++;
        }
    }
}
close($fd);
print "\nNombre de lignes : $nb_ligne\n";
print "Nombre d'erreurs : $nb_errors\n";
print "Nombre total d'octets transférés : $sum_volume\n";

my $index_url = 0;
my $limit_url = 5;
print "\nUrls dans l'ordre decroissant du nombre d'accès (limit = $limit_url) : \n";
foreach my $url ( sort { $access_url{$b} <=> $access_url{$a} } keys %access_url) {
    print "$url : access=$access_url{$url}\n";
    $index_url++;
    last if $index_url == $limit_url;
}

my $index_ip = 0;
my $limit_ip = 10;
print "\nIP ayant accédé le plus de fois au serveur (limit = $limit_ip) :\n";
foreach my $ip ( sort { $access_ip{$b} <=> $access_ip{$a} } keys %access_ip) {
    print "$ip : access=$access_ip{$ip} volume=$ip_volume{$ip}\n";
    $index_ip++;
    last if $index_ip == $limit_ip;
}
