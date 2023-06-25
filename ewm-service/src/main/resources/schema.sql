CREATE TABLE IF NOT EXISTS users
(
    id    INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(250),
    email VARCHAR(254),
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_NAME UNIQUE (name, email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50),
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         varchar(2000),
    title              varchar(120),
    description        varchar(7000),
    event_date         timestamp,
    lat                float,
    lon                float,
    participant_limit  integer,
    paid               boolean,
    request_moderation boolean,
    created_on         timestamp,
    published_on       timestamp,
    initiator_id       integer,
    category_id        integer,
    state              varchar(25),

    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_event_user FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_event_category FOREIGN KEY (category_id) REFERENCES categories (id)
);


CREATE TABLE IF NOT EXISTS compilations
(
    id     INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned boolean,
    title  varchar,
    CONSTRAINT pk_compilation PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilations_events
(
    compilation_id INTEGER REFERENCES compilations (id),
    event_id       INTEGER REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    requester_id integer,
    event_id     integer,
    created      TIMESTAMP,
    status       varchar(25),
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_request_user FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT fk_request_event FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created  TIMESTAMP,
    text     VARCHAR(5000),
    user_id  integer,
    event_id integer,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_comment_event FOREIGN KEY (event_id) REFERENCES events (id)
);

