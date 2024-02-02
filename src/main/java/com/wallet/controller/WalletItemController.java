package com.wallet.controller;

import com.wallet.dto.WalletItemDTO;
import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.enums.TypeEnum;
import com.wallet.response.Response;
import com.wallet.service.WalletItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wallet-item")
public class WalletItemController {

    @Autowired
    private WalletItemService walletItemService;

    @PostMapping
    public ResponseEntity<Response<WalletItemDTO>> createWalletItem(@Valid @RequestBody WalletItemDTO walletItemDto, BindingResult result) {

        Response<WalletItemDTO> response = new Response<WalletItemDTO>();

        if (result.hasErrors()) {
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        WalletItem walletItem = walletItemService.save(this.convertDtoToEntity(walletItemDto));

        response.setData(this.convertEntityToDto(walletItem));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{wallet}")
    public ResponseEntity<Response<Page<WalletItemDTO>>> findBetweenDate(
            @PathVariable("wallet") Long wallet,
            @RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        Response<Page<WalletItemDTO>> response = new Response<Page<WalletItemDTO>>();
        Page<WalletItem> items = walletItemService.findBetweenDates(wallet, startDate, endDate, page);
        Page<WalletItemDTO> dto = items.map( i -> this.convertEntityToDto(i));

        response.setData(dto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/type/{wallet}")
    public ResponseEntity<Response<List<WalletItemDTO>>> findByWalletIdAndType(
            @PathVariable("wallet") Long wallet,
            @RequestParam("type") String type) {

        Response<List<WalletItemDTO>> response = new Response<List<WalletItemDTO>>();
        List<WalletItem> list = walletItemService.findByWalletAndType(wallet, TypeEnum.getEnum(type));
        List<WalletItemDTO> dto = new ArrayList<>();
        list.forEach(i -> dto.add(this.convertEntityToDto(i)));

        response.setData(dto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/total/{wallet}")
    public ResponseEntity<Response<BigDecimal>> sumByWalletId(@PathVariable("wallet") Long wallet) {

        Response<BigDecimal> response = new Response<BigDecimal>();
        BigDecimal value = walletItemService.sumByWalletId(wallet);

        response.setData(value);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<Response<WalletItemDTO>> update(@Valid @RequestBody WalletItemDTO walletItemDto, BindingResult result) {

        Response<WalletItemDTO> response = new Response<WalletItemDTO>();
        Optional<WalletItem> wi = walletItemService.findById(walletItemDto.getId());

        if (!wi.isPresent()) {
            result.addError(new ObjectError("WalletItem", "WalletItem não encontrado"));
        } else if (wi.get().getWallet().getId().compareTo(walletItemDto.getWallet()) != 0) {
                result.addError(new ObjectError("WalletItemChange", "Você não pode alterar a carteira"));
        }

        if (result.hasErrors()) {
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        WalletItem saved = walletItemService.save(this.convertDtoToEntity(walletItemDto));

        response.setData(this.convertEntityToDto(saved));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @DeleteMapping(value = "/{walletItemId}")
    public ResponseEntity<Response<String>> delete(@PathVariable("walletItemId") Long walletItemId) {

        Response<String> response = new Response<String>();
        Optional<WalletItem> wi = walletItemService.findById(walletItemId);

        if (!wi.isPresent()) {
            response.getErrors().add("Carteira de id " + walletItemId + " não encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        walletItemService.deleteById(walletItemId);
        response.setData("Carteira de id " + walletItemId + " apagada com sucesso");
        return ResponseEntity.ok(response);

    }



    private WalletItem convertDtoToEntity(WalletItemDTO walletItemDTO) {
        WalletItem walletItem = new WalletItem();
        walletItem.setDate(walletItemDTO.getDate());
        walletItem.setDescription(walletItemDTO.getDescription());
        walletItem.setId(walletItemDTO.getId());
        walletItem.setType(TypeEnum.getEnum(walletItemDTO.getType()));
        walletItem.setValue(walletItemDTO.getValue());

        Wallet wallet = new Wallet();
        wallet.setId(walletItemDTO.getWallet());
        walletItem.setWallet(wallet);

        return walletItem;
    }

    private WalletItemDTO convertEntityToDto( WalletItem walletItem) {
        WalletItemDTO dto = new WalletItemDTO();
        dto.setDate(walletItem.getDate());
        dto.setDescription(walletItem.getDescription());
        dto.setId(walletItem.getId());
        dto.setType(walletItem.getType().getValue());
        dto.setValue(walletItem.getValue());
        dto.setWallet(walletItem.getWallet().getId());
        return dto;
    }

}
