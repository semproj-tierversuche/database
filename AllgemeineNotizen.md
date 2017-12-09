# Semesterprojekt Notizen vom Datenbankteam

<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Semesterprojekt Notizen vom Datenbankteam](#semesterprojekt-notizen-vom-datenbankteam)
	- [1. Allgemeine Informationen](#1-allgemeine-informationen)
		- [1.1 Email-Verteiler](#11-email-verteiler)
		- [1.2 Github](#12-github)
		- [1.3 Trello](#13-trello)
		- [1.4 Slack](#14-slack)
	- [2. Datenbankschema](#2-datenbankschema)
	- [3. Ähnlichkeitssuche](#3-ähnlichkeitssuche)
	- [4. Tomcat Ansatz (nicht mehr aktuell!)](#4-tomcat-ansatz-nicht-mehr-aktuell)
	- [5. Netty Ansatz](#5-netty-ansatz)
	- [6. ElasticSearch](#6-elasticsearch)

<!-- /TOC -->


## 1. Allgemeine Informationen

### 1.1 Email-Verteiler
* intern:
  - informatik_sp_tierversuche@lists.hu-berlin.de
* extern:
  - informatik_sp_tierversuche_extern@lists.hu-berlin.de

### 1.2 Github
* https://github.com/semproj-tierversuche

### 1.3 Trello
* https://trello.com/b/O0TRy3w9/alternativmethoden-zu-tierversuchen

### 1.4 Slack
*


## 2. Datenbankschema

* Index: Articles
* 1. Type: Article
* 2. Type: FullText

* 1. Schema:

  - PMID - Integer
  - Title - String
  - Author - String[]
  - Date - Date
  - Journal - String[]
  - Link - String
  - Keywords - String[]
  - Identifyer - String[]	(Synonyme)
  - Annotations - String[]
  - Suggest(Dient zu Autocompletion)
  - Abstract - String[] (Wegen strukturierter Abstracts)
  - Publikationstype - String
  - Substances - String[]
  - Mesh-Terms - String[]
  - Textmining Version - String

## 3. Ähnlichkeitssuche

Die suche erfolgt über 3 Stufen, dabei wird versucht immer mehr Dokumente zu filtern. Vorraussetzung für die Suche ist allerdings, dass in dem input Dokument Mesh-Terms vergeben sind. (Falls bei dem input Dokument keine Mesh-Terms vergeben wurden, soll eine Fehlermeldung angezeigt werden) Bei der Ähnlichkeitssuche werden auch nur die Dokumente berücksichtgt, bei denen die Mesh-Terms gesetzt wurden.

Suchschritte:
1. Suche nach Dokumenten mit gleichen Mesh-Terms (50% Ähnlichkeit?!?)
	- in Zukunft auch über gleiche Annotations
2. aus Ergebnismenge nach 1.: Suche nach ähnlichen Dokumenten (MoreLikeThisQuery)
	- Parameter folgen noch...
3. aus Ergebnismenge nach 2.: Prüfen ob Wörter aus der 3R-Liste in den Dokumenten vorhanden sind (mind. 1 Wort muss vorhanden sein?)

## 4. Tomcat Ansatz (nicht mehr aktuell!)
* deployment:
	- mvn install
	- kopieren von .war:
		- sudo cp ~/Dropbox/Die\ Höhle\ des\ Löwen/Uni/5.\ Semester/Semesterprojekt/gitrepo/database/elasticsearch/target/esapi.war /var/lib/tomcat8/webapps      
	- tomcat starten:
		- sudo systemctl start/restart tomcat8.service

## 5. Netty Ansatz

(in intellij)

* 1. Maven dependencies laden, wenn nicht bereits getan
	```
	mvn install
	```

* 2. elasticsearch server starten

	```
	./bin/elasticseach
	```

* 3. ServerApp.java starten (startet den Netty Server)
* 4. innerhalb des Projekts befindet Beispiele in Testclient um die einzelnen servieces zu testen, hier die Pfade für die Testeingaben anpassen


## 6. ElasticSearch
* Ein Verfahren, um die gespeicherten Daten im Browser anschauen:
	- man kann im Browser das Folgende eingeben (formatierte Ausgabe): http://localhost:9200/index/type/_id?pretty
	- nicht formatierte Ausgabe: http://localhost:9200/index/type/_id
	- hier ist ein Beispiel für index "customer", type "doc" und _id "1": http://localhost:9200/customer/doc/1?pretty
