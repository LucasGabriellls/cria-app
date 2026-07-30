CREATE TABLE `user_role` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(100),
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `user`(id)
);