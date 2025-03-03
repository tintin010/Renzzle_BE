package com.renzzle.backend.domain.test.api;

import com.renzzle.backend.domain.test.api.request.SaveEntityRequest;
import com.renzzle.backend.domain.test.api.response.FindEntityResponse;
import com.renzzle.backend.domain.test.api.response.HelloResponse;
import com.renzzle.backend.domain.test.api.response.SaveEntityResponse;
import com.renzzle.backend.domain.test.domain.JdbcEntity;
import com.renzzle.backend.domain.test.domain.TestEntity;
import com.renzzle.backend.domain.test.service.TestService;
import com.renzzle.backend.global.common.response.ApiResponse;
import com.renzzle.backend.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import static com.renzzle.backend.global.util.ErrorUtils.getErrorMessages;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Test API", description = "Server testing API")
public class TestController {

    private final TestService testService;

    @Operation(summary = "Server response test")
    @GetMapping("/hello/{name}")
    public ApiResponse<HelloResponse> helloToServer(@PathVariable("name") String name) {
        return ApiUtils.success(testService.getHelloResponse(name));
    }

    @Operation(summary = "Server DB test", description = "Test saving entity on DB through JPA")
    @PostMapping("/save/jpa")
    public ApiResponse<SaveEntityResponse> saveTestEntity(@Valid @RequestBody SaveEntityRequest request, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(getErrorMessages(bindingResult));
        }

        TestEntity entity = TestEntity
                .builder()
                .name(request.name())
                .build();

        TestEntity result = testService.saveEntity(entity);

        SaveEntityResponse response = SaveEntityResponse
                .builder()
                .id(result.getId())
                .name(result.getName())
                .build();

        return ApiUtils.success(response);
    }

    @Operation(summary = "Server DB test", description = "Test finding entity on DB through JPA")
    @GetMapping("/find/jpa/{id}")
    public ApiResponse<FindEntityResponse> findTestEntity(@PathVariable("id") Long id) {
        TestEntity result = testService.findEntityById(id);

        FindEntityResponse response = FindEntityResponse
                .builder()
                .id(result.getId())
                .name(result.getName())
                .build();

        return ApiUtils.success(response);
    }

    @Operation(summary = "Server DB test", description = "Test saving entity on DB through JDBC")
    @PostMapping("/save/jdbc")
    public ApiResponse<SaveEntityResponse> saveJdbcEntity(@Valid @RequestBody SaveEntityRequest request, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(getErrorMessages(bindingResult));
        }

        long id = testService.saveJdbcEntity(request.name());

        SaveEntityResponse response = SaveEntityResponse
                .builder()
                .id(id)
                .name(request.name())
                .build();

        return ApiUtils.success(response);
    }

    @Operation(summary = "Server DB test", description = "Test finding entity on DB through JDBC")
    @GetMapping("/find/jdbc/{id}")
    public ApiResponse<FindEntityResponse> findJdbcEntity(@PathVariable("id") Long id) {
        JdbcEntity result = testService.findJdbcEntityById(id);

        FindEntityResponse response = FindEntityResponse
                .builder()
                .id(result.getId())
                .name(result.getName())
                .build();

        return ApiUtils.success(response);
    }

    @Operation(summary = "Server DB test", description = "Delete all test data")
    @DeleteMapping("/clear")
    public ApiResponse<Boolean> deleteAllTestData() {
        testService.deleteAllTestData();
        return ApiUtils.success(true);
    }

}
