-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 30, 2025 at 08:41 PM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_mapala_unand`
--

-- --------------------------------------------------------

--
-- Table structure for table `alat`
--

CREATE TABLE `alat` (
  `id_alat` int NOT NULL,
  `nama_alat` varchar(100) DEFAULT NULL,
  `jenis` varchar(50) DEFAULT NULL,
  `stok` int DEFAULT NULL,
  `harga_sewa` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `alat`
--

INSERT INTO `alat` (`id_alat`, `nama_alat`, `jenis`, `stok`, `harga_sewa`) VALUES
(1, 'Tenda Dome Merapi', 'Camping', 5, 25000),
(2, 'Carrier 60L', 'Camping', 3, 20000),
(3, 'Kompor Portable', 'Masak', 4, 10000),
(4, 'Harness Petzl', 'Teknis', 2, 50000),
(5, 'Sleeping Bag Eiger', 'Camping', 10, 15000),
(6, 'Matras Karet', 'Camping', 15, 5000),
(7, 'Flysheet 3x4 Meter', 'Camping', 5, 10000),
(8, 'Nesting (Panci Lapangan)', 'Masak', 8, 12000),
(9, 'Tali Karmantel 50m', 'Teknis', 2, 75000),
(10, 'Carabiner Screw', 'Teknis', 17, 5000),
(11, 'Helm Petzl', 'Caving', 6, 20000),
(12, 'Headlamp Waterproof', 'Caving', 10, 15000),
(13, 'GPS Garmin', 'Navigasi', 2, 100000),
(14, 'Kompas Suunto', 'Navigasi', 5, 15000);

-- --------------------------------------------------------

--
-- Table structure for table `peminjaman`
--

CREATE TABLE `peminjaman` (
  `id_pinjam` int NOT NULL,
  `nama_peminjam` varchar(100) DEFAULT NULL,
  `id_alat` int DEFAULT NULL,
  `tgl_pinjam` datetime DEFAULT NULL,
  `tgl_kembali` datetime DEFAULT NULL,
  `total_biaya` double DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `jumlah` int DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `peminjaman`
--

INSERT INTO `peminjaman` (`id_pinjam`, `nama_peminjam`, `id_alat`, `tgl_pinjam`, `tgl_kembali`, `total_biaya`, `status`, `jumlah`) VALUES
(1, 'Testing', 2, '2025-12-30 20:46:02', '2025-12-30 21:07:27', 60000, 'Kembali', 1),
(2, 'Testing 2', 10, '2025-12-30 21:57:35', NULL, 10000, 'Dipinjam', 1),
(5, 'Acid', 1, '2025-12-31 02:42:50', '2025-12-31 02:45:13', 50000, 'Kembali', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `alat`
--
ALTER TABLE `alat`
  ADD PRIMARY KEY (`id_alat`);

--
-- Indexes for table `peminjaman`
--
ALTER TABLE `peminjaman`
  ADD PRIMARY KEY (`id_pinjam`),
  ADD KEY `id_alat` (`id_alat`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `alat`
--
ALTER TABLE `alat`
  MODIFY `id_alat` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `peminjaman`
--
ALTER TABLE `peminjaman`
  MODIFY `id_pinjam` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `peminjaman`
--
ALTER TABLE `peminjaman`
  ADD CONSTRAINT `peminjaman_ibfk_1` FOREIGN KEY (`id_alat`) REFERENCES `alat` (`id_alat`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
