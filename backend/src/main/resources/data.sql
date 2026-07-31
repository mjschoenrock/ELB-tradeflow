-- ============================================================================
-- data.sql — Spring Boot SQL initializer (dev profile)
-- ============================================================================
-- Dev seed only. spring.sql.init.mode=embedded restricts this script to H2.
-- Liquibase owns schema + production seed data.
-- ============================================================================

INSERT INTO counterparties (name, lei_code, region) VALUES
	('Deutsche Bank AG',        '7LTWFZYICNSX8D621K86', 'EMEA'),
	('Goldman Sachs Group Inc', '784F5XWPLTWKTBV3E584', 'NAMR'),
	('Nomura Holdings Inc',     '6N69WMNCQOWKSDLVDX42', 'APAC');

INSERT INTO instruments (symbol, name, asset_class, currency, isin) VALUES
	('SAP3.DE', 'SAP SE',                'EQUITY',       'EUR', 'DE0007164600'),
	('NVDA',    'NVIDIA Corp',           'EQUITY',       'USD', 'US67066G1040'),
	('EURUSD',  'EUR/USD spot',          'FX',           'USD',  NULL),
	('BUND10Y', 'German 10Y Bund',       'FIXED_INCOME', 'EUR', 'DE0001102606'),
	('XAU',     'Gold spot',             'COMMODITY',    'USD',  NULL);

INSERT INTO trades (trade_ref, instrument_id, counterparty_id, quantity, price, trade_date, status, created_at) VALUES
	('TR-001', 1, 1,     500.0000,  120.5000, CURRENT_DATE - 5, 'MATCHED',   CURRENT_TIMESTAMP),
	('TR-002', 2, 2,     100.0000,  890.2500, CURRENT_DATE - 4, 'PENDING',   CURRENT_TIMESTAMP),
	('TR-003', 3, 1, 1000000.0000,    1.0850, CURRENT_DATE - 3, 'MATCHED',   CURRENT_TIMESTAMP),
	('TR-004', 4, 3,       5.0000,   99.4000, CURRENT_DATE - 2, 'UNMATCHED', CURRENT_TIMESTAMP),
	('TR-005', 5, 2,     100.0000, 2150.0000, CURRENT_DATE - 2, 'DISPUTED',  CURRENT_TIMESTAMP),
	('TR-006', 1, 3,     200.0000,  121.0000, CURRENT_DATE - 1, 'PENDING',   CURRENT_TIMESTAMP),
	('TR-007', 2, 1,      50.0000,  895.0000, CURRENT_DATE - 1, 'MATCHED',   CURRENT_TIMESTAMP),
	('TR-008', 3, 2,  500000.0000,    1.0855, CURRENT_DATE,     'PENDING',   CURRENT_TIMESTAMP),
	('TR-009', 4, 1,      10.0000,   99.5500, CURRENT_DATE,     'MATCHED',   CURRENT_TIMESTAMP),
	('TR-010', 5, 3,      50.0000, 2148.7500, CURRENT_DATE,     'UNMATCHED', CURRENT_TIMESTAMP);
