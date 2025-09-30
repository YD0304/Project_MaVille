# MaVille

## Description du Projet 
`MaVille` est une application de simulation en ligne de commande conçue pour gérer les signalements de problèmes civils et les projets de travaux publics dans la ville de Montréal. Elle facilite l'interaction entre trois types d'utilisateurs : les **Résidents**, qui signalent les problèmes et suivent les travaux ; les **Prestataires de services**, qui soumissionnent sur des projets pour résoudre ces problèmes ; et le **STPM** (Service Technique de la Ville), qui supervise, priorise et approuve les projets. L'application utilise des données initiales pour simuler un environnement de travail réaliste et est entièrement contrôlée via le terminal via command-line interface (CLI)) ou le web via graphical user interface (GUI). 

---

## Fonctionnalités 

L'application offre des menus et des fonctionnalités spécifiques pour chaque profil d'utilisateur.

### Profil Résident
* **Signaler un problème**: Permet aux résidents de signaler un nouveau problème.
* **Consulter les travaux**: Offre deux options de consultation :
    * Une option pour appeler l'**API externe** de la Ville de Montréal et voir les travaux publics réels, avec des options de filtrage.
    * Une option pour consulter les projets internes gérés par l'application.
* **Gérer les notifications**: Un système de notification et d'abonnement permet aux résidents de recevoir des alertes et de gérer leurs abonnements.

### Profil Prestataire 
* **Sélection de profil**: Permet à l'utilisateur de choisir parmi une liste de prestataires prédéfinis pour agir en leur nom.
* **Consulter les projets disponibles**: Affiche une liste de projets en attente de soumission.
* **Soumettre une candidature**: Permet à un prestataire de soumissionner pour un projet disponible.
* **Mettre à jour un projet**: Une fois une candidature acceptée, le prestataire peut mettre à jour le statut de son projet.

### Profil STPM 
* **Visualiser et prioriser les problèmes**: Le STPM peut voir tous les problèmes signalés et leur affecter un niveau de priorité.
* **Évaluer les candidatures**: Le STPM peut consulter les candidatures soumises et choisir de les "Approuver" ou de les "Refuser".
  
---

## Structure du Projet 

Le projet est organisé selon une architecture à couches pour séparer les responsabilités :
* `ca.udem.maville.cli`: Contient l'interface utilisateur en ligne de commande (CLI). Cette couche permet aux utilisateurs d’interagir avec l’application via le terminal.
* `ca.udem.maville.gui`: Contient l’interface graphique (GUI) permettant une interaction visuelle plus conviviale. Elle inclut les fenêtres, panneaux et composants graphiques.
* `ca.udem.maville.metier`: Regroupe la logique métier de l’application, souvent appelée les « Services ». Cette couche orchestre les règles métier, les traitements et la coordination entre les données et la présentation.
* `ca.udem.maville.donnees`: Contient les DAO (Data Access Objects), qui gèrent la lecture, l’écriture et la persistance des données. Elle encapsule les interactions avec les fichiers JSON ou d’autres sources de données.
* `ca.udem.maville.model`: La couche de modèle, qui définit les structures de données (classes modèles) utilisées dans tout le projet,
* `ca.udem.maville.api`: Regroupe les classes pour la communication avec des services externes ou pour exposer une API REST (serveur et client).
* `ca.udem.maville.Main`: La classe principale qui initialise l’application. Elle configure les différentes couches selon le mode choisi (CLI, GUI, serveur) et démarre le système.


---

## Comment Utiliser 

Ce projet est géré par **Apache Maven**. Vous n'avez plus besoin d'utiliser `javac` manuellement.

### Prérequis 
* **Java Development Kit (JDK)**: Version 17 ou supérieure.
* **Apache Maven**: Doit être installé et configuré dans les variables d'environnement de votre système.

### Compilation 
1.  Ouvrez un terminal (ou PowerShell).
2.  Naviguez vers le **répertoire racine** de votre projet (le dossier qui contient le fichier `pom.xml`).
3.  Exécutez la commande Maven suivante :
    ```bash
    mvn clean compile
    ```

### Exécution 
1.  Assurez-vous d'être toujours dans le **répertoire racine** du projet.
2.  Exécutez la commande suivante pour lancer le serveur
    Rester dans cette étape pour GUI interface (GUI lance avec la demarage du serveur) :
    ```bash
    mvn exec:java -Dexec.mainClass="ca.udem.maville.Main"
    ```
