USE logistics;

-- Optional cleanup. Uncomment if you want a fresh reload.
-- DELETE FROM drivers;
-- DELETE FROM users;

-- Change these counts before running if you want more or less data.
SET @user_count = 50000;
SET @driver_count = 10000;

INSERT INTO users (id, username, email, password, phone_number)
SELECT
    seq.max_id + seq.n AS id,
    CONCAT('user_', seq.n) AS username,
    CONCAT('user_', seq.n, '@example.com') AS email,
    CONCAT('$2a$10$seeded_user_password_', LPAD(seq.n, 6, '0')) AS password,
    CONCAT(
        '9',
        LPAD(MOD(seq.n * 37, 1000000000), 9, '0')
    ) AS phone_number
FROM (
    SELECT
        ones.n
        + tens.n * 10
        + hundreds.n * 100
        + thousands.n * 1000
        + ten_thousands.n * 10000
        + hundred_thousands.n * 100000
        + 1 AS n,
        base.max_id
    FROM
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) ones
    CROSS JOIN
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) tens
    CROSS JOIN
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) hundreds
    CROSS JOIN
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) thousands
    CROSS JOIN
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) ten_thousands
    CROSS JOIN
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) hundred_thousands
    CROSS JOIN
        (SELECT COALESCE(MAX(id), 0) AS max_id FROM users) base
) seq
WHERE seq.n <= @user_count;

INSERT INTO drivers (
    id,
    name,
    license_number,
    vehicle_type,
    vehicle_number,
    available,
    status,
    rating,
    driver_lat,
    driver_lon
)
SELECT
    seq.max_id + seq.n AS id,
    CONCAT('Driver ', seq.n) AS name,
    CONCAT('LIC', LPAD(seq.n, 8, '0')) AS license_number,
    CASE MOD(seq.n, 2)
        WHEN 0 THEN 'standard'
        ELSE 'premium'
    END AS vehicle_type,
    CONCAT(
        'MH',
        LPAD(MOD(seq.n, 50) + 1, 2, '0'),
        CHAR(65 + MOD(seq.n, 26)),
        CHAR(65 + MOD(seq.n + 7, 26)),
        LPAD(seq.n, 4, '0')
    ) AS vehicle_number,
    CASE WHEN MOD(seq.n, 10) IN (0, 1) THEN FALSE ELSE TRUE END AS available,
    CASE
        WHEN MOD(seq.n, 10) IN (0, 1) THEN 'Accepted'
        ELSE 'Available'
    END AS status,
    3 + MOD(seq.n, 3) AS rating,
    12.9000 + (MOD(seq.n, 500) * 0.0015) AS driver_lat,
    77.5000 + (MOD(seq.n, 500) * 0.0015) AS driver_lon
FROM (
    SELECT
        ones.n
        + tens.n * 10
        + hundreds.n * 100
        + thousands.n * 1000
        + ten_thousands.n * 10000
        + hundred_thousands.n * 100000
        + 1 AS n,
        base.max_id
    FROM
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) ones
    CROSS JOIN
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) tens
    CROSS JOIN
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) hundreds
    CROSS JOIN
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) thousands
    CROSS JOIN
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) ten_thousands
    CROSS JOIN
        (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) hundred_thousands
    CROSS JOIN
        (SELECT COALESCE(MAX(id), 0) AS max_id FROM drivers) base
) seq
WHERE seq.n <= @driver_count;

SELECT COUNT(*) AS total_users FROM users;
SELECT COUNT(*) AS total_drivers FROM drivers;
