package com.wallet.controller;

import com.wallet.dto.UserDTO;
import com.wallet.dto.WalletDTO;
import com.wallet.entity.User;
import com.wallet.entity.Wallet;
import com.wallet.response.Response;
import com.wallet.service.UserService;
import com.wallet.service.WalletService;
import com.wallet.util.Bcrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping
    public ResponseEntity<Response<WalletDTO>> createWallet(@Validated @RequestBody WalletDTO walletDto, BindingResult result) {

        Response<WalletDTO> response = new Response<WalletDTO>();

        if (result.hasErrors()) {
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Wallet wallet = walletService.save(this.convertDtoToEntity(walletDto));

        response.setData(this.convertEntityToDto(wallet));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    private Wallet convertDtoToEntity(WalletDTO walletDto) {
        Wallet wallet = new Wallet();
        wallet.setId(walletDto.getId());
        wallet.setValue(walletDto.getValue());
        wallet.setName(walletDto.getName());

        return wallet;
    }

    private WalletDTO convertEntityToDto(Wallet wallet) {
        WalletDTO dto = new WalletDTO();
        dto.setId(wallet.getId());
        dto.setValue(wallet.getValue());
        dto.setName(wallet.getName());
        return dto;
    }

}
