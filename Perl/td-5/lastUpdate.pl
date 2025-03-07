#!/usr/bin/perl

use strict;
use warnings;
use POSIX qw(strftime);

# my (undef, undef, undef,undef,$uid,undef,undef,undef,
# undef,$mtime) = stat($ENV{HOME});

my ($uid, $mtime) = (stat($ENV{HOME}))[4, 9];

my $login = getpwuid($uid);

my ($sec,$min,$hour,$mday,$mon,$year,$wday) = localtime($mtime);
$year = $year + 1900;

print "Login du propriétaire : $login\n";
print "Dernière modification de $ENV{HOME} : $year/$mon/$mday $hour:$min:$sec \n";