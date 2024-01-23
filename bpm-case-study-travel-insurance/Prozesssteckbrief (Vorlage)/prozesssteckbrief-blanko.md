


# [Reisekrankenversicherung]

![](prozessdiagramm.png)

## Kurzbeschreibung
Der Versicherungsnehmerin macht sein Antragsformular auf der Onlineportal des Versicherungsunternehmens. Sobald das Antragsformular im System eingegangen ist, beginnt der Prozess.

Der erste Schritt besteht darin, mit Hilfe des DMN Tabelle "Persönlichen Daten validieren"zu überprüfen, ob die Angaben des Versicherungsnehmerin  den Anforderungen entsprechen， wie z. B. die Bestimmungen zu Reisezeit und Reisekosten. 
Danach folgt das DMN Tabelle  "Alter,Wohnort,Anzahl prüfen", mit dem geprüft wird, ob Alter, Herkunftsort und Anzahl der versicherten Personen den Anforderungen entsprechen.
Bei einem "Misserfolg" oder einem "sonstigen Fehler" im Prozess wird dem Versicherungsnehmer  eine Ablehnungsnachricht übermittelt.

Der zweite Schritt ist die Prüfung der Reisewarnungen. Stellen Sie fest, ob es Rückgabedaten im Json-Format gibt, indem Sie die REST-API aufrufen. Wenn nicht, beenden Sie die Reisewarnung als Task; wenn ja, müssen Sie die Rückgabe an den Versicherungsnehmer senden

...


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
| Kund:innen          |  Alle Versicherungsnehmerin, die die Informationen auf der Onlineportal ausgefüllt haben. Sie haben vom Ergebnis der Prozessausführung profitieren.|

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
| :heavy_check_mark: Gutfall | Wenn alle Informationsüberprüfung erfolgreich ist, senden Bestätigungsmail und drucken & senden Vertragsunterlage         |
| :x: Fehlerfall 1           | Wenn das Audit nicht genehmigt wird, sendet das System eine Ablehnungsmitteilung an den Versicherungsnehmer |
| :x: Fehlerfall 2           | Kurzbeschreibung der Bedingungen bei Eintreten von Fehlerfall 2 |


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
