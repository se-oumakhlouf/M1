/* Question 1 :

    select ville from client
    where prenom = 'Alice' and age = 38
    intersect
    select ville from client
    where prenom = 'Robert' and age = 38;




Client
                                                                     QUERY PLAN    
---------------------------------------------------------------------------------------------------------------------------------
 HashSetOp Intersect  (cost=0.00..12286.94 rows=10 width=72) (actual time=90.516..90.519 rows=4 loops=1)
   ->  Append  (cost=0.00..12286.88 rows=26 width=72) (actual time=2.035..90.462 rows=31 loops=1)
         ->  Subquery Scan on "*SELECT* 1"  (cost=0.00..6143.44 rows=13 width=11) (actual time=2.035..55.616 rows=18 loops=1)
               ->  Seq Scan on client  (cost=0.00..6143.31 rows=13 width=7) (actual time=2.033..55.608 rows=18 loops=1)
                     Filter: (((prenom)::text = 'Alice'::text) AND (age = 38))
                     Rows Removed by Filter: 248936
         ->  Subquery Scan on "*SELECT* 2"  (cost=0.00..6143.44 rows=13 width=11) (actual time=3.342..34.831 rows=13 loops=1)
               ->  Seq Scan on client client_1  (cost=0.00..6143.31 rows=13 width=7) (actual time=3.341..34.825 rows=13 loops=1)
                     Filter: (((prenom)::text = 'Robert'::text) AND (age = 38))
                     Rows Removed by Filter: 248941
 Planning time: 0.266 ms
 Execution time: 90.583 ms
(12 lignes)



Client Btree
                                                                     QUERY PLAN                                                                      
-----------------------------------------------------------------------------------------------------------------------------------------------------
 HashSetOp Intersect  (cost=98.28..5222.56 rows=9 width=72) (actual time=10.724..10.725 rows=4 loops=1)
   ->  Append  (cost=98.28..5222.50 rows=24 width=72) (actual time=2.593..10.700 rows=31 loops=1)
         ->  Subquery Scan on "*SELECT* 1"  (cost=98.28..2611.25 rows=12 width=11) (actual time=2.591..6.879 rows=18 loops=1)
               ->  Bitmap Heap Scan on client_btree  (cost=98.28..2611.13 rows=12 width=7) (actual time=2.590..6.875 rows=
18 loops=1)
                     Recheck Cond: (age = 38)
                     Filter: ((prenom)::text = 'Alice'::text)
                     Rows Removed by Filter: 4545
                     Heap Blocks: exact=2046
                     ->  Bitmap Index Scan on b_age  (cost=0.00..98.27 rows=4514 width=0) (actual time=1.438..1.438 rows=4563 loops=1)
                           Index Cond: (age = 38)
         ->  Subquery Scan on "*SELECT* 2"  (cost=98.28..2611.25 rows=12 width=11) (actual time=1.199..3.815 rows=13 loops=1)
               ->  Bitmap Heap Scan on client_btree client_btree_1  (cost=98.28..2611.13 rows=12 width=7) (actual time=1.198..3.811 rows=13 loops=1)
                     Recheck Cond: (age = 38)
                     Filter: ((prenom)::text = 'Robert'::text)
                     Rows Removed by Filter: 4550
                     Heap Blocks: exact=2046
                     ->  Bitmap Index Scan on b_age  (cost=0.00..98.27 rows=4514 width=0) (actual time=0.629..0.629 rows=4563 loops=1)
                           Index Cond: (age = 38)
 Planning time: 0.302 ms
 Execution time: 10.812 ms
(20 lignes)

*/



