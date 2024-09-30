/* Question 2:

    Un index est créé automatiquement sur la clé primaire
    
    explain select * from client where numcli = 0;

    On observe que c'est un Index Scan qui utilise la clé primaire

*/

/* Question 3-4:

explain select * from client;
                          QUERY PLAN                           
---------------------------------------------------------------
 Seq Scan on client  (cost=0.00..2866.71 rows=45771 width=386)
(1 ligne)


analyze;


explain select * from client;
                          QUERY PLAN                           
---------------------------------------------------------------
 Seq Scan on client  (cost=0.00..4898.54 rows=248954 width=54)
(1 ligne)


cost    : estimation du cout de la commande
rows    :
width   :  

*/

/* Question 5:

create index b_nom on client_btree using btree (age);
create index b_nom on client_btree using btree (te

*/


/* Question 7 :

select age, count(age) from client
group by age
order by count(age)
desc limit 10;

explain select * from client where age = 30;

client : (cost=0.00..2981.14 rows=229 width=386)
b_tree : (cost=432.27..3086.18 rows=19593 width=54)
hash   : (cost=653.02..3302.37 rows=19228 width=54)

*/

/*Question 8 :

select tel, count(tel) from client
group by tel
order by count(tel)
desc limit 10;

explain select * from client where tel = '0112555655';

client : (cost=0.00..5520.93 rows=1 width=54)
b_tree : (cost=0.42..8.44 rows=1 width=54)
hash   : (cost=0.00..8.02 rows=1 width=54)

*/


/* Question 9 :

explain select * from client where age > 25 and age < 35;

même coût pour les 3 tables car Seq Scan

hash et btree ne sont pas optimiser pour les intervalles
*/


/* Question 10 :

explain select * from client where age is null;

hash n'aime pas les nulls et utilise Seq Scan
btree utilise l'index

*/


/* Question 11 :

explain select numcli from client_hash where age is not null;

seq scan pour les 3 tables

*/


/* Question 12


select * from client where age = 30
intersect
select * from client where tel = '0112555655';





---------------------basique
 HashSetOp Intersect  (cost=0.00..11627.93 rows=1 width=390)
   ->  Append  (cost=0.00..11237.21 rows=19536 width=390)
         ->  Subquery Scan on "*SELECT* 2"  (cost=0.00..5520.94 rows=1 width=58)
               ->  Seq Scan on client  (cost=0.00..5520.93 rows=1 width=54)
                     Filter: ((tel)::text = '0112555655'::text)
         ->  Subquery Scan on "*SELECT* 1"  (cost=0.00..5716.28 rows=19535 width=58)
               ->  Seq Scan on client client_1  (cost=0.00..5520.93 rows=19535 width=54)
                     Filter: (age = 30)


------------------ btree
 HashSetOp Intersect  (cost=0.42..3682.44 rows=1 width=390)
   ->  Append  (cost=0.42..3290.56 rows=19594 width=390)
         ->  Subquery Scan on "*SELECT* 2"  (cost=0.42..8.45 rows=1 width=58)
               ->  Index Scan using b_tel on client_btree  (cost=0.42..8.44 rows=1 width=54)
                     Index Cond: ((tel)::text = '0112555655'::text)
         ->  Subquery Scan on "*SELECT* 1"  (cost=432.27..3282.11 rows=19593 width=58)
               ->  Bitmap Heap Scan on client_btree client_btree_1  (cost=432.27..3086.18 rows=19593 width=54)
                     Recheck Cond: (age = 30)
                     ->  Bitmap Index Scan on b_age  (cost=0.00..427.37 rows=19593 width=0)
                           Index Cond: (age = 30)
(10 lignes)



---------------------------hash
 HashSetOp Intersect  (cost=0.00..3887.25 rows=1 width=390)
   ->  Append  (cost=0.00..3502.67 rows=19229 width=390)
         ->  Subquery Scan on "*SELECT* 2"  (cost=0.00..8.03 rows=1 width=58)
               ->  Index Scan using h_nom on client_hash  (cost=0.00..8.02 rows=1 width=54)
                     Index Cond: ((tel)::text = '0112555655'::text)
         ->  Subquery Scan on "*SELECT* 1"  (cost=653.02..3494.65 rows=19228 width=58)
               ->  Bitmap Heap Scan on client_hash client_hash_1  (cost=653.02..3302.37 rows=19228 width=54)
                     Recheck Cond: (age = 30)
                     ->  Bitmap Index Scan on h_age  (cost=0.00..648.21 rows=19228 width=0)
                           Index Cond: (age = 30)
(10 lignes)


l'index hash est le plus rapide en utilisant ses index, btree est très prochaine
mais on observe que sans indice dans la table client la recherche est 3x plus longue

*/



/* Question 13 :


explain select * from client where age between 20 and 30;

*/