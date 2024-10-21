## init :

    ~/Téléchargements/mongosh-2.3.2-linux-x64/bin$ ./mongosh

    mkdir -p /tmp/tp/data
    ~/Téléchargements/mongodb-linux-x86_64-debian11-7.0.14/bin$ ./mongod --dbpath /tmp/tp/data

<br></br>

# Question 1 :

    ./mongod --replSet rs2 --dbpath /tmp/tp/data/rs2_21 --port 27021

    ./mongosh --port 27021 (dans /mongosh-2.3.2-linux-x64/bin)

<br></br>

# Question 2 :

    ./mongod --replSet rs2 --dbpath /tmp/tp/data/rs2_22 --port 27022

    ./mongosh --port 27022 (dans /mongosh-2.3.2-linux-x64/bin)

```json
rs2 [direct: primary] test> rs.add("localhost:27022")
{
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1729513272, i: 1 }),
    signature: {
      hash: Binary.createFromBase64('AAAAAAAAAAAAAAAAAAAAAAAAAAA=', 0),
      keyId: Long('0')
    }
  },
  operationTime: Timestamp({ t: 1729513272, i: 1 })
}

```

    ./mongod --replSet rs2 --dbpath /tmp/tp/data/rs2_24 --port 27024

    ./mongosh --port 27024

```json
cfg= rs.config()
cfg.settings.chainingAllowed=false
rs.reconfig(cfg)
```

<br></br>

# Question 4 :

```json
use rs2db                   ## sur le port 27021
for(i=0;i<1000;i++){db.log.insertOne({"count":i,"date":new Date()})}
db.log.countDocuments()     ## 1000  
```

<br></br>

# Question 5 :

On observe bien les 1000 documents sur les 3 ports

<br></br>

# Question 6 :

```json
cfg = rs.conf()
cfg.members[3].priority = 0
cfg.members[3].hidden = true
rs.reconfig(cfg)


    _id: 3,
    host: 'localhost:27024',
    arbiterOnly: false,
    buildIndexes: true,
    hidden: true,
    priority: 0,
    tags: {},
    secondaryDelaySecs: Long('0'),
    votes: 1

```

<br></br>

# Question 7 :

```json
for(i=0;i<100;i++){db.log.insertOne({"count":i,"date":new Date()})}
On observe 1100 docuements pour les 3 ports
```

<br></br>

# Question 8 :

    ./mongod --replSet rs2 --dbpath /tmp/tp/data/rs2_25 --port 27025

    ./mongosh --port 27025

```json
db.adminCommand({
  "setDefaultRWConcern" : 1,
  "defaultWriteConcern" : {
    "w" : 2
  }
})

rs.addArb("localhost:27025")
```

<br></br>

# Question 9 :

```json
rs.stepDown(5,5)        # temps arbitraire
```

    le port 27021 devient secondary et le port 27022 devient primary

<br></br>

# Question 10 :

    Le problème dans une situation de split brain est qu'un host en secondary ne pourra pas être relier à plusieurs primary, et donc il y aura des problèmes dans la synchronisation du cluster


<br></br>

# Question 11 :

    Le fait d'avoir un nombre impair de noeud dans un cluster apporte une assurance pour obtenir un vote sans égalité et donc une majorité évitant les scénario de split-brain
    Avoir un nombre pair n'offre pas plus de robustesse

<br></br>

# Question 12 :

    7 noeuds votants pour une questions de temps, le processus pour élire un new primary est long

<br></br>

# Question 13 :

```json 
rs.printReplicationInfo()
actual oplog size
'990 MB'
---
configured oplog size
'990 MB'
---
log length start to end
'5250 secs (1.46 hrs)'
---
oplog first event time
'Mon Oct 21 2024 14:21:12 GMT+0200 (heure d’été d’Europe centrale)'
---
oplog last event time
'Mon Oct 21 2024 15:48:42 GMT+0200 (heure d’été d’Europe centrale)'
---
now
'Mon Oct 21 2024 15:48:45 GMT+0200 (heure d’été d’Europe centrale)'
```

    Avec 5% on peut sauvegarder les données des dernières 24h



