# --- !Ups

ALTER TABLE stop ADD majorstop BOOLEAN;


# --- !Downs

ALTER TABLE stop DROP majorstop;
