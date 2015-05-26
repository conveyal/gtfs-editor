# Importing sample data

Once you have an instance properly set up, you can install some test data from different places. Two of these places are:

* Google Developers website: https://developers.google.com/transit/gtfs/examples/gtfs-feed

* Google Code Repository: https://code.google.com/p/googletransitdatafeed/wiki/PublicFeeds

In the imports folder you can see a sample feed (`sample_feed.zip`), data for the city of DC (`google_transit_dc.zip`), and data for the city of San Francisco (`google_transit_sf.zip`).

For each city, you can also see the corresponding log entry when the data was imported in the server: `sample_feed.txt`, `google_transit_dc.txt`, and `google_transit_sf.txt`.

## Blank screen after import

After uploading and processing the `.zip` file, the server always ends with a blank screen. Although the logs above show that the data has been correctly imported (see also the [screenshots](Screenshots.md) of the data), we've submitted an issue to make sure this is the intended behavior: https://github.com/conveyal/gtfs-editor/issues/222
