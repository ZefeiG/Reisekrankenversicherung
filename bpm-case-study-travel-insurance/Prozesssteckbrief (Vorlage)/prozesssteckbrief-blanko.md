


# [Reisekrankenversicherung]

![](prozessdiagramm.png)

## Kurzbeschreibung
Der Versicherungsnehmerin macht sein Antragsformular auf der Onlineportal des Versicherungsunternehmens. Sobald das Antragsformular im System eingegangen ist, beginnt der Prozess.

Der erste Schritt besteht darin, mit Hilfe des DMN Tabelle "Travel Daten prüfen"zu überprüfen, ob die Angaben des Versicherungsnehmerin  den Anforderungen entsprechen， wie z. B. die Bestimmungen zu Reisezeit und Reisekosten. 
Danach folgt das DMN Tabelle  "Person Daten prüfen", mit dem geprüft wird, ob Alter, Herkunftsort und Anzahl der versicherten Personen den Anforderungen entsprechen.
Bei einem "Misserfolg" oder einem "sonstigen Fehler" im Prozess wird dem Versicherungsnehmer  eine Ablehnungsnachricht übermittelt.

Der zweite Schritt ist die Prüfung der Reisewarnungen. Stellen Sie fest, ob es Rückgabedaten im Json-Format gibt, indem Sie die REST-API aufrufen. Wenn nicht, beenden Sie die Reisewarnung als Task; wenn ja, müssen Sie die Rückgabe an den Versicherungsnehmer senden

...
...
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

## Prozessschritte

### Prozessschritt 1

Name des Task: "Daten Lesen".

Beschreibung: Es dient als 'read-input-data' zum Lesen und Verarbeiten von reisekrankenversicherungsbezogenen allen Eingabedaten. Sie konvertiert die JSON-Daten in ein Java-Objekt vom Typ ‘TravelInsuranceRequest’ zur weiteren Verarbeitung. Treten bei der Konvertierung Probleme auf, wird ein Fehler protokolliert und eine benutzerdefinierte Ausnahme ‘TravelInsuranceProcessException’ ausgelöst.

Ergebnis des Prozessschnitt: Diese Daten werden dann später zur Überprüfung des Prozesses verwendet.

### Prozessschritt 2

"Antragsdaten validieren" ist ein Expanded Sub-Prozess. Es enthält zwei Business Rule-Task "Travel Daten prüfen " und "Person Daten prüfen ".

-Name des Task: Business Rule-Task "Travel Daten prüfen ".

-Beschreibung der Task: Nachdem die Prüfung begonnen hat, beginnt es mit der Task "Travel Daten prüfen", die eine DMN-Entscheidungstabelle mit dem Namen "Travel Daten prüfen" verknüpft. Es gibt drei boolesche Werte aus, indem es jede der drei Tasks "TravelCostChecker(Kosten mehr als 0)","TravelStartChecker(Reisebeginn in der Zukunft)", "TravelEndChecker(Reisebeginn vor dem Reiseende)" aufrufen.  Nachdem die Verarbeitung durch die DMN-Entscheidungstabelle abgeschlossen ist, können wir feststellen, ob die Daten der Prüfung bestehen oder nicht.

-Mögliche Entscheidungen nach Prozesschritt durch Gateways: Wenn die Entscheidungstabelle das Ergebnis "True" liefert, wird der Prozess weiter laufen. Lautet das Ergebnis "Falsch", wird eine Ablehnungsnachricht an den Versicherungsnehmer gesendet, und der Vorgang wird beendet.

~Name des Task: Business Rule-Task "Person Daten prüfen ".

~Beschreibung der Task:Als nächstes folgt die Aufgabe "Person Daten prüfen", die mit einer DMN-Entscheidungstabelle namens "Person Daten prüfen" verknüpft ist. Es gibt zwei boolesche Werte aus, indem es jede der drei Tasks "AgeChecker(größer als 18 Jahre alt)","PlaceOfResidenceChecker (Herkunft in Deutschland)" aufrufen.Und dann die Überprüfung, ob die Zahl der Versicherte Personen weniger als 7 beträgt.In der Entscheidungstabelle wird schließlich angegeben, ob der Prüfung  bestanden wurde oder nicht.

