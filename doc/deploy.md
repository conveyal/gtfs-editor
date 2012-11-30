Deploy
======

DotCloud
--------

Using the old CLI

Very first deploy
1. `dotcloud create gtfs`
2. `dotcloud push gtfs`
3. `dotcloud info gtfs.db`
4. `createdb -h <host name> -U <user name> -p <port number> gtfs_editor`
5. `dotcloud push gtfs --clean`

Subsequent deploys
1. `dotcloud push gtfs`
