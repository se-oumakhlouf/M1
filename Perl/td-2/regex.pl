#!/usr/bin/perl

use strict;
use warnings;


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
    $ligne =~ s/^(.*?):(.*?):/$2:$1/;

    print "$ligne\n";
}
close($fd);