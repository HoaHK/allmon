-- allmon schema (by Tomasz Sikora)

-- TODO :
-- * add hierarchical dimensions (for snowflake schemas - not needed for star schemas)
-- * add multi-key dimensions
-- * add data type column to dimension and measures ??

-- ** add procedures which checking data integrity in values tables "cube" (the same rows 
--    in all dimensions values and measures!!) 
--      # whole fact row is not valid if any of dimensions or measures does not exist
--      # 

-------------------------------------------------------------------------------------------------------------------------
-- dropping schema
DROP SEQUENCE fc_dim_seq;
DROP SEQUENCE fc_div_seq;
DROP SEQUENCE fc_msr_seq;
DROP SEQUENCE fc_vld_seq;
DROP SEQUENCE fc_vlm_seq;
DROP SEQUENCE fc_fac_seq;

DROP TABLE fc_factdim;
DROP TABLE fc_factmsr;
DROP TABLE fc_fact;

DROP TABLE fc_valuesmsr;
DROP TABLE fc_measures;

DROP TABLE fc_valuesdim;
DROP TABLE fc_dimvalues;
DROP TABLE fc_dimensions;

DROP TABLE pivot;

-------------------------------------------------------------------------------------------------------------------------
-- creating schema
-- -- -- multidimensional schema metadata
CREATE TABLE fc_dimensions (
  fc_dim_id NUMBER(10) NOT NULL,
  dimname VARCHAR(20) NOT NULL,
  code VARCHAR(8) NOT NULL,
  dimdesc VARCHAR(512),
  CONSTRAINT fc_dimensions__pk PRIMARY KEY (fc_dim_id) USING INDEX
);
CREATE SEQUENCE fc_dim_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
CREATE UNIQUE INDEX fc_dim_uk1 ON fc_dimensions(code);

CREATE TABLE fc_dimvalues (
  fc_div_id NUMBER(10) NOT NULL,
  fc_dim_id NUMBER(10) NOT NULL,
  val VARCHAR(1000) NOT NULL,
  CONSTRAINT fc_dimvalues__pk PRIMARY KEY (fc_div_id) USING INDEX,
  CONSTRAINT fc_div_fc_dim__fk1 FOREIGN KEY (fc_dim_id) REFERENCES fc_dimensions(fc_dim_id)
);
CREATE SEQUENCE fc_div_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
CREATE UNIQUE INDEX fc_div_uk1 ON fc_dimvalues(fc_dim_id, val);
CREATE INDEX fc_div_idx1 ON fc_dimvalues(fc_dim_id); -- REVIEW necessity of this index
CREATE INDEX fc_div_idx2 ON fc_dimvalues(val); -- REVIEW necessity of this index

CREATE TABLE fc_measures (
  fc_msr_id NUMBER(10) NOT NULL,
  msrname VARCHAR(20) NOT NULL,
  code VARCHAR(8) NOT NULL,
  CONSTRAINT fc_measures__pk PRIMARY KEY (fc_msr_id) USING INDEX
);
CREATE SEQUENCE fc_msr_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
CREATE UNIQUE INDEX fc_msr_uk1 ON fc_measures(code);

-- -- -- multidimensional data values (metacube)
CREATE TABLE fc_valuesdim (
  fc_vld_id NUMBER(10) NOT NULL, -- TODO remove - possibly not necessary 
  fc_div_id NUMBER(10) NOT NULL,
  rownumber NUMBER(10) NOT NULL, -- REVIEW potentially fk to separate table 
  CONSTRAINT fc_valuesdim__pk PRIMARY KEY (fc_vld_id) USING INDEX,
  CONSTRAINT fc_vld_fc_div__fk1 FOREIGN KEY (fc_div_id) REFERENCES fc_dimvalues(fc_div_id)
);
CREATE SEQUENCE fc_vld_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
CREATE UNIQUE INDEX fc_vld_uk1 ON fc_valuesdim(fc_div_id, rownumber);
CREATE INDEX fc_vld_idx1 ON fc_valuesdim(fc_div_id); -- REVIEW necessity of this index
CREATE INDEX fc_vld_idx2 ON fc_valuesdim(rownumber); -- REVIEW necessity of this index

