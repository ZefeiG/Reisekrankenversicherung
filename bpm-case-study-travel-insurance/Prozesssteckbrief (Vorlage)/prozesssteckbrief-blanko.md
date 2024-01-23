


# [Reisekrankenversicherung]

![](prozessdiagramm.png)

## Kurzbeschreibung
Der Versicherungsnehmerin macht sein Antrag auf der Onlineportal des Versicherungsunternehmens, das Versicherungsunternehmen prüft anschließend die Materialien und pro Partnersystem prüft, ob er ein neuer Kunde ist. Abschließend wird ein Selbstbehalt ermitteln , eine Bestätigungs-E-Mail an den Kunden gesendet und die Der Vertrag wird ausgedruckt und versendet.


## Technischer Name

Der eindeutige Bezeichner des Prozesses lautet wie folgt:

`id_der_prozessdefinition_wie_im_camunda_modeler_angegeben`

## Organisatorischer Kontext

### Prozessziele

Ziel dieses Prozesses ist es, den Abschluss einer Reisekrankenversicherung zu automatisieren. Damit Kunden Online-Einkäufe tätigen können.
Automatisieren Sie die Antragsbearbeitung, um manuelle Eingaben und menschliche Fehler zu reduzieren, was zu einer schnelleren Genehmigung und Ausstellung der Versicherung führt. Und es setzt Personalressourcen frei, damit sich die Mitarbeiter auf höherwertige Aufgaben zu konzentrieren können.


### Stakeholder

| Personengruppe      | Details |
| ------------------- | ------- |
| Prozesseigner:innen |  Das Versicherungsunternehmen ist der Prozesseigner, da es die letzte Verantwortung und Entscheidungsbefugnis über den Prozess hat.   |
| Prozessbeteiligte   |    Interne Mitarbeiter des Unternehmens sowie externe Partner, die Partnersysteme bereitstellen, die bestimmte Aufgaben oder Aktivitäten innerhalb des Geschäftsprozesses übernehmen.    |
| Kund:innen          |  Alle Versicherungsnehmerin, die die Informationen auf der Onlineportal ausgefüllt haben. |

### Anwendungssysteme

| System | Details |
| ------ | ------- |
|    API-Aufruf   |   Verwendet es das HTTP-Protokoll, um eine Anfrage an den Server zu senden, ob eine Reisewarnung vorliegt und ob die IBAN korrekt ist.   |
|    Partnersystem   |      Es automatisiert abgeglichen wird, ob die Kundin bereits vorhanden ist.    |
|    E-Mail-Versandsystem    |     E-Mail-Benachrichtigung an Kunden senden    |
|    Vertragsystem |  Eine Reisekrankenversicherung wird im Vertragssystem gespeichert.  |

## Prozessbeginn

### Start / Auslöser

| Startbedingung | Details |
| -------------- | ------- |
|   Der Antragformular der Versicherungsnehmerin   |   das auf der Onlineportal eingegebene Informationen     |

### Input

| Eingabe | Details |
|---------|---------|
|Vorname|Text field|
| Nachname|Text field|
|Geburtsdatum |Date time|
|E-Mail|Text field|
|Partnernummer|Nummer|
|IBAN|Nummer|
|Straße |Text field|
|Hausnummer |Nummer|
|PLZ|Nummer|
|Herkunft |Text field|
|Reiseziel |Text field|
|Reisebeginn|Date time|
|Reiseende|Date time|
|Gesamtkosten |Nummer|
|Anzahl der Personen |Nummer|
| API-Aufruf |  Das Senden einer Anfrage über das HTTP-Protokoll an einen Server.( travelwarning.api, api-nijas, travel-insurance-api)  |

## Prozessschritte

### Prozessschritt 1



### Prozessschritt 2



## Prozessende

### Ende

| Endbedingung | Details |
| ------------ | ------- |
| Wenn die Informationsüberprüfung erfolgreich ist   |  Bestätigungsmail senden und Vertragsunterlage drucken & senden    |
|   Wenn die Überprüfung der Informationen fehlschlägt oder fehlt     |    Von der Versicherungsunternehmen abgelehnt     |

### Ergebnis / Output

| Geschäftsobjekt | Zielsystem | Verantwortlich |
| --------------- | ---------- | -------------- |
|  Bestätigungsmail | E-Mail-Versandsystem  | SendGird Outbound Connector|
|  Ausgedruckten Versicherungsvertrag  |      Vertragsystem,Drucksystem     |        Output-Managements        |


## Prozesskontext

Folgende Variablen werden während der Ausführung im Prozesskontext abgelegt:

| Variablenname | Typ  | Datentyp | Details |
| ------------- | ---- | -------- | ------- |
|               |      |          |         |
|               |      |          |         |
|               |      |          |         |

## Verknüpfte Dokumente 

### DMN Tabelle 
| DMN's Name |  
|--------|
|  "Selbstbehalt ermitteln" |   
|"Alter,Wohnort,Anzahl prüfen"|   
|"Persönlichen Daten validieren"|  
