# -- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
# --
# -- Host: localhost    Database: Leave
# -- ------------------------------------------------------
# -- Server version	10.4.32-MariaDB
#
# /*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
# /*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
# /*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
# /*!40101 SET NAMES utf8mb4 */;
# /*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
# /*!40103 SET TIME_ZONE='+00:00' */;
# /*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
# /*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
# /*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
# /*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
#
# --
# -- Table structure for table `collaborateurs`
# --
#
# DROP TABLE IF EXISTS `collaborateurs`;
# /*!40101 SET @saved_cs_client     = @@character_set_client */;
# /*!40101 SET character_set_client = utf8 */;
# CREATE TABLE `collaborateurs` (
#   `id` binary(16) NOT NULL,
#   `date_entree_projet` date NOT NULL,
#   `date_sortie_projet` date DEFAULT NULL,
#   `email` varchar(255) NOT NULL,
#   `nom` varchar(255) NOT NULL,
#   `nombre_jours_payés` double DEFAULT NULL,
#   `prenom` varchar(255) NOT NULL,
#   `role_collaborateur` enum('COLLABORATEUR','CHEF_EQUIPE') DEFAULT NULL,
#   `type_collaborateur` enum('SALARIE','INDEPENDANT') DEFAULT NULL,
#   `equipe_id` binary(16) DEFAULT NULL,
#   `niveau_id` binary(16) DEFAULT NULL,
#   PRIMARY KEY (`id`),
#   UNIQUE KEY `UK_ll6lvbf4tsp32pyka01xqwjnc` (`email`),
#   KEY `FK39b9num10i7wldiyhh7d080th` (`equipe_id`),
#   KEY `FKe7y4mea1qi8p5xg929xdko5rg` (`niveau_id`),
#   CONSTRAINT `FK39b9num10i7wldiyhh7d080th` FOREIGN KEY (`equipe_id`) REFERENCES `equipe` (`code`),
#   CONSTRAINT `FKe7y4mea1qi8p5xg929xdko5rg` FOREIGN KEY (`niveau_id`) REFERENCES `niveau` (`code`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
# /*!40101 SET character_set_client = @saved_cs_client */;
#
# --
# -- Table structure for table `conges`
# --
#
# DROP TABLE IF EXISTS `conges`;
# /*!40101 SET @saved_cs_client     = @@character_set_client */;
# /*!40101 SET character_set_client = utf8 */;
# CREATE TABLE `conges` (
#   `id_conge` binary(16) NOT NULL,
#   `date_debut` date NOT NULL,
#   `date_fin` date NOT NULL,
#   `demi_journee_matin` bit(1) DEFAULT NULL,
#   `demi_journee_soir` bit(1) DEFAULT NULL,
#   `description` varchar(255) DEFAULT NULL,
#   `nombre_jours_pris` double NOT NULL,
#   `collaborateur_email` varchar(255) DEFAULT NULL,
#   PRIMARY KEY (`id_conge`),
#   KEY `FKavfmt6587awa7fqwoagy6au6l` (`collaborateur_email`),
#   CONSTRAINT `FKavfmt6587awa7fqwoagy6au6l` FOREIGN KEY (`collaborateur_email`) REFERENCES `collaborateurs` (`email`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
# /*!40101 SET character_set_client = @saved_cs_client */;
#
# --
# -- Table structure for table `equipe`
# --
#
# DROP TABLE IF EXISTS `equipe`;
# /*!40101 SET @saved_cs_client     = @@character_set_client */;
# /*!40101 SET character_set_client = utf8 */;
# CREATE TABLE `equipe` (
#   `code` binary(16) NOT NULL,
#   `date_creation` timestamp NOT NULL DEFAULT current_timestamp(),
#   `description` varchar(255) DEFAULT NULL,
#   `nom` varchar(255) DEFAULT NULL,
#   PRIMARY KEY (`code`),
#   UNIQUE KEY `UK_tmpaatkvnqxaa1xpqfah1xhg9` (`nom`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
# /*!40101 SET character_set_client = @saved_cs_client */;
#
# --
# -- Table structure for table `exercice`
# --
#
# DROP TABLE IF EXISTS `exercice`;
# /*!40101 SET @saved_cs_client     = @@character_set_client */;
# /*!40101 SET character_set_client = utf8 */;
# CREATE TABLE `exercice` (
#   `id_exercice` binary(16) NOT NULL,
#   `annee` int(11) NOT NULL,
#   `created_date` date DEFAULT NULL,
#   `last_updated_date` date DEFAULT NULL,
#   `nombre_jours_ouvres` int(11) NOT NULL,
#   PRIMARY KEY (`id_exercice`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
# /*!40101 SET character_set_client = @saved_cs_client */;
#
# --
# -- Table structure for table `jour_ferie`
# --
#
# DROP TABLE IF EXISTS `jour_ferie`;
# /*!40101 SET @saved_cs_client     = @@character_set_client */;
# /*!40101 SET character_set_client = utf8 */;
# CREATE TABLE `jour_ferie` (
#   `id` binary(16) NOT NULL,
#   `date_debut` date NOT NULL,
#   `date_fin` date NOT NULL,
#   `description` varchar(255) DEFAULT NULL,
#   `is_fixe` bit(1) DEFAULT NULL,
#   PRIMARY KEY (`id`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
# /*!40101 SET character_set_client = @saved_cs_client */;
#
# --
# -- Table structure for table `niveau`
# --
#
# DROP TABLE IF EXISTS `niveau`;
# /*!40101 SET @saved_cs_client     = @@character_set_client */;
# /*!40101 SET character_set_client = utf8 */;
# CREATE TABLE `niveau` (
#   `code` binary(16) NOT NULL,
#   `date_creation` timestamp NOT NULL DEFAULT current_timestamp(),
#   `description` varchar(255) DEFAULT NULL,
#   `nom` varchar(255) DEFAULT NULL,
#   PRIMARY KEY (`code`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
# /*!40101 SET character_set_client = @saved_cs_client */;
#
# --
# -- Table structure for table `solde_conge`
# --
#
# DROP TABLE IF EXISTS `solde_conge`;
# /*!40101 SET @saved_cs_client     = @@character_set_client */;
# /*!40101 SET character_set_client = utf8 */;
# CREATE TABLE `solde_conge` (
#   `id` binary(16) NOT NULL,
#   `solde_consomme` double NOT NULL,
#   `solde_initial` double NOT NULL,
#   `solde_restant` double NOT NULL,
#   `collaborateur_email` varchar(255) DEFAULT NULL,
#   `exercice_id` binary(16) NOT NULL,
#   PRIMARY KEY (`id`),
#   KEY `FKmlxxthu6l5qngftqcojxc93mu` (`collaborateur_email`),
#   KEY `FKa0cagpahnqema47xu6fc36inm` (`exercice_id`),
#   CONSTRAINT `FKa0cagpahnqema47xu6fc36inm` FOREIGN KEY (`exercice_id`) REFERENCES `exercice` (`id_exercice`),
#   CONSTRAINT `FKmlxxthu6l5qngftqcojxc93mu` FOREIGN KEY (`collaborateur_email`) REFERENCES `collaborateurs` (`email`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
# /*!40101 SET character_set_client = @saved_cs_client */;
# /*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
#
# /*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
# /*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
# /*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
# /*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
# /*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
# /*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
# /*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
#
# -- Dump completed on 2024-07-19 11:12:05
