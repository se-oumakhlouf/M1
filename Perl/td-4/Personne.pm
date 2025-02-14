package Personne;

use Moose;
with 'Fetard';

has nom => ( is => 'ro', isa => 'Str' );

sub delirer {
    my ($self) = @_;
    print $self->nom . " délire !!!\n";
}

1;

