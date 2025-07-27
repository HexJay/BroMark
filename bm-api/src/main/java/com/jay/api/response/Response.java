package com.jay.api.response;

import com.jay.types.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 7000723935764546321L;

    private String code;
    private String info;
    private T data;

    public static <T> Response<T> ok() {
        return ok(null);
    }

    public static <T> Response<T> ok(T data) {
        return of(ResponseCode.SUCCESS, null, data);
    }

    public static <T> Response<T> fail() {
        return fail(ResponseCode.FAILURE, null);
    }

    public static <T> Response<T> fail(String info) {
        return fail(ResponseCode.FAILURE, info);
    }

    public static <T> Response<T> fail(ResponseCode res, String info) {
        return of(res, info, null);
    }

    public static <T> Response<T> fail(String res, String info) {
        return of(res, info, null);
    }

    public static <T> Response<T> of(ResponseCode res, String info, T data) {
        Response<T> response = new Response<T>();
        response.setCode(res.getCode());
        response.setInfo(info != null ? info : res.getInfo());
        response.setData(data);
        return response;
    }

    public static <T> Response<T> of(String res, String info, T data) {
        Response<T> response = new Response<T>();
        response.setCode(res);
        response.setInfo(info);
        response.setData(data);
        return response;
    }

}
