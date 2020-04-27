# Сборка и запуск
```bash
docker-compose build
docker-compose up
```

# Swagger
```
https://localhost:433/swagger-ui.html
```

# Получение токена
Для всех тестовых пользователей пароль совпадает с логином. 

У административного аккаунта логин и пароль: `admin` 
```bash
curl -X POST "https://localhost:433/auth?login=admin&password=admin" --insecure
```
```json
{
  "token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU4Nzk4MTQ5NywiaWF0IjoxNTg3OTc3ODk3fQ.iOZE7D77vjKJhDphGo_HIWjpxU0DP5MSFY7D5CoSjtBUqXDhZMihkBiNHFOowu1WYW-YhqkMhJ0CTkCBfErNAQ"
}
```

Ключ `--insecure` необходим потому, что использован самоподписанный сертификат.

# Выполнение запросов на примере `/list`
В каждом запросе необходимо использовать полученный ранее токен в заголовке `Authorization`

Токен для запроса формируется следующим образом:
`Bearer $token`

```bash
curl -X GET "https://localhost:433/list?page=0&size=5" -H "accept: */*" --insecure \
-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU4Nzk4MTYyMywiaWF0IjoxNTg3OTc4MDIzfQ.dBJlUZArlyrq32-ORDkcJGBHHPruwy_oRthvy2RhldHTUHuMBvd_8OXNCbXYmfPJRiWtwFZOAzobDk-c5gCucA"
```

```json
{
  "data": [
    {
      "name": "Анисимов Захар Протасьевич",
      "position": "Мусорщик",
      "login": "anisimov",
      "born": "1981-11-23T00:00:00.000+0000",
      "role": "USER"
    },
    {
      "name": "Блинов Андрей Оскарович",
      "position": "Архитектор",
      "login": "blinov",
      "born": "1977-09-06T00:00:00.000+0000",
      "role": "USER"
    },
    {
      "name": "Блохин Мечислав Романович",
      "position": "Тестировщик",
      "login": "blohin",
      "born": "1987-04-09T00:00:00.000+0000",
      "role": "USER"
    },
    {
      "name": "Борисов Николай Олегович",
      "position": "Пекарь",
      "login": "borisov",
      "born": "1972-11-17T00:00:00.000+0000",
      "role": "USER"
    },
    {
      "name": "Волкова Августа Иосифовна",
      "position": "Ремонтник",
      "login": "volkova",
      "born": "1992-11-06T00:00:00.000+0000",
      "role": "USER"
    }
  ],
  "meta": {
    "pageSize": 5,
    "page": 0,
    "pages": 9,
    "totalElements": 41,
    "elements": 5
  },
  "links": {
    "current": "https://localhost:433/list?page=0&size=5",
    "first": "https://localhost:433/list?page=0&size=5",
    "last": "https://localhost:433/list?page=8&size=5",
    "next": "https://localhost:433/list?page=1&size=5"
  }
}
```