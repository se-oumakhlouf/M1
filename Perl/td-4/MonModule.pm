package MonModule;

use strict;
use warnings;

use parent qw(Exporter);
our @EXPORT = qw(TableMult2);

sub TableMult1 {
    my ($n) = @_;

    for (my $i = 1; $i <= $n; $i++) {
        for (my $j = 1; $j <= $n; $j++) {
            printf "%5d", $i * $j;
        }
        print "\n";
    }
}

sub TableMult2 {
    my ($n) = @_;

    foreach my $l (1..$n) {
        foreach my $c (1..$n) {
            printf "%5d", $l * $c;
        }
        print "\n";
    }
}

sub TableMult3 {
    my ($n) = @_;
    $n //= 10;
    my $s = '';

    foreach my $l (1..$n) {
        foreach my $c (1..$n) {
            $s .= sprintf "%5d", $l * $c;
        }
        $s .= "\n";
    }

    return $s;
}

1;