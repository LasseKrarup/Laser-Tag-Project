# Laser Tag Project Repository

## GUI - install guide

Der skal være installeret [Node.js](https://nodejs.org/en/) på computeren. 
cd til GUI directory og skriv ```npm install``` for at installere modulerne.
Kør GUI i development mode med kommandoen ```npm start```.

For at builde og package GUI'en, skriv:

```
npm run build-linx
```

For at installere appen, installer da enten `.deb` filen, der bliver oprettet i `dist` mappen, med `sudo apt install ./filename.deb` eller ved at dobbeltklikke på `.deb` filen. Alternativt kan det _unpackaged_ program også køres fra `GUI/dist/linux-unpackaged/gui_lasertag`.

## BackendApp - install guide

Der skal installeres følgende på computeren:

### apache2, MySQL og phpMyAdmin

Rækkefølgen er vigtig for installationen af disse. Ved installation af phpMyAdmin er det vigtigt, at man trykker space, når man bliver spurgt, om `apache2` skal re-konfigureres. Denne _skal_ være markeret. Indtast `password` som password til phpmyadmin, når du bliver spurgt.

```
sudo apt install apache2
```

Hvis der er installeret mysql-server før:
```
sudo apt purge mysql-client-5.7 mysql-client-core-5.7 mysql-common mysql-server mysql-server mysql-server-5.7 mysql-server-core-5.7
```

Derefter:
```
sudo apt install mysql-server
```

Kør så:

```
sudo mysql_secure_installation
```

Svar ja til validate password plugin. Svar 0 til validation policy. Skriv "password" til nyt root password. Skriv password igen. Svar ja til at bekræfte password-styrke. Svar ja til at fjerne anonyme brugere. Svar ja til disallow remote. Svar ja til remove test. Svar ja til reload privilleges.

Kør så:

```
sudo mysql -u root
```

Du bør komme ind i mysql. Indtast da følgende:

```
FLUSH PRIVILEGES;
USE mysql;
UPDATE user SET authentication_string=PASSWORD("password") WHERE User='root';
UPDATE user SET plugin="mysql_native_password" WHERE User='root';
quit
```

Reload mysql service:

```
sudo service mysql restart
```

Nu er password sat til 'password' for root-brugeren.

Installer også phpMyAdmin

```
sudo apt install phpmyadmin
```

### Maven

```
sudo apt install maven
```

### JDK

Hent [JDK .deb filen](https://www.oracle.com/technetwork/java/javase/downloads/jdk12-downloads-5295953.html) nederst fra siden. Vælg "Accept license manager" og klik download.

`cd` til den mappe, hvor .deb filen ligger.

Kør følgende:

```
sudo apt install ./jdk-12.0.1_linux-x64_bin.deb
```

## Klargør database

Åbn [localhost/phpmyadmin](http://localhost/phpmyadmin) i en browser. Opret en ny database, der hedder "lasertag". Vælg databasen "lasertag" i phpMyAdmin og klik "Import". Vælg filen `backend app/kits.sql`, som ligger i dette repository.

## Backend Compilation

cd til root-mappen for `Laser-Tag-Project` git-repository.

Kør følgende:

```
cd backend app/backend
mvn clean compile assembly:single
``` 

Så genereres der en .jar fil i mappen ```target```. Filen køres ved kommandoen ```java -jar filnavn```.