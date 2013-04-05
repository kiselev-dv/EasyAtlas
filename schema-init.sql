CREATE TABLE if not exists polygons
(
  id bigserial,  
  geom geometry(Multipolygon, 4326),
  tags hstore,
  CONSTRAINT polygons_pk PRIMARY KEY (id)
);

create index polygon_geometry on polygons using gist(geom);

create table if not exists roads (
  id bigserial,  
  type text,
  name text,
  ref text,
  oneway boolean,
  bridge boolean,
  tunel boolean,
  surface text,
  smoothenes text,
  maxspeed smallint,
  lanes smallint,
  geom geometry(Linestring, 4326),  
  CONSTRAINT roads_pk PRIMARY KEY (id)
);

create index roads_geometry on roads using gist(geom);

create table if not exists buildings (
  id bigserial,  
  type text,
  name text,
  housenumber text,
  street text,
  levels smallint,  
  geom geometry(Polygon, 4326),  
  CONSTRAINT buildings_pk PRIMARY KEY (id)
);

create index bldngs_geometry on buildings using gist(geom);

create table if not exists addr_points (
  nbr text,
  street text,
  angle float,
  building boolean,
  geom geometry(Point, 4326)
);

create index addr_geometry on addr_points using gist(geom);

create table rivers_ways (
  type text,
  name text,
  geom geometry(Linestring, 4326)
);

create index rivers_ways_geometry on rivers_ways using gist(geom);

create table water_polygons (
  type text,
  name text,
  geom geometry(Multipolygon, 4326)
);

create index water_polygons_geometry on water_polygons using gist(geom);

create table railroads (
  type text,  
  geom geometry(Linestring, 4326)
);

create index railroads_geometry on railroads using gist(geom);

create table amenity_polygons (
  type text,
  name text,
  building boolean,
  has_point_nsd boolean,
  geom geometry(Multipolygon, 4326)
);

create index amenity_polygons_geometry on amenity_polygons using gist(geom);

create table poi (
  primary_type text,
  secondary_type text,
  name text,
  phone text,
  site text,
  wh text,
  building boolean,  
  geom geometry(Point, 4326)
);

create index poi_geometry on poi using gist(geom);

create table coastline (
  geom geometry(Linestring, 4326)
);

create index coastline_geometry on coastline using gist(geom);

create table barier_line (
	type text,
	name text,
	geom geometry(Linestring, 4326)
);

 create index barier_line_geometry on barier_line using gist(geom);

 create table barier_point (
	type text,
	name text,
	access text,
	geom geometry(Point, 4326)
 );

create index barier_point_geometry on barier_point using gist(geom);

create table landuse (
  type text,
  name text,
  geom geometry(Multipolygon, 4326)
);

create index landuse_geometry on landuse using gist(geom);


create table leisure (
  type text,
  name text,
  geom geometry(Multipolygon, 4326)
);

create index leisure_geometry on leisure using gist(geom);

