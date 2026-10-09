INSERT INTO pricing_profiles (
    pricing_profile_id, profile_code, profile_name, description, active_flag,
    created_at, updated_at, version
)
VALUES
    (gen_random_uuid(), 'STANDARD', 'Standard Pricing', 'Standard service price calculation profile', FALSE,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (gen_random_uuid(), 'CUSTOMER', 'Customer Pricing', 'Customer price calculation profile before customer discounts', FALSE,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
ON CONFLICT (profile_code) DO NOTHING;

INSERT INTO pricing_profile_parameters (
    pricing_parameter_id, pricing_profile_id, parameter_code, parameter_name,
    parameter_value, parameter_type, active_flag, created_at, updated_at, version
)
SELECT gen_random_uuid(), p.pricing_profile_id, x.parameter_code, x.parameter_name,
       'UNCONFIGURED', x.parameter_type, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM pricing_profiles p
CROSS JOIN (VALUES
    ('TRAVEL_RATE', 'Travel rate per kilometre', 'DECIMAL'),
    ('AGENT_DA', 'Agent daily allowance', 'DECIMAL'),
    ('OVERHEAD_PERCENT', 'Operational overhead percentage', 'DECIMAL'),
    ('PROPERTYPILOT_MARGIN_PERCENT', 'PropertyPilot margin percentage', 'DECIMAL'),
    ('GST_PERCENT', 'GST percentage', 'DECIMAL'),
    ('TRAVEL_DISTANCE_MODE', 'Travel distance calculation mode', 'ENUM'),
    ('ETA_DISCOUNT_PERCENT', 'ETA flexibility discount percentage', 'DECIMAL'),
    ('ETA_DISCOUNT_MIN_DAYS', 'Minimum flexible ETA days for discount', 'INTEGER')
) AS x(parameter_code, parameter_name, parameter_type)
WHERE p.profile_code IN ('STANDARD', 'CUSTOMER')
ON CONFLICT (pricing_profile_id, parameter_code) DO NOTHING;
