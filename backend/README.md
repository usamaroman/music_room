# Music Room Backend

## локальный запуск
```bash
docker compose up --build
```

## почистить данные в бд
```bash
docker exec -it postgres psql -U postgres music_room
DELETE FROM users;
```
