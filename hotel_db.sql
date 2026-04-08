-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : jeu. 09 avr. 2026 à 01:38
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `hotel_db`
--

-- --------------------------------------------------------

--
-- Structure de la table `administrateur`
--

CREATE TABLE `administrateur` (
  `id` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `chambre`
--

CREATE TABLE `chambre` (
  `id` varchar(50) NOT NULL,
  `numero` varchar(20) DEFAULT NULL,
  `etat` varchar(20) DEFAULT NULL,
  `type_chambre_id` varchar(50) DEFAULT NULL,
  `hotel_id` varchar(50) DEFAULT NULL,
  `prix` double DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `chambre`
--

INSERT INTO `chambre` (`id`, `numero`, `etat`, `type_chambre_id`, `hotel_id`, `prix`) VALUES
('d4ccaa97-536a-46a9-a887-f28fc68a6652', '1', 'OCCUPEE', '4a25229d', 'uuid-001', 0);

-- --------------------------------------------------------

--
-- Structure de la table `client`
--

CREATE TABLE `client` (
  `id` varchar(50) NOT NULL,
  `nationalite` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `etat`
--

CREATE TABLE `etat` (
  `etat` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `etat`
--

INSERT INTO `etat` (`etat`) VALUES
('DISPONIBLE'),
('HORS_SERVICE'),
('OCCUPEE');

-- --------------------------------------------------------

--
-- Structure de la table `hotel`
--

CREATE TABLE `hotel` (
  `id` varchar(50) NOT NULL,
  `nom` varchar(100) DEFAULT NULL,
  `ville` varchar(100) DEFAULT NULL,
  `adresse` varchar(150) DEFAULT NULL,
  `categorie` varchar(50) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `statut` varchar(20) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `hotel`
--

INSERT INTO `hotel` (`id`, `nom`, `ville`, `adresse`, `categorie`, `description`, `telephone`, `email`, `statut`, `image`) VALUES
('1c883b05-80f4-4d01-b604-106b58a9a77d', 'Palm Beach ', 'Saly Portudal', 'Saly', '4 Etoiles', 'relax', '000000000', 'palm@hotel.com', 'ACTIF', 'palm.jpg'),
('uuid-001', 'Royal Beach Horizon', 'La Somone', 'Somone', '4 Etoiles', 'ras', '770000001 ', 'RHB@hotel.com', 'ACTIF', 'royal.jpg');

-- --------------------------------------------------------

--
-- Structure de la table `receptionniste`
--

CREATE TABLE `receptionniste` (
  `id` varchar(50) NOT NULL,
  `hotel_id` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `reservation`
--

CREATE TABLE `reservation` (
  `id` varchar(50) NOT NULL,
  `numReservation` varchar(50) DEFAULT NULL,
  `dateArrive` date DEFAULT NULL,
  `dateDepart` date DEFAULT NULL,
  `nbPersonne` int(11) DEFAULT NULL,
  `montantTotal` float DEFAULT NULL,
  `statut` varchar(20) DEFAULT NULL,
  `client_id` varchar(50) DEFAULT NULL,
  `chambre_id` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `reservation`
--

INSERT INTO `reservation` (`id`, `numReservation`, `dateArrive`, `dateDepart`, `nbPersonne`, `montantTotal`, `statut`, `client_id`, `chambre_id`) VALUES
('29a65511', 'RES-802347', '2026-04-08', '2026-04-09', 2, 25000, 'CONFIRMEE', '8248b22d', 'd4ccaa97-536a-46a9-a887-f28fc68a6652');

-- --------------------------------------------------------

--
-- Structure de la table `statut`
--

CREATE TABLE `statut` (
  `statut` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `statut`
--

INSERT INTO `statut` (`statut`) VALUES
('ACTIF'),
('INACTIF');

-- --------------------------------------------------------

--
-- Structure de la table `statut_reservation`
--

CREATE TABLE `statut_reservation` (
  `statut` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `statut_reservation`
--

INSERT INTO `statut_reservation` (`statut`) VALUES
('ANNULEE'),
('CONFIRMEE'),
('EN_ATTENTE'),
('OCCUPEE'),
('TERMINEE');

-- --------------------------------------------------------

--
-- Structure de la table `type_chambre`
--

CREATE TABLE `type_chambre` (
  `id` varchar(50) NOT NULL,
  `nomType` varchar(50) DEFAULT NULL,
  `capacite` int(11) DEFAULT NULL,
  `tarifNuit` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `statut` varchar(20) DEFAULT NULL,
  `hotel_id` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `type_chambre`
--

INSERT INTO `type_chambre` (`id`, `nomType`, `capacite`, `tarifNuit`, `description`, `statut`, `hotel_id`) VALUES
('06efbec7-3b14-4f80-b257-af230aaa795e', 'Suite Vip', 0, 0, NULL, NULL, '1c883b05-80f4-4d01-b604-106b58a9a77d'),
('1776d668', 'Standard', 2, 25000, NULL, NULL, '1c883b05-80f4-4d01-b604-106b58a9a77d'),
('31f46562', 'Suite Royale', 2, 40000, NULL, NULL, '1c883b05-80f4-4d01-b604-106b58a9a77d'),
('4a25229d', 'Standard', 2, 25000, NULL, NULL, 'uuid-001'),
('9154bda6', 'Suite VIP', 0, 50000, NULL, NULL, '1c883b05-80f4-4d01-b604-106b58a9a77d');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` varchar(50) NOT NULL,
  `login` varchar(50) DEFAULT NULL,
  `motDePasse` varchar(100) DEFAULT NULL,
  `nom` varchar(50) DEFAULT NULL,
  `prenom` varchar(50) DEFAULT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `adresse` varchar(20) DEFAULT NULL,
  `nationalite` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role` varchar(20) DEFAULT NULL,
  `hotel_id` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `login`, `motDePasse`, `nom`, `prenom`, `telephone`, `adresse`, `nationalite`, `email`, `role`, `hotel_id`) VALUES
('15d573cd', 'adja', 'adja123', 'DIOP', 'Adja', 'Non défini', 'Hôtel', 'Sénégalaise', 'adja@hotel.com', 'RECEPTIONNISTE', '1c883b05-80f4-4d01-b604-106b58a9a77d'),
('1dabb003', 'cheikhouna', 'cheikh123', 'DIOP', 'Cheikhouna', 'Non défini', 'Hôtel', 'Sénégalaise', 'cheikh@hotel.com', 'RECEPTIONNISTE', 'uuid-001'),
('2748b73c', 'Adja@hotel.com', 'adja', 'Diop', 'Adja', 'Non défini', 'Hôtel', 'Sénégalaise', 'Adja@hotel.com', 'RECEPTIONNISTE', NULL),
('8248b22d', 'ebeydi@gmail.com', 'ebeydi', 'Fall', 'Ebeydi', '771234567', 'Bambey', 'Senegalaise', 'ebeydi@gmail.com', 'CLIENT', NULL),
('adm-cal', 'admin.LC@hotel.com', 'admin123', 'SYSTEM', 'Admi', '770000011', 'Warang', 'Senegal', 'admin.LC@hotel.com', 'ADMIN', 'uuid-calanques'),
('adm-fin', 'admin.finio@hotel.com', 'admin123', 'SYSTEM', 'Admi', '770000012', 'Joal', 'Senegal', 'admin.finio@hotel.com', 'ADMIN', 'uuid-finio'),
('adm-nia', 'admin.DN@hotel.com', 'admin123', 'SYSTEM', 'Admi', '770000006', 'Nianing', 'Senegal', 'admin.DN@hotel.com', 'ADMIN', 'uuid-nianing'),
('adm-pal', 'admin.PE@hotel.com', 'admin123', 'SYSTEM', 'Admi', '770000003', 'Palmarin', 'Senegal', 'admin.PE@hotel.com', 'ADMIN', 'uuid-palmarin'),
('adm-par', 'admin.LP@hotel.com', 'admin123', 'SYSTEM', 'Admi', '770000010', 'Mbodjene', 'Senegal', 'admin.LP@hotel.com', 'ADMIN', 'uuid-parenthese'),
('adm-res', 'admin.residence@hotel.com', 'admin123', 'SYSTEM', 'Admi', '770000013', 'Popenguine', 'Senegal', 'admin.residence@hotel.com', 'ADMIN', 'uuid-residence'),
('adm-sob', 'admin.sobobade@hotel.com', 'admin123', 'SYSTEM', 'Admi', '770000014', 'Toubab Dialaw', 'Senegal', 'admin.sobobade@hotel.com', 'ADMIN', 'uuid-sobobade'),
('adm10', 'admin.RHB@hotel.com', 'admin123', 'SYSTEM', 'Admi', '770000001', 'La Somone', 'Senegal', 'admin.RHB@hotel.com', 'ADMIN', 'uuid-001'),
('adm11', 'admin.BA@hotel.com', 'admin123', 'SYSTEM', 'Admi', '770000002', 'Ngaparou', 'Senegal', 'admin.BA@hotel.com', 'ADMIN', 'uuid-002'),
('d11c4f5c', 'admin@hotel.com', 'admin123', 'SYSTEM', 'Admin', '00000000', 'Hotel', 'Senegal', 'admin@hotel.com', 'ADMIN', '1c883b05-80f4-4d01-b604-106b58a9a77d'),
('df375eeb', 'ebeydi@hotel.com', 'ebeydiotaku', 'Fall', 'Ebeydi', NULL, 'Saintlouis', 'Senegal', 'ebeydi@hotel.com', 'CLIENT', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `users_role`
--

CREATE TABLE `users_role` (
  `role` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users_role`
--

INSERT INTO `users_role` (`role`) VALUES
('ADMIN'),
('CLIENT'),
('RECEPTIONNISTE');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `administrateur`
--
ALTER TABLE `administrateur`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `chambre`
--
ALTER TABLE `chambre`
  ADD PRIMARY KEY (`id`),
  ADD KEY `etat` (`etat`),
  ADD KEY `type_chambre_id` (`type_chambre_id`),
  ADD KEY `hotel_id` (`hotel_id`);

--
-- Index pour la table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `etat`
--
ALTER TABLE `etat`
  ADD PRIMARY KEY (`etat`);

--
-- Index pour la table `hotel`
--
ALTER TABLE `hotel`
  ADD PRIMARY KEY (`id`),
  ADD KEY `statut` (`statut`);

--
-- Index pour la table `receptionniste`
--
ALTER TABLE `receptionniste`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_recep_hotel` (`hotel_id`);

--
-- Index pour la table `reservation`
--
ALTER TABLE `reservation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `statut` (`statut`),
  ADD KEY `fk_reservation_user` (`client_id`),
  ADD KEY `reservation_ibfk_3` (`chambre_id`);

--
-- Index pour la table `statut`
--
ALTER TABLE `statut`
  ADD PRIMARY KEY (`statut`);

--
-- Index pour la table `statut_reservation`
--
ALTER TABLE `statut_reservation`
  ADD PRIMARY KEY (`statut`);

--
-- Index pour la table `type_chambre`
--
ALTER TABLE `type_chambre`
  ADD PRIMARY KEY (`id`),
  ADD KEY `statut` (`statut`),
  ADD KEY `hotel_id` (`hotel_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `login` (`login`),
  ADD KEY `role` (`role`);

--
-- Index pour la table `users_role`
--
ALTER TABLE `users_role`
  ADD PRIMARY KEY (`role`);

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `administrateur`
--
ALTER TABLE `administrateur`
  ADD CONSTRAINT `administrateur_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`);

--
-- Contraintes pour la table `chambre`
--
ALTER TABLE `chambre`
  ADD CONSTRAINT `chambre_ibfk_1` FOREIGN KEY (`etat`) REFERENCES `etat` (`etat`),
  ADD CONSTRAINT `chambre_ibfk_2` FOREIGN KEY (`type_chambre_id`) REFERENCES `type_chambre` (`id`),
  ADD CONSTRAINT `chambre_ibfk_3` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`);

--
-- Contraintes pour la table `client`
--
ALTER TABLE `client`
  ADD CONSTRAINT `client_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`);

--
-- Contraintes pour la table `hotel`
--
ALTER TABLE `hotel`
  ADD CONSTRAINT `hotel_ibfk_1` FOREIGN KEY (`statut`) REFERENCES `statut` (`statut`);

--
-- Contraintes pour la table `receptionniste`
--
ALTER TABLE `receptionniste`
  ADD CONSTRAINT `fk_recep_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`),
  ADD CONSTRAINT `receptionniste_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`);

--
-- Contraintes pour la table `reservation`
--
ALTER TABLE `reservation`
  ADD CONSTRAINT `fk_reservation_user` FOREIGN KEY (`client_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`statut`) REFERENCES `statut_reservation` (`statut`),
  ADD CONSTRAINT `reservation_ibfk_3` FOREIGN KEY (`chambre_id`) REFERENCES `chambre` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `type_chambre`
--
ALTER TABLE `type_chambre`
  ADD CONSTRAINT `type_chambre_ibfk_1` FOREIGN KEY (`statut`) REFERENCES `statut` (`statut`),
  ADD CONSTRAINT `type_chambre_ibfk_2` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`);

--
-- Contraintes pour la table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role`) REFERENCES `users_role` (`role`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
