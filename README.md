# Laser Tag Project Repository

## GUI - install guide

Der skal være installeret [Node.js](https://nodejs.org/en/) på computeren. 
cd til GUI directory og skriv ```npm install``` for at installere modulerne.
Kør GUI med kommandoen ```npm start```.

## BackendApp - install guide

Der skal installeres følgende på computeren:

### apache2, MySQL og phpMyAdmin

Rækkefølgen er vigtig for installationen af disse. Ved installation af phpMyAdmin er det vigtigt, at man trykker space, når man bliver spurgt, om `apache2` skal re-konfigureres. Denne _skal_ være markeret. Indtast `password` som password til phpmyadmin, når du bliver spurgt.

```
sudo apt install apache2
sudo apt install mysql-server
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

## MySQL Setup

Efter succesfuld installation, ændr da adgangskoden på `root` brugeren til `password`, hvis det ikke allerede er det:

```
mkdir -p /var/run/mysqld
chown mysql:mysql /var/run/mysqld
sudo service mysql stop
sudo -s
mysql_safe --skip-grant-tables &
mysql -u root
UPDATE mysql.user SET Password=PASSWORD('password') WHERE User='root';
FLUSH PRIVILEGES;
exit;
mysqladmin -u root -p shutdown
sudo service mysql start
```

Åbn [localhost/phpmyadmin](http://localhost/phpmyadmin) i en browser. Opret en ny database, der hedder "lasertag". Vælg databasen "lasertag" i phpMyAdmin og klik "Import". Vælg filen `backend app/kits.sql`, som ligger i dette repository.

## Backend Compilation

cd til root-mappen for `Laser-Tag-Project` git-repository.

Kør følgende:

```
cd backend app/backend
mvn clean compile assembly:single
``` 

Så genereres der en .jar fil i mappen ```target```. Filen køres ved kommandoen ```java -jar filnavn```.