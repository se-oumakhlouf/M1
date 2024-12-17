# Compte-rendu TP-Noté Groupe 2 - Merwan GHERMAOUI - Selym OUMAKHLOUF

## Exercice 1 :

On a 
* `x1 = 2`
* `x2 = 8`

## Exercice 2 :

* ```txt
    Pour max -2x1+x2 :
    Value of objective function: 10.00000000

    Actual values of the variables:
    x1                              0
    x2                             10

    ```

* ```txt
    Le problème possède un optimum non-borné
    ```

## Exercice 3 :

```txt
Value of objective function: 3250.00000000

Actual values of the variables:
x                              15
y                              10
```

## Exercice 4/5 :

```txt
Pour rédiger ce code, nous avons fait une approche en 3 étapes : 

-> Dans la 1re étape on lit le fichier txt et on convertit sous forme de liste. On récupère la première ligne pour les ressources. pour la deuxième ligne on récupère toutes les limites sur les ressources en parcourant les lignes du fichiers. Les n lignes suivantes sont les descriptions produits.

-> Dans la 2ème étape, On génère le fichier lp en convertissant ce que l'on a récupéré en rajoutant les caractères nécessaires pour respecter la syntaxe lp

-> Dans la 3ème et dernière étape, on implémente le main qui appelle les fonctions nécessaires pour générer le lp et fais appel a lp_solve via le module subprocess.
```


```txt
Pour l'option -int on a ajouter une fonction qui recupere les produits et les rajoutes avec un int avant et un ; apres pour respecter la syntaxe puis nous introduisons un booleen qui lorsqu'il est a True lance cette méthode sinon on reste sur le code d'origine.
```
```py
    try:
        subprocess.run(["lp_solve", "-t", "-SO", output_file], check=True)
    except FileNotFoundError:
        print("Erreur : lp_solve n'est pas installé ou n'est pas dans le PATH.")
        sys.exit(1)
    except subprocess.CalledProcessError as e:
        print("Erreur lors de l'exécution de lp_solve.")
        sys.exit(1)
```

## Exercice 6 : 

* Optimum : `21654.79332331`

* 6 produit différents

* ```txt
    real 0m0,020s
    user 0m0,013s
    sys 0m0,006s
    ```


* Il faut 1 itération.

```sh
lp_solve -piv0 -t -SO output2.lp 
Simplex pivots: 44

lp_solve -piv1 -t -SO output2.lp 
Simplex pivots: 16

lp_solve -piv2 -t -SO output2.lp 
Simplex pivots: 14

lp_solve -piv3 -t -SO output2.lp 
Simplex pivots: 16

```
    Pour changer le critère de pivot, il faut rajouter cette option :

```sh
    -piv <rule>	specify simplex pivot rule
        -piv0: Select first
        -piv1: Select according to Dantzig
        -piv2: Select Devex pricing from Paula Harris (default)
        -piv3: Select steepest edge
```



## Exercice 7 :


* ```txt
    Bénefice optimale : 21638.00000000
    ```
* ```txt
    Nbre produit différent 12
    ```



* ```txt
    real	0m19,381s
    user	0m19,371s
    sys	0m0,001s
    ```

```txt
Pour les contraintes d'intégralités, on a un problème car lp_solve utilise l'algorithme 'branch-and-bound' qui a une compléxité exponentielle. C'est pourquoi le temps d'éxecution est long.
```

## Exercice 8 :

```sh
python3 generic.py data/bigdata.txt outputBig.lp
Branch & Bound depth: 1
Nodes processed: 0
Simplex pivots: 1245
Number of equal solutions: 1

lp_solve -piv0 -t -SO outputBig.lp 
Branch & Bound depth: 1
Nodes processed: 0
Simplex pivots: 44337
Number of equal solutions: 1

lp_solve -piv1 -t -SO outputBig.lp 
Branch & Bound depth: 1
Nodes processed: 0
Simplex pivots: 1016
Number of equal solutions: 1

lp_solve -piv2 -t -SO outputBig.lp 
Branch & Bound depth: 1
Nodes processed: 0
Simplex pivots: 1245
Number of equal solutions: 1

lp_solve -piv3 -t -SO outputBig.lp 
Branch & Bound depth: 1
Nodes processed: 0
Simplex pivots: 865
Number of equal solutions: 1
```