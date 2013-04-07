EasyAtlas
=========

Easy and fast create styled maps for your region.

First: import data into pg_snampshot osm db.
--------------------------------------------

    osmosis --read-pbf myhome.pbf --write-pgsql database=mydatabase user=me password=mypassword

Second: run schema-init.sql build_area.sql load-layers.sql
----------------------------------------------------------

    psql -d mydatabase -f schema-init.sql
    psql -d mydatabase -f build_area.sql
    psql -d mydatabase -f load-layers.sql

schema-init.sql contains description of layer tables, load-layers.sql contains scripts to fill those tables. 

It's modification is simple. I think it's better when you train in sql and postgres hstore syntax instead of learn new
specific program config. So there is no configurable layer tables generator.

Third: open qgis (bw.qgs project) there is some styled layers. 
--------------------------------------------------------------

Use them as template, to make you own map style.

Fourth: generate atlas.
-----------------------

    java -jar AtlasGenerator.jar dir_with_templates_path scale_divisor coverage_poly_path
    
It's very very not final version yet. I'll replace some of arguments with config file.

coverage.poly - can be generated with JOSM. 

template dir - must contains map-page.ott file. - It's open document template, with some frames. Only map_frame 
(frame with such name) is required. 

AtlasGenerator.jar will create pages subdir with number of map-page#.odt files, with filled page numbers, 
page-references and page images.

Fithfs: meld all pages in one pdf.
----------------------------------
   
    ./combine.sh atlasname.pdf
   
I'll rewrite this script later. Try to use less of requiered packages. And rewrite hardcoded paths. Now this 
script looks for pages subdir in curent dir, and create atlas in curent dir.
