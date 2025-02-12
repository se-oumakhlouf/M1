#!/usr/bin/perl

use strict;
use warnings;
use Data::Dumper;


sub parse {
    my ($file) = @_;
    my $ref;

    open(my $fd, '<', $file) or die("open $file: $!");

    while(defined (my $ligne = <$fd>)) {
        chomp $ligne;
        my ($login, $passwd, $uid, $gid, $info, $home, $shell) = split(/:/, $ligne);
        $ref->{$login}->{passwd} = $passwd;
        $ref->{$login}->{uid} = $uid;
        $ref->{$login}->{gid} = $gid;
        $ref->{$login}->{info} = $info;
        $ref->{$login}->{home} = $home;
        $ref->{$login}->{shell} = $shell;
    }

    close($fd);
    return $ref;
}

sub display1 {

    my ($ref) = @_;

    foreach my $login (keys %$ref) {

        print "Login: $login \n";

        foreach my $data (keys %{$ref->{$login}}) {
            
            print "\t$data: " . $ref->{$login}->{$data} . "\n";
        }

        print "\n";

    }
}

sub display2 {

    my ($ref) = @_;
                    # sort keys %$ref fonctionne aussi
    foreach my $login (sort { $a cmp $b } keys %$ref) {

        print "Login: $login \n";

        foreach my $data (keys %{$ref->{$login}}) {
            
            print "\t$data: " . $ref->{$login}->{$data} . "\n";
        }

        print "\n";

    }
}

sub display3 {

    my ($ref) = @_;

    foreach my $login (sort { $ref->{$b}->{uid} <=> $ref->{$a}->{uid} or $a cmp $b } keys %$ref) {

        print "Login: $login \n";

        foreach my $data (keys %{$ref->{$login}}) {
            
            print "\t$data: " . $ref->{$login}->{$data} . "\n";
        }

        print "\n";
    }
}

# my $ref = parse('../td-2/passwd');
my $ref = parse('/home/ens/edu-lhullier/ens/passwd');
# print Dumper($ref);
display3($ref);


