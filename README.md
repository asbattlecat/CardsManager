# Задание

Описание задания, по которому выполнялся этот проект (другими словами ТЗ), находится в файле `TASK.md`

# Система управления банковскими картами. Инструкция запуска

## Создать секреты (или другие)
<code>mkdir -p secrets</code>

<code>echo "abc_abc_abc_abc_abc_abc_abc_abc_" > secrets/jwt_secret.txt</code>

<code>echo "abc_abc_abc_abc_abc_abc_abc_abc_" > secrets/encryption_secret.txt</code>

<code>echo "bankcards_user" > secrets/db_username.txt</code>

<code>echo "bankcards_password" > secrets/db_password.txt</code>

## Билд

<code>docker compose build</code>

## Очистка старых контейнеров
<code>docker compose down -v</code>

## Запуск
<code>docker compose up -d</code>

# Просмотр логов
<code>docker compose logs -f app</code>

<code>docker compose logs -f postgres</code>

## Документация

После запуска контейнеров:

* JSON формат: http://localhost:8080/v3/api-docs

* YAML формат: http://localhost:8080/v3/api-docs.yaml

* Swagger UI: http://localhost:8080/swagger-ui.html