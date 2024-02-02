package com.wallet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.dto.WalletItemDTO;
import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.enums.TypeEnum;
import com.wallet.service.WalletItemService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class WalletItemControllerTest {

    public static final Long ID = 1L;
    private static final Date DATE = new Date();
    private static final LocalDate TODAY = LocalDate.now();
    public static final TypeEnum TYPE = TypeEnum.EN;
    public static final String DESCRIPTION = "Conta de Luz";
    public static final BigDecimal VALUE = BigDecimal.valueOf(65);
    public static final String URL = "/wallet-item";

    MockMvc mockMvc;

    @Mock
    WalletItemService walletItemService;

    @InjectMocks
    private WalletItemController walletItemController;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletItemController).build();
    }

    @Test
    public void testSave() throws Exception {

        when(walletItemService.save(any(WalletItem.class))).thenReturn(getMockWalletItem());

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .content(this.getJsonPayload())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(ID))
                .andExpect(jsonPath("$.data.date").value(TODAY.format(getDateFormater())))
                .andExpect(jsonPath("$.data.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.data.type").value(TYPE.getValue()))
                .andExpect(jsonPath("$.data.value").value(VALUE))
                .andExpect(jsonPath("$.data.wallet").value(ID));
    }

    @Test
    public void testFindBetweenDates() throws Exception {
        List<WalletItem> list = new ArrayList<>();
        list.add(getMockWalletItem());
        Page<WalletItem> page = new PageImpl<>(list);

        String startDate = TODAY.format(getDateFormater());
        String endDate = TODAY.plusDays(5).format(getDateFormater());

        BDDMockito.given(walletItemService.findBetweenDates(Mockito.anyLong(), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.anyInt())).willReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/1?startDate=" + startDate + "&endDate=" + endDate)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(ID))
                .andExpect(jsonPath("$.data.content[0].date").value(TODAY.format(getDateFormater())))
                .andExpect(jsonPath("$.data.content[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$.data.content[0].type").value(TYPE.getValue()))
                .andExpect(jsonPath("$.data.content[0].value").value(VALUE))
                .andExpect(jsonPath("$.data.content[0].wallet").value(ID));
    }

    @Test
    public void testFindByType() throws Exception {
        List<WalletItem> list = new ArrayList<>();
        list.add(getMockWalletItem());

        when(walletItemService.findByWalletAndType(Mockito.anyLong(), Mockito.any(TypeEnum.class))).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/type/1?type=ENTRADA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(ID))
                .andExpect(jsonPath("$.data[0].date").value(TODAY.format(getDateFormater())))
                .andExpect(jsonPath("$.data[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$.data[0].type").value(TYPE.getValue()))
                .andExpect(jsonPath("$.data[0].value").value(VALUE))
                .andExpect(jsonPath("$.data[0].wallet").value(ID));
    }

    @Test
    public void testSumByWallet() throws Exception {
        BigDecimal value = BigDecimal.valueOf(536.90);

        BDDMockito.given(walletItemService.sumByWalletId(Mockito.anyLong())).willReturn(value);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/total/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("536.9"));
    }

    @Test
    public void testUpdate() throws Exception {

        when(walletItemService.findById(anyLong())).thenReturn(Optional.of(getMockWalletItem()));
        when(walletItemService.save(any(WalletItem.class))).thenReturn(getMockWalletItem());

        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .content(this.getJsonPayload())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(ID))
                .andExpect(jsonPath("$.data.date").value(TODAY.format(getDateFormater())))
                .andExpect(jsonPath("$.data.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.data.type").value(TYPE.getValue()))
                .andExpect(jsonPath("$.data.value").value(VALUE))
                .andExpect(jsonPath("$.data.wallet").value(ID));
    }

    @Test
    public void testUpdateWalletChange() throws Exception {

        Wallet w = new Wallet();
        w.setId(99L);

        WalletItem wi = new WalletItem(1l, w , DATE, TypeEnum.EN, DESCRIPTION, VALUE);

        when(walletItemService.findById(anyLong())).thenReturn(Optional.of((wi)));

        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .content(this.getJsonPayload())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors[0]").value("Você não pode alterar a carteira"));
    }

    @Test
    public void testUpdateInvalidId() throws Exception {

        when(walletItemService.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .content(this.getJsonPayload())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors[0]").value("WalletItem não encontrado"));
    }

    @Test
    public void testDelete() throws JsonProcessingException, Exception {

        when(walletItemService.findById(anyLong())).thenReturn(Optional.of(new WalletItem()));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/1")
                        .content(this.getJsonPayload())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Carteira de id "+ ID +" apagada com sucesso"));
    }

    @Test
    public void testDeleteInvalid() throws JsonProcessingException, Exception {

        when(walletItemService.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/99")
                        .content(this.getJsonPayload())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors[0]").value("Carteira de id "+ 99 +" não encontrada"));
    }

        private WalletItem getMockWalletItem() {

        Wallet wallet = new Wallet();
        wallet.setId(ID);

        WalletItem walletItem = new WalletItem(ID, wallet, DATE, TYPE, DESCRIPTION, VALUE);
        return walletItem;
    }


    private String getJsonPayload() throws JsonProcessingException {
        WalletItemDTO dto = new WalletItemDTO();
        dto.setId(ID);
        dto.setDate(DATE);
        dto.setDescription(DESCRIPTION);
        dto.setType(TYPE.getValue());
        dto.setValue(VALUE);
        dto.setWallet(ID);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(dto);
    }

    private DateTimeFormatter getDateFormater(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return  formatter;
    }


}
