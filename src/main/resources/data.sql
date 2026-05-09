-- Test user: email=thor.odinson@example.com  password=password123
INSERT INTO users (full_name, email, password_hash)
VALUES ('Thor Odinson', 'thor.odinson@example.com',
        'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f');

INSERT INTO availability_slots (provider_name, start_date_time, end_date_time, marked_unavailable, version)
VALUES
    ('Dr. Stark',  '2026-05-09 09:00:00', '2026-05-09 09:30:00', FALSE, 0),
    ('Dr. Stark',  '2026-05-09 10:00:00', '2026-05-09 10:30:00', FALSE, 0),
    ('Dr. Stark',  '2026-05-09 11:00:00', '2026-05-09 11:30:00', FALSE, 0),
    ('Dr. Parker',  '2026-05-09 13:00:00', '2026-05-09 13:30:00', FALSE, 0),
    ('Dr. Parker',  '2026-05-09 14:00:00', '2026-05-09 14:30:00', FALSE, 0),
    ('Dr. Romanova',    '2026-05-10 09:00:00', '2026-05-10 09:30:00', FALSE, 0),
    ('Dr. Romanova',    '2026-05-10 10:00:00', '2026-05-10 10:30:00', FALSE, 0),
    ('Dr. Banner',  '2026-05-10 15:00:00', '2026-05-10 15:30:00', FALSE, 0);