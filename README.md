# DriveEasy — Plateforme de Location de Véhicules (Backend Microservices)

Système de gestion et de réservation de véhicules en architecture **microservices**, développé avec **Spring Boot 3.4.5**, **Spring Cloud (2024.0.2)** et **Java 17/21**.

Projet conforme au cahier des charges : stockage **100% en RAM** (InMemory ConcurrentHashMap), sans base de données, avec découverte dynamique, configuration centralisée, passerelle d'accès et traçage distribué.

---

## 1. Architecture et Ports

| Composant | Technologie | Port | Rôle |
| :--- | :--- | :---: | :--- |
| **Service Registry** | Spring Cloud Netflix Eureka | **8761** | Découverte dynamique & annuaire des services |
| **Config Server** | Spring Cloud Config Server | **8888** | Configuration centralisée (profil `native`) |
| **Vehicule Service** | Spring Boot (Web) | **8081** | Gestion du parc de véhicules & disponibilités |
| **Client Service** | Spring Boot (Web) + Feign | **8082** | Gestion des clients & calcul d'éligibilité |
| **Location Service** | Spring Boot (Web) + Feign | **8083** | Cycle de vie des locations, devis & pénalités |
| **API Gateway** | Spring Cloud Gateway | **8080** | Point d'entrée unique, Load Balancing & CORS |
| **Monitoring** | Zipkin (Docker optionnel) | **9411** | Traçage distribué des requêtes (Brave / Micrometer) |

---

## 2. Prérequis

- **Java JDK 17 ou 21** (recommandé : Eclipse Adoptium Temurin 17+)
- **Gradle 9+** (inclus via le wrapper `gradlew.bat` / `./gradlew`)
- **Docker** (optionnel, pour Zipkin)

---

## 3. Compilation & Exécution des Tests

Toutes les commandes peuvent être exécutées depuis le dossier racine du projet (`demo1`) :

### Compiler le projet
```powershell
.\gradlew.bat compileJava
```

### Exécuter la suite de tests unitaires
Des tests couvrant les règles métiers (éligibilité > 21 ans et permis valide, unicité, unicité des réservations, calcul des remises fidélité/VIP, pénalités de retard) sont configurés :
```powershell
.\gradlew.bat test
```

### Générer les packages exécutables (.jar)
```powershell
.\gradlew.bat assemble
```

---

## 4. Lancement de la Plateforme

### Méthode 1 : Lancement en un clic (Recommandé sous Windows)
Exécutez le script automatique qui lance les 6 microservices dans l'ordre préconisé :
```cmd
run-all.bat
```
*(ou via PowerShell : `.\run-all.ps1`)*

Pour tout arrêter proprement :
```cmd
stop-all.bat
```

### Méthode 2 : Lancement manuel dans l'ordre préconisé

Ouvrez un terminal par service dans l'ordre suivant :

1. **Service Registry (Eureka)**
   ```powershell
   java -jar service-registry/build/libs/service-registry-0.0.1-SNAPSHOT.jar
   ```
   *Vérifier sur [http://localhost:8761](http://localhost:8761)*

2. **Config Server** (attendre 15 secondes)
   ```powershell
   java -jar config-server/build/libs/config-server-0.0.1-SNAPSHOT.jar
   ```
   *Vérifier sur [http://localhost:8888/vehicule-service/default](http://localhost:8888/vehicule-service/default)*

3. **Services Métier**
   ```powershell
   java -jar vehicule-service/build/libs/vehicule-service-0.0.1-SNAPSHOT.jar
   java -jar client-service/build/libs/client-service-0.0.1-SNAPSHOT.jar
   java -jar location-service/build/libs/location-service-0.0.1-SNAPSHOT.jar
   ```

4. **API Gateway**
   ```powershell
   java -jar api-gateway/build/libs/api-gateway-0.0.1-SNAPSHOT.jar
   ```

5. **(Optionnel) Zipkin Tracing via Docker**
   ```powershell
   docker compose up -d
   ```
   *Accessible sur [http://localhost:9411](http://localhost:9411)*

---

## 5. Endpoints de l'API (via la Gateway : port 8080)

Le frontend et les clients accèdent à toutes les ressources via l'API Gateway (`http://localhost:8080`) :

### 🚗 Véhicules (`/api/vehicules/**`)
- `GET /api/vehicules` : Liste des véhicules (avec filtres : `categorie`, `carburant`, `statut`, `prixMax`)
- `GET /api/vehicules/disponibles` : Véhicules avec statut `DISPONIBLE`
- `GET /api/vehicules/{id}` : Détails d'un véhicule
- `POST /api/vehicules` : Ajouter un véhicule
- `PUT /api/vehicules/{id}` : Mettre à jour un véhicule
- `DELETE /api/vehicules/{id}` : Supprimer un véhicule (refusé si loué)
- `PATCH /api/vehicules/{id}/statut` : Mettre à jour le statut (`DISPONIBLE`, `LOUE`, `MAINTENANCE`)

### 👤 Clients (`/api/clients/**`)
- `GET /api/clients` : Recherche de clients (par `nom`, `cin`, `email`)
- `GET /api/clients/{id}` : Fiche détaillée d'un client
- `POST /api/clients` : Créer un client
- `PUT /api/clients/{id}` : Mettre à jour un client
- `DELETE /api/clients/{id}` : Supprimer un client (refusé si location active)
- `GET /api/clients/{id}/eligibilite` : Vérifier l'éligibilité (Âge ≥ 21 ans, permis non expiré)
- `PATCH /api/clients/{id}/categorie` : Modifier la catégorie (`STANDARD`, `FIDELE`, `VIP`)

### 📋 Locations (`/api/locations/**`)
- `GET /api/locations` : Liste paginée des locations (filtres `statut`, `client`, `vehicule`, `dateDebut`, `dateFin`)
- `GET /api/locations/{id}` : Détail enrichi d'une location (avec snapshots client & véhicule)
- `POST /api/locations` : Créer une réservation (vérification éligibilité + disponibilité)
- `POST /api/locations/simulation` : Simulation du prix sans création
- `PATCH /api/locations/{id}/depart` : Démarrer la location (passe le statut à `EN_COURS` et le véhicule à `LOUE`)
- `PATCH /api/locations/{id}/retour` : Retour du véhicule (kilométrage, pénalité de retard, libération véhicule à `DISPONIBLE`, mise à jour fidélité)
- `PATCH /api/locations/{id}/annulation` : Annuler une réservation
- `GET /api/locations/statistiques` : Chiffre d'affaires, taux d'occupation et top véhicules

---

## 6. Données de Démonstration

Au démarrage de `vehicule-service` et `client-service`, des chargeurs de données en mémoire injectent automatiquement des enregistrements initiaux réalistes :
- **4 véhicules** : Renault Clio (Compacte - Essence), Peugeot 3008 (SUV - Hybride), Volkswagen Golf 8 (Berline - Diesel), Tesla Model 3 (Luxe - Electrique).
- **3 clients** : Profils variés (client Standard de 28 ans, client Fidèle de 35 ans, et profil jeune / permis spécifique pour tester l'éligibilité).