~Mögliche Entscheidungen nach Prozesschritt durch Gateways: Wenn die Entscheidungstabelle das Ergebnis "True" liefert, wird der Prozess weiter laufen. Lautet das Ergebnis "Falsch", wird eine Ablehnungsnachricht an den Versicherungsnehmer gesendet, und der Vorgang wird beendet.

### Prozessschritt 3
"Reisewarnung prüfen" ist ein Expanded Sub-Prozess. Es enthält ein REST Outbound Connector Task "Reisewarnung prüfen " 

Name des Task:"Reisewarnung prüfen"

Beschreibung der Task: Diese Task ruft die von Travel Data erhaltenen Informationen über das Reiseziel ab, sendet über die API-Schnittstelle eine HTTP-Anfrage an die Website “https://travelwarning.api.bund.dev/”.Daraufhin erhalten eine Rückmeldung(JASON) von dieser Website.

Mögliche Entscheidungen nach Prozesschritt durch Gateways: Ein Gateway stellt dann fest, ob sie JASON Rücksendeinformationen erhält, und wenn ja, sendet es die geparsten Warnungen mit Ablehnung per Email an den VN, wenn nicht, beendet sie diese Task.

### Prozessschritt 4

### Prozessschritt 5

### Prozessschritt 6

### Prozessschritt 7



## Prozessende

### Ende

| Endbedingung | Details |
| ------------ | ------- |
| :heavy_check_mark: Gutfall | Wenn alle Informationsüberprüfung erfolgreich ist, senden Bestätigungsmail und drucken & senden Vertragsunterlage   |
| :x: Fehlerfall 1           | Wenn die Überprüfung "Travel Daten prüfen" nicht genehmigt wird, sendet das System eine Ablehnungsmitteilung an den Versicherungsnehmer |
| :x: Fehlerfall 2           | Wenn die Überprüfung "Person Daten prüfen" nicht genehmigt wird, sendet das System eine Ablehnungsmitteilung an den Versicherungsnehmer |
| :x: Fehlerfall 3           | Wenn die Reisewarnung existiert , sendet das System eine Ablehnungsmitteilung an den Versicherungsnehmer |


### Ergebnis / Output

| Geschäftsobjekt | Zielsystem | Verantwortlich |
| --------------- | ---------- | -------------- |
|  Bestätigungsmail | E-Mail-Versandsystem  | SendGird Outbound Connector|
|  Ausgedruckten Versicherungsvertrag  |      Vertragsystem,Drucksystem     |        Output-Managements        |


## Prozesskontext

Folgende Variablen werden während der Ausführung im Prozesskontext abgelegt:

| Variablenname | Typ  | Datentyp | Details |
| ------------- | ---- | -------- | ------- |
|  ext_Adresse  | Externe Variablen | String   |  Von außen empfangene Adresse  |
|  ext_Reisebeginn  | Externe Variablen  | Date    |   Von außen empfangen      |
|  ext_Reiseende | Externe Variablen  | Date    |    Von außen empfangen    |
|  ext_Reiseziel| Externe Variablen | String|Von außen empfangen|
|  ext_Gesamtkosten | Externe Variablen | BigDecmal| Von außen empfangen |
|  ext_Geburtsdatum | Externe Variablen | Date| Von außen empfangen |
|  ext_Vorname | Externe Variablen | Externe Variablen|String|Von außen empfangen|
|  ext_Nachname | Externe Variablen | Externe Variablen|String|Von außen empfangen|
|  ext_mail| Externe Variablen | Externe Variablen|String|Von außen empfangen|
|  ext_IBAN| Externe Variablen | String|Von außen empfangen|
|  ext_childOfPolicyHolder|Externe Variablen |boolean|Von außen empfangen|
|int_StatusTravelPrüfung|Interne Variablen|boolean| Während der Ausführung erzeugte Variablen|
|int_StatusPersonPrüfung|Interne Variablen|boolean| Während der Ausführung erzeugte Variablen|
|tec_Reisewarnung|Technische Variablen |API|Steurung des Kontrollflusses|
|ext_AblehnungNachricht|Externe Variablen|String|die nach draußen geschickt wird|

## Verknüpfte Dokumente 

### DMN Tabelle 
| DMN's Name |  
|--------|
|"Selbstbehalt ermitteln" |   
|"Person Daten prüfen"|   
|"Travel Daten prüfen "|  
