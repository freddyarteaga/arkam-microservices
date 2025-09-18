package com.arkam.user.controllers;

import com.arkam.user.dto.UserRequest;
import com.arkam.user.dto.UserResponse;
import com.arkam.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;
//    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return new ResponseEntity<>(userService.fetchAllUsers(),
                                    HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id){
        log.info("Request received for user: {}", id);

        log.trace("This is TRACE level - Verifica detalles de Logs");
        log.debug("This is DEBUG level - Usado para debuggear en desarrollo");
        log.info("This is INFO level - Información general del sistema");
        log.warn("This is WARN level - Algo puede presentar error");
        log.error("This is ERROR level - Algo puede fallar");

        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest){
        userService.addUser(userRequest);
        return ResponseEntity.ok("Usuario Agregado Exitosamente");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id,
                                             @RequestBody UserRequest updateUserRequest){
        boolean updated = userService.updateUser(id, updateUserRequest);
        if (updated)
            return ResponseEntity.ok("Usuario actualizado exitosamente");
        return ResponseEntity.notFound().build();
    }
}