/* Question 2

    Renvoie idmag, idpro, prixunit sur les magasins qui possèdent des bureaux en stock


explain analyze SELECT idmag, idpro, prixunit
FROM   stocke NATURAL JOIN produit
WHERE  libelle = 'bureau';
                                                   QUERY PLAN                                                    
-----------------------------------------------------------------------------------------------------------------
 Hash Join  (cost=19.91..693.54 rows=1087 width=14) (actual time=0.138..6.194 rows=1031 loops=1)
   Hash Cond: (stocke.idpro = produit.idpro)
   ->  Seq Scan on stocke  (cost=0.00..539.28 rows=32928 width=14) (actual time=0.008..2.818 rows=32928 loops=1)
   ->  Hash  (cost=19.50..19.50 rows=33 width=4) (actual time=0.123..0.123 rows=33 loops=1)
         Buckets: 1024  Batches: 1  Memory Usage: 10kB
         ->  Seq Scan on produit  (cost=0.00..19.50 rows=33 width=4) (actual time=0.012..0.116 rows=33 loops=1)
               Filter: ((libelle)::text = 'bureau'::text)
               Rows Removed by Filter: 967
 Planning time: 0.172 ms
 Execution time: 6.257 ms
(10 lignes)



explain analyze SELECT idmag, produit.idpro, prixunit
FROM   stocke, produit
WHERE  libelle = 'bureau'
       AND stocke.idpro = produit.idpro; 
                                                   QUERY PLAN                                                    
-----------------------------------------------------------------------------------------------------------------
 Hash Join  (cost=19.91..693.54 rows=1087 width=14) (actual time=0.139..5.580 rows=1031 loops=1)
   Hash Cond: (stocke.idpro = produit.idpro)
   ->  Seq Scan on stocke  (cost=0.00..539.28 rows=32928 width=14) (actual time=0.007..2.588 rows=32928 loops=1)
   ->  Hash  (cost=19.50..19.50 rows=33 width=4) (actual time=0.126..0.126 rows=33 loops=1)
         Buckets: 1024  Batches: 1  Memory Usage: 10kB
         ->  Seq Scan on produit  (cost=0.00..19.50 rows=33 width=4) (actual time=0.012..0.118 rows=33 loops=1)
               Filter: ((libelle)::text = 'bureau'::text)
               Rows Removed by Filter: 967
 Planning time: 0.166 ms
 Execution time: 5.635 ms
(10 lignes)


explain analyze SELECT idmag, idpro, prixunit
FROM   stocke
WHERE  EXISTS (SELECT *
               FROM   produit
               WHERE  libelle = 'bureau'
                      AND produit.idpro = stocke.idpro);  
                                                   QUERY PLAN                                                    
-----------------------------------------------------------------------------------------------------------------
 Hash Semi Join  (cost=19.91..657.72 rows=1087 width=14) (actual time=0.136..5.521 rows=1031 loops=1)
   Hash Cond: (stocke.idpro = produit.idpro)
   ->  Seq Scan on stocke  (cost=0.00..539.28 rows=32928 width=14) (actual time=0.010..2.504 rows=32928 loops=1)
   ->  Hash  (cost=19.50..19.50 rows=33 width=4) (actual time=0.117..0.117 rows=33 loops=1)
         Buckets: 1024  Batches: 1  Memory Usage: 10kB
         ->  Seq Scan on produit  (cost=0.00..19.50 rows=33 width=4) (actual time=0.015..0.105 rows=33 loops=1)
               Filter: ((libelle)::text = 'bureau'::text)
               Rows Removed by Filter: 967
 Planning time: 0.245 ms
 Execution time: 5.592 ms
(10 lignes)


explain analyze SELECT idmag, idpro, prixunit
FROM   stocke
WHERE  idpro IN (SELECT idpro
                 FROM   produit
                 WHERE  libelle = 'bureau'); 
                                                   QUERY PLAN                                                    
-----------------------------------------------------------------------------------------------------------------
 Hash Semi Join  (cost=19.91..657.72 rows=1087 width=14) (actual time=0.135..5.542 rows=1031 loops=1)
   Hash Cond: (stocke.idpro = produit.idpro)
   ->  Seq Scan on stocke  (cost=0.00..539.28 rows=32928 width=14) (actual time=0.007..2.541 rows=32928 loops=1)
   ->  Hash  (cost=19.50..19.50 rows=33 width=4) (actual time=0.121..0.121 rows=33 loops=1)
         Buckets: 1024  Batches: 1  Memory Usage: 10kB
         ->  Seq Scan on produit  (cost=0.00..19.50 rows=33 width=4) (actual time=0.012..0.115 rows=33 loops=1)
               Filter: ((libelle)::text = 'bureau'::text)
               Rows Removed by Filter: 967
 Planning time: 0.191 ms
 Execution time: 5.602 ms
(10 lignes)


Les query plan sont très similaires voire identiques.
Cela veut dire que le query planner a réussi à simplifier les requêtes.


*/


