ALTER TABLE properties
    ADD COLUMN survey_number VARCHAR(120),
    ADD COLUMN area_value NUMERIC(15, 2),
    ADD COLUMN area_unit VARCHAR(20),
    ADD COLUMN area_guntas INTEGER;

ALTER TABLE properties
    ADD CONSTRAINT ck_properties_area_value
        CHECK (area_value IS NULL OR area_value >= 0),
    ADD CONSTRAINT ck_properties_area_unit
        CHECK (area_unit IS NULL OR area_unit IN ('SQ_FT', 'SQ_YD', 'ACRES_GUNTAS')),
    ADD CONSTRAINT ck_properties_area_guntas
        CHECK (area_guntas IS NULL OR area_guntas BETWEEN 0 AND 39),
    ADD CONSTRAINT ck_properties_area_components
        CHECK (
            (area_value IS NULL AND area_unit IS NULL AND area_guntas IS NULL)
            OR (area_value > 0 AND area_unit IN ('SQ_FT', 'SQ_YD') AND area_guntas IS NULL)
            OR (area_value >= 0 AND area_unit = 'ACRES_GUNTAS' AND area_guntas IS NOT NULL AND (area_value > 0 OR area_guntas > 0))
        );

COMMENT ON COLUMN properties.survey_number IS 'Survey or municipal parcel identifier provided for the property.';
COMMENT ON COLUMN properties.area_value IS 'Entered area: square-unit amount, or whole acres when area_unit is ACRES_GUNTAS.';
COMMENT ON COLUMN properties.area_unit IS 'Unit for area_value: SQ_FT, SQ_YD, or ACRES_GUNTAS.';
COMMENT ON COLUMN properties.area_guntas IS 'Guntas component (0–39) when area_unit is ACRES_GUNTAS.';
