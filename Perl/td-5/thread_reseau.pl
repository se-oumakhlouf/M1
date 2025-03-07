#!/usr/bin/perl

use strict;
use warnings;
use IO::Socket;
use threads;
use threads::shared;

print "Lancement du serveur ...\n";

my $listen_socket = IO::Socket::INET->new(
    Proto=>'tcp', LocalPort=>2000, Listen=>5, Reuse=>1
) or die($@);
my $counter : shared = 1;

print "Serveur lancé\n";

sub action {
    my ($socket) = @_;
    $socket->send($counter++ . "\n");
    sleep(5);
    $socket->send($counter++ . "\n");
}

while (my $accept_socket = $listen_socket->accept()) {
    print "Nouveau client\n";
    my $thread = threads->new(\&action, $accept_socket)->detach();
    close($accept_socket);
}