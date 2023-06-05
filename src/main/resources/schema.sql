CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  varchar(100) NOT NULL,
    email varchar(320),
    CONSTRAINT UQ_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_request
(
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description   varchar(200),
    requestor_id  BIGINT REFERENCES users (id) ON DELETE CASCADE,
    creation_date timestamp
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        varchar(100) NOT NULL,
    description varchar(200),
    available   bool,
    owner_id    BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    request_id  BIGINT REFERENCES item_request (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date timestamp,
    end_date   timestamp,
    booker_id  BIGINT REFERENCES users (id) ON DELETE CASCADE,
    item_id    BIGINT REFERENCES items (id) ON DELETE CASCADE,
    status     varchar(30)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text      varchar(300) NOT NULL,
    item_id   BIGINT REFERENCES items (id) ON DELETE CASCADE,
    author_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created   timestamp
);

