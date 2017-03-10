DROP TABLE devices;
DROP TABLE users;

BEGIN TRANSACTION;
CREATE TABLE users
(
  user_name VARCHAR(32),
  password  VARCHAR(32) NOT NULL,
  PRIMARY KEY (user_name)
);

CREATE TABLE devices
(
  device_id            INTEGER,
  password             VARCHAR(32) NOT NULL,
  hardware_description TEXT,
  PRIMARY KEY (device_id)
);
COMMIT;