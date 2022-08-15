package com.young.asow.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestResponse<T> {
    private static final String CODE_OK = String.valueOf(HttpStatus.OK.value());
    private static final String SUCCESS_MESSAGE = "Succeeded";

    private static final String CODE_ERROR = "Error";
    private static final String FAIL_MESSAGE = "Failed";

    String code;

    String message;

    T data;

    public RestResponse(String message, T data) {
        this.code = CODE_OK;
        this.message = message;
        this.data = data;
    }

    public RestResponse(T data) {
        this.code = CODE_OK;
        this.message = SUCCESS_MESSAGE;
        this.data = data;
    }

    public static RestResponse<Object> ok() {
        return new RestResponse<Object>(CODE_OK, SUCCESS_MESSAGE, null);
    }

    public static <T> RestResponse<T> ok(T data) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(CODE_OK);
        response.setData(data);
        response.setMessage(SUCCESS_MESSAGE);
        return response;
    }

    public static RestResponse<Object> ok(String message) {
        return new RestResponse<Object>(CODE_OK, message, null);
    }

    public static RestResponse<Object> fail(String message) {
        return new RestResponse<Object>(CODE_ERROR, message, null);
    }

}
