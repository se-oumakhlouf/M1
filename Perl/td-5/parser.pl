#!/usr/bin/perl

use strict;
use warnings;

use POSIX qw(strftime);

use MIME::Parser;
use MIME::Entity;
use MIME::Base64;
use Date::Manip;

my $parser = MIME::Parser->new();
$parser->output_dir('/tmp');
my $mime = $parser->parse_open('courriel.txt');

my $from = $mime->get('From');
my $date = $mime->get('Date');
my $sujet = $mime->get('Subject');

print "$from\n";
print "$date\n";
print "$sujet\n";

$sujet =~ s/=\?utf-8\?b\?(.*?)\?=/decode_base64($1)/eig;
print "$sujet\n";

my $date_manip = Date::Manip::Date->new;
my $err = $date_manip->parse($date);
my $seconds = $date_manip->printf('%s');


{
    local $ENV{TZ} = 'Europe/Paris';
    print "France: " . strftime('%c', localtime($seconds)) . "\n";
}

{
    local $ENV{TZ} = 'Europe/Moscow';
    print "Moscou: " . strftime('%c', localtime($seconds)) . "\n";
}