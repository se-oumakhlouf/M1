package Anneau;

use strict;
use warnings;
use parent qw(Disque);
use overload '""' => \&dump;
use Math::Trig;

sub new {
    my ($class, $x, $y, $r,$RI) = @_;
    my $self = $class->SUPER::new($x, $y, $r);
    $self->{RI} = $RI // 0;
    return bless($self, $class);
}

sub surface {
    my ($self) = @_;
    return $self->SUPER::surface() - $self->{RI} ** 2 * pi;
}

sub dump {
    my ($self) = @_;
    return $self->SUPER::dump() . ',' . $self->{RI};
}

1;