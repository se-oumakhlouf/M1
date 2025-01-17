#!/usr/bin/perl

use strict;
use warnings;

my $index = 0;
open (my $fd , '<', 'passwd.txt') or die ("open: $!");

while (defined (my $ligne = <$fd>)) {
    chomp $ligne;

    # format -> login:password:uid:gid:info:home:shell

    # Question 1 : utilisateur jc
    # if ($ligne =~ m/^jc:/) {
    #     print "$ligne\n";
    # }

    # Question 2 : utilisateurs qui n'ont pas bash comme shell
    # if ($ligne !~ m/bash$/) {
    #     print "$ligne\n";
    # }

    # Question 3 : lignes en remplaçant /home/ par /mnt/home/
    # $ligne =~ s!/home/!/mnt/home/!;

    # Question 4 : supprimé password
    # $ligne =~ s/^(.*?):.*?:/$1:/;


    # Question 5 : swap login et passwd
    # $ligne =~ s/^(.*?):(.*?):/$2:$1/;

    # Question 6 : swap uid et gid
    # $ligne =~ s/^((?:.*?:){2})(.*?):(.*?):/$1:$3:$2:/;

    # Question 7 : affichez uniquement le gid
    # if (my ($gid) = $ligne =~ m/^(?:.*?:){3}(.*?):/) {
    #     print "$gid\n";
    # }

    # Question 8 :  affichez les lignes en multipliant le gid par 2 (option e)
    $ligne =~ s/^((?:.*?:){3})(.*?)(:)/$1 . $2 * 2 . $3/e;

    print "$ligne\n";
}

close($fd);