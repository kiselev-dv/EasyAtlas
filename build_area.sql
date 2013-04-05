insert into polygons 
select nextval('polygons_id_seq'), ST_Multi(ST_BuildArea(ST_LineMerge(
	(select ST_Union(way.linestring) from relations as rel 
		join relation_members as memb on rel.id = memb.relation_id
		join ways as way on way.id = memb.member_id
	where rel.id=src_rel.id and memb.member_type = 'W' and (memb.member_role = 'outer' or memb.member_role = 'inner'))
))) as geometry, src_rel.tags as tags
from relations as src_rel;

insert into polygons select nextval('polygons_id_seq'), ST_Multi(ST_MakePolygon(linestring)) as geometry, tags
from ways 
where (not tags ? 'highway' or tags @> hstore(ARRAY['area', 'yes']) ) and ST_IsClosed(linestring) and ST_NumPoints(linestring) > 3 and not EXISTS(
	select member_id from relation_members where relation_id=id and (member_role='outer' or member_role='inner')
);

