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