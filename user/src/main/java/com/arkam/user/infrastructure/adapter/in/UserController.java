package com.arkam.user.infrastructure.adapter.in;

import com.arkam.user.application.dto.UserRequest;
import com.arkam.user.application.dto.UserResponse;
import com.arkam.user.application.port.in.CreateUserUseCase;
import com.arkam.user.application.port.in.GetAllUsersUseCase;
import com.arkam.user.application.port.in.GetUserUseCase;
import com.arkam.user.application.port.in.UpdateUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    @GetMapping
    public Flux<UserResponse> getAllUsers(){
        return getAllUsersUseCase.getAllUsers();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getUser(@PathVariable String id){
        log.info("Petición recibida por el usuario: {}", id);

        log.trace("nivel de traza - Very detailed logs");
        log.debug("nivel de debbug - usado por los desarrolladores para debuggear");
        log.info("nivel de finormación - infomracion general del sistema");
        log.warn("nivel de emergencia- cuando algo puede fallar");
        log.error("nivel de error - algo falla");

        return getUserUseCase.getUser(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<UserResponse>> createUser(@RequestBody UserRequest userRequest){
        return createUserUseCase.createUser(userRequest)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<String>> updateUser(@PathVariable String id,
                                              @RequestBody UserRequest updateUserRequest){
        return updateUserUseCase.updateUser(id, updateUserRequest)
                .flatMap(updated -> updated ?
                        Mono.just(ResponseEntity.ok("Usuario actualizado correctamente")) :
                        Mono.just(ResponseEntity.notFound().build()));
    }
}
