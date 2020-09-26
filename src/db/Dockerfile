#BUILD DB
FROM postgres
COPY db_dump.sql /docker-entrypoint-initdb.d/
ENV POSTGRES_DB postgres
ENV POSTGRES_PASSWORD 1234
