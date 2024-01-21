


# [Name des Prozesses]

![](prozessdiagramm.png)

## Kurzbeschreibung
Der Versicherungsnehmerin macht sein Antrag auf der Onlineportal des Versicherungsunternehmens, das Versicherungsunternehmen prüft anschließend die Materialien und pro Partnersystem prüft, ob er ein neuer Kunde ist. Abschließend wird ein Selbstbehalt ermitteln , eine Bestätigungs-E-Mail an den Kunden gesendet und die Der Vertrag wird ausgedruckt und versendet.


## Technischer Name

Der eindeutige Bezeichner des Prozesses lautet wie folgt:

`id_der_prozessdefinition_wie_im_camunda_modeler_angegeben`

## Organisatorischer Kontext

### Prozessziele

Ziel dieses Prozesses ist es, den Abschluss einer Reisekrankenversicherung zu automatisieren. Damit Kunden Online-Einkäufe tätigen und modernisieren können.


### Stakeholder

| Personengruppe      | Details |
| ------------------- | ------- |
| Prozesseigner:innen |  Versicherungsunternehmen    |
| Prozessbeteiligte   |    Partnersystem     |
| Kund:innen          |  Versicherungsnehmerin   |

### Anwendungssysteme

| System | Details |
| ------ | ------- |
|        |         |
|        |         |
|        |         |

## Prozessbeginn

### Start / Auslöser

| Startbedingung | Details |
| -------------- | ------- |
|   Der Antrag der Versicherungsnehmerin   |    auf der Onlineportal eingegebene Informationen     |

### Input

| Eingabe | Details |
|---------|---------|
| Address | String  |
| Geburtstag |  Date  |
| Versicherungsnummer|  Nummer |
|    Reiseziel   |    String    |

## Prozessschritte

### Prozessschritt 1



### Prozessschritt 2



## Prozessende

### Ende

| Endbedingung | Details |
| ------------ | ------- |
| Wenn die Informationsüberprüfung erfolgreich ist   |     Vertragsunterlage drucken und senden    |
|   Wenn die Überprüfung der Informationen fehlschlägt oder fehlt     |    Von der Versicherungsunternehmen abgelehnt     |

### Ergebnis / Output

| Geschäftsobjekt | Zielsystem | Verantwortlich |
| --------------- | ---------- | -------------- |
|  Versicherungsvertrag  |      Vertragsystem,Partnersystem,E-Mail-Versandsystem      |                |
|                 |            |                |
|                 |            |                |

## Prozesskontext

Folgende Variablen werden während der Ausführung im Prozesskontext abgelegt:

| Variablenname | Typ  | Datentyp | Details |
| ------------- | ---- | -------- | ------- |
|               |      |          |         |
|               |      |          |         |
|               |      |          |         |
