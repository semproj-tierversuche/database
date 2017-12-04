# Semesterprojekt Notizen

<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Semesterprojekt Notizen](#semesterprojekt-notizen)
	- [1 Allgemeine Informationen](#1-allgemeine-informationen)
		- [1.1 Email-Verteiler](#11-email-verteiler)
		- [1.2 Github](#12-github)
		- [1.3 Trello](#13-trello)
		- [1.4 Slack](#14-slack)
		- [1.5 Scrum](#15-scrum)
	- [2 Paper Notizen](#2-paper-notizen)
		- [2.1 Go3R – Semantic Internet Search Engine for Alternative Methods to Animal Testing](#21-go3r-semantic-internet-search-engine-for-alternative-methods-to-animal-testing)
		- [2.2 Experiences from Developing the Domain-Specific Entity Search Engine GeneView](#22-experiences-from-developing-the-domain-specific-entity-search-engine-geneview)
	- [3 Technologien](#3-technologien)
	- [4 Datenbankschema](#4-datenbankschema)
	- [5 Ähnlichkeitssuche](#5-ähnlichkeitssuche)
	- [6 Tomcat Ansatz (nicht mehr aktuell!)](#6-tomcat-ansatz-nicht-mehr-aktuell)
	- [7 Netty Ansatz](#7-netty-ansatz)
	- [7 ElasticSearch](#7-elasticsearch)

<!-- /TOC -->


## 1 Allgemeine Informationen

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

### 1.5 Scrum
* alle 2 Wochen ein neuer Sprint
  - 1.Sprint: 10.11.2017

## 2 Paper Notizen

### 2.1 Go3R – Semantic Internet Search Engine for Alternative Methods to Animal Testing

* 3Rs Prinzip:
  - Replacement (Vermeidung)
  - Reduction (Verringerung)
  - Refinement (Verbesserung)
* Go3R - Suchengine die auf "GoPubMed" aufbaut
* gesammtes Web (hinzugefügte Datenbanken) können durchsucht werden
  - spannend da nicht jedes Dokument auf PubMed indexiert wird
* ontology - es werden "groups of conecpts" gebildet
  - Genre
  - Synonyme
  - Korrelationen
* Dokumente sollen somit in Gruppen eingeordnet werden
* Schwierigkeit: Identifizieren von Beschreibungen für die selbe Bedeutung
  - Aufbau von hierarchischer Struktur durch Eltern-Kind Knoten
  - hier muss ausgebaut werden...
* Verwendung Machine Learning Algorithmus (Maximum Entropy Method)
  - lernen zu welcher Kategorie ein bestimmtes Wort gehört durch heranziehen von
    - immer auftretenden Wörtern in dem 3Rs Zusammenhang
    - Metadaten
* Ergebnisse werden nach Relevanz aufgelistet
* auch Dokumente die nur indirekt mit der Suchanfrage können durch die verwendeten Technologien aufgelistet werden
* erster Test-Query: "Eye irritation" war erfolgreich
  - 29/31 relevante Dokumente wurden gelistet
  - 13/16 Methoden wurden somit präsentiert


### 2.2 Experiences from Developing the Domain-Specific Entity Search Engine GeneView
* GeneView - Such Engine um wissenschaftliche Dokumente zu durchsuchen, dabei wird auch der Text analysiert
  - zwischen verschiedenen Kategorien kann unterschieden werden
  - und mehrere Relation können präsentiert werden
* Herausforderung: Synonyme, Morphologie und Mehrdeutigkeit
* Ergebnisse können nach bestimmten Attributen/Kategorien gefiltert werden
* Texte werden in Lucene gespeichert
* Metadaten werden in relationalen Datenbanken gespeichert
* ganze Texte werden zusätzlich in HTML gespeichert
* Bevor die Hauptinformationen extrahiert werden: Abschnittnamen werden identifiziert, dient zur Gewichtung der Wichtigkeit des Artikels
* Texte werden beim einlesen durch RER und RE Tools zur Analyse geschickt (Machine Learning)
* nächster Schritt: Relationship Analyse, wird durch verschiedene Tools ermöglicht
* in NER:
  - Falls zu einem Wort eine bekannte Abkürzunng existiert wird die Abkürzung auch als Tag hinzugefügt
  - Wenn ein Wort/Wörter mehrmals in einem Text gefunden werden, werden diese auch als Tag markiert???
* es existiert Identifiyer Table um nach genau einem Thema nachzuschlagen
* Lucene kann nicht per Default nach extrahierten Informationen suchen!

## 3 Technologien
* ElasticSearch zur Indexierung und Speicherung der Daten/Metadaten(JSON)
* Dokumente gesondert in Datenbank speichern
* GSON
* Levensthein-Distanz


## 4 Datenbankschema

* Index: Articles
* 1. Type: Article
* 2. Type: FullText

* 1. Schema:

  - PMID - Integer
  - Titel - String
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
  - 3R Feld?

* 2. Schema:
  - PMID - Integer
  - FullText - String

## 5 Ähnlichkeitssuche

* zwei-stufige Suche
  - zunächst ähnliche Dokumente finden
  - dann aus dieser Ergebnismenge die relevanten filtern
    - 1. Liste an relevanten Wörtern von Daniel vergleichen mit den Annotations (Liste bis Freitag)  -> Levensthein Distanz mit match-query
    - 2. Common-Terms Query über die Abstracts
    - 3. Annotations mit Annotations vergleichen (min should match noch unbekannt) -> Levensthein Distanz
    - (4. Mesh Terms)
    - (5. Keywords)
    - (6. Substances)


## 6 Tomcat Ansatz (nicht mehr aktuell!)
* deployment:
	- mvn install
	- kopieren von .war:
		- sudo cp ~/Dropbox/Die\ Höhle\ des\ Löwen/Uni/5.\ Semester/Semesterprojekt/gitrepo/database/elasticsearch/target/esapi.war /var/lib/tomcat8/webapps      
	- tomcat starten:
		- sudo systemctl start/restart tomcat8.service

## 7 Netty Ansatz

* 1. Maven dependencies laden, wenn nicht bereits getan
	```
	mvn install
	```

* 2. elasticsearch server starten

```
	./bin/elasticseach
```

* 3. ServerApp.java starten (startet den Netty Server)
* 4. innerhalb des Projekts befindet siche in Testclient um die einzelnen servieces zu testen, hier die Pfade für die Testeingaben anpassen




## 7 ElasticSearch
* Ein Verfahren, um die gespeicherten Daten im Browser anschauen:
	- man kann im Browser das Folgende eingeben (formatierte Ausgabe): http://localhost:9200/index/type/_id?pretty
	- nicht formatierte Ausgabe: http://localhost:9200/index/type/_id
	- hier ist ein Beispiel für index "customer", type "doc" und _id "1": http://localhost:9200/customer/doc/1?pretty
	- die Portnummer von "localhost" ist von Benutzer eingestellt, muss nicht unbedingt "9200" sein
	- wenn man statt "_id" "_count" eingibt, dann sieht man die Anzahl von gespeicherten Daten
	- Vorrige Information wurde erstmal (28.11.2017) von Olja vorgestellt und aufgrund Seds Anfrage veröffentlicht
