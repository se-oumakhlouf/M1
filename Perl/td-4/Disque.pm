package Disque;

use strict;
use warnings;
use Math::Trig;
use overload '""' => \&dump;

sub new {
    my ($class, $x, $y, $r) = @_;
    my $self = {};
    $self->{X} = $x // 0;
    $self->{Y} = $y // 0;
    $self->{rayon} = $r // 1;

    return bless($self, $class);
}

sub surface {
    my ($self) = @_;
    return $self->{rayon} ** 2 * pi;
}

sub dump {
    my ($self) = @_;
    return '' . ref($self) . ':' . $self->{X} . ',' . $self->{Y} . ',' . $self->{rayon};
}

1;