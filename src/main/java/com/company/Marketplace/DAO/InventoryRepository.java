package com.company.Marketplace.DAO;

import com.company.Marketplace.DTO.Inventory;
import com.company.Marketplace.DTO.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {


    Inventory findByProduct(Product product);
}
