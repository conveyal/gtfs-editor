--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: box2d; Type: SHELL TYPE; Schema: public; Owner: postgres
--

CREATE TYPE box2d;


--
-- Name: box2d_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box2d_in(cstring) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_in';


ALTER FUNCTION public.box2d_in(cstring) OWNER TO postgres;

--
-- Name: box2d_out(box2d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box2d_out(box2d) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_out';


ALTER FUNCTION public.box2d_out(box2d) OWNER TO postgres;

--
-- Name: box2d; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE box2d (
    INTERNALLENGTH = 16,
    INPUT = box2d_in,
    OUTPUT = box2d_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


ALTER TYPE public.box2d OWNER TO postgres;

--
-- Name: box3d; Type: SHELL TYPE; Schema: public; Owner: postgres
--

CREATE TYPE box3d;


--
-- Name: box3d_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box3d_in(cstring) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_in';


ALTER FUNCTION public.box3d_in(cstring) OWNER TO postgres;

--
-- Name: box3d_out(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box3d_out(box3d) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_out';


ALTER FUNCTION public.box3d_out(box3d) OWNER TO postgres;

--
-- Name: box3d; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE box3d (
    INTERNALLENGTH = 48,
    INPUT = box3d_in,
    OUTPUT = box3d_out,
    ALIGNMENT = double,
    STORAGE = plain
);


ALTER TYPE public.box3d OWNER TO postgres;

--
-- Name: box3d_extent; Type: SHELL TYPE; Schema: public; Owner: postgres
--

CREATE TYPE box3d_extent;


--
-- Name: box3d_extent_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box3d_extent_in(cstring) RETURNS box3d_extent
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_in';


ALTER FUNCTION public.box3d_extent_in(cstring) OWNER TO postgres;

--
-- Name: box3d_extent_out(box3d_extent); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box3d_extent_out(box3d_extent) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_extent_out';


ALTER FUNCTION public.box3d_extent_out(box3d_extent) OWNER TO postgres;

--
-- Name: box3d_extent; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE box3d_extent (
    INTERNALLENGTH = 48,
    INPUT = box3d_extent_in,
    OUTPUT = box3d_extent_out,
    ALIGNMENT = double,
    STORAGE = plain
);


ALTER TYPE public.box3d_extent OWNER TO postgres;

--
-- Name: chip; Type: SHELL TYPE; Schema: public; Owner: postgres
--

CREATE TYPE chip;


--
-- Name: chip_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION chip_in(cstring) RETURNS chip
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_in';


ALTER FUNCTION public.chip_in(cstring) OWNER TO postgres;

--
-- Name: chip_out(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION chip_out(chip) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_out';


ALTER FUNCTION public.chip_out(chip) OWNER TO postgres;

--
-- Name: chip; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE chip (
    INTERNALLENGTH = variable,
    INPUT = chip_in,
    OUTPUT = chip_out,
    ALIGNMENT = double,
    STORAGE = extended
);


ALTER TYPE public.chip OWNER TO postgres;

--
-- Name: geography; Type: SHELL TYPE; Schema: public; Owner: postgres
--

CREATE TYPE geography;


--
-- Name: geography_analyze(internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_analyze(internal) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/postgis-1.5', 'geography_analyze';


ALTER FUNCTION public.geography_analyze(internal) OWNER TO postgres;

--
-- Name: geography_in(cstring, oid, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_in(cstring, oid, integer) RETURNS geography
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_in';


ALTER FUNCTION public.geography_in(cstring, oid, integer) OWNER TO postgres;

--
-- Name: geography_out(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_out(geography) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_out';


ALTER FUNCTION public.geography_out(geography) OWNER TO postgres;

--
-- Name: geography_typmod_in(cstring[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_typmod_in(cstring[]) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_typmod_in';


ALTER FUNCTION public.geography_typmod_in(cstring[]) OWNER TO postgres;

--
-- Name: geography_typmod_out(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_typmod_out(integer) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_typmod_out';


ALTER FUNCTION public.geography_typmod_out(integer) OWNER TO postgres;

--
-- Name: geography; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE geography (
    INTERNALLENGTH = variable,
    INPUT = geography_in,
    OUTPUT = geography_out,
    TYPMOD_IN = geography_typmod_in,
    TYPMOD_OUT = geography_typmod_out,
    ANALYZE = geography_analyze,
    ALIGNMENT = double,
    STORAGE = main
);


ALTER TYPE public.geography OWNER TO postgres;

--
-- Name: geometry; Type: SHELL TYPE; Schema: public; Owner: postgres
--

CREATE TYPE geometry;


--
-- Name: geometry_analyze(internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_analyze(internal) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_analyze';


ALTER FUNCTION public.geometry_analyze(internal) OWNER TO postgres;

--
-- Name: geometry_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_in(cstring) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_in';


ALTER FUNCTION public.geometry_in(cstring) OWNER TO postgres;

--
-- Name: geometry_out(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_out(geometry) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_out';


ALTER FUNCTION public.geometry_out(geometry) OWNER TO postgres;

--
-- Name: geometry_recv(internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_recv(internal) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_recv';


ALTER FUNCTION public.geometry_recv(internal) OWNER TO postgres;

--
-- Name: geometry_send(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_send(geometry) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_send';


ALTER FUNCTION public.geometry_send(geometry) OWNER TO postgres;

--
-- Name: geometry; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE geometry (
    INTERNALLENGTH = variable,
    INPUT = geometry_in,
    OUTPUT = geometry_out,
    RECEIVE = geometry_recv,
    SEND = geometry_send,
    ANALYZE = geometry_analyze,
    DELIMITER = ':',
    ALIGNMENT = int4,
    STORAGE = main
);


ALTER TYPE public.geometry OWNER TO postgres;

--
-- Name: geometry_dump; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE geometry_dump AS (
	path integer[],
	geom geometry
);


ALTER TYPE public.geometry_dump OWNER TO postgres;

--
-- Name: gidx; Type: SHELL TYPE; Schema: public; Owner: postgres
--

CREATE TYPE gidx;


--
-- Name: gidx_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION gidx_in(cstring) RETURNS gidx
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'gidx_in';


ALTER FUNCTION public.gidx_in(cstring) OWNER TO postgres;

--
-- Name: gidx_out(gidx); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION gidx_out(gidx) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'gidx_out';


ALTER FUNCTION public.gidx_out(gidx) OWNER TO postgres;

--
-- Name: gidx; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE gidx (
    INTERNALLENGTH = variable,
    INPUT = gidx_in,
    OUTPUT = gidx_out,
    ALIGNMENT = double,
    STORAGE = plain
);


ALTER TYPE public.gidx OWNER TO postgres;

--
-- Name: pgis_abs; Type: SHELL TYPE; Schema: public; Owner: postgres
--

CREATE TYPE pgis_abs;


--
-- Name: pgis_abs_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pgis_abs_in(cstring) RETURNS pgis_abs
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'pgis_abs_in';


ALTER FUNCTION public.pgis_abs_in(cstring) OWNER TO postgres;

--
-- Name: pgis_abs_out(pgis_abs); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pgis_abs_out(pgis_abs) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'pgis_abs_out';


ALTER FUNCTION public.pgis_abs_out(pgis_abs) OWNER TO postgres;

--
-- Name: pgis_abs; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE pgis_abs (
    INTERNALLENGTH = 8,
    INPUT = pgis_abs_in,
    OUTPUT = pgis_abs_out,
    ALIGNMENT = double,
    STORAGE = plain
);


ALTER TYPE public.pgis_abs OWNER TO postgres;

--
-- Name: spheroid; Type: SHELL TYPE; Schema: public; Owner: postgres
--

CREATE TYPE spheroid;


--
-- Name: spheroid_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION spheroid_in(cstring) RETURNS spheroid
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'ellipsoid_in';


ALTER FUNCTION public.spheroid_in(cstring) OWNER TO postgres;

--
-- Name: spheroid_out(spheroid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION spheroid_out(spheroid) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'ellipsoid_out';


ALTER FUNCTION public.spheroid_out(spheroid) OWNER TO postgres;

--
-- Name: spheroid; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE spheroid (
    INTERNALLENGTH = 65,
    INPUT = spheroid_in,
    OUTPUT = spheroid_out,
    ALIGNMENT = double,
    STORAGE = plain
);


ALTER TYPE public.spheroid OWNER TO postgres;

--
-- Name: _st_asgeojson(integer, geometry, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_asgeojson(integer, geometry, integer, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asGeoJson';


ALTER FUNCTION public._st_asgeojson(integer, geometry, integer, integer) OWNER TO postgres;

--
-- Name: _st_asgeojson(integer, geography, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_asgeojson(integer, geography, integer, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_as_geojson';


ALTER FUNCTION public._st_asgeojson(integer, geography, integer, integer) OWNER TO postgres;

--
-- Name: _st_asgml(integer, geometry, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_asgml(integer, geometry, integer, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asGML';


ALTER FUNCTION public._st_asgml(integer, geometry, integer, integer) OWNER TO postgres;

--
-- Name: _st_asgml(integer, geography, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_asgml(integer, geography, integer, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_as_gml';


ALTER FUNCTION public._st_asgml(integer, geography, integer, integer) OWNER TO postgres;

--
-- Name: _st_askml(integer, geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_askml(integer, geometry, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asKML';


ALTER FUNCTION public._st_askml(integer, geometry, integer) OWNER TO postgres;

--
-- Name: _st_askml(integer, geography, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_askml(integer, geography, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_as_kml';


ALTER FUNCTION public._st_askml(integer, geography, integer) OWNER TO postgres;

--
-- Name: _st_bestsrid(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_bestsrid(geography) RETURNS integer
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_BestSRID($1,$1)$_$;


ALTER FUNCTION public._st_bestsrid(geography) OWNER TO postgres;

--
-- Name: _st_bestsrid(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_bestsrid(geography, geography) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_bestsrid';


ALTER FUNCTION public._st_bestsrid(geography, geography) OWNER TO postgres;

--
-- Name: _st_buffer(geometry, double precision, cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_buffer(geometry, double precision, cstring) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'buffer';


ALTER FUNCTION public._st_buffer(geometry, double precision, cstring) OWNER TO postgres;

--
-- Name: _st_contains(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_contains(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'contains';


ALTER FUNCTION public._st_contains(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_containsproperly(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_containsproperly(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'containsproperly';


ALTER FUNCTION public._st_containsproperly(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_coveredby(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_coveredby(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'coveredby';


ALTER FUNCTION public._st_coveredby(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_covers(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_covers(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'covers';


ALTER FUNCTION public._st_covers(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_covers(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_covers(geography, geography) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'geography_covers';


ALTER FUNCTION public._st_covers(geography, geography) OWNER TO postgres;

--
-- Name: _st_crosses(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_crosses(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'crosses';


ALTER FUNCTION public._st_crosses(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_dfullywithin(geometry, geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_dfullywithin(geometry, geometry, double precision) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_dfullywithin';


ALTER FUNCTION public._st_dfullywithin(geometry, geometry, double precision) OWNER TO postgres;

--
-- Name: _st_distance(geography, geography, double precision, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_distance(geography, geography, double precision, boolean) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'geography_distance';


ALTER FUNCTION public._st_distance(geography, geography, double precision, boolean) OWNER TO postgres;

--
-- Name: _st_dumppoints(geometry, integer[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_dumppoints(the_geom geometry, cur_path integer[]) RETURNS SETOF geometry_dump
    LANGUAGE plpgsql
    AS $$
DECLARE
  tmp geometry_dump;
  tmp2 geometry_dump;
  nb_points integer;
  nb_geom integer;
  i integer;
  j integer;
  g geometry;
  
BEGIN
  
  RAISE DEBUG '%,%', cur_path, ST_GeometryType(the_geom);

  -- Special case (MULTI* OR GEOMETRYCOLLECTION) : iterate and return the DumpPoints of the geometries
  SELECT ST_NumGeometries(the_geom) INTO nb_geom;

  IF (nb_geom IS NOT NULL) THEN
    
    i = 1;
    FOR tmp2 IN SELECT (ST_Dump(the_geom)).* LOOP

      FOR tmp IN SELECT * FROM _ST_DumpPoints(tmp2.geom, cur_path || tmp2.path) LOOP
	    RETURN NEXT tmp;
      END LOOP;
      i = i + 1;
      
    END LOOP;

    RETURN;
  END IF;
  

  -- Special case (POLYGON) : return the points of the rings of a polygon
  IF (ST_GeometryType(the_geom) = 'ST_Polygon') THEN

    FOR tmp IN SELECT * FROM _ST_DumpPoints(ST_ExteriorRing(the_geom), cur_path || ARRAY[1]) LOOP
      RETURN NEXT tmp;
    END LOOP;
    
    j := ST_NumInteriorRings(the_geom);
    FOR i IN 1..j LOOP
        FOR tmp IN SELECT * FROM _ST_DumpPoints(ST_InteriorRingN(the_geom, i), cur_path || ARRAY[i+1]) LOOP
          RETURN NEXT tmp;
        END LOOP;
    END LOOP;
    
    RETURN;
  END IF;

    
  -- Special case (POINT) : return the point
  IF (ST_GeometryType(the_geom) = 'ST_Point') THEN

    tmp.path = cur_path || ARRAY[1];
    tmp.geom = the_geom;

    RETURN NEXT tmp;
    RETURN;

  END IF;


  -- Use ST_NumPoints rather than ST_NPoints to have a NULL value if the_geom isn't
  -- a LINESTRING or CIRCULARSTRING.
  SELECT ST_NumPoints(the_geom) INTO nb_points;

  -- This should never happen
  IF (nb_points IS NULL) THEN
    RAISE EXCEPTION 'Unexpected error while dumping geometry %', ST_AsText(the_geom);
  END IF;

  FOR i IN 1..nb_points LOOP
    tmp.path = cur_path || ARRAY[i];
    tmp.geom := ST_PointN(the_geom, i);
    RETURN NEXT tmp;
  END LOOP;
   
END
$$;


ALTER FUNCTION public._st_dumppoints(the_geom geometry, cur_path integer[]) OWNER TO postgres;

--
-- Name: _st_dwithin(geometry, geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_dwithin(geometry, geometry, double precision) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_dwithin';


ALTER FUNCTION public._st_dwithin(geometry, geometry, double precision) OWNER TO postgres;

--
-- Name: _st_dwithin(geography, geography, double precision, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_dwithin(geography, geography, double precision, boolean) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'geography_dwithin';


ALTER FUNCTION public._st_dwithin(geography, geography, double precision, boolean) OWNER TO postgres;

--
-- Name: _st_equals(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_equals(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'geomequals';


ALTER FUNCTION public._st_equals(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_expand(geography, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_expand(geography, double precision) RETURNS geography
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_expand';


ALTER FUNCTION public._st_expand(geography, double precision) OWNER TO postgres;

--
-- Name: _st_intersects(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_intersects(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'intersects';


ALTER FUNCTION public._st_intersects(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_linecrossingdirection(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_linecrossingdirection(geometry, geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'ST_LineCrossingDirection';


ALTER FUNCTION public._st_linecrossingdirection(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_longestline(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_longestline(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_longestline2d';


ALTER FUNCTION public._st_longestline(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_maxdistance(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_maxdistance(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_maxdistance2d_linestring';


ALTER FUNCTION public._st_maxdistance(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_orderingequals(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_orderingequals(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_same';


ALTER FUNCTION public._st_orderingequals(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_overlaps(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_overlaps(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'overlaps';


ALTER FUNCTION public._st_overlaps(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_pointoutside(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_pointoutside(geography) RETURNS geography
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_point_outside';


ALTER FUNCTION public._st_pointoutside(geography) OWNER TO postgres;

--
-- Name: _st_touches(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_touches(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'touches';


ALTER FUNCTION public._st_touches(geometry, geometry) OWNER TO postgres;

--
-- Name: _st_within(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _st_within(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'within';


ALTER FUNCTION public._st_within(geometry, geometry) OWNER TO postgres;

--
-- Name: addauth(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION addauth(text) RETURNS boolean
    LANGUAGE plpgsql
    AS $_$ 
DECLARE
	lockid alias for $1;
	okay boolean;
	myrec record;
BEGIN
	-- check to see if table exists
	--  if not, CREATE TEMP TABLE mylock (transid xid, lockcode text)
	okay := 'f';
	FOR myrec IN SELECT * FROM pg_class WHERE relname = 'temp_lock_have_table' LOOP
		okay := 't';
	END LOOP; 
	IF (okay <> 't') THEN 
		CREATE TEMP TABLE temp_lock_have_table (transid xid, lockcode text);
			-- this will only work from pgsql7.4 up
			-- ON COMMIT DELETE ROWS;
	END IF;

	--  INSERT INTO mylock VALUES ( $1)
--	EXECUTE 'INSERT INTO temp_lock_have_table VALUES ( '||
--		quote_literal(getTransactionID()) || ',' ||
--		quote_literal(lockid) ||')';

	INSERT INTO temp_lock_have_table VALUES (getTransactionID(), lockid);

	RETURN true::boolean;
END;
$_$;


ALTER FUNCTION public.addauth(text) OWNER TO postgres;

--
-- Name: addbbox(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION addbbox(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_addBBOX';


ALTER FUNCTION public.addbbox(geometry) OWNER TO postgres;

--
-- Name: addgeometrycolumn(character varying, character varying, integer, character varying, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION addgeometrycolumn(character varying, character varying, integer, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT AddGeometryColumn('','',$1,$2,$3,$4,$5) into ret;
	RETURN ret;
END;
$_$;


ALTER FUNCTION public.addgeometrycolumn(character varying, character varying, integer, character varying, integer) OWNER TO postgres;

--
-- Name: addgeometrycolumn(character varying, character varying, character varying, integer, character varying, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION addgeometrycolumn(character varying, character varying, character varying, integer, character varying, integer) RETURNS text
    LANGUAGE plpgsql STABLE STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT AddGeometryColumn('',$1,$2,$3,$4,$5,$6) into ret;
	RETURN ret;
END;
$_$;


ALTER FUNCTION public.addgeometrycolumn(character varying, character varying, character varying, integer, character varying, integer) OWNER TO postgres;

--
-- Name: addgeometrycolumn(character varying, character varying, character varying, character varying, integer, character varying, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION addgeometrycolumn(character varying, character varying, character varying, character varying, integer, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	catalog_name alias for $1;
	schema_name alias for $2;
	table_name alias for $3;
	column_name alias for $4;
	new_srid alias for $5;
	new_type alias for $6;
	new_dim alias for $7;
	rec RECORD;
	sr varchar;
	real_schema name;
	sql text;

BEGIN

	-- Verify geometry type
	IF ( NOT ( (new_type = 'GEOMETRY') OR
			   (new_type = 'GEOMETRYCOLLECTION') OR
			   (new_type = 'POINT') OR
			   (new_type = 'MULTIPOINT') OR
			   (new_type = 'POLYGON') OR
			   (new_type = 'MULTIPOLYGON') OR
			   (new_type = 'LINESTRING') OR
			   (new_type = 'MULTILINESTRING') OR
			   (new_type = 'GEOMETRYCOLLECTIONM') OR
			   (new_type = 'POINTM') OR
			   (new_type = 'MULTIPOINTM') OR
			   (new_type = 'POLYGONM') OR
			   (new_type = 'MULTIPOLYGONM') OR
			   (new_type = 'LINESTRINGM') OR
			   (new_type = 'MULTILINESTRINGM') OR
			   (new_type = 'CIRCULARSTRING') OR
			   (new_type = 'CIRCULARSTRINGM') OR
			   (new_type = 'COMPOUNDCURVE') OR
			   (new_type = 'COMPOUNDCURVEM') OR
			   (new_type = 'CURVEPOLYGON') OR
			   (new_type = 'CURVEPOLYGONM') OR
			   (new_type = 'MULTICURVE') OR
			   (new_type = 'MULTICURVEM') OR
			   (new_type = 'MULTISURFACE') OR
			   (new_type = 'MULTISURFACEM')) )
	THEN
		RAISE EXCEPTION 'Invalid type name - valid ones are:
	POINT, MULTIPOINT,
	LINESTRING, MULTILINESTRING,
	POLYGON, MULTIPOLYGON,
	CIRCULARSTRING, COMPOUNDCURVE, MULTICURVE,
	CURVEPOLYGON, MULTISURFACE,
	GEOMETRY, GEOMETRYCOLLECTION,
	POINTM, MULTIPOINTM,
	LINESTRINGM, MULTILINESTRINGM,
	POLYGONM, MULTIPOLYGONM,
	CIRCULARSTRINGM, COMPOUNDCURVEM, MULTICURVEM
	CURVEPOLYGONM, MULTISURFACEM,
	or GEOMETRYCOLLECTIONM';
		RETURN 'fail';
	END IF;


	-- Verify dimension
	IF ( (new_dim >4) OR (new_dim <0) ) THEN
		RAISE EXCEPTION 'invalid dimension';
		RETURN 'fail';
	END IF;

	IF ( (new_type LIKE '%M') AND (new_dim!=3) ) THEN
		RAISE EXCEPTION 'TypeM needs 3 dimensions';
		RETURN 'fail';
	END IF;


	-- Verify SRID
	IF ( new_srid != -1 ) THEN
		SELECT SRID INTO sr FROM spatial_ref_sys WHERE SRID = new_srid;
		IF NOT FOUND THEN
			RAISE EXCEPTION 'AddGeometryColumns() - invalid SRID';
			RETURN 'fail';
		END IF;
	END IF;


	-- Verify schema
	IF ( schema_name IS NOT NULL AND schema_name != '' ) THEN
		sql := 'SELECT nspname FROM pg_namespace ' ||
			'WHERE text(nspname) = ' || quote_literal(schema_name) ||
			'LIMIT 1';
		RAISE DEBUG '%', sql;
		EXECUTE sql INTO real_schema;

		IF ( real_schema IS NULL ) THEN
			RAISE EXCEPTION 'Schema % is not a valid schemaname', quote_literal(schema_name);
			RETURN 'fail';
		END IF;
	END IF;

	IF ( real_schema IS NULL ) THEN
		RAISE DEBUG 'Detecting schema';
		sql := 'SELECT n.nspname AS schemaname ' ||
			'FROM pg_catalog.pg_class c ' ||
			  'JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace ' ||
			'WHERE c.relkind = ' || quote_literal('r') ||
			' AND n.nspname NOT IN (' || quote_literal('pg_catalog') || ', ' || quote_literal('pg_toast') || ')' ||
			' AND pg_catalog.pg_table_is_visible(c.oid)' ||
			' AND c.relname = ' || quote_literal(table_name);
		RAISE DEBUG '%', sql;
		EXECUTE sql INTO real_schema;

		IF ( real_schema IS NULL ) THEN
			RAISE EXCEPTION 'Table % does not occur in the search_path', quote_literal(table_name);
			RETURN 'fail';
		END IF;
	END IF;


	-- Add geometry column to table
	sql := 'ALTER TABLE ' ||
		quote_ident(real_schema) || '.' || quote_ident(table_name)
		|| ' ADD COLUMN ' || quote_ident(column_name) ||
		' geometry ';
	RAISE DEBUG '%', sql;
	EXECUTE sql;


	-- Delete stale record in geometry_columns (if any)
	sql := 'DELETE FROM geometry_columns WHERE
		f_table_catalog = ' || quote_literal('') ||
		' AND f_table_schema = ' ||
		quote_literal(real_schema) ||
		' AND f_table_name = ' || quote_literal(table_name) ||
		' AND f_geometry_column = ' || quote_literal(column_name);
	RAISE DEBUG '%', sql;
	EXECUTE sql;


	-- Add record in geometry_columns
	sql := 'INSERT INTO geometry_columns (f_table_catalog,f_table_schema,f_table_name,' ||
										  'f_geometry_column,coord_dimension,srid,type)' ||
		' VALUES (' ||
		quote_literal('') || ',' ||
		quote_literal(real_schema) || ',' ||
		quote_literal(table_name) || ',' ||
		quote_literal(column_name) || ',' ||
		new_dim::text || ',' ||
		new_srid::text || ',' ||
		quote_literal(new_type) || ')';
	RAISE DEBUG '%', sql;
	EXECUTE sql;


	-- Add table CHECKs
	sql := 'ALTER TABLE ' ||
		quote_ident(real_schema) || '.' || quote_ident(table_name)
		|| ' ADD CONSTRAINT '
		|| quote_ident('enforce_srid_' || column_name)
		|| ' CHECK (ST_SRID(' || quote_ident(column_name) ||
		') = ' || new_srid::text || ')' ;
	RAISE DEBUG '%', sql;
	EXECUTE sql;

	sql := 'ALTER TABLE ' ||
		quote_ident(real_schema) || '.' || quote_ident(table_name)
		|| ' ADD CONSTRAINT '
		|| quote_ident('enforce_dims_' || column_name)
		|| ' CHECK (ST_NDims(' || quote_ident(column_name) ||
		') = ' || new_dim::text || ')' ;
	RAISE DEBUG '%', sql;
	EXECUTE sql;

	IF ( NOT (new_type = 'GEOMETRY')) THEN
		sql := 'ALTER TABLE ' ||
			quote_ident(real_schema) || '.' || quote_ident(table_name) || ' ADD CONSTRAINT ' ||
			quote_ident('enforce_geotype_' || column_name) ||
			' CHECK (GeometryType(' ||
			quote_ident(column_name) || ')=' ||
			quote_literal(new_type) || ' OR (' ||
			quote_ident(column_name) || ') is null)';
		RAISE DEBUG '%', sql;
		EXECUTE sql;
	END IF;

	RETURN
		real_schema || '.' ||
		table_name || '.' || column_name ||
		' SRID:' || new_srid::text ||
		' TYPE:' || new_type ||
		' DIMS:' || new_dim::text || ' ';
END;
$_$;


ALTER FUNCTION public.addgeometrycolumn(character varying, character varying, character varying, character varying, integer, character varying, integer) OWNER TO postgres;

--
-- Name: addpoint(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION addpoint(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_addpoint';


ALTER FUNCTION public.addpoint(geometry, geometry) OWNER TO postgres;

--
-- Name: addpoint(geometry, geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION addpoint(geometry, geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_addpoint';


ALTER FUNCTION public.addpoint(geometry, geometry, integer) OWNER TO postgres;

--
-- Name: affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1,  $2, $3, 0,  $4, $5, 0,  0, 0, 1,  $6, $7, 0)$_$;


ALTER FUNCTION public.affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_affine';


ALTER FUNCTION public.affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: area(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION area(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_area_polygon';


ALTER FUNCTION public.area(geometry) OWNER TO postgres;

--
-- Name: area2d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION area2d(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_area_polygon';


ALTER FUNCTION public.area2d(geometry) OWNER TO postgres;

--
-- Name: asbinary(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION asbinary(geometry) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asBinary';


ALTER FUNCTION public.asbinary(geometry) OWNER TO postgres;

--
-- Name: asbinary(geometry, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION asbinary(geometry, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asBinary';


ALTER FUNCTION public.asbinary(geometry, text) OWNER TO postgres;

--
-- Name: asewkb(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION asewkb(geometry) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'WKBFromLWGEOM';


ALTER FUNCTION public.asewkb(geometry) OWNER TO postgres;

--
-- Name: asewkb(geometry, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION asewkb(geometry, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'WKBFromLWGEOM';


ALTER FUNCTION public.asewkb(geometry, text) OWNER TO postgres;

--
-- Name: asewkt(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION asewkt(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asEWKT';


ALTER FUNCTION public.asewkt(geometry) OWNER TO postgres;

--
-- Name: asgml(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION asgml(geometry) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML(2, $1, 15, 0)$_$;


ALTER FUNCTION public.asgml(geometry) OWNER TO postgres;

--
-- Name: asgml(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION asgml(geometry, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML(2, $1, $2, 0)$_$;


ALTER FUNCTION public.asgml(geometry, integer) OWNER TO postgres;

--
-- Name: ashexewkb(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION ashexewkb(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asHEXEWKB';


ALTER FUNCTION public.ashexewkb(geometry) OWNER TO postgres;

--
-- Name: ashexewkb(geometry, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION ashexewkb(geometry, text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asHEXEWKB';


ALTER FUNCTION public.ashexewkb(geometry, text) OWNER TO postgres;

--
-- Name: askml(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION askml(geometry) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML(2, transform($1,4326), 15)$_$;


ALTER FUNCTION public.askml(geometry) OWNER TO postgres;

--
-- Name: askml(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION askml(geometry, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML(2, transform($1,4326), $2)$_$;


ALTER FUNCTION public.askml(geometry, integer) OWNER TO postgres;

--
-- Name: askml(integer, geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION askml(integer, geometry, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML($1, transform($2,4326), $3)$_$;


ALTER FUNCTION public.askml(integer, geometry, integer) OWNER TO postgres;

--
-- Name: assvg(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION assvg(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'assvg_geometry';


ALTER FUNCTION public.assvg(geometry) OWNER TO postgres;

--
-- Name: assvg(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION assvg(geometry, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'assvg_geometry';


ALTER FUNCTION public.assvg(geometry, integer) OWNER TO postgres;

--
-- Name: assvg(geometry, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION assvg(geometry, integer, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'assvg_geometry';


ALTER FUNCTION public.assvg(geometry, integer, integer) OWNER TO postgres;

--
-- Name: astext(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION astext(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asText';


ALTER FUNCTION public.astext(geometry) OWNER TO postgres;

--
-- Name: azimuth(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION azimuth(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_azimuth';


ALTER FUNCTION public.azimuth(geometry, geometry) OWNER TO postgres;

--
-- Name: bdmpolyfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION bdmpolyfromtext(text, integer) RETURNS geometry
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
DECLARE
	geomtext alias for $1;
	srid alias for $2;
	mline geometry;
	geom geometry;
BEGIN
	mline := MultiLineStringFromText(geomtext, srid);

	IF mline IS NULL
	THEN
		RAISE EXCEPTION 'Input is not a MultiLinestring';
	END IF;

	geom := multi(BuildArea(mline));

	RETURN geom;
END;
$_$;


ALTER FUNCTION public.bdmpolyfromtext(text, integer) OWNER TO postgres;

--
-- Name: bdpolyfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION bdpolyfromtext(text, integer) RETURNS geometry
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
DECLARE
	geomtext alias for $1;
	srid alias for $2;
	mline geometry;
	geom geometry;
BEGIN
	mline := MultiLineStringFromText(geomtext, srid);

	IF mline IS NULL
	THEN
		RAISE EXCEPTION 'Input is not a MultiLinestring';
	END IF;

	geom := BuildArea(mline);

	IF GeometryType(geom) != 'POLYGON'
	THEN
		RAISE EXCEPTION 'Input returns more then a single polygon, try using BdMPolyFromText instead';
	END IF;

	RETURN geom;
END;
$_$;


ALTER FUNCTION public.bdpolyfromtext(text, integer) OWNER TO postgres;

--
-- Name: boundary(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION boundary(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'boundary';


ALTER FUNCTION public.boundary(geometry) OWNER TO postgres;

--
-- Name: box(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box(geometry) RETURNS box
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_BOX';


ALTER FUNCTION public.box(geometry) OWNER TO postgres;

--
-- Name: box(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box(box3d) RETURNS box
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_to_BOX';


ALTER FUNCTION public.box(box3d) OWNER TO postgres;

--
-- Name: box2d(box3d_extent); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box2d(box3d_extent) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_to_BOX2DFLOAT4';


ALTER FUNCTION public.box2d(box3d_extent) OWNER TO postgres;

--
-- Name: box2d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box2d(geometry) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_BOX2DFLOAT4';


ALTER FUNCTION public.box2d(geometry) OWNER TO postgres;

--
-- Name: box2d(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box2d(box3d) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_to_BOX2DFLOAT4';


ALTER FUNCTION public.box2d(box3d) OWNER TO postgres;

--
-- Name: box3d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box3d(geometry) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_BOX3D';


ALTER FUNCTION public.box3d(geometry) OWNER TO postgres;

--
-- Name: box3d(box2d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box3d(box2d) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_to_BOX3D';


ALTER FUNCTION public.box3d(box2d) OWNER TO postgres;

--
-- Name: box3d_extent(box3d_extent); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box3d_extent(box3d_extent) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_extent_to_BOX3D';


ALTER FUNCTION public.box3d_extent(box3d_extent) OWNER TO postgres;

--
-- Name: box3dtobox(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION box3dtobox(box3d) RETURNS box
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT box($1)$_$;


ALTER FUNCTION public.box3dtobox(box3d) OWNER TO postgres;

--
-- Name: buffer(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION buffer(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'buffer';


ALTER FUNCTION public.buffer(geometry, double precision) OWNER TO postgres;

--
-- Name: buffer(geometry, double precision, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION buffer(geometry, double precision, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_Buffer($1, $2, $3)$_$;


ALTER FUNCTION public.buffer(geometry, double precision, integer) OWNER TO postgres;

--
-- Name: buildarea(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION buildarea(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_buildarea';


ALTER FUNCTION public.buildarea(geometry) OWNER TO postgres;

--
-- Name: bytea(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION bytea(geometry) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_bytea';


ALTER FUNCTION public.bytea(geometry) OWNER TO postgres;

--
-- Name: centroid(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION centroid(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'centroid';


ALTER FUNCTION public.centroid(geometry) OWNER TO postgres;

--
-- Name: checkauth(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION checkauth(text, text) RETURNS integer
    LANGUAGE sql
    AS $_$ SELECT CheckAuth('', $1, $2) $_$;


ALTER FUNCTION public.checkauth(text, text) OWNER TO postgres;

--
-- Name: checkauth(text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION checkauth(text, text, text) RETURNS integer
    LANGUAGE plpgsql
    AS $_$ 
DECLARE
	schema text;
BEGIN
	IF NOT LongTransactionsEnabled() THEN
		RAISE EXCEPTION 'Long transaction support disabled, use EnableLongTransaction() to enable.';
	END IF;

	if ( $1 != '' ) THEN
		schema = $1;
	ELSE
		SELECT current_schema() into schema;
	END IF;

	-- TODO: check for an already existing trigger ?

	EXECUTE 'CREATE TRIGGER check_auth BEFORE UPDATE OR DELETE ON ' 
		|| quote_ident(schema) || '.' || quote_ident($2)
		||' FOR EACH ROW EXECUTE PROCEDURE CheckAuthTrigger('
		|| quote_literal($3) || ')';

	RETURN 0;
END;
$_$;


ALTER FUNCTION public.checkauth(text, text, text) OWNER TO postgres;

--
-- Name: checkauthtrigger(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION checkauthtrigger() RETURNS trigger
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'check_authorization';


ALTER FUNCTION public.checkauthtrigger() OWNER TO postgres;

--
-- Name: collect(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION collect(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'LWGEOM_collect';


ALTER FUNCTION public.collect(geometry, geometry) OWNER TO postgres;

--
-- Name: combine_bbox(box2d, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION combine_bbox(box2d, geometry) RETURNS box2d
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_combine';


ALTER FUNCTION public.combine_bbox(box2d, geometry) OWNER TO postgres;

--
-- Name: combine_bbox(box3d_extent, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION combine_bbox(box3d_extent, geometry) RETURNS box3d_extent
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'BOX3D_combine';


ALTER FUNCTION public.combine_bbox(box3d_extent, geometry) OWNER TO postgres;

--
-- Name: combine_bbox(box3d, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION combine_bbox(box3d, geometry) RETURNS box3d
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'BOX3D_combine';


ALTER FUNCTION public.combine_bbox(box3d, geometry) OWNER TO postgres;

--
-- Name: compression(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION compression(chip) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getCompression';


ALTER FUNCTION public.compression(chip) OWNER TO postgres;

--
-- Name: contains(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION contains(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'contains';


ALTER FUNCTION public.contains(geometry, geometry) OWNER TO postgres;

--
-- Name: convexhull(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION convexhull(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'convexhull';


ALTER FUNCTION public.convexhull(geometry) OWNER TO postgres;

--
-- Name: crosses(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION crosses(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'crosses';


ALTER FUNCTION public.crosses(geometry, geometry) OWNER TO postgres;

--
-- Name: datatype(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION datatype(chip) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getDatatype';


ALTER FUNCTION public.datatype(chip) OWNER TO postgres;

--
-- Name: difference(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION difference(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'difference';


ALTER FUNCTION public.difference(geometry, geometry) OWNER TO postgres;

--
-- Name: dimension(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dimension(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_dimension';


ALTER FUNCTION public.dimension(geometry) OWNER TO postgres;

--
-- Name: disablelongtransactions(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION disablelongtransactions() RETURNS text
    LANGUAGE plpgsql
    AS $$ 
DECLARE
	rec RECORD;

BEGIN

	--
	-- Drop all triggers applied by CheckAuth()
	--
	FOR rec IN
		SELECT c.relname, t.tgname, t.tgargs FROM pg_trigger t, pg_class c, pg_proc p
		WHERE p.proname = 'checkauthtrigger' and t.tgfoid = p.oid and t.tgrelid = c.oid
	LOOP
		EXECUTE 'DROP TRIGGER ' || quote_ident(rec.tgname) ||
			' ON ' || quote_ident(rec.relname);
	END LOOP;

	--
	-- Drop the authorization_table table
	--
	FOR rec IN SELECT * FROM pg_class WHERE relname = 'authorization_table' LOOP
		DROP TABLE authorization_table;
	END LOOP;

	--
	-- Drop the authorized_tables view
	--
	FOR rec IN SELECT * FROM pg_class WHERE relname = 'authorized_tables' LOOP
		DROP VIEW authorized_tables;
	END LOOP;

	RETURN 'Long transactions support disabled';
END;
$$;


ALTER FUNCTION public.disablelongtransactions() OWNER TO postgres;

--
-- Name: disjoint(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION disjoint(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'disjoint';


ALTER FUNCTION public.disjoint(geometry, geometry) OWNER TO postgres;

--
-- Name: distance(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION distance(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_mindistance2d';


ALTER FUNCTION public.distance(geometry, geometry) OWNER TO postgres;

--
-- Name: distance_sphere(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION distance_sphere(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_distance_sphere';


ALTER FUNCTION public.distance_sphere(geometry, geometry) OWNER TO postgres;

--
-- Name: distance_spheroid(geometry, geometry, spheroid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION distance_spheroid(geometry, geometry, spheroid) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_distance_ellipsoid';


ALTER FUNCTION public.distance_spheroid(geometry, geometry, spheroid) OWNER TO postgres;

--
-- Name: dropbbox(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dropbbox(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_dropBBOX';


ALTER FUNCTION public.dropbbox(geometry) OWNER TO postgres;

--
-- Name: dropgeometrycolumn(character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dropgeometrycolumn(character varying, character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret text;
BEGIN
	SELECT DropGeometryColumn('','',$1,$2) into ret;
	RETURN ret;
END;
$_$;


ALTER FUNCTION public.dropgeometrycolumn(character varying, character varying) OWNER TO postgres;

--
-- Name: dropgeometrycolumn(character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dropgeometrycolumn(character varying, character varying, character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret text;
BEGIN
	SELECT DropGeometryColumn('',$1,$2,$3) into ret;
	RETURN ret;
END;
$_$;


ALTER FUNCTION public.dropgeometrycolumn(character varying, character varying, character varying) OWNER TO postgres;

--
-- Name: dropgeometrycolumn(character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dropgeometrycolumn(character varying, character varying, character varying, character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	catalog_name alias for $1;
	schema_name alias for $2;
	table_name alias for $3;
	column_name alias for $4;
	myrec RECORD;
	okay boolean;
	real_schema name;

BEGIN


	-- Find, check or fix schema_name
	IF ( schema_name != '' ) THEN
		okay = 'f';

		FOR myrec IN SELECT nspname FROM pg_namespace WHERE text(nspname) = schema_name LOOP
			okay := 't';
		END LOOP;

		IF ( okay <> 't' ) THEN
			RAISE NOTICE 'Invalid schema name - using current_schema()';
			SELECT current_schema() into real_schema;
		ELSE
			real_schema = schema_name;
		END IF;
	ELSE
		SELECT current_schema() into real_schema;
	END IF;

	-- Find out if the column is in the geometry_columns table
	okay = 'f';
	FOR myrec IN SELECT * from geometry_columns where f_table_schema = text(real_schema) and f_table_name = table_name and f_geometry_column = column_name LOOP
		okay := 't';
	END LOOP;
	IF (okay <> 't') THEN
		RAISE EXCEPTION 'column not found in geometry_columns table';
		RETURN 'f';
	END IF;

	-- Remove ref from geometry_columns table
	EXECUTE 'delete from geometry_columns where f_table_schema = ' ||
		quote_literal(real_schema) || ' and f_table_name = ' ||
		quote_literal(table_name)  || ' and f_geometry_column = ' ||
		quote_literal(column_name);

	-- Remove table column
	EXECUTE 'ALTER TABLE ' || quote_ident(real_schema) || '.' ||
		quote_ident(table_name) || ' DROP COLUMN ' ||
		quote_ident(column_name);

	RETURN real_schema || '.' || table_name || '.' || column_name ||' effectively removed.';

END;
$_$;


ALTER FUNCTION public.dropgeometrycolumn(character varying, character varying, character varying, character varying) OWNER TO postgres;

--
-- Name: dropgeometrytable(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dropgeometrytable(character varying) RETURNS text
    LANGUAGE sql STRICT
    AS $_$ SELECT DropGeometryTable('','',$1) $_$;


ALTER FUNCTION public.dropgeometrytable(character varying) OWNER TO postgres;

--
-- Name: dropgeometrytable(character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dropgeometrytable(character varying, character varying) RETURNS text
    LANGUAGE sql STRICT
    AS $_$ SELECT DropGeometryTable('',$1,$2) $_$;


ALTER FUNCTION public.dropgeometrytable(character varying, character varying) OWNER TO postgres;

--
-- Name: dropgeometrytable(character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dropgeometrytable(character varying, character varying, character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	catalog_name alias for $1;
	schema_name alias for $2;
	table_name alias for $3;
	real_schema name;

BEGIN

	IF ( schema_name = '' ) THEN
		SELECT current_schema() into real_schema;
	ELSE
		real_schema = schema_name;
	END IF;

	-- Remove refs from geometry_columns table
	EXECUTE 'DELETE FROM geometry_columns WHERE ' ||
		'f_table_schema = ' || quote_literal(real_schema) ||
		' AND ' ||
		' f_table_name = ' || quote_literal(table_name);

	-- Remove table
	EXECUTE 'DROP TABLE '
		|| quote_ident(real_schema) || '.' ||
		quote_ident(table_name);

	RETURN
		real_schema || '.' ||
		table_name ||' dropped.';

END;
$_$;


ALTER FUNCTION public.dropgeometrytable(character varying, character varying, character varying) OWNER TO postgres;

--
-- Name: dump(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dump(geometry) RETURNS SETOF geometry_dump
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_dump';


ALTER FUNCTION public.dump(geometry) OWNER TO postgres;

--
-- Name: dumprings(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dumprings(geometry) RETURNS SETOF geometry_dump
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_dump_rings';


ALTER FUNCTION public.dumprings(geometry) OWNER TO postgres;

--
-- Name: enablelongtransactions(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION enablelongtransactions() RETURNS text
    LANGUAGE plpgsql
    AS $$ 
DECLARE
	"query" text;
	exists bool;
	rec RECORD;

BEGIN

	exists = 'f';
	FOR rec IN SELECT * FROM pg_class WHERE relname = 'authorization_table'
	LOOP
		exists = 't';
	END LOOP;

	IF NOT exists
	THEN
		"query" = 'CREATE TABLE authorization_table (
			toid oid, -- table oid
			rid text, -- row id
			expires timestamp,
			authid text
		)';
		EXECUTE "query";
	END IF;

	exists = 'f';
	FOR rec IN SELECT * FROM pg_class WHERE relname = 'authorized_tables'
	LOOP
		exists = 't';
	END LOOP;

	IF NOT exists THEN
		"query" = 'CREATE VIEW authorized_tables AS ' ||
			'SELECT ' ||
			'n.nspname as schema, ' ||
			'c.relname as table, trim(' ||
			quote_literal(chr(92) || '000') ||
			' from t.tgargs) as id_column ' ||
			'FROM pg_trigger t, pg_class c, pg_proc p ' ||
			', pg_namespace n ' ||
			'WHERE p.proname = ' || quote_literal('checkauthtrigger') ||
			' AND c.relnamespace = n.oid' ||
			' AND t.tgfoid = p.oid and t.tgrelid = c.oid';
		EXECUTE "query";
	END IF;

	RETURN 'Long transactions support enabled';
END;
$$;


ALTER FUNCTION public.enablelongtransactions() OWNER TO postgres;

--
-- Name: endpoint(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION endpoint(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_endpoint_linestring';


ALTER FUNCTION public.endpoint(geometry) OWNER TO postgres;

--
-- Name: envelope(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION envelope(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_envelope';


ALTER FUNCTION public.envelope(geometry) OWNER TO postgres;

--
-- Name: equals(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION equals(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geomequals';


ALTER FUNCTION public.equals(geometry, geometry) OWNER TO postgres;

--
-- Name: estimated_extent(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION estimated_extent(text, text) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT SECURITY DEFINER
    AS '$libdir/postgis-1.5', 'LWGEOM_estimated_extent';


ALTER FUNCTION public.estimated_extent(text, text) OWNER TO postgres;

--
-- Name: estimated_extent(text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION estimated_extent(text, text, text) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT SECURITY DEFINER
    AS '$libdir/postgis-1.5', 'LWGEOM_estimated_extent';


ALTER FUNCTION public.estimated_extent(text, text, text) OWNER TO postgres;

--
-- Name: expand(box3d, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION expand(box3d, double precision) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_expand';


ALTER FUNCTION public.expand(box3d, double precision) OWNER TO postgres;

--
-- Name: expand(box2d, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION expand(box2d, double precision) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_expand';


ALTER FUNCTION public.expand(box2d, double precision) OWNER TO postgres;

--
-- Name: expand(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION expand(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_expand';


ALTER FUNCTION public.expand(geometry, double precision) OWNER TO postgres;

--
-- Name: exteriorring(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION exteriorring(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_exteriorring_polygon';


ALTER FUNCTION public.exteriorring(geometry) OWNER TO postgres;

--
-- Name: factor(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION factor(chip) RETURNS real
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getFactor';


ALTER FUNCTION public.factor(chip) OWNER TO postgres;

--
-- Name: find_extent(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION find_extent(text, text) RETURNS box2d
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
DECLARE
	tablename alias for $1;
	columnname alias for $2;
	myrec RECORD;

BEGIN
	FOR myrec IN EXECUTE 'SELECT extent("' || columnname || '") FROM "' || tablename || '"' LOOP
		return myrec.extent;
	END LOOP;
END;
$_$;


ALTER FUNCTION public.find_extent(text, text) OWNER TO postgres;

--
-- Name: find_extent(text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION find_extent(text, text, text) RETURNS box2d
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
DECLARE
	schemaname alias for $1;
	tablename alias for $2;
	columnname alias for $3;
	myrec RECORD;

BEGIN
	FOR myrec IN EXECUTE 'SELECT extent("' || columnname || '") FROM "' || schemaname || '"."' || tablename || '"' LOOP
		return myrec.extent;
	END LOOP;
END;
$_$;


ALTER FUNCTION public.find_extent(text, text, text) OWNER TO postgres;

--
-- Name: find_srid(character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION find_srid(character varying, character varying, character varying) RETURNS integer
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
DECLARE
	schem text;
	tabl text;
	sr int4;
BEGIN
	IF $1 IS NULL THEN
	  RAISE EXCEPTION 'find_srid() - schema is NULL!';
	END IF;
	IF $2 IS NULL THEN
	  RAISE EXCEPTION 'find_srid() - table name is NULL!';
	END IF;
	IF $3 IS NULL THEN
	  RAISE EXCEPTION 'find_srid() - column name is NULL!';
	END IF;
	schem = $1;
	tabl = $2;
-- if the table contains a . and the schema is empty
-- split the table into a schema and a table
-- otherwise drop through to default behavior
	IF ( schem = '' and tabl LIKE '%.%' ) THEN
	 schem = substr(tabl,1,strpos(tabl,'.')-1);
	 tabl = substr(tabl,length(schem)+2);
	ELSE
	 schem = schem || '%';
	END IF;

	select SRID into sr from geometry_columns where f_table_schema like schem and f_table_name = tabl and f_geometry_column = $3;
	IF NOT FOUND THEN
	   RAISE EXCEPTION 'find_srid() - couldnt find the corresponding SRID - is the geometry registered in the GEOMETRY_COLUMNS table?  Is there an uppercase/lowercase missmatch?';
	END IF;
	return sr;
END;
$_$;


ALTER FUNCTION public.find_srid(character varying, character varying, character varying) OWNER TO postgres;

--
-- Name: fix_geometry_columns(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fix_geometry_columns() RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
	mislinked record;
	result text;
	linked integer;
	deleted integer;
	foundschema integer;
BEGIN

	-- Since 7.3 schema support has been added.
	-- Previous postgis versions used to put the database name in
	-- the schema column. This needs to be fixed, so we try to
	-- set the correct schema for each geometry_colums record
	-- looking at table, column, type and srid.
	UPDATE geometry_columns SET f_table_schema = n.nspname
		FROM pg_namespace n, pg_class c, pg_attribute a,
			pg_constraint sridcheck, pg_constraint typecheck
			WHERE ( f_table_schema is NULL
		OR f_table_schema = ''
			OR f_table_schema NOT IN (
					SELECT nspname::varchar
					FROM pg_namespace nn, pg_class cc, pg_attribute aa
					WHERE cc.relnamespace = nn.oid
					AND cc.relname = f_table_name::name
					AND aa.attrelid = cc.oid
					AND aa.attname = f_geometry_column::name))
			AND f_table_name::name = c.relname
			AND c.oid = a.attrelid
			AND c.relnamespace = n.oid
			AND f_geometry_column::name = a.attname

			AND sridcheck.conrelid = c.oid
		AND sridcheck.consrc LIKE '(srid(% = %)'
			AND sridcheck.consrc ~ textcat(' = ', srid::text)

			AND typecheck.conrelid = c.oid
		AND typecheck.consrc LIKE
		'((geometrytype(%) = ''%''::text) OR (% IS NULL))'
			AND typecheck.consrc ~ textcat(' = ''', type::text)

			AND NOT EXISTS (
					SELECT oid FROM geometry_columns gc
					WHERE c.relname::varchar = gc.f_table_name
					AND n.nspname::varchar = gc.f_table_schema
					AND a.attname::varchar = gc.f_geometry_column
			);

	GET DIAGNOSTICS foundschema = ROW_COUNT;

	-- no linkage to system table needed
	return 'fixed:'||foundschema::text;

END;
$$;


ALTER FUNCTION public.fix_geometry_columns() OWNER TO postgres;

--
-- Name: force_2d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION force_2d(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_2d';


ALTER FUNCTION public.force_2d(geometry) OWNER TO postgres;

--
-- Name: force_3d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION force_3d(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_3dz';


ALTER FUNCTION public.force_3d(geometry) OWNER TO postgres;

--
-- Name: force_3dm(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION force_3dm(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_3dm';


ALTER FUNCTION public.force_3dm(geometry) OWNER TO postgres;

--
-- Name: force_3dz(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION force_3dz(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_3dz';


ALTER FUNCTION public.force_3dz(geometry) OWNER TO postgres;

--
-- Name: force_4d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION force_4d(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_4d';


ALTER FUNCTION public.force_4d(geometry) OWNER TO postgres;

--
-- Name: force_collection(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION force_collection(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_collection';


ALTER FUNCTION public.force_collection(geometry) OWNER TO postgres;

--
-- Name: forcerhr(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION forcerhr(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_forceRHR_poly';


ALTER FUNCTION public.forcerhr(geometry) OWNER TO postgres;

--
-- Name: geography(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography(geometry) RETURNS geography
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_from_geometry';


ALTER FUNCTION public.geography(geometry) OWNER TO postgres;

--
-- Name: geography(geography, integer, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography(geography, integer, boolean) RETURNS geography
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_enforce_typmod';


ALTER FUNCTION public.geography(geography, integer, boolean) OWNER TO postgres;

--
-- Name: geography_cmp(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_cmp(geography, geography) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_cmp';


ALTER FUNCTION public.geography_cmp(geography, geography) OWNER TO postgres;

--
-- Name: geography_eq(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_eq(geography, geography) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_eq';


ALTER FUNCTION public.geography_eq(geography, geography) OWNER TO postgres;

--
-- Name: geography_ge(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_ge(geography, geography) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_ge';


ALTER FUNCTION public.geography_ge(geography, geography) OWNER TO postgres;

--
-- Name: geography_gist_compress(internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_gist_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'geography_gist_compress';


ALTER FUNCTION public.geography_gist_compress(internal) OWNER TO postgres;

--
-- Name: geography_gist_consistent(internal, geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_gist_consistent(internal, geometry, integer) RETURNS boolean
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'geography_gist_consistent';


ALTER FUNCTION public.geography_gist_consistent(internal, geometry, integer) OWNER TO postgres;

--
-- Name: geography_gist_decompress(internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_gist_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'geography_gist_decompress';


ALTER FUNCTION public.geography_gist_decompress(internal) OWNER TO postgres;

--
-- Name: geography_gist_join_selectivity(internal, oid, internal, smallint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_gist_join_selectivity(internal, oid, internal, smallint) RETURNS double precision
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'geography_gist_join_selectivity';


ALTER FUNCTION public.geography_gist_join_selectivity(internal, oid, internal, smallint) OWNER TO postgres;

--
-- Name: geography_gist_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_gist_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'geography_gist_penalty';


ALTER FUNCTION public.geography_gist_penalty(internal, internal, internal) OWNER TO postgres;

--
-- Name: geography_gist_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_gist_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'geography_gist_picksplit';


ALTER FUNCTION public.geography_gist_picksplit(internal, internal) OWNER TO postgres;

--
-- Name: geography_gist_same(box2d, box2d, internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_gist_same(box2d, box2d, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'geography_gist_same';


ALTER FUNCTION public.geography_gist_same(box2d, box2d, internal) OWNER TO postgres;

--
-- Name: geography_gist_selectivity(internal, oid, internal, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_gist_selectivity(internal, oid, internal, integer) RETURNS double precision
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'geography_gist_selectivity';


ALTER FUNCTION public.geography_gist_selectivity(internal, oid, internal, integer) OWNER TO postgres;

--
-- Name: geography_gist_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_gist_union(bytea, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'geography_gist_union';


ALTER FUNCTION public.geography_gist_union(bytea, internal) OWNER TO postgres;

--
-- Name: geography_gt(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_gt(geography, geography) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_gt';


ALTER FUNCTION public.geography_gt(geography, geography) OWNER TO postgres;

--
-- Name: geography_le(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_le(geography, geography) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_le';


ALTER FUNCTION public.geography_le(geography, geography) OWNER TO postgres;

--
-- Name: geography_lt(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_lt(geography, geography) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_lt';


ALTER FUNCTION public.geography_lt(geography, geography) OWNER TO postgres;

--
-- Name: geography_overlaps(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_overlaps(geography, geography) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_overlaps';


ALTER FUNCTION public.geography_overlaps(geography, geography) OWNER TO postgres;

--
-- Name: geography_typmod_dims(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_typmod_dims(integer) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_typmod_dims';


ALTER FUNCTION public.geography_typmod_dims(integer) OWNER TO postgres;

--
-- Name: geography_typmod_srid(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_typmod_srid(integer) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_typmod_srid';


ALTER FUNCTION public.geography_typmod_srid(integer) OWNER TO postgres;

--
-- Name: geography_typmod_type(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geography_typmod_type(integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_typmod_type';


ALTER FUNCTION public.geography_typmod_type(integer) OWNER TO postgres;

--
-- Name: geomcollfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomcollfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE
	WHEN geometrytype(GeomFromText($1)) = 'GEOMETRYCOLLECTION'
	THEN GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.geomcollfromtext(text) OWNER TO postgres;

--
-- Name: geomcollfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomcollfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE
	WHEN geometrytype(GeomFromText($1, $2)) = 'GEOMETRYCOLLECTION'
	THEN GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.geomcollfromtext(text, integer) OWNER TO postgres;

--
-- Name: geomcollfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomcollfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE
	WHEN geometrytype(GeomFromWKB($1)) = 'GEOMETRYCOLLECTION'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.geomcollfromwkb(bytea) OWNER TO postgres;

--
-- Name: geomcollfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomcollfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE
	WHEN geometrytype(GeomFromWKB($1, $2)) = 'GEOMETRYCOLLECTION'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.geomcollfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: geometry(box3d_extent); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry(box3d_extent) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_to_LWGEOM';


ALTER FUNCTION public.geometry(box3d_extent) OWNER TO postgres;

--
-- Name: geometry(box2d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry(box2d) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_to_LWGEOM';


ALTER FUNCTION public.geometry(box2d) OWNER TO postgres;

--
-- Name: geometry(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry(box3d) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_to_LWGEOM';


ALTER FUNCTION public.geometry(box3d) OWNER TO postgres;

--
-- Name: geometry(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'parse_WKT_lwgeom';


ALTER FUNCTION public.geometry(text) OWNER TO postgres;

--
-- Name: geometry(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry(chip) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_to_LWGEOM';


ALTER FUNCTION public.geometry(chip) OWNER TO postgres;

--
-- Name: geometry(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry(bytea) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_bytea';


ALTER FUNCTION public.geometry(bytea) OWNER TO postgres;

--
-- Name: geometry(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry(geography) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geometry_from_geography';


ALTER FUNCTION public.geometry(geography) OWNER TO postgres;

--
-- Name: geometry_above(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_above(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_above';


ALTER FUNCTION public.geometry_above(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_below(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_below(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_below';


ALTER FUNCTION public.geometry_below(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_cmp(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_cmp(geometry, geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_cmp';


ALTER FUNCTION public.geometry_cmp(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_contain(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_contain(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_contain';


ALTER FUNCTION public.geometry_contain(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_contained(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_contained(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_contained';


ALTER FUNCTION public.geometry_contained(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_eq(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_eq(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_eq';


ALTER FUNCTION public.geometry_eq(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_ge(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_ge(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_ge';


ALTER FUNCTION public.geometry_ge(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_gist_joinsel(internal, oid, internal, smallint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_gist_joinsel(internal, oid, internal, smallint) RETURNS double precision
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_joinsel';


ALTER FUNCTION public.geometry_gist_joinsel(internal, oid, internal, smallint) OWNER TO postgres;

--
-- Name: geometry_gist_sel(internal, oid, internal, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_gist_sel(internal, oid, internal, integer) RETURNS double precision
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_sel';


ALTER FUNCTION public.geometry_gist_sel(internal, oid, internal, integer) OWNER TO postgres;

--
-- Name: geometry_gt(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_gt(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_gt';


ALTER FUNCTION public.geometry_gt(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_le(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_le(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_le';


ALTER FUNCTION public.geometry_le(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_left(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_left(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_left';


ALTER FUNCTION public.geometry_left(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_lt(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_lt(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_lt';


ALTER FUNCTION public.geometry_lt(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_overabove(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_overabove(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_overabove';


ALTER FUNCTION public.geometry_overabove(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_overbelow(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_overbelow(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_overbelow';


ALTER FUNCTION public.geometry_overbelow(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_overlap(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_overlap(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_overlap';


ALTER FUNCTION public.geometry_overlap(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_overleft(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_overleft(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_overleft';


ALTER FUNCTION public.geometry_overleft(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_overright(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_overright(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_overright';


ALTER FUNCTION public.geometry_overright(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_right(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_right(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_right';


ALTER FUNCTION public.geometry_right(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_same(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_same(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_samebox';


ALTER FUNCTION public.geometry_same(geometry, geometry) OWNER TO postgres;

--
-- Name: geometry_samebox(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometry_samebox(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_samebox';


ALTER FUNCTION public.geometry_samebox(geometry, geometry) OWNER TO postgres;

--
-- Name: geometryfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometryfromtext(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_text';


ALTER FUNCTION public.geometryfromtext(text) OWNER TO postgres;

--
-- Name: geometryfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometryfromtext(text, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_text';


ALTER FUNCTION public.geometryfromtext(text, integer) OWNER TO postgres;

--
-- Name: geometryn(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometryn(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_geometryn_collection';


ALTER FUNCTION public.geometryn(geometry, integer) OWNER TO postgres;

--
-- Name: geometrytype(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geometrytype(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_getTYPE';


ALTER FUNCTION public.geometrytype(geometry) OWNER TO postgres;

--
-- Name: geomfromewkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomfromewkb(bytea) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOMFromWKB';


ALTER FUNCTION public.geomfromewkb(bytea) OWNER TO postgres;

--
-- Name: geomfromewkt(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomfromewkt(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'parse_WKT_lwgeom';


ALTER FUNCTION public.geomfromewkt(text) OWNER TO postgres;

--
-- Name: geomfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT geometryfromtext($1)$_$;


ALTER FUNCTION public.geomfromtext(text) OWNER TO postgres;

--
-- Name: geomfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT geometryfromtext($1, $2)$_$;


ALTER FUNCTION public.geomfromtext(text, integer) OWNER TO postgres;

--
-- Name: geomfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomfromwkb(bytea) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_WKB';


ALTER FUNCTION public.geomfromwkb(bytea) OWNER TO postgres;

--
-- Name: geomfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT setSRID(GeomFromWKB($1), $2)$_$;


ALTER FUNCTION public.geomfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: geomunion(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION geomunion(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geomunion';


ALTER FUNCTION public.geomunion(geometry, geometry) OWNER TO postgres;

--
-- Name: get_proj4_from_srid(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION get_proj4_from_srid(integer) RETURNS text
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
BEGIN
	RETURN proj4text::text FROM spatial_ref_sys WHERE srid= $1;
END;
$_$;


ALTER FUNCTION public.get_proj4_from_srid(integer) OWNER TO postgres;

--
-- Name: getbbox(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getbbox(geometry) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_BOX2DFLOAT4';


ALTER FUNCTION public.getbbox(geometry) OWNER TO postgres;

--
-- Name: getsrid(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getsrid(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_getSRID';


ALTER FUNCTION public.getsrid(geometry) OWNER TO postgres;

--
-- Name: gettransactionid(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION gettransactionid() RETURNS xid
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'getTransactionID';


ALTER FUNCTION public.gettransactionid() OWNER TO postgres;

--
-- Name: hasbbox(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION hasbbox(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_hasBBOX';


ALTER FUNCTION public.hasbbox(geometry) OWNER TO postgres;

--
-- Name: height(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION height(chip) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getHeight';


ALTER FUNCTION public.height(chip) OWNER TO postgres;

--
-- Name: interiorringn(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION interiorringn(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_interiorringn_polygon';


ALTER FUNCTION public.interiorringn(geometry, integer) OWNER TO postgres;

--
-- Name: intersection(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION intersection(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'intersection';


ALTER FUNCTION public.intersection(geometry, geometry) OWNER TO postgres;

--
-- Name: intersects(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION intersects(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'intersects';


ALTER FUNCTION public.intersects(geometry, geometry) OWNER TO postgres;

--
-- Name: isclosed(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION isclosed(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_isclosed_linestring';


ALTER FUNCTION public.isclosed(geometry) OWNER TO postgres;

--
-- Name: isempty(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION isempty(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_isempty';


ALTER FUNCTION public.isempty(geometry) OWNER TO postgres;

--
-- Name: isring(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION isring(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'isring';


ALTER FUNCTION public.isring(geometry) OWNER TO postgres;

--
-- Name: issimple(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION issimple(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'issimple';


ALTER FUNCTION public.issimple(geometry) OWNER TO postgres;

--
-- Name: isvalid(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION isvalid(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'isvalid';


ALTER FUNCTION public.isvalid(geometry) OWNER TO postgres;

--
-- Name: length(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION length(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_length_linestring';


ALTER FUNCTION public.length(geometry) OWNER TO postgres;

--
-- Name: length2d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION length2d(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_length2d_linestring';


ALTER FUNCTION public.length2d(geometry) OWNER TO postgres;

--
-- Name: length2d_spheroid(geometry, spheroid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION length2d_spheroid(geometry, spheroid) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_length2d_ellipsoid';


ALTER FUNCTION public.length2d_spheroid(geometry, spheroid) OWNER TO postgres;

--
-- Name: length3d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION length3d(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_length_linestring';


ALTER FUNCTION public.length3d(geometry) OWNER TO postgres;

--
-- Name: length3d_spheroid(geometry, spheroid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION length3d_spheroid(geometry, spheroid) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_length_ellipsoid_linestring';


ALTER FUNCTION public.length3d_spheroid(geometry, spheroid) OWNER TO postgres;

--
-- Name: length_spheroid(geometry, spheroid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION length_spheroid(geometry, spheroid) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_length_ellipsoid_linestring';


ALTER FUNCTION public.length_spheroid(geometry, spheroid) OWNER TO postgres;

--
-- Name: line_interpolate_point(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION line_interpolate_point(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_line_interpolate_point';


ALTER FUNCTION public.line_interpolate_point(geometry, double precision) OWNER TO postgres;

--
-- Name: line_locate_point(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION line_locate_point(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_line_locate_point';


ALTER FUNCTION public.line_locate_point(geometry, geometry) OWNER TO postgres;

--
-- Name: line_substring(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION line_substring(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_line_substring';


ALTER FUNCTION public.line_substring(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: linefrommultipoint(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION linefrommultipoint(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_line_from_mpoint';


ALTER FUNCTION public.linefrommultipoint(geometry) OWNER TO postgres;

--
-- Name: linefromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION linefromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1)) = 'LINESTRING'
	THEN GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.linefromtext(text) OWNER TO postgres;

--
-- Name: linefromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION linefromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1, $2)) = 'LINESTRING'
	THEN GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.linefromtext(text, integer) OWNER TO postgres;

--
-- Name: linefromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION linefromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'LINESTRING'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.linefromwkb(bytea) OWNER TO postgres;

--
-- Name: linefromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION linefromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1, $2)) = 'LINESTRING'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.linefromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: linemerge(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION linemerge(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'linemerge';


ALTER FUNCTION public.linemerge(geometry) OWNER TO postgres;

--
-- Name: linestringfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION linestringfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT LineFromText($1)$_$;


ALTER FUNCTION public.linestringfromtext(text) OWNER TO postgres;

--
-- Name: linestringfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION linestringfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT LineFromText($1, $2)$_$;


ALTER FUNCTION public.linestringfromtext(text, integer) OWNER TO postgres;

--
-- Name: linestringfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION linestringfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'LINESTRING'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.linestringfromwkb(bytea) OWNER TO postgres;

--
-- Name: linestringfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION linestringfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1, $2)) = 'LINESTRING'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.linestringfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: locate_along_measure(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION locate_along_measure(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT locate_between_measures($1, $2, $2) $_$;


ALTER FUNCTION public.locate_along_measure(geometry, double precision) OWNER TO postgres;

--
-- Name: locate_between_measures(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION locate_between_measures(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_locate_between_m';


ALTER FUNCTION public.locate_between_measures(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: lockrow(text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lockrow(text, text, text) RETURNS integer
    LANGUAGE sql STRICT
    AS $_$ SELECT LockRow(current_schema(), $1, $2, $3, now()::timestamp+'1:00'); $_$;


ALTER FUNCTION public.lockrow(text, text, text) OWNER TO postgres;

--
-- Name: lockrow(text, text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lockrow(text, text, text, text) RETURNS integer
    LANGUAGE sql STRICT
    AS $_$ SELECT LockRow($1, $2, $3, $4, now()::timestamp+'1:00'); $_$;


ALTER FUNCTION public.lockrow(text, text, text, text) OWNER TO postgres;

--
-- Name: lockrow(text, text, text, timestamp without time zone); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lockrow(text, text, text, timestamp without time zone) RETURNS integer
    LANGUAGE sql STRICT
    AS $_$ SELECT LockRow(current_schema(), $1, $2, $3, $4); $_$;


ALTER FUNCTION public.lockrow(text, text, text, timestamp without time zone) OWNER TO postgres;

--
-- Name: lockrow(text, text, text, text, timestamp without time zone); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lockrow(text, text, text, text, timestamp without time zone) RETURNS integer
    LANGUAGE plpgsql STRICT
    AS $_$ 
DECLARE
	myschema alias for $1;
	mytable alias for $2;
	myrid   alias for $3;
	authid alias for $4;
	expires alias for $5;
	ret int;
	mytoid oid;
	myrec RECORD;
	
BEGIN

	IF NOT LongTransactionsEnabled() THEN
		RAISE EXCEPTION 'Long transaction support disabled, use EnableLongTransaction() to enable.';
	END IF;

	EXECUTE 'DELETE FROM authorization_table WHERE expires < now()'; 

	SELECT c.oid INTO mytoid FROM pg_class c, pg_namespace n
		WHERE c.relname = mytable
		AND c.relnamespace = n.oid
		AND n.nspname = myschema;

	-- RAISE NOTICE 'toid: %', mytoid;

	FOR myrec IN SELECT * FROM authorization_table WHERE 
		toid = mytoid AND rid = myrid
	LOOP
		IF myrec.authid != authid THEN
			RETURN 0;
		ELSE
			RETURN 1;
		END IF;
	END LOOP;

	EXECUTE 'INSERT INTO authorization_table VALUES ('||
		quote_literal(mytoid::text)||','||quote_literal(myrid)||
		','||quote_literal(expires::text)||
		','||quote_literal(authid) ||')';

	GET DIAGNOSTICS ret = ROW_COUNT;

	RETURN ret;
END;
$_$;


ALTER FUNCTION public.lockrow(text, text, text, text, timestamp without time zone) OWNER TO postgres;

--
-- Name: longtransactionsenabled(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION longtransactionsenabled() RETURNS boolean
    LANGUAGE plpgsql
    AS $$ 
DECLARE
	rec RECORD;
BEGIN
	FOR rec IN SELECT oid FROM pg_class WHERE relname = 'authorized_tables'
	LOOP
		return 't';
	END LOOP;
	return 'f';
END;
$$;


ALTER FUNCTION public.longtransactionsenabled() OWNER TO postgres;

--
-- Name: lwgeom_gist_compress(internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lwgeom_gist_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_compress';


ALTER FUNCTION public.lwgeom_gist_compress(internal) OWNER TO postgres;

--
-- Name: lwgeom_gist_consistent(internal, geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lwgeom_gist_consistent(internal, geometry, integer) RETURNS boolean
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_consistent';


ALTER FUNCTION public.lwgeom_gist_consistent(internal, geometry, integer) OWNER TO postgres;

--
-- Name: lwgeom_gist_decompress(internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lwgeom_gist_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_decompress';


ALTER FUNCTION public.lwgeom_gist_decompress(internal) OWNER TO postgres;

--
-- Name: lwgeom_gist_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lwgeom_gist_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_penalty';


ALTER FUNCTION public.lwgeom_gist_penalty(internal, internal, internal) OWNER TO postgres;

--
-- Name: lwgeom_gist_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lwgeom_gist_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_picksplit';


ALTER FUNCTION public.lwgeom_gist_picksplit(internal, internal) OWNER TO postgres;

--
-- Name: lwgeom_gist_same(box2d, box2d, internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lwgeom_gist_same(box2d, box2d, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_same';


ALTER FUNCTION public.lwgeom_gist_same(box2d, box2d, internal) OWNER TO postgres;

--
-- Name: lwgeom_gist_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION lwgeom_gist_union(bytea, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_union';


ALTER FUNCTION public.lwgeom_gist_union(bytea, internal) OWNER TO postgres;

--
-- Name: m(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION m(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_m_point';


ALTER FUNCTION public.m(geometry) OWNER TO postgres;

--
-- Name: makebox2d(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION makebox2d(geometry, geometry) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_construct';


ALTER FUNCTION public.makebox2d(geometry, geometry) OWNER TO postgres;

--
-- Name: makebox3d(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION makebox3d(geometry, geometry) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_construct';


ALTER FUNCTION public.makebox3d(geometry, geometry) OWNER TO postgres;

--
-- Name: makeline(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION makeline(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makeline';


ALTER FUNCTION public.makeline(geometry, geometry) OWNER TO postgres;

--
-- Name: makeline_garray(geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION makeline_garray(geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makeline_garray';


ALTER FUNCTION public.makeline_garray(geometry[]) OWNER TO postgres;

--
-- Name: makepoint(double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION makepoint(double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoint';


ALTER FUNCTION public.makepoint(double precision, double precision) OWNER TO postgres;

--
-- Name: makepoint(double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION makepoint(double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoint';


ALTER FUNCTION public.makepoint(double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: makepoint(double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION makepoint(double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoint';


ALTER FUNCTION public.makepoint(double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: makepointm(double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION makepointm(double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoint3dm';


ALTER FUNCTION public.makepointm(double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: makepolygon(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION makepolygon(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoly';


ALTER FUNCTION public.makepolygon(geometry) OWNER TO postgres;

--
-- Name: makepolygon(geometry, geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION makepolygon(geometry, geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoly';


ALTER FUNCTION public.makepolygon(geometry, geometry[]) OWNER TO postgres;

--
-- Name: max_distance(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION max_distance(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_maxdistance2d_linestring';


ALTER FUNCTION public.max_distance(geometry, geometry) OWNER TO postgres;

--
-- Name: mem_size(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mem_size(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_mem_size';


ALTER FUNCTION public.mem_size(geometry) OWNER TO postgres;

--
-- Name: mlinefromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mlinefromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1)) = 'MULTILINESTRING'
	THEN GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mlinefromtext(text) OWNER TO postgres;

--
-- Name: mlinefromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mlinefromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE
	WHEN geometrytype(GeomFromText($1, $2)) = 'MULTILINESTRING'
	THEN GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mlinefromtext(text, integer) OWNER TO postgres;

--
-- Name: mlinefromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mlinefromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'MULTILINESTRING'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mlinefromwkb(bytea) OWNER TO postgres;

--
-- Name: mlinefromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mlinefromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1, $2)) = 'MULTILINESTRING'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mlinefromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: mpointfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mpointfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1)) = 'MULTIPOINT'
	THEN GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mpointfromtext(text) OWNER TO postgres;

--
-- Name: mpointfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mpointfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1,$2)) = 'MULTIPOINT'
	THEN GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mpointfromtext(text, integer) OWNER TO postgres;

--
-- Name: mpointfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mpointfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'MULTIPOINT'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mpointfromwkb(bytea) OWNER TO postgres;

--
-- Name: mpointfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mpointfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1,$2)) = 'MULTIPOINT'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mpointfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: mpolyfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mpolyfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1)) = 'MULTIPOLYGON'
	THEN GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mpolyfromtext(text) OWNER TO postgres;

--
-- Name: mpolyfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mpolyfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1, $2)) = 'MULTIPOLYGON'
	THEN GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mpolyfromtext(text, integer) OWNER TO postgres;

--
-- Name: mpolyfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mpolyfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'MULTIPOLYGON'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mpolyfromwkb(bytea) OWNER TO postgres;

--
-- Name: mpolyfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION mpolyfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1, $2)) = 'MULTIPOLYGON'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.mpolyfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: multi(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multi(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_multi';


ALTER FUNCTION public.multi(geometry) OWNER TO postgres;

--
-- Name: multilinefromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multilinefromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'MULTILINESTRING'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.multilinefromwkb(bytea) OWNER TO postgres;

--
-- Name: multilinefromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multilinefromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1, $2)) = 'MULTILINESTRING'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.multilinefromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: multilinestringfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multilinestringfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_MLineFromText($1)$_$;


ALTER FUNCTION public.multilinestringfromtext(text) OWNER TO postgres;

--
-- Name: multilinestringfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multilinestringfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT MLineFromText($1, $2)$_$;


ALTER FUNCTION public.multilinestringfromtext(text, integer) OWNER TO postgres;

--
-- Name: multipointfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multipointfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT MPointFromText($1)$_$;


ALTER FUNCTION public.multipointfromtext(text) OWNER TO postgres;

--
-- Name: multipointfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multipointfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT MPointFromText($1, $2)$_$;


ALTER FUNCTION public.multipointfromtext(text, integer) OWNER TO postgres;

--
-- Name: multipointfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multipointfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'MULTIPOINT'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.multipointfromwkb(bytea) OWNER TO postgres;

--
-- Name: multipointfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multipointfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1,$2)) = 'MULTIPOINT'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.multipointfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: multipolyfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multipolyfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'MULTIPOLYGON'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.multipolyfromwkb(bytea) OWNER TO postgres;

--
-- Name: multipolyfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multipolyfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1, $2)) = 'MULTIPOLYGON'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.multipolyfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: multipolygonfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multipolygonfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT MPolyFromText($1)$_$;


ALTER FUNCTION public.multipolygonfromtext(text) OWNER TO postgres;

--
-- Name: multipolygonfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION multipolygonfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT MPolyFromText($1, $2)$_$;


ALTER FUNCTION public.multipolygonfromtext(text, integer) OWNER TO postgres;

--
-- Name: ndims(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION ndims(geometry) RETURNS smallint
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_ndims';


ALTER FUNCTION public.ndims(geometry) OWNER TO postgres;

--
-- Name: noop(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION noop(geometry) RETURNS geometry
    LANGUAGE c STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_noop';


ALTER FUNCTION public.noop(geometry) OWNER TO postgres;

--
-- Name: npoints(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION npoints(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_npoints';


ALTER FUNCTION public.npoints(geometry) OWNER TO postgres;

--
-- Name: nrings(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nrings(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_nrings';


ALTER FUNCTION public.nrings(geometry) OWNER TO postgres;

--
-- Name: numgeometries(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION numgeometries(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_numgeometries_collection';


ALTER FUNCTION public.numgeometries(geometry) OWNER TO postgres;

--
-- Name: numinteriorring(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION numinteriorring(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_numinteriorrings_polygon';


ALTER FUNCTION public.numinteriorring(geometry) OWNER TO postgres;

--
-- Name: numinteriorrings(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION numinteriorrings(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_numinteriorrings_polygon';


ALTER FUNCTION public.numinteriorrings(geometry) OWNER TO postgres;

--
-- Name: numpoints(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION numpoints(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_numpoints_linestring';


ALTER FUNCTION public.numpoints(geometry) OWNER TO postgres;

--
-- Name: overlaps(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION "overlaps"(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'overlaps';


ALTER FUNCTION public."overlaps"(geometry, geometry) OWNER TO postgres;

--
-- Name: perimeter(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION perimeter(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_perimeter_poly';


ALTER FUNCTION public.perimeter(geometry) OWNER TO postgres;

--
-- Name: perimeter2d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION perimeter2d(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_perimeter2d_poly';


ALTER FUNCTION public.perimeter2d(geometry) OWNER TO postgres;

--
-- Name: perimeter3d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION perimeter3d(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_perimeter_poly';


ALTER FUNCTION public.perimeter3d(geometry) OWNER TO postgres;

--
-- Name: pgis_geometry_accum_finalfn(pgis_abs); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pgis_geometry_accum_finalfn(pgis_abs) RETURNS geometry[]
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'pgis_geometry_accum_finalfn';


ALTER FUNCTION public.pgis_geometry_accum_finalfn(pgis_abs) OWNER TO postgres;

--
-- Name: pgis_geometry_accum_transfn(pgis_abs, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pgis_geometry_accum_transfn(pgis_abs, geometry) RETURNS pgis_abs
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'pgis_geometry_accum_transfn';


ALTER FUNCTION public.pgis_geometry_accum_transfn(pgis_abs, geometry) OWNER TO postgres;

--
-- Name: pgis_geometry_collect_finalfn(pgis_abs); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pgis_geometry_collect_finalfn(pgis_abs) RETURNS geometry
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'pgis_geometry_collect_finalfn';


ALTER FUNCTION public.pgis_geometry_collect_finalfn(pgis_abs) OWNER TO postgres;

--
-- Name: pgis_geometry_makeline_finalfn(pgis_abs); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pgis_geometry_makeline_finalfn(pgis_abs) RETURNS geometry
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'pgis_geometry_makeline_finalfn';


ALTER FUNCTION public.pgis_geometry_makeline_finalfn(pgis_abs) OWNER TO postgres;

--
-- Name: pgis_geometry_polygonize_finalfn(pgis_abs); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pgis_geometry_polygonize_finalfn(pgis_abs) RETURNS geometry
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'pgis_geometry_polygonize_finalfn';


ALTER FUNCTION public.pgis_geometry_polygonize_finalfn(pgis_abs) OWNER TO postgres;

--
-- Name: pgis_geometry_union_finalfn(pgis_abs); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pgis_geometry_union_finalfn(pgis_abs) RETURNS geometry
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'pgis_geometry_union_finalfn';


ALTER FUNCTION public.pgis_geometry_union_finalfn(pgis_abs) OWNER TO postgres;

--
-- Name: point_inside_circle(geometry, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION point_inside_circle(geometry, double precision, double precision, double precision) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_inside_circle_point';


ALTER FUNCTION public.point_inside_circle(geometry, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: pointfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pointfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1)) = 'POINT'
	THEN GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.pointfromtext(text) OWNER TO postgres;

--
-- Name: pointfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pointfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1, $2)) = 'POINT'
	THEN GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.pointfromtext(text, integer) OWNER TO postgres;

--
-- Name: pointfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pointfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'POINT'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.pointfromwkb(bytea) OWNER TO postgres;

--
-- Name: pointfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pointfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1, $2)) = 'POINT'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.pointfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: pointn(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pointn(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_pointn_linestring';


ALTER FUNCTION public.pointn(geometry, integer) OWNER TO postgres;

--
-- Name: pointonsurface(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION pointonsurface(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'pointonsurface';


ALTER FUNCTION public.pointonsurface(geometry) OWNER TO postgres;

--
-- Name: polyfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION polyfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1)) = 'POLYGON'
	THEN GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.polyfromtext(text) OWNER TO postgres;

--
-- Name: polyfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION polyfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1, $2)) = 'POLYGON'
	THEN GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.polyfromtext(text, integer) OWNER TO postgres;

--
-- Name: polyfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION polyfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'POLYGON'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.polyfromwkb(bytea) OWNER TO postgres;

--
-- Name: polyfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION polyfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1, $2)) = 'POLYGON'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.polyfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: polygonfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION polygonfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT PolyFromText($1)$_$;


ALTER FUNCTION public.polygonfromtext(text) OWNER TO postgres;

--
-- Name: polygonfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION polygonfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT PolyFromText($1, $2)$_$;


ALTER FUNCTION public.polygonfromtext(text, integer) OWNER TO postgres;

--
-- Name: polygonfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION polygonfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'POLYGON'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.polygonfromwkb(bytea) OWNER TO postgres;

--
-- Name: polygonfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION polygonfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1,$2)) = 'POLYGON'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.polygonfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: polygonize_garray(geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION polygonize_garray(geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'polygonize_garray';


ALTER FUNCTION public.polygonize_garray(geometry[]) OWNER TO postgres;

--
-- Name: populate_geometry_columns(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION populate_geometry_columns() RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
	inserted    integer;
	oldcount    integer;
	probed      integer;
	stale       integer;
	gcs         RECORD;
	gc          RECORD;
	gsrid       integer;
	gndims      integer;
	gtype       text;
	query       text;
	gc_is_valid boolean;

BEGIN
	SELECT count(*) INTO oldcount FROM geometry_columns;
	inserted := 0;

	EXECUTE 'TRUNCATE geometry_columns';

	-- Count the number of geometry columns in all tables and views
	SELECT count(DISTINCT c.oid) INTO probed
	FROM pg_class c,
		 pg_attribute a,
		 pg_type t,
		 pg_namespace n
	WHERE (c.relkind = 'r' OR c.relkind = 'v')
	AND t.typname = 'geometry'
	AND a.attisdropped = false
	AND a.atttypid = t.oid
	AND a.attrelid = c.oid
	AND c.relnamespace = n.oid
	AND n.nspname NOT ILIKE 'pg_temp%';

	-- Iterate through all non-dropped geometry columns
	RAISE DEBUG 'Processing Tables.....';

	FOR gcs IN
	SELECT DISTINCT ON (c.oid) c.oid, n.nspname, c.relname
		FROM pg_class c,
			 pg_attribute a,
			 pg_type t,
			 pg_namespace n
		WHERE c.relkind = 'r'
		AND t.typname = 'geometry'
		AND a.attisdropped = false
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid
		AND c.relnamespace = n.oid
		AND n.nspname NOT ILIKE 'pg_temp%'
	LOOP

	inserted := inserted + populate_geometry_columns(gcs.oid);
	END LOOP;

	-- Add views to geometry columns table
	RAISE DEBUG 'Processing Views.....';
	FOR gcs IN
	SELECT DISTINCT ON (c.oid) c.oid, n.nspname, c.relname
		FROM pg_class c,
			 pg_attribute a,
			 pg_type t,
			 pg_namespace n
		WHERE c.relkind = 'v'
		AND t.typname = 'geometry'
		AND a.attisdropped = false
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid
		AND c.relnamespace = n.oid
	LOOP

	inserted := inserted + populate_geometry_columns(gcs.oid);
	END LOOP;

	IF oldcount > inserted THEN
	stale = oldcount-inserted;
	ELSE
	stale = 0;
	END IF;

	RETURN 'probed:' ||probed|| ' inserted:'||inserted|| ' conflicts:'||probed-inserted|| ' deleted:'||stale;
END

$$;


ALTER FUNCTION public.populate_geometry_columns() OWNER TO postgres;

--
-- Name: populate_geometry_columns(oid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION populate_geometry_columns(tbl_oid oid) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
	gcs         RECORD;
	gc          RECORD;
	gsrid       integer;
	gndims      integer;
	gtype       text;
	query       text;
	gc_is_valid boolean;
	inserted    integer;

BEGIN
	inserted := 0;

	-- Iterate through all geometry columns in this table
	FOR gcs IN
	SELECT n.nspname, c.relname, a.attname
		FROM pg_class c,
			 pg_attribute a,
			 pg_type t,
			 pg_namespace n
		WHERE c.relkind = 'r'
		AND t.typname = 'geometry'
		AND a.attisdropped = false
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid
		AND c.relnamespace = n.oid
		AND n.nspname NOT ILIKE 'pg_temp%'
		AND c.oid = tbl_oid
	LOOP

	RAISE DEBUG 'Processing table %.%.%', gcs.nspname, gcs.relname, gcs.attname;

	DELETE FROM geometry_columns
	  WHERE f_table_schema = gcs.nspname
	  AND f_table_name = gcs.relname
	  AND f_geometry_column = gcs.attname;

	gc_is_valid := true;

	-- Try to find srid check from system tables (pg_constraint)
	gsrid :=
		(SELECT replace(replace(split_part(s.consrc, ' = ', 2), ')', ''), '(', '')
		 FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
		 WHERE n.nspname = gcs.nspname
		 AND c.relname = gcs.relname
		 AND a.attname = gcs.attname
		 AND a.attrelid = c.oid
		 AND s.connamespace = n.oid
		 AND s.conrelid = c.oid
		 AND a.attnum = ANY (s.conkey)
		 AND s.consrc LIKE '%srid(% = %');
	IF (gsrid IS NULL) THEN
		-- Try to find srid from the geometry itself
		EXECUTE 'SELECT srid(' || quote_ident(gcs.attname) || ')
				 FROM ONLY ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
				 WHERE ' || quote_ident(gcs.attname) || ' IS NOT NULL LIMIT 1'
			INTO gc;
		gsrid := gc.srid;

		-- Try to apply srid check to column
		IF (gsrid IS NOT NULL) THEN
			BEGIN
				EXECUTE 'ALTER TABLE ONLY ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
						 ADD CONSTRAINT ' || quote_ident('enforce_srid_' || gcs.attname) || '
						 CHECK (srid(' || quote_ident(gcs.attname) || ') = ' || gsrid || ')';
			EXCEPTION
				WHEN check_violation THEN
					RAISE WARNING 'Not inserting ''%'' in ''%.%'' into geometry_columns: could not apply constraint CHECK (srid(%) = %)', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname), quote_ident(gcs.attname), gsrid;
					gc_is_valid := false;
			END;
		END IF;
	END IF;

	-- Try to find ndims check from system tables (pg_constraint)
	gndims :=
		(SELECT replace(split_part(s.consrc, ' = ', 2), ')', '')
		 FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
		 WHERE n.nspname = gcs.nspname
		 AND c.relname = gcs.relname
		 AND a.attname = gcs.attname
		 AND a.attrelid = c.oid
		 AND s.connamespace = n.oid
		 AND s.conrelid = c.oid
		 AND a.attnum = ANY (s.conkey)
		 AND s.consrc LIKE '%ndims(% = %');
	IF (gndims IS NULL) THEN
		-- Try to find ndims from the geometry itself
		EXECUTE 'SELECT ndims(' || quote_ident(gcs.attname) || ')
				 FROM ONLY ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
				 WHERE ' || quote_ident(gcs.attname) || ' IS NOT NULL LIMIT 1'
			INTO gc;
		gndims := gc.ndims;

		-- Try to apply ndims check to column
		IF (gndims IS NOT NULL) THEN
			BEGIN
				EXECUTE 'ALTER TABLE ONLY ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
						 ADD CONSTRAINT ' || quote_ident('enforce_dims_' || gcs.attname) || '
						 CHECK (ndims(' || quote_ident(gcs.attname) || ') = '||gndims||')';
			EXCEPTION
				WHEN check_violation THEN
					RAISE WARNING 'Not inserting ''%'' in ''%.%'' into geometry_columns: could not apply constraint CHECK (ndims(%) = %)', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname), quote_ident(gcs.attname), gndims;
					gc_is_valid := false;
			END;
		END IF;
	END IF;

	-- Try to find geotype check from system tables (pg_constraint)
	gtype :=
		(SELECT replace(split_part(s.consrc, '''', 2), ')', '')
		 FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
		 WHERE n.nspname = gcs.nspname
		 AND c.relname = gcs.relname
		 AND a.attname = gcs.attname
		 AND a.attrelid = c.oid
		 AND s.connamespace = n.oid
		 AND s.conrelid = c.oid
		 AND a.attnum = ANY (s.conkey)
		 AND s.consrc LIKE '%geometrytype(% = %');
	IF (gtype IS NULL) THEN
		-- Try to find geotype from the geometry itself
		EXECUTE 'SELECT geometrytype(' || quote_ident(gcs.attname) || ')
				 FROM ONLY ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
				 WHERE ' || quote_ident(gcs.attname) || ' IS NOT NULL LIMIT 1'
			INTO gc;
		gtype := gc.geometrytype;
		--IF (gtype IS NULL) THEN
		--    gtype := 'GEOMETRY';
		--END IF;

		-- Try to apply geometrytype check to column
		IF (gtype IS NOT NULL) THEN
			BEGIN
				EXECUTE 'ALTER TABLE ONLY ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
				ADD CONSTRAINT ' || quote_ident('enforce_geotype_' || gcs.attname) || '
				CHECK ((geometrytype(' || quote_ident(gcs.attname) || ') = ' || quote_literal(gtype) || ') OR (' || quote_ident(gcs.attname) || ' IS NULL))';
			EXCEPTION
				WHEN check_violation THEN
					-- No geometry check can be applied. This column contains a number of geometry types.
					RAISE WARNING 'Could not add geometry type check (%) to table column: %.%.%', gtype, quote_ident(gcs.nspname),quote_ident(gcs.relname),quote_ident(gcs.attname);
			END;
		END IF;
	END IF;

	IF (gsrid IS NULL) THEN
		RAISE WARNING 'Not inserting ''%'' in ''%.%'' into geometry_columns: could not determine the srid', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname);
	ELSIF (gndims IS NULL) THEN
		RAISE WARNING 'Not inserting ''%'' in ''%.%'' into geometry_columns: could not determine the number of dimensions', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname);
	ELSIF (gtype IS NULL) THEN
		RAISE WARNING 'Not inserting ''%'' in ''%.%'' into geometry_columns: could not determine the geometry type', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname);
	ELSE
		-- Only insert into geometry_columns if table constraints could be applied.
		IF (gc_is_valid) THEN
			INSERT INTO geometry_columns (f_table_catalog,f_table_schema, f_table_name, f_geometry_column, coord_dimension, srid, type)
			VALUES ('', gcs.nspname, gcs.relname, gcs.attname, gndims, gsrid, gtype);
			inserted := inserted + 1;
		END IF;
	END IF;
	END LOOP;

	-- Add views to geometry columns table
	FOR gcs IN
	SELECT n.nspname, c.relname, a.attname
		FROM pg_class c,
			 pg_attribute a,
			 pg_type t,
			 pg_namespace n
		WHERE c.relkind = 'v'
		AND t.typname = 'geometry'
		AND a.attisdropped = false
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid
		AND c.relnamespace = n.oid
		AND n.nspname NOT ILIKE 'pg_temp%'
		AND c.oid = tbl_oid
	LOOP
		RAISE DEBUG 'Processing view %.%.%', gcs.nspname, gcs.relname, gcs.attname;

	DELETE FROM geometry_columns
	  WHERE f_table_schema = gcs.nspname
	  AND f_table_name = gcs.relname
	  AND f_geometry_column = gcs.attname;
	  
		EXECUTE 'SELECT ndims(' || quote_ident(gcs.attname) || ')
				 FROM ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
				 WHERE ' || quote_ident(gcs.attname) || ' IS NOT NULL LIMIT 1'
			INTO gc;
		gndims := gc.ndims;

		EXECUTE 'SELECT srid(' || quote_ident(gcs.attname) || ')
				 FROM ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
				 WHERE ' || quote_ident(gcs.attname) || ' IS NOT NULL LIMIT 1'
			INTO gc;
		gsrid := gc.srid;

		EXECUTE 'SELECT geometrytype(' || quote_ident(gcs.attname) || ')
				 FROM ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
				 WHERE ' || quote_ident(gcs.attname) || ' IS NOT NULL LIMIT 1'
			INTO gc;
		gtype := gc.geometrytype;

		IF (gndims IS NULL) THEN
			RAISE WARNING 'Not inserting ''%'' in ''%.%'' into geometry_columns: could not determine ndims', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname);
		ELSIF (gsrid IS NULL) THEN
			RAISE WARNING 'Not inserting ''%'' in ''%.%'' into geometry_columns: could not determine srid', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname);
		ELSIF (gtype IS NULL) THEN
			RAISE WARNING 'Not inserting ''%'' in ''%.%'' into geometry_columns: could not determine gtype', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname);
		ELSE
			query := 'INSERT INTO geometry_columns (f_table_catalog,f_table_schema, f_table_name, f_geometry_column, coord_dimension, srid, type) ' ||
					 'VALUES ('''', ' || quote_literal(gcs.nspname) || ',' || quote_literal(gcs.relname) || ',' || quote_literal(gcs.attname) || ',' || gndims || ',' || gsrid || ',' || quote_literal(gtype) || ')';
			EXECUTE query;
			inserted := inserted + 1;
		END IF;
	END LOOP;

	RETURN inserted;
END

$$;


ALTER FUNCTION public.populate_geometry_columns(tbl_oid oid) OWNER TO postgres;

--
-- Name: postgis_addbbox(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_addbbox(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_addBBOX';


ALTER FUNCTION public.postgis_addbbox(geometry) OWNER TO postgres;

--
-- Name: postgis_cache_bbox(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_cache_bbox() RETURNS trigger
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'cache_bbox';


ALTER FUNCTION public.postgis_cache_bbox() OWNER TO postgres;

--
-- Name: postgis_dropbbox(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_dropbbox(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_dropBBOX';


ALTER FUNCTION public.postgis_dropbbox(geometry) OWNER TO postgres;

--
-- Name: postgis_full_version(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_full_version() RETURNS text
    LANGUAGE plpgsql IMMUTABLE
    AS $$
DECLARE
	libver text;
	projver text;
	geosver text;
	libxmlver text;
	usestats bool;
	dbproc text;
	relproc text;
	fullver text;
BEGIN
	SELECT postgis_lib_version() INTO libver;
	SELECT postgis_proj_version() INTO projver;
	SELECT postgis_geos_version() INTO geosver;
	SELECT postgis_libxml_version() INTO libxmlver;
	SELECT postgis_uses_stats() INTO usestats;
	SELECT postgis_scripts_installed() INTO dbproc;
	SELECT postgis_scripts_released() INTO relproc;

	fullver = 'POSTGIS="' || libver || '"';

	IF  geosver IS NOT NULL THEN
		fullver = fullver || ' GEOS="' || geosver || '"';
	END IF;

	IF  projver IS NOT NULL THEN
		fullver = fullver || ' PROJ="' || projver || '"';
	END IF;

	IF  libxmlver IS NOT NULL THEN
		fullver = fullver || ' LIBXML="' || libxmlver || '"';
	END IF;

	IF usestats THEN
		fullver = fullver || ' USE_STATS';
	END IF;

	-- fullver = fullver || ' DBPROC="' || dbproc || '"';
	-- fullver = fullver || ' RELPROC="' || relproc || '"';

	IF dbproc != relproc THEN
		fullver = fullver || ' (procs from ' || dbproc || ' need upgrade)';
	END IF;

	RETURN fullver;
END
$$;


ALTER FUNCTION public.postgis_full_version() OWNER TO postgres;

--
-- Name: postgis_geos_version(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_geos_version() RETURNS text
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'postgis_geos_version';


ALTER FUNCTION public.postgis_geos_version() OWNER TO postgres;

--
-- Name: postgis_getbbox(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_getbbox(geometry) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_BOX2DFLOAT4';


ALTER FUNCTION public.postgis_getbbox(geometry) OWNER TO postgres;

--
-- Name: postgis_gist_joinsel(internal, oid, internal, smallint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_gist_joinsel(internal, oid, internal, smallint) RETURNS double precision
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_joinsel';


ALTER FUNCTION public.postgis_gist_joinsel(internal, oid, internal, smallint) OWNER TO postgres;

--
-- Name: postgis_gist_sel(internal, oid, internal, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_gist_sel(internal, oid, internal, integer) RETURNS double precision
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_sel';


ALTER FUNCTION public.postgis_gist_sel(internal, oid, internal, integer) OWNER TO postgres;

--
-- Name: postgis_hasbbox(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_hasbbox(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_hasBBOX';


ALTER FUNCTION public.postgis_hasbbox(geometry) OWNER TO postgres;

--
-- Name: postgis_lib_build_date(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_lib_build_date() RETURNS text
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'postgis_lib_build_date';


ALTER FUNCTION public.postgis_lib_build_date() OWNER TO postgres;

--
-- Name: postgis_lib_version(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_lib_version() RETURNS text
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'postgis_lib_version';


ALTER FUNCTION public.postgis_lib_version() OWNER TO postgres;

--
-- Name: postgis_libxml_version(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_libxml_version() RETURNS text
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'postgis_libxml_version';


ALTER FUNCTION public.postgis_libxml_version() OWNER TO postgres;

--
-- Name: postgis_noop(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_noop(geometry) RETURNS geometry
    LANGUAGE c STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_noop';


ALTER FUNCTION public.postgis_noop(geometry) OWNER TO postgres;

--
-- Name: postgis_proj_version(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_proj_version() RETURNS text
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'postgis_proj_version';


ALTER FUNCTION public.postgis_proj_version() OWNER TO postgres;

--
-- Name: postgis_scripts_build_date(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_scripts_build_date() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$SELECT '2011-12-05 14:07:24'::text AS version$$;


ALTER FUNCTION public.postgis_scripts_build_date() OWNER TO postgres;

--
-- Name: postgis_scripts_installed(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_scripts_installed() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$SELECT '1.5 r7360'::text AS version$$;


ALTER FUNCTION public.postgis_scripts_installed() OWNER TO postgres;

--
-- Name: postgis_scripts_released(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_scripts_released() RETURNS text
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'postgis_scripts_released';


ALTER FUNCTION public.postgis_scripts_released() OWNER TO postgres;

--
-- Name: postgis_transform_geometry(geometry, text, text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_transform_geometry(geometry, text, text, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'transform_geom';


ALTER FUNCTION public.postgis_transform_geometry(geometry, text, text, integer) OWNER TO postgres;

--
-- Name: postgis_uses_stats(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_uses_stats() RETURNS boolean
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'postgis_uses_stats';


ALTER FUNCTION public.postgis_uses_stats() OWNER TO postgres;

--
-- Name: postgis_version(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION postgis_version() RETURNS text
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'postgis_version';


ALTER FUNCTION public.postgis_version() OWNER TO postgres;

--
-- Name: probe_geometry_columns(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION probe_geometry_columns() RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
	inserted integer;
	oldcount integer;
	probed integer;
	stale integer;
BEGIN

	SELECT count(*) INTO oldcount FROM geometry_columns;

	SELECT count(*) INTO probed
		FROM pg_class c, pg_attribute a, pg_type t,
			pg_namespace n,
			pg_constraint sridcheck, pg_constraint typecheck

		WHERE t.typname = 'geometry'
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid
		AND c.relnamespace = n.oid
		AND sridcheck.connamespace = n.oid
		AND typecheck.connamespace = n.oid
		AND sridcheck.conrelid = c.oid
		AND sridcheck.consrc LIKE '(srid('||a.attname||') = %)'
		AND typecheck.conrelid = c.oid
		AND typecheck.consrc LIKE
		'((geometrytype('||a.attname||') = ''%''::text) OR (% IS NULL))'
		;

	INSERT INTO geometry_columns SELECT
		''::varchar as f_table_catalogue,
		n.nspname::varchar as f_table_schema,
		c.relname::varchar as f_table_name,
		a.attname::varchar as f_geometry_column,
		2 as coord_dimension,
		trim(both  ' =)' from
			replace(replace(split_part(
				sridcheck.consrc, ' = ', 2), ')', ''), '(', ''))::integer AS srid,
		trim(both ' =)''' from substr(typecheck.consrc,
			strpos(typecheck.consrc, '='),
			strpos(typecheck.consrc, '::')-
			strpos(typecheck.consrc, '=')
			))::varchar as type
		FROM pg_class c, pg_attribute a, pg_type t,
			pg_namespace n,
			pg_constraint sridcheck, pg_constraint typecheck
		WHERE t.typname = 'geometry'
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid
		AND c.relnamespace = n.oid
		AND sridcheck.connamespace = n.oid
		AND typecheck.connamespace = n.oid
		AND sridcheck.conrelid = c.oid
		AND sridcheck.consrc LIKE '(st_srid('||a.attname||') = %)'
		AND typecheck.conrelid = c.oid
		AND typecheck.consrc LIKE
		'((geometrytype('||a.attname||') = ''%''::text) OR (% IS NULL))'

			AND NOT EXISTS (
					SELECT oid FROM geometry_columns gc
					WHERE c.relname::varchar = gc.f_table_name
					AND n.nspname::varchar = gc.f_table_schema
					AND a.attname::varchar = gc.f_geometry_column
			);

	GET DIAGNOSTICS inserted = ROW_COUNT;

	IF oldcount > probed THEN
		stale = oldcount-probed;
	ELSE
		stale = 0;
	END IF;

	RETURN 'probed:'||probed::text||
		' inserted:'||inserted::text||
		' conflicts:'||(probed-inserted)::text||
		' stale:'||stale::text;
END

$$;


ALTER FUNCTION public.probe_geometry_columns() OWNER TO postgres;

--
-- Name: relate(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION relate(geometry, geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'relate_full';


ALTER FUNCTION public.relate(geometry, geometry) OWNER TO postgres;

--
-- Name: relate(geometry, geometry, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION relate(geometry, geometry, text) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'relate_pattern';


ALTER FUNCTION public.relate(geometry, geometry, text) OWNER TO postgres;

--
-- Name: removepoint(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION removepoint(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_removepoint';


ALTER FUNCTION public.removepoint(geometry, integer) OWNER TO postgres;

--
-- Name: rename_geometry_table_constraints(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION rename_geometry_table_constraints() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$
SELECT 'rename_geometry_table_constraint() is obsoleted'::text
$$;


ALTER FUNCTION public.rename_geometry_table_constraints() OWNER TO postgres;

--
-- Name: reverse(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION reverse(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_reverse';


ALTER FUNCTION public.reverse(geometry) OWNER TO postgres;

--
-- Name: rotate(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION rotate(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT rotateZ($1, $2)$_$;


ALTER FUNCTION public.rotate(geometry, double precision) OWNER TO postgres;

--
-- Name: rotatex(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION rotatex(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1, 1, 0, 0, 0, cos($2), -sin($2), 0, sin($2), cos($2), 0, 0, 0)$_$;


ALTER FUNCTION public.rotatex(geometry, double precision) OWNER TO postgres;

--
-- Name: rotatey(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION rotatey(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1,  cos($2), 0, sin($2),  0, 1, 0,  -sin($2), 0, cos($2), 0,  0, 0)$_$;


ALTER FUNCTION public.rotatey(geometry, double precision) OWNER TO postgres;

--
-- Name: rotatez(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION rotatez(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1,  cos($2), -sin($2), 0,  sin($2), cos($2), 0,  0, 0, 1,  0, 0, 0)$_$;


ALTER FUNCTION public.rotatez(geometry, double precision) OWNER TO postgres;

--
-- Name: scale(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION scale(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT scale($1, $2, $3, 1)$_$;


ALTER FUNCTION public.scale(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: scale(geometry, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION scale(geometry, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1,  $2, 0, 0,  0, $3, 0,  0, 0, $4,  0, 0, 0)$_$;


ALTER FUNCTION public.scale(geometry, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: se_envelopesintersect(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION se_envelopesintersect(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ 
	SELECT $1 && $2
	$_$;


ALTER FUNCTION public.se_envelopesintersect(geometry, geometry) OWNER TO postgres;

--
-- Name: se_is3d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION se_is3d(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_hasz';


ALTER FUNCTION public.se_is3d(geometry) OWNER TO postgres;

--
-- Name: se_ismeasured(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION se_ismeasured(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_hasm';


ALTER FUNCTION public.se_ismeasured(geometry) OWNER TO postgres;

--
-- Name: se_locatealong(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION se_locatealong(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT locate_between_measures($1, $2, $2) $_$;


ALTER FUNCTION public.se_locatealong(geometry, double precision) OWNER TO postgres;

--
-- Name: se_locatebetween(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION se_locatebetween(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_locate_between_m';


ALTER FUNCTION public.se_locatebetween(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: se_m(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION se_m(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_m_point';


ALTER FUNCTION public.se_m(geometry) OWNER TO postgres;

--
-- Name: se_z(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION se_z(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_z_point';


ALTER FUNCTION public.se_z(geometry) OWNER TO postgres;

--
-- Name: segmentize(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION segmentize(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_segmentize2d';


ALTER FUNCTION public.segmentize(geometry, double precision) OWNER TO postgres;

--
-- Name: setfactor(chip, real); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION setfactor(chip, real) RETURNS chip
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_setFactor';


ALTER FUNCTION public.setfactor(chip, real) OWNER TO postgres;

--
-- Name: setpoint(geometry, integer, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION setpoint(geometry, integer, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_setpoint_linestring';


ALTER FUNCTION public.setpoint(geometry, integer, geometry) OWNER TO postgres;

--
-- Name: setsrid(chip, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION setsrid(chip, integer) RETURNS chip
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_setSRID';


ALTER FUNCTION public.setsrid(chip, integer) OWNER TO postgres;

--
-- Name: setsrid(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION setsrid(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_setSRID';


ALTER FUNCTION public.setsrid(geometry, integer) OWNER TO postgres;

--
-- Name: shift_longitude(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION shift_longitude(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_longitude_shift';


ALTER FUNCTION public.shift_longitude(geometry) OWNER TO postgres;

--
-- Name: simplify(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION simplify(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_simplify2d';


ALTER FUNCTION public.simplify(geometry, double precision) OWNER TO postgres;

--
-- Name: snaptogrid(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION snaptogrid(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT SnapToGrid($1, 0, 0, $2, $2)$_$;


ALTER FUNCTION public.snaptogrid(geometry, double precision) OWNER TO postgres;

--
-- Name: snaptogrid(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION snaptogrid(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT SnapToGrid($1, 0, 0, $2, $3)$_$;


ALTER FUNCTION public.snaptogrid(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: snaptogrid(geometry, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION snaptogrid(geometry, double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_snaptogrid';


ALTER FUNCTION public.snaptogrid(geometry, double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: snaptogrid(geometry, geometry, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION snaptogrid(geometry, geometry, double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_snaptogrid_pointoff';


ALTER FUNCTION public.snaptogrid(geometry, geometry, double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: srid(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION srid(chip) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getSRID';


ALTER FUNCTION public.srid(chip) OWNER TO postgres;

--
-- Name: srid(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION srid(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_getSRID';


ALTER FUNCTION public.srid(geometry) OWNER TO postgres;

--
-- Name: st_addmeasure(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_addmeasure(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'ST_AddMeasure';


ALTER FUNCTION public.st_addmeasure(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: st_addpoint(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_addpoint(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_addpoint';


ALTER FUNCTION public.st_addpoint(geometry, geometry) OWNER TO postgres;

--
-- Name: st_addpoint(geometry, geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_addpoint(geometry, geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_addpoint';


ALTER FUNCTION public.st_addpoint(geometry, geometry, integer) OWNER TO postgres;

--
-- Name: st_affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1,  $2, $3, 0,  $4, $5, 0,  0, 0, 1,  $6, $7, 0)$_$;


ALTER FUNCTION public.st_affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_affine';


ALTER FUNCTION public.st_affine(geometry, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_area(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_area(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_area_polygon';


ALTER FUNCTION public.st_area(geometry) OWNER TO postgres;

--
-- Name: st_area(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_area(geography) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_Area($1, true)$_$;


ALTER FUNCTION public.st_area(geography) OWNER TO postgres;

--
-- Name: st_area(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_area(text) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Area($1::geometry);  $_$;


ALTER FUNCTION public.st_area(text) OWNER TO postgres;

--
-- Name: st_area(geography, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_area(geography, boolean) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'geography_area';


ALTER FUNCTION public.st_area(geography, boolean) OWNER TO postgres;

--
-- Name: st_area2d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_area2d(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_area_polygon';


ALTER FUNCTION public.st_area2d(geometry) OWNER TO postgres;

--
-- Name: st_asbinary(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asbinary(geometry) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asBinary';


ALTER FUNCTION public.st_asbinary(geometry) OWNER TO postgres;

--
-- Name: st_asbinary(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asbinary(geography) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_as_binary';


ALTER FUNCTION public.st_asbinary(geography) OWNER TO postgres;

--
-- Name: st_asbinary(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asbinary(text) RETURNS bytea
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsBinary($1::geometry);  $_$;


ALTER FUNCTION public.st_asbinary(text) OWNER TO postgres;

--
-- Name: st_asbinary(geometry, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asbinary(geometry, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asBinary';


ALTER FUNCTION public.st_asbinary(geometry, text) OWNER TO postgres;

--
-- Name: st_asewkb(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asewkb(geometry) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'WKBFromLWGEOM';


ALTER FUNCTION public.st_asewkb(geometry) OWNER TO postgres;

--
-- Name: st_asewkb(geometry, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asewkb(geometry, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'WKBFromLWGEOM';


ALTER FUNCTION public.st_asewkb(geometry, text) OWNER TO postgres;

--
-- Name: st_asewkt(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asewkt(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asEWKT';


ALTER FUNCTION public.st_asewkt(geometry) OWNER TO postgres;

--
-- Name: st_asgeojson(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(geometry) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson(1, $1, 15, 0)$_$;


ALTER FUNCTION public.st_asgeojson(geometry) OWNER TO postgres;

--
-- Name: st_asgeojson(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(geography) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson(1, $1, 15, 0)$_$;


ALTER FUNCTION public.st_asgeojson(geography) OWNER TO postgres;

--
-- Name: st_asgeojson(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsGeoJson($1::geometry);  $_$;


ALTER FUNCTION public.st_asgeojson(text) OWNER TO postgres;

--
-- Name: st_asgeojson(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(geometry, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson(1, $1, $2, 0)$_$;


ALTER FUNCTION public.st_asgeojson(geometry, integer) OWNER TO postgres;

--
-- Name: st_asgeojson(integer, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(integer, geometry) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson($1, $2, 15, 0)$_$;


ALTER FUNCTION public.st_asgeojson(integer, geometry) OWNER TO postgres;

--
-- Name: st_asgeojson(geography, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(geography, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson(1, $1, $2, 0)$_$;


ALTER FUNCTION public.st_asgeojson(geography, integer) OWNER TO postgres;

--
-- Name: st_asgeojson(integer, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(integer, geography) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson($1, $2, 15, 0)$_$;


ALTER FUNCTION public.st_asgeojson(integer, geography) OWNER TO postgres;

--
-- Name: st_asgeojson(integer, geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(integer, geometry, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson($1, $2, $3, 0)$_$;


ALTER FUNCTION public.st_asgeojson(integer, geometry, integer) OWNER TO postgres;

--
-- Name: st_asgeojson(geometry, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(geometry, integer, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson(1, $1, $2, $3)$_$;


ALTER FUNCTION public.st_asgeojson(geometry, integer, integer) OWNER TO postgres;

--
-- Name: st_asgeojson(integer, geography, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(integer, geography, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson($1, $2, $3, 0)$_$;


ALTER FUNCTION public.st_asgeojson(integer, geography, integer) OWNER TO postgres;

--
-- Name: st_asgeojson(geography, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(geography, integer, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson(1, $1, $2, $3)$_$;


ALTER FUNCTION public.st_asgeojson(geography, integer, integer) OWNER TO postgres;

--
-- Name: st_asgeojson(integer, geometry, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(integer, geometry, integer, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson($1, $2, $3, $4)$_$;


ALTER FUNCTION public.st_asgeojson(integer, geometry, integer, integer) OWNER TO postgres;

--
-- Name: st_asgeojson(integer, geography, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgeojson(integer, geography, integer, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGeoJson($1, $2, $3, $4)$_$;


ALTER FUNCTION public.st_asgeojson(integer, geography, integer, integer) OWNER TO postgres;

--
-- Name: st_asgml(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(geometry) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML(2, $1, 15, 0)$_$;


ALTER FUNCTION public.st_asgml(geometry) OWNER TO postgres;

--
-- Name: st_asgml(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(geography) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML(2, $1, 15, 0)$_$;


ALTER FUNCTION public.st_asgml(geography) OWNER TO postgres;

--
-- Name: st_asgml(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsGML($1::geometry);  $_$;


ALTER FUNCTION public.st_asgml(text) OWNER TO postgres;

--
-- Name: st_asgml(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(geometry, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML(2, $1, $2, 0)$_$;


ALTER FUNCTION public.st_asgml(geometry, integer) OWNER TO postgres;

--
-- Name: st_asgml(integer, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(integer, geometry) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML($1, $2, 15, 0)$_$;


ALTER FUNCTION public.st_asgml(integer, geometry) OWNER TO postgres;

--
-- Name: st_asgml(geography, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(geography, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML(2, $1, $2, 0)$_$;


ALTER FUNCTION public.st_asgml(geography, integer) OWNER TO postgres;

--
-- Name: st_asgml(integer, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(integer, geography) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML($1, $2, 15, 0)$_$;


ALTER FUNCTION public.st_asgml(integer, geography) OWNER TO postgres;

--
-- Name: st_asgml(integer, geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(integer, geometry, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML($1, $2, $3, 0)$_$;


ALTER FUNCTION public.st_asgml(integer, geometry, integer) OWNER TO postgres;

--
-- Name: st_asgml(geometry, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(geometry, integer, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML(2, $1, $2, $3)$_$;


ALTER FUNCTION public.st_asgml(geometry, integer, integer) OWNER TO postgres;

--
-- Name: st_asgml(integer, geography, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(integer, geography, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML($1, $2, $3, 0)$_$;


ALTER FUNCTION public.st_asgml(integer, geography, integer) OWNER TO postgres;

--
-- Name: st_asgml(geography, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(geography, integer, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML(2, $1, $2, $3)$_$;


ALTER FUNCTION public.st_asgml(geography, integer, integer) OWNER TO postgres;

--
-- Name: st_asgml(integer, geometry, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(integer, geometry, integer, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML($1, $2, $3, $4)$_$;


ALTER FUNCTION public.st_asgml(integer, geometry, integer, integer) OWNER TO postgres;

--
-- Name: st_asgml(integer, geography, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_asgml(integer, geography, integer, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsGML($1, $2, $3, $4)$_$;


ALTER FUNCTION public.st_asgml(integer, geography, integer, integer) OWNER TO postgres;

--
-- Name: st_ashexewkb(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_ashexewkb(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asHEXEWKB';


ALTER FUNCTION public.st_ashexewkb(geometry) OWNER TO postgres;

--
-- Name: st_ashexewkb(geometry, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_ashexewkb(geometry, text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asHEXEWKB';


ALTER FUNCTION public.st_ashexewkb(geometry, text) OWNER TO postgres;

--
-- Name: st_askml(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_askml(geometry) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML(2, ST_Transform($1,4326), 15)$_$;


ALTER FUNCTION public.st_askml(geometry) OWNER TO postgres;

--
-- Name: st_askml(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_askml(geography) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML(2, $1, 15)$_$;


ALTER FUNCTION public.st_askml(geography) OWNER TO postgres;

--
-- Name: st_askml(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_askml(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsKML($1::geometry);  $_$;


ALTER FUNCTION public.st_askml(text) OWNER TO postgres;

--
-- Name: st_askml(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_askml(geometry, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML(2, ST_Transform($1,4326), $2)$_$;


ALTER FUNCTION public.st_askml(geometry, integer) OWNER TO postgres;

--
-- Name: st_askml(integer, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_askml(integer, geometry) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML($1, ST_Transform($2,4326), 15)$_$;


ALTER FUNCTION public.st_askml(integer, geometry) OWNER TO postgres;

--
-- Name: st_askml(geography, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_askml(geography, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML(2, $1, $2)$_$;


ALTER FUNCTION public.st_askml(geography, integer) OWNER TO postgres;

--
-- Name: st_askml(integer, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_askml(integer, geography) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML($1, $2, 15)$_$;


ALTER FUNCTION public.st_askml(integer, geography) OWNER TO postgres;

--
-- Name: st_askml(integer, geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_askml(integer, geometry, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML($1, ST_Transform($2,4326), $3)$_$;


ALTER FUNCTION public.st_askml(integer, geometry, integer) OWNER TO postgres;

--
-- Name: st_askml(integer, geography, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_askml(integer, geography, integer) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_AsKML($1, $2, $3)$_$;


ALTER FUNCTION public.st_askml(integer, geography, integer) OWNER TO postgres;

--
-- Name: st_assvg(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_assvg(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'assvg_geometry';


ALTER FUNCTION public.st_assvg(geometry) OWNER TO postgres;

--
-- Name: st_assvg(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_assvg(geography) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_as_svg';


ALTER FUNCTION public.st_assvg(geography) OWNER TO postgres;

--
-- Name: st_assvg(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_assvg(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsSVG($1::geometry);  $_$;


ALTER FUNCTION public.st_assvg(text) OWNER TO postgres;

--
-- Name: st_assvg(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_assvg(geometry, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'assvg_geometry';


ALTER FUNCTION public.st_assvg(geometry, integer) OWNER TO postgres;

--
-- Name: st_assvg(geography, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_assvg(geography, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_as_svg';


ALTER FUNCTION public.st_assvg(geography, integer) OWNER TO postgres;

--
-- Name: st_assvg(geometry, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_assvg(geometry, integer, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'assvg_geometry';


ALTER FUNCTION public.st_assvg(geometry, integer, integer) OWNER TO postgres;

--
-- Name: st_assvg(geography, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_assvg(geography, integer, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_as_svg';


ALTER FUNCTION public.st_assvg(geography, integer, integer) OWNER TO postgres;

--
-- Name: st_astext(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_astext(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_asText';


ALTER FUNCTION public.st_astext(geometry) OWNER TO postgres;

--
-- Name: st_astext(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_astext(geography) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_as_text';


ALTER FUNCTION public.st_astext(geography) OWNER TO postgres;

--
-- Name: st_astext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_astext(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsText($1::geometry);  $_$;


ALTER FUNCTION public.st_astext(text) OWNER TO postgres;

--
-- Name: st_azimuth(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_azimuth(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_azimuth';


ALTER FUNCTION public.st_azimuth(geometry, geometry) OWNER TO postgres;

--
-- Name: st_bdmpolyfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_bdmpolyfromtext(text, integer) RETURNS geometry
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
DECLARE
	geomtext alias for $1;
	srid alias for $2;
	mline geometry;
	geom geometry;
BEGIN
	mline := ST_MultiLineStringFromText(geomtext, srid);

	IF mline IS NULL
	THEN
		RAISE EXCEPTION 'Input is not a MultiLinestring';
	END IF;

	geom := multi(ST_BuildArea(mline));

	RETURN geom;
END;
$_$;


ALTER FUNCTION public.st_bdmpolyfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_bdpolyfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_bdpolyfromtext(text, integer) RETURNS geometry
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
DECLARE
	geomtext alias for $1;
	srid alias for $2;
	mline geometry;
	geom geometry;
BEGIN
	mline := ST_MultiLineStringFromText(geomtext, srid);

	IF mline IS NULL
	THEN
		RAISE EXCEPTION 'Input is not a MultiLinestring';
	END IF;

	geom := ST_BuildArea(mline);

	IF GeometryType(geom) != 'POLYGON'
	THEN
		RAISE EXCEPTION 'Input returns more then a single polygon, try using BdMPolyFromText instead';
	END IF;

	RETURN geom;
END;
$_$;


ALTER FUNCTION public.st_bdpolyfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_boundary(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_boundary(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'boundary';


ALTER FUNCTION public.st_boundary(geometry) OWNER TO postgres;

--
-- Name: st_box(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box(geometry) RETURNS box
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_BOX';


ALTER FUNCTION public.st_box(geometry) OWNER TO postgres;

--
-- Name: st_box(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box(box3d) RETURNS box
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_to_BOX';


ALTER FUNCTION public.st_box(box3d) OWNER TO postgres;

--
-- Name: st_box2d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box2d(geometry) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_BOX2DFLOAT4';


ALTER FUNCTION public.st_box2d(geometry) OWNER TO postgres;

--
-- Name: st_box2d(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box2d(box3d) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_to_BOX2DFLOAT4';


ALTER FUNCTION public.st_box2d(box3d) OWNER TO postgres;

--
-- Name: st_box2d(box3d_extent); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box2d(box3d_extent) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_to_BOX2DFLOAT4';


ALTER FUNCTION public.st_box2d(box3d_extent) OWNER TO postgres;

--
-- Name: st_box2d_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box2d_in(cstring) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_in';


ALTER FUNCTION public.st_box2d_in(cstring) OWNER TO postgres;

--
-- Name: st_box2d_out(box2d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box2d_out(box2d) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_out';


ALTER FUNCTION public.st_box2d_out(box2d) OWNER TO postgres;

--
-- Name: st_box3d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box3d(geometry) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_BOX3D';


ALTER FUNCTION public.st_box3d(geometry) OWNER TO postgres;

--
-- Name: st_box3d(box2d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box3d(box2d) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_to_BOX3D';


ALTER FUNCTION public.st_box3d(box2d) OWNER TO postgres;

--
-- Name: st_box3d_extent(box3d_extent); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box3d_extent(box3d_extent) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_extent_to_BOX3D';


ALTER FUNCTION public.st_box3d_extent(box3d_extent) OWNER TO postgres;

--
-- Name: st_box3d_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box3d_in(cstring) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_in';


ALTER FUNCTION public.st_box3d_in(cstring) OWNER TO postgres;

--
-- Name: st_box3d_out(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_box3d_out(box3d) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_out';


ALTER FUNCTION public.st_box3d_out(box3d) OWNER TO postgres;

--
-- Name: st_buffer(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_buffer(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'buffer';


ALTER FUNCTION public.st_buffer(geometry, double precision) OWNER TO postgres;

--
-- Name: st_buffer(geography, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_buffer(geography, double precision) RETURNS geography
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT geography(ST_Transform(ST_Buffer(ST_Transform(geometry($1), _ST_BestSRID($1)), $2), 4326))$_$;


ALTER FUNCTION public.st_buffer(geography, double precision) OWNER TO postgres;

--
-- Name: st_buffer(text, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_buffer(text, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Buffer($1::geometry, $2);  $_$;


ALTER FUNCTION public.st_buffer(text, double precision) OWNER TO postgres;

--
-- Name: st_buffer(geometry, double precision, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_buffer(geometry, double precision, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT _ST_Buffer($1, $2,
		CAST('quad_segs='||CAST($3 AS text) as cstring))
	   $_$;


ALTER FUNCTION public.st_buffer(geometry, double precision, integer) OWNER TO postgres;

--
-- Name: st_buffer(geometry, double precision, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_buffer(geometry, double precision, text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT _ST_Buffer($1, $2,
		CAST( regexp_replace($3, '^[0123456789]+$',
			'quad_segs='||$3) AS cstring)
		)
	   $_$;


ALTER FUNCTION public.st_buffer(geometry, double precision, text) OWNER TO postgres;

--
-- Name: st_buildarea(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_buildarea(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_buildarea';


ALTER FUNCTION public.st_buildarea(geometry) OWNER TO postgres;

--
-- Name: st_bytea(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_bytea(geometry) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_bytea';


ALTER FUNCTION public.st_bytea(geometry) OWNER TO postgres;

--
-- Name: st_centroid(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_centroid(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'centroid';


ALTER FUNCTION public.st_centroid(geometry) OWNER TO postgres;

--
-- Name: st_chip_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_chip_in(cstring) RETURNS chip
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_in';


ALTER FUNCTION public.st_chip_in(cstring) OWNER TO postgres;

--
-- Name: st_chip_out(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_chip_out(chip) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_out';


ALTER FUNCTION public.st_chip_out(chip) OWNER TO postgres;

--
-- Name: st_closestpoint(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_closestpoint(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_closestpoint';


ALTER FUNCTION public.st_closestpoint(geometry, geometry) OWNER TO postgres;

--
-- Name: st_collect(geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_collect(geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_collect_garray';


ALTER FUNCTION public.st_collect(geometry[]) OWNER TO postgres;

--
-- Name: st_collect(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_collect(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'LWGEOM_collect';


ALTER FUNCTION public.st_collect(geometry, geometry) OWNER TO postgres;

--
-- Name: st_collectionextract(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_collectionextract(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'ST_CollectionExtract';


ALTER FUNCTION public.st_collectionextract(geometry, integer) OWNER TO postgres;

--
-- Name: st_combine_bbox(box2d, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_combine_bbox(box2d, geometry) RETURNS box2d
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_combine';


ALTER FUNCTION public.st_combine_bbox(box2d, geometry) OWNER TO postgres;

--
-- Name: st_combine_bbox(box3d_extent, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_combine_bbox(box3d_extent, geometry) RETURNS box3d_extent
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'BOX3D_combine';


ALTER FUNCTION public.st_combine_bbox(box3d_extent, geometry) OWNER TO postgres;

--
-- Name: st_combine_bbox(box3d, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_combine_bbox(box3d, geometry) RETURNS box3d
    LANGUAGE c IMMUTABLE
    AS '$libdir/postgis-1.5', 'BOX3D_combine';


ALTER FUNCTION public.st_combine_bbox(box3d, geometry) OWNER TO postgres;

--
-- Name: st_compression(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_compression(chip) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getCompression';


ALTER FUNCTION public.st_compression(chip) OWNER TO postgres;

--
-- Name: st_contains(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_contains(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_Contains($1,$2)$_$;


ALTER FUNCTION public.st_contains(geometry, geometry) OWNER TO postgres;

--
-- Name: st_containsproperly(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_containsproperly(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_ContainsProperly($1,$2)$_$;


ALTER FUNCTION public.st_containsproperly(geometry, geometry) OWNER TO postgres;

--
-- Name: st_convexhull(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_convexhull(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'convexhull';


ALTER FUNCTION public.st_convexhull(geometry) OWNER TO postgres;

--
-- Name: st_coorddim(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_coorddim(geometry) RETURNS smallint
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_ndims';


ALTER FUNCTION public.st_coorddim(geometry) OWNER TO postgres;

--
-- Name: st_coveredby(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_coveredby(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_CoveredBy($1,$2)$_$;


ALTER FUNCTION public.st_coveredby(geometry, geometry) OWNER TO postgres;

--
-- Name: st_coveredby(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_coveredby(geography, geography) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_Covers($2, $1)$_$;


ALTER FUNCTION public.st_coveredby(geography, geography) OWNER TO postgres;

--
-- Name: st_coveredby(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_coveredby(text, text) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_CoveredBy($1::geometry, $2::geometry);  $_$;


ALTER FUNCTION public.st_coveredby(text, text) OWNER TO postgres;

--
-- Name: st_covers(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_covers(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_Covers($1,$2)$_$;


ALTER FUNCTION public.st_covers(geometry, geometry) OWNER TO postgres;

--
-- Name: st_covers(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_covers(geography, geography) RETURNS boolean
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT $1 && $2 AND _ST_Covers($1, $2)$_$;


ALTER FUNCTION public.st_covers(geography, geography) OWNER TO postgres;

--
-- Name: st_covers(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_covers(text, text) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_Covers($1::geometry, $2::geometry);  $_$;


ALTER FUNCTION public.st_covers(text, text) OWNER TO postgres;

--
-- Name: st_crosses(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_crosses(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_Crosses($1,$2)$_$;


ALTER FUNCTION public.st_crosses(geometry, geometry) OWNER TO postgres;

--
-- Name: st_curvetoline(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_curvetoline(geometry) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_CurveToLine($1, 32)$_$;


ALTER FUNCTION public.st_curvetoline(geometry) OWNER TO postgres;

--
-- Name: st_curvetoline(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_curvetoline(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_curve_segmentize';


ALTER FUNCTION public.st_curvetoline(geometry, integer) OWNER TO postgres;

--
-- Name: st_datatype(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_datatype(chip) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getDatatype';


ALTER FUNCTION public.st_datatype(chip) OWNER TO postgres;

--
-- Name: st_dfullywithin(geometry, geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_dfullywithin(geometry, geometry, double precision) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && ST_Expand($2,$3) AND $2 && ST_Expand($1,$3) AND _ST_DFullyWithin(ST_ConvexHull($1), ST_ConvexHull($2), $3)$_$;


ALTER FUNCTION public.st_dfullywithin(geometry, geometry, double precision) OWNER TO postgres;

--
-- Name: st_difference(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_difference(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'difference';


ALTER FUNCTION public.st_difference(geometry, geometry) OWNER TO postgres;

--
-- Name: st_dimension(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_dimension(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_dimension';


ALTER FUNCTION public.st_dimension(geometry) OWNER TO postgres;

--
-- Name: st_disjoint(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_disjoint(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'disjoint';


ALTER FUNCTION public.st_disjoint(geometry, geometry) OWNER TO postgres;

--
-- Name: st_distance(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_distance(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_mindistance2d';


ALTER FUNCTION public.st_distance(geometry, geometry) OWNER TO postgres;

--
-- Name: st_distance(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_distance(geography, geography) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_Distance($1, $2, 0.0, true)$_$;


ALTER FUNCTION public.st_distance(geography, geography) OWNER TO postgres;

--
-- Name: st_distance(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_distance(text, text) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Distance($1::geometry, $2::geometry);  $_$;


ALTER FUNCTION public.st_distance(text, text) OWNER TO postgres;

--
-- Name: st_distance(geography, geography, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_distance(geography, geography, boolean) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_Distance($1, $2, 0.0, $3)$_$;


ALTER FUNCTION public.st_distance(geography, geography, boolean) OWNER TO postgres;

--
-- Name: st_distance_sphere(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_distance_sphere(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_distance_sphere';


ALTER FUNCTION public.st_distance_sphere(geometry, geometry) OWNER TO postgres;

--
-- Name: st_distance_spheroid(geometry, geometry, spheroid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_distance_spheroid(geometry, geometry, spheroid) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_distance_ellipsoid';


ALTER FUNCTION public.st_distance_spheroid(geometry, geometry, spheroid) OWNER TO postgres;

--
-- Name: st_dump(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_dump(geometry) RETURNS SETOF geometry_dump
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_dump';


ALTER FUNCTION public.st_dump(geometry) OWNER TO postgres;

--
-- Name: st_dumppoints(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_dumppoints(geometry) RETURNS SETOF geometry_dump
    LANGUAGE sql STRICT
    AS $_$
  SELECT * FROM _ST_DumpPoints($1, NULL);
$_$;


ALTER FUNCTION public.st_dumppoints(geometry) OWNER TO postgres;

--
-- Name: st_dumprings(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_dumprings(geometry) RETURNS SETOF geometry_dump
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_dump_rings';


ALTER FUNCTION public.st_dumprings(geometry) OWNER TO postgres;

--
-- Name: st_dwithin(geometry, geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_dwithin(geometry, geometry, double precision) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && ST_Expand($2,$3) AND $2 && ST_Expand($1,$3) AND _ST_DWithin($1, $2, $3)$_$;


ALTER FUNCTION public.st_dwithin(geometry, geometry, double precision) OWNER TO postgres;

--
-- Name: st_dwithin(geography, geography, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_dwithin(geography, geography, double precision) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && _ST_Expand($2,$3) AND $2 && _ST_Expand($1,$3) AND _ST_DWithin($1, $2, $3, true)$_$;


ALTER FUNCTION public.st_dwithin(geography, geography, double precision) OWNER TO postgres;

--
-- Name: st_dwithin(text, text, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_dwithin(text, text, double precision) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_DWithin($1::geometry, $2::geometry, $3);  $_$;


ALTER FUNCTION public.st_dwithin(text, text, double precision) OWNER TO postgres;

--
-- Name: st_dwithin(geography, geography, double precision, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_dwithin(geography, geography, double precision, boolean) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && _ST_Expand($2,$3) AND $2 && _ST_Expand($1,$3) AND _ST_DWithin($1, $2, $3, $4)$_$;


ALTER FUNCTION public.st_dwithin(geography, geography, double precision, boolean) OWNER TO postgres;

--
-- Name: st_endpoint(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_endpoint(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_endpoint_linestring';


ALTER FUNCTION public.st_endpoint(geometry) OWNER TO postgres;

--
-- Name: st_envelope(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_envelope(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_envelope';


ALTER FUNCTION public.st_envelope(geometry) OWNER TO postgres;

--
-- Name: st_equals(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_equals(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_Equals($1,$2)$_$;


ALTER FUNCTION public.st_equals(geometry, geometry) OWNER TO postgres;

--
-- Name: st_estimated_extent(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_estimated_extent(text, text) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT SECURITY DEFINER
    AS '$libdir/postgis-1.5', 'LWGEOM_estimated_extent';


ALTER FUNCTION public.st_estimated_extent(text, text) OWNER TO postgres;

--
-- Name: st_estimated_extent(text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_estimated_extent(text, text, text) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT SECURITY DEFINER
    AS '$libdir/postgis-1.5', 'LWGEOM_estimated_extent';


ALTER FUNCTION public.st_estimated_extent(text, text, text) OWNER TO postgres;

--
-- Name: st_expand(box3d, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_expand(box3d, double precision) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_expand';


ALTER FUNCTION public.st_expand(box3d, double precision) OWNER TO postgres;

--
-- Name: st_expand(box2d, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_expand(box2d, double precision) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_expand';


ALTER FUNCTION public.st_expand(box2d, double precision) OWNER TO postgres;

--
-- Name: st_expand(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_expand(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_expand';


ALTER FUNCTION public.st_expand(geometry, double precision) OWNER TO postgres;

--
-- Name: st_exteriorring(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_exteriorring(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_exteriorring_polygon';


ALTER FUNCTION public.st_exteriorring(geometry) OWNER TO postgres;

--
-- Name: st_factor(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_factor(chip) RETURNS real
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getFactor';


ALTER FUNCTION public.st_factor(chip) OWNER TO postgres;

--
-- Name: st_find_extent(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_find_extent(text, text) RETURNS box2d
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
DECLARE
	tablename alias for $1;
	columnname alias for $2;
	myrec RECORD;

BEGIN
	FOR myrec IN EXECUTE 'SELECT extent("' || columnname || '") FROM "' || tablename || '"' LOOP
		return myrec.extent;
	END LOOP;
END;
$_$;


ALTER FUNCTION public.st_find_extent(text, text) OWNER TO postgres;

--
-- Name: st_find_extent(text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_find_extent(text, text, text) RETURNS box2d
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
DECLARE
	schemaname alias for $1;
	tablename alias for $2;
	columnname alias for $3;
	myrec RECORD;

BEGIN
	FOR myrec IN EXECUTE 'SELECT extent("' || columnname || '") FROM "' || schemaname || '"."' || tablename || '"' LOOP
		return myrec.extent;
	END LOOP;
END;
$_$;


ALTER FUNCTION public.st_find_extent(text, text, text) OWNER TO postgres;

--
-- Name: st_force_2d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_force_2d(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_2d';


ALTER FUNCTION public.st_force_2d(geometry) OWNER TO postgres;

--
-- Name: st_force_3d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_force_3d(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_3dz';


ALTER FUNCTION public.st_force_3d(geometry) OWNER TO postgres;

--
-- Name: st_force_3dm(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_force_3dm(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_3dm';


ALTER FUNCTION public.st_force_3dm(geometry) OWNER TO postgres;

--
-- Name: st_force_3dz(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_force_3dz(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_3dz';


ALTER FUNCTION public.st_force_3dz(geometry) OWNER TO postgres;

--
-- Name: st_force_4d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_force_4d(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_4d';


ALTER FUNCTION public.st_force_4d(geometry) OWNER TO postgres;

--
-- Name: st_force_collection(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_force_collection(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_collection';


ALTER FUNCTION public.st_force_collection(geometry) OWNER TO postgres;

--
-- Name: st_forcerhr(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_forcerhr(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_forceRHR_poly';


ALTER FUNCTION public.st_forcerhr(geometry) OWNER TO postgres;

--
-- Name: st_geogfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geogfromtext(text) RETURNS geography
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_from_text';


ALTER FUNCTION public.st_geogfromtext(text) OWNER TO postgres;

--
-- Name: st_geogfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geogfromwkb(bytea) RETURNS geography
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_from_binary';


ALTER FUNCTION public.st_geogfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_geographyfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geographyfromtext(text) RETURNS geography
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geography_from_text';


ALTER FUNCTION public.st_geographyfromtext(text) OWNER TO postgres;

--
-- Name: st_geohash(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geohash(geometry) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_GeoHash($1, 0)$_$;


ALTER FUNCTION public.st_geohash(geometry) OWNER TO postgres;

--
-- Name: st_geohash(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geohash(geometry, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'ST_GeoHash';


ALTER FUNCTION public.st_geohash(geometry, integer) OWNER TO postgres;

--
-- Name: st_geomcollfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomcollfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE
	WHEN geometrytype(ST_GeomFromText($1)) = 'GEOMETRYCOLLECTION'
	THEN ST_GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_geomcollfromtext(text) OWNER TO postgres;

--
-- Name: st_geomcollfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomcollfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE
	WHEN geometrytype(ST_GeomFromText($1, $2)) = 'GEOMETRYCOLLECTION'
	THEN ST_GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_geomcollfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_geomcollfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomcollfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE
	WHEN geometrytype(ST_GeomFromWKB($1)) = 'GEOMETRYCOLLECTION'
	THEN ST_GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_geomcollfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_geomcollfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomcollfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE
	WHEN geometrytype(GeomFromWKB($1, $2)) = 'GEOMETRYCOLLECTION'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_geomcollfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_geometry(box2d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry(box2d) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_to_LWGEOM';


ALTER FUNCTION public.st_geometry(box2d) OWNER TO postgres;

--
-- Name: st_geometry(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry(box3d) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_to_LWGEOM';


ALTER FUNCTION public.st_geometry(box3d) OWNER TO postgres;

--
-- Name: st_geometry(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'parse_WKT_lwgeom';


ALTER FUNCTION public.st_geometry(text) OWNER TO postgres;

--
-- Name: st_geometry(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry(chip) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_to_LWGEOM';


ALTER FUNCTION public.st_geometry(chip) OWNER TO postgres;

--
-- Name: st_geometry(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry(bytea) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_bytea';


ALTER FUNCTION public.st_geometry(bytea) OWNER TO postgres;

--
-- Name: st_geometry(box3d_extent); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry(box3d_extent) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_to_LWGEOM';


ALTER FUNCTION public.st_geometry(box3d_extent) OWNER TO postgres;

--
-- Name: st_geometry_above(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_above(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_above';


ALTER FUNCTION public.st_geometry_above(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_analyze(internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_analyze(internal) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_analyze';


ALTER FUNCTION public.st_geometry_analyze(internal) OWNER TO postgres;

--
-- Name: st_geometry_below(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_below(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_below';


ALTER FUNCTION public.st_geometry_below(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_cmp(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_cmp(geometry, geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_cmp';


ALTER FUNCTION public.st_geometry_cmp(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_contain(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_contain(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_contain';


ALTER FUNCTION public.st_geometry_contain(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_contained(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_contained(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_contained';


ALTER FUNCTION public.st_geometry_contained(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_eq(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_eq(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_eq';


ALTER FUNCTION public.st_geometry_eq(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_ge(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_ge(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_ge';


ALTER FUNCTION public.st_geometry_ge(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_gt(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_gt(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_gt';


ALTER FUNCTION public.st_geometry_gt(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_in(cstring) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_in';


ALTER FUNCTION public.st_geometry_in(cstring) OWNER TO postgres;

--
-- Name: st_geometry_le(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_le(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_le';


ALTER FUNCTION public.st_geometry_le(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_left(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_left(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_left';


ALTER FUNCTION public.st_geometry_left(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_lt(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_lt(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'lwgeom_lt';


ALTER FUNCTION public.st_geometry_lt(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_out(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_out(geometry) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_out';


ALTER FUNCTION public.st_geometry_out(geometry) OWNER TO postgres;

--
-- Name: st_geometry_overabove(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_overabove(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_overabove';


ALTER FUNCTION public.st_geometry_overabove(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_overbelow(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_overbelow(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_overbelow';


ALTER FUNCTION public.st_geometry_overbelow(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_overlap(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_overlap(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_overlap';


ALTER FUNCTION public.st_geometry_overlap(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_overleft(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_overleft(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_overleft';


ALTER FUNCTION public.st_geometry_overleft(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_overright(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_overright(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_overright';


ALTER FUNCTION public.st_geometry_overright(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_recv(internal); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_recv(internal) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_recv';


ALTER FUNCTION public.st_geometry_recv(internal) OWNER TO postgres;

--
-- Name: st_geometry_right(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_right(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_right';


ALTER FUNCTION public.st_geometry_right(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_same(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_same(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_samebox';


ALTER FUNCTION public.st_geometry_same(geometry, geometry) OWNER TO postgres;

--
-- Name: st_geometry_send(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometry_send(geometry) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_send';


ALTER FUNCTION public.st_geometry_send(geometry) OWNER TO postgres;

--
-- Name: st_geometryfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometryfromtext(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_text';


ALTER FUNCTION public.st_geometryfromtext(text) OWNER TO postgres;

--
-- Name: st_geometryfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometryfromtext(text, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_text';


ALTER FUNCTION public.st_geometryfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_geometryn(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometryn(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_geometryn_collection';


ALTER FUNCTION public.st_geometryn(geometry, integer) OWNER TO postgres;

--
-- Name: st_geometrytype(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geometrytype(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geometry_geometrytype';


ALTER FUNCTION public.st_geometrytype(geometry) OWNER TO postgres;

--
-- Name: st_geomfromewkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomfromewkb(bytea) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOMFromWKB';


ALTER FUNCTION public.st_geomfromewkb(bytea) OWNER TO postgres;

--
-- Name: st_geomfromewkt(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomfromewkt(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'parse_WKT_lwgeom';


ALTER FUNCTION public.st_geomfromewkt(text) OWNER TO postgres;

--
-- Name: st_geomfromgml(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomfromgml(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geom_from_gml';


ALTER FUNCTION public.st_geomfromgml(text) OWNER TO postgres;

--
-- Name: st_geomfromkml(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomfromkml(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geom_from_kml';


ALTER FUNCTION public.st_geomfromkml(text) OWNER TO postgres;

--
-- Name: st_geomfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomfromtext(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_text';


ALTER FUNCTION public.st_geomfromtext(text) OWNER TO postgres;

--
-- Name: st_geomfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomfromtext(text, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_text';


ALTER FUNCTION public.st_geomfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_geomfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomfromwkb(bytea) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_WKB';


ALTER FUNCTION public.st_geomfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_geomfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_geomfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_SetSRID(ST_GeomFromWKB($1), $2)$_$;


ALTER FUNCTION public.st_geomfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_gmltosql(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_gmltosql(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geom_from_gml';


ALTER FUNCTION public.st_gmltosql(text) OWNER TO postgres;

--
-- Name: st_hasarc(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_hasarc(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_has_arc';


ALTER FUNCTION public.st_hasarc(geometry) OWNER TO postgres;

--
-- Name: st_hausdorffdistance(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_hausdorffdistance(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'hausdorffdistance';


ALTER FUNCTION public.st_hausdorffdistance(geometry, geometry) OWNER TO postgres;

--
-- Name: st_hausdorffdistance(geometry, geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_hausdorffdistance(geometry, geometry, double precision) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'hausdorffdistancedensify';


ALTER FUNCTION public.st_hausdorffdistance(geometry, geometry, double precision) OWNER TO postgres;

--
-- Name: st_height(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_height(chip) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getHeight';


ALTER FUNCTION public.st_height(chip) OWNER TO postgres;

--
-- Name: st_interiorringn(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_interiorringn(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_interiorringn_polygon';


ALTER FUNCTION public.st_interiorringn(geometry, integer) OWNER TO postgres;

--
-- Name: st_intersection(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_intersection(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'intersection';


ALTER FUNCTION public.st_intersection(geometry, geometry) OWNER TO postgres;

--
-- Name: st_intersection(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_intersection(geography, geography) RETURNS geography
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT geography(ST_Transform(ST_Intersection(ST_Transform(geometry($1), _ST_BestSRID($1, $2)), ST_Transform(geometry($2), _ST_BestSRID($1, $2))), 4326))$_$;


ALTER FUNCTION public.st_intersection(geography, geography) OWNER TO postgres;

--
-- Name: st_intersection(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_intersection(text, text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Intersection($1::geometry, $2::geometry);  $_$;


ALTER FUNCTION public.st_intersection(text, text) OWNER TO postgres;

--
-- Name: st_intersects(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_intersects(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_Intersects($1,$2)$_$;


ALTER FUNCTION public.st_intersects(geometry, geometry) OWNER TO postgres;

--
-- Name: st_intersects(geography, geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_intersects(geography, geography) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_Distance($1, $2, 0.0, false) < 0.00001$_$;


ALTER FUNCTION public.st_intersects(geography, geography) OWNER TO postgres;

--
-- Name: st_intersects(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_intersects(text, text) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_Intersects($1::geometry, $2::geometry);  $_$;


ALTER FUNCTION public.st_intersects(text, text) OWNER TO postgres;

--
-- Name: st_isclosed(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_isclosed(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_isclosed_linestring';


ALTER FUNCTION public.st_isclosed(geometry) OWNER TO postgres;

--
-- Name: st_isempty(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_isempty(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_isempty';


ALTER FUNCTION public.st_isempty(geometry) OWNER TO postgres;

--
-- Name: st_isring(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_isring(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'isring';


ALTER FUNCTION public.st_isring(geometry) OWNER TO postgres;

--
-- Name: st_issimple(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_issimple(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'issimple';


ALTER FUNCTION public.st_issimple(geometry) OWNER TO postgres;

--
-- Name: st_isvalid(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_isvalid(geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'isvalid';


ALTER FUNCTION public.st_isvalid(geometry) OWNER TO postgres;

--
-- Name: st_isvalidreason(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_isvalidreason(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'isvalidreason';


ALTER FUNCTION public.st_isvalidreason(geometry) OWNER TO postgres;

--
-- Name: st_length(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_length(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_length2d_linestring';


ALTER FUNCTION public.st_length(geometry) OWNER TO postgres;

--
-- Name: st_length(geography); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_length(geography) RETURNS double precision
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT ST_Length($1, true)$_$;


ALTER FUNCTION public.st_length(geography) OWNER TO postgres;

--
-- Name: st_length(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_length(text) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Length($1::geometry);  $_$;


ALTER FUNCTION public.st_length(text) OWNER TO postgres;

--
-- Name: st_length(geography, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_length(geography, boolean) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'geography_length';


ALTER FUNCTION public.st_length(geography, boolean) OWNER TO postgres;

--
-- Name: st_length2d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_length2d(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_length2d_linestring';


ALTER FUNCTION public.st_length2d(geometry) OWNER TO postgres;

--
-- Name: st_length2d_spheroid(geometry, spheroid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_length2d_spheroid(geometry, spheroid) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_length2d_ellipsoid';


ALTER FUNCTION public.st_length2d_spheroid(geometry, spheroid) OWNER TO postgres;

--
-- Name: st_length3d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_length3d(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_length_linestring';


ALTER FUNCTION public.st_length3d(geometry) OWNER TO postgres;

--
-- Name: st_length3d_spheroid(geometry, spheroid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_length3d_spheroid(geometry, spheroid) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_length_ellipsoid_linestring';


ALTER FUNCTION public.st_length3d_spheroid(geometry, spheroid) OWNER TO postgres;

--
-- Name: st_length_spheroid(geometry, spheroid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_length_spheroid(geometry, spheroid) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'LWGEOM_length_ellipsoid_linestring';


ALTER FUNCTION public.st_length_spheroid(geometry, spheroid) OWNER TO postgres;

--
-- Name: st_line_interpolate_point(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_line_interpolate_point(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_line_interpolate_point';


ALTER FUNCTION public.st_line_interpolate_point(geometry, double precision) OWNER TO postgres;

--
-- Name: st_line_locate_point(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_line_locate_point(geometry, geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_line_locate_point';


ALTER FUNCTION public.st_line_locate_point(geometry, geometry) OWNER TO postgres;

--
-- Name: st_line_substring(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_line_substring(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_line_substring';


ALTER FUNCTION public.st_line_substring(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: st_linecrossingdirection(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_linecrossingdirection(geometry, geometry) RETURNS integer
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT CASE WHEN NOT $1 && $2 THEN 0 ELSE _ST_LineCrossingDirection($1,$2) END $_$;


ALTER FUNCTION public.st_linecrossingdirection(geometry, geometry) OWNER TO postgres;

--
-- Name: st_linefrommultipoint(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_linefrommultipoint(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_line_from_mpoint';


ALTER FUNCTION public.st_linefrommultipoint(geometry) OWNER TO postgres;

--
-- Name: st_linefromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_linefromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromText($1)) = 'LINESTRING'
	THEN ST_GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_linefromtext(text) OWNER TO postgres;

--
-- Name: st_linefromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_linefromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1, $2)) = 'LINESTRING'
	THEN GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_linefromtext(text, integer) OWNER TO postgres;

--
-- Name: st_linefromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_linefromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1)) = 'LINESTRING'
	THEN ST_GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_linefromwkb(bytea) OWNER TO postgres;

--
-- Name: st_linefromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_linefromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1, $2)) = 'LINESTRING'
	THEN ST_GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_linefromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_linemerge(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_linemerge(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'linemerge';


ALTER FUNCTION public.st_linemerge(geometry) OWNER TO postgres;

--
-- Name: st_linestringfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_linestringfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'LINESTRING'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_linestringfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_linestringfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_linestringfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1, $2)) = 'LINESTRING'
	THEN ST_GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_linestringfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_linetocurve(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_linetocurve(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_line_desegmentize';


ALTER FUNCTION public.st_linetocurve(geometry) OWNER TO postgres;

--
-- Name: st_locate_along_measure(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_locate_along_measure(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT locate_between_measures($1, $2, $2) $_$;


ALTER FUNCTION public.st_locate_along_measure(geometry, double precision) OWNER TO postgres;

--
-- Name: st_locate_between_measures(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_locate_between_measures(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_locate_between_m';


ALTER FUNCTION public.st_locate_between_measures(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: st_locatebetweenelevations(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_locatebetweenelevations(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'ST_LocateBetweenElevations';


ALTER FUNCTION public.st_locatebetweenelevations(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: st_longestline(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_longestline(geometry, geometry) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_LongestLine(ST_ConvexHull($1), ST_ConvexHull($2))$_$;


ALTER FUNCTION public.st_longestline(geometry, geometry) OWNER TO postgres;

--
-- Name: st_m(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_m(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_m_point';


ALTER FUNCTION public.st_m(geometry) OWNER TO postgres;

--
-- Name: st_makebox2d(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makebox2d(geometry, geometry) RETURNS box2d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX2DFLOAT4_construct';


ALTER FUNCTION public.st_makebox2d(geometry, geometry) OWNER TO postgres;

--
-- Name: st_makebox3d(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makebox3d(geometry, geometry) RETURNS box3d
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_construct';


ALTER FUNCTION public.st_makebox3d(geometry, geometry) OWNER TO postgres;

--
-- Name: st_makeenvelope(double precision, double precision, double precision, double precision, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makeenvelope(double precision, double precision, double precision, double precision, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'ST_MakeEnvelope';


ALTER FUNCTION public.st_makeenvelope(double precision, double precision, double precision, double precision, integer) OWNER TO postgres;

--
-- Name: st_makeline(geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makeline(geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makeline_garray';


ALTER FUNCTION public.st_makeline(geometry[]) OWNER TO postgres;

--
-- Name: st_makeline(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makeline(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makeline';


ALTER FUNCTION public.st_makeline(geometry, geometry) OWNER TO postgres;

--
-- Name: st_makeline_garray(geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makeline_garray(geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makeline_garray';


ALTER FUNCTION public.st_makeline_garray(geometry[]) OWNER TO postgres;

--
-- Name: st_makepoint(double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makepoint(double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoint';


ALTER FUNCTION public.st_makepoint(double precision, double precision) OWNER TO postgres;

--
-- Name: st_makepoint(double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makepoint(double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoint';


ALTER FUNCTION public.st_makepoint(double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_makepoint(double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makepoint(double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoint';


ALTER FUNCTION public.st_makepoint(double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_makepointm(double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makepointm(double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoint3dm';


ALTER FUNCTION public.st_makepointm(double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_makepolygon(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makepolygon(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoly';


ALTER FUNCTION public.st_makepolygon(geometry) OWNER TO postgres;

--
-- Name: st_makepolygon(geometry, geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_makepolygon(geometry, geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoly';


ALTER FUNCTION public.st_makepolygon(geometry, geometry[]) OWNER TO postgres;

--
-- Name: st_maxdistance(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_maxdistance(geometry, geometry) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT _ST_MaxDistance(ST_ConvexHull($1), ST_ConvexHull($2))$_$;


ALTER FUNCTION public.st_maxdistance(geometry, geometry) OWNER TO postgres;

--
-- Name: st_mem_size(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mem_size(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_mem_size';


ALTER FUNCTION public.st_mem_size(geometry) OWNER TO postgres;

--
-- Name: st_minimumboundingcircle(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_minimumboundingcircle(geometry) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_MinimumBoundingCircle($1, 48)$_$;


ALTER FUNCTION public.st_minimumboundingcircle(geometry) OWNER TO postgres;

--
-- Name: st_minimumboundingcircle(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_minimumboundingcircle(inputgeom geometry, segs_per_quarter integer) RETURNS geometry
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $$
	DECLARE
	hull GEOMETRY;
	ring GEOMETRY;
	center GEOMETRY;
	radius DOUBLE PRECISION;
	dist DOUBLE PRECISION;
	d DOUBLE PRECISION;
	idx1 integer;
	idx2 integer;
	l1 GEOMETRY;
	l2 GEOMETRY;
	p1 GEOMETRY;
	p2 GEOMETRY;
	a1 DOUBLE PRECISION;
	a2 DOUBLE PRECISION;


	BEGIN

	-- First compute the ConvexHull of the geometry
	hull = ST_ConvexHull(inputgeom);
	--A point really has no MBC
	IF ST_GeometryType(hull) = 'ST_Point' THEN
		RETURN hull;
	END IF;
	-- convert the hull perimeter to a linestring so we can manipulate individual points
	--If its already a linestring force it to a closed linestring
	ring = CASE WHEN ST_GeometryType(hull) = 'ST_LineString' THEN ST_AddPoint(hull, ST_StartPoint(hull)) ELSE ST_ExteriorRing(hull) END;

	dist = 0;
	-- Brute Force - check every pair
	FOR i in 1 .. (ST_NumPoints(ring)-2)
		LOOP
			FOR j in i .. (ST_NumPoints(ring)-1)
				LOOP
				d = ST_Distance(ST_PointN(ring,i),ST_PointN(ring,j));
				-- Check the distance and update if larger
				IF (d > dist) THEN
					dist = d;
					idx1 = i;
					idx2 = j;
				END IF;
			END LOOP;
		END LOOP;

	-- We now have the diameter of the convex hull.  The following line returns it if desired.
	-- RETURN MakeLine(PointN(ring,idx1),PointN(ring,idx2));

	-- Now for the Minimum Bounding Circle.  Since we know the two points furthest from each
	-- other, the MBC must go through those two points. Start with those points as a diameter of a circle.

	-- The radius is half the distance between them and the center is midway between them
	radius = ST_Distance(ST_PointN(ring,idx1),ST_PointN(ring,idx2)) / 2.0;
	center = ST_Line_interpolate_point(ST_MakeLine(ST_PointN(ring,idx1),ST_PointN(ring,idx2)),0.5);

	-- Loop through each vertex and check if the distance from the center to the point
	-- is greater than the current radius.
	FOR k in 1 .. (ST_NumPoints(ring)-1)
		LOOP
		IF(k <> idx1 and k <> idx2) THEN
			dist = ST_Distance(center,ST_PointN(ring,k));
			IF (dist > radius) THEN
				-- We have to expand the circle.  The new circle must pass trhough
				-- three points - the two original diameters and this point.

				-- Draw a line from the first diameter to this point
				l1 = ST_Makeline(ST_PointN(ring,idx1),ST_PointN(ring,k));
				-- Compute the midpoint
				p1 = ST_line_interpolate_point(l1,0.5);
				-- Rotate the line 90 degrees around the midpoint (perpendicular bisector)
				l1 = ST_Translate(ST_Rotate(ST_Translate(l1,-X(p1),-Y(p1)),pi()/2),X(p1),Y(p1));
				--  Compute the azimuth of the bisector
				a1 = ST_Azimuth(ST_PointN(l1,1),ST_PointN(l1,2));
				--  Extend the line in each direction the new computed distance to insure they will intersect
				l1 = ST_AddPoint(l1,ST_Makepoint(X(ST_PointN(l1,2))+sin(a1)*dist,Y(ST_PointN(l1,2))+cos(a1)*dist),-1);
				l1 = ST_AddPoint(l1,ST_Makepoint(X(ST_PointN(l1,1))-sin(a1)*dist,Y(ST_PointN(l1,1))-cos(a1)*dist),0);

				-- Repeat for the line from the point to the other diameter point
				l2 = ST_Makeline(ST_PointN(ring,idx2),ST_PointN(ring,k));
				p2 = ST_Line_interpolate_point(l2,0.5);
				l2 = ST_Translate(ST_Rotate(ST_Translate(l2,-X(p2),-Y(p2)),pi()/2),X(p2),Y(p2));
				a2 = ST_Azimuth(ST_PointN(l2,1),ST_PointN(l2,2));
				l2 = ST_AddPoint(l2,ST_Makepoint(X(ST_PointN(l2,2))+sin(a2)*dist,Y(ST_PointN(l2,2))+cos(a2)*dist),-1);
				l2 = ST_AddPoint(l2,ST_Makepoint(X(ST_PointN(l2,1))-sin(a2)*dist,Y(ST_PointN(l2,1))-cos(a2)*dist),0);

				-- The new center is the intersection of the two bisectors
				center = ST_Intersection(l1,l2);
				-- The new radius is the distance to any of the three points
				radius = ST_Distance(center,ST_PointN(ring,idx1));
			END IF;
		END IF;
		END LOOP;
	--DONE!!  Return the MBC via the buffer command
	RETURN ST_Buffer(center,radius,segs_per_quarter);

	END;
$$;


ALTER FUNCTION public.st_minimumboundingcircle(inputgeom geometry, segs_per_quarter integer) OWNER TO postgres;

--
-- Name: st_mlinefromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mlinefromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromText($1)) = 'MULTILINESTRING'
	THEN ST_GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mlinefromtext(text) OWNER TO postgres;

--
-- Name: st_mlinefromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mlinefromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE
	WHEN geometrytype(GeomFromText($1, $2)) = 'MULTILINESTRING'
	THEN GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mlinefromtext(text, integer) OWNER TO postgres;

--
-- Name: st_mlinefromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mlinefromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1)) = 'MULTILINESTRING'
	THEN ST_GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mlinefromwkb(bytea) OWNER TO postgres;

--
-- Name: st_mlinefromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mlinefromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1, $2)) = 'MULTILINESTRING'
	THEN ST_GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mlinefromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_mpointfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mpointfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromText($1)) = 'MULTIPOINT'
	THEN ST_GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mpointfromtext(text) OWNER TO postgres;

--
-- Name: st_mpointfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mpointfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromText($1, $2)) = 'MULTIPOINT'
	THEN GeomFromText($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mpointfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_mpointfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mpointfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1)) = 'MULTIPOINT'
	THEN ST_GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mpointfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_mpointfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mpointfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1, $2)) = 'MULTIPOINT'
	THEN GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mpointfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_mpolyfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mpolyfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromText($1)) = 'MULTIPOLYGON'
	THEN ST_GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mpolyfromtext(text) OWNER TO postgres;

--
-- Name: st_mpolyfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mpolyfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromText($1, $2)) = 'MULTIPOLYGON'
	THEN ST_GeomFromText($1,$2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mpolyfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_mpolyfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mpolyfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1)) = 'MULTIPOLYGON'
	THEN ST_GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mpolyfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_mpolyfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_mpolyfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1, $2)) = 'MULTIPOLYGON'
	THEN ST_GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_mpolyfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_multi(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multi(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_force_multi';


ALTER FUNCTION public.st_multi(geometry) OWNER TO postgres;

--
-- Name: st_multilinefromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multilinefromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1)) = 'MULTILINESTRING'
	THEN ST_GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_multilinefromwkb(bytea) OWNER TO postgres;

--
-- Name: st_multilinestringfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multilinestringfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_MLineFromText($1)$_$;


ALTER FUNCTION public.st_multilinestringfromtext(text) OWNER TO postgres;

--
-- Name: st_multilinestringfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multilinestringfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT MLineFromText($1, $2)$_$;


ALTER FUNCTION public.st_multilinestringfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_multipointfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multipointfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT MPointFromText($1)$_$;


ALTER FUNCTION public.st_multipointfromtext(text) OWNER TO postgres;

--
-- Name: st_multipointfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multipointfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1)) = 'MULTIPOINT'
	THEN ST_GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_multipointfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_multipointfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multipointfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1,$2)) = 'MULTIPOINT'
	THEN ST_GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_multipointfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_multipolyfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multipolyfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1)) = 'MULTIPOLYGON'
	THEN ST_GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_multipolyfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_multipolyfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multipolyfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1, $2)) = 'MULTIPOLYGON'
	THEN ST_GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_multipolyfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_multipolygonfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multipolygonfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT MPolyFromText($1)$_$;


ALTER FUNCTION public.st_multipolygonfromtext(text) OWNER TO postgres;

--
-- Name: st_multipolygonfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_multipolygonfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT MPolyFromText($1, $2)$_$;


ALTER FUNCTION public.st_multipolygonfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_ndims(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_ndims(geometry) RETURNS smallint
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_ndims';


ALTER FUNCTION public.st_ndims(geometry) OWNER TO postgres;

--
-- Name: st_npoints(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_npoints(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_npoints';


ALTER FUNCTION public.st_npoints(geometry) OWNER TO postgres;

--
-- Name: st_nrings(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_nrings(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_nrings';


ALTER FUNCTION public.st_nrings(geometry) OWNER TO postgres;

--
-- Name: st_numgeometries(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_numgeometries(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_numgeometries_collection';


ALTER FUNCTION public.st_numgeometries(geometry) OWNER TO postgres;

--
-- Name: st_numinteriorring(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_numinteriorring(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_numinteriorrings_polygon';


ALTER FUNCTION public.st_numinteriorring(geometry) OWNER TO postgres;

--
-- Name: st_numinteriorrings(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_numinteriorrings(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_numinteriorrings_polygon';


ALTER FUNCTION public.st_numinteriorrings(geometry) OWNER TO postgres;

--
-- Name: st_numpoints(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_numpoints(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_numpoints_linestring';


ALTER FUNCTION public.st_numpoints(geometry) OWNER TO postgres;

--
-- Name: st_orderingequals(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_orderingequals(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ 
	SELECT $1 ~= $2 AND _ST_OrderingEquals($1, $2)
	$_$;


ALTER FUNCTION public.st_orderingequals(geometry, geometry) OWNER TO postgres;

--
-- Name: st_overlaps(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_overlaps(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_Overlaps($1,$2)$_$;


ALTER FUNCTION public.st_overlaps(geometry, geometry) OWNER TO postgres;

--
-- Name: st_perimeter(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_perimeter(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_perimeter2d_poly';


ALTER FUNCTION public.st_perimeter(geometry) OWNER TO postgres;

--
-- Name: st_perimeter2d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_perimeter2d(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_perimeter2d_poly';


ALTER FUNCTION public.st_perimeter2d(geometry) OWNER TO postgres;

--
-- Name: st_perimeter3d(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_perimeter3d(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_perimeter_poly';


ALTER FUNCTION public.st_perimeter3d(geometry) OWNER TO postgres;

--
-- Name: st_point(double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_point(double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_makepoint';


ALTER FUNCTION public.st_point(double precision, double precision) OWNER TO postgres;

--
-- Name: st_point_inside_circle(geometry, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_point_inside_circle(geometry, double precision, double precision, double precision) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_inside_circle_point';


ALTER FUNCTION public.st_point_inside_circle(geometry, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_pointfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_pointfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromText($1)) = 'POINT'
	THEN ST_GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_pointfromtext(text) OWNER TO postgres;

--
-- Name: st_pointfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_pointfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromText($1, $2)) = 'POINT'
	THEN ST_GeomFromText($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_pointfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_pointfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_pointfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1)) = 'POINT'
	THEN ST_GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_pointfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_pointfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_pointfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1, $2)) = 'POINT'
	THEN ST_GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_pointfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_pointn(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_pointn(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_pointn_linestring';


ALTER FUNCTION public.st_pointn(geometry, integer) OWNER TO postgres;

--
-- Name: st_pointonsurface(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_pointonsurface(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'pointonsurface';


ALTER FUNCTION public.st_pointonsurface(geometry) OWNER TO postgres;

--
-- Name: st_polyfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polyfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromText($1)) = 'POLYGON'
	THEN ST_GeomFromText($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_polyfromtext(text) OWNER TO postgres;

--
-- Name: st_polyfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polyfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromText($1, $2)) = 'POLYGON'
	THEN ST_GeomFromText($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_polyfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_polyfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polyfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1)) = 'POLYGON'
	THEN ST_GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_polyfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_polyfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polyfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1, $2)) = 'POLYGON'
	THEN ST_GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_polyfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_polygon(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polygon(geometry, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ 
	SELECT setSRID(makepolygon($1), $2)
	$_$;


ALTER FUNCTION public.st_polygon(geometry, integer) OWNER TO postgres;

--
-- Name: st_polygonfromtext(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polygonfromtext(text) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_PolyFromText($1)$_$;


ALTER FUNCTION public.st_polygonfromtext(text) OWNER TO postgres;

--
-- Name: st_polygonfromtext(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polygonfromtext(text, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT PolyFromText($1, $2)$_$;


ALTER FUNCTION public.st_polygonfromtext(text, integer) OWNER TO postgres;

--
-- Name: st_polygonfromwkb(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polygonfromwkb(bytea) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(GeomFromWKB($1)) = 'POLYGON'
	THEN GeomFromWKB($1)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_polygonfromwkb(bytea) OWNER TO postgres;

--
-- Name: st_polygonfromwkb(bytea, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polygonfromwkb(bytea, integer) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
	SELECT CASE WHEN geometrytype(ST_GeomFromWKB($1,$2)) = 'POLYGON'
	THEN ST_GeomFromWKB($1, $2)
	ELSE NULL END
	$_$;


ALTER FUNCTION public.st_polygonfromwkb(bytea, integer) OWNER TO postgres;

--
-- Name: st_polygonize(geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polygonize(geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'polygonize_garray';


ALTER FUNCTION public.st_polygonize(geometry[]) OWNER TO postgres;

--
-- Name: st_polygonize_garray(geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_polygonize_garray(geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'polygonize_garray';


ALTER FUNCTION public.st_polygonize_garray(geometry[]) OWNER TO postgres;

--
-- Name: st_postgis_gist_joinsel(internal, oid, internal, smallint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_postgis_gist_joinsel(internal, oid, internal, smallint) RETURNS double precision
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_joinsel';


ALTER FUNCTION public.st_postgis_gist_joinsel(internal, oid, internal, smallint) OWNER TO postgres;

--
-- Name: st_postgis_gist_sel(internal, oid, internal, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_postgis_gist_sel(internal, oid, internal, integer) RETURNS double precision
    LANGUAGE c
    AS '$libdir/postgis-1.5', 'LWGEOM_gist_sel';


ALTER FUNCTION public.st_postgis_gist_sel(internal, oid, internal, integer) OWNER TO postgres;

--
-- Name: st_relate(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_relate(geometry, geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'relate_full';


ALTER FUNCTION public.st_relate(geometry, geometry) OWNER TO postgres;

--
-- Name: st_relate(geometry, geometry, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_relate(geometry, geometry, text) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'relate_pattern';


ALTER FUNCTION public.st_relate(geometry, geometry, text) OWNER TO postgres;

--
-- Name: st_removepoint(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_removepoint(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_removepoint';


ALTER FUNCTION public.st_removepoint(geometry, integer) OWNER TO postgres;

--
-- Name: st_reverse(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_reverse(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_reverse';


ALTER FUNCTION public.st_reverse(geometry) OWNER TO postgres;

--
-- Name: st_rotate(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_rotate(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT rotateZ($1, $2)$_$;


ALTER FUNCTION public.st_rotate(geometry, double precision) OWNER TO postgres;

--
-- Name: st_rotatex(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_rotatex(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1, 1, 0, 0, 0, cos($2), -sin($2), 0, sin($2), cos($2), 0, 0, 0)$_$;


ALTER FUNCTION public.st_rotatex(geometry, double precision) OWNER TO postgres;

--
-- Name: st_rotatey(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_rotatey(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1,  cos($2), 0, sin($2),  0, 1, 0,  -sin($2), 0, cos($2), 0,  0, 0)$_$;


ALTER FUNCTION public.st_rotatey(geometry, double precision) OWNER TO postgres;

--
-- Name: st_rotatez(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_rotatez(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1,  cos($2), -sin($2), 0,  sin($2), cos($2), 0,  0, 0, 1,  0, 0, 0)$_$;


ALTER FUNCTION public.st_rotatez(geometry, double precision) OWNER TO postgres;

--
-- Name: st_scale(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_scale(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT scale($1, $2, $3, 1)$_$;


ALTER FUNCTION public.st_scale(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: st_scale(geometry, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_scale(geometry, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1,  $2, 0, 0,  0, $3, 0,  0, 0, $4,  0, 0, 0)$_$;


ALTER FUNCTION public.st_scale(geometry, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_segmentize(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_segmentize(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_segmentize2d';


ALTER FUNCTION public.st_segmentize(geometry, double precision) OWNER TO postgres;

--
-- Name: st_setfactor(chip, real); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_setfactor(chip, real) RETURNS chip
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_setFactor';


ALTER FUNCTION public.st_setfactor(chip, real) OWNER TO postgres;

--
-- Name: st_setpoint(geometry, integer, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_setpoint(geometry, integer, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_setpoint_linestring';


ALTER FUNCTION public.st_setpoint(geometry, integer, geometry) OWNER TO postgres;

--
-- Name: st_setsrid(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_setsrid(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_setSRID';


ALTER FUNCTION public.st_setsrid(geometry, integer) OWNER TO postgres;

--
-- Name: st_shift_longitude(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_shift_longitude(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_longitude_shift';


ALTER FUNCTION public.st_shift_longitude(geometry) OWNER TO postgres;

--
-- Name: st_shortestline(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_shortestline(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_shortestline2d';


ALTER FUNCTION public.st_shortestline(geometry, geometry) OWNER TO postgres;

--
-- Name: st_simplify(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_simplify(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_simplify2d';


ALTER FUNCTION public.st_simplify(geometry, double precision) OWNER TO postgres;

--
-- Name: st_simplifypreservetopology(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_simplifypreservetopology(geometry, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT COST 100
    AS '$libdir/postgis-1.5', 'topologypreservesimplify';


ALTER FUNCTION public.st_simplifypreservetopology(geometry, double precision) OWNER TO postgres;

--
-- Name: st_snaptogrid(geometry, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_snaptogrid(geometry, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_SnapToGrid($1, 0, 0, $2, $2)$_$;


ALTER FUNCTION public.st_snaptogrid(geometry, double precision) OWNER TO postgres;

--
-- Name: st_snaptogrid(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_snaptogrid(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT ST_SnapToGrid($1, 0, 0, $2, $3)$_$;


ALTER FUNCTION public.st_snaptogrid(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: st_snaptogrid(geometry, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_snaptogrid(geometry, double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_snaptogrid';


ALTER FUNCTION public.st_snaptogrid(geometry, double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_snaptogrid(geometry, geometry, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_snaptogrid(geometry, geometry, double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_snaptogrid_pointoff';


ALTER FUNCTION public.st_snaptogrid(geometry, geometry, double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_spheroid_in(cstring); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_spheroid_in(cstring) RETURNS spheroid
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'ellipsoid_in';


ALTER FUNCTION public.st_spheroid_in(cstring) OWNER TO postgres;

--
-- Name: st_spheroid_out(spheroid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_spheroid_out(spheroid) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'ellipsoid_out';


ALTER FUNCTION public.st_spheroid_out(spheroid) OWNER TO postgres;

--
-- Name: st_srid(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_srid(chip) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getSRID';


ALTER FUNCTION public.st_srid(chip) OWNER TO postgres;

--
-- Name: st_srid(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_srid(geometry) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_getSRID';


ALTER FUNCTION public.st_srid(geometry) OWNER TO postgres;

--
-- Name: st_startpoint(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_startpoint(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_startpoint_linestring';


ALTER FUNCTION public.st_startpoint(geometry) OWNER TO postgres;

--
-- Name: st_summary(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_summary(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_summary';


ALTER FUNCTION public.st_summary(geometry) OWNER TO postgres;

--
-- Name: st_symdifference(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_symdifference(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'symdifference';


ALTER FUNCTION public.st_symdifference(geometry, geometry) OWNER TO postgres;

--
-- Name: st_symmetricdifference(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_symmetricdifference(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'symdifference';


ALTER FUNCTION public.st_symmetricdifference(geometry, geometry) OWNER TO postgres;

--
-- Name: st_text(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_text(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_text';


ALTER FUNCTION public.st_text(geometry) OWNER TO postgres;

--
-- Name: st_touches(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_touches(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_Touches($1,$2)$_$;


ALTER FUNCTION public.st_touches(geometry, geometry) OWNER TO postgres;

--
-- Name: st_transform(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_transform(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'transform';


ALTER FUNCTION public.st_transform(geometry, integer) OWNER TO postgres;

--
-- Name: st_translate(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_translate(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT translate($1, $2, $3, 0)$_$;


ALTER FUNCTION public.st_translate(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: st_translate(geometry, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_translate(geometry, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1, 1, 0, 0, 0, 1, 0, 0, 0, 1, $2, $3, $4)$_$;


ALTER FUNCTION public.st_translate(geometry, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_transscale(geometry, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_transscale(geometry, double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1,  $4, 0, 0,  0, $5, 0,
		0, 0, 1,  $2 * $4, $3 * $5, 0)$_$;


ALTER FUNCTION public.st_transscale(geometry, double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: st_union(geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_union(geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'pgis_union_geometry_array';


ALTER FUNCTION public.st_union(geometry[]) OWNER TO postgres;

--
-- Name: st_union(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_union(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'geomunion';


ALTER FUNCTION public.st_union(geometry, geometry) OWNER TO postgres;

--
-- Name: st_unite_garray(geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_unite_garray(geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'pgis_union_geometry_array';


ALTER FUNCTION public.st_unite_garray(geometry[]) OWNER TO postgres;

--
-- Name: st_width(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_width(chip) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getWidth';


ALTER FUNCTION public.st_width(chip) OWNER TO postgres;

--
-- Name: st_within(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_within(geometry, geometry) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$SELECT $1 && $2 AND _ST_Within($1,$2)$_$;


ALTER FUNCTION public.st_within(geometry, geometry) OWNER TO postgres;

--
-- Name: st_wkbtosql(bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_wkbtosql(bytea) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_WKB';


ALTER FUNCTION public.st_wkbtosql(bytea) OWNER TO postgres;

--
-- Name: st_wkttosql(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_wkttosql(text) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_from_text';


ALTER FUNCTION public.st_wkttosql(text) OWNER TO postgres;

--
-- Name: st_x(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_x(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_x_point';


ALTER FUNCTION public.st_x(geometry) OWNER TO postgres;

--
-- Name: st_xmax(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_xmax(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_xmax';


ALTER FUNCTION public.st_xmax(box3d) OWNER TO postgres;

--
-- Name: st_xmin(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_xmin(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_xmin';


ALTER FUNCTION public.st_xmin(box3d) OWNER TO postgres;

--
-- Name: st_y(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_y(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_y_point';


ALTER FUNCTION public.st_y(geometry) OWNER TO postgres;

--
-- Name: st_ymax(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_ymax(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_ymax';


ALTER FUNCTION public.st_ymax(box3d) OWNER TO postgres;

--
-- Name: st_ymin(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_ymin(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_ymin';


ALTER FUNCTION public.st_ymin(box3d) OWNER TO postgres;

--
-- Name: st_z(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_z(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_z_point';


ALTER FUNCTION public.st_z(geometry) OWNER TO postgres;

--
-- Name: st_zmax(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_zmax(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_zmax';


ALTER FUNCTION public.st_zmax(box3d) OWNER TO postgres;

--
-- Name: st_zmflag(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_zmflag(geometry) RETURNS smallint
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_zmflag';


ALTER FUNCTION public.st_zmflag(geometry) OWNER TO postgres;

--
-- Name: st_zmin(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION st_zmin(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_zmin';


ALTER FUNCTION public.st_zmin(box3d) OWNER TO postgres;

--
-- Name: startpoint(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION startpoint(geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_startpoint_linestring';


ALTER FUNCTION public.startpoint(geometry) OWNER TO postgres;

--
-- Name: summary(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION summary(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_summary';


ALTER FUNCTION public.summary(geometry) OWNER TO postgres;

--
-- Name: symdifference(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION symdifference(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'symdifference';


ALTER FUNCTION public.symdifference(geometry, geometry) OWNER TO postgres;

--
-- Name: symmetricdifference(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION symmetricdifference(geometry, geometry) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'symdifference';


ALTER FUNCTION public.symmetricdifference(geometry, geometry) OWNER TO postgres;

--
-- Name: text(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION text(geometry) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_to_text';


ALTER FUNCTION public.text(geometry) OWNER TO postgres;

--
-- Name: touches(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION touches(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'touches';


ALTER FUNCTION public.touches(geometry, geometry) OWNER TO postgres;

--
-- Name: transform(geometry, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION transform(geometry, integer) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'transform';


ALTER FUNCTION public.transform(geometry, integer) OWNER TO postgres;

--
-- Name: translate(geometry, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION translate(geometry, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT translate($1, $2, $3, 0)$_$;


ALTER FUNCTION public.translate(geometry, double precision, double precision) OWNER TO postgres;

--
-- Name: translate(geometry, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION translate(geometry, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1, 1, 0, 0, 0, 1, 0, 0, 0, 1, $2, $3, $4)$_$;


ALTER FUNCTION public.translate(geometry, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: transscale(geometry, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION transscale(geometry, double precision, double precision, double precision, double precision) RETURNS geometry
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT affine($1,  $4, 0, 0,  0, $5, 0,
		0, 0, 1,  $2 * $4, $3 * $5, 0)$_$;


ALTER FUNCTION public.transscale(geometry, double precision, double precision, double precision, double precision) OWNER TO postgres;

--
-- Name: unite_garray(geometry[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION unite_garray(geometry[]) RETURNS geometry
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'pgis_union_geometry_array';


ALTER FUNCTION public.unite_garray(geometry[]) OWNER TO postgres;

--
-- Name: unlockrows(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION unlockrows(text) RETURNS integer
    LANGUAGE plpgsql STRICT
    AS $_$ 
DECLARE
	ret int;
BEGIN

	IF NOT LongTransactionsEnabled() THEN
		RAISE EXCEPTION 'Long transaction support disabled, use EnableLongTransaction() to enable.';
	END IF;

	EXECUTE 'DELETE FROM authorization_table where authid = ' ||
		quote_literal($1);

	GET DIAGNOSTICS ret = ROW_COUNT;

	RETURN ret;
END;
$_$;


ALTER FUNCTION public.unlockrows(text) OWNER TO postgres;

--
-- Name: updategeometrysrid(character varying, character varying, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION updategeometrysrid(character varying, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT UpdateGeometrySRID('','',$1,$2,$3) into ret;
	RETURN ret;
END;
$_$;


ALTER FUNCTION public.updategeometrysrid(character varying, character varying, integer) OWNER TO postgres;

--
-- Name: updategeometrysrid(character varying, character varying, character varying, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION updategeometrysrid(character varying, character varying, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT UpdateGeometrySRID('',$1,$2,$3,$4) into ret;
	RETURN ret;
END;
$_$;


ALTER FUNCTION public.updategeometrysrid(character varying, character varying, character varying, integer) OWNER TO postgres;

--
-- Name: updategeometrysrid(character varying, character varying, character varying, character varying, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION updategeometrysrid(character varying, character varying, character varying, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	catalog_name alias for $1;
	schema_name alias for $2;
	table_name alias for $3;
	column_name alias for $4;
	new_srid alias for $5;
	myrec RECORD;
	okay boolean;
	cname varchar;
	real_schema name;

BEGIN


	-- Find, check or fix schema_name
	IF ( schema_name != '' ) THEN
		okay = 'f';

		FOR myrec IN SELECT nspname FROM pg_namespace WHERE text(nspname) = schema_name LOOP
			okay := 't';
		END LOOP;

		IF ( okay <> 't' ) THEN
			RAISE EXCEPTION 'Invalid schema name';
		ELSE
			real_schema = schema_name;
		END IF;
	ELSE
		SELECT INTO real_schema current_schema()::text;
	END IF;

	-- Find out if the column is in the geometry_columns table
	okay = 'f';
	FOR myrec IN SELECT * from geometry_columns where f_table_schema = text(real_schema) and f_table_name = table_name and f_geometry_column = column_name LOOP
		okay := 't';
	END LOOP;
	IF (okay <> 't') THEN
		RAISE EXCEPTION 'column not found in geometry_columns table';
		RETURN 'f';
	END IF;

	-- Update ref from geometry_columns table
	EXECUTE 'UPDATE geometry_columns SET SRID = ' || new_srid::text ||
		' where f_table_schema = ' ||
		quote_literal(real_schema) || ' and f_table_name = ' ||
		quote_literal(table_name)  || ' and f_geometry_column = ' ||
		quote_literal(column_name);

	-- Make up constraint name
	cname = 'enforce_srid_'  || column_name;

	-- Drop enforce_srid constraint
	EXECUTE 'ALTER TABLE ' || quote_ident(real_schema) ||
		'.' || quote_ident(table_name) ||
		' DROP constraint ' || quote_ident(cname);

	-- Update geometries SRID
	EXECUTE 'UPDATE ' || quote_ident(real_schema) ||
		'.' || quote_ident(table_name) ||
		' SET ' || quote_ident(column_name) ||
		' = setSRID(' || quote_ident(column_name) ||
		', ' || new_srid::text || ')';

	-- Reset enforce_srid constraint
	EXECUTE 'ALTER TABLE ' || quote_ident(real_schema) ||
		'.' || quote_ident(table_name) ||
		' ADD constraint ' || quote_ident(cname) ||
		' CHECK (srid(' || quote_ident(column_name) ||
		') = ' || new_srid::text || ')';

	RETURN real_schema || '.' || table_name || '.' || column_name ||' SRID changed to ' || new_srid::text;

END;
$_$;


ALTER FUNCTION public.updategeometrysrid(character varying, character varying, character varying, character varying, integer) OWNER TO postgres;

--
-- Name: width(chip); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION width(chip) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'CHIP_getWidth';


ALTER FUNCTION public.width(chip) OWNER TO postgres;

--
-- Name: within(geometry, geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION within(geometry, geometry) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'within';


ALTER FUNCTION public.within(geometry, geometry) OWNER TO postgres;

--
-- Name: x(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION x(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_x_point';


ALTER FUNCTION public.x(geometry) OWNER TO postgres;

--
-- Name: xmax(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION xmax(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_xmax';


ALTER FUNCTION public.xmax(box3d) OWNER TO postgres;

--
-- Name: xmin(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION xmin(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_xmin';


ALTER FUNCTION public.xmin(box3d) OWNER TO postgres;

--
-- Name: y(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION y(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_y_point';


ALTER FUNCTION public.y(geometry) OWNER TO postgres;

--
-- Name: ymax(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION ymax(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_ymax';


ALTER FUNCTION public.ymax(box3d) OWNER TO postgres;

--
-- Name: ymin(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION ymin(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_ymin';


ALTER FUNCTION public.ymin(box3d) OWNER TO postgres;

--
-- Name: z(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION z(geometry) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_z_point';


ALTER FUNCTION public.z(geometry) OWNER TO postgres;

--
-- Name: zmax(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION zmax(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_zmax';


ALTER FUNCTION public.zmax(box3d) OWNER TO postgres;

--
-- Name: zmflag(geometry); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION zmflag(geometry) RETURNS smallint
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'LWGEOM_zmflag';


ALTER FUNCTION public.zmflag(geometry) OWNER TO postgres;

--
-- Name: zmin(box3d); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION zmin(box3d) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/postgis-1.5', 'BOX3D_zmin';


ALTER FUNCTION public.zmin(box3d) OWNER TO postgres;

--
-- Name: accum(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE accum(geometry) (
    SFUNC = pgis_geometry_accum_transfn,
    STYPE = pgis_abs,
    FINALFUNC = pgis_geometry_accum_finalfn
);


ALTER AGGREGATE public.accum(geometry) OWNER TO postgres;

--
-- Name: collect(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE collect(geometry) (
    SFUNC = pgis_geometry_accum_transfn,
    STYPE = pgis_abs,
    FINALFUNC = pgis_geometry_collect_finalfn
);


ALTER AGGREGATE public.collect(geometry) OWNER TO postgres;

--
-- Name: extent(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE extent(geometry) (
    SFUNC = public.st_combine_bbox,
    STYPE = box3d_extent
);


ALTER AGGREGATE public.extent(geometry) OWNER TO postgres;

--
-- Name: extent3d(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE extent3d(geometry) (
    SFUNC = public.combine_bbox,
    STYPE = box3d
);


ALTER AGGREGATE public.extent3d(geometry) OWNER TO postgres;

--
-- Name: makeline(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE makeline(geometry) (
    SFUNC = pgis_geometry_accum_transfn,
    STYPE = pgis_abs,
    FINALFUNC = pgis_geometry_makeline_finalfn
);


ALTER AGGREGATE public.makeline(geometry) OWNER TO postgres;

--
-- Name: memcollect(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE memcollect(geometry) (
    SFUNC = public.st_collect,
    STYPE = geometry
);


ALTER AGGREGATE public.memcollect(geometry) OWNER TO postgres;

--
-- Name: memgeomunion(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE memgeomunion(geometry) (
    SFUNC = geomunion,
    STYPE = geometry
);


ALTER AGGREGATE public.memgeomunion(geometry) OWNER TO postgres;

--
-- Name: polygonize(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE polygonize(geometry) (
    SFUNC = pgis_geometry_accum_transfn,
    STYPE = pgis_abs,
    FINALFUNC = pgis_geometry_polygonize_finalfn
);


ALTER AGGREGATE public.polygonize(geometry) OWNER TO postgres;

--
-- Name: st_accum(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE st_accum(geometry) (
    SFUNC = pgis_geometry_accum_transfn,
    STYPE = pgis_abs,
    FINALFUNC = pgis_geometry_accum_finalfn
);


ALTER AGGREGATE public.st_accum(geometry) OWNER TO postgres;

--
-- Name: st_collect(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE st_collect(geometry) (
    SFUNC = pgis_geometry_accum_transfn,
    STYPE = pgis_abs,
    FINALFUNC = pgis_geometry_collect_finalfn
);


ALTER AGGREGATE public.st_collect(geometry) OWNER TO postgres;

--
-- Name: st_extent(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE st_extent(geometry) (
    SFUNC = public.st_combine_bbox,
    STYPE = box3d_extent
);


ALTER AGGREGATE public.st_extent(geometry) OWNER TO postgres;

--
-- Name: st_extent3d(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE st_extent3d(geometry) (
    SFUNC = public.st_combine_bbox,
    STYPE = box3d
);


ALTER AGGREGATE public.st_extent3d(geometry) OWNER TO postgres;

--
-- Name: st_makeline(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE st_makeline(geometry) (
    SFUNC = pgis_geometry_accum_transfn,
    STYPE = pgis_abs,
    FINALFUNC = pgis_geometry_makeline_finalfn
);


ALTER AGGREGATE public.st_makeline(geometry) OWNER TO postgres;

--
-- Name: st_memcollect(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE st_memcollect(geometry) (
    SFUNC = public.st_collect,
    STYPE = geometry
);


ALTER AGGREGATE public.st_memcollect(geometry) OWNER TO postgres;

--
-- Name: st_memunion(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE st_memunion(geometry) (
    SFUNC = public.st_union,
    STYPE = geometry
);


ALTER AGGREGATE public.st_memunion(geometry) OWNER TO postgres;

--
-- Name: st_polygonize(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE st_polygonize(geometry) (
    SFUNC = pgis_geometry_accum_transfn,
    STYPE = pgis_abs,
    FINALFUNC = pgis_geometry_polygonize_finalfn
);


ALTER AGGREGATE public.st_polygonize(geometry) OWNER TO postgres;

--
-- Name: st_union(geometry); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE st_union(geometry) (
    SFUNC = pgis_geometry_accum_transfn,
    STYPE = pgis_abs,
    FINALFUNC = pgis_geometry_union_finalfn
);


ALTER AGGREGATE public.st_union(geometry) OWNER TO postgres;

--
-- Name: &&; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR && (
    PROCEDURE = geometry_overlap,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = &&,
    RESTRICT = geometry_gist_sel,
    JOIN = geometry_gist_joinsel
);


ALTER OPERATOR public.&& (geometry, geometry) OWNER TO postgres;

--
-- Name: &&; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR && (
    PROCEDURE = geography_overlaps,
    LEFTARG = geography,
    RIGHTARG = geography,
    COMMUTATOR = &&,
    RESTRICT = geography_gist_selectivity,
    JOIN = geography_gist_join_selectivity
);


ALTER OPERATOR public.&& (geography, geography) OWNER TO postgres;

--
-- Name: &<; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR &< (
    PROCEDURE = geometry_overleft,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = &>,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


ALTER OPERATOR public.&< (geometry, geometry) OWNER TO postgres;

--
-- Name: &<|; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR &<| (
    PROCEDURE = geometry_overbelow,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = |&>,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


ALTER OPERATOR public.&<| (geometry, geometry) OWNER TO postgres;

--
-- Name: &>; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR &> (
    PROCEDURE = geometry_overright,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = &<,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


ALTER OPERATOR public.&> (geometry, geometry) OWNER TO postgres;

--
-- Name: <; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR < (
    PROCEDURE = geometry_lt,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = >,
    NEGATOR = >=,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.< (geometry, geometry) OWNER TO postgres;

--
-- Name: <; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR < (
    PROCEDURE = geography_lt,
    LEFTARG = geography,
    RIGHTARG = geography,
    COMMUTATOR = >,
    NEGATOR = >=,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.< (geography, geography) OWNER TO postgres;

--
-- Name: <<; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR << (
    PROCEDURE = geometry_left,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = >>,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


ALTER OPERATOR public.<< (geometry, geometry) OWNER TO postgres;

--
-- Name: <<|; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR <<| (
    PROCEDURE = geometry_below,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = |>>,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


ALTER OPERATOR public.<<| (geometry, geometry) OWNER TO postgres;

--
-- Name: <=; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR <= (
    PROCEDURE = geometry_le,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = >=,
    NEGATOR = >,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.<= (geometry, geometry) OWNER TO postgres;

--
-- Name: <=; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR <= (
    PROCEDURE = geography_le,
    LEFTARG = geography,
    RIGHTARG = geography,
    COMMUTATOR = >=,
    NEGATOR = >,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.<= (geography, geography) OWNER TO postgres;

--
-- Name: =; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR = (
    PROCEDURE = geometry_eq,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = =,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.= (geometry, geometry) OWNER TO postgres;

--
-- Name: =; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR = (
    PROCEDURE = geography_eq,
    LEFTARG = geography,
    RIGHTARG = geography,
    COMMUTATOR = =,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.= (geography, geography) OWNER TO postgres;

--
-- Name: >; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR > (
    PROCEDURE = geometry_gt,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = <,
    NEGATOR = <=,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.> (geometry, geometry) OWNER TO postgres;

--
-- Name: >; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR > (
    PROCEDURE = geography_gt,
    LEFTARG = geography,
    RIGHTARG = geography,
    COMMUTATOR = <,
    NEGATOR = <=,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.> (geography, geography) OWNER TO postgres;

--
-- Name: >=; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR >= (
    PROCEDURE = geometry_ge,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = <=,
    NEGATOR = <,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.>= (geometry, geometry) OWNER TO postgres;

--
-- Name: >=; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR >= (
    PROCEDURE = geography_ge,
    LEFTARG = geography,
    RIGHTARG = geography,
    COMMUTATOR = <=,
    NEGATOR = <,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.>= (geography, geography) OWNER TO postgres;

--
-- Name: >>; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR >> (
    PROCEDURE = geometry_right,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = <<,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


ALTER OPERATOR public.>> (geometry, geometry) OWNER TO postgres;

--
-- Name: @; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR @ (
    PROCEDURE = geometry_contained,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = ~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.@ (geometry, geometry) OWNER TO postgres;

--
-- Name: |&>; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR |&> (
    PROCEDURE = geometry_overabove,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = &<|,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


ALTER OPERATOR public.|&> (geometry, geometry) OWNER TO postgres;

--
-- Name: |>>; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR |>> (
    PROCEDURE = geometry_above,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = <<|,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


ALTER OPERATOR public.|>> (geometry, geometry) OWNER TO postgres;

--
-- Name: ~; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR ~ (
    PROCEDURE = geometry_contain,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = @,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


ALTER OPERATOR public.~ (geometry, geometry) OWNER TO postgres;

--
-- Name: ~=; Type: OPERATOR; Schema: public; Owner: postgres
--

CREATE OPERATOR ~= (
    PROCEDURE = geometry_samebox,
    LEFTARG = geometry,
    RIGHTARG = geometry,
    COMMUTATOR = ~=,
    RESTRICT = eqsel,
    JOIN = eqjoinsel
);


ALTER OPERATOR public.~= (geometry, geometry) OWNER TO postgres;

--
-- Name: btree_geography_ops; Type: OPERATOR CLASS; Schema: public; Owner: postgres
--

CREATE OPERATOR CLASS btree_geography_ops
    DEFAULT FOR TYPE geography USING btree AS
    OPERATOR 1 <(geography,geography) ,
    OPERATOR 2 <=(geography,geography) ,
    OPERATOR 3 =(geography,geography) ,
    OPERATOR 4 >=(geography,geography) ,
    OPERATOR 5 >(geography,geography) ,
    FUNCTION 1 geography_cmp(geography,geography);


ALTER OPERATOR CLASS public.btree_geography_ops USING btree OWNER TO postgres;

--
-- Name: btree_geometry_ops; Type: OPERATOR CLASS; Schema: public; Owner: postgres
--

CREATE OPERATOR CLASS btree_geometry_ops
    DEFAULT FOR TYPE geometry USING btree AS
    OPERATOR 1 <(geometry,geometry) ,
    OPERATOR 2 <=(geometry,geometry) ,
    OPERATOR 3 =(geometry,geometry) ,
    OPERATOR 4 >=(geometry,geometry) ,
    OPERATOR 5 >(geometry,geometry) ,
    FUNCTION 1 geometry_cmp(geometry,geometry);


ALTER OPERATOR CLASS public.btree_geometry_ops USING btree OWNER TO postgres;

--
-- Name: gist_geography_ops; Type: OPERATOR CLASS; Schema: public; Owner: postgres
--

CREATE OPERATOR CLASS gist_geography_ops
    DEFAULT FOR TYPE geography USING gist AS
    STORAGE gidx ,
    OPERATOR 3 &&(geography,geography) ,
    FUNCTION 1 geography_gist_consistent(internal,geometry,integer) ,
    FUNCTION 2 geography_gist_union(bytea,internal) ,
    FUNCTION 3 geography_gist_compress(internal) ,
    FUNCTION 4 geography_gist_decompress(internal) ,
    FUNCTION 5 geography_gist_penalty(internal,internal,internal) ,
    FUNCTION 6 geography_gist_picksplit(internal,internal) ,
    FUNCTION 7 geography_gist_same(box2d,box2d,internal);


ALTER OPERATOR CLASS public.gist_geography_ops USING gist OWNER TO postgres;

--
-- Name: gist_geometry_ops; Type: OPERATOR CLASS; Schema: public; Owner: postgres
--

CREATE OPERATOR CLASS gist_geometry_ops
    DEFAULT FOR TYPE geometry USING gist AS
    STORAGE box2d ,
    OPERATOR 1 <<(geometry,geometry) ,
    OPERATOR 2 &<(geometry,geometry) ,
    OPERATOR 3 &&(geometry,geometry) ,
    OPERATOR 4 &>(geometry,geometry) ,
    OPERATOR 5 >>(geometry,geometry) ,
    OPERATOR 6 ~=(geometry,geometry) ,
    OPERATOR 7 ~(geometry,geometry) ,
    OPERATOR 8 @(geometry,geometry) ,
    OPERATOR 9 &<|(geometry,geometry) ,
    OPERATOR 10 <<|(geometry,geometry) ,
    OPERATOR 11 |>>(geometry,geometry) ,
    OPERATOR 12 |&>(geometry,geometry) ,
    FUNCTION 1 lwgeom_gist_consistent(internal,geometry,integer) ,
    FUNCTION 2 lwgeom_gist_union(bytea,internal) ,
    FUNCTION 3 lwgeom_gist_compress(internal) ,
    FUNCTION 4 lwgeom_gist_decompress(internal) ,
    FUNCTION 5 lwgeom_gist_penalty(internal,internal,internal) ,
    FUNCTION 6 lwgeom_gist_picksplit(internal,internal) ,
    FUNCTION 7 lwgeom_gist_same(box2d,box2d,internal);


ALTER OPERATOR CLASS public.gist_geometry_ops USING gist OWNER TO postgres;

SET search_path = pg_catalog;

--
-- Name: CAST (public.box2d AS public.box3d); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.box2d AS public.box3d) WITH FUNCTION public.box3d(public.box2d) AS IMPLICIT;


--
-- Name: CAST (public.box2d AS public.geometry); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.box2d AS public.geometry) WITH FUNCTION public.geometry(public.box2d) AS IMPLICIT;


--
-- Name: CAST (public.box3d AS box); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.box3d AS box) WITH FUNCTION public.box(public.box3d) AS IMPLICIT;


--
-- Name: CAST (public.box3d AS public.box2d); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.box3d AS public.box2d) WITH FUNCTION public.box2d(public.box3d) AS IMPLICIT;


--
-- Name: CAST (public.box3d AS public.geometry); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.box3d AS public.geometry) WITH FUNCTION public.geometry(public.box3d) AS IMPLICIT;


--
-- Name: CAST (public.box3d_extent AS public.box2d); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.box3d_extent AS public.box2d) WITH FUNCTION public.box2d(public.box3d_extent) AS IMPLICIT;


--
-- Name: CAST (public.box3d_extent AS public.box3d); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.box3d_extent AS public.box3d) WITH FUNCTION public.box3d_extent(public.box3d_extent) AS IMPLICIT;


--
-- Name: CAST (public.box3d_extent AS public.geometry); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.box3d_extent AS public.geometry) WITH FUNCTION public.geometry(public.box3d_extent) AS IMPLICIT;


--
-- Name: CAST (bytea AS public.geometry); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (bytea AS public.geometry) WITH FUNCTION public.geometry(bytea) AS IMPLICIT;


--
-- Name: CAST (public.chip AS public.geometry); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.chip AS public.geometry) WITH FUNCTION public.geometry(public.chip) AS IMPLICIT;


--
-- Name: CAST (public.geography AS public.geography); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.geography AS public.geography) WITH FUNCTION public.geography(public.geography, integer, boolean) AS IMPLICIT;


--
-- Name: CAST (public.geography AS public.geometry); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.geography AS public.geometry) WITH FUNCTION public.geometry(public.geography);


--
-- Name: CAST (public.geometry AS box); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.geometry AS box) WITH FUNCTION public.box(public.geometry) AS IMPLICIT;


--
-- Name: CAST (public.geometry AS public.box2d); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.geometry AS public.box2d) WITH FUNCTION public.box2d(public.geometry) AS IMPLICIT;


--
-- Name: CAST (public.geometry AS public.box3d); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.geometry AS public.box3d) WITH FUNCTION public.box3d(public.geometry) AS IMPLICIT;


--
-- Name: CAST (public.geometry AS bytea); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.geometry AS bytea) WITH FUNCTION public.bytea(public.geometry) AS IMPLICIT;


--
-- Name: CAST (public.geometry AS public.geography); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.geometry AS public.geography) WITH FUNCTION public.geography(public.geometry) AS IMPLICIT;


--
-- Name: CAST (public.geometry AS text); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (public.geometry AS text) WITH FUNCTION public.text(public.geometry) AS IMPLICIT;


--
-- Name: CAST (text AS public.geometry); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (text AS public.geometry) WITH FUNCTION public.geometry(text) AS IMPLICIT;


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: account; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE account (
    id bigint NOT NULL,
    active boolean,
    admin boolean,
    email character varying(255),
    lastlogin timestamp without time zone,
    password character varying(255),
    passwordchangetoken character varying(255),
    username character varying(255),
    agency_id bigint
);


ALTER TABLE public.account OWNER TO postgres;

--
-- Name: agency; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE agency (
    id bigint NOT NULL,
    color character varying(255),
    defaultlat double precision,
    defaultlon double precision,
    gtfsagencyid character varying(255),
    lang character varying(255),
    name character varying(255),
    phone character varying(255),
    systemmap boolean,
    timezone character varying(255),
    url character varying(255),
    defaultroutetype_id bigint
);


ALTER TABLE public.agency OWNER TO postgres;

--
-- Name: geography_columns; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW geography_columns AS
    SELECT current_database() AS f_table_catalog, n.nspname AS f_table_schema, c.relname AS f_table_name, a.attname AS f_geography_column, geography_typmod_dims(a.atttypmod) AS coord_dimension, geography_typmod_srid(a.atttypmod) AS srid, geography_typmod_type(a.atttypmod) AS type FROM pg_class c, pg_attribute a, pg_type t, pg_namespace n WHERE ((((((t.typname = 'geography'::name) AND (a.attisdropped = false)) AND (a.atttypid = t.oid)) AND (a.attrelid = c.oid)) AND (c.relnamespace = n.oid)) AND (NOT pg_is_other_temp_schema(c.relnamespace)));


ALTER TABLE public.geography_columns OWNER TO postgres;

SET default_with_oids = true;

--
-- Name: geometry_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE geometry_columns (
    f_table_catalog character varying(256) NOT NULL,
    f_table_schema character varying(256) NOT NULL,
    f_table_name character varying(256) NOT NULL,
    f_geometry_column character varying(256) NOT NULL,
    coord_dimension integer NOT NULL,
    srid integer NOT NULL,
    type character varying(30) NOT NULL
);


ALTER TABLE public.geometry_columns OWNER TO postgres;

SET default_with_oids = false;

--
-- Name: gisexport; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gisexport (
    id bigint NOT NULL,
    creationdate timestamp without time zone,
    description character varying(255),
    status character varying(255),
    type character varying(255)
);


ALTER TABLE public.gisexport OWNER TO postgres;

--
-- Name: gisexport_agency; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gisexport_agency (
    gisexport_id bigint NOT NULL,
    agencies_id bigint NOT NULL
);


ALTER TABLE public.gisexport_agency OWNER TO postgres;

--
-- Name: gisroute; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gisroute (
    id bigint NOT NULL,
    description character varying(255),
    oid character varying(255),
    originalshape geometry,
    routeid character varying(255),
    routename character varying(255),
    routetype character varying(255),
    agency_id bigint,
    gisupload_id bigint
);


ALTER TABLE public.gisroute OWNER TO postgres;

--
-- Name: gisroutealignment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gisroutealignment (
    id bigint NOT NULL,
    description character varying(255),
    gtfsshape geometry,
    reversealignment boolean,
    gisroute_id bigint
);


ALTER TABLE public.gisroutealignment OWNER TO postgres;

--
-- Name: gisroutecontrolpoint; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gisroutecontrolpoint (
    id bigint NOT NULL,
    controlpoint geometry,
    originalsequence integer,
    gisroute_id bigint
);


ALTER TABLE public.gisroutecontrolpoint OWNER TO postgres;

--
-- Name: gisroutecontrolpointsequence; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gisroutecontrolpointsequence (
    id bigint NOT NULL,
    sequence integer,
    controlpoint_id bigint,
    gisroutealignment_id bigint,
    segment_id bigint
);


ALTER TABLE public.gisroutecontrolpointsequence OWNER TO postgres;

--
-- Name: gisroutesegment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gisroutesegment (
    id bigint NOT NULL,
    reverse boolean,
    segment geometry,
    frompoint_id bigint,
    topoint_id bigint
);


ALTER TABLE public.gisroutesegment OWNER TO postgres;

--
-- Name: gisstop; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gisstop (
    id bigint NOT NULL,
    description character varying(255),
    oid character varying(255),
    shape geometry,
    stopid character varying(255),
    stopname character varying(255),
    agency_id bigint,
    gisupload_id bigint
);


ALTER TABLE public.gisstop OWNER TO postgres;

--
-- Name: gisupload; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gisupload (
    id bigint NOT NULL,
    creationdate timestamp without time zone,
    description character varying(255),
    fielddescription character varying(255),
    fieldid character varying(255),
    fieldname character varying(255),
    fieldtype character varying(255),
    status character varying(255),
    type character varying(255),
    agency_id bigint
);


ALTER TABLE public.gisupload OWNER TO postgres;

--
-- Name: gisuploadfield; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gisuploadfield (
    id bigint NOT NULL,
    fieldname character varying(255),
    fieldposition bigint,
    fieldtype character varying(255),
    gisupload_id bigint
);


ALTER TABLE public.gisuploadfield OWNER TO postgres;

--
-- Name: gtfssnapshot; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gtfssnapshot (
    id bigint NOT NULL,
    creationdate timestamp without time zone,
    description character varying(255),
    source character varying(255)
);


ALTER TABLE public.gtfssnapshot OWNER TO postgres;

--
-- Name: gtfssnapshotexport; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gtfssnapshotexport (
    id bigint NOT NULL,
    calendarfrom timestamp without time zone,
    calendarto timestamp without time zone,
    calendars character varying(255),
    description character varying(255),
    mergecomplete timestamp without time zone,
    mergestarted timestamp without time zone,
    source character varying(255),
    status character varying(255)
);


ALTER TABLE public.gtfssnapshotexport OWNER TO postgres;

--
-- Name: gtfssnapshotexport_agency; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gtfssnapshotexport_agency (
    gtfssnapshotexport_id bigint NOT NULL,
    agencies_id bigint NOT NULL
);


ALTER TABLE public.gtfssnapshotexport_agency OWNER TO postgres;

--
-- Name: gtfssnapshotmerge; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gtfssnapshotmerge (
    id bigint NOT NULL,
    description character varying(255),
    mergecomplete timestamp without time zone,
    mergestarted timestamp without time zone,
    status character varying(255),
    snapshot_id bigint
);


ALTER TABLE public.gtfssnapshotmerge OWNER TO postgres;

--
-- Name: gtfssnapshotmergetask; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gtfssnapshotmergetask (
    id bigint NOT NULL,
    description character varying(255),
    status character varying(255),
    taskcompleted timestamp without time zone,
    taskstarted timestamp without time zone,
    merge_id bigint
);


ALTER TABLE public.gtfssnapshotmergetask OWNER TO postgres;

--
-- Name: gtfssnapshotvalidation; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gtfssnapshotvalidation (
    id bigint NOT NULL,
    status character varying(255),
    validationdesciption character varying(255),
    snapshot_id bigint
);


ALTER TABLE public.gtfssnapshotvalidation OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: route; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE route (
    id bigint NOT NULL,
    comments character varying(255),
    gtfsrouteid character varying(255),
    publiclyvisible boolean,
    routecolor character varying(255),
    routedesc character varying(255),
    routelongname character varying(255),
    routeshortname character varying(255),
    routetextcolor character varying(255),
    routeurl character varying(255),
    saturday boolean,
    status character varying(255),
    sunday boolean,
    weekday boolean,
    agency_id bigint,
    gisroute_id bigint,
    gisupload_id bigint,
    routetype_id bigint
);


ALTER TABLE public.route OWNER TO postgres;

--
-- Name: routetype; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE routetype (
    id bigint NOT NULL,
    description character varying(255),
    gtfsroutetype character varying(255),
    hvtroutetype character varying(255),
    localizedvehicletype character varying(255)
);


ALTER TABLE public.routetype OWNER TO postgres;

--
-- Name: servicecalendar; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE servicecalendar (
    id bigint NOT NULL,
    description character varying(255),
    enddate timestamp without time zone,
    friday boolean,
    gtfsserviceid character varying(255),
    monday boolean,
    saturday boolean,
    startdate timestamp without time zone,
    sunday boolean,
    thursday boolean,
    tuesday boolean,
    wednesday boolean,
    agency_id bigint
);


ALTER TABLE public.servicecalendar OWNER TO postgres;

--
-- Name: servicecalendardate; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE servicecalendardate (
    id bigint NOT NULL,
    date timestamp without time zone,
    description character varying(255),
    exceptiontype character varying(255),
    gtfsserviceid character varying(255),
    calendar_id bigint
);


ALTER TABLE public.servicecalendardate OWNER TO postgres;

--
-- Name: spatial_ref_sys; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE spatial_ref_sys (
    srid integer NOT NULL,
    auth_name character varying(256),
    auth_srid integer,
    srtext character varying(2048),
    proj4text character varying(2048)
);


ALTER TABLE public.spatial_ref_sys OWNER TO postgres;

--
-- Name: stop; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stop (
    id bigint NOT NULL,
    bikeparking character varying(255),
    gtfsstopid character varying(255),
    location geometry,
    locationtype character varying(255),
    majorstop boolean,
    parentstation character varying(255),
    stopcode character varying(255),
    stopdesc character varying(255),
    stopiconurl character varying(255),
    stopname character varying(255),
    stopurl character varying(255),
    wheelchairboarding character varying(255),
    zoneid character varying(255),
    agency_id bigint
);


ALTER TABLE public.stop OWNER TO postgres;

--
-- Name: stoptime; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stoptime (
    id bigint NOT NULL,
    arrivaltime integer,
    departuretime integer,
    dropofftype character varying(255),
    pickuptype character varying(255),
    shapedisttraveled double precision,
    stopheadsign character varying(255),
    stopsequence integer,
    stop_id bigint,
    trip_id bigint
);


ALTER TABLE public.stoptime OWNER TO postgres;

--
-- Name: stoptype; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stoptype (
    id bigint NOT NULL,
    description character varying(255),
    interpolated boolean,
    majorstop boolean,
    stoptype character varying(255)
);


ALTER TABLE public.stoptype OWNER TO postgres;

--
-- Name: trip; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE trip (
    id bigint NOT NULL,
    blockid character varying(255),
    endtime integer,
    gtfstripid character varying(255),
    headway integer,
    starttime integer,
    tripdescription character varying(255),
    tripdirection character varying(255),
    tripheadsign character varying(255),
    tripshortname character varying(255),
    usefrequency boolean,
    pattern_id bigint,
    route_id bigint,
    servicecalendar_id bigint,
    servicecalendardate_id bigint,
    shape_id bigint
);


ALTER TABLE public.trip OWNER TO postgres;

--
-- Name: trippattern; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE trippattern (
    id bigint NOT NULL,
    encodedshape text,
    endtime integer,
    headsign character varying(255),
    headway integer,
    longest boolean,
    name character varying(255),
    saturday boolean,
    starttime integer,
    sunday boolean,
    usefrequency boolean,
    weekday boolean,
    route_id bigint,
    shape_id bigint
);


ALTER TABLE public.trippattern OWNER TO postgres;

--
-- Name: trippattern_trippatternstop; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE trippattern_trippatternstop (
    trippattern_id bigint NOT NULL,
    patternstops_id bigint NOT NULL
);


ALTER TABLE public.trippattern_trippatternstop OWNER TO postgres;

--
-- Name: trippatternstop; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE trippatternstop (
    id bigint NOT NULL,
    defaultdistance double precision,
    defaultdwelltime integer,
    defaulttraveltime integer,
    stopsequence integer,
    pattern_id bigint,
    stop_id bigint
);


ALTER TABLE public.trippatternstop OWNER TO postgres;

--
-- Name: tripshape; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tripshape (
    id bigint NOT NULL,
    describeddistance double precision,
    description character varying(255),
    gtfsshapeid character varying(255),
    shape geometry,
    simpleshape geometry,
    agency_id bigint
);


ALTER TABLE public.tripshape OWNER TO postgres;

--
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY account (id, active, admin, email, lastlogin, password, passwordchangetoken, username, agency_id) FROM stdin;
1	t	t	admin@test.com	2013-06-13 12:53:43.371	82bd1385596fe315e13aa179e49fa758	\N	admin	\N
2	t	t	kwebb@conveyal.com	2013-06-13 12:58:15.838	84346bf630817e0f21b21691829eeb24	\N	cats	\N
\.


--
-- Data for Name: agency; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY agency (id, color, defaultlat, defaultlon, gtfsagencyid, lang, name, phone, systemmap, timezone, url, defaultroutetype_id) FROM stdin;
4	\N	39.920532644455875	116.389846801757798	TEST	zh	Test Agency	\N	\N	Asia/Shanghai	http://test.com/	3
\.


--
-- Data for Name: geometry_columns; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY geometry_columns (f_table_catalog, f_table_schema, f_table_name, f_geometry_column, coord_dimension, srid, type) FROM stdin;
\.


--
-- Data for Name: gisexport; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gisexport (id, creationdate, description, status, type) FROM stdin;
\.


--
-- Data for Name: gisexport_agency; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gisexport_agency (gisexport_id, agencies_id) FROM stdin;
\.


--
-- Data for Name: gisroute; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gisroute (id, description, oid, originalshape, routeid, routename, routetype, agency_id, gisupload_id) FROM stdin;
\.


--
-- Data for Name: gisroutealignment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gisroutealignment (id, description, gtfsshape, reversealignment, gisroute_id) FROM stdin;
\.


--
-- Data for Name: gisroutecontrolpoint; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gisroutecontrolpoint (id, controlpoint, originalsequence, gisroute_id) FROM stdin;
\.


--
-- Data for Name: gisroutecontrolpointsequence; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gisroutecontrolpointsequence (id, sequence, controlpoint_id, gisroutealignment_id, segment_id) FROM stdin;
\.


--
-- Data for Name: gisroutesegment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gisroutesegment (id, reverse, segment, frompoint_id, topoint_id) FROM stdin;
\.


--
-- Data for Name: gisstop; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gisstop (id, description, oid, shape, stopid, stopname, agency_id, gisupload_id) FROM stdin;
\.


--
-- Data for Name: gisupload; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gisupload (id, creationdate, description, fielddescription, fieldid, fieldname, fieldtype, status, type, agency_id) FROM stdin;
\.


--
-- Data for Name: gisuploadfield; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gisuploadfield (id, fieldname, fieldposition, fieldtype, gisupload_id) FROM stdin;
\.


--
-- Data for Name: gtfssnapshot; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gtfssnapshot (id, creationdate, description, source) FROM stdin;
\.


--
-- Data for Name: gtfssnapshotexport; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gtfssnapshotexport (id, calendarfrom, calendarto, calendars, description, mergecomplete, mergestarted, source, status) FROM stdin;
\.


--
-- Data for Name: gtfssnapshotexport_agency; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gtfssnapshotexport_agency (gtfssnapshotexport_id, agencies_id) FROM stdin;
\.


--
-- Data for Name: gtfssnapshotmerge; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gtfssnapshotmerge (id, description, mergecomplete, mergestarted, status, snapshot_id) FROM stdin;
\.


--
-- Data for Name: gtfssnapshotmergetask; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gtfssnapshotmergetask (id, description, status, taskcompleted, taskstarted, merge_id) FROM stdin;
\.


--
-- Data for Name: gtfssnapshotvalidation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY gtfssnapshotvalidation (id, status, validationdesciption, snapshot_id) FROM stdin;
\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('hibernate_sequence', 4, true);


--
-- Data for Name: route; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY route (id, comments, gtfsrouteid, publiclyvisible, routecolor, routedesc, routelongname, routeshortname, routetextcolor, routeurl, saturday, status, sunday, weekday, agency_id, gisroute_id, gisupload_id, routetype_id) FROM stdin;
\.


--
-- Data for Name: routetype; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY routetype (id, description, gtfsroutetype, hvtroutetype, localizedvehicletype) FROM stdin;
3	Bus	BUS	BUS	Bus
\.


--
-- Data for Name: servicecalendar; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY servicecalendar (id, description, enddate, friday, gtfsserviceid, monday, saturday, startdate, sunday, thursday, tuesday, wednesday, agency_id) FROM stdin;
\.


--
-- Data for Name: servicecalendardate; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY servicecalendardate (id, date, description, exceptiontype, gtfsserviceid, calendar_id) FROM stdin;
\.


--
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY spatial_ref_sys (srid, auth_name, auth_srid, srtext, proj4text) FROM stdin;
\.


--
-- Data for Name: stop; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY stop (id, bikeparking, gtfsstopid, location, locationtype, majorstop, parentstation, stopcode, stopdesc, stopiconurl, stopname, stopurl, wheelchairboarding, zoneid, agency_id) FROM stdin;
\.


--
-- Data for Name: stoptime; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY stoptime (id, arrivaltime, departuretime, dropofftype, pickuptype, shapedisttraveled, stopheadsign, stopsequence, stop_id, trip_id) FROM stdin;
\.


--
-- Data for Name: stoptype; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY stoptype (id, description, interpolated, majorstop, stoptype) FROM stdin;
\.


--
-- Data for Name: trip; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY trip (id, blockid, endtime, gtfstripid, headway, starttime, tripdescription, tripdirection, tripheadsign, tripshortname, usefrequency, pattern_id, route_id, servicecalendar_id, servicecalendardate_id, shape_id) FROM stdin;
\.


--
-- Data for Name: trippattern; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY trippattern (id, encodedshape, endtime, headsign, headway, longest, name, saturday, starttime, sunday, usefrequency, weekday, route_id, shape_id) FROM stdin;
\.


--
-- Data for Name: trippattern_trippatternstop; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY trippattern_trippatternstop (trippattern_id, patternstops_id) FROM stdin;
\.


--
-- Data for Name: trippatternstop; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY trippatternstop (id, defaultdistance, defaultdwelltime, defaulttraveltime, stopsequence, pattern_id, stop_id) FROM stdin;
\.


--
-- Data for Name: tripshape; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tripshape (id, describeddistance, description, gtfsshapeid, shape, simpleshape, agency_id) FROM stdin;
\.


--
-- Name: account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


--
-- Name: agency_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agency
    ADD CONSTRAINT agency_pkey PRIMARY KEY (id);


--
-- Name: geometry_columns_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY geometry_columns
    ADD CONSTRAINT geometry_columns_pk PRIMARY KEY (f_table_catalog, f_table_schema, f_table_name, f_geometry_column);


--
-- Name: gisexport_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gisexport
    ADD CONSTRAINT gisexport_pkey PRIMARY KEY (id);


--
-- Name: gisroute_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gisroute
    ADD CONSTRAINT gisroute_pkey PRIMARY KEY (id);


--
-- Name: gisroutealignment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gisroutealignment
    ADD CONSTRAINT gisroutealignment_pkey PRIMARY KEY (id);


--
-- Name: gisroutecontrolpoint_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gisroutecontrolpoint
    ADD CONSTRAINT gisroutecontrolpoint_pkey PRIMARY KEY (id);


--
-- Name: gisroutecontrolpointsequence_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gisroutecontrolpointsequence
    ADD CONSTRAINT gisroutecontrolpointsequence_pkey PRIMARY KEY (id);


--
-- Name: gisroutesegment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gisroutesegment
    ADD CONSTRAINT gisroutesegment_pkey PRIMARY KEY (id);


--
-- Name: gisstop_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gisstop
    ADD CONSTRAINT gisstop_pkey PRIMARY KEY (id);


--
-- Name: gisupload_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gisupload
    ADD CONSTRAINT gisupload_pkey PRIMARY KEY (id);


--
-- Name: gisuploadfield_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gisuploadfield
    ADD CONSTRAINT gisuploadfield_pkey PRIMARY KEY (id);


--
-- Name: gtfssnapshot_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gtfssnapshot
    ADD CONSTRAINT gtfssnapshot_pkey PRIMARY KEY (id);


--
-- Name: gtfssnapshotexport_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gtfssnapshotexport
    ADD CONSTRAINT gtfssnapshotexport_pkey PRIMARY KEY (id);


--
-- Name: gtfssnapshotmerge_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gtfssnapshotmerge
    ADD CONSTRAINT gtfssnapshotmerge_pkey PRIMARY KEY (id);


--
-- Name: gtfssnapshotmergetask_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gtfssnapshotmergetask
    ADD CONSTRAINT gtfssnapshotmergetask_pkey PRIMARY KEY (id);


--
-- Name: gtfssnapshotvalidation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gtfssnapshotvalidation
    ADD CONSTRAINT gtfssnapshotvalidation_pkey PRIMARY KEY (id);


--
-- Name: route_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY route
    ADD CONSTRAINT route_pkey PRIMARY KEY (id);


--
-- Name: routetype_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY routetype
    ADD CONSTRAINT routetype_pkey PRIMARY KEY (id);


--
-- Name: servicecalendar_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY servicecalendar
    ADD CONSTRAINT servicecalendar_pkey PRIMARY KEY (id);


--
-- Name: servicecalendardate_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY servicecalendardate
    ADD CONSTRAINT servicecalendardate_pkey PRIMARY KEY (id);


--
-- Name: spatial_ref_sys_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY spatial_ref_sys
    ADD CONSTRAINT spatial_ref_sys_pkey PRIMARY KEY (srid);


--
-- Name: stop_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stop
    ADD CONSTRAINT stop_pkey PRIMARY KEY (id);


--
-- Name: stoptime_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stoptime
    ADD CONSTRAINT stoptime_pkey PRIMARY KEY (id);


--
-- Name: stoptype_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stoptype
    ADD CONSTRAINT stoptype_pkey PRIMARY KEY (id);


--
-- Name: trip_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY trip
    ADD CONSTRAINT trip_pkey PRIMARY KEY (id);


--
-- Name: trippattern_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY trippattern
    ADD CONSTRAINT trippattern_pkey PRIMARY KEY (id);


--
-- Name: trippattern_trippatternstop_patternstops_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY trippattern_trippatternstop
    ADD CONSTRAINT trippattern_trippatternstop_patternstops_id_key UNIQUE (patternstops_id);


--
-- Name: trippatternstop_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY trippatternstop
    ADD CONSTRAINT trippatternstop_pkey PRIMARY KEY (id);


--
-- Name: tripshape_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tripshape
    ADD CONSTRAINT tripshape_pkey PRIMARY KEY (id);


--
-- Name: fk152bc912d46c7c39; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisupload
    ADD CONSTRAINT fk152bc912d46c7c39 FOREIGN KEY (agency_id) REFERENCES agency(id);


--
-- Name: fk1a65b38b604a2ff6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trippattern
    ADD CONSTRAINT fk1a65b38b604a2ff6 FOREIGN KEY (shape_id) REFERENCES tripshape(id);


--
-- Name: fk1a65b38bea648c9b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trippattern
    ADD CONSTRAINT fk1a65b38bea648c9b FOREIGN KEY (route_id) REFERENCES route(id);


--
-- Name: fk1d0c220dd46c7c39; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY account
    ADD CONSTRAINT fk1d0c220dd46c7c39 FOREIGN KEY (agency_id) REFERENCES agency(id);


--
-- Name: fk1fcf8ff74c5206c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gtfssnapshotmergetask
    ADD CONSTRAINT fk1fcf8ff74c5206c FOREIGN KEY (merge_id) REFERENCES gtfssnapshotmerge(id);


--
-- Name: fk277c22d46c7c39; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stop
    ADD CONSTRAINT fk277c22d46c7c39 FOREIGN KEY (agency_id) REFERENCES agency(id);


--
-- Name: fk27e845434c4b3b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trip
    ADD CONSTRAINT fk27e845434c4b3b FOREIGN KEY (servicecalendar_id) REFERENCES servicecalendar(id);


--
-- Name: fk27e845604a2ff6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trip
    ADD CONSTRAINT fk27e845604a2ff6 FOREIGN KEY (shape_id) REFERENCES tripshape(id);


--
-- Name: fk27e8456c88ee96; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trip
    ADD CONSTRAINT fk27e8456c88ee96 FOREIGN KEY (pattern_id) REFERENCES trippattern(id);


--
-- Name: fk27e845dd77d01b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trip
    ADD CONSTRAINT fk27e845dd77d01b FOREIGN KEY (servicecalendardate_id) REFERENCES servicecalendardate(id);


--
-- Name: fk27e845ea648c9b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trip
    ADD CONSTRAINT fk27e845ea648c9b FOREIGN KEY (route_id) REFERENCES route(id);


--
-- Name: fk34c58f3d46c7c39; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY servicecalendar
    ADD CONSTRAINT fk34c58f3d46c7c39 FOREIGN KEY (agency_id) REFERENCES agency(id);


--
-- Name: fk3a1f0579474c387b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trippattern_trippatternstop
    ADD CONSTRAINT fk3a1f0579474c387b FOREIGN KEY (trippattern_id) REFERENCES trippattern(id);


--
-- Name: fk3a1f0579992b8387; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trippattern_trippatternstop
    ADD CONSTRAINT fk3a1f0579992b8387 FOREIGN KEY (patternstops_id) REFERENCES trippatternstop(id);


--
-- Name: fk4222722d4cea3ad9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trippatternstop
    ADD CONSTRAINT fk4222722d4cea3ad9 FOREIGN KEY (stop_id) REFERENCES stop(id);


--
-- Name: fk4222722d6c88ee96; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trippatternstop
    ADD CONSTRAINT fk4222722d6c88ee96 FOREIGN KEY (pattern_id) REFERENCES trippattern(id);


--
-- Name: fk42f8abab140cc93b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisroutealignment
    ADD CONSTRAINT fk42f8abab140cc93b FOREIGN KEY (gisroute_id) REFERENCES gisroute(id);


--
-- Name: fk448fedf3e9f741b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisexport_agency
    ADD CONSTRAINT fk448fedf3e9f741b FOREIGN KEY (agencies_id) REFERENCES agency(id);


--
-- Name: fk448fedf67040139; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisexport_agency
    ADD CONSTRAINT fk448fedf67040139 FOREIGN KEY (gisexport_id) REFERENCES gisexport(id);


--
-- Name: fk4ad6d118b28cc219; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisroute
    ADD CONSTRAINT fk4ad6d118b28cc219 FOREIGN KEY (gisupload_id) REFERENCES gisupload(id);


--
-- Name: fk4ad6d118d46c7c39; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisroute
    ADD CONSTRAINT fk4ad6d118d46c7c39 FOREIGN KEY (agency_id) REFERENCES agency(id);


--
-- Name: fk4b7c229140cc93b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY route
    ADD CONSTRAINT fk4b7c229140cc93b FOREIGN KEY (gisroute_id) REFERENCES gisroute(id);


--
-- Name: fk4b7c22948f7a5fb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY route
    ADD CONSTRAINT fk4b7c22948f7a5fb FOREIGN KEY (routetype_id) REFERENCES routetype(id);


--
-- Name: fk4b7c229b28cc219; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY route
    ADD CONSTRAINT fk4b7c229b28cc219 FOREIGN KEY (gisupload_id) REFERENCES gisupload(id);


--
-- Name: fk4b7c229d46c7c39; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY route
    ADD CONSTRAINT fk4b7c229d46c7c39 FOREIGN KEY (agency_id) REFERENCES agency(id);


--
-- Name: fk4f470c5ac9819064; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gtfssnapshotmerge
    ADD CONSTRAINT fk4f470c5ac9819064 FOREIGN KEY (snapshot_id) REFERENCES gtfssnapshot(id);


--
-- Name: fk5f09da5cd46c7c39; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tripshape
    ADD CONSTRAINT fk5f09da5cd46c7c39 FOREIGN KEY (agency_id) REFERENCES agency(id);


--
-- Name: fk62e10b415aede510; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY servicecalendardate
    ADD CONSTRAINT fk62e10b415aede510 FOREIGN KEY (calendar_id) REFERENCES servicecalendar(id);


--
-- Name: fk64057afb6d144a11; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisroutesegment
    ADD CONSTRAINT fk64057afb6d144a11 FOREIGN KEY (topoint_id) REFERENCES gisroutecontrolpoint(id);


--
-- Name: fk64057afbddbbb440; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisroutesegment
    ADD CONSTRAINT fk64057afbddbbb440 FOREIGN KEY (frompoint_id) REFERENCES gisroutecontrolpoint(id);


--
-- Name: fk65835353b28cc219; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisstop
    ADD CONSTRAINT fk65835353b28cc219 FOREIGN KEY (gisupload_id) REFERENCES gisupload(id);


--
-- Name: fk65835353d46c7c39; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisstop
    ADD CONSTRAINT fk65835353d46c7c39 FOREIGN KEY (agency_id) REFERENCES agency(id);


--
-- Name: fk6a10620f4cea3ad9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stoptime
    ADD CONSTRAINT fk6a10620f4cea3ad9 FOREIGN KEY (stop_id) REFERENCES stop(id);


--
-- Name: fk6a10620f7e12a3f9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stoptime
    ADD CONSTRAINT fk6a10620f7e12a3f9 FOREIGN KEY (trip_id) REFERENCES trip(id);


--
-- Name: fk74c60825373f22bc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY agency
    ADD CONSTRAINT fk74c60825373f22bc FOREIGN KEY (defaultroutetype_id) REFERENCES routetype(id);


--
-- Name: fk7cb5a597c9819064; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gtfssnapshotvalidation
    ADD CONSTRAINT fk7cb5a597c9819064 FOREIGN KEY (snapshot_id) REFERENCES gtfssnapshot(id);


--
-- Name: fk80c9544ca1055f01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisroutecontrolpointsequence
    ADD CONSTRAINT fk80c9544ca1055f01 FOREIGN KEY (segment_id) REFERENCES gisroutesegment(id);


--
-- Name: fk80c9544ca773ac59; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisroutecontrolpointsequence
    ADD CONSTRAINT fk80c9544ca773ac59 FOREIGN KEY (gisroutealignment_id) REFERENCES gisroutealignment(id);


--
-- Name: fk80c9544ce07f9ef3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisroutecontrolpointsequence
    ADD CONSTRAINT fk80c9544ce07f9ef3 FOREIGN KEY (controlpoint_id) REFERENCES gisroutecontrolpoint(id);


--
-- Name: fk8e8cc308b28cc219; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisuploadfield
    ADD CONSTRAINT fk8e8cc308b28cc219 FOREIGN KEY (gisupload_id) REFERENCES gisupload(id);


--
-- Name: fkb3db45323e9f741b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gtfssnapshotexport_agency
    ADD CONSTRAINT fkb3db45323e9f741b FOREIGN KEY (agencies_id) REFERENCES agency(id);


--
-- Name: fkb3db4532a15bb94a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gtfssnapshotexport_agency
    ADD CONSTRAINT fkb3db4532a15bb94a FOREIGN KEY (gtfssnapshotexport_id) REFERENCES gtfssnapshotexport(id);


--
-- Name: fkb6a7136b140cc93b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gisroutecontrolpoint
    ADD CONSTRAINT fkb6a7136b140cc93b FOREIGN KEY (gisroute_id) REFERENCES gisroute(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