4. Ensuite exécutez la commande suivante pour lancer l'application CLI interface) :
    ```bash
    mvn exec:java -Dexec.mainClass="ca.udem.maville.Main" -Dexec.args="--cli"
    ```
### Exemple d'utilisation de la fonctionnalité API 
Pour tester la fonctionnalité qui appelle l'API externe de la Ville de Montréal, suivez ces étapes :
1.  Lancez l'application avec la commande `mvn exec:java...` ci-dessus.
2.  Au menu principal, choisissez l'option **`1`** pour le `Profil Résident`.
3.  L'application vous présentera une liste de résidents. Choisissez-en un (par exemple, entrez **`1`**).
4.  Dans le menu résident, choisissez l'option **`1`** pour `Consulter les travaux en cours (API Ville de Montréal)`.
5.  L'application vous proposera des options de recherche. Choisissez **`1`** pour lister les travaux les plus récents.
6.  Le système affichera "Chargement des données..." puis la liste des travaux récupérés directement depuis l'API externe.

## Tests et Rapport de Couverture de Code
Nous avons intégré JaCoCo, un outil standard de l'industrie, pour mesurer l'efficacité de nos tests unitaires et garantir la qualité du code.

### **Comment Générer le Rapport JaCoCo**

Pour générer vous-même le rapport de couverture de test, exécutez la commande Maven suivante à la racine du projet :


```bash
mvn clean verify
```

Cette commande exécute l'ensemble du cycle de vie de construction, y compris la phase de test où les données de couverture sont collectées. Une fois la commande terminée avec succès (`BUILD SUCCESS`), le rapport sera généré.

### **Comment Consulter le Rapport**

Le rapport de couverture de test est un site web HTML interactif. Vous pouvez l'ouvrir avec n'importe quel navigateur web.

- **Emplacement du rapport** : Le rapport se trouve dans le dossier `target` (qui est généré par la commande ci-dessus), à l'emplacement suivant :
  ```
  target/site/jacoco/index.html
  ```

### **Analyse du Rapport de Couverture Actuel**

Le rapport généré montrera une couverture de test pour l'ensemble du projet. Voici une brève analyse de l'état actuel :

ca.udem.maville.api.client (environ 12% de couverture) : La couverture a progressé et confirme que nos tests portent principalement sur cette couche, validant les interactions avec 
les API externes, ce qui est un point central de l’application.

ca.udem.maville.metier (autour de 28%) et ca.udem.maville.donnees (environ 11%) : Une amélioration notable par rapport aux versions précédentes montre que des tests commencent à couvrir la logique métier et la couche d’accès aux données, qui sont des composants essentiels.

ca.udem.maville.model (environ 38%) : Cette progression suggère que des tests sont en place pour les classes de modèle, garantissant la validité des structures de données manipulées.

Conclusion : L’infrastructure de tests est fonctionnelle et les résultats montrent un progrès réel, bien qu’une couverture globale de 8% reste insuffisante pour garantir la robustesse complète de l’application. Les prochaines étapes devront viser à étendre la couverture aux couches GUI et CLI, ainsi qu’à renforcer encore les tests sur les parties métier et données.
---
---

## Comment Lancer avec Docker 
Ce projet est entièrement conteneurisé avec Docker, ce qui simplifie grandement son déploiement.

1.  **Prérequis**
    - Assurez-vous d'avoir [Docker](https://www.docker.com/products/docker-desktop/) installé sur votre machine.

2.  **Construisez l'image Docker :**
    À la racine du projet, exécutez la commande suivante pour construire l'image :
    ```bash
    docker build -t maville-app .
    ```

3.  **Lancez le conteneur Docker :**
    Une fois l'image construite, lancez un conteneur avec cette commande :
    ```bash
    docker run -d -p 7070:7070 --name maville-container maville-app
    ```

4.  **Accédez à l'application :**
    Ouvrez votre navigateur web et allez à l'adresse [http://localhost:7070](http://localhost:7070).

⚠️ Gestion des Ports
Avant de lancer le conteneur, assurez-vous qu’aucun autre processus n’utilise le port 7070.
Si le port est déjà occupé, vous pouvez :
Identifier le processus utilisant le port :
 ```bash
lsof -i :7070
```
Terminer le processus (remplacez <PID> par l’identifiant affiché) :

```bash
kill -9 <PID>
```
Relancer l’étape 2.

Si le port est occupé par un ancien conteneur Docker, utilisez :
```bash
docker stop maville-container
docker rm maville-container
```
Ensuite, relancez le conteneur.
