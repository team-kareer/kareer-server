package org.sopt.kareer.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseResponse<T> {

    @Schema(example = "200")
    private final int code;

    private final String message;

    private final T data;

    private BaseResponse(int code, T data, String message) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> ok(T data, String message) {
        return new BaseResponse<>(HttpStatus.OK.value(), data, message);
    }

    public static <T> BaseResponse<T> ok(String message){
        return new BaseResponse<>(HttpStatus.OK.value(), null, message);
    }

    public static <T> BaseResponse<T> create(T data, String message) {
        return new BaseResponse<>(HttpStatus.CREATED.value(), data, message);
    }

    public static <T> BaseResponse<T> create(String message) {
        return new BaseResponse<>(HttpStatus.CREATED.value(), null, message);
    }
}
