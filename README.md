

# ArenaOfGlory

**Настройки плагина**
Настройки плагина представлены в виде конфигурационных файлов и таблиц БД. Содержимое файлов представлены в формате JSON. Файлы создаются (если не существуют) при включении плагина в директории /*местонахождение jar-файла*/ArenaOfGlory/. В качестве БД используется sqlite и она НЕ создается автоматически. Пустую БД необходимо создать вручную и вписать настройки в нужный конфиг файл.

**Конфигурационные файлы**
Есть два файла: 
 - db_config.txt 
 - settings.txt

*Параметры в файлах могут быть в любом порядке*

*db_config.txt*
При первом запуске плагина, файл создается со следующей структурой:
{
  "url": "",
  "driver": "",
  "user": "",
  "password": ""
}
url - путь к файлу с БД
driver - драйвер для БД
user, password - пользователь и пароль, это необязательно

url заполняется так: jdbc:sqlite:<путь к файлу с БД>
Например, "jdbc:sqlite:D:/Minecraft/Arenaofglory/arena.db"
driver просто заполняем "org.sqlite.JDBC"
user, password - по необходимости

в итоге, получаем примерно такое содержимое:
{
  "url": "jdbc:sqlite:D:/Minecraft/Arenaofglory/arena.db",
  "driver": "org.sqlite.JDBC",
  "user": "",
  "password": ""
}

*settings.txt*
Структура файла выглядит след. образом:
{
  "open_waiting_room": "18:50",
  "start_arena_match": "19:00"
}
Настройки представлены в формате HH:mm, **в другом формате читаться не будут**. Настройки по умолчанию - 18:50 и 19:00 (как в примере)
open_waiting_room - время открытия комнаты ожидания
start_arena_match - время начала матча
**Важно: время серверное и зависит от настроек сервера. Когда плагин запускается, он пишет текущую серверную дату и время: "Серверное время: <дата и время>".** 

**База данных**
Таблицы:

 - fractions
 - arena_players
 - waiting_room_coordinates
 - reward
 - arena_respawn_coordinates
 
 ***fractions***
 Таблица со всеми фракциями.
 Колонки:
 - id - уникальный id фракции
 - name - имя фракции (для отображения)
 - name_in_english - имя фракции на английском для консольных команд
 - motto - девиз фракции
 По умолчанию таблица заполняется данными:
 id=1, name=Капелла, name_in_english=kapella, motto=Несущие свет
 id=2, name=Процион, name_in_english=procion, motto=Несущие бурю

| id | name | name_in_english | motto|
|--|--|--|--|
| 1 | Капелла| kapella | Несущие свет |
| 2 | Процион | procion | Несущие бурю |
 
***arena_players***
Таблица со всеми игроками, которые присодинились к какой-либо фракции
Колонки:
 - id - uuid игрока
 - fraction_id - id фракции из таблицы fractions
 - name - имя игрока
По умолчанию таблица пуста

***waiting_room_coordinates***
Таблица с координатами комнаты ожидания. **Координаты должны быть целыми**
Колонки:
 - id - строка top_left_corner или bottom_right_corner, в зависимости от
   того, координаты какого угла вводишь 
 - x - координата X     
 - y - координата Y     
 - z - координата Z
По умолчанию таблица заполняется след. значениями: 

| id | x | y | z |
|--|--|--|--|
| top_left_corner | 0 | 0 | 0 |
| bottom_right_corner | 10 | 10 | 10 |


Какой угол считать левым верхним, а какой правым нижним - неважно. **Главное - они должны быть по диагонали**

***reward***
Таблица с кол-вом жетонов, которые выдаются каждому игроку при победе, проигрыше и ничье. Жетоны выдаются только в том случае, если игрок набрал как минимум 5 убийств.
Колонки:
type - наименовение типа награды. Три варианта: victory (победа), loss (поражение), draw (ничья)
tokens_amount - кол-во токенов для определенного типа
Значения по умолчанию:

| type | tokens_amount |
|--|--|
| victory | 2 |
| loss | 1 |
| draw | 1 |

***arena_respawn_coordinates***
Таблица с координатами спавнов участников арены
Колонки:
fraction_id - id фракции для которой принадлежит данный респ
top_left_corner_x - координата Х левого верхнего угла
top_left_corner_y - координата У левого верхнего угла
top_left_corner_z - координата Z левого верхнего угла
bottom_right_corner_x - координата X нижнего правого угла
bottom_right_corner_y - координата У нижнего правого угла
bottom_right_corner_z - координата Z нижнего правого угла
Значения по умолчанию:
| fraction_id | top_left_corner_x | top_left_corner_y | top_left_corner_z | bottom_right_corner_x | bottom_right_corner_y | bottom_right_corner_z |
|--|--|--|--|--|--|--|
| 1 | 0 | 0 | 0 | 20 | 20 | 20 |
| 2 |  0 | 0 | 0 | 20 | 20 | 20 |

**Инструкция по установке**
Закинуть в папку plugins, запустить. При первом запуске плагин вылетит из-за отсуствия настроек БД, но при этом он создаст файл db_config.txt. Создать пустую sqlite БД, прописать нужные настройки в конфиг файле. Запустить лпагин. Плагин создаст все таблицы, заплнит их данными по умолчанию, так же создаст еще один конфиг файл - settings.txt. После этого можно выключать плагин и настраивать. **Настройки применяются только после перезапуска плагина**
