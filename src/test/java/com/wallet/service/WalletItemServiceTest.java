package com.wallet.service;

import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.enums.TypeEnum;
import com.wallet.repository.WalletItemRepository;
import com.wallet.service.impl.WalletItemServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletItemServiceTest {
    private static final Date DATE = new Date();
    public static final TypeEnum TYPE = TypeEnum.EN;
    public static final String DESCRIPTION = "Conta de Luz";
    public static final BigDecimal VALUE = BigDecimal.valueOf(65);

    @Mock
    private WalletItemRepository repository;

    @InjectMocks
    private WalletItemServiceImpl wallettemService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(wallettemService, "itemsPerPage", 10);
    }
    @Test
    public void testSave() {

        when(repository.save(Mockito.any(WalletItem.class))).thenReturn(getMockWalletItem());
        WalletItem response = wallettemService.save(new WalletItem());

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getDescription(), DESCRIPTION);
        Assert.assertEquals(response.getValue().compareTo(VALUE), 0);
    }

    @Test
    public void testFindBetweenDates() {

        List<WalletItem> list = new ArrayList<>();
        list.add(getMockWalletItem());
        Page<WalletItem> page = new PageImpl(list);
        when(repository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(Mockito.anyLong(), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(PageRequest.class))).thenReturn(page);

        Page<WalletItem> response = wallettemService.findBetweenDates(1L, new Date(), new Date(), 0);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getContent().size(), 1);
        Assert.assertEquals(response.getContent().get(0).getDescription(), DESCRIPTION);

    }

    @Test
    public void testFindByType() {

        List<WalletItem> list = new ArrayList<>();
        list.add(getMockWalletItem());

        when(repository.findByWalletIdAndType(Mockito.anyLong(), Mockito.any(TypeEnum.class))).thenReturn(list);

        List<WalletItem> response = wallettemService.findByWalletAndType(1L, TypeEnum.EN);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.get(0).getType(), TYPE);

    }

    @Test
    public void testSumByWallet() {

        BigDecimal value = BigDecimal.valueOf(45);
        when(repository.sumByWalletId(Mockito.anyLong())).thenReturn(value);

        BigDecimal response = wallettemService.sumByWalletId(1L);

        Assert.assertEquals(response.compareTo(value), 0);

    }


    private WalletItem getMockWalletItem() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);

        WalletItem walletItem = new WalletItem(1L, wallet, DATE, TYPE, DESCRIPTION, VALUE);

        return walletItem;
    }

}
