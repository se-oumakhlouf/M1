#!/usr/bin/perl

use strict;
use warnings;
use Data::Dumper;

my $ref = {
    Pierre => { Telephone => '01.02.03.04.05',
                Adr => 'Une addresse',
                Enfants => ['Maurice', 'Michel']
            },

    Paul => {   Telephone => '0.60.70.80.9.10',
                Adr => 'Université Gustave Eiffel',
                Enfants => ['Claire', 'David']
            },

    Jacques => { Telephone => '11.12.13.14.15',
                 Adr => 'Créteil',
                 Enfants => ['Remi', 'Forax']

            },
};

# print Dumper($ref);

foreach my $p ( keys %$ref ) {

    print "Nom: " . $p . "\n";

    foreach my $data ( $p ) {

        print "\tTéléphone: " . $ref->{$p}->{Telephone} . "\n";
        print "\tAddresse: " . $ref->{$p}->{Adr} . "\n";
        print "\tEnfant: ";

        # foreach my $enfant ( @{$ref->{$p}->{Enfants}} ) {

        #     print $enfant . ", ";

        # }

        print join(', ', @{$ref->{$p}->{Enfants}}) . "\n";
        print "Nombre d'enfants: " . @{$ref->{$p}->{Enfants}} . "\n";

    }

    print "\n";

}



# Résultat du Dumper
# $VAR1 = {
#           'Jacques' => {
#                          'Enfants' => [
#                                         'Remi',
#                                         'Forax'
#                                       ],
#                          'Telephone' => '11.12.13.14.15',
#                          'Adr' => 'Créteil'
#                        },
#           'Paul' => {
#                       'Telephone' => '0.60.70.80.9.10',
#                       'Enfants' => [
#                                      'Claire',
#                                      'David'
#                                    ],
#                       'Adr' => 'Université Gustave Eiffel'
#                     },
#           'Pierre' => {
#                         'Enfants' => [
#                                        'Maurice',
#                                        'Michel'
#                                      ],
#                         'Telephone' => '01.02.03.04.05',
#                         'Adr' => 'Une addresse'
#                       }
#         };
