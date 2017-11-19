# database


#1. Prototype vom 11.11.2017
1. zunächst muss der Index "Semesterprojekt" erstell werden;
```
curl -XPUT 'localhost:9200/twitter?pretty' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 3,
            "number_of_replicas" : 2
        }
    }
}
'
```

2. dann muss der Pfad zu dem JSON File innerhalb des Programms gesetzt werden


#2. Prototype vom 19.11.2017
(Beschreibung wurde auf einem Arch-Linux System getestet)
Verwendung eines tomcat8 Webservers
Deployment:

1. kopieren der ```esapi.war``` nach ```/var/lib/tomcat8/webapps```
2. tomcat8 Server starten ```sudo systemctl start tomcat8.service``` 
