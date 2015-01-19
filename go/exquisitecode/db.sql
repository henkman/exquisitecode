CREATE TABLE player (
    id       INTEGER      PRIMARY KEY AUTOINCREMENT
                          UNIQUE
                          NOT NULL,
    name     VARCHAR (32) UNIQUE
                          NOT NULL,
    password BLOB (64)    NOT NULL
);

CREATE TABLE task (
    id          INTEGER       PRIMARY KEY AUTOINCREMENT
                              UNIQUE
                              NOT NULL,
    description VARCHAR (512) NOT NULL,
    solution    BLOB (4096)   NOT NULL
);
