CREATE TABLE user_role (
    id_user BIGINT NOT NULL,
    id_role BIGINT NOT NULL,
    PRIMARY KEY (id_user, id_role),
    CONSTRAINT fk_user_role_user FOREIGN KEY (id_user) REFERENCES user (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (id_role) REFERENCES roles (id_roles) ON DELETE CASCADE
);