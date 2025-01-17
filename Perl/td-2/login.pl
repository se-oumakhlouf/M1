#!/usr/bin/perl

use strict;
use warnings;

my %uids = ();

open (my $fd , '<', 'passwd.txt') or die ("open: $!");
while (defined (my $ligne = <$fd>)) {
    chomp $ligne;
    my ($login, undef, $uid) = split (/:/, $ligne);
    $uids{$login} = $uid;
}
close($fd);

print "Basic print : login -> uid\n";
foreach my $login (keys %uids) {
    print "$login -> $uids{$login}\n";
}

print "\nSorted print (on login)\n";
foreach my $login (sort keys %uids) {
    print "$login -> $uids{$login}\n";
}

print "\nSorted print (on uid)\n";
foreach my $login (sort { $uids{$a} <=> $uids{$b} } keys %uids) {
    print "$uids{$login} <- $login\n";
}

print "\nDouble sort:\n";
foreach my $login ( sort { $uids{$a} <=> $uids{$b} or $a cmp $b } keys %uids) {
    print "$login -> $uids{$login}\n";
}
