# syntax=docker/dockerfile:1

# 直接基于已打好的 fat jar 构建运行镜像（jar 由本地/CI 用 ./mvnw package 生成）。
# 构建前请确保 target/love-space-admin-*.jar 已存在。

# ---------- 分层拆解阶段 ----------
# 把 Spring Boot fat jar 拆成分层目录，让运行镜像按层缓存：依赖层不随业务代码改动失效。
FROM eclipse-temurin:25-jre AS layers
WORKDIR /workspace
ARG JAR_FILE=love-space-admin-*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# ---------- 运行阶段 ----------
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# 创建无登录权限的非特权用户。
RUN groupadd --system spring && useradd --system --gid spring spring

# 按 Spring Boot 分层顺序复制：变动越少的层越靠前，最大化镜像层缓存命中。
COPY --from=layers /workspace/extracted/dependencies/ ./
COPY --from=layers /workspace/extracted/spring-boot-loader/ ./
COPY --from=layers /workspace/extracted/snapshot-dependencies/ ./
COPY --from=layers /workspace/extracted/application/ ./

USER spring

# admin 后端默认端口 8080。
EXPOSE 8080
ENV JAVA_OPTS=""

# 使用 Spring Boot loader 的 JarLauncher 启动分层应用。
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
