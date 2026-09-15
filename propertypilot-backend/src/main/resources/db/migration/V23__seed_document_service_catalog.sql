INSERT INTO document_service_catalog
(
    document_service_id,
    document_code,
    service_available,
    service_name,
    active
)
VALUES

(gen_random_uuid(),
 'ENCUMBRANCE_CERTIFICATE',
 true,
 'Get Encumbrance Certificate',
 true),

(gen_random_uuid(),
 'PROPERTY_TAX_RECEIPT',
 true,
 'Retrieve Property Tax Receipt',
 true),

(gen_random_uuid(),
 'MUTATION_CERTIFICATE',
 true,
 'Mutation Certificate Assistance',
 true),

(gen_random_uuid(),
 'PATTADAR_PASSBOOK',
 true,
 'Pattadar Passbook Assistance',
 true),

(gen_random_uuid(),
 'PAHANI_ADANGAL',
 true,
 'Pahani Retrieval',
 true),

(gen_random_uuid(),
 'ROR_1B',
 true,
 'ROR 1B Retrieval',
 true),

(gen_random_uuid(),
 'DHARANI_RECORD',
 true,
 'Dharani Record Retrieval',
 true),

(gen_random_uuid(),
 'SURVEY_SKETCH',
 true,
 'Survey Sketch Retrieval',
 true),

(gen_random_uuid(),
 'FMB',
 true,
 'Field Measurement Book Retrieval',
 true),

(gen_random_uuid(),
 'LINK_DOCUMENT',
 true,
 'Link Document Collection',
 true);