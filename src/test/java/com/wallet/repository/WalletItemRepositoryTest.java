package com.wallet.repository;

import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.enums.TypeEnum;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class WalletItemRepositoryTest {

    private static final Date DATE = new Date();
    private static final TypeEnum TYPE = TypeEnum.EN;
    private static final String DESCRIPTION = "Conta de Luz";
    private static final BigDecimal VALUE = BigDecimal.valueOf(65);

    private Long savedWalletItemId;
    private Long savedWalletId;

    @Autowired
    WalletItemRepository walletItemRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    UserWalletRepository userWalletRepository;

    @Before
    public void setup() {
        Wallet wallet = new Wallet();
        wallet.setName("Carteira Teste");
        wallet.setValue(new BigDecimal(500));
        walletRepository.save(wallet);

        WalletItem walletItem = new WalletItem(null, wallet, DATE, TYPE, DESCRIPTION, VALUE);
        walletItemRepository.save(walletItem);

        savedWalletItemId = walletItem.getId();
        savedWalletId = wallet.getId();
    }

    @After
    public void tearDown() {
        //walletItemRepository.deleteAll();
        //userWalletRepository.deleteAll();
        //walletRepository.deleteAll();
    }

    @Test
    public void testSave() {

        Wallet wallet = new Wallet();
        wallet.setName("Carteira 1");
        wallet.setValue(new BigDecimal(500));
        walletRepository.save(wallet);

        WalletItem wi = new WalletItem(1l, wallet, DATE, TYPE, DESCRIPTION, VALUE);

        WalletItem response = walletItemRepository.save(wi);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getDescription(), DESCRIPTION);
        Assert.assertEquals(response.getType(), TYPE);
        Assert.assertEquals(response.getValue(), VALUE);
        Assert.assertEquals(response.getWallet().getId(), wallet.getId());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testSaveInvalidWalletItem() {
        WalletItem walletItem = new WalletItem();
        walletItemRepository.save(walletItem);
    }

    @Test
    public void testUpdate() {
        Optional<WalletItem> walletItem = walletItemRepository.findById(savedWalletItemId);
        String description = "Descrição alterada";

        WalletItem changed = walletItem.get();
        changed.setDescription(description);

        walletItemRepository.save(changed);

        Optional<WalletItem> newWalletItem = walletItemRepository.findById(savedWalletItemId);
        Assert.assertEquals(newWalletItem.get().getDescription(), description);
    }

    @Test
    public void testDeleteWalletItem() {

        Optional<Wallet> wallet = walletRepository.findById(savedWalletId);
        WalletItem walletItem = new WalletItem(null, wallet.get(), DATE, TYPE, DESCRIPTION, VALUE);

        walletItemRepository.save(walletItem);

        walletItemRepository.deleteById(walletItem.getId());

        Optional<WalletItem> response = walletItemRepository.findById(walletItem.getId());

        Assert.assertFalse(response.isPresent());
    }


    @Test
    public void testFindBetweenDates() {

        Optional<Wallet> wallet = walletRepository.findById(savedWalletId);
        LocalDateTime localDateTime = DATE.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Date currentDatePlusFiveDays = Date.from(localDateTime.plusDays(5).atZone(ZoneId.systemDefault()).toInstant());
        Date currentDatePlusSevenDays = Date.from(localDateTime.plusDays(7).atZone(ZoneId.systemDefault()).toInstant());

        walletItemRepository.save(new WalletItem(null, wallet.get(), currentDatePlusFiveDays, TYPE, DESCRIPTION, VALUE));
        walletItemRepository.save(new WalletItem(null, wallet.get(), currentDatePlusSevenDays, TYPE, DESCRIPTION, VALUE));

        PageRequest page = PageRequest.of(0, 10);

        Page<WalletItem> response = walletItemRepository.findByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(savedWalletId, DATE, currentDatePlusFiveDays, page);

        Assert.assertEquals(response.getContent().size(), 2);
        Assert.assertEquals(response.getTotalElements(), 2);
        Assert.assertEquals(response.getContent().get(0).getWallet().getId(), savedWalletId);
    }

    @Test
    public void testFindByType() {
        List<WalletItem> response =  walletItemRepository.findByWalletIdAndType(savedWalletId, TYPE);
        Assert.assertEquals(response.size(), 1);
        Assert.assertEquals(response.get(0).getType(), TYPE);
    }

    @Test
    public void testFindByTypeSd() {
        Optional<Wallet> wallet = walletRepository.findById(savedWalletId);
        walletItemRepository.save(new WalletItem(null, wallet.get(), DATE, TypeEnum.SD, DESCRIPTION, VALUE));

    }

    @Test
    public void testSumByWallet() {
        Optional<Wallet> wallet = walletRepository.findById(savedWalletId);
        walletItemRepository.save(new WalletItem(null, wallet.get(), DATE, TypeEnum.SD, DESCRIPTION, BigDecimal.valueOf(150.80)));
        BigDecimal response = walletItemRepository.sumByWalletId(savedWalletId);
        Assert.assertEquals(response.compareTo(BigDecimal.valueOf(215.8)), 0);
    }

}
