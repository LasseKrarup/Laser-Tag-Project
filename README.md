# Laser Tag Project Repository

## GUI - install guide

Der skal være installeret [Node.js](https://nodejs.org/en/) på computeren. 
cd til GUI directory og skriv ```npm install``` for at installere modulerne.
Kør GUI med kommandoen ```npm start```.

## BackendApp - install guide

Der skal installeres Apache Maven på computeren samt JDK.
Inde i backendApp mappen ```Laser-Tag-Project/backend app/backend``` skal kommandoen ```mvn clean compile assembly:single``` køres. Så genereres der en .jar fil i mappen ```target```. Filen køres ved kommandoen ```java -jar filnavn```.

I baggrunden skal der køre en MySql server med en database kaldt ```lasertag```.
