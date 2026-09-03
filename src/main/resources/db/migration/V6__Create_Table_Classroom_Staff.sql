CREATE TABLE classroom_staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    id_classroom BIGINT NOT NULL,
    id_employee BIGINT NOT NULL,
    CONSTRAINT fk_classroom_staff_classroom FOREIGN KEY (id_classroom) REFERENCES classroom(id),
    CONSTRAINT fk_classroom_staff_employee FOREIGN KEY (id_employee) REFERENCES employee(id)
);