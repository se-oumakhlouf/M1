package Fetard;

use Moose::Role;

has boisson => (
    is => 'rw',
    isa => 'Str',
    required => 1
);

sub boire {
    my ($self) = @_;
    print "Je bois " . $self->boisson . ".\n";
}

requires 'delirer';

1;