/* Question 3 :

magasin (Mag) où il n'y a pas
		produit (Maurice) dont la couleur n'est pas nulle et il n'y a pas*
			produit tq est d'une couleur différente de Maurice et le produit est dans le stocke de Mag

Magasin qui ont un produit de chaque couleur


EXPLAIN ANALYSE
SELECT idmag FROM magasin
WHERE NOT EXISTS
	(
		SELECT * FROM produit
		WHERE couleur IS NOT NULL
		AND NOT EXISTS
			(
				SELECT *       
				FROM stocke NATURAL JOIN produit as p
				WHERE stocke.idmag = magasin.idmag
				AND p.couleur = produit.couleur
			) 
	);
                                                           QUERY PLAN                                                            
---------------------------------------------------------------------------------------------------------------------------------
 Nested Loop Anti Join  (cost=0.00..7386139.33 rows=250 width=4) (actual time=47.897..238.848 rows=437 loops=1)
   Join Filter: (NOT (alternatives: SubPlan 1 or hashed SubPlan 2))
   Rows Removed by Join Filter: 404255
   ->  Seq Scan on magasin  (cost=0.00..10.00 rows=500 width=4) (actual time=0.007..0.103 rows=500 loops=1)
   ->  Materialize  (cost=0.00..21.62 rows=923 width=5) (actual time=0.000..0.066 rows=809 loops=500)
         ->  Seq Scan on produit  (cost=0.00..17.00 rows=923 width=5) (actual time=0.006..0.162 rows=923 loops=1)
               Filter: (couleur IS NOT NULL)
               Rows Removed by Filter: 77
   SubPlan 1
     ->  Merge Join  (cost=0.56..157.38 rows=5 width=0) (never executed)
           Merge Cond: (stocke.idpro = p.idpro)
           ->  Index Only Scan using stocke_pkey on stocke  (cost=0.29..109.20 rows=66 width=4) (never executed)
                 Index Cond: (idmag = magasin.idmag)
                 Heap Fetches: 0
           ->  Index Scan using produit_pkey on produit p  (cost=0.28..47.77 rows=77 width=4) (never executed)
                 Filter: ((couleur)::text = (produit.couleur)::text)
   SubPlan 2
     ->  Hash Join  (cost=29.50..1021.54 rows=32928 width=36) (actual time=0.317..13.231 rows=32928 loops=1)
           Hash Cond: (stocke_1.idpro = p_1.idpro)
           ->  Seq Scan on stocke stocke_1  (cost=0.00..539.28 rows=32928 width=8) (actual time=0.006..3.396 rows=32928 loops=1)
           ->  Hash  (cost=17.00..17.00 rows=1000 width=9) (actual time=0.293..0.293 rows=1000 loops=1)
                 Buckets: 1024  Batches: 1  Memory Usage: 51kB
                 ->  Seq Scan on produit p_1  (cost=0.00..17.00 rows=1000 width=9) (actual time=0.005..0.134 rows=1000 loops=1)
 Planning time: 0.666 ms
 Execution time: 238.993 ms
(25 lignes)



EXPLAIN ANALYSE
SELECT idmag FROM magasin
WHERE NOT EXISTS
	(
		SELECT * FROM produit
		WHERE couleur IS NOT NULL
		AND couleur NOT IN
			(
				SELECT couleur 
				FROM stocke NATURAL JOIN produit  
				WHERE stocke.idmag = magasin.idmag
				AND couleur IS NOT NULL
			) 
	);
                                                                       QUERY PLAN                                                                       
--------------------------------------------------------------------------------------------------------------------------------------------------------
 Nested Loop Anti Join  (cost=0.00..18303426.28 rows=250 width=4) (actual time=89.298..27335.654 rows=437 loops=1)
   Join Filter: (NOT (SubPlan 1))
   Rows Removed by Join Filter: 404255
   ->  Seq Scan on magasin  (cost=0.00..10.00 rows=500 width=4) (actual time=0.007..0.401 rows=500 loops=1)
   ->  Materialize  (cost=0.00..21.62 rows=923 width=5) (actual time=0.000..0.050 rows=809 loops=500)
         ->  Seq Scan on produit  (cost=0.00..17.00 rows=923 width=5) (actual time=0.005..0.174 rows=923 loops=1)
               Filter: (couleur IS NOT NULL)
               Rows Removed by Filter: 77
   SubPlan 1
     ->  Merge Join  (cost=0.56..157.55 rows=61 width=5) (actual time=0.013..0.066 rows=11 loops=404318)
           Merge Cond: (stocke.idpro = produit_1.idpro)
           ->  Index Only Scan using stocke_pkey on stocke  (cost=0.29..109.20 rows=66 width=4) (actual time=0.004..0.007 rows=13 loops=404318)
                 Index Cond: (idmag = magasin.idmag)
                 Heap Fetches: 5064951
           ->  Index Scan using produit_pkey on produit produit_1  (cost=0.28..45.27 rows=923 width=9) (actual time=0.004..0.043 rows=177 loops=404318)
                 Filter: (couleur IS NOT NULL)
                 Rows Removed by Filter: 15
 Planning time: 0.270 ms
 Execution time: 27335.967 ms
(19 lignes)


EXPLAIN ANALYSE
SELECT idmag FROM magasin
WHERE NOT EXISTS
	(
		SELECT * FROM produit
		WHERE couleur IS NOT NULL
		AND idmag NOT IN
			(
				SELECT idmag
				FROM stocke NATURAL JOIN produit p
				WHERE p.couleur = produit.couleur
			)
	);

EXPLAIN ANALYSE
SELECT idmag FROM magasin
WHERE NOT EXISTS
        (
                SELECT * FROM produit
                WHERE couleur IS NOT NULL
                AND idmag NOT IN
                        (
                                SELECT idmag
                                FROM stocke NATURAL JOIN produit p
                                WHERE p.couleur = produit.couleur
                        )
        );
                                                          QUERY PLAN                                                           
-------------------------------------------------------------------------------------------------------------------------------
 Nested Loop Anti Join  (cost=0.00..85030746.70 rows=250 width=4) (actual time=234.145..1170137.629 rows=437 loops=1)
   Join Filter: (NOT (SubPlan 1))
   Rows Removed by Join Filter: 404255
   ->  Seq Scan on magasin  (cost=0.00..10.00 rows=500 width=4) (actual time=0.009..0.415 rows=500 loops=1)
   ->  Materialize  (cost=0.00..21.62 rows=923 width=5) (actual time=0.000..0.170 rows=809 loops=500)
         ->  Seq Scan on produit  (cost=0.00..17.00 rows=923 width=5) (actual time=0.009..0.168 rows=923 loops=1)
               Filter: (couleur IS NOT NULL)
               Rows Removed by Filter: 77
   SubPlan 1
     ->  Hash Join  (cost=20.46..708.57 rows=2535 width=4) (actual time=0.146..2.790 rows=1286 loops=404318)
           Hash Cond: (stocke.idpro = p.idpro)
           ->  Seq Scan on stocke  (cost=0.00..539.28 rows=32928 width=8) (actual time=0.002..1.145 rows=16629 loops=404318)
           ->  Hash  (cost=19.50..19.50 rows=77 width=4) (actual time=0.138..0.138 rows=77 loops=404318)
                 Buckets: 1024  Batches: 1  Memory Usage: 11kB
                 ->  Seq Scan on produit p  (cost=0.00..19.50 rows=77 width=4) (actual time=0.005..0.127 rows=77 loops=404318)
                       Filter: ((couleur)::text = (produit.couleur)::text)
                       Rows Removed by Filter: 923
 Planning time: 0.351 ms
 Execution time: 1170137.925 ms 
(19 lignes)
= 19 minutes

*/


