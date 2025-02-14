package Soiree;

use Moose;

has capacite => ( is => 'rw', isa => 'Int' );
has potes => (  
        is => 'rw', 
        isa => 'ArrayRef[Fetard]', 
        default => sub{ [] }, 
        auto_deref => 1,
        traits => [ 'Array' ],
        handles => {
            entrer => 'push',
            expulser => 'pop',
            nbPotes => 'count',
        }
    );

sub fetes {
    my ($self) = @_;

    print "Personnes dans la soirée:\n";
    foreach my $pote ( $self->potes() ) {
        print "\t";
        $pote->boire();
        print "\t\t";
        $pote->delirer();
    }
    print "Total: " . $self->nbPotes . "\n";
}

before entrer => sub {
    my ($self, $pote) = @_;
    print "Une personne veut entrer: " . $pote->nom() . "\n";
};

after entrer => sub {
    my ($self, $pote) = @_;

    if ($self->capacite() < $self->nbPotes()) {
        my $out = $self->expulser();
        print "La soiree est déjà pleine.";
        print " Il n'y avait plus de place pour " . $out->nom . ".\n";
    } else {
        print "Bienvenue à " . $pote->nom() . "\n";
    }
};

1;