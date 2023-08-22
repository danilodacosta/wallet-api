package com.wallet.repository;

import com.wallet.entity.WalletItem;
import com.wallet.enums.TypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface WalletItemRepository extends JpaRepository<WalletItem, Long> {

    Page<WalletItem> findByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(Long walletId, Date init, Date end, PageRequest page);

    List<WalletItem> findByWalletIdAndType(Long walletId, TypeEnum type);

    @Query(value = "select sum(value) from WalletItem wi where wi.wallet.id = :wallet")
    BigDecimal sumByWalletId(@Param("wallet") Long wallet);

    Page<WalletItem> findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(Long wallet, Date start, Date end, PageRequest pg);
}