/* Question 4

select most_common_vals, n_distinct, null_frac from pg_stats where tablename = 'client_btree' and attname = 'age';
                       most_common_vals                        | n_distinct | null_frac 
---------------------------------------------------------------+------------+-----------
 {30,31,29,28,27,32,33,26,34,25,24,35,23,36,37,22,21,38,20,39} |         41 |     0.009

select count(distinct age) from client_btree;
45

select most_common_vals, n_distinct, null_frac from pg_stats where tablename = 'client_btree' and attname = 'tel';
 most_common_vals | n_distinct | null_frac  
------------------+------------+------------
                  |  -0.990533 | 0.00946667
presques toutes les valeurs de tel sont uniques

select count(distinct tel) from client_btree;
246436


select most_common_vals, n_distinct, null_frac from pg_stats where tablename = 'client_btree' and attname = 'prenom3';
                       most_common_vals                                                                                                                                                                                                                                                                                                                                                                                                                                                             | n_distinct | null_frac 
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+------------+-----------
 {Camille,Bruce,Dominique,Zacharie,Alphonse,Timothé,Charles,Diego,Etienne,Hector,Isidore,Maxime,Queene,Simon,Eric,Fatima,Léonard,Moussa,Alexandre,Edouard,Fanny,Frédérique,Geoffrey,Ludovic,Mathis,Vivien,Yasmine,Corentin,Germain,Mélissa,Natacha,Rhada,Roger,Romane,Simone,Solène,Victoria,Youssef,Emeric,Hugues,Julien,Michel,Norbert,Patrique,Pauline,Philippe,Rodolphe,Stéphane,Susanne,Agnès,Anna,Cyndie,Françoise,Gaston,Jean,Lilya,Lucien,Marc,Marco,Marylin,Pablo,Patrice,Renaud,Séverine} |        367 |     0.768

*/


/* Question 5

Un histogramme est une liste de liste où les sous listes contiennent des valeurs et sont de tailles similaires (pas tout le temps égal)

*/

/* Question 6

show default_statistics_target;

 default_statistics_target 
---------------------------
 100

*/


/* Question 7

alter table if exists client_btree alter age set statistics 10;

analyze client_btree;

analyze verbose client_btree(age);
INFO:  analyse « public.client_btree »
INFO:  « client_btree » : 2409 pages parcourues sur 2409,
  contenant 248954 lignes à conserver et 0 lignes à supprimer,
  3000 lignes dans l'échantillon,
  248954 lignes totales estimées
ANALYZE

select most_common_vals, n_distinct, null_frac from pg_stats where tablename = 'client_btree' and attname = 'age';
        most_common_vals         | n_distinct | null_frac 
---------------------------------+------------+-----------
 {31,30,28,29,27,32,33,26,34,25} |         34 |     0.011




*/