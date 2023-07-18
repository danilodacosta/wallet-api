package com.wallet.controller;

import com.wallet.dto.UserDTO;
import com.wallet.dto.UserWalletDTO;
import com.wallet.entity.User;
import com.wallet.entity.UserWallet;
import com.wallet.entity.Wallet;
import com.wallet.response.Response;
import com.wallet.service.UserService;
import com.wallet.service.UserWalletService;
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
@RequestMapping("/user-wallet")
public class UserWalletController {

    @Autowired
    private UserWalletService userWalletService;

    @PostMapping
    public ResponseEntity<Response<UserWalletDTO>> create(@Validated @RequestBody UserWalletDTO userWalletDto, BindingResult result) {

        Response<UserWalletDTO> response = new Response<UserWalletDTO>();

        if (result.hasErrors()) {
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        UserWallet userWallet = userWalletService.save(this.convertDtoToEntity(userWalletDto));

        response.setData(this.convertEntityToDto(userWallet));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    private UserWallet convertDtoToEntity(UserWalletDTO userWalletDto) {
        UserWallet userWallet = new UserWallet();

        User user = new User();
        user.setId(userWalletDto.getUsers());

        Wallet wallet = new Wallet();
        wallet.setId(userWalletDto.getWallet());

        userWallet.setId(userWalletDto.getId());
        userWallet.setUsers(user);
        userWallet.setWallet(wallet);

        return userWallet;
    }

    private UserWalletDTO convertEntityToDto(UserWallet userWallet) {
        UserWalletDTO dto = new UserWalletDTO();
        dto.setId(userWallet.getId());
        dto.setUsers(userWallet.getUsers().getId());
        dto.setWallet(userWallet.getWallet().getId());
        return dto;
    }

}
