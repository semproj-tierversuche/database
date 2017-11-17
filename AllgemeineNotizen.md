#Semesterprojekt Notizen
<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Semesterprojekt Notizen](#semesterprojekt-notizen)
	- [1 Allgemeine Informationen](#1-allgemeine-informationen)
		- [Termine](#termine)
		- [1.1 Email-Verteiler](#11-email-verteiler)
		- [1.2 Github](#12-github)
		- [1.3 Trello](#13-trello)
		- [1.4 Slack](#14-slack)
		- [1.5 Scrum](#15-scrum)
	- [2 Paper Notizen](#2-paper-notizen)
		- [2.1 Go3R – Semantic Internet Search Engine for Alternative Methods to Animal Testing](#21-go3r-semantic-internet-search-engine-for-alternative-methods-to-animal-testing)
		- [2.2 Experiences from Developing the Domain-Specific Entity Search Engine GeneView](#22-experiences-from-developing-the-domain-specific-entity-search-engine-geneview)
	- [3 Technologien](#3-technologien)
	- [4 Fragen zu User Stories?](#4-fragen-zu-user-stories)
		- [4.1 Suche?](#41-suche)
		- [4.2 Replacement Publikationen](#42-replacement-publikationen)
		- [4.3 Metadaten von Publikationen](#43-metadaten-von-publikationen)
		- [4.4 Erweiterbarkeit für neue Ressourcen-Quellen](#44-erweiterbarkeit-für-neue-ressourcen-quellen)
		- [4.5 Filtern für die Output-Liste](#45-filtern-für-die-output-liste)
		- [4.6 Grund für Ähnlichkeit](#46-grund-für-ähnlichkeit)
		- [4.7 Grund für keine Alternativmethode](#47-grund-für-keine-alternativmethode)
		- [4.8 Log-Information der Benutzer](#48-log-information-der-benutzer)

<!-- /TOC -->


##1 Allgemeine Informationen

###Termine
* 03.11.2017 BfR

###1.1 Email-Verteiler
* intern:
  - informatik_sp_tierversuche@lists.hu-berlin.de
* extern:
  - informatik_sp_tierversuche_extern@lists.hu-berlin.de

###1.2 Github
* https://github.com/semproj-tierversuche

###1.3 Trello
* https://trello.com/b/O0TRy3w9/alternativmethoden-zu-tierversuchen

###1.4 Slack
*

###1.5 Scrum
* alle 2 Wochen ein neuer Sprint
  - 1.Sprint: 10.11.2017

## 2 Paper Notizen

###2.1 Go3R – Semantic Internet Search Engine for Alternative Methods to Animal Testing

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


###2.2 Experiences from Developing the Domain-Specific Entity Search Engine GeneView
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

##3 Technologien
* ElasticSearch zur Indexierung und Speicherung der Daten/Metadaten(JSON)
* Dokumente gesondert in Datenbank speichern
* GSON
* Levensthein-Distanz


##4 Datenbankschema

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


##5Fragen????
* Was bedeutet Ähnlichkeit über Annotations, Schlagwörter, Substanzen????
