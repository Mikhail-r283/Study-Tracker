CREATE TABLE tags (
                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      name VARCHAR(255) NOT NULL UNIQUE,
                      icon_url VARCHAR(500),
                      description TEXT
);

-- Создаём таблицу связи lesson_tags
CREATE TABLE lesson_tags (
                             lesson_id BIGINT NOT NULL,
                             tag_id BIGINT NOT NULL,
                             PRIMARY KEY (lessonid, tagid),
                             CONSTRAINT fklessontagslesson FOREIGN KEY (lessonid)
                                 REFERENCES lessons(id) ON DELETE CASCADE,
                             CONSTRAINT fklessontagstag FOREIGN KEY (tagid)
                                 REFERENCES tags(id) ON DELETE CASCADE
);
-- Добавляем базовые теги
INSERT INTO tags (name, icon_url, description) VALUES
                                                   ('Математика', 'https://img.icons8.com/color/48/math.png', 'Математические дисциплины'),
                                                   ('Физика', 'https://img.icons8.com/color/48/physics.png', 'Физика и естественные науки'),
                                                   ('Программирование', 'https://img.icons8.com/color/48/code.png', 'Программирование и разработка'),
                                                   ('Английский', 'https://img.icons8.com/color/48/english.png', 'Английский язык'),
                                                   ('История', 'https://img.icons8.com/color/48/history.png', 'История и обществознание'),
                                                   ('Дизайн', 'https://img.icons8.com/color/48/design.png', 'Дизайн и графика'),
                                                   ('Музыка', 'https://img.icons8.com/color/48/music.png', 'Музыкальное образование'),
                                                   ('Спорт', 'https://img.icons8.com/color/48/sport.png', 'Спорт и физическая культура');