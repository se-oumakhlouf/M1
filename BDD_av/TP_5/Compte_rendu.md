# Sharding

## init : 

    mkdir -p /tmp/dbs/config1
    mkdir -p /tmp/dbs/config2
    mkdir -p /tmp/dbs/shard31
    mkdir -p /tmp/dbs/shard32


<br></br>

# Question 2 - 3 :

    dans ~/Téléchargements/mongodb-linux-x86_64-debian11-7.0.14/bin$ : 
        ./mongod --configsvr --replSet configReplSet --port 27019 --dbpath /tmp/dbs/config1
        ./mongod --configsvr --replSet configReplSet --port 27020 --dbpath /tmp/dbs/config2


    dans  ~/Téléchargements/mongosh-2.3.2-linux-x64/bin$ : 
        ./mongosh --port 27019

    dans 27019 :
        rs.initiate()
        rs.add("localhost:27020")

    vérification avec rs.status(), on voit id1 en primaire et id2 en secondaire

<br></br>

# Question 4 :

    dans ~/Téléchargements/mongodb-linux-x86_64-debian11-7.0.14/bin$ :
        ./mongod --shardsvr --replSet sh0 --port 27031 --dbpath /tmp/dbs/shard31
        ./mongod --shardsvr --replSet sh1 --port 27032 --dbpath /tmp/dbs/shard32

    dans  ~/Téléchargements/mongosh-2.3.2-linux-x64/bin$ :
        ./mongosh --port 27031 --eval "rs.initiate()"
        ./mongosh --port 27032 --eval "rs.initiate()"

<br></br>

# Question 5 :

    dans mongodb:
        ./mongos --configdb configReplSet/localhost:27019,localhost:27020 --port 27017

    dans mongosh:
        ./mongosh --port 27017

    dans 27017 :
        use config
        db.settings.updateOne(
            { _id: "chunksize" },
            { $set: { _id: "chunksize", value: 1 } },
            { upsert: true }
        )

        use testdb
        sh.addShard( "sh0/localhost:27031" );
        sh.addShard( "sh1/localhost:27032" );

        On peut voir le status du sharding avec : db.printShardingStatus()

<br></br>

# Question 6 :


    dans testdb à partir de 27017 :
        sh.enableSharding("testdb")
        sh.shardCollection("testdb.col1", {testkey : 1})

        for(i=0;i<30000;i++){db.col1.insertOne({"testkey":i%3000})} // cela ne produit pas une quantité de donnée assez grande, il faut le texte

        for(i=0;i<30000;i++){db.col1.insertOne({"testkey":i%3000, "text":"Sous un ciel étoilé une douce brise caresse les feuilles des arbres murmurant des histoires anciennes Les lucioles scintillent éclairant le chemin des rêveurs Chaque pas résonne comme un écho de l’univers rappelant que la magie existe pour ceux qui croient La nuit pleine de mystères invite à l’aventure et à la découverte"})}

        db.col1.getShardDistribution()
            Shard sh0 at sh0/localhost:27031
            {
                data: '4.61MiB',
                docs: 23220,
                chunks: 2,
                'estimated data per chunk': '2.3MiB',
                'estimated docs per chunk': 11610
            }
            ---
            Shard sh1 at sh1/localhost:27032
            {
                data: '9.52MiB',
                docs: 53385,
                chunks: 1,
                'estimated data per chunk': '9.52MiB',
                'estimated docs per chunk': 53385
            }
            ---
            Totals
            {
                data: '14.13MiB',
                docs: 76605,
                chunks: 3,
                'Shard sh0': [
                    '32.65 % data',
                    '30.31 % docs in cluster',
                    '208B avg obj size on shard'
                ],
                'Shard sh1': [
                    '67.34 % data',
                    '69.68 % docs in cluster',
                    '187B avg obj size on shard'
                ]
            }



<br></br>

# Question 7 :

    dans mongosh:
        ./mongosh --port 27031
            sh0 [direct: primary] testdb> db.col1.countDocuments()
                résultat : 23220

        ./mongosh --port 27032
            sh1 [direct: primary] testdb> db.col1.countDocuments()
                résultat : 53385

        port 27017 :
            [direct: mongos] testdb> db.col1.countDocuments()
                résultat : 60000

<br></br>

# Question 8 :

    db.printShardingStatus()
        chunks: [
            { min: { testkey: MinKey() }, max: { testkey: 783 }, 'on shard': 'sh0', 'last modified': Timestamp({ t: 2, i: 0 }) },
            { min: { testkey: 783 }, max: { testkey: 1161 }, 'on shard': 'sh0', 'last modified': Timestamp({ t: 3, i: 0 }) },
            { min: { testkey: 1161 }, max: { testkey: MaxKey() }, 'on shard': 'sh1', 'last modified': Timestamp({ t: 3, i: 1 }) }
        ]


<br></br>

# Complément sur MongoDB (administration et indexation)

<br></br>

# Question 9 :

    dans mongodb-data:
        ./mongoimport --db tmdb --collection films --jsonArray --file tmdb_movies.json 
        use tmdb
            switched to db tmdb
        [direct: mongos] tmdb> db.films.countDocuments()
            2000
