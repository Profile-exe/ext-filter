-- 초기 고정 확장자 7개 삽입
-- bat, cmd, com, cpl, exe, scr, js

INSERT INTO fixed_extensions (extension_name, is_blocked) VALUES
    ('bat', true),
    ('cmd', true),
    ('com', true),
    ('cpl', true),
    ('exe', true),
    ('scr', true),
    ('js', true);