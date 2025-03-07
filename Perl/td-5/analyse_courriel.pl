#!/usr/bin/perl

use strict;
use warnings;

use MIME::Parser;
use MIME::Entity;

use Mail::Box::Manager;
my $mgr = Mail::Box::Manager->new;
my $folder = $mgr -> open ( folder => 'courriels.mbox' );
foreach my $message ( $folder-> messages ) {
    # Extraction de chaque courriel
    # (à traiter avec MIME :: Parser ?)
    print $message -> string ;
}

# my $parser = MIME::Parser->new();
# $parser->output_dir('/tmp');
# my $mime = $parser->parse_open('courriels.mbox');

# my $from = $mime->get('From');
# my $date = $mime->get('Date');
# my $sujet = $mime->get('Subject');

# print "$from\n";
# print "$date\n";
# print "$sujet\n";