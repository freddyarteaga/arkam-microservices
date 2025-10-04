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

import java.util.List;

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
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return new ResponseEntity<>(getAllUsersUseCase.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id){
        log.info("Request received for user: {}", id);

        log.trace("This is TRACE level - Very detailed logs");
        log.debug("This is DEBUG level - Used for development debugging");
        log.info("This is INFO level - General system information");
        log.warn("This is WARN level - Something might be wrong");
        log.error("This is ERROR level - Something failed");

        return getUserUseCase.getUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest){
        UserResponse response = createUserUseCase.createUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id,
                                             @RequestBody UserRequest updateUserRequest){
        boolean updated = updateUserUseCase.updateUser(id, updateUserRequest);
        if (updated)
            return ResponseEntity.ok("User updated successfully");
        return ResponseEntity.notFound().build();
    }
}