CREATE TABLE fc_valuesmsr (
  fc_vlm_id NUMBER(10) NOT NULL, -- TODO remove - possibly not necessary 
  fc_msr_id NUMBER(10) NOT NULL,
  rownumber NUMBER(10) NOT NULL, -- REVIEW potentially fk to separate table 
  val NUMBER(16, 6) NOT NULL,
  CONSTRAINT fc_valuesmsr__pk PRIMARY KEY (fc_vlm_id) USING INDEX,
  CONSTRAINT fc_vlm_fc_vld__fk1 FOREIGN KEY (fc_msr_id) REFERENCES fc_measures(fc_msr_id)
);
CREATE SEQUENCE fc_vlm_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
CREATE UNIQUE INDEX fc_vlm_uk1 ON fc_valuesmsr(fc_msr_id, rownumber);
CREATE INDEX fc_vlm_idx1 ON fc_valuesmsr(fc_msr_id); -- REVIEW necessity of this index
CREATE INDEX fc_vlm_idx2 ON fc_valuesmsr(rownumber); -- REVIEW necessity of this index
CREATE INDEX fc_vlm_idx3 ON fc_valuesmsr(val); -- REVIEW necessity of this index

-- -- -- metaschema 
CREATE TABLE fc_fact (
  fc_fac_id NUMBER(10) NOT NULL,
  factname VARCHAR(20) NOT NULL,
  code VARCHAR(8) NOT NULL,
  factdesc VARCHAR(512),
  CONSTRAINT fc_fact__pk PRIMARY KEY (fc_fac_id) USING INDEX
);
CREATE SEQUENCE fc_fac_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
--CREATE UNIQUE INDEX fc_fac_pk ON fc_fact(fc_fac_id);
CREATE UNIQUE INDEX fc_fac_uk1 ON fc_fact(code);

CREATE TABLE fc_factdim (
  fc_fac_id NUMBER(10) NOT NULL,
  fc_dim_id NUMBER(10) NOT NULL,
  CONSTRAINT fc_fad_fc_fac__fk1 FOREIGN KEY (fc_fac_id) REFERENCES fc_fact(fc_fac_id),
  CONSTRAINT fc_fad_fc_dim__fk2 FOREIGN KEY (fc_dim_id) REFERENCES fc_dimensions(fc_dim_id)
);
CREATE UNIQUE INDEX fc_fad_uk1 ON fc_factdim(fc_fac_id, fc_dim_id);

CREATE TABLE fc_factmsr (
  fc_fac_id NUMBER(10) NOT NULL,
  fc_msr_id NUMBER(10) NOT NULL,
  CONSTRAINT fc_fam_fc_fac__fk1 FOREIGN KEY (fc_fac_id) REFERENCES fc_fact(fc_fac_id),
  CONSTRAINT fc_fam_fc_msr__fk2 FOREIGN KEY (fc_msr_id) REFERENCES fc_measures(fc_msr_id)
);
CREATE UNIQUE INDEX fc_fam_uk1 ON fc_factmsr(fc_fac_id, fc_msr_id);

/*
CREATE TABLE fc_valuesmsr (
  fc_vlm_id NUMBER(10) NOT NULL, -- TODO remove - possibly not necessary 
  fc_msr_id NUMBER(10) NOT NULL,
  rownumber NUMBER(10) NOT NULL, -- REVIEW potentially fk to separate table 
  val NUMBER(16, 6) NOT NULL,
  CONSTRAINT fc_valuesmsr__pk PRIMARY KEY (fc_vlm_id) USING INDEX,
  CONSTRAINT fc_vlm_fc_vld__fk1 FOREIGN KEY (fc_msr_id) REFERENCES fc_measures(fc_msr_id)
);
*/

-------------------------------------------------------------------------------------------------------------------------
-- miscelaneous 
CREATE TABLE pivot AS
WITH ones AS
   (SELECT 0 x FROM dual
    UNION SELECT 1 FROM dual
    UNION SELECT 2 FROM dual
    UNION SELECT 3 FROM dual
    UNION SELECT 4 FROM dual
    UNION SELECT 5 FROM dual
    UNION SELECT 6 FROM dual
    UNION SELECT 7 FROM dual
    UNION SELECT 8 FROM dual
    UNION SELECT 9 FROM dual)
    SELECT 1000000*o1000000.x + 100000*o100000.x + 10000*o10000.x + 1000*o1000.x + 100*o100.x + 10*o10.x + o1.x x
    FROM ones o1, ones o10, ones o100, ones o1000, ones o10000, ones o100000, ones o1000000;

-------------------------------------------------------------------------------------------------------------------------
----- -- -- rebuild indexes      
SELECT 'ALTER INDEX '||ai.index_name||' REBUILD UNRECOVERABLE;'
FROM   all_indexes ai
WHERE  ai.index_name LIKE 'FC_%'
AND    ai.table_owner = 'PR_TTC';

-- statistics
SELECT * FROM user_tables;

SELECT segment_type, segment_name table_name, SUM(bytes) / (1024 * 1024) table_size_meg
FROM   user_extents
WHERE  segment_type IN('TABLE', 'INDEX')
GROUP  BY segment_type, segment_name
ORDER BY 1, 2;


