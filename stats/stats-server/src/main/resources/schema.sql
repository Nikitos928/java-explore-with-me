create table if not exists hits
(
    id        INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app       varchar(250)                             NOT NULL,
    uri       varchar(100)                             NOT NULL,
    ip        varchar(100)                             NOT NULL,
    timestamp TIMESTAMP,
    CONSTRAINT pk_hit PRIMARY KEY (id),
    CREATE INDEX index1 ON hits (app),
    CREATE INDEX index2 ON hits (uri);

);