    ~/Téléchargements/mongosh-2.3.2-linux-x64/bin$ ./mongosh

    ~/Téléchargements/mongodb-linux-x86_64-debian11-7.0.14/bin$ ./mongod --dbpath /tmp/tp/data


### Question 2 :

```json
db.pers.insertOne({ "nom": "Davis", "prenom": "Miles", "age": 65, "adresse": { "city": "NY", "zip": "10021" }, "phones": [{ "type": "home", "phone": "111-555-1234" }, { "type": "mobile", "phone": "116-55-1234" }] })
{
  acknowledged: true,
  insertedId: ObjectId('670fb3b9f89ce4a034fe6911')
}


db.pers.findOne()
{
  _id: ObjectId('670fb3b9f89ce4a034fe6911'),
  nom: 'Davis',
  prenom: 'Miles',
  age: 65,
  adresse: { city: 'NY', zip: '10021' },
  phones: [
    { type: 'home', phone: '111-555-1234' },
    { type: 'mobile', phone: '116-55-1234' }
  ]
}


db.pers.countDocuments()
1
```

<br></br>

### Question 3 :


    ./mongoimport --db films --collection films --jsonArray --file ../../films.json 


<br></br>

### Question 4 et + :

```json
db.films.find( {"Type" : "movie"})

db.films.find( {"Type" : "movie"}, {"Title" : 1})

db.films.find( {"Type" : "movie"}, {"Title" : 1}, {"_id" : 0})

db.films.countDocuments()
16

db.films.distinct("Type")
[ 'movie', 'series' ]

db.films.distinct("Title")
[
  '300',
  "Assassin's Creed",
  'Avatar',
  'Breaking Bad',
  'Doctor Strange',
  'Game of Thrones',
  'Gotham',
  'I Am Legend',
  'Interstellar',
  'Luke Cage',
  'Narcos',
  'Power',
  'Rogue One: A Star Wars Story',
  'The Avengers',
  'The Wolf of Wall Street',
  'Vikings'
]

```

<br></br>

### Question 10 :

    Dans mon mongodb-linux-x86_64-debian11-7.0.14/bin : 

    ./mongod --replSet toto --dbpath /tmp/tp/dbs/data1 --port 27017

    ./mongod --replSet toto --dbpath /tmp/tp/dbs/data2 --port 27018

    ./mongod --replSet toto --dbpath /tmp/tp/dbs/data3 --port 27019

<br></br>

### Question 11 :

    ./mongosh --port 27017 (dans /mongosh-2.3.2-linux-x64/bin)

```json
test> config = {
...  _id: "toto",
...  members: [
...  {"_id": 0, "host": "localhost:27017"},
...  {"_id": 1, "host": "localhost:27018"},
...  {"_id": 2, "host": "localhost:27019"}
...  ]
... }
{
  _id: 'toto',
  members: [
    { _id: 0, host: 'localhost:27017' },
    { _id: 1, host: 'localhost:27018' },
    { _id: 2, host: 'localhost:27019' }
  ]
}
test> rs.initiate(config)
{ ok: 1 }
```

<br></br>

### Question 12 :

