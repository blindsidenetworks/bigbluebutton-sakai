ALTER TABLE BBB_MEETING ADD COLUMN RECORDING CHAR(1) AFTER END_DATE;
ALTER TABLE BBB_MEETING ADD COLUMN RECORDING_DURATION PLS_INTEGER AFTER RECORDING;