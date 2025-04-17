## Question 3 :

a -> la varibale *x* n'est pas initialisé / définit

b -> cela signifie qu'il manque un include

include assemble le contenue du fichier a et b lors de la pré-compilation

## Question 4 :

g++ -std=c++17 -o links main.cpp
/usr/bin/ld : /tmp/ccTWeYDR.o : dans la fonction « main » :
main.cpp:(.text+0x5) : référence indéfinie vers « print_hello() »
collect2: error: ld returned 1 exit status

Cela signifie qu'il n'y a pas de lien entre le main et la fonction_hello() trouvable avec cette compilation. Le prototype est connue mais pas la fonction en elle même

## Question 5 :

g++ -std=c++17 -o links main.cpp utils.hpp utils.cpp
/usr/bin/ld : /tmp/ccRajgWA.o : dans la fonction « print_bye() » :
utils.cpp:(.text+0x0) : définitions multiples de « print_bye() »; /tmp/ccQahvcC.o:main.cpp:(.text+0x0) : défini pour la première fois ici
collect2: error: ld returned 1 exit status

c'est une erreur de link

elle se produit parce que à la précompilation on va avoir le prototype de la fonction et la fonction en elle même

il faudrait mettre uniquement les prototypes dans les fichiers *.hpp et les définition dans les *.cpp ou alors mettre les mots clés inline dans la définition de la fonction si elle se trouve dans le *.hpp