```json
toto [direct: primary] test> rs.status()
{
  set: 'toto',
  date: ISODate('2024-10-16T13:29:03.885Z'),
  myState: 1,
  term: Long('1'),
  syncSourceHost: '',
  syncSourceId: -1,
  heartbeatIntervalMillis: Long('2000'),
  majorityVoteCount: 2,
  writeMajorityCount: 2,
  votingMembersCount: 3,
  writableVotingMembersCount: 3,
  optimes: {
    lastCommittedOpTime: { ts: Timestamp({ t: 1729085337, i: 1 }), t: Long('1') },
    lastCommittedWallTime: ISODate('2024-10-16T13:28:57.969Z'),
    readConcernMajorityOpTime: { ts: Timestamp({ t: 1729085337, i: 1 }), t: Long('1') },
    appliedOpTime: { ts: Timestamp({ t: 1729085337, i: 1 }), t: Long('1') },
    durableOpTime: { ts: Timestamp({ t: 1729085337, i: 1 }), t: Long('1') },
    lastAppliedWallTime: ISODate('2024-10-16T13:28:57.969Z'),
    lastDurableWallTime: ISODate('2024-10-16T13:28:57.969Z')
  },
  lastStableRecoveryTimestamp: Timestamp({ t: 1729085317, i: 1 }),
  electionCandidateMetrics: {
    lastElectionReason: 'electionTimeout',
    lastElectionDate: ISODate('2024-10-16T13:25:57.944Z'),
    electionTerm: Long('1'),
    lastCommittedOpTimeAtElection: { ts: Timestamp({ t: 1729085147, i: 1 }), t: Long('-1') },
    lastSeenOpTimeAtElection: { ts: Timestamp({ t: 1729085147, i: 1 }), t: Long('-1') },
    numVotesNeeded: 2,
    priorityAtElection: 1,
    electionTimeoutMillis: Long('10000'),
    numCatchUpOps: Long('0'),
    newTermStartDate: ISODate('2024-10-16T13:25:57.955Z'),
    wMajorityWriteAvailabilityDate: ISODate('2024-10-16T13:25:58.484Z')
  },
  members: [
    {
      _id: 0,
      name: 'localhost:27017',
      health: 1,
      state: 1,
      stateStr: 'PRIMARY',
      uptime: 525,
      optime: { ts: Timestamp({ t: 1729085337, i: 1 }), t: Long('1') },
      optimeDate: ISODate('2024-10-16T13:28:57.000Z'),
      lastAppliedWallTime: ISODate('2024-10-16T13:28:57.969Z'),
      lastDurableWallTime: ISODate('2024-10-16T13:28:57.969Z'),
      syncSourceHost: '',
      syncSourceId: -1,
      infoMessage: '',
      electionTime: Timestamp({ t: 1729085157, i: 1 }),
      electionDate: ISODate('2024-10-16T13:25:57.000Z'),
      configVersion: 1,
      configTerm: 1,
      self: true,
      lastHeartbeatMessage: ''
    },
    {
      _id: 1,
      name: 'localhost:27018',
      health: 1,
      state: 2,
      stateStr: 'SECONDARY',
      uptime: 196,
      optime: { ts: Timestamp({ t: 1729085337, i: 1 }), t: Long('1') },
      optimeDurable: { ts: Timestamp({ t: 1729085337, i: 1 }), t: Long('1') },
      optimeDate: ISODate('2024-10-16T13:28:57.000Z'),
      optimeDurableDate: ISODate('2024-10-16T13:28:57.000Z'),
      lastAppliedWallTime: ISODate('2024-10-16T13:28:57.969Z'),
      lastDurableWallTime: ISODate('2024-10-16T13:28:57.969Z'),
      lastHeartbeat: ISODate('2024-10-16T13:29:02.026Z'),
      lastHeartbeatRecv: ISODate('2024-10-16T13:29:03.020Z'),
      pingMs: Long('0'),
      lastHeartbeatMessage: '',
      syncSourceHost: 'localhost:27017',
      syncSourceId: 0,
      infoMessage: '',
      configVersion: 1,
      configTerm: 1
    },
    {
      _id: 2,
      name: 'localhost:27019',
      health: 1,
      state: 2,
      stateStr: 'SECONDARY',
      uptime: 196,
      optime: { ts: Timestamp({ t: 1729085337, i: 1 }), t: Long('1') },
      optimeDurable: { ts: Timestamp({ t: 1729085337, i: 1 }), t: Long('1') },
      optimeDate: ISODate('2024-10-16T13:28:57.000Z'),
      optimeDurableDate: ISODate('2024-10-16T13:28:57.000Z'),
      lastAppliedWallTime: ISODate('2024-10-16T13:28:57.969Z'),
      lastDurableWallTime: ISODate('2024-10-16T13:28:57.969Z'),
      lastHeartbeat: ISODate('2024-10-16T13:29:02.025Z'),
      lastHeartbeatRecv: ISODate('2024-10-16T13:29:03.019Z'),
      pingMs: Long('0'),
      lastHeartbeatMessage: '',
      syncSourceHost: 'localhost:27017',
      syncSourceId: 0,
      infoMessage: '',
      configVersion: 1,
      configTerm: 1
    }
  ],
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1729085337, i: 1 }),
    signature: {
      hash: Binary.createFromBase64('AAAAAAAAAAAAAAAAAAAAAAAAAAA=', 0),
      keyId: Long('0')
    }
  },
  operationTime: Timestamp({ t: 1729085337, i: 1 })
}


toto [direct: primary] test> rs.hello()
{
  topologyVersion: {
    processId: ObjectId('670fbd92d6c6cbf69e7b3488'),
    counter: Long('6')
  },
  hosts: [ 'localhost:27017', 'localhost:27018', 'localhost:27019' ],
  setName: 'toto',
  setVersion: 1,
  isWritablePrimary: true,
  secondary: false,
  primary: 'localhost:27017',
  me: 'localhost:27017',
  electionId: ObjectId('7fffffff0000000000000001'),
  lastWrite: {
    opTime: { ts: Timestamp({ t: 1729085477, i: 1 }), t: Long('1') },
    lastWriteDate: ISODate('2024-10-16T13:31:17.000Z'),
    majorityOpTime: { ts: Timestamp({ t: 1729085477, i: 1 }), t: Long('1') },
    majorityWriteDate: ISODate('2024-10-16T13:31:17.000Z')
  },
  maxBsonObjectSize: 16777216,
  maxMessageSizeBytes: 48000000,
  maxWriteBatchSize: 100000,
  localTime: ISODate('2024-10-16T13:31:19.019Z'),
  logicalSessionTimeoutMinutes: 30,
  connectionId: 2,
  minWireVersion: 0,
  maxWireVersion: 21,
  readOnly: false,
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1729085477, i: 1 }),
    signature: {
      hash: Binary.createFromBase64('AAAAAAAAAAAAAAAAAAAAAAAAAAA=', 0),
      keyId: Long('0')
    }
  },
  operationTime: Timestamp({ t: 1729085477, i: 1 })
}
```

