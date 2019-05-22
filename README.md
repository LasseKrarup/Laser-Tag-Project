# Laser Tag Project Repository

## GUI - install guide

Der skal være installeret [Node.js](https://nodejs.org/en/) på computeren. 
cd til GUI directory og skriv ```npm install``` for at installere modulerne.
Kør GUI med kommandoen ```npm start```.

## BackendApp - install guide

Der skal installeres følgende på computeren:

### apache2, MySQL og phpMyAdmin

Rækkefølgen er vigtig for installationen af disse.

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

Hent [JDK .deb filen](https://download.oracle.com/otn-pub/java/jdk/12.0.1+12/69cfe15208a647278a19ef0990eea691/jdk-12.0.1_linux-x64_bin.deb). 

`cd` til den mappe, hvor .deb filen ligger.

Kør følgende:

```
sudo apt install ./jdk-12.0.1_linux-x64_bin.deb
```

## MySQL Setup

Efter succesfuld installation, ændr da adgangskoden på `root` brugeren til `password`, hvis det ikke allerede er det.

Åbn [localhost/phpmyadmin](http://localhost/phpmyadmin) i en browser. Opret en ny database, der hedder "lasertag". Download [kits.sql](backend app/kits.sql). Vælg databasen "lasertag" i phpMyAdmin og klik "Import". Vælg den downloaded `kits.sql` fil.

## Backend Compilation

cd til root-mappen for `Laser-Tag-Project` git-repository.

Kør følgende:

```
cd backend app/backend
mvn clean compile assembly:single
``` 

Så genereres der en .jar fil i mappen ```target```. Filen køres ved kommandoen ```java -jar filnavn```.