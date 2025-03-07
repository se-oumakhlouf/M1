#!/usr/bin/perl

use strict;
use warnings;

use MIME::Lite;

my $message = MIME::Lite->new(
    FROM => 'selym.oumakhlouf@edu.univ-eiffel.fr',
    To => 'selym.oumakhlouf@gmail.com',
    Subject => 'Test mail en perl',
    Data => "C'est envoyé ?\n", # \n TRES IMPORTANT, sinon pas d'envoi
);

$message->attach(
    Type => 'application/pdf',
    Encoding => 'base64',
    Path => '/home/2inf1/selym.oumakhlouf/Téléchargements',
    Filename => 'BDD.pdf',
);

$message->send(
    'smtp', # Protocole utilisé
    'partage.univ-eiffel.fr', # Serveur de mail contacté
    Port => 465, # Port ssmtp/smtps
    SSL => 1, # Activation du chiffrement
    AuthUser => $email_universite, # Crédentiels
    AuthPass=> $mot_de_passe,
) or die("$!");