<br></br>

### Question 13 :

```json
toto [direct: primary] test> use db1
switched to db db1

toto [direct: primary] db1> for(i=0;i<1000;i++){db.col2.insertOne({"count":i})}
{
  acknowledged: true,
  insertedId: ObjectId('670fc0d68cdc669879fe6cf8')
}

toto [direct: primary] db1> db.col2.countDocuments()
1000
```

<br></br>

### Question 14 :

    ./mongosh --port 27018

    toto [direct: secondary] test>

```json
toto [direct: secondary] test> show dbs
admin    80.00 KiB
config  228.00 KiB
db1      76.00 KiB
local   516.00 KiB

toto [direct: secondary] db1> db.col2.countDocuments()
1000

toto [direct: primary] db1> db.col2.insertOne( {"count" : 1001} )
{
  acknowledged: true,
  insertedId: ObjectId('670fc3668cdc669879fe6cf9')
}


toto [direct: secondary] db1> db.col2.countDocuments()
1001
```

<br></br>

### Question 15 :

Une erreur car isWritablePrimary: false, dans le port 27018

```json
toto [direct: secondary] db1> db.col2.insertOne( {"count" : 1002} )
MongoServerError[NotWritablePrimary]: not primary
```

<br></br>

### Question 16 :

```json
db1> rs.status()
Uncaught:
MongoServerSelectionError: connect ECONNREFUSED 127.0.0.1:27017
Caused by: 
MongoNetworkError: connect ECONNREFUSED 127.0.0.1:27017
Caused by: 
Error: connect ECONNREFUSED 127.0.0.1:27017
```

    27017 s'est fermé
    27018 est devenue primary

<br></br>

### Question 17 :

    27017 sera secondary et 27018 garde son primary


