--liquibase formatted sql

-- 初始 admin Manager 植入（幂等）
-- 明文密码 8@y2eoRLyStM*UVU，cost=10
-- 哈希生成命令（离线，密码轮换时按此重算）：
--   htpasswd -bnBC 10 "" '8@y2eoRLyStM*UVU' | tr -d ':\n'
--   或等价的 Spring Security BCryptPasswordEncoder(10).encode("8@y2eoRLyStM*UVU")
-- 本 changelog 是默认 admin Manager 的唯一植入路径；应用代码不再二次写入。

--changeset love-space:002-seed-admin-manager
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM loves_manager WHERE username = 'admin'
--preconditions onFail:MARK_RAN
--comment: 写入默认 admin 账号到 loves_manager（仅当 username='admin' 不存在时）
INSERT INTO loves_manager (id, username, password, nickname, role, enable, created_at, updated_at)
VALUES (
    '019794b6-b400-7000-8000-000000000001'::uuid,
    'admin',
    '$2a$10$/ZJgnCpiw6StS8HFoG/NauuXklHJsYIUfDnWPXATm.F0YwuPZvJM6',
    '管理员',
    'ADMIN',
    true,
    now(),
    now()
);
--rollback DELETE FROM loves_manager WHERE username = 'admin';
