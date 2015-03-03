-- Create a database dump
\copy agency TO 'agency.csv' WITH CSV HEADER;
\copy route TO 'route.csv' WITH CSV HEADER;
\copy (SELECT *, ST_X(location) lon, ST_Y(location) lat FROM stop) to 'stop.csv' WITH CSV HEADER;
\copy routetype TO 'routetype.csv' WITH CSV HEADER;
\copy scheduleexception TO 'exception.csv' WITH CSV HEADER;
\copy scheduleexception_dates TO 'exception_dates.csv' WITH CSV HEADER;
\copy scheduleexception_servicecalendar TO 'exception_calendars.csv' WITH CSV HEADER;
\copy servicecalendar TO 'servicecalendar.csv' WITH CSV HEADER;
\copy trip TO 'trip.csv' WITH CSV HEADER;
\copy trippattern TO 'trippattern.csv' WITH CSV HEADER;
\copy trippatternstop TO 'patternstop.csv' WITH CSV HEADER;
\copy stoptime TO 'stoptime.csv' WITH CSV HEADER;
\copy account TO 'account.csv' WITH CSV HEADER;
\copy (SELECT id, ST_AsText(shape) shape FROM tripshape) TO 'shapes.csv' WITH CSV HEADER;
