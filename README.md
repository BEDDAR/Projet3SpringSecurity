## **Introduction** :
Ce projet implémente une application web de mise en relation entre locataires et propriétaires.basée sur les couches Controller, Service, Repository, et Security. Il utilise Spring Boot, MySQL, et suit les bonnes pratiques de développement pour garantir modularité, maintenabilité et sécurité.

## **Prérequis** :
Avant de commencer, assurez-vous d'avoir installé les outils suivants :
- **Java JDK 17+**
- **Maven 3.8+**
- **MySQL Server**
- **Postman** (ou tout autre outil API pour tester les endpoints)

## **Installation et Lancement**

### **Étape 1 : Cloner le dépôt**
```bash
git clone https://github.com/BEDDAR/Projet3Openclassrooms.git
cd Projet3Openclassrooms
```

### **Étape 2 : Configurer l'application**

## **Base de données** :
Modifiez le fichier `application.properties` dans `src/main/resources` pour configurer les paramètres de connexion à votre base de données :
```properties
spring.application.name=rental

# Configuration du serveur
server.port=3001
server.servlet.context-path=/api
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

spring.datasource.url=jdbc:mysql://localhost:3306/nom_de_votre_base
spring.datasource.username=votre_utilisateur
spring.datasource.password=votre_mot_de_passe
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### **Étape 3 : Installer les dépendances**
Utilisez Maven pour télécharger les dépendances nécessaires :
```bash
mvn clean install
```

### **Étape 4 : Lancer le projet**
Démarrez l'application avec Maven :
```bash
mvn spring-boot:run
```

## **Swagger** :
Une fois l'application démarrée, accédez à la documentation via l'URL suivante :
[http://localhost:3001/api/swagger-ui/index.html#/](http://localhost:3001/api/swagger-ui/index.html#/)

## **Fonctionnalités principales** :
- Architecture modulaire : Controller, Service, Repository, Security.
- Gestion des utilisateurs avec MySQL comme base de données.
- Authentification et autorisation avec Spring Security.
- Documentation interactive avec Swagger.
- Persistance des données avec JPA et Hibernate.
