postgresql.conf
Odkomentowac linije 205 i zmienic jej wartosc na 'logical'

wal_level = logical			# minimal, replica, or logical

Pobranie obecnej wersji pliku -> docker cp postgres:/var/lib/postgresql/data/postgresql.conf ./postgresql.conf

Wypchniecie do kontenera -> 
docker cp ./postgresql.conf postgres:/var/lib/postgresql/data/postgresql.conf

Zrobić restart kontenera -> docker restart postgres 