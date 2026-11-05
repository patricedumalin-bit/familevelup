# FamiLevelUp 🏰 (Slydroid Galaxy Ecosystem)

**FamiLevelUp** est une application Android moderne de gestion ludique des tâches familiales, des quêtes et des récompenses, conçue avec **Jetpack Compose** et **Material 3**. Elle fait partie intégrante de l'écosystème **Slydroid Galaxy**, partageant le même tronc technique et les mêmes standards d'excellence que **FamiDocs**.

---

## 🚀 Socle Technique & Architecture

L'application repose sur les fondations robustes du socle commun Slydroid Galaxy :
- **UI Framework :** Jetpack Compose, Material 3, Dynamic Themes (`ThemeManager` multi-thèmes : *Ocean*, *Nature*, *Zen*, *Creative*, *Sakura*).
- **Sécurité & Chiffrement :**
  - Chiffrement local AES-256-GCM via **Android Keystore**.
  - Authentification biométrique renforcée (`BiometricPrompt` pour le Mode Parent).
  - Protection anti-capture (`FLAG_SECURE` sur la `MainActivity`).
- **Facturation & Licences :** Intégration de la *Google Play Billing Library* pour les licences uniques.
- **Tâches de fond :** Utilisation de **WorkManager** pour les rappels et la synchronisation des quêtes.

---

## ✨ Fonctionnalités Clés

### 🛡️ Espace Parent
- **Gestion du Foyer & Profils :** Configuration des enfants, des multiplicateurs de récompenses et choix du type de gain (*Argent de poche en Euros 💶* ou *Bonbons magiques 🍬*).
- **Création de Quêtes & Raids :** Quêtes quotidiennes, hebdomadaires, ou "Boss Raids" familiaux coopératifs.
- **Boutique Personnalisable :** Ajout et gestion de récompenses sur-mesure.
- **Paramètres Galaxy :** Personnalisation du thème visuel global.

### ⚔️ Espace Enfant (Gamification)
- **Tableau de Quêtes :** Accomplissement de missions pour gagner des Pièces d'or et de l'XP.
- **Titres Honorifiques :** Évolution des titres selon le niveau (*Apprenti Héros 🛡️*, *Chevalier du Foyer ⚔️*, *Mage de l'Ordre 🧙‍♂️*, *Seigneur des Tâches 👑*, *Légende Vivante 🌟*).
- **Trophées & Badges Interactifs :** Trophées grisés tant qu'ils ne sont pas débloqués, et pop-up d'inspection détaillée au clic.
- **Avatar Personnalisable :** Sélecteur rapide d'avatar par émojis.

---

## 🛠️ Compilation & Déploiement

1. Ouvrez le projet dans **Android Studio** (AGP 9.4+, Gradle 8.x+, Kotlin 2.0+).
2. Lancez une synchronisation Gradle.
3. Exécutez la tâche de compilation ou déployez directement sur votre appareil :
   ```bash
   ./gradlew assembleDebug
   ```
