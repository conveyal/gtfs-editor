#!/usr/bin/python
# bulk-load feeds to the GTFS Editor

from sys import argv
from getpass import getpass
import urllib2
from urllib import urlencode
from cookielib import CookieJar
from poster.encode import multipart_encode
from poster.streaminghttp import register_openers

server = argv[-1]

if len(argv) < 3:
    print 'usage: %s feed.zip [feed2.zip feed3.zip . . . feedn.zip] http://localhost:9000/' % argv[0]

# log in to the server
print 'Please authenticate'
uname = raw_input('username: ')
pw = getpass('password: ')

# strip trailing slash to normalize url
server = server if not server.endswith('/') else server[:-1]

# cookie handling
# http://www.techchorus.net/using-cookie-jar-urllib2
# and http://stackoverflow.com/questions/1690446
cj = CookieJar()
opener = register_openers()
opener.add_handler(urllib2.HTTPCookieProcessor(cj))

# authenticate
opener.open(server + '/secure/authenticate', urlencode(dict(username=uname, password=pw)))

# load each feed
for feed in argv[1:-1]:
    print 'processing feed %s' % feed,

    # upload the feed
    data, head = multipart_encode(dict(gtfsUpload=open(feed, 'rb')))
    req = urllib2.Request(server + '/application/uploadgtfs', data, head)
    opener.open(req)

    print 'done'
