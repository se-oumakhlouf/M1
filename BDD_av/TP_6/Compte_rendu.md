# Sharding et réplication

<br></br>

## Partie 1 -> Solution du TP précédent (avec 2 Config Server) :

    mkdir -p /tmp/dbs/config1
    mkdir -p /tmp/dbs/config2
    mkdir -p /tmp/dbs/shard31
    mkdir -p /tmp/dbs/shard32

    Console 1 onglet 1 :

        ./mongod --configsvr --replSet configReplSet --port 27019 --dbpath /tmp/dbs/config1 


    Console 2 :

        ./mongod --configsvr --replSet configReplSet --port 27020 --dbpath /tmp/dbs/config2


    Console 1 onglet 2  --port 27019 :

        ./mongosh --port 27019

        config= { _id:"configReplSet", members : [{"_id":0, "host":"localhost:27019"}, {"_id":1, "host":"localhost:27020"}]}

        rs.initiate(config)


    Console 3 onglet 1 --port 27031 :

        ./mongod --shardsvr --replSet sh0 --port 27031 --dbpath /tmp/dbs/shard31

    
    Console 4 onglet 1-- port 27032 :

        ./mongod --shardsvr --replSet sh1 --port 27032 --dbpath /tmp/dbs/shard32


    Console 3 onglet 2 :

        ./mongosh --port 27031 --eval "rs.initiate()"

    
    Console 4 onglet 2 :

        ./mongosh --port 27032 --eval "rs.initiate()"


    Console 5 onglet 1 :

    ./mongos --configdb configReplSet/localhost:27019,localhost:27020 --port 27017


    Console 5, onglet 2 :

        ./mongosh --port 27017 

        sh.addShard( "sh0/localhost:27031");
        sh.addShard( "sh1/localhost:27032");
        sh.enableSharding("testdb")
        sh.shardCollection("testdb.col", {"testkey":1})

        use config
        db.settings.updateOne(
        { _id: "chunksize" },
        { $set: { _id: "chunksize", value: 1 } },
        { upsert: true }
        )

        use testdb
        db.printShardingStatus();

        for(i=0;i<10000;i++)
            db.col.insertOne({"testkey":Math.floor(Math.random()*10),"text":"You cannot specify multiple collations for an operation. For example, you cannot specify different collations per field, or if performing a find with a sort, you cannot use one collation for the find and another for the sort." });

        db.printShardingStatus();

        On ajoute 100 000 au lieu de 10 000 pour être sûr d'avoir plusieus chunks
        for(i=0;i<100000;i++)
            db.col.insertOne({"testkey":Math.floor(Math.random()*10),"text":"You cannot specify multiple collations for an operation. For example, you cannot specify different collations per field, or if performing a find with a sort, you cannot use one collation for the find and another for the sort." });
        On voit bien 110 000 documents

        db.col.getShardDistribution()



    Console 3 onglet 2 :

        ./mongosh --port 27031

        use testdb
        db.col.countDocuments() -> 54 908


    Console 4 onglet 2 :

        ./mongosh --port 27032

        use testdb
        db.col.countDocuments() -> 69323

    Pas exactement 100 000 car il y a des doublons, il faut attendre quelques minutes pour l'équilibrage


<br></br>

## Partie B :

    Question 1 :

        use sante

        ./mongoimport --db sante --collection etab --file ../../base-document-etablissements.jsonl

        db.etab.countDocuments() -> 4268

    
    On ajoute une Zone "Nord" et une Zone "Sud"

    sh.enableSharding("sante")
    sh.addShardToZone("sh0", "NORD")
    sh.addShardToZone("sh1", "SUD")

    sh.createIndex("coordoonees.latitude")

    sh.updateZoneKeyRange("sante.etab", { "coordonnees.latitude": 47 }, { "coordonnees.latitude": 52 }, "NORD")
    sh.updateZoneKeyRange("sante.etab", { "coordonnees.latitude": 0 }, { "coordonnees.latitude": 47 }, "SUD")

