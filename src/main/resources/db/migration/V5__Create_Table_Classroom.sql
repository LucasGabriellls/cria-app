CREATE TABLE classroom (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stage VARCHAR(30) NOT NULL,
    class_letter VARCHAR(10) NOT NULL,
    shift VARCHAR(20) NOT NULL,
    academic_year INT NOT NULL